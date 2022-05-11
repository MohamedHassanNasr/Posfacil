/*
 * ===========================================================================================
 * = COPYRIGHT
 *          PAX Computer Technology(Shenzhen) CO., LTD PROPRIETARY INFORMATION
 *   This software is supplied under the terms of a license agreement or nondisclosure
 *   agreement with PAX Computer Technology(Shenzhen) CO., LTD and may not be copied or
 *   disclosed except in accordance with the terms in that agreement.
 *     Copyright (C) 2020-? PAX Computer Technology(Shenzhen) CO., LTD All rights reserved.
 * Description: // Detail description about the function of this module,
 *             // interfaces with the other modules, and dependencies.
 * Revision History:
 * Date                  Author	                 Action
 * 20200525  	         JackHuang               Create
 * ===========================================================================================
 */

package com.paxsz.module.emv.process.contactless;

import com.pax.commonlib.utils.LogUtils;
import com.pax.commonlib.utils.convert.IConvert;
import com.pax.jemv.clcommon.ACType;
import com.pax.jemv.clcommon.ByteArray;
import com.pax.jemv.clcommon.Clss_ProgramID;
import com.pax.jemv.clcommon.Clss_ReaderParam;
import com.pax.jemv.clcommon.Clss_VisaAidParam;
import com.pax.jemv.clcommon.CvmType;
import com.pax.jemv.clcommon.DDAFlag;
import com.pax.jemv.clcommon.EMV_CAPK;
import com.pax.jemv.clcommon.EMV_REVOCLIST;
import com.pax.jemv.clcommon.KernType;
import com.pax.jemv.clcommon.RetCode;
import com.pax.jemv.clcommon.TransactionPath;
import com.pax.jemv.entrypoint.api.ClssEntryApi;
import com.pax.jemv.paywave.api.ClssWaveApi;
import com.paxsz.module.emv.process.entity.IssuerRspData;
import com.paxsz.module.emv.process.entity.TransResult;
import com.paxsz.module.emv.process.enums.CvmResultEnum;
import com.paxsz.module.emv.process.enums.TransResultEnum;
import com.paxsz.module.emv.utils.EmvParamConvert;
import com.paxsz.module.emv.xmlparam.entity.clss.PayWaveAid;
import com.paxsz.module.emv.xmlparam.entity.clss.PayWaveProgramId;

import java.util.ArrayList;
import java.util.Arrays;

class ClssPayWaveProcess extends ClssKernelProcess {
    private static final String TAG = "ClssPayWaveProcess";
    private PayWaveAid payWaveAid = null;
    private String track2 = null;

    //partial match, the aid len in xml file is less than kernel select in card
    private boolean isSupportPartialAidMatch(PayWaveAid aid) {
        boolean flag = false;
        if ((aid.getPartialAidSelection() == 0) && (aid.getApplicationId().length < finalSelectData[0])
                && (Arrays.equals(Arrays.copyOfRange(finalSelectData, 1, aid.getApplicationId().length + 1), aid.getApplicationId()))) {
            flag = true;
        }
        return flag;
    }

    private PayWaveAid getAidParam() {
        for (PayWaveAid aid : emvProcessParam.getPayWaveParam().getPayWaveAidArrayList()) {
            //finalSelectData[0] is aid len
            if (Arrays.equals(Arrays.copyOfRange(finalSelectData, 1, finalSelectData[0] + 1), aid.getApplicationId()) //full match
                    || isSupportPartialAidMatch(aid)) {
                return aid;
            }
        }
        return null;
    }


    private Clss_ReaderParam toClssReaderParam(PayWaveAid aid) {
        Clss_ReaderParam readerParam = new Clss_ReaderParam();
        readerParam.aucMchNameLoc = emvProcessParam.getTermConfig().getMerchantNameAndLocation().getBytes();
        readerParam.usMchLocLen = (short) emvProcessParam.getTermConfig().getMerchantNameAndLocation().length();
        readerParam.aucMerchantID = emvProcessParam.getTermConfig().getMerchantId().getBytes();
        readerParam.aucMerchCatCode = emvProcessParam.getTermConfig().getMerchantCategoryCode();
        readerParam.aucTmCntrCode = emvProcessParam.getTermConfig().getTerminalCountryCode();
        readerParam.aucTmRefCurCode = emvProcessParam.getTermConfig().getTransReferenceCurrencyCode();
        readerParam.ucTmRefCurExp = emvProcessParam.getTermConfig().getTransReferenceCurrencyExponent();
        readerParam.ulReferCurrCon = emvProcessParam.getTermConfig().getConversionRatio();
        readerParam.aucTmID = emvProcessParam.getEmvTransParam().getTerminalID().getBytes();
        readerParam.aucTmTransCur = emvProcessParam.getEmvTransParam().getTransCurrencyCode();
        readerParam.ucTmTransCurExp = emvProcessParam.getEmvTransParam().getTransCurrencyExponent();
        readerParam.ucTmType = aid.getTermType();
        readerParam.aucTmCap[2] = aid.getSecurityCapability();

        return readerParam;
    }

    // to Clss_VisaAidParam byte ucCvmReqNum, byte[] aucCvmReq.
    private byte[] toCvmReqTypes(PayWaveAid aid) {
        byte[] ttq = aid.getReaderTtq();// tag 9F66

        //support online pin and signature
        if ((ttq[0] & (byte) 0x06) == 0x06) {
            return new byte[]{0x02, 0x01}; //byte[0] = 0x02 mean support online pin. byte[1] = 0x01 mean support signature. ucCvmReqNum = 2.
        } else if (((ttq[0] & (byte) 0x02) == 0x02) && ((ttq[0] & (byte) 0x04) == 0x00)) {//support signature but not support online pin
            return new byte[]{0x01}; // byte[0] = 0x01 mean support signature. ucCvmReqNum = 1.
        } else if (((ttq[0] & (byte) 0x04) == 0x04) && ((ttq[0] & (byte) 0x02) == 0x00)) {//support online pin but not support signature
            return new byte[]{0x02}; // byte[0] = 0x02 mean online pin. ucCvmReqNum = 1.
        }

        return new byte[0];
    }


    private int setProgramIdParam(ByteArray programId) {
        ArrayList<PayWaveProgramId> programIds = emvProcessParam.getPayWaveParam().getWaveProgramIdArrayList();

        for (PayWaveProgramId payWaveProgramId : programIds) {
            if (Arrays.equals(programId.data, payWaveProgramId.getProgramId())) {
                Clss_ProgramID clssProgramID = new Clss_ProgramID(payWaveProgramId.getContactlessTransactionLimit(),
                        payWaveProgramId.getContactlessCvmLimit(),
                        payWaveProgramId.getContactlessFloorLimit(),
                        payWaveProgramId.getContactlessFloorLimit(),
                        programId.data,
                        (byte) programId.length,
                        payWaveProgramId.getContactlessFloorLimitSupported(),
                        payWaveProgramId.getContactlessTransactionLimitSupported(),
                        payWaveProgramId.getCvmLimitSupported(),
                        payWaveProgramId.getContactlessFloorLimitSupported(),
                        payWaveProgramId.getStatusCheckSupported(),
                        payWaveProgramId.getZeroAmountNoAllowed(),
                        new byte[4]);
                return ClssWaveApi.Clss_SetDRLParam_Wave(clssProgramID);
            }

        }

        //if not find drl configure in paywave_param.clss_wave, continue.
        //depend on business, also can return RetCode.CLSS_PARAM_ERR to abort transaction
        return RetCode.EMV_OK;
    }

    private int setTransParam() {
        byte transType = emvProcessParam.getEmvTransParam().getTransType();
        int index = EmvParamConvert.getPayWaveInterFloorLimitIndexByTransType(transType, payWaveAid.getPayWaveInterFloorLimitList());
        byte area = 0x00; //supports domestic and international clss transaction
        byte[] cvmTypesSupport = toCvmReqTypes(payWaveAid);
        byte enDDAVerNo = 0x00;//Reader support all DDA version IC card offline transaction

        Clss_VisaAidParam clssVisaAidParam = new Clss_VisaAidParam(payWaveAid.getPayWaveInterFloorLimitList().get(index).getContactlessFloorLimit(),
                area, (byte) cvmTypesSupport.length, cvmTypesSupport, enDDAVerNo);

        int ret = ClssWaveApi.Clss_SetVisaAidParam_Wave(clssVisaAidParam);
        if (ret != RetCode.EMV_OK) {
            LogUtils.e(TAG, "Clss_SetVisaAidParam_Wave ret = " + ret);
            return ret;
        }

        ByteArray proID = new ByteArray();
        ret = getTlv(0x9F5A, proID);
        //card support program ID
        if (ret == RetCode.EMV_OK) {
            ret = setProgramIdParam(proID);
            if (ret != 0) {
                LogUtils.e(TAG, "setProgramIdParam ret = " + ret);
                return ret;
            }
        }

        return ClssWaveApi.Clss_SetTransData_Wave(transParam, preProcInterInfo);
    }

    private int coreInit() {
        int ret = ClssWaveApi.Clss_CoreInit_Wave();
        if (ret != RetCode.EMV_OK) {
            LogUtils.e(TAG, "Clss_CoreInit_Wave ret = " + ret);
            return ret;
        }

        payWaveAid = getAidParam();
        if (payWaveAid == null) {
            return RetCode.CLSS_PARAM_ERR;
        }

        ret = ClssWaveApi.Clss_SetReaderParam_Wave(toClssReaderParam(payWaveAid));
        if (ret != RetCode.EMV_OK) {
            LogUtils.e("Clss_SetReaderParam_Wave", "ret = " + ret);
            return ret;
        }

        return ClssWaveApi.Clss_SetFinalSelectData_Wave(finalSelectData, finalSelectDataLen);
    }

    @Override
    public TransResult startTransProcess() {

        int ret = coreInit();
        if (ret != RetCode.EMV_OK) {
            LogUtils.e(TAG, "coreInit ret = " + ret);
            return new TransResult(ret, TransResultEnum.RESULT_OFFLINE_DENIED, CvmResultEnum.CVM_NO_CVM);
        }

        ret = setTransParam();
        if (ret != RetCode.EMV_OK) {
            LogUtils.e(TAG, "setTransParam ret = " + ret);
            return new TransResult(ret, TransResultEnum.RESULT_OFFLINE_DENIED, CvmResultEnum.CVM_NO_CVM);
        }

        ACType acType = new ACType();
        ret = ClssWaveApi.Clss_Proctrans_Wave(transactionPath, acType);

        // send cmd err, can not prompt read card ok
        if(ret == RetCode.ICC_CMD_ERR){
            return new TransResult(ret, TransResultEnum.RESULT_OFFLINE_DENIED, CvmResultEnum.CVM_NO_CVM);
        }

        if (statusListener != null) {
            statusListener.onReadCardOk();
        }

        if (clssStatusListener != null) {
            clssStatusListener.onRemoveCard();
        }

        if (ret == RetCode.CLSS_REFER_CONSUMER_DEVICE
                && preProcInterInfo != null && (preProcInterInfo.aucReaderTTQ[0] & 0x20) == 0x20) {
            LogUtils.e(TAG, "Clss_Proctrans_Wave CLSS_REFER_CONSUMER_DEVICE and ttq support see phone = " + ret);
            return new TransResult(ret, TransResultEnum.RESULT_CLSS_SEE_PHONE, CvmResultEnum.CVM_CONSUMER_DEVICE);
        } else if (ret == RetCode.CLSS_USE_CONTACT) {
            return new TransResult(ret, TransResultEnum.RESULT_CLSS_TRY_ANOTHER_INTERFACE, CvmResultEnum.CVM_NO_CVM);
        } else if (ret != RetCode.EMV_OK) {
            LogUtils.e(TAG, "Clss_Proctrans_Wave ret = " + ret);
            return new TransResult(ret, TransResultEnum.RESULT_OFFLINE_DENIED, CvmResultEnum.CVM_NO_CVM);
        }

        if (acType.type == ACType.AC_AAC) {
            LogUtils.e(TAG, "Clss_Proctrans_Wave acType.type = " + acType.type);
            return new TransResult(ret, TransResultEnum.RESULT_OFFLINE_DENIED, CvmResultEnum.CVM_NO_CVM);
        }

        ret = processTransactionPath(acType);
        if (ret != RetCode.EMV_OK) {
            LogUtils.e(TAG, "processTransactionPath ret = " + ret);
             return new TransResult(ret, TransResultEnum.RESULT_OFFLINE_DENIED, CvmResultEnum.CVM_NO_CVM);
        }

        return genTransResult(acType);
    }

    private int processTransactionPath(ACType acType) {
        LogUtils.d(TAG, "processTradnsactionPath transactionPath.path = " + transactionPath.path);
        if (transactionPath.path == TransactionPath.CLSS_VISA_MSD
                || transactionPath.path == TransactionPath.CLSS_VISA_MSD_CVN17
                || transactionPath.path == TransactionPath.CLSS_VISA_MSD_LEGACY) {

            return processMSD();
        } else if (transactionPath.path == TransactionPath.CLSS_VISA_QVSDC
                || transactionPath.path == TransactionPath.CLSS_VISA_WAVE2) {
            return processWave(acType);
        }

        LogUtils.e(TAG, "processTransactionPath err transactionPath.path = " + transactionPath.path);
        return RetCode.CLSS_PARAM_ERR;
    }

    private int processMSD() {
        byte msdType = ClssWaveApi.Clss_GetMSDType_Wave();
        LogUtils.i(TAG, "clssWaveGetMSDType msdType = " + msdType);
        //get MSD track 2 data
        ByteArray waveGetTrack2List = new ByteArray();
        int ret = ClssWaveApi.Clss_nGetTrack2MapData_Wave(waveGetTrack2List);
        if (ret != RetCode.EMV_OK) {
            LogUtils.e(TAG, "Clss_nGetTrack2MapData_Wave ret = " + ret);
            return ret;
        }
        track2 = getTrack2FromTag57(convert.bcdToStr(waveGetTrack2List.data, waveGetTrack2List.length));
        return RetCode.EMV_OK;
    }

    private int processWave(ACType acType) {
        if(acType.type == ACType.AC_TC){
            //todo, exception files checking, if need
            //The exception files checking is implemented by application. When card returns TC,
            // application should check the exception file first. If the PAN is in exception file,
            // the application should decline the transaction immediately, otherwise, continue to Clss_ProcRestric_Wave.
        }
        int ret = ClssWaveApi.Clss_ProcRestric_Wave();
        if (ret != RetCode.EMV_OK) {
            LogUtils.e(TAG, "Clss_ProcRestric_Wave  ret =" + ret);
            return ret;
        }

        //Special purpose readers may perform offline data authentication for online transactions.
        // change the condition of Clss_CardAuth_Wave if need(like ttq byte1 bit 1 = 1, Offline Data Authentication for Online Authorizations supported)
        if (((acType.type == ACType.AC_TC) && transParam.ucTransType != 0x20) //no refund
                || ((preProcInterInfo.aucReaderTTQ[0] & (byte)0x01) == 0x01)) { //Offline Data Authentication for Online Authorizations supported
            ClssWaveApi.Clss_DelAllRevocList_Wave();
            ClssWaveApi.Clss_DelAllCAPK_Wave();
            addCapkRevList();

            DDAFlag flag = new DDAFlag();
            ret = ClssWaveApi.Clss_CardAuth_Wave(acType, flag);
            LogUtils.d(TAG, "after Clss_CardAuth_Wave acType =" + acType.type + ", flag = " + flag.flag);
            if (ret != RetCode.EMV_OK) {
                LogUtils.e(TAG, "Clss_CardAuth_Wave ret =" + ret);
                return ret;
            }
        }
        return ret;
    }

    private TransResult genTransResult(ACType acType) {
        LogUtils.d(TAG, "genTransResult acType:" + acType.type);
        TransResult result = new TransResult(RetCode.EMV_OK, TransResultEnum.RESULT_OFFLINE_DENIED, CvmResultEnum.CVM_NO_CVM);
        byte cvmType = ClssWaveApi.Clss_GetCvmType_Wave();
        if (cvmType < 0) {
            LogUtils.e(TAG, "Clss_GetCvmType_Wave ret = " + cvmType);
            if (cvmType == RetCode.CLSS_DECLINE) {
                result.setTransResult(TransResultEnum.RESULT_OFFLINE_DENIED);
                result.setResultCode(cvmType);
                return result;
            }
        }

        //set cvm result
        switch (cvmType) {
            case CvmType.RD_CVM_NO:
                result.setCvmResult(CvmResultEnum.CVM_NO_CVM);
                break;
            case CvmType.RD_CVM_ONLINE_PIN:
                result.setCvmResult(CvmResultEnum.CVM_ONLINE_PIN);
                break;
            case CvmType.RD_CVM_SIG:
                result.setCvmResult(CvmResultEnum.CVM_SIG);
                break;
            default:
                LogUtils.e(TAG, "cvmType Unkonwn = " + cvmType);
                result.setResultCode(RetCode.CLSS_PARAM_ERR);
                return result;
        }

        //set transResult
        switch (acType.type) {
            case ACType.AC_TC:
                result.setTransResult(TransResultEnum.RESULT_OFFLINE_APPROVED);
                break;
            case ACType.AC_AAC:
                result.setTransResult(TransResultEnum.RESULT_OFFLINE_DENIED);
                break;
            case ACType.AC_ARQC:
                result.setTransResult(TransResultEnum.RESULT_REQ_ONLINE);
                break;
            default:
                LogUtils.e(TAG, "acType Unkonwn = " + acType.type);
                result.setResultCode(RetCode.CLSS_PARAM_ERR);
                return result;
        }

        return result;
    }

    @Override
    public TransResult completeTransProcess(IssuerRspData issuerRspData) {
        KernType kernType = new KernType();
        ByteArray sltData = new ByteArray();

        int ret = ClssEntryApi.Clss_FinalSelect_Entry(kernType, sltData);
        LogUtils.d(TAG, "Clss_FinalSelect_Entry = " + ret);
        if (ret != RetCode.EMV_OK) {
            return new TransResult(ret, TransResultEnum.RESULT_ONLINE_CARD_DENIED, CvmResultEnum.CVM_NO_CVM);
        }

        if(issuerRspData.getAuthData() != null && issuerRspData.getAuthData().length != 0) {
            ret = ClssWaveApi.Clss_IssuerAuth_Wave(issuerRspData.getAuthData(), issuerRspData.getAuthData().length);
            LogUtils.d(TAG, "Clss_IssuerAuth_Wave = " + ret);
            if (ret != RetCode.EMV_OK) {
                return new TransResult(ret, TransResultEnum.RESULT_ONLINE_CARD_DENIED, CvmResultEnum.CVM_NO_CVM);
            }
        }

        if(issuerRspData.getScript() != null && issuerRspData.getScript().length != 0) {
            ret = ClssWaveApi.Clss_IssScriptProc_Wave(issuerRspData.getScript(), issuerRspData.getScript().length);
            LogUtils.d(TAG, "Clss_IssScriptProc_Wave = " + ret);
            if (ret != RetCode.EMV_OK) {
                LogUtils.e(TAG, "clssWaveIssScriptProc = " + ret);
                return new TransResult(ret, TransResultEnum.RESULT_ONLINE_CARD_DENIED, CvmResultEnum.CVM_NO_CVM);
            }
        }

        if(clssStatusListener != null) {
            clssStatusListener.onRemoveCard();
        }

        return new TransResult(RetCode.EMV_OK,TransResultEnum.RESULT_ONLINE_APPROVED, CvmResultEnum.CVM_NO_CVM);
    }

    @Override
    protected int addCapkAndRevokeList(EMV_CAPK emvCapk, EMV_REVOCLIST emvRevoclist) {
        int ret = 0;
        if(emvCapk != null) {
            ret = ClssWaveApi.Clss_AddCAPK_Wave(emvCapk);
            LogUtils.d(TAG, "Clss_AddCAPK_Wave ret :" + ret);
            if (ret != 0) {
                LogUtils.e(TAG, "Clss_AddCAPK_Wave ret :" + ret);
                return ret;
            }
        }

        if(emvRevoclist != null) {
            ret = ClssWaveApi.Clss_AddRevocList_Wave(emvRevoclist);
            LogUtils.d(TAG, "Clss_AddRevocList_Wave ret :" + ret);
            if (ret != 0) {
                LogUtils.e(TAG, "Clss_AddRevocList_Wave ret :" + ret);
                return ret;
            }
        }
        return RetCode.EMV_OK;
    }

    @Override
    public int getTlv(int tag, ByteArray value) {
        int ret = 0;
        LogUtils.d(TAG, "paywave getTlv tag = " + convert.bcdToStr(convert.intToByteArray(tag, IConvert.EEndian.BIG_ENDIAN)));
        ret = ClssWaveApi.Clss_GetTLVData_Wave((short) tag, value);
        LogUtils.d(TAG, "paywave getTlv tag = " + convert.bcdToStr(value.data, value.length));
        return ret;
    }

    @Override
    protected int setTlv(int tag, byte[] value) {
        LogUtils.d(TAG, "paywave setTlv tag = " + convert.bcdToStr(convert.intToByteArray(tag, IConvert.EEndian.BIG_ENDIAN)));
        LogUtils.d(TAG, "paywave setTlv value = " + value);
        return ClssWaveApi.Clss_SetTLVData_Wave((short) tag, value, value.length);
    }

    @Override
    protected String getTrack2() {
        if (track2 == null) {
            ByteArray waveGetTrack2List = new ByteArray();
            getTlv(0x57, waveGetTrack2List);
            track2 = getTrack2FromTag57(convert.bcdToStr(waveGetTrack2List.data, waveGetTrack2List.length));
        }
        return track2;
    }


    @Override
    protected boolean isNeedSecondTap(IssuerRspData issuerRspData) {
        LogUtils.d(TAG, "paywave check if need second tap");
        boolean flag = false;
        if (issuerRspData.getScript().length == 0
                && issuerRspData.getAuthData().length == 0) {
           return false;
        }

        ByteArray aucCTQ = new ByteArray();
        int ret = getTlv(0x9F6C, aucCTQ);
        if (ret != RetCode.EMV_OK) {
            LogUtils.e(TAG, "get ctq fail = " + ret);
        }

        if ((preProcInterInfo.aucReaderTTQ[2] & 0x80) == 0x80 && (aucCTQ.data[1] & 0x40) == 0x40){
            flag = true;
        }
        return flag;
    }
}

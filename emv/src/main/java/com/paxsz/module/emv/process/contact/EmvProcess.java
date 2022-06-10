/*
 *  ===========================================================================================
 *  = COPYRIGHT
 *          PAX Computer Technology(Shenzhen) CO., LTD PROPRIETARY INFORMATION
 *     This software is supplied under the terms of a license agreement or nondisclosure
 *     agreement with PAX Computer Technology(Shenzhen) CO., LTD and may not be copied or
 *     disclosed except in accordance with the terms in that agreement.
 *          Copyright (C) 2020 -? PAX Computer Technology(Shenzhen) CO., LTD All rights reserved.
 *  Description: // Detail description about the function of this module,
 *               // interfaces with the other modules, and dependencies.
 *  Revision History:
 *  Date	               Author	                   Action
 *  2020/05/26 	         Qinny Zhou           	      Create
 *  ===========================================================================================
 */

package com.paxsz.module.emv.process.contact;

import android.text.TextUtils;

import com.pax.commonlib.utils.LogUtils;
import com.pax.commonlib.utils.convert.ConvertHelper;
import com.pax.jemv.clcommon.ACType;
import com.pax.jemv.clcommon.ByteArray;
import com.pax.jemv.clcommon.EMV_APPLIST;
import com.pax.jemv.clcommon.EMV_CAPK;
import com.pax.jemv.clcommon.EMV_REVOCLIST;
import com.pax.jemv.clcommon.RetCode;
import com.pax.jemv.device.DeviceManager;
import com.pax.jemv.emv.api.EMVCallback;
import com.pax.jemv.emv.model.EmvMCKParam;
import com.pax.jemv.emv.model.EmvParam;
import com.paxsz.module.emv.param.EmvProcessParam;
import com.paxsz.module.emv.param.EmvTransParam;
import com.paxsz.module.emv.process.EmvBase;
import com.paxsz.module.emv.process.entity.IssuerRspData;
import com.paxsz.module.emv.process.entity.TransResult;
import com.paxsz.module.emv.process.enums.CvmResultEnum;
import com.paxsz.module.emv.process.enums.TransResultEnum;
import com.paxsz.module.emv.utils.EmvParamConvert;
import com.paxsz.module.emv.xmlparam.entity.common.Capk;
import com.paxsz.module.emv.xmlparam.entity.common.CapkRevoke;
import com.paxsz.module.emv.xmlparam.entity.common.Config;
import com.paxsz.module.emv.xmlparam.entity.contact.EmvAid;

import java.util.ArrayList;
import java.util.Arrays;

public class EmvProcess extends EmvBase {
    private static final int PCI_TIMEOUT = 60 * 1000;
    private static final String TAG = "EmvProcess";
    private static EmvProcess instance;

    private EmvParam emvParam;
    private EmvMCKParam mckParam;
    private EMVCallback emvCallback;
    private IEmvTransProcessListener emvProcessListener;

    private ArrayList<Capk> capkParamList;
    private ArrayList<CapkRevoke> revokeList;
    private ArrayList<EmvAid> aidList;
    private EmvTransParam transParam;


    private EmvProcess() {
        emvParam = new EmvParam();
        mckParam = new EmvMCKParam();
    }

    public static EmvProcess getInstance() {
        if (instance == null) {
            instance = new EmvProcess();
        }
        return instance;
    }

    public void registerEmvProcessListener(IEmvTransProcessListener emvTransProcessListener) {
        this.emvProcessListener = emvTransProcessListener;
    }

    @Override
    public int preTransProcess(EmvProcessParam emvProcessParam) {
        capkParamList = emvProcessParam.getCapkParam().getCapkList();
        revokeList = emvProcessParam.getCapkParam().getCapkRevokeList();
        transParam = emvProcessParam.getEmvTransParam();
        aidList = emvProcessParam.getEmvAidList();
        LogUtils.w(TAG, "capkList:" + capkParamList.size());
        LogUtils.w(TAG, "revokeList:" + revokeList.size());
        //1.Core init
        int ret = EMVCallback.EMVCoreInit();
        if (ret != RetCode.EMV_OK) {
            return ret;
        }
        //2.set param(trans amt,ICS)
        ret = setEmvAndMCKParam(emvProcessParam);
        if (ret != RetCode.EMV_OK) {
            return ret;
        }
        //Set whether the kernel uses PCI verify offline PIN interface or not.
        // 1: use PIC verify offline PIN
        // 0: not use.
        EMVCallback.EMVSetPCIModeParam((byte) 1, "0,4,5,6,7,8,9,10,11,12".getBytes(), PCI_TIMEOUT);
        //3.Add AID
        ret = addAID(aidList);
        return ret;
    }


    private int setEmvAndMCKParam(EmvProcessParam emvProcessParam) {
        EMVCallback.EMVSetCallback();
        EMVCallback.EMVGetParameter(emvParam);
        EMVCallback.EMVGetMCKParam(mckParam);
        emvCallback = EMVCallback.getInstance();
        emvCallback.setCallbackListener(new EmvCallBackListener());


        Config terminalCfg = emvProcessParam.getTermConfig();
        EmvTransParam emvTransParam = emvProcessParam.getEmvTransParam();
        if (emvProcessParam.getEmvAidList().isEmpty() || terminalCfg == null || emvTransParam == null) {
            throw new IllegalArgumentException();
        }

        EmvAid emvAid = emvProcessParam.getEmvAidList().get(0);//first one is default

        emvParam.capability[0] = emvAid.getCardDataInputCapability();
        emvParam.capability[1] = emvAid.getCvmCapability();
        emvParam.capability[2] = emvAid.getSecurityCapability();
        emvParam.countryCode = terminalCfg.getTerminalCountryCode();
        emvParam.exCapability = emvAid.getAdditionalTerminalCapabilities();
        emvParam.forceOnline = (byte) emvAid.getForcedOnlineCapability();
        emvParam.getDataPIN = (byte) emvAid.getGetDataForPINTryCounter();
        emvParam.merchCateCode = terminalCfg.getMerchantCategoryCode();

        emvParam.referCurrCode = terminalCfg.getTransReferenceCurrencyCode();
        emvParam.referCurrCon = terminalCfg.getConversionRatio();
        emvParam.referCurrExp = terminalCfg.getTransReferenceCurrencyExponent();
        emvParam.surportPSESel = 0x01;
        emvParam.terminalType = emvAid.getTerminalType();
        emvParam.transCurrCode = emvTransParam.getTransCurrencyCode();
        emvParam.transCurrExp = emvTransParam.getTransCurrencyExponent();
        emvParam.transType = emvTransParam.getTransType();
        emvParam.termId = emvTransParam.getTerminalID().getBytes();
        emvParam.merchId = terminalCfg.getMerchantId().getBytes();
        emvParam.merchName = terminalCfg.getMerchantNameAndLocation().split(",")[0].getBytes();

        mckParam.ucBypassPin = (emvAid.getBypassPINEntry() == 0) ? (byte) 0 : 1;
        mckParam.ucBatchCapture = 1;

        mckParam.extmParam.aucTermAIP = new byte[]{0x08, 0x00};// The bit4 of byte1 decide whether force to perform TRM: "08 00"-Yes;"00 00"-No(default)
        mckParam.extmParam.ucBypassAllFlg = (emvAid.getSubsequentBypassPINEntry() == 0) ? (byte) 0 : 1;
        mckParam.extmParam.ucUseTermAIPFlg = 1;// 0-TRM is based on AIP of card(default),1-TRM is based on AIP of Terminal

        EMVCallback.EMVSetParameter(emvParam);
        return EMVCallback.EMVSetMCKParam(mckParam);
    }

    private int addAID(ArrayList<EmvAid> aidList) {
        int ret = RetCode.EMV_OK;
        if (aidList == null) {
            throw new IllegalArgumentException();
        }
        LogUtils.w(TAG, "addAID aidList:" + aidList.size());

        for (EmvAid emvAid : aidList) {
            ret = EMVCallback.EMVAddApp(EmvParamConvert.toEMVApp(emvAid));
            if (ret != RetCode.EMV_OK) {
                return ret;
            }
        }
        return ret;
    }

    @Override
    public TransResult startTransProcess() {
        LogUtils.i(TAG, "startTransProcess");
        //After detected card,start emv process
        //1.App select
        int ret = EMVCallback.EMVAppSelect(0, Long.parseLong(transParam.getTransTraceNo()));
        LogUtils.i(TAG, "startTransProcess, EMVAppSelect ret:" + ret);
        if (ret != RetCode.EMV_OK) {
            switch (ret) {
                case RetCode.EMV_DATA_ERR:
                case RetCode.EMV_NO_APP:
                case RetCode.EMV_RSP_ERR:
                case RetCode.ICC_RSP_6985:
                case RetCode.ICC_RESET_ERR:
                case RetCode.ICC_CMD_ERR:
                    return new TransResult(ret, TransResultEnum.RESULT_FALLBACK, CvmResultEnum.CVM_NO_CVM);
                default:
                    return new TransResult(ret, TransResultEnum.RESULT_OFFLINE_DENIED, CvmResultEnum.CVM_NO_CVM);
            }
        }
        //2.Read App Data
        ret = EMVCallback.EMVReadAppData();
        LogUtils.i(TAG, "startTransProcess, EMVReadAppData ret:" + ret);
        if (ret != RetCode.EMV_OK) {
            return new TransResult(ret, TransResultEnum.RESULT_OFFLINE_DENIED, CvmResultEnum.CVM_NO_CVM);
        }
        if (statusListener != null) {
            statusListener.onReadCardOk();
        }

        changeTAG9CValue();//PAX emv kernel not define the refund(0x20) trans,and the value will be transfer to 0x40

        //3.Add Capk revoke and Card Auth
        ret = addCapk();  //ignore return value for some case which the card doesn't has the capk index
        LogUtils.i(TAG, "startTransProcess, addCapk ret:" + ret);
        ret = EMVCallback.EMVCardAuth();
        LogUtils.i(TAG, "startTransProcess, EMVCardAuth ret:" + ret);
        if (ret != RetCode.EMV_OK) {
            return new TransResult(ret, TransResultEnum.RESULT_OFFLINE_DENIED, CvmResultEnum.CVM_NO_CVM);
        }

        //4. Processing restrictions->CardHolder verification->Terminal risk management->First GAC
        ACType acType = new ACType();
        long authAmt = TextUtils.isEmpty(transParam.getAmount()) ? 0 : Long.parseLong(transParam.getAmount());
        long cashbackAmt = TextUtils.isEmpty(transParam.getAmountOther()) ? 0 : Long.parseLong(transParam.getAmountOther());
        ret = EMVCallback.EMVStartTrans(authAmt, cashbackAmt, acType);
        LogUtils.i(TAG, "startTransProcess, EMVStartTrans ret:" + ret + ", acType:" + acType.type);
        if (ret != RetCode.EMV_OK) {
            return new TransResult(ret, TransResultEnum.RESULT_OFFLINE_DENIED, CvmResultEnum.CVM_NO_CVM);
        }

        TransResult transResult = new TransResult(RetCode.EMV_OK, TransResultEnum.RESULT_OFFLINE_DENIED, CvmResultEnum.CVM_NO_CVM);
        transResult.setResultCode(ret);
        if (acType.type == ACType.AC_TC) {
            transResult.setTransResult(TransResultEnum.RESULT_OFFLINE_APPROVED);
        } else if (acType.type == ACType.AC_AAC) {
            transResult.setTransResult(TransResultEnum.RESULT_OFFLINE_DENIED);
        } else {
            transResult.setTransResult(TransResultEnum.RESULT_REQ_ONLINE);
        }
        transResult.setCvmResult(getCvm());
        return transResult;

    }

    private void changeTAG9CValue() {
        setEmvTlv(0x9C, new byte[]{emvParam.transType});
    }

    private CvmResultEnum getCvm() {
        CvmResultEnum cvmResut = CvmResultEnum.CVM_ONLINE_PIN;

        ByteArray cvmType = new ByteArray();
        getTlv(0x9F34, cvmType);
        if (cvmType.length > 0) {
            LogUtils.i(TAG, "saveProcessData  cvmType:" + ConvertHelper.getConvert().bcdToStr(cvmType.data));
            switch (cvmType.data[0] & 0x3F) {
                case 0x01:
                case 0x04:
                    cvmResut = CvmResultEnum.CVM_OFFLINE_PIN;
                    break;
                case 0x02:
                    cvmResut = CvmResultEnum.CVM_ONLINE_PIN;
                    break;
                case 0x1E:
                    cvmResut = CvmResultEnum.CVM_SIG;
                    break;
                case 0x03:
                case 0x05:
                    cvmResut = CvmResultEnum.CVM_ONLINE_PIN_SIG;
                    break;
                case 0x1F:
                    cvmResut = CvmResultEnum.CVM_NO_CVM;
                    break;
                default:
                    cvmResut = CvmResultEnum.CVM_ONLINE_PIN;
                    break;
            }
        }
        LogUtils.i(TAG, "getCvm result:" + cvmResut);
        return cvmResut;
    }

    private int addCapk() {
        //get Capk with matched RID and KeyID
        ByteArray dataList = new ByteArray();
        int ret = EMVCallback.EMVGetTLVData((short) 0x4F, dataList);
        if (ret != RetCode.EMV_OK) {
            ret = EMVCallback.EMVGetTLVData((short) 0x84, dataList);
        }
        if (dataList.length < 5) {
            return ret;
        }
        LogUtils.d(TAG, "addCapk, dataList 1 :" + ConvertHelper.getConvert().bcdToStr(dataList.data));
        byte[] rid = new byte[5];
        System.arraycopy(dataList.data, 0, rid, 0, 5);
        LogUtils.d(TAG, "addCapk, RID :" + ConvertHelper.getConvert().bcdToStr(rid));
        ret = EMVCallback.EMVGetTLVData((short) 0x8F, dataList);
        if (ret != RetCode.EMV_OK) {
            return ret;
        }
        LogUtils.d(TAG, "addCapk, dataList 2 :" + ConvertHelper.getConvert().bcdToStr(dataList.data));
        byte keyId = dataList.data[0];
        LogUtils.d(TAG, "addCapk  KeyID :" + keyId);
        for (Capk capk : capkParamList) {
            if (new String(capk.getRid()).equals(new String(rid)) && capk.getKeyId() == keyId) {
                EMV_CAPK emvCapk = EmvParamConvert.toEMVCapk(capk);
                ret = EMVCallback.EMVAddCAPK(emvCapk);
            }
        }

        EMVCallback.EMVDelAllRevocList();
        for (CapkRevoke capkRevoke : revokeList) {
            if (new String(capkRevoke.getRid()).equals(new String(rid)) && capkRevoke.getKeyId() == keyId) {
                EMV_REVOCLIST emvRevocList = new EMV_REVOCLIST(rid, keyId, capkRevoke.getCertificateSN());
                ret = EMVCallback.EMVAddRevocList(emvRevocList);
            }
        }

        return ret;
    }

    @Override
    public TransResult completeTransProcess(IssuerRspData issuerRspData) {
        TransResult completeTransResult = new TransResult(RetCode.EMV_OK, TransResultEnum.RESULT_ONLINE_CARD_DENIED, CvmResultEnum.CVM_NO_CVM);
        ACType acType = new ACType();
        setEmvTlv(0x8a, issuerRspData.getRespCode());
        LogUtils.i(TAG, "response code(8A):" + ConvertHelper.getConvert().bcdToStr(issuerRspData.getRespCode()));
        if (issuerRspData.getAuthCode().length != 0) {
            LogUtils.i(TAG, "");
            setEmvTlv(0x89, issuerRspData.getAuthCode());
            LogUtils.i(TAG, "auth code(89):" + ConvertHelper.getConvert().bcdToStr(issuerRspData.getAuthCode()));
        }
        if (issuerRspData.getAuthData().length != 0) {
            setEmvTlv(0x91, issuerRspData.getAuthData());
            LogUtils.i(TAG, "auth data(89):" + ConvertHelper.getConvert().bcdToStr(issuerRspData.getAuthData()));
        }
        LogUtils.i(TAG, "online result:" + issuerRspData.getOnlineResult());
        LogUtils.i(TAG, "issuer script:" + ConvertHelper.getConvert().bcdToStr(issuerRspData.getScript()));
        int ret = EMVCallback.EMVCompleteTrans(issuerRspData.getOnlineResult(), issuerRspData.getScript(), issuerRspData.getScript().length, acType);
        LogUtils.d(TAG, "EMVCallback.EMVCompleteTrans ret =" + ret);

        if (ret != RetCode.EMV_OK) {
            ByteArray scriptResult = new ByteArray();
            EMVCallback.EMVGetScriptResult(scriptResult);
            LogUtils.d(TAG, "EMVGetScriptResult scriptResult" + ConvertHelper.getConvert().bcdToStr(scriptResult.data));
            completeTransResult.setResultCode(ret);
            completeTransResult.setTransResult(TransResultEnum.RESULT_ONLINE_CARD_DENIED);
            return completeTransResult;
        }
        completeTransResult.setResultCode(ret);
        LogUtils.i(TAG, "completeTransProcess,acType:" + acType.type);
        if (acType.type == ACType.AC_TC) {
            completeTransResult.setTransResult(TransResultEnum.RESULT_ONLINE_APPROVED);
        } else if (acType.type == ACType.AC_AAC) {
            completeTransResult.setTransResult(TransResultEnum.RESULT_ONLINE_CARD_DENIED);
        }
        return completeTransResult;
    }

    private int setEmvTlv(int tag, byte[] value) {
        return EMVCallback.EMVSetTLVData((short) tag, value, value.length);
    }

    @Override
    public int getTlv(int tag, ByteArray byteArray) {
        int ret = EMVCallback.EMVGetTLVData((short) tag, byteArray);
        if (ret == RetCode.EMV_OK) {
            Arrays.copyOfRange(byteArray.data, 0, byteArray.length);
        }
        return ret;
    }


    private class EmvCallBackListener implements EMVCallback.EmvCallbackListener {
        private boolean isFirstCall = true;
        private int tmpRemainCount = 0;

        @Override
        public void emvWaitAppSel(int tryCnt, EMV_APPLIST[] appLists, int appNum) {
            ArrayList<CandidateAID> candidateAIDS = new ArrayList<>();
            LogUtils.i(TAG, "candidate AID list size:" + appLists.length + ",appNum:" + appNum + ", tryCnt:" + tryCnt);
            CandidateAID candidateAID = null;
            int size = Math.min(appLists.length, appNum);
            for (int i = 0; i < size; i++) {
                candidateAID = new CandidateAID();
                candidateAID.setAid(appLists[i].aid);
                candidateAID.setAidLen(appLists[i].aidLen);
                candidateAID.setAppName(appLists[i].appName);
                candidateAID.setPriority(appLists[i].priority);
                candidateAIDS.add(candidateAID);
            }
            if (emvProcessListener != null) {
                int index = emvProcessListener.onWaitAppSelect(tryCnt <= 0, candidateAIDS);
                emvCallback.setCallBackResult(index);
            }
        }

        @Override
        public void emvInputAmount(long[] amt) {
            LogUtils.i(TAG, "emvInputAmount");
            amt[0] = Long.parseLong(transParam.getAmount());
            if (amt.length > 1) {
                if (transParam.getAmountOther() == null || transParam.getAmountOther().isEmpty()) {
                    amt[1] = 0;
                } else {
                    amt[1] = Long.parseLong(transParam.getAmountOther());
                }
            }

            emvCallback.setCallBackResult(RetCode.EMV_OK);
        }

        @Override
        public void emvGetHolderPwd(int tryFlag, int remainCnt, byte[] pinData) {
            if (this.isFirstCall) {
                this.tmpRemainCount = remainCnt;
                this.isFirstCall = false;
            }
            boolean isOnline = (null == pinData);
            if (isOnline) {
                LogUtils.i(TAG, "emvGetHolderPwd pin is null, tryFlag" + tryFlag + " remainCnt:" + remainCnt);
            } else {
                LogUtils.i(TAG, "emvGetHolderPwd pin is not null, tryFlag" + tryFlag + " remainCnt:" + remainCnt);
            }
            if (pinData != null && pinData[0] != 0) {
                if (pinData[0] == 1) {
                    LogUtils.e(TAG, "enter pin timeout");
                    emvCallback.setCallBackResult(RetCode.EMV_TIME_OUT);
                    return;
                } else {
                    emvCallback.setCallBackResult(pinData[0]);
                    return;
                }
            }
            int result;
            if (isOnline) {
                result = emvProcessListener.onCardHolderPwd(true, remainCnt, pinData);
            } else {
                result = emvProcessListener.onCardHolderPwd(false, this.tmpRemainCount, pinData);
                this.tmpRemainCount--;
            }
            LogUtils.w(TAG, "emvGetHolderPwd,result:" + result);
            emvCallback.setCallBackResult(result);
        }

        @Override
        public void emvAdviceProc() {
            LogUtils.i(TAG, "emvAdviceProc");
        }

        @Override
        public void emvVerifyPINOK() {
            LogUtils.i(TAG, "EMV library verify PIN and passes, prompt PIN OK");
        }

        @Override
        public int emvUnknowTLVData(short tag, ByteArray data) {
            LogUtils.i(TAG, "emvUnknowTLVData, tag: " + Integer.toHexString(tag) + " data:" + data.data.length);
            //for prolin platform,the kernel will obtain the following Tags' value by this function
            switch ((int) tag) {
                case 0x9A:
                    byte[] date = new byte[7];
                    DeviceManager.getInstance().getTime(date);
                    System.arraycopy(date, 1, data.data, 0, 3);
                    break;
                case 0x9F1E:
                    byte[] sn = new byte[10];
                    DeviceManager.getInstance().readSN(sn);
                    System.arraycopy(sn, 0, data.data, 0, Math.min(data.data.length, sn.length));
                    break;
                case 0x9F21:
                    byte[] time = new byte[7];
                    DeviceManager.getInstance().getTime(time);
                    System.arraycopy(time, 4, data.data, 0, 3);
                    break;
                case 0x9F37:
                    byte[] random = new byte[4];
                    DeviceManager.getInstance().getRand(random, 4);
                    System.arraycopy(random, 0, data.data, 0, data.data.length);
                    break;
                case 0xFF01:
                    Arrays.fill(data.data, (byte) 0x00);
                    break;
                default:
                    return -1;
            }
            data.length = data.data.length;
            return RetCode.EMV_OK;
        }

        @Override
        public void certVerify() {
//            Cardholder credential verify(PBOC)
        }

        @Override
        public int emvSetParam() {
//          This function is used to set some AID specific parameter after performing application selection and before GPO. Application can call EMVSetTLVData in this function to set these parameters.
//          When the return value of this function is not EMV_OK, kernel will abort the current transaction.
            ByteArray byteArray = new ByteArray();
            int ret = EMVCallback.EMVGetTLVData((short) 0x4f, byteArray);
            LogUtils.d(TAG, "emvSetParam EMVGetTLVData ret =" + ret + "byteArray.length = " + byteArray.length);
            if (ret != RetCode.EMV_OK) {
                return RetCode.EMV_OK;
            }
            LogUtils.d(TAG, "emvSetParam EMVGetTLVData ret =" + ConvertHelper.getConvert().bcdToStr(byteArray.data));
            byte aid[] = Arrays.copyOfRange(byteArray.data, 0, byteArray.length);
            LogUtils.d(TAG, "emvSetParam aid =" + ConvertHelper.getConvert().bcdToStr(aid));
            resetParam(aid);
            return RetCode.EMV_OK;
        }

        @Override
        public int emvVerifyPINfailed(byte[] reserved) {
            return 0;
        }

        @Override
        public int cRFU2() {
            return 0;
        }

    }

    private void resetParam(byte[] aid) {
        EmvAid selectedAid = null;
        for (EmvAid emvAid : aidList) {
            //finalSelectData[0] is aid len
            if (Arrays.equals(aid, emvAid.getApplicationID()) //full match
                    || (emvAid.getPartialAIDSelection() == 0 && (emvAid.getApplicationID().length < aid.length) && (Arrays.equals(Arrays.copyOfRange(aid, 0, emvAid.getApplicationID().length), emvAid.getApplicationID())))) {//partial match
                selectedAid = emvAid;
                LogUtils.i(TAG, "matched aid:" + ConvertHelper.getConvert().bcdToStr(selectedAid.getApplicationID()) + ",CvmCapability:" + ConvertHelper.getConvert().bcdToStr(new byte[]{selectedAid.getCvmCapability()}));
            }
        }
        if (selectedAid == null) {
            return;
        }

        EMVCallback.EMVGetParameter(emvParam);

        emvParam.capability[0] = selectedAid.getCardDataInputCapability();
        emvParam.capability[1] = selectedAid.getCvmCapability();
        emvParam.capability[2] = selectedAid.getSecurityCapability();
        emvParam.exCapability = selectedAid.getAdditionalTerminalCapabilities();
        emvParam.forceOnline = (byte) selectedAid.getForcedOnlineCapability();
        emvParam.getDataPIN = (byte) selectedAid.getGetDataForPINTryCounter();
        emvParam.terminalType = selectedAid.getTerminalType();

        LogUtils.d(TAG, "resetParam emvParam.capability =" + ConvertHelper.getConvert().bcdToStr(emvParam.capability));
        LogUtils.d(TAG, "resetParam emvParam.exCapability =" + ConvertHelper.getConvert().bcdToStr(emvParam.exCapability));
        EMVCallback.EMVSetParameter(emvParam);

        // it seems that  after calling EMVSetParameter, the value of TAG 9F33 still didn't change
        // try to set the TAGs directly here
        EMVCallback.EMVSetTLVData((short) 0x9f33, emvParam.capability, emvParam.capability.length);
        EMVCallback.EMVSetTLVData((short) 0x9f40, emvParam.exCapability, emvParam.exCapability.length);
        EMVCallback.EMVSetTLVData((short) 0x9f35, new byte[]{emvParam.terminalType}, 1);

        mckParam.ucBypassPin = (selectedAid.getBypassPINEntry() == 0) ? (byte) 0 : 1;
        mckParam.ucBatchCapture = 1;

        mckParam.extmParam.aucTermAIP = new byte[]{0x08, 0x00};
        mckParam.extmParam.ucBypassAllFlg = (selectedAid.getSubsequentBypassPINEntry() == 0) ? (byte) 0 : 1;
        mckParam.extmParam.ucUseTermAIPFlg = 1;

        int ret = EMVCallback.EMVGetMCKParam(mckParam);
        LogUtils.d(TAG, "resetParam EMVGetMCKParam ret=" + ret);


    }

}

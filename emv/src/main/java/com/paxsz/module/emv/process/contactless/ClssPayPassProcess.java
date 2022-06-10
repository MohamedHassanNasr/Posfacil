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
import com.pax.jemv.clcommon.EMV_CAPK;
import com.pax.jemv.clcommon.EMV_REVOCLIST;
import com.pax.jemv.clcommon.RetCode;
import com.pax.jemv.clcommon.TransactionPath;
import com.pax.jemv.device.model.ApduRespL2;
import com.pax.jemv.device.model.ApduSendL2;
import com.pax.jemv.paypass.api.ClssPassApi;
import com.pax.jemv.paypass.listener.ClssPassCBFunApi;
import com.pax.jemv.paypass.listener.IClssPassCBFun;
import com.paxsz.module.emv.param.EmvTransParam;
import com.paxsz.module.emv.process.entity.IssuerRspData;
import com.paxsz.module.emv.process.entity.TransResult;
import com.paxsz.module.emv.process.enums.CvmResultEnum;
import com.paxsz.module.emv.process.enums.TransResultEnum;
import com.paxsz.module.emv.xmlparam.entity.clss.PayPassAid;
import com.paxsz.module.emv.xmlparam.entity.common.Config;

import java.util.Arrays;

class ClssPayPassProcess extends ClssKernelProcess {
    private static final String TAG = "ClssPayPassProcess";
    private ClssPassListener clssPassListener = new ClssPassListener();
    private ClssPassCBFunApi passCBFun = ClssPassCBFunApi.getInstance();

    @Override
    protected int addCapkAndRevokeList(EMV_CAPK emvCapk, EMV_REVOCLIST emvRevoclist) {
        int ret = 0;

        if(emvCapk != null) {
            ret = ClssPassApi.Clss_AddCAPK_MC_MChip(emvCapk);
            if (ret != 0) {
                LogUtils.e(TAG, "Clss_AddCAPK_MC_MChip ret :" + ret);
                return ret;
            }
        }

        if(emvRevoclist != null) {
            ret = ClssPassApi.Clss_AddRevocList_MC_MChip(emvRevoclist);
            if (ret != 0) {
                LogUtils.e(TAG, "Clss_AddRevocList_MC_MChip ret :" + ret);
                return ret;
            }
        }

        return RetCode.EMV_OK;
    }

    private int coreInit(){
        int ret = ClssPassApi.Clss_CoreInit_MC((byte) 0x01);
        if(ret != 0){
            LogUtils.e(TAG, "Clss_CoreInit_MC = " + ret);
            return ret;
        }

        //set the timer number for kernel [1/21/2014 ZhouJie]
        ClssPassApi.Clss_SetParam_MC(new byte[]{0x01, 0x01, 0x04}, 3);

        passCBFun.setICBFun(clssPassListener);
        ClssPassApi.Clss_SetCBFun_SendTransDataOutput_MC();

        return ClssPassApi.Clss_SetFinalSelectData_MC(finalSelectData, finalSelectDataLen);
    }

    @Override
    public TransResult startTransProcess() {
        int ret = coreInit();
        if(ret != RetCode.EMV_OK){
            return new TransResult(ret, TransResultEnum.RESULT_OFFLINE_DENIED, CvmResultEnum.CVM_NO_CVM);
        }

        setPayPassParam();

        ret = ClssPassApi.Clss_InitiateApp_MC();
        if(ret != RetCode.EMV_OK){
            LogUtils.e(TAG, "Clss_InitiateApp_MC ret :" + ret);
            return new TransResult(ret, TransResultEnum.RESULT_OFFLINE_DENIED, CvmResultEnum.CVM_NO_CVM);
        }

        ret = ClssPassApi.Clss_ReadData_MC(transactionPath);
        if(ret != RetCode.EMV_OK){
            LogUtils.e(TAG, "Clss_ReadData_MC ret :" + ret);
            return new TransResult(ret, TransResultEnum.RESULT_OFFLINE_DENIED, CvmResultEnum.CVM_NO_CVM);
        }


        ACType acType = new ACType();
        if (transactionPath.path == TransactionPath.CLSS_MC_MCHIP) {
            ret = processMChip(acType);
        } else if (transactionPath.path == TransactionPath.CLSS_MC_MAG) {
            ret = processMag(acType);
        }

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

        /**
         * see phone
         * If ‘On device cardholder verification is supported' (TAG '82' Byte1 b2)in Application Interchange Profile is set and
         * 'On device cardholder verification supported'(TAG 'DF811B' Byte1,b6) in Kernel Configuration is set,
         * the kernel will return SEE PHONE in Message Identifier (byte1) of DF8116.
         */
        LogUtils.d(TAG, "clssPassListener.userInterReqData.data[0] = " + clssPassListener.userInterReqData.data[0]);
        if(clssPassListener.userInterReqData.data[0] == 0x20){
            return new TransResult(ret, TransResultEnum.RESULT_CLSS_SEE_PHONE, CvmResultEnum.CVM_CONSUMER_DEVICE);
        }

        if(ret != RetCode.EMV_OK){
            return new TransResult(ret, TransResultEnum.RESULT_OFFLINE_DENIED, CvmResultEnum.CVM_NO_CVM);
        }

        LogUtils.d(TAG, "outcomeParamSet data:" + convert.bcdToStr(clssPassListener.outcomeParamSet.data));
        if (clssPassListener.outcomeParamSet.data[0] == 0x70 || clssPassListener.outcomeParamSet.data[1] != (byte) 0xF0) {
            return new TransResult(ret, TransResultEnum.RESULT_TRY_AGAIN, CvmResultEnum.CVM_NO_CVM);
        }

        return genTransResult();
    }


    //generate transult and cvm result
    private TransResult genTransResult() {
        TransResult transResult = new TransResult(RetCode.EMV_OK, TransResultEnum.RESULT_OFFLINE_DENIED, CvmResultEnum.CVM_NO_CVM);

        //set TransResult
        LogUtils.i(TAG, convert.bcdToStr(clssPassListener.outcomeParamSet.data));
        switch (clssPassListener.outcomeParamSet.data[0] & 0xF0) {
            case 0x10:
                transResult.setTransResult(TransResultEnum.RESULT_OFFLINE_APPROVED);
                break;
            case 0x30:
                transResult.setTransResult(TransResultEnum.RESULT_REQ_ONLINE);
                break;
            case 0x60:
                transResult.setTransResult(TransResultEnum.RESULT_CLSS_TRY_ANOTHER_INTERFACE);
                transResult.setResultCode(RetCode.CLSS_USE_CONTACT);
                break;
            case 0x20:
            default:
                transResult.setTransResult(TransResultEnum.RESULT_OFFLINE_DENIED);
        }

        //set cvm result
        switch (clssPassListener.outcomeParamSet.data[3] & 0xF0) {
            case 0x10:
                transResult.setCvmResult(CvmResultEnum.CVM_SIG);
                LogUtils.d(TAG, "CVM = signature");
                break;
            case 0x20:
                transResult.setCvmResult(CvmResultEnum.CVM_ONLINE_PIN);
                LogUtils.d(TAG, "CVM = online pin");
                break;
            default:
                transResult.setCvmResult(CvmResultEnum.CVM_NO_CVM);
                LogUtils.d(TAG, "CVM = no cvm");
                break;
        }

        return transResult;
    }

    private int processMChip(ACType acType) {
        ClssPassApi.Clss_DelAllRevocList_MC_MChip();
        ClssPassApi.Clss_DelAllCAPK_MC_MChip();
        addCapkRevList();


        int ret = ClssPassApi.Clss_TransProc_MC_MChip(acType);
        LogUtils.d(TAG, "Clss_TransProc_MC_MChip = " + ret + "  ACType = " + acType.type);

        return ret;
    }

    private int processMag(ACType acType) {
        int ret = ClssPassApi.Clss_TransProc_MC_Mag(acType);
        LogUtils.d(TAG, "Clss_TransProc_MC_Mag = " + ret + "  ACType = " + acType.type);
        return ret;
    }

    /**
     * PayPass does not have sencond Gac, do nothing in this process
     */
    @Override
    public TransResult completeTransProcess(IssuerRspData issuerRspData) {
        return new TransResult(RetCode.EMV_OK, TransResultEnum.RESULT_ONLINE_APPROVED, CvmResultEnum.CVM_NO_CVM);
    }

    @Override
    public int getTlv(int tag, ByteArray value) {
        byte[] bcdTag = convert.intToByteArray(tag, IConvert.EEndian.BIG_ENDIAN);//convert hex to bcd, 0x1234->byte[]{0x12,0x34}
        int ret = ClssPassApi.Clss_GetTLVDataList_MC(bcdTag, (byte) bcdTag.length, value.length, value);
        LogUtils.d(TAG, "paypass bcdTag :" + convert.bcdToStr(bcdTag));
        LogUtils.d(TAG, "paypass gettlv :" + convert.bcdToStr(value.data));
        return ret;
    }

    @Override
    protected int setTlv(int tag, byte[] value) {
        byte[] bcdTag = convert.intToByteArray(tag, IConvert.EEndian.BIG_ENDIAN);//convert hex to bcd, 0x1234->byte[]{0x12,0x34}
        LogUtils.d(TAG, "paypass bcdTag :" + convert.bcdToStr(bcdTag));
        byte[] buf = new byte[bcdTag.length + 1 + (value != null ? value.length : 0)];

        System.arraycopy(bcdTag, 0, buf, 0, bcdTag.length);
        if (value != null) {
            buf[bcdTag.length] = (byte) value.length;
            System.arraycopy(value, 0, buf, bcdTag.length + 1, value.length);
        } else {
            buf[bcdTag.length] = 0x00;
        }
        LogUtils.d(TAG, "paypass setTlv :" + convert.bcdToStr(buf));
        return ClssPassApi.Clss_SetTLVDataList_MC(buf, buf.length);
    }

    @Override
    protected String getTrack2() {
        ByteArray track = new ByteArray();
        int ret = -1;
        if (transactionPath.path == TransactionPath.CLSS_MC_MCHIP) {
            ret = getTlv(0x57, track);
        } else if (transactionPath.path == TransactionPath.CLSS_MC_MAG) {
            ret = getTlv(0X9F6B, track);
        }

        if (ret == RetCode.EMV_OK) {
            return getTrack2FromTag57(convert.bcdToStr(track.data, track.length));
        }
        LogUtils.e(TAG, "paypass getTrack2 error ret :" + ret);
        return "";
    }

    //paypass no second tap
    @Override
    protected boolean isNeedSecondTap(IssuerRspData issuerRspData) {
        LogUtils.d(TAG, "paywave check if need second tap");
        return false;
    }

    //partial match, the aid len in xml file is less than kernel select in card
    private boolean isSupportPartialAidMatch(PayPassAid aid){
        boolean flag = false;
        if((aid.getPartialAIDSelection() == 0) && (aid.getApplicationId().length < finalSelectData[0])
                && (Arrays.equals(Arrays.copyOfRange(finalSelectData, 1,aid.getApplicationId().length + 1), aid.getApplicationId()))){
            flag = true;
        }
        return flag;
    }

    private PayPassAid getAidParam(){
        for(PayPassAid aid : emvProcessParam.getPayPassAidList()){
            //finalSelectData[0] is aid len
            if(Arrays.equals(Arrays.copyOfRange(finalSelectData, 1,finalSelectData[0] + 1), aid.getApplicationId()) //full match
                || isSupportPartialAidMatch(aid)){
                return aid;
            }
        }
        return null;
    }

    private int setAidParam() {
        PayPassAid aid = null;

        aid = getAidParam();
        if(aid == null){
            LogUtils.e(TAG, "setPayPassParam setAidParam error");
            return RetCode.CLSS_PARAM_ERR;
        }

        setTlv(0xDF811B, new byte[]{aid.getKernelConfiguration()});
        setTlv(0x9F1D, aid.getTerminalRisk());

        byte transType = emvProcessParam.getEmvTransParam().getTransType();
        //refund or void, need to req AAC
        //Refund required AAC  collis case:1.205 Test: PPC.MCD.03.Test.06.Scenario.01
        if(transType == 0x20 || transType == 0x02){

            setTlv(0xDF8123, new byte[]{0x00, 0x00, 0x00, 0x00, 0x00, 0x00});//Reader Contactless Floor Limit
            setTlv(0xDF8121, new byte[]{(byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF});//Terminal Action Code – Denial
        }else{
            //Reader Contactless Floor Limit
            String clssFloorLimit = convert.getPaddedNumber(aid.getContactlessFloorLimit(),12);
            setTlv(0xDF8123, convert.strToBcd(clssFloorLimit,IConvert.EPaddingPosition.PADDING_LEFT));
            setTlv(0xDF8121, aid.getTacDenial());//Terminal Action Code – Denial
        }

        //Reader Contactless Transaction Limit (No On-device CVM)
        String noOnDeviceLimt = convert.getPaddedNumber(aid.getContactlessTransactionLimitNoOnDevice(),12);
        setTlv(0xDF8124, convert.strToBcd(noOnDeviceLimt, IConvert.EPaddingPosition.PADDING_LEFT));

        //Reader Contactless Transaction Limit (On-device CVM)
        String onDeviceLimt = convert.getPaddedNumber(aid.getContactlessTransactionLimitOnDevice(),12);
        setTlv(0xDF8125, convert.strToBcd(onDeviceLimt, IConvert.EPaddingPosition.PADDING_LEFT));

        //Reader CVM Required Limit
        String cvmLimt = convert.getPaddedNumber(aid.getContactlessCvmLimit(),12);
        setTlv(0xDF8126, convert.strToBcd(cvmLimt, IConvert.EPaddingPosition.PADDING_LEFT));

        setTlv(0xDF8120, aid.getTacDefault());//Terminal Action Code – Default
        setTlv(0xDF8122, aid.getTacOnline());//Terminal Action Code – Online
        setTlv(0xDF811B, new byte[]{aid.getKernelConfiguration()});//Kernel Configuration
        setTlv(0x9F1D, aid.getTerminalRisk());//Terminal Risk Management Data
        setTlv(0xDF8117, new byte[]{aid.getCardDataInput()});//Card Data Input Capability
        setTlv(0xDF8118, new byte[]{aid.getCvmCapabilityCvmRequired()});//CVM Capability – CVM Required
        setTlv(0xDF8119, new byte[]{aid.getCvmCapabilityNoCvmRequired()});//CVM Capability – No CVM Required
        setTlv(0xDF811F, new byte[]{aid.getSecurityCapability()});//Security Capability
        setTlv(0xDF811E, new byte[]{aid.getMagneticCvm()});//Mag-stripe CVM Capability – CVM Required
        setTlv(0xDF812C, new byte[]{aid.getMageticNoCvm()});//Mag-stripe CVM Capability – No CVM Required

        //L3 Auth there are not cases about Torn Transaction
//        setTlv(0xDF811C, new byte[]{aid.getTornLeftTime()});//Max Lifetime of Torn Transaction Log Record
//        setTlv(0xDF811D, new byte[]{aid.getMaximumTornNumber()});//Max Number of Torn Transaction Log Records

        setTlv(0xDF810C, new byte[]{aid.getKernelId()});//Kernel ID
        setTlv(0x9F35, new byte[]{aid.getTerminalType()});//Terminal Type
        setTlv(0x9F40, aid.getAdditionalTerminalCapability());//Additional Terminal Capabilities
        setTlv(0x9F09, aid.getTerminalAidVersion());//Application Version Number
        setTlv(0xDF811A, new byte[]{(byte) 0x9F, 0x6A, 0x04});//Default UDOL

        return RetCode.EMV_OK;
    }

    private void setTermConfig(){
        Config config = emvProcessParam.getTermConfig();
        setTlv(0x9F16, config.getMerchantId().getBytes());//Merchant Identifier
        setTlv(0x9F15, config.getMerchantCategoryCode());//Merchant Category Code
        setTlv(0x9F4E, config.getMerchantNameAndLocation().getBytes());//Merchant Name and Location
        setTlv(0x9F1A, config.getTerminalCountryCode());//Terminal Country Code
        setTlv(0x9F3C, config.getTransReferenceCurrencyCode());//Transaction Reference Currency Code
        setTlv(0x9F3D, config.getTransReferenceCurrencyCode());//Transaction Reference Currency Exponent
    }

    private void setTransParam(){
        EmvTransParam emvTransParam = emvProcessParam.getEmvTransParam();

        byte[] amount = new byte[6];
        byte[] tmp = convert.strToBcd(emvTransParam.getAmount(), IConvert.EPaddingPosition.PADDING_LEFT);
        System.arraycopy(tmp, 0, amount, 6 - tmp.length, tmp.length);
        setTlv(0x9F02, amount);

        byte[] otherAmount = new byte[6];
        tmp = convert.strToBcd(emvTransParam.getAmountOther(), IConvert.EPaddingPosition.PADDING_LEFT);
        System.arraycopy(tmp, 0, otherAmount, 6 - tmp.length, tmp.length);
        setTlv(0x9F03, otherAmount);

        setTlv(0x9C, new byte[]{emvTransParam.getTransType()});
        setTlv(0x9A, convert.strToBcd(emvTransParam.getTransDate(), IConvert.EPaddingPosition.PADDING_LEFT));
        setTlv(0x9F21, convert.strToBcd(emvTransParam.getTransTime(), IConvert.EPaddingPosition.PADDING_LEFT));
    }

    private int setPayPassParam(){
        //setDefaultMcTermParam();// depend on the function you need.
        setTransParam();
        int ret = setAidParam();
        if(ret != 0){
            return ret;
        }
        setTermConfig();

        return RetCode.EMV_OK;
    }

    /**enable or disable kernel functions by Clss_SetTLVDataList_MC and Clss_SetTagPresent_MC
    * the detail describe in EMV Contactless Book C-2*/
    /*
    private void setDefaultMcTermParam(Clss_TransParam clssTransParam, AidParam aid, Clss_PreProcInfo info) {

        setTlv(0x5F57, null);
        setTlv(0x9F01, null);
        setTlv(0x9F1E, ConvertHelper.getConvert().strToBcd("1122334455667788", IConvert.EPaddingPosition.PADDING_LEFT));
        setTlv(0x9F15, null);
        setTlv(0x9F7E, null);

        setTlv(0xDF8108, null);
        setTlv(0xDF60, null);
        setTlv(0xDF8109, null);
        setTlv(0xDF62, null);
        setTlv(0xDF810A, null);
        setTlv(0xDF63, null);

        setTagPresent(0xDF8104, false);
        setTagPresent(0xDF8105, false);
        setTagPresent(0xDF812D, false);

        setEmptyTlv(0xDF8110);
        setEmptyTlv(0xDF8112);
        setEmptyTlv(0xFF8102);
        setEmptyTlv(0xFF8103);
        setEmptyTlv(0xDF8127);

        setTlv(0x9F5C, ConvertHelper.getConvert().strToBcd("7A45123EE59C7F40", IConvert.EPaddingPosition.PADDING_LEFT));
        setTagPresent(0xDF810D, false);
        setTagPresent(0x9F70, false);
        setTagPresent(0x9F75, false);

        setTlv(0x9F6D, new byte[]{0x00, 0x01});

        setTagPresent(0xDF8130, false);
        setTagPresent(0xDF812D, false);
    }


    private void setTagPresent(int tag, boolean present) {
        byte b = 0;
        if(present){
            b = 1;
        }
        ClssPassApi.Clss_SetTagPresent_MC(ConvertHelper.getConvert().intToByteArray(tag, IConvert.EEndian.BIG_ENDIAN), b);
    }



    private int setEmptyTlv(int tag) {
        byte[] bcdTag = ConvertHelper.getConvert().intToByteArray(tag, IConvert.EEndian.BIG_ENDIAN);
        return ClssPassApi.Clss_SetTLVDataList_MC(bcdTag, 0);
    }
     */


    private class ClssPassListener implements IClssPassCBFun {
        ByteArray outcomeParamSet = new ByteArray(8);
        ByteArray userInterReqData = new ByteArray(22);
        ByteArray errIndication = new ByteArray(6);

        @Override
        public int sendDEKData(byte[] bytes, int i) {
            return 0;
        }

        @Override
        public int receiveDETData(ByteArray byteArray, byte[] bytes) {
            return 0;
        }

        @Override
        public int addAPDUToTransLog(ApduSendL2 apduSendL2, ApduRespL2 apduRespL2) {
            return 0;
        }

        @Override
        public int sendTransDataOutput(byte b) {
            if ((b & 0x01) != 0) {
                getTlv(0xDF8129, outcomeParamSet);//Outcome Parameter Set
            }

            if ((b & 0x04) != 0) {
                getTlv(0xDF8116, userInterReqData);//User Interface Request Data
            }

            if ((b & 0x02) != 0) {
                getTlv(0xDF8115, errIndication);//Error Indication
            }
            return RetCode.EMV_OK;
        }
    }
}

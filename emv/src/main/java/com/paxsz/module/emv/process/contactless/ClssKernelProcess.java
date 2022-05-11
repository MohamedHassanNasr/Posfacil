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
 * 20200528  	         JackHuang               Create
 * ===========================================================================================
 */
package com.paxsz.module.emv.process.contactless;

import com.pax.commonlib.utils.LogUtils;
import com.pax.commonlib.utils.convert.ConvertHelper;
import com.pax.commonlib.utils.convert.IConvert;
import com.pax.jemv.clcommon.ByteArray;
import com.pax.jemv.clcommon.Clss_PreProcInterInfo;
import com.pax.jemv.clcommon.Clss_TransParam;
import com.pax.jemv.clcommon.EMV_CAPK;
import com.pax.jemv.clcommon.EMV_REVOCLIST;
import com.pax.jemv.clcommon.RetCode;
import com.pax.jemv.clcommon.TransactionPath;
import com.paxsz.module.emv.param.EmvProcessParam;
import com.paxsz.module.emv.process.IStatusListener;
import com.paxsz.module.emv.process.entity.IssuerRspData;
import com.paxsz.module.emv.process.entity.TransResult;
import com.paxsz.module.emv.utils.EmvParamConvert;
import com.paxsz.module.emv.xmlparam.entity.common.Capk;
import com.paxsz.module.emv.xmlparam.entity.common.CapkRevoke;

import java.util.Arrays;

/**
 * this is clss kernel process framework, Please do not change this file at will, if need to
 * add a new kernel process,Please add aid in ClssEntryAddAid.java and create a ClssXXXProcess.java
 * extend ClssKernelProcessFactory.java. Then create ClssXXXProcess object in ClssKernelProcessFactory.java
 */
abstract class ClssKernelProcess {
    private static final String TAG = "ClssKernelProcess";
    protected EmvProcessParam emvProcessParam;
    protected Clss_TransParam transParam;
    protected byte[] finalSelectData;
    protected int finalSelectDataLen;
    protected Clss_PreProcInterInfo preProcInterInfo;
    protected IConvert convert = ConvertHelper.getConvert();
    protected TransactionPath transactionPath = new TransactionPath();
    protected IClssStatusListener clssStatusListener;
    protected IStatusListener statusListener;

    /**
     * 1.core init.
     * 2.different clss kernel's process is a little different
     */
    protected abstract TransResult startTransProcess();

    //if need second tap, call this
    protected abstract TransResult completeTransProcess(IssuerRspData issuerRspData);

    protected abstract int getTlv(int tag, ByteArray value);

    protected abstract int setTlv(int tag, byte[] value);

    //add capk and revovk list
    protected abstract int addCapkAndRevokeList(EMV_CAPK emvCapk, EMV_REVOCLIST emvRevoclist);

    protected abstract String getTrack2();

    protected abstract boolean isNeedSecondTap(IssuerRspData issuerRspData);

    protected ClssKernelProcess setEmvProcessParam(EmvProcessParam emvProcessParam) {
        this.emvProcessParam = emvProcessParam;
        return this;
    }

    protected ClssKernelProcess setClssTransParam(Clss_TransParam transParam) {
        this.transParam = transParam;
        return this;
    }

    protected ClssKernelProcess setFinalSelectData(byte[] finalSelectData, int finalSelectDataLen) {
        this.finalSelectData = finalSelectData;
        this.finalSelectDataLen = finalSelectDataLen;
        return this;
    }


    protected ClssKernelProcess setPreProcInterInfo(Clss_PreProcInterInfo preProcInterInfo) {
        this.preProcInterInfo = preProcInterInfo;
        return this;
    }

    protected ClssKernelProcess setClssStatusListener(IClssStatusListener clssStatusListener) {
        this.clssStatusListener = clssStatusListener;
        return this;
    }

    public ClssKernelProcess setStatusListener(IStatusListener statusListener) {
        this.statusListener = statusListener;
        return this;
    }

    protected int addCapkRevList() {
        ByteArray keyIdTLVDataList = new ByteArray(1);
        ByteArray aidTLVDataList = new ByteArray(17);
        if (getTlv(0x8F, keyIdTLVDataList) == RetCode.EMV_OK &&
                getTlv(0x4F, aidTLVDataList) == RetCode.EMV_OK) {
            byte keyId = keyIdTLVDataList.data[0];
            LogUtils.d(TAG, "addCapkRevList keyId" + keyId);
            LogUtils.d(TAG, "addCapkRevList keyId bcd" + convert.bcdToStr(new byte[]{keyId}));
            byte[] rid = new byte[5];
            System.arraycopy(aidTLVDataList.data, 0, rid, 0, 5);
            EMV_CAPK emvCapk = null;
            EMV_REVOCLIST emvRevoclist = null;
            for (Capk capk : emvProcessParam.getCapkParam().getCapkList()) {
                if (Arrays.equals(capk.getRid(), rid) && capk.getKeyId() == keyId) {
                    emvCapk = EmvParamConvert.toEMVCapk(capk);
                }
            }
            for (CapkRevoke capkRevoke : emvProcessParam.getCapkParam().getCapkRevokeList()) {
                if (new String(capkRevoke.getRid()).equals(new String(rid)) && capkRevoke.getKeyId() == keyId) {
                    emvRevoclist = new EMV_REVOCLIST(rid, keyId, capkRevoke.getCertificateSN());
                }
            }

            return addCapkAndRevokeList(emvCapk, emvRevoclist);
        }

        return RetCode.EMV_DATA_ERR;
    }

    protected static String getTrack2FromTag57(String tag57) {
        return tag57.split("F")[0];
    }

}

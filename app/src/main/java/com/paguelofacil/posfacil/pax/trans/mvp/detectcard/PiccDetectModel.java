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
 *  2020/05/19 	         Qinny Zhou           	Create/Add/Modify/Delete
 *  ===========================================================================================
 */
package com.paguelofacil.posfacil.pax.trans.mvp.detectcard;


import android.os.SystemClock;

import com.paguelofacil.posfacil.ApplicationClass;
import com.paguelofacil.posfacil.pax.entity.DetectCardResult;
import com.paguelofacil.posfacil.pax.manager.ReadTypeHelper;
import com.pax.commonlib.utils.LogUtils;
import com.pax.dal.IIcc;
import com.pax.dal.IMag;
import com.pax.dal.IPicc;
import com.pax.dal.entity.EDetectMode;
import com.pax.dal.entity.EPiccType;
import com.pax.dal.entity.EReaderType;
import com.pax.dal.entity.PiccCardInfo;
import com.pax.dal.exceptions.PiccDevException;

public class PiccDetectModel implements DetectCardContract.Model{
    private static final String TAG = "PiccDetectModel";
    private static final Byte SLOT_ICC = 0;
    private static final int timeout = 60 * 1000;

    private IPicc piccInternal;
    private IMag mag;
    private IIcc icc;
    private volatile boolean isStop = false;
    private static class LazyHolder {
        public static final PiccDetectModel INSTANCE = new PiccDetectModel();
    }

    public static PiccDetectModel getInstance() {
        return LazyHolder.INSTANCE;
    }

    private PiccDetectModel() {
        mag = ApplicationClass.getApp().getDal().getMag();
        icc = ApplicationClass.getApp().getDal().getIcc();
    }

    @Override
    public DetectCardResult polling(EReaderType readerType) {
        isStop = false;
        DetectCardResult result = new DetectCardResult();
        byte mode = readerType.getEReaderType();
        if ((mode & EReaderType.PICC.getEReaderType()) == EReaderType.PICC.getEReaderType()) {
            piccInternal = ApplicationClass.getApp().getDal().getPicc(EPiccType.INTERNAL);
            try {
                piccInternal.close();
                piccInternal.open();
            } catch (PiccDevException e) {
                result.setRetCode(DetectCardResult.ERetCode.INIT_FAILED);
                result.setReadType(EReaderType.PICC);
                LogUtils.w(TAG,e.getMessage());
                return result;
            }
        }
        long startTime = System.currentTimeMillis();

        while (!isStop) {
            if (timeout > 0) {
                long endTime = System.currentTimeMillis();
                if (endTime - startTime > timeout) {//polling timeout
                    result.setRetCode(DetectCardResult.ERetCode.TIMEOUT);
                    result.setReadType(readerType);
                    closeReader(mode);
                    return result;
                }
            }
            // MAG
            if ((mode & EReaderType.MAG.getEReaderType()) == EReaderType.MAG.getEReaderType()) {
                if (ReadTypeHelper.getInstance().getReadType() == EReaderType.MAG.getEReaderType()) {
                    result.setRetCode(DetectCardResult.ERetCode.OK);
                    result.setReadType(EReaderType.MAG);
                    closeReader((byte)((byte)0x06 & mode));//close picc and icc if support
                    return result;
                }
            }
            // ICC
            if ((mode & EReaderType.ICC.getEReaderType()) == EReaderType.ICC.getEReaderType()) {
                if (ReadTypeHelper.getInstance().getReadType() == EReaderType.ICC.getEReaderType()) {
                    result.setRetCode(DetectCardResult.ERetCode.OK);
                    result.setReadType(EReaderType.ICC);
                    closeReader((byte)((byte)0x05 & mode));//close picc and mag if support
                    return result;
                }
            }
            // PICC
            if ((mode & EReaderType.PICC.getEReaderType()) == EReaderType.PICC.getEReaderType()) {
                try {
                    PiccCardInfo info = piccInternal.detect(EDetectMode.EMV_AB);
                     /* ================NOTE==============
                         some terminals(mag reader and picc reader is very close) may detect picc when swipe card,
                         so if support mag, must sleep  several hundred ms to check if mag is swiped,
                         if during several hundred ms, detected swipe card, go to mag process, else go to clss process
                        ================================== */

                    SystemClock.sleep(600); //May increase the time of detecting card process
                    if (info != null && ReadTypeHelper.getInstance().getReadType() != EReaderType.MAG.getEReaderType()) {
                        result.setRetCode(DetectCardResult.ERetCode.OK);
                        result.setReadType(EReaderType.PICC);
                        closeReader((byte)((byte)0x03 & mode));//close icc and mag if support
                        return result;
                    }
                } catch (PiccDevException e) {
                    LogUtils.w(TAG,e.getMessage());
                    result.setRetCode(DetectCardResult.ERetCode.ERR_OTHER);
                    result.setReadType(EReaderType.PICC);
                    closeReader((byte)((byte)0x07 & mode));//close picc mag and icc if support
                    return result;
                }
            }
        }
        result.setRetCode(DetectCardResult.ERetCode.CANCEL);
        closeReader(mode);
        return result;
    }


    @Override
    public void stopPolling() {
        isStop = true;
    }

    public void closeReader(Byte flag) {
        if ((flag & 0x01) != 0) {
            try {
                LogUtils.d(TAG,"closeReader mag");
                mag.close();
            } catch (Exception e) {
                LogUtils.e(TAG, e.getMessage());
            }
        }

        if ((flag & 0x02) != 0) {
            try {
                LogUtils.d(TAG,"closeReader icc");
                icc.close(SLOT_ICC);
            } catch (Exception e) {
                LogUtils.e(TAG, e.getMessage());
            }
        }

        if ((flag & 0x04) != 0) {
            try {
                LogUtils.d(TAG,"closeReader picc");
                piccInternal.close();
            } catch (Exception e) {
                LogUtils.e(TAG, e.getMessage());
            }
        }
    }
}

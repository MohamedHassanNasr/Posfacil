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
import com.pax.dal.IMag;
import com.pax.dal.entity.EReaderType;
import com.pax.dal.entity.TrackData;
import com.pax.dal.exceptions.MagDevException;

public class MagDetectModel implements DetectCardContract.Model {
    private static final String TAG = "MagDetectModel";
    private IMag mag;
    private volatile boolean isStop = false;

    private MagDetectModel() {

    }

    public static MagDetectModel getInstance() {
        return LazyHolder.INSTANCE;
    }

    @Override
    public DetectCardResult polling(EReaderType readerType) {
        isStop = false;
        DetectCardResult result = new DetectCardResult();
        byte mode = readerType.getEReaderType();
        if ((mode & EReaderType.MAG.getEReaderType()) == EReaderType.MAG.getEReaderType()) {
            mag = ApplicationClass.getApp().getDal().getMag();
            try {
                mag.close();
                mag.open();
                mag.reset();
            } catch (MagDevException e) {
                LogUtils.w(TAG, e.getMessage());
                result.setRetCode(DetectCardResult.ERetCode.INIT_FAILED);
                result.setReadType(EReaderType.MAG);
                return result;
            }
        } else{
            LogUtils.w(TAG, "read type not contain mag");
            return result;
        }

        while (!isStop) {
            try {
                if ((mode & EReaderType.MAG.getEReaderType()) == EReaderType.MAG.getEReaderType()) {
                    if (mag.isSwiped()) {
                        TrackData info = mag.read();
                        if (info.getTrack2() == null || info.getTrack2().isEmpty()) {
                            LogUtils.i(TAG, "track2 data is null");
                            continue;
                        }
                        result.setRetCode(DetectCardResult.ERetCode.OK);
                        result.setReadType(EReaderType.MAG);
                        result.setTrack2(info.getTrack2());
                        ReadTypeHelper.getInstance().setReadType(EReaderType.MAG.getEReaderType());
                        return result;
                    }
                    SystemClock.sleep(6);

                }else{
                    LogUtils.w(TAG, "read type not contain mag");
                }
            } catch (MagDevException e) {
                result.setRetCode(DetectCardResult.ERetCode.ERR_OTHER);
                result.setReadType(readerType);
                return result;
            }


        }
        result.setRetCode(DetectCardResult.ERetCode.CANCEL);
        return result;
    }

    @Override
    public void stopPolling() {
        isStop = true;
    }

    private static class LazyHolder {
        public static final MagDetectModel INSTANCE = new MagDetectModel();
    }


}

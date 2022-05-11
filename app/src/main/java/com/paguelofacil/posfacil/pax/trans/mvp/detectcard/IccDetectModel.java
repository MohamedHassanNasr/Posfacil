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

import com.paguelofacil.posfacil.ApplicationClass;
import com.paguelofacil.posfacil.pax.entity.DetectCardResult;
import com.paguelofacil.posfacil.pax.manager.ReadTypeHelper;
import com.pax.commonlib.utils.LogUtils;
import com.pax.dal.IIcc;
import com.pax.dal.entity.EReaderType;
import com.pax.dal.exceptions.IccDevException;

public class IccDetectModel implements DetectCardContract.Model{
    private static final String TAG = "MagDetectModel";
    private static final int ICC_REVERSE = 97;
    private IIcc icc;
    private volatile boolean isStop = false;
    private static class LazyHolder {
        public static final IccDetectModel INSTANCE = new IccDetectModel();
    }

    public static IccDetectModel getInstance() {
        return LazyHolder.INSTANCE;
    }

    private IccDetectModel() {
        icc = ApplicationClass.getApp().getDal().getIcc();
    }

    @Override
    public DetectCardResult polling(EReaderType readerType) {
        isStop = false;
        DetectCardResult result = new DetectCardResult();
        byte mode = readerType.getEReaderType();
        if ((mode & EReaderType.ICC.getEReaderType()) == EReaderType.ICC.getEReaderType()) {
            try {
                icc.close((byte)0x00);
            } catch (IccDevException e) {
                result.setRetCode(DetectCardResult.ERetCode.INIT_FAILED);
                result.setReadType(EReaderType.ICC);
                return result;
            }
        } else{
            LogUtils.w(TAG, "read type not contain icc");
            return result;
        }

        while (!isStop) {
            try {
                if ((mode & EReaderType.ICC.getEReaderType()) == EReaderType.ICC.getEReaderType()) {
                    if (icc.detect((byte)0x00)) {
                        byte[] res =  icc.init((byte)0x00);//reset ic card, and return reset response content
                        if (res == null ) {
                            LogUtils.e(TAG, "Please remove card to swipe or reinsert IC card");
                            continue;
                        }else{
                            result.setRetCode(DetectCardResult.ERetCode.OK);
                            result.setReadType(EReaderType.ICC);
                            ReadTypeHelper.getInstance().setReadType(EReaderType.ICC.getEReaderType());
                            return result;
                        }
                    }
                }
            } catch (IccDevException e) {
                if (e.getErrCode() == ICC_REVERSE) {
                    result.setRetCode(DetectCardResult.ERetCode.OK);
                    result.setReadType(EReaderType.ICC);
                    ReadTypeHelper.getInstance().setReadType(EReaderType.ICC.getEReaderType());
                    isStop = true;
                    break;
                }
            }

        }
        result.setRetCode(DetectCardResult.ERetCode.CANCEL);
        return result;
    }


    @Override
    public void stopPolling() {
        isStop = true;
    }





}

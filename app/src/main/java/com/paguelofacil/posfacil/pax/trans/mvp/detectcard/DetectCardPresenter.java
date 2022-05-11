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
import com.paguelofacil.posfacil.pax.trans.mvp.BasePresenter;
import com.paguelofacil.posfacil.pax.util.CardInfoUtils;
import com.pax.commonlib.utils.LogUtils;
import com.pax.dal.entity.EReaderType;

public class DetectCardPresenter extends BasePresenter<DetectCardContract.View> implements DetectCardContract.Presenter {
    private static final String TAG = "DetectCardPresenter";
    private PiccDetectModel piccDetectModel;
    private MagDetectModel magDetectModel;
    private IccDetectModel iccDetectModel;
    private DetectCardResult detectResult;
    private DetectCardResult magInfoResult;

    private EReaderType currentReaderType;
    private EReaderType readType;

    public DetectCardPresenter() {
        piccDetectModel = PiccDetectModel.getInstance();
        magDetectModel = MagDetectModel.getInstance();
        iccDetectModel = IccDetectModel.getInstance();
        detectResult = new DetectCardResult();

    }

    @Override
    public void startDetectCard(EReaderType readType) {
        if (!isViewAttached()) {
            return;
        }
        currentReaderType = readType;
        ReadTypeHelper.getInstance().setReadType(EReaderType.DEFAULT.getEReaderType());

        ApplicationClass.getApp().runInBackground(new Runnable() {
            @Override
            public void run() {
                LogUtils.d(TAG, "start picc polling");
                detectResult = piccDetectModel.polling(readType);
                stopDetectCard();
                LogUtils.i(TAG, "detect finished,code : " + detectResult.getRetCode() + ",type:" + detectResult.getReadType());
                ApplicationClass.getApp().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (detectResult.getRetCode() == DetectCardResult.ERetCode.OK) {
                            detectSuccProcess();
                        } else {
                            detectFailedProcess();
                        }
                    }
                });
            }
        });

        ApplicationClass.getApp().runInBackground(new Runnable() {
            @Override
            public void run() {
                LogUtils.d(TAG, "start mag polling");
                magInfoResult = magDetectModel.polling(readType);
            }
        });

        ApplicationClass.getApp().runInBackground(new Runnable() {
            @Override
            public void run() {
                LogUtils.d(TAG, "start icc polling");
                iccDetectModel.polling(readType);
            }
        });
    }

    @Override
    public void stopDetectCard() {
        if (piccDetectModel != null) {
            piccDetectModel.stopPolling();
        }
        if (iccDetectModel != null) {
            iccDetectModel.stopPolling();
        }
        if (magDetectModel != null) {
            magDetectModel.stopPolling();
        }
    }

    public void closeReader() {
        if (piccDetectModel != null && readType != null){
            piccDetectModel.closeReader((byte) (readType.getEReaderType() & (byte) 0x07));
        }
    }

    private void detectSuccProcess() {
        if (!isViewAttached()) {
            return;
        }
        readType = detectResult.getReadType();
        if (readType == EReaderType.PICC) {
            mView.onPiccDetectOK();
        } else if (readType == EReaderType.ICC) {
            mView.onIccDetectOK();
        } else if (readType == EReaderType.MAG) {
            String track2 = magInfoResult.getTrack2();
            LogUtils.d(TAG, "detectSuccProcess, track: " + track2);
            if ((currentReaderType.getEReaderType() & EReaderType.ICC.getEReaderType()) == EReaderType.ICC.getEReaderType() && CardInfoUtils.isIcCard(track2)) {
                mView.onDetectError(DetectCardResult.ERetCode.FALLBACK);
                byte currentMode = currentReaderType.getEReaderType();
                currentMode &= ~EReaderType.MAG.getEReaderType();
                currentReaderType = toReaderType(currentMode);
                startDetectCard(currentReaderType);
                return;
            }

            String pan = CardInfoUtils.getPan(track2);
            String expiryDate = CardInfoUtils.getExpDate(track2);
            mView.onMagDetectOK(pan, expiryDate);
        }


    }

    private EReaderType toReaderType(Byte mode) {
        EReaderType[] types = EReaderType.values();
        for (EReaderType type : types) {
            if (type.getEReaderType() == mode) {
                return type;
            }
        }
        return null;
    }

    private void detectFailedProcess() {
        if (!isViewAttached()) {
            return;
        }
        mView.onDetectError(detectResult.getRetCode());
    }
}

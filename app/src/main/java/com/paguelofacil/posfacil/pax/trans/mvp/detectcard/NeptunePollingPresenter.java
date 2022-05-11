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
 *  2020/06/20 	         Qinny Zhou           	      Create
 *  ===========================================================================================
 */
package com.paguelofacil.posfacil.pax.trans.mvp.detectcard;


import com.paguelofacil.posfacil.ApplicationClass;
import com.paguelofacil.posfacil.pax.entity.DetectCardResult;
import com.paguelofacil.posfacil.pax.trans.mvp.BasePresenter;
import com.paguelofacil.posfacil.pax.util.CardInfoUtils;
import com.pax.commonlib.utils.LogUtils;
import com.pax.dal.ICardReaderHelper;
import com.pax.dal.entity.EPiccType;
import com.pax.dal.entity.EReaderType;
import com.pax.dal.entity.PollingResult;
import com.pax.dal.exceptions.IccDevException;
import com.pax.dal.exceptions.MagDevException;
import com.pax.dal.exceptions.PiccDevException;

public class NeptunePollingPresenter extends BasePresenter<DetectCardContract.View> implements DetectCardContract.Presenter {
    private static final String TAG = "NeptunePolling";
    private DetectCardResult detectResult;

    private EReaderType currentReaderType;

    private ICardReaderHelper cardReaderHelper;

    public NeptunePollingPresenter() {
        detectResult = new DetectCardResult();
        cardReaderHelper = ApplicationClass.getApp().getDal().getCardReaderHelper();

    }

    @Override
    public void startDetectCard(EReaderType readType) {
        if (!isViewAttached()) {
            return;
        }
        currentReaderType = readType;
        ApplicationClass.getApp().runInBackground(new Runnable() {
            @Override
            public void run() {
                LogUtils.d(TAG, "start polling");
                try {
                    PollingResult pollingResult = cardReaderHelper.polling(readType, 60 * 1000);
                    //need to close slot(if detect picc, icc should call icc.close(0x01), if detect icc, picc should call picc.close())
//                    PollingResult pollingResult = cardReaderHelper.polling(readType, 60 * 1000, true);//auto close reader
                    if (pollingResult.getOperationType() == PollingResult.EOperationType.OK) {
                        detectResult.setRetCode(DetectCardResult.ERetCode.OK);
                        detectResult.setReadType(pollingResult.getReaderType());
                        detectResult.setTrack2(pollingResult.getTrack2());
                    } else if (pollingResult.getOperationType() == PollingResult.EOperationType.TIMEOUT) {
                        detectResult.setRetCode(DetectCardResult.ERetCode.TIMEOUT);
                    } else if (pollingResult.getOperationType() == PollingResult.EOperationType.CANCEL) {
                        detectResult.setRetCode(DetectCardResult.ERetCode.CANCEL);
                    }
                    LogUtils.i(TAG, "polling end,code : " + detectResult.getRetCode() + ",type:" + detectResult.getReadType());
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

                } catch (MagDevException | IccDevException | PiccDevException e) {
                    LogUtils.w(TAG, "polling error:" + e);
                    detectResult.setRetCode(DetectCardResult.ERetCode.ERR_OTHER);
                    detectFailedProcess();
                } finally {
                    stopDetectCard();
                    byte closeByte = 0x07;
                    if (detectResult.getReadType() == EReaderType.PICC) {
                        closeByte = (byte) (0x03 & currentReaderType.getEReaderType());//close mag and icc
                    } else if (detectResult.getReadType() == EReaderType.ICC) {
                        closeByte = (byte) (0x05 & currentReaderType.getEReaderType());//close picc and mag
                    } else if (detectResult.getReadType() == EReaderType.MAG) {
                        closeByte = (byte) (0x06 & currentReaderType.getEReaderType());//close icc and picc
                    }
                    close(closeByte);

                }


            }
        });
    }

    @Override
    public void stopDetectCard() {
        if (cardReaderHelper != null) {
            cardReaderHelper.stopPolling();
        }

    }

    private void detectSuccProcess() {
        if (!isViewAttached()) {
            return;
        }
        EReaderType readTypeRet = detectResult.getReadType();
        if (readTypeRet == EReaderType.PICC) {
            mView.onPiccDetectOK();
        } else if (readTypeRet == EReaderType.ICC) {
            mView.onIccDetectOK();
        } else if (readTypeRet == EReaderType.MAG) {
            String track2 = detectResult.getTrack2();
            LogUtils.d(TAG, "detectSuccProcess, track: " + track2);
            if ((currentReaderType.getEReaderType() & EReaderType.ICC.getEReaderType()) == EReaderType.ICC.getEReaderType() && CardInfoUtils.isIcCard(track2)) {
                mView.onDetectError(DetectCardResult.ERetCode.FALLBACK);
                byte currentMode = currentReaderType.getEReaderType();
                currentMode &= ~EReaderType.MAG.getEReaderType();
                currentReaderType = toReaderType(currentMode);
                close((byte) 0x02);//close icc
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

    private void close(byte flag) {
        if ((flag & 0x01) != 0) {
            try {
                LogUtils.d(TAG, "closeReader mag");
                ApplicationClass.getApp().getDal().getMag().close();
            } catch (MagDevException e) {
                LogUtils.e(TAG, e.getMessage());
            }
        }

        if ((flag & 0x02) != 0) {
            try {
                LogUtils.d(TAG, "closeReader icc");
                ApplicationClass.getApp().getDal().getIcc().close((byte) 0x00);
            } catch (IccDevException e) {
                LogUtils.e(TAG, e.getMessage());
            }
        }

        if ((flag & 0x04) != 0) {
            try {
                LogUtils.d(TAG, "closeReader picc");
                ApplicationClass.getApp().getDal().getPicc(EPiccType.INTERNAL).close();
            } catch (PiccDevException e) {
                LogUtils.e(TAG, e.getMessage());
            }
        }

    }



    public void closeReader() {
        close((byte) (currentReaderType.getEReaderType() & (byte) 0x07));
    }

    private void detectFailedProcess() {
        if (!isViewAttached()) {
            return;
        }
        mView.onDetectError(detectResult.getRetCode());
    }
}

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
 *  2020/06/01 	         Qinny Zhou           	      Create
 *  ===========================================================================================
 */

package com.paguelofacil.posfacil.pax.trans.mvp.cardprocess;

import androidx.appcompat.widget.AppCompatSpinner;

import com.paguelofacil.posfacil.ApplicationClass;
import com.paguelofacil.posfacil.pax.entity.EnterPinResult;
import com.paguelofacil.posfacil.pax.manager.EEnterPinType;
import com.paguelofacil.posfacil.pax.util.CardInfoUtils;
import com.paguelofacil.posfacil.pax.util.PedApiUtils;
import com.pax.commonlib.utils.LogUtils;
import com.pax.dal.IPed;
import com.pax.dal.entity.EKeyCode;
import com.pax.dal.entity.EPedType;
import com.pax.dal.exceptions.EPedDevException;
import com.pax.dal.exceptions.PedDevException;
import com.pax.jemv.clcommon.RetCode;

public class EnterPinTask {
    private static final String TAG = "EnterPinTask";
    private IPed ped;
    private EEnterPinType enterPinType;
    private String onlinePan = "";
    private IEnterPinListener listener;

    public EnterPinTask() {
        ped = ApplicationClass.getApp().getDal().getPed(EPedType.INTERNAL);
    }

    public EnterPinTask registerListener(IEnterPinListener listener) {
        this.listener = listener;
        return this;
    }

    public EnterPinTask setEnterPinType(EEnterPinType enterPinType) {
        this.enterPinType = enterPinType;
        return this;
    }

    public EnterPinTask setOnlinePan(String pan) {
        this.onlinePan = pan;
        return this;
    }

    public void unregisterListener() {
        this.listener = null;
    }

    public void startEnterPin() {
        if (this.listener == null) {
            return;
        }

        LogUtils.i(TAG, "onlinePan:" + onlinePan + ",enterPinType:" + enterPinType);
        ApplicationClass.getApp().runInBackground(new Runnable() {
            @Override
            public void run() {
                if (enterPinType == EEnterPinType.ONLINE_PIN) {
                    String pan = CardInfoUtils.getPanBlock(onlinePan, CardInfoUtils.X9_8_WITH_PAN);
                    enterOnlinePin(pan);
                } else if (enterPinType == EEnterPinType.OFFLINE_PCI_MODE) {
                    enterOfflinePin();
                }

            }
        });
    }

    private void enterOfflinePin() {
        try {
            ped.setIntervalTime(1, 1);
            ped.setInputPinListener(new IPed.IPedInputPinListener() {
                @Override
                public void onKeyEvent(EKeyCode eKeyCode) {
                    String temp = listener.getEnteredPin();
                    if (eKeyCode == EKeyCode.KEY_CLEAR) {
                        temp = "";
                    } else if (eKeyCode == EKeyCode.KEY_CANCEL) {
                        ped.setInputPinListener(null);
                        listener.onEnterPinFinish(new EnterPinResult(EnterPinResult.RET_CANCEL));
                        return;
                    } else if (eKeyCode == EKeyCode.KEY_ENTER) {
                        if (temp.length() > 3 || temp.length() == 0) {
                            ped.setInputPinListener(null);
                            listener.onEnterPinFinish(new EnterPinResult(EnterPinResult.RET_SUCC));
                            return;
                        }
                    } else {
                        temp += "*";
                    }
                    listener.onUpdatePinLen(temp);
                }
            });
            listener.onEnterPinFinish(new EnterPinResult(EnterPinResult.RET_OFFLINE_PIN_READY));
        } catch (PedDevException e) {
            LogUtils.i(TAG, "enterOfflinePin:" + e);
            if (e.getErrCode() == EPedDevException.PED_ERR_INPUT_TIMEOUT.getErrCodeFromBasement()) {
                listener.onEnterPinFinish(new EnterPinResult(EnterPinResult.RET_TIMEOUT));
            } else {
                listener.onEnterPinFinish(new EnterPinResult(e.getErrCode()));
            }

        }
    }

    private void enterOnlinePin(String panBlock) {
        EnterPinResult pinResult = new EnterPinResult();
        try {
            ped.setIntervalTime(1, 1);
            ped.setInputPinListener(new IPed.IPedInputPinListener() {
                @Override
                public void onKeyEvent(EKeyCode eKeyCode) {
                    String temp;
                    if (eKeyCode == EKeyCode.KEY_CLEAR) {
                        temp = "";
                    } else if (eKeyCode == EKeyCode.KEY_ENTER || eKeyCode == EKeyCode.KEY_CANCEL) {
                        // do nothing
                        return;
                    } else {
                        temp = listener.getEnteredPin();
                        temp += "*";
                    }
                    listener.onUpdatePinLen(temp);
                }
            });
            byte[] pinBlock = PedApiUtils.getPinBlock(panBlock, 60 * 1000);
            if (pinBlock == null || pinBlock.length == 0) {
                pinResult.setRet(RetCode.EMV_NO_PASSWORD);
            } else {
                pinResult.setRet(EnterPinResult.RET_SUCC);
            }


        } catch (PedDevException e) {
            LogUtils.w(TAG, "EnterPinTask:" + e.getErrCode());
            if (e.getErrCode() == EPedDevException.PED_ERR_INPUT_CANCEL.getErrCodeFromBasement()) {
                pinResult.setRet(EnterPinResult.RET_CANCEL);
            } else if (e.getErrCode() == EPedDevException.PED_ERR_INPUT_TIMEOUT.getErrCodeFromBasement()) {
                pinResult.setRet(EnterPinResult.RET_TIMEOUT);
            } else if (e.getErrCode() == EPedDevException.PED_ERR_NO_KEY.getErrCodeFromBasement()) {
                pinResult.setRet(EnterPinResult.RET_NO_KEY);
            } else {
                pinResult.setRet(RetCode.EMV_RSP_ERR);
            }
        } finally {
            ped.setInputPinListener(null);
        }
        if (listener != null) {
            listener.onEnterPinFinish(pinResult);
        }
    }


    interface IEnterPinListener {
        void onUpdatePinLen(String pin);

        String getEnteredPin();

        void onEnterPinFinish(EnterPinResult enterPinResult);
    }
}

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

package com.paguelofacil.posfacil.pax.entity;

import com.pax.jemv.clcommon.RetCode;

public class EnterPinResult {

    public static final int RET_SUCC = 0;
    public static final int RET_CANCEL = RetCode.EMV_USER_CANCEL;
    public static final int RET_TIMEOUT = RetCode.EMV_TIME_OUT;
    public static final int RET_PIN_BY_PASS = RetCode.EMV_NO_PASSWORD;
    public static final int RET_NO_KEY = RetCode.EMV_RSP_ERR;
    public static final int RET_OFFLINE_PIN_READY = 2000;
    private int ret;

    public EnterPinResult() {
    }

    public EnterPinResult(int ret) {
        this.ret = ret;
    }

    public int getRet() {
        return ret;
    }

    public void setRet(int ret) {
        this.ret = ret;
    }
}

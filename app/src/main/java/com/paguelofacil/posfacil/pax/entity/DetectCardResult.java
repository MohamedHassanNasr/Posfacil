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

import com.pax.dal.entity.EReaderType;

public class DetectCardResult {

    private EReaderType mReadType;

    private ERetCode mRetCode;

    private String track2;

    public EReaderType getReadType() {
        return mReadType;
    }

    public ERetCode getRetCode() {
        return mRetCode;
    }

    public void setRetCode(ERetCode retCode) {
        mRetCode = retCode;
    }

    public String getTrack2() {
        return track2;
    }

    public void setTrack2(String track2) {
        this.track2 = track2;
    }

    public void setReadType(EReaderType readType) {
        mReadType = readType;
    }



    public static enum ERetCode {
        OK,
        TIMEOUT,
        CANCEL,
        INIT_FAILED,
        FALLBACK,
        ERR_OTHER;


        private ERetCode() {
        }
    }
}

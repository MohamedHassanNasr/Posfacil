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
 * 20200526  	         JackHuang               Create
 * ===========================================================================================
 */
package com.paxsz.module.emv.process.entity;

import com.paxsz.module.emv.process.enums.TransResultEnum;
import com.paxsz.module.emv.process.enums.CvmResultEnum;

public class TransResult {

    //emv l2 lib api return code, refer to emv lib jar.
    private int resultCode;

    private TransResultEnum transResult;

    private CvmResultEnum cvmResult;

    public TransResult() {

    }
    public TransResult(int resultCode) {
        this.resultCode = resultCode;
    }

    public TransResult(int resultCode, TransResultEnum transResult) {
        this.resultCode = resultCode;
        this.transResult = transResult;
    }

    public TransResult(int resultCode, TransResultEnum transResult, CvmResultEnum cvmType) {
        this.resultCode = resultCode;
        this.transResult = transResult;
        this.cvmResult = cvmType;
    }

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public TransResultEnum getTransResult() {
        return transResult;
    }

    public void setTransResult(TransResultEnum transResult) {
        this.transResult = transResult;
    }

    public CvmResultEnum getCvmResult() {
        return cvmResult;
    }

    public void setCvmResult(CvmResultEnum cvmResult) {
        this.cvmResult = cvmResult;
    }
}

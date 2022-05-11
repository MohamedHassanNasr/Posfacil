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
 * 20200519  	         JackHuang               Create
 * ===========================================================================================
 */
package com.paxsz.module.emv.xmlparam.parser;

public class ParseException extends Exception {
    public static final int CODE_COMMON_ERROR         = -1;
    public static final int CODE_FILE_OPEN_ERR        = -2;
    public static final int CODE_NODE_NOT_FOUND       = -3;
    public static final int CODE_RAW_NAME_EMPTY       = -4;

    private int code;
    private String message;

    public ParseException(Throwable cause) {
        super(cause);
    }

    public ParseException(int code, String message, Throwable cause) {
        super(cause);
        this.code = code;
        this.message = message;
    }

    public ParseException(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}


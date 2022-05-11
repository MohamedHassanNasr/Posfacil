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
 *  2020/06/03 	         Qinny Zhou           	      Create
 *  ===========================================================================================
 */
package com.paxsz.module.emv.process.entity;

public class IssuerRspData {
    private byte onlineResult;

    private byte[] authData;// 91

    private byte[] script; // 7172

    private byte[] respCode;  //8a

    private byte[] authCode; // 89

    public IssuerRspData() {
        onlineResult = 0;
        authData = new byte[0];
        script = new byte[0];
        respCode = new byte[0];
        authCode = new byte[0];
    }

    public byte getOnlineResult() {
        return onlineResult;
    }

    public void setOnlineResult(byte onlineResult) {
        this.onlineResult = onlineResult;
    }

    public byte[] getAuthData() {
        return this.authData;
    }

    public void setAuthData(byte[] authData) {
        this.authData = authData;
    }

    public byte[] getScript() {
        return this.script;
    }

    public void setScript(byte[] script) {
        this.script = script;
    }

    public byte[] getRespCode() {
        return this.respCode;
    }

    public void setRespCode(byte[] respCode) {
        this.respCode = respCode;
    }

    public byte[] getAuthCode() {
        return this.authCode;
    }

    public void setAuthCode(byte[] authCode) {
        this.authCode = authCode;
    }
}

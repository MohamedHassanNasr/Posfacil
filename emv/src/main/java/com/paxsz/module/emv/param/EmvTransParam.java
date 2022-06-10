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
 * 20200525  	         JackHuang               Create
 * ===========================================================================================
 */

package com.paxsz.module.emv.param;

public class EmvTransParam {
    //tag 9F02
    private String amount;
    //tag 9F03,such as cashback
    private String amountOther;
    //tag 9C
    private byte transType;
    //tag 5F2A, Transaction Currency Code
    private byte[] transCurrencyCode;
    //tag 5F36, Transaction Currency Exponent
    private byte transCurrencyExponent;
    //tag 9A
    private String transDate;
    //9F21
    private String transTime;
    private String transTraceNo;

    private String terminalID;

    public EmvTransParam() {
        this.transType = 0;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getAmountOther() {
        return amountOther;
    }

    public void setAmountOther(String amountOther) {
        this.amountOther = amountOther;
    }

    public byte getTransType() {
        return transType;
    }

    public void setTransType(byte transType) {
        this.transType = transType;
    }

    public String getTransDate() {
        return transDate;
    }

    public void setTransDate(String transDate) {
        this.transDate = transDate;
    }

    public String getTransTime() {
        return transTime;
    }

    public void setTransTime(String transTime) {
        this.transTime = transTime;
    }

    public String getTransTraceNo() {
        return transTraceNo;
    }

    public void setTransTraceNo(String transTraceNo) {
        this.transTraceNo = transTraceNo;
    }

    public byte[] getTransCurrencyCode() {
        return transCurrencyCode;
    }

    public void setTransCurrencyCode(byte[] transCurrencyCode) {
        this.transCurrencyCode = transCurrencyCode;
    }

    public byte getTransCurrencyExponent() {
        return transCurrencyExponent;
    }

    public void setTransCurrencyExponent(byte transCurrencyExponent) {
        this.transCurrencyExponent = transCurrencyExponent;
    }

    public String getTerminalID() {
        return terminalID;
    }

    public void setTerminalID(String terminalId) {
        this.terminalID = terminalId;
    }
}

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
 * 20200518  	         JackHuang               Create
 * ===========================================================================================
 */
package com.paxsz.module.emv.xmlparam.entity.common;

public class Capk {
    //Registered Application Provider Identifier
    private byte[] rid;

    //Key Index
    private byte keyId;

    //Hash arithmetic indicator
    private byte hashArithmeticIndex;

    //RSA arithmetic indicator
    private byte rsaArithmeticIndex;

    //Length of Module
    private int moduleLength;

    //Module
    private byte[] module;

    //Length of exponent
    private byte exponentLength;

    //Exponent
    private byte[] exponent;

    //Expiration Date (YYMMDD)
    private byte[] expireDate;

    //Check Sum of Key
    private byte[] checkSum;

    public Capk() {
        rid = new byte[0];
        module = new byte[0];
        exponent = new byte[0];
        expireDate = new byte[0];
        checkSum = new byte[0];
    }

    public byte[] getRid() {
        return rid;
    }

    public void setRid(byte[] rid) {
        this.rid = rid;
    }

    public byte getKeyId() {
        return keyId;
    }

    public void setKeyId(byte keyId) {
        this.keyId = keyId;
    }

    public byte getHashArithmeticIndex() {
        return hashArithmeticIndex;
    }

    public void setHashArithmeticIndex(byte hashArithmeticIndex) {
        this.hashArithmeticIndex = hashArithmeticIndex;
    }

    public byte getRsaArithmeticIndex() {
        return rsaArithmeticIndex;
    }

    public void setRsaArithmeticIndex(byte rsaArithmeticIndex) {
        this.rsaArithmeticIndex = rsaArithmeticIndex;
    }

    public int getModuleLength() {
        return moduleLength;
    }

    public void setModuleLength(int moduleLength) {
        this.moduleLength = moduleLength;
    }

    public byte[] getModule() {
        return module;
    }

    public void setModule(byte[] module) {
        this.module = module;
    }

    public byte getExponentLength() {
        return exponentLength;
    }

    public void setExponentLength(byte exponentLength) {
        this.exponentLength = exponentLength;
    }

    public byte[] getExponent() {
        return exponent;
    }

    public void setExponent(byte[] exponent) {
        this.exponent = exponent;
    }

    public byte[] getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(byte[] expireDate) {
        this.expireDate = expireDate;
    }

    public byte[] getCheckSum() {
        return checkSum;
    }

    public void setCheckSum(byte[] checkSum) {
        this.checkSum = checkSum;
    }
}

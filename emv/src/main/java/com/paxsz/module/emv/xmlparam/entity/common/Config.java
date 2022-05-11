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

public class Config {
    //tag 9F16, Merchant Identifier
    private String merchantId;

    //tag 9F4E, Merchant Name and Location
    private String merchantNameAndLocation;

    //tag 9F15, Merchant Category Code
    private byte[] merchantCategoryCode;

    //tag 9F1A, Terminal Country Code
    private byte[] terminalCountryCode;

    //The Symbol show before amount, such  as: $1234
    private String terminalCurrencySymbol;

    //tag 9F3C, Transaction Reference Currency Code
    private byte[] transReferenceCurrencyCode;

    //tag 9F3D, Transaction Reference Currency Exponent
    private byte transReferenceCurrencyExponent;

    //Transaction Reference Currency Conversion
    private long conversionRatio;

    public Config() {
        merchantId = "";
        merchantNameAndLocation = "";
        merchantCategoryCode = new byte[0];
        terminalCountryCode = new byte[0];
        terminalCurrencySymbol = "";
        transReferenceCurrencyCode = new byte[0];
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getMerchantNameAndLocation() {
        return merchantNameAndLocation;
    }

    public void setMerchantNameAndLocation(String merchantNameAndLocation) {
        this.merchantNameAndLocation = merchantNameAndLocation;
    }

    public byte[] getMerchantCategoryCode() {
        return merchantCategoryCode;
    }

    public void setMerchantCategoryCode(byte[] merchantCategoryCode) {
        this.merchantCategoryCode = merchantCategoryCode;
    }

    public byte[] getTerminalCountryCode() {
        return terminalCountryCode;
    }

    public void setTerminalCountryCode(byte[] terminalCountryCode) {
        this.terminalCountryCode = terminalCountryCode;
    }

    public String getTerminalCurrencySymbol() {
        return terminalCurrencySymbol;
    }

    public void setTerminalCurrencySymbol(String terminalCurrencySymbol) {
        this.terminalCurrencySymbol = terminalCurrencySymbol;
    }

    public byte[] getTransReferenceCurrencyCode() {
        return transReferenceCurrencyCode;
    }

    public void setTransReferenceCurrencyCode(byte[] transReferenceCurrencyCode) {
        this.transReferenceCurrencyCode = transReferenceCurrencyCode;
    }

    public byte getTransReferenceCurrencyExponent() {
        return transReferenceCurrencyExponent;
    }

    public void setTransReferenceCurrencyExponent(byte transReferenceCurrencyExponent) {
        this.transReferenceCurrencyExponent = transReferenceCurrencyExponent;
    }

    public long getConversionRatio() {
        return conversionRatio;
    }

    public void setConversionRatio(long conversionRatio) {
        this.conversionRatio = conversionRatio;
    }
}

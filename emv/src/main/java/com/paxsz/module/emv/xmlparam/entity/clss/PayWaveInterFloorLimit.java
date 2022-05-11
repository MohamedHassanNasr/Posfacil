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
package com.paxsz.module.emv.xmlparam.entity.clss;

/**
 * this is design for different transType, base on transType, can change contactlessFloorLimit,
 * contactlessTransactionLimit and contactlessCvmLimit
 */
public class PayWaveInterFloorLimit {
    private byte transactionType;
    private long contactlessFloorLimit;
    private long contactlessTransactionLimit;
    private long contactlessCvmLimit;
    private byte contactlessTransactionLimitSupported;
    private byte cvmLimitSupported;
    private byte contactlessFloorLimitSupported;

    public byte getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(byte transactionType) {
        this.transactionType = transactionType;
    }

    public long getContactlessFloorLimit() {
        return contactlessFloorLimit;
    }

    public void setContactlessFloorLimit(long contactlessFloorLimit) {
        this.contactlessFloorLimit = contactlessFloorLimit;
    }

    public long getContactlessTransactionLimit() {
        return contactlessTransactionLimit;
    }

    public void setContactlessTransactionLimit(long contactlessTransactionLimit) {
        this.contactlessTransactionLimit = contactlessTransactionLimit;
    }

    public long getContactlessCvmLimit() {
        return contactlessCvmLimit;
    }

    public void setContactlessCvmLimit(long contactlessCvmLimit) {
        this.contactlessCvmLimit = contactlessCvmLimit;
    }

    public byte getContactlessTransactionLimitSupported() {
        return contactlessTransactionLimitSupported;
    }

    public void setContactlessTransactionLimitSupported(byte contactlessTransactionLimitSupported) {
        this.contactlessTransactionLimitSupported = contactlessTransactionLimitSupported;
    }

    public byte getCvmLimitSupported() {
        return cvmLimitSupported;
    }

    public void setCvmLimitSupported(byte cvmLimitSupported) {
        this.cvmLimitSupported = cvmLimitSupported;
    }

    public byte getContactlessFloorLimitSupported() {
        return contactlessFloorLimitSupported;
    }

    public void setContactlessFloorLimitSupported(byte contactlessFloorLimitSupported) {
        this.contactlessFloorLimitSupported = contactlessFloorLimitSupported;
    }
}

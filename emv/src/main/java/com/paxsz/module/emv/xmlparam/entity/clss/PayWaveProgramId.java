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

public class PayWaveProgramId {

    //tag 9F5A(get from card), Application Program Identifier (Program ID)
    private byte[] programId;

    //Reader CVM Required Limit
    private long contactlessCvmLimit;

    //Reader contactless transaction limit
    private long contactlessTransactionLimit;

    //Reader contactless Offline limit
    private long contactlessFloorLimit;

    //Reader contactless transaction limit check flag, 0- Deactivated, 1- Active and exist, 2- Active but not exist
    private byte contactlessTransactionLimitSupported;

    //Card reader CVM limit check flag, 0- Deactivated, 1- Active and exist, 2- Active but not exist
    private byte cvmLimitSupported;

    //Card reader contactless Offline limit check flag, 0- Deactivated, 1- Active and exist, 2- Active but not exist
    private byte contactlessFloorLimitSupported;

    //MSD CVN17 support flag, 0- not support, 1- support
    private byte cryptogramVersion17Supported;

    //Amount, Authorized of Zero Check flag, 0- Flag activated, online required, 1- Flag activated, amount zero not allowed, 2-Flag deactivated
    private byte zeroAmountNoAllowed;

    //Status check support flag, 0- not support, 1- support
    private byte statusCheckSupported;

    //tag 9F66, Terminal Transaction Qualifiers (TTQ)
    private byte[] readerTtq;

    public PayWaveProgramId() {
        programId = new byte[0];
        readerTtq = new byte[0];
    }

    public byte[] getProgramId() {
        return programId;
    }

    public void setProgramId(byte[] programId) {
        this.programId = programId;
    }

    public long getContactlessCvmLimit() {
        return contactlessCvmLimit;
    }

    public void setContactlessCvmLimit(long contactlessCvmLimit) {
        this.contactlessCvmLimit = contactlessCvmLimit;
    }

    public long getContactlessTransactionLimit() {
        return contactlessTransactionLimit;
    }

    public void setContactlessTransactionLimit(long contactlessTransactionLimit) {
        this.contactlessTransactionLimit = contactlessTransactionLimit;
    }

    public long getContactlessFloorLimit() {
        return contactlessFloorLimit;
    }

    public void setContactlessFloorLimit(long contactlessFloorLimit) {
        this.contactlessFloorLimit = contactlessFloorLimit;
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

    public byte getCryptogramVersion17Supported() {
        return cryptogramVersion17Supported;
    }

    public void setCryptogramVersion17Supported(byte cryptogramVersion17Supported) {
        this.cryptogramVersion17Supported = cryptogramVersion17Supported;
    }

    public byte getZeroAmountNoAllowed() {
        return zeroAmountNoAllowed;
    }

    public void setZeroAmountNoAllowed(byte zeroAmountNoAllowed) {
        this.zeroAmountNoAllowed = zeroAmountNoAllowed;
    }

    public byte getStatusCheckSupported() {
        return statusCheckSupported;
    }

    public void setStatusCheckSupported(byte statusCheckSupported) {
        this.statusCheckSupported = statusCheckSupported;
    }

    public byte[] getReaderTtq() {
        return readerTtq;
    }

    public void setReaderTtq(byte[] readerTtq) {
        this.readerTtq = readerTtq;
    }
}

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
package com.paxsz.module.emv.xmlparam.entity.clss;

import java.util.ArrayList;

public class PayWaveAid {
    private String localAidName;

    //tag 9F06, Application Identifier (AID)
    private byte[] applicationId;

    //0:support. 1:nonsupport, full match
    private byte partialAidSelection;

    //MSD CVN17 support flag, 0- not support, 1- support
    private byte cryptogramVersion17Supported;

    //Amount, Authorized of Zero Check flag, 0- Flag activated, online required, 1- Flag activated, amount zero not allowed, 2-Flag deactivated
    private byte zeroAmountNoAllowed;

    //Status check support flag, 0- not support, 1- support
    private byte statusCheckSupported;

    //tag 9F66, Terminal Transaction Qualifiers (TTQ)
    private byte[] readerTtq;

    //tag 9F33 byte 3, Security Capability
    private byte securityCapability;

    //Terminal Type
    private byte termType;

    //floolimt by transation type
    private ArrayList<PayWaveInterFloorLimit> PayWaveInterFloorLimitList;

    public PayWaveAid() {
        localAidName = "";
        applicationId = new byte[0];
        readerTtq = new byte[0];
    }

    public String getLocalAidName() {
        return localAidName;
    }

    public void setLocalAidName(String localAidName) {
        this.localAidName = localAidName;
    }

    public byte[] getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(byte[] applicationId) {
        this.applicationId = applicationId;
    }

    public byte getPartialAidSelection() {
        return partialAidSelection;
    }

    public void setPartialAidSelection(byte partialAidSelection) {
        this.partialAidSelection = partialAidSelection;
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

    public byte getSecurityCapability() {
        return securityCapability;
    }

    public void setSecurityCapability(byte securityCapability) {
        this.securityCapability = securityCapability;
    }

    public void setReaderTtq(byte[] readerTtq) {
        this.readerTtq = readerTtq;
    }

    public byte getTermType() {
        return termType;
    }

    public void setTermType(byte termType) {
        this.termType = termType;
    }

    public ArrayList<PayWaveInterFloorLimit> getPayWaveInterFloorLimitList() {
        return PayWaveInterFloorLimitList;
    }

    public void setPayWaveInterFloorLimitList(ArrayList<PayWaveInterFloorLimit> payWaveInterFloorLimitList) {
        PayWaveInterFloorLimitList = payWaveInterFloorLimitList;
    }
}

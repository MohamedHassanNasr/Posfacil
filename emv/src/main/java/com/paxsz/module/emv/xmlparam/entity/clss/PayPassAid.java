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

public class PayPassAid {
    private String localAidName;
    private byte[] applicationId;

    //0:support. 1:nonsupport, full match
    private byte partialAIDSelection;

    //tag 9F09, Application Version Number
    private byte[] terminalAidVersion;

    //tag DF8121, Terminal Action Code – Denial
    private byte[] tacDenial;

    //tag DF8122, Terminal Action Code – Online
    private byte[] tacOnline;

    //tag DF8120, Terminal Action Code – Default
    private byte[] tacDefault;

    //tag 9F1D, Terminal Risk Management Data
    private byte[] terminalRisk;

    //tag DF8126, Reader CVM Required Limit
    private long contactlessCvmLimit;

    //tag DF8123, Reader Contactless Floor Limit
    private long contactlessFloorLimit;

    //tag DF8124, Reader Contactless Transaction Limit (No On-device CVM)
    private long contactlessTransactionLimitNoOnDevice;

    //tag DF8125,Reader Contactless Transaction Limit (On-device CVM)
    private long contactlessTransactionLimitOnDevice;

    //Reader contactless transaction limit check flag, 0- Deactivated, 1- Active and exist, 2- Active but not exist
    private byte contactlessTransactionLimitSupported;

    //Card reader CVM limit check flag, 0- Deactivated, 1- Active and exist, 2- Active but not exist
    private byte contactlessCvmLimitSupported;

    //Card reader contactless Offline limit check flag, 0- Deactivated, 1- Active and exist, 2- Active but not exist
    private byte contactlessFloorLimitSupported;

    //tag DF811B, Kernel Configuration
    private byte kernelConfiguration;

    //tag DF811C, Max Lifetime of Torn Transaction Log Record
    private byte tornLeftTime;

    //tag DF811D, Max Number of Torn Transaction Log Records
    private byte maximumTornNumber;

    //tag DF8117, Card Data Input Capability
    private byte cardDataInput;

    //tag DF811E, Mag-stripe CVM Capability – CVM Required
    private byte magneticCvm;

    //tag DF812C, Mag-stripe CVM Capability – No CVM Required
    private byte mageticNoCvm;

    //tag DF8118, CVM Capability – CVM Required
    private byte cvmCapabilityCvmRequired;

    //tag DF8119, CVM Capability – No CVM Required
    private byte cvmCapabilityNoCvmRequired;

    //tag DF811F, Security Capability
    private byte securityCapability;

    //tag 9F40, Additional Terminal Capabilities
    private byte[] additionalTerminalCapability;

    //tag DF810C, Kernel ID
    private byte kernelId;

    //tag 9F35, Terminal Type
    private byte terminalType;

    public PayPassAid() {

        applicationId = new byte[0];
        localAidName = "";
        terminalAidVersion = new byte[0];
        tacDenial = new byte[0];
        tacOnline = new byte[0];
        tacDefault = new byte[0];
        terminalRisk = new byte[0];
        additionalTerminalCapability = new byte[0];
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

    public byte getPartialAIDSelection() {
        return partialAIDSelection;
    }

    public void setPartialAIDSelection(byte partialAIDSelection) {
        this.partialAIDSelection = partialAIDSelection;
    }

    public byte[] getTerminalAidVersion() {
        return terminalAidVersion;
    }

    public void setTerminalAidVersion(byte[] terminalAidVersion) {
        this.terminalAidVersion = terminalAidVersion;
    }

    public byte[] getTacDenial() {
        return tacDenial;
    }

    public void setTacDenial(byte[] tacDenial) {
        this.tacDenial = tacDenial;
    }

    public byte[] getTacOnline() {
        return tacOnline;
    }

    public void setTacOnline(byte[] tacOnline) {
        this.tacOnline = tacOnline;
    }

    public byte[] getTacDefault() {
        return tacDefault;
    }

    public void setTacDefault(byte[] tacDefault) {
        this.tacDefault = tacDefault;
    }

    public byte[] getTerminalRisk() {
        return terminalRisk;
    }

    public void setTerminalRisk(byte[] terminalRisk) {
        this.terminalRisk = terminalRisk;
    }

    public long getContactlessCvmLimit() {
        return contactlessCvmLimit;
    }

    public void setContactlessCvmLimit(long contactlessCvmLimit) {
        this.contactlessCvmLimit = contactlessCvmLimit;
    }

    public long getContactlessFloorLimit() {
        return contactlessFloorLimit;
    }

    public void setContactlessFloorLimit(long contactlessFloorLimit) {
        this.contactlessFloorLimit = contactlessFloorLimit;
    }

    public long getContactlessTransactionLimitNoOnDevice() {
        return contactlessTransactionLimitNoOnDevice;
    }

    public void setContactlessTransactionLimitNoOnDevice(long contactlessTransactionLimitNoOnDevice) {
        this.contactlessTransactionLimitNoOnDevice = contactlessTransactionLimitNoOnDevice;
    }

    public long getContactlessTransactionLimitOnDevice() {
        return contactlessTransactionLimitOnDevice;
    }

    public void setContactlessTransactionLimitOnDevice(long contactlessTransactionLimitOnDevice) {
        this.contactlessTransactionLimitOnDevice = contactlessTransactionLimitOnDevice;
    }

    public byte getContactlessTransactionLimitSupported() {
        return contactlessTransactionLimitSupported;
    }

    public void setContactlessTransactionLimitSupported(byte contactlessTransactionLimitSupported) {
        this.contactlessTransactionLimitSupported = contactlessTransactionLimitSupported;
    }

    public byte getContactlessCvmLimitSupported() {
        return contactlessCvmLimitSupported;
    }

    public void setContactlessCvmLimitSupported(byte contactlessCvmLimitSupported) {
        this.contactlessCvmLimitSupported = contactlessCvmLimitSupported;
    }

    public byte getContactlessFloorLimitSupported() {
        return contactlessFloorLimitSupported;
    }

    public void setContactlessFloorLimitSupported(byte contactlessFloorLimitSupported) {
        this.contactlessFloorLimitSupported = contactlessFloorLimitSupported;
    }

    public byte getKernelConfiguration() {
        return kernelConfiguration;
    }

    public void setKernelConfiguration(byte kernelConfiguration) {
        this.kernelConfiguration = kernelConfiguration;
    }

    public byte getTornLeftTime() {
        return tornLeftTime;
    }

    public void setTornLeftTime(byte tornLeftTime) {
        this.tornLeftTime = tornLeftTime;
    }

    public byte getMaximumTornNumber() {
        return maximumTornNumber;
    }

    public void setMaximumTornNumber(byte maximumTornNumber) {
        this.maximumTornNumber = maximumTornNumber;
    }

    public byte getCardDataInput() {
        return cardDataInput;
    }

    public void setCardDataInput(byte cardDataInput) {
        this.cardDataInput = cardDataInput;
    }

    public byte getMagneticCvm() {
        return magneticCvm;
    }

    public void setMagneticCvm(byte magneticCvm) {
        this.magneticCvm = magneticCvm;
    }

    public byte getMageticNoCvm() {
        return mageticNoCvm;
    }

    public void setMageticNoCvm(byte mageticNoCvm) {
        this.mageticNoCvm = mageticNoCvm;
    }

    public byte getCvmCapabilityCvmRequired() {
        return cvmCapabilityCvmRequired;
    }

    public void setCvmCapabilityCvmRequired(byte cvmCapabilityCvmRequired) {
        this.cvmCapabilityCvmRequired = cvmCapabilityCvmRequired;
    }

    public byte getCvmCapabilityNoCvmRequired() {
        return cvmCapabilityNoCvmRequired;
    }

    public void setCvmCapabilityNoCvmRequired(byte cvmCapabilityNoCvmRequired) {
        this.cvmCapabilityNoCvmRequired = cvmCapabilityNoCvmRequired;
    }

    public byte getSecurityCapability() {
        return securityCapability;
    }

    public void setSecurityCapability(byte securityCapability) {
        this.securityCapability = securityCapability;
    }

    public byte[] getAdditionalTerminalCapability() {
        return additionalTerminalCapability;
    }

    public void setAdditionalTerminalCapability(byte[] additionalTerminalCapability) {
        this.additionalTerminalCapability = additionalTerminalCapability;
    }

    public byte getKernelId() {
        return kernelId;
    }

    public void setKernelId(byte kernelId) {
        this.kernelId = kernelId;
    }

    public byte getTerminalType() {
        return terminalType;
    }

    public void setTerminalType(byte terminalType) {
        this.terminalType = terminalType;
    }
}

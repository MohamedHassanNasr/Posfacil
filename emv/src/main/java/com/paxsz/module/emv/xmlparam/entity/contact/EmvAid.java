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
 *  2020/05/20 	         Qinny Zhou           	Create/Add/Modify/Delete
 *  ===========================================================================================
 */
package com.paxsz.module.emv.xmlparam.entity.contact;

public class EmvAid {
    private byte partialAIDSelection;
    private byte[] applicationID;
    private String localAIDName;
    private byte[] terminalAIDVersion;
    private byte[] tacDenial;
    private byte[] tacOnline;
    private byte[] tacDefault;
    private long floorLimit;
    private long threshold;
    private byte targetPercentage;
    private byte maxTargetPercentage;
    private byte[] terminalDefaultTDOL;
    private byte[] terminalDefaultDDOL;
    private byte[] terminalRiskManagementData;
    private byte terminalType;
    private byte cardDataInputCapability;
    private byte cvmCapability;
    private byte securityCapability;
    private byte[] additionalTerminalCapabilities;
    private int getDataForPINTryCounter;
    private int bypassPINEntry;
    private int subsequentBypassPINEntry;
    private int forcedOnlineCapability;

    public byte getPartialAIDSelection() {
        return partialAIDSelection;
    }

    /**
     * Set Partial AID Selection Flag
     *
     * @param partialAIDSelection Partial AID Selection Flag value, 0:Partial Match, 1: Full Match
     */
    public void setPartialAIDSelection(byte partialAIDSelection) {
        this.partialAIDSelection = partialAIDSelection;
    }

    public byte[] getApplicationID() {
        return applicationID;
    }

    /**
     * set Application ID
     *
     * @param applicationID Application ID (tag:4F)
     */
    public void setApplicationID(byte[] applicationID) {
        this.applicationID = applicationID;
    }

    public String getLocalAIDName() {
        return localAIDName;
    }

    /**
     * Set Local Application Name
     *
     * @param localAIDName
     */
    public void setLocalAIDName(String localAIDName) {
        this.localAIDName = localAIDName;
    }

    public byte[] getTerminalAIDVersion() {
        return terminalAIDVersion;
    }

    /**
     * Set Terminal Application Version Number. (tag:9F09)
     *
     * @param terminalAIDVersion Terminal Application Version Number
     */
    public void setTerminalAIDVersion(byte[] terminalAIDVersion) {
        this.terminalAIDVersion = terminalAIDVersion;
    }

    public byte[] getTacDenial() {
        return tacDenial;
    }

    /**
     * Set Terminal Action Code – Denial
     *
     * @param tacDenial Terminal Action Code – Denial
     */
    public void setTacDenial(byte[] tacDenial) {
        this.tacDenial = tacDenial;
    }

    public byte[] getTacOnline() {
        return tacOnline;
    }

    /**
     * Set Terminal Action Code – Online
     *
     * @param tacOnline Terminal Action Code – Online
     */
    public void setTacOnline(byte[] tacOnline) {
        this.tacOnline = tacOnline;
    }

    public byte[] getTacDefault() {
        return tacDefault;
    }

    /**
     * Set Terminal Action Code - Default
     *
     * @param tacDefault Terminal Action Code - Default
     */
    public void setTacDefault(byte[] tacDefault) {
        this.tacDefault = tacDefault;
    }

    public long getFloorLimit() {
        return floorLimit;
    }

    /**
     * Set Terminal Floor Limit(tag:9F1B)
     *
     * @param floorLimit Terminal Floor Limit
     */
    public void setFloorLimit(long floorLimit) {
        this.floorLimit = floorLimit;
    }

    public long getThreshold() {
        return threshold;
    }

    /**
     * Set Threshold
     * <p>Notes: If Target Percentage is 99 and Threshold is 0xffffffff, all the transaction will be done online.
     *
     * @param threshold Threshold value
     */
    public void setThreshold(long threshold) {
        this.threshold = threshold;
    }

    public byte getTargetPercentage() {
        return targetPercentage;
    }

    /**
     * Set Target Percentage, Refer to the risk management in EMV specification
     *
     * @param targetPercentage Target Percentage value
     */
    public void setTargetPercentage(byte targetPercentage) {
        this.targetPercentage = targetPercentage;
    }

    public byte getMaxTargetPercentage() {
        return maxTargetPercentage;
    }

    /**
     * Set Max TargetPercentage,Refer to the risk management in EMV specification
     *
     * @param maxTargetPercentage Max TargetPercentage value
     */
    public void setMaxTargetPercentage(byte maxTargetPercentage) {
        this.maxTargetPercentage = maxTargetPercentage;
    }

    public byte[] getTerminalDefaultTDOL() {
        return terminalDefaultTDOL;
    }

    /**
     * Set Default Transaction Certificate Data Object List
     *
     * @param terminalDefaultTDOL Default Transaction Certificate Data Object List
     */
    public void setTerminalDefaultTDOL(byte[] terminalDefaultTDOL) {
        this.terminalDefaultTDOL = terminalDefaultTDOL;
    }

    public byte[] getTerminalDefaultDDOL() {
        return terminalDefaultDDOL;
    }

    /**
     * Set Default Dynamic Data Authentication Data Object List
     *
     * @param terminalDefaultDDOL Default Dynamic Data Authentication Data Object List
     */
    public void setTerminalDefaultDDOL(byte[] terminalDefaultDDOL) {
        this.terminalDefaultDDOL = terminalDefaultDDOL;
    }

    public byte[] getTerminalRiskManagementData() {
        return terminalRiskManagementData;
    }

    /**
     * Set Terminal Risk Management Data, It needn't be set unless issuer requires.( tag:9F1D)
     *
     * @param terminalRiskManagementData Terminal Risk Management Data
     */
    public void setTerminalRiskManagementData(byte[] terminalRiskManagementData) {
        this.terminalRiskManagementData = terminalRiskManagementData;
    }

    public byte getTerminalType() {
        return terminalType;
    }

    /**
     * Set Terminal Type. (tag:9F35)
     *
     * @param terminalType
     */
    public void setTerminalType(byte terminalType) {
        this.terminalType = terminalType;
    }

    public byte getCardDataInputCapability() {
        return cardDataInputCapability;
    }

    /**
     * Set Card Data Input Capability (tag:9F33 Byte 1)
     *
     * @param cardDataInputCapability
     */
    public void setCardDataInputCapability(byte cardDataInputCapability) {
        this.cardDataInputCapability = cardDataInputCapability;
    }

    public byte getCvmCapability() {
        return cvmCapability;
    }

    /**
     * Set CVM Capability.(tag:9F33 Byte 2)
     *
     * @param cvmCapability
     */
    public void setCvmCapability(byte cvmCapability) {
        this.cvmCapability = cvmCapability;
    }

    public byte getSecurityCapability() {
        return securityCapability;
    }

    /**
     * Set Security Capability (tag:9F33 Byte 3)
     *
     * @param securityCapability
     */
    public void setSecurityCapability(byte securityCapability) {
        this.securityCapability = securityCapability;
    }

    public byte[] getAdditionalTerminalCapabilities() {
        return additionalTerminalCapabilities;
    }

    /**
     * Additional Terminal Capability (tag:9F40,)
     *
     * @param additionalTerminalCapabilities
     */
    public void setAdditionalTerminalCapabilities(byte[] additionalTerminalCapabilities) {
        this.additionalTerminalCapabilities = additionalTerminalCapabilities;
    }

    public int getGetDataForPINTryCounter() {
        return getDataForPINTryCounter;
    }

    /**
     * Set Whether support get the PIN retry counter before let cardholder enter offline PIN.
     *
     * @param getDataForPINTryCounter 0: NOT supported, 1:supported
     */
    public void setGetDataForPINTryCounter(int getDataForPINTryCounter) {
        this.getDataForPINTryCounter = getDataForPINTryCounter;
    }

    public int getBypassPINEntry() {
        return bypassPINEntry;
    }

    /**
     * Set Whether support Bypass PIN entry.
     *
     * @param bypassPINEntry 0:NOT supported, 1:supported
     */
    public void setBypassPINEntry(int bypassPINEntry) {
        this.bypassPINEntry = bypassPINEntry;
    }

    public int getSubsequentBypassPINEntry() {
        return subsequentBypassPINEntry;
    }

    /**
     * Set Whether support Bypass all other PIN when one PIN has been Bypassed.
     *
     * @param subsequentBypassPINEntry 0: NOT supported, 1:supported
     */
    public void setSubsequentBypassPINEntry(int subsequentBypassPINEntry) {
        this.subsequentBypassPINEntry = subsequentBypassPINEntry;
    }

    public int getForcedOnlineCapability() {
        return forcedOnlineCapability;
    }

    /**
     * Merchant force online Flag.
     *
     * @param forcedOnlineCapability 1: means always require online transaction, 0:Whether require online is decided by terminal and card
     */
    public void setForcedOnlineCapability(int forcedOnlineCapability) {
        this.forcedOnlineCapability = forcedOnlineCapability;
    }
}

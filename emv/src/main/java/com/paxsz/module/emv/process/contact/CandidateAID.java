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
 *  2020/05/28 	         Qinny Zhou           	      Create
 *  ===========================================================================================
 */

package com.paxsz.module.emv.process.contact;

public class CandidateAID {
    private byte[] appPreName;
    private byte[] appLabel;
    private byte[] issDiscrData;
    private byte[] aid;
    private byte aidLen;
    private byte priority;
    private byte[] appName;
    private byte[] reserve;

    public CandidateAID() {
        this.appPreName = new byte[17];
        this.appLabel = new byte[17];
        this.issDiscrData = new byte['ô'];
        this.aid = new byte[17];
        this.aidLen = 0;
        this.priority = 0;
        this.appName = new byte[33];
        this.reserve = new byte[2];
    }


    public byte[] getAppPreName() {
        return appPreName;
    }

    public void setAppPreName(byte[] appPreName) {
        this.appPreName = appPreName;
    }

    public byte[] getAppLabel() {
        return appLabel;
    }

    public void setAppLabel(byte[] appLabel) {
        this.appLabel = appLabel;
    }

    public byte[] getIssDiscrData() {
        return issDiscrData;
    }

    public void setIssDiscrData(byte[] issDiscrData) {
        this.issDiscrData = issDiscrData;
    }

    public byte[] getAid() {
        return aid;
    }

    public void setAid(byte[] aid) {
        this.aid = aid;
    }

    public byte getAidLen() {
        return aidLen;
    }

    public void setAidLen(byte aidLen) {
        this.aidLen = aidLen;
    }

    public byte getPriority() {
        return priority;
    }

    public void setPriority(byte priority) {
        this.priority = priority;
    }

    public byte[] getAppName() {
        return appName;
    }

    public void setAppName(byte[] appName) {
        this.appName = appName;
    }

    public byte[] getReserve() {
        return reserve;
    }

    public void setReserve(byte[] reserve) {
        this.reserve = reserve;
    }
}

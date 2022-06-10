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
 *  2020/05/27 	         Qinny Zhou           	      Create
 *  ===========================================================================================
 */

package com.paxsz.module.emv.xmlparam.entity.common;

public class CapkRevoke {
    // Registered Application Provider Identifier
    private byte[] rid;

    // Certification Authenticate Public Key Index.
    private byte keyId;

    // Issuer Certificate Serial Number.
    private byte[] certificateSN;

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

    public byte[] getCertificateSN() {
        return certificateSN;
    }

    public void setCertificateSN(byte[] certificateSN) {
        this.certificateSN = certificateSN;
    }
}

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

import java.util.ArrayList;

public class CapkParam {
    ArrayList<Capk> capkList;
    ArrayList<CapkRevoke> capkRevokeList;

    public ArrayList<Capk> getCapkList() {
        return capkList;
    }

    public void setCapkList(ArrayList<Capk> capkList) {
        this.capkList = capkList;
    }

    public ArrayList<CapkRevoke> getCapkRevokeList() {
        return capkRevokeList;
    }

    public void setCapkRevokeList(ArrayList<CapkRevoke> capkRevokeList) {
        this.capkRevokeList = capkRevokeList;
    }
}

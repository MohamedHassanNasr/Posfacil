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
package com.paxsz.module.emv.process.contact;

import java.util.List;

public interface IEmvTransProcessListener {

    /**
     * When Select app
     */
    int onWaitAppSelect(boolean isFirstSelect, List<CandidateAID> candList);//todo


    /**
     * When need to enter pin
     */
    int onCardHolderPwd(boolean bOnlinePin, int leftTimes, byte[] pinData);//todo
}

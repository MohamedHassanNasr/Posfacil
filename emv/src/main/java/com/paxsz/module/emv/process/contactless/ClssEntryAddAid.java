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
 * 20200528  	         JackHuang               Create
 * ===========================================================================================
 */
package com.paxsz.module.emv.process.contactless;

import com.pax.jemv.clcommon.Clss_PreProcInfo;
import com.pax.jemv.clcommon.KernType;
import com.pax.jemv.entrypoint.api.ClssEntryApi;
import com.paxsz.module.emv.param.EmvProcessParam;
import com.paxsz.module.emv.utils.EmvParamConvert;
import com.paxsz.module.emv.xmlparam.entity.clss.PayPassAid;
import com.paxsz.module.emv.xmlparam.entity.clss.PayWaveAid;

class ClssEntryAddAid {
    private EmvProcessParam emvProcessParam;

    ClssEntryAddAid(EmvProcessParam emvProcessParam){
        this.emvProcessParam = emvProcessParam;
    }

    /**
     * add aid and set entry param, before deteted card, need add all aids
     */
    void addApp(){
        addPayPassAid();
        addPayWaveAid();
        //todo, other clss aid,if need
    }

    private void addPayPassAid(){
        if(emvProcessParam.getPayPassAidList() == null){
            throw new IllegalArgumentException();
        }

        for (PayPassAid i : emvProcessParam.getPayPassAidList()){
            ClssEntryApi.Clss_AddAidList_Entry(i.getApplicationId(),(byte) i.getApplicationId().length,
                    i.getPartialAIDSelection(),(byte) KernType.KERNTYPE_DEF);
            Clss_PreProcInfo clss_preProcInfo = EmvParamConvert.PayPassAidPreProcInfo(i);
            ClssEntryApi.Clss_SetPreProcInfo_Entry(clss_preProcInfo);
        }
    }

    private void addPayWaveAid(){
        if(emvProcessParam.getPayWaveParam().getPayWaveAidArrayList() == null){
            throw new IllegalArgumentException();
        }

        for (PayWaveAid i : emvProcessParam.getPayWaveParam().getPayWaveAidArrayList()){
            ClssEntryApi.Clss_AddAidList_Entry(i.getApplicationId(),(byte) i.getApplicationId().length,
                    i.getPartialAidSelection(),(byte) KernType.KERNTYPE_DEF);
            Clss_PreProcInfo clss_preProcInfo = EmvParamConvert.PayPassWavePreProcInfo(i, emvProcessParam.getEmvTransParam().getTransType());
            ClssEntryApi.Clss_SetPreProcInfo_Entry(clss_preProcInfo);
        }
    }
}

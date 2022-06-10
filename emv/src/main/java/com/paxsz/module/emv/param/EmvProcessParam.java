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
 * 20200525  	         JackHuang               Create
 * ===========================================================================================
 */

package com.paxsz.module.emv.param;

import com.paxsz.module.emv.xmlparam.entity.clss.PayPassAid;
import com.paxsz.module.emv.xmlparam.entity.clss.PayWaveAid;
import com.paxsz.module.emv.xmlparam.entity.clss.PayWaveParam;
import com.paxsz.module.emv.xmlparam.entity.common.CapkParam;
import com.paxsz.module.emv.xmlparam.entity.common.Config;
import com.paxsz.module.emv.xmlparam.entity.contact.EmvAid;

import java.util.ArrayList;

public class EmvProcessParam {

    private EmvTransParam emvTransParam;
    private ArrayList<EmvAid> emvAidList;
    private ArrayList<PayPassAid> payPassAidList;
    private PayWaveParam payWaveParam;
    private CapkParam capkParam;
    private Config termConfig;

    private EmvProcessParam(Builder builder) {
        this.emvTransParam = builder.emvTransParam;
        this.capkParam = builder.capkParam;
        this.emvAidList = builder.emvAidList;
        this.payPassAidList = builder.payPassAidList;
        this.payWaveParam = builder.payWaveParam;
        this.termConfig = builder.termConfig;
    }

    public EmvTransParam getEmvTransParam() {
        return emvTransParam;
    }

    public CapkParam getCapkParam() {
        return capkParam;
    }

    public ArrayList<EmvAid> getEmvAidList() {
        return emvAidList;
    }

    public ArrayList<PayPassAid> getPayPassAidList() {
        return payPassAidList;
    }

    public PayWaveParam getPayWaveParam() {
        return payWaveParam;
    }

    public Config getTermConfig() {
        return termConfig;
    }

    public final static class Builder {
        private EmvTransParam emvTransParam;

        private ArrayList<EmvAid> emvAidList;
        private ArrayList<PayPassAid> payPassAidList;
        private PayWaveParam payWaveParam;

        private CapkParam capkParam;
        private Config termConfig;

        public Builder(EmvTransParam transParam, Config termConfigParam, CapkParam capkParam) {
            this.emvTransParam = transParam;
            this.termConfig = termConfigParam;
            this.capkParam = capkParam;
        }

        public Builder setEmvAidList(ArrayList<EmvAid> emvAidList) {
            this.emvAidList = emvAidList;
            return this;
        }

        public Builder setPayPassAidList(ArrayList<PayPassAid> payPassAidList) {
            this.payPassAidList = payPassAidList;
            return this;
        }

        public Builder setPayWaveParam(PayWaveParam payWaveParam) {
            this.payWaveParam = payWaveParam;
            return this;
        }

        public EmvProcessParam create() {
            return new EmvProcessParam(this);
        }

    }


}

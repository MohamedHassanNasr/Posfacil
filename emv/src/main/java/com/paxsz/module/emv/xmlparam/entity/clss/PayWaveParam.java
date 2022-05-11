package com.paxsz.module.emv.xmlparam.entity.clss;

import java.util.ArrayList;

public class PayWaveParam {
    private ArrayList<PayWaveAid> payWaveAidArrayList;
    private ArrayList<PayWaveProgramId> waveProgramIdArrayList;

    public ArrayList<PayWaveAid> getPayWaveAidArrayList() {
        return payWaveAidArrayList;
    }

    public void setPayWaveAidArrayList(ArrayList<PayWaveAid> payWaveAidArrayList) {
        this.payWaveAidArrayList = payWaveAidArrayList;
    }

    public ArrayList<PayWaveProgramId> getWaveProgramIdArrayList() {
        return waveProgramIdArrayList;
    }

    public void setWaveProgramIdArrayList(ArrayList<PayWaveProgramId> waveProgramIdArrayList) {
        this.waveProgramIdArrayList = waveProgramIdArrayList;
    }
}

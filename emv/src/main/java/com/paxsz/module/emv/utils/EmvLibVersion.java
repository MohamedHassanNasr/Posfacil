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
 * 2020619  	         JackHuang               Create
 * ===========================================================================================
 */
package com.paxsz.module.emv.utils;

import android.util.ArrayMap;

import com.pax.jemv.clcommon.ByteArray;
import com.pax.jemv.emv.api.EMVApi;
import com.pax.jemv.entrypoint.api.ClssEntryApi;
import com.pax.jemv.paypass.api.ClssPassApi;
import com.pax.jemv.paywave.api.ClssWaveApi;

import java.util.Arrays;

public class EmvLibVersion {


    public EmvLibVersion() {
    }

    public ArrayMap<String, String> getEmvLibVersion() {
        ArrayMap<String, String> arrayMap = new ArrayMap<>();

        //get emv lib version
        ByteArray emvLibVersion = new ByteArray();
        EMVApi.EMVReadVerInfo(emvLibVersion);

        arrayMap.put("EMV", new String(Arrays.copyOf(emvLibVersion.data, emvLibVersion.length)));

        //get entry lib version
        ByteArray entryLibVersion = new ByteArray();
        ClssEntryApi.Clss_ReadVerInfo_Entry(entryLibVersion);
        arrayMap.put("Entry", new String(Arrays.copyOf(entryLibVersion.data, entryLibVersion.length)));

        //get Paypass lib version
        ByteArray payPassLibVersion = new ByteArray();
        ClssPassApi.Clss_ReadVerInfo_MC(payPassLibVersion);
        arrayMap.put("PayPass", new String(Arrays.copyOf(payPassLibVersion.data, payPassLibVersion.length)));

        //get PayWave lib version
        ByteArray payWaveLibVersion = new ByteArray();
        ClssWaveApi.Clss_ReadVerInfo_Wave(payWaveLibVersion);
        arrayMap.put("PayWave", new String(Arrays.copyOf(payWaveLibVersion.data, payWaveLibVersion.length)));

        return arrayMap;
    }
}

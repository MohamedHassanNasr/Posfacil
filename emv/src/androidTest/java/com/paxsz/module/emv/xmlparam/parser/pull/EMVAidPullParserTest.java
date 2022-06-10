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
 *  2020/05/21 	         Qinny Zhou           	Create/Add/Modify/Delete
 *  ===========================================================================================
 */

package com.paxsz.module.emv.xmlparam.parser.pull;

import android.support.test.InstrumentationRegistry;

import com.pax.commonlib.utils.LogUtils;
import com.pax.commonlib.utils.convert.ConvertHelper;
import com.paxsz.module.emv.xmlparam.entity.common.Capk;
import com.paxsz.module.emv.xmlparam.entity.contact.EmvAid;

import org.junit.Test;

import java.io.InputStream;
import java.util.ArrayList;

public class EMVAidPullParserTest {

    @Test
    public void parse() {
        try (InputStream inputStream = InstrumentationRegistry.getTargetContext().getAssets().open("emv_param.contact")){
            LogUtils.d("EMVAidPullParserTest", "test parse start" );
            long startMs = System.currentTimeMillis();
            ArrayList<EmvAid> emvAidArrayList = new EmvAidPullParser().parse(inputStream);
            long endMs = System.currentTimeMillis();
            LogUtils.d("EMVAidPullParserTest", "test parse need " + (endMs - startMs) + "ms" );
            LogUtils.d("EMVAidPullParserTest", "list size =" + emvAidArrayList.size());
            int size = emvAidArrayList.size();

            long startCmp = System.currentTimeMillis();
            for (EmvAid emvAid : emvAidArrayList){
                if (ConvertHelper.getConvert().bcdToStr(emvAid.getApplicationID()).equals(new String("A000000333010106"))){
                    long endCmp = System.currentTimeMillis();
                    LogUtils.d("EMVAidPullParserTest", "get last aid ned  " + (endCmp - startCmp) + "ms" );
                    LogUtils.d("EMVAidPullParserTest", "get last aid cvm capability:  " + ConvertHelper.getConvert().bcdToStr(new byte[]{emvAid.getCvmCapability()}));
                    break;
                }
            }
        } catch (Exception e) {
            LogUtils.e("EMVAidPullParserTest", e);
        }
    }
}
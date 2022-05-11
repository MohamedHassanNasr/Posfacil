package com.paxsz.module.emv.xmlparam.parser.pull;

import android.support.test.InstrumentationRegistry;

import com.pax.commonlib.utils.LogUtils;
import com.pax.commonlib.utils.convert.ConvertHelper;
import com.paxsz.module.emv.xmlparam.entity.common.Capk;
import com.paxsz.module.emv.xmlparam.entity.common.CapkParam;

import org.junit.Test;

import java.io.InputStream;
import java.util.ArrayList;

public class CapkPullParserTest {

    @Test
    public void parse() {
        try (InputStream inputStream = InstrumentationRegistry.getTargetContext().getAssets().open("capk.capk")){
            LogUtils.d("CapkPullParserTest", "test parse start" );
            long startMs = System.currentTimeMillis();
            CapkParam capkParam = new CapkParamPullParser().parse(inputStream);
            long endMs = System.currentTimeMillis();
            LogUtils.d("CapkPullParserTest", "test parse need " + (endMs - startMs) + "ms" );
            LogUtils.d("CapkPullParserTest", "list size =" + capkParam.getCapkList().size());
            int size = capkParam.getCapkList().size();

            long startCmp = System.currentTimeMillis();
            for (Capk capk : capkParam.getCapkList()) {
                if (ConvertHelper.getConvert().bcdToStr(capk.getRid()).equals(new String("A000000524")) && capk.getKeyId() == 0x06){
                    long endCmp = System.currentTimeMillis();
                    LogUtils.d("CapkPullParserTest", "get rid capk ned  " + (endCmp - startCmp) + "ms" );
                    LogUtils.d("CapkPullParserTest", "get rid capk check sum  " + ConvertHelper.getConvert().bcdToStr(capk.getCheckSum()));
                    break;
                }
            }
        } catch (Exception e) {
            LogUtils.e("CapkPullParserTest", e);
        }
    }
}
package com.paxsz.module.emv.xmlparam.parser.pull;

import android.support.test.InstrumentationRegistry;

import com.pax.commonlib.utils.LogUtils;
import com.pax.commonlib.utils.convert.ConvertHelper;
import com.paxsz.module.emv.xmlparam.entity.clss.PayWaveParam;
import com.paxsz.module.emv.xmlparam.entity.common.Capk;

import org.junit.Test;

import java.io.InputStream;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class PayWavePullParserTest {

    @Test
    public void parse() {
        try (InputStream inputStream = InstrumentationRegistry.getTargetContext().getAssets().open("paywave_param.clss_wave")){
            LogUtils.d("PayWavePullParserTest", "test parse start" );
            long startMs = System.currentTimeMillis();
            PayWaveParam payWaveParams = new PayWavePullParser().parse(inputStream);
            long endMs = System.currentTimeMillis();
            LogUtils.d("PayWavePullParserTest", "payWaveParams.getPayWaveAidArrayList().size() = " + payWaveParams.getPayWaveAidArrayList().size() );
            LogUtils.d("PayWavePullParserTest", "payWaveParams.getWaveProgramIdArrayList().size() = " + payWaveParams.getWaveProgramIdArrayList().size());

        } catch (Exception e) {
            LogUtils.e("PayWavePullParserTest", e);
        }
    }
}
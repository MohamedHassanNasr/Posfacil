package com.paxsz.module.emv.xmlparam.parser.pull;

import android.support.test.InstrumentationRegistry;

import com.pax.commonlib.utils.LogUtils;
import com.pax.commonlib.utils.convert.ConvertHelper;
import com.paxsz.module.emv.xmlparam.entity.common.Config;

import org.junit.Test;

import java.io.InputStream;

public class ConfigPullParserTest {

    @Test
    public void parse() {
        try (InputStream inputStream = InstrumentationRegistry.getTargetContext().getAssets().open("emv_clss.config")) {
            LogUtils.d("ConfigPullParserTest", "test parse start" );
            long startMs = System.currentTimeMillis();
            Config config = new ConfigPullParser().parse(inputStream);
            long endMs = System.currentTimeMillis();
            LogUtils.d("ConfigPullParserTest", "config.getMerchantId() = " + config.getMerchantId());
            LogUtils.d("ConfigPullParserTest", "config.getMerchantNameAndLocation() = " + config.getMerchantNameAndLocation());
            LogUtils.d("ConfigPullParserTest", "config.getTerminalCurrencySymbol() = " + config.getTerminalCurrencySymbol());
            LogUtils.d("ConfigPullParserTest", "config.getConversionRatio() = " + config.getConversionRatio());
            LogUtils.d("ConfigPullParserTest", "config.getMerchantCategoryCode() = " + ConvertHelper.getConvert().bcdToStr(config.getMerchantCategoryCode()));

        } catch (Exception e) {
            LogUtils.e("ConfigPullParserTest", e);
        }
    }
}
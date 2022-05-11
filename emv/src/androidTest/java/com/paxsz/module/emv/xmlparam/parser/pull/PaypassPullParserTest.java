package com.paxsz.module.emv.xmlparam.parser.pull;

import android.support.test.InstrumentationRegistry;

import com.pax.commonlib.utils.LogUtils;
import com.pax.commonlib.utils.convert.ConvertHelper;
import com.paxsz.module.emv.xmlparam.entity.clss.PayPassAid;

import org.junit.Test;

import java.io.InputStream;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class PaypassPullParserTest {

    @Test
    public void parse() {
        try (InputStream inputStream = InstrumentationRegistry.getTargetContext().getAssets().open("paypass_param.clss_mc")){
            LogUtils.d("PaypassPullParserTest", "test parse start" );
            long startMs = System.currentTimeMillis();
            ArrayList<PayPassAid> aidArrayList = new PaypassPullParser().parse(inputStream);
            long endMs = System.currentTimeMillis();
            LogUtils.d("PaypassPullParserTest", "test parse need " + (endMs - startMs) + "ms" );
            LogUtils.d("PaypassPullParserTest", "list size =" + aidArrayList.size());
            int size = aidArrayList.size();

            for(int i = 0; i < size; i++){
                LogUtils.d("PaypassPullParserTest", "AID index =" + i + " start------------------------" );

                LogUtils.d("PaypassPullParserTest", "LocalAIDName = " + aidArrayList.get(i).getLocalAidName());
                LogUtils.d("PaypassPullParserTest", "ApplicationID = " + ConvertHelper.getConvert().bcdToStr(aidArrayList.get(i).getApplicationId()));
                LogUtils.d("PaypassPullParserTest", "PartialAIDSelection = " + aidArrayList.get(i).getPartialAIDSelection());
                LogUtils.d("PaypassPullParserTest", "TerminalAIDVersion = " + ConvertHelper.getConvert().bcdToStr(aidArrayList.get(i).getTerminalAidVersion()));
                LogUtils.d("PaypassPullParserTest", "TACDenial = " + ConvertHelper.getConvert().bcdToStr(aidArrayList.get(i).getTacDenial()));
                LogUtils.d("PaypassPullParserTest", "TACOnline = " + ConvertHelper.getConvert().bcdToStr(aidArrayList.get(i).getTacOnline()));
                LogUtils.d("PaypassPullParserTest", "TACDefault = " + ConvertHelper.getConvert().bcdToStr(aidArrayList.get(i).getTacDefault()));
                LogUtils.d("PaypassPullParserTest", "TerminalRisk = " + ConvertHelper.getConvert().bcdToStr(aidArrayList.get(i).getTerminalRisk()));
                LogUtils.d("PaypassPullParserTest", "ContactlessCVMLimit = " + aidArrayList.get(i).getContactlessCvmLimit());
                LogUtils.d("PaypassPullParserTest", "ContactlessFloorLimit = " + aidArrayList.get(i).getContactlessFloorLimit());
                LogUtils.d("PaypassPullParserTest", "ContactlessTransactionLimit_NoOnDevice = " + aidArrayList.get(i).getContactlessTransactionLimitNoOnDevice());
                LogUtils.d("PaypassPullParserTest", "ContactlessTransactionLimit_OnDevice = " + aidArrayList.get(i).getContactlessTransactionLimitOnDevice());
                LogUtils.d("PaypassPullParserTest", "ContactlessTransactionLimitSupported = " + aidArrayList.get(i).getContactlessTransactionLimitSupported());
                LogUtils.d("PaypassPullParserTest", "CVMLimitSupported = " + aidArrayList.get(i).getContactlessCvmLimitSupported());
                LogUtils.d("PaypassPullParserTest", "ContactlessFloorLimitSupported = " + aidArrayList.get(i).getContactlessFloorLimitSupported());
                LogUtils.d("PaypassPullParserTest", "KernelConfiguration = " + ConvertHelper.getConvert().bcdToStr(new byte[]{aidArrayList.get(i).getKernelConfiguration()}));
                LogUtils.d("PaypassPullParserTest", "TornLeftTime = " + ConvertHelper.getConvert().bcdToStr(new byte[]{aidArrayList.get(i).getTornLeftTime()}));
                LogUtils.d("PaypassPullParserTest", "MaximumTornNumber = " + aidArrayList.get(i).getMaximumTornNumber());
                LogUtils.d("PaypassPullParserTest", "CardDataInput = " + ConvertHelper.getConvert().bcdToStr(new byte[]{aidArrayList.get(i).getCardDataInput()}));
                LogUtils.d("PaypassPullParserTest", "MagneticCVM = " + ConvertHelper.getConvert().bcdToStr(new byte[]{aidArrayList.get(i).getMagneticCvm()}));
                LogUtils.d("PaypassPullParserTest", "MageticNoCVM = " + ConvertHelper.getConvert().bcdToStr(new byte[]{aidArrayList.get(i).getMageticNoCvm()}));
                LogUtils.d("PaypassPullParserTest", "CVMCapability_CVMRequired = " + ConvertHelper.getConvert().bcdToStr(new byte[]{aidArrayList.get(i).getCvmCapabilityCvmRequired()}));
                LogUtils.d("PaypassPullParserTest", "CVMCapability_NoCVMRequired = " + ConvertHelper.getConvert().bcdToStr(new byte[]{aidArrayList.get(i).getCvmCapabilityNoCvmRequired()}));
                LogUtils.d("PaypassPullParserTest", "SecurityCapability = " + ConvertHelper.getConvert().bcdToStr(new byte[]{aidArrayList.get(i).getSecurityCapability()}));
                LogUtils.d("PaypassPullParserTest", "AdditionalTerminalCapability = " + ConvertHelper.getConvert().bcdToStr(aidArrayList.get(i).getAdditionalTerminalCapability()));
                LogUtils.d("PaypassPullParserTest", "KernelID = " + ConvertHelper.getConvert().bcdToStr(new byte[]{aidArrayList.get(i).getKernelId()}));
                LogUtils.d("PaypassPullParserTest", "TerminalType = " + ConvertHelper.getConvert().bcdToStr(new byte[]{aidArrayList.get(i).getTerminalType()}));

                LogUtils.d("PaypassPullParserTest", "AID index =" + i + " end------------------------" );
            }
        } catch (Exception e) {
            LogUtils.e("PaypassPullParserTest", e);
        }
    }
}
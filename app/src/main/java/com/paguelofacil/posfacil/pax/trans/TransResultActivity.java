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
 *  2020/06/05 	         Qinny Zhou           	      Create
 *  ===========================================================================================
 */

package com.paguelofacil.posfacil.pax.trans;

import static com.paguelofacil.posfacil.pax.MainActivity.TXN_TYPE_ICC;
import static com.paguelofacil.posfacil.pax.MainActivity.TXN_TYPE_PICC;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.paguelofacil.posfacil.ApplicationClass;
import com.paguelofacil.posfacil.R;
import com.paguelofacil.posfacil.pax.util.CurrencyConverter;
import com.paguelofacil.posfacil.pax.util.ScreenUtils;
import com.pax.commonlib.utils.LogUtils;
import com.pax.commonlib.utils.ToastUtils;
import com.pax.commonlib.utils.convert.ConvertHelper;
import com.pax.jemv.clcommon.ByteArray;
import com.pax.jemv.clcommon.KernType;
import com.pax.jemv.clcommon.RetCode;
import com.paxsz.module.emv.process.contact.EmvProcess;
import com.paxsz.module.emv.process.contactless.ClssProcess;
import com.paxsz.module.emv.process.enums.TransResultEnum;

import java.math.BigInteger;
import java.util.ArrayList;

public class TransResultActivity extends Activity {
    private static final String TAG = "TransResultActivity";


    //UI List: Transaction Type, Transaction Amount,Other Amount, card No.,AID,AIP, CID,TSI,TVR,Terminal capabilities,CVM List,ATC
    private static final int[] EMV_TAG = {0x9C, 0x9F02, 0x9F03, 0x5A, 0x4F, 0x82, 0x9F27, 0x9B, 0x95, 0x9F33, 0x8E, 0x9F36};
    private static final String[] TAG_TITLE = {"TransType", "TransAmount", "OtherAmount", "Card No.", "AID", "AIP", "CID", "TSI", "TVR", "Terminal Capabilities", "CVM List", "ATC"};

    private TextView transAmtText;
    private TextView otherAmtText;
    private TextView transTypeText;
    private TextView transResultText;
    private TextView cardNoText;
    private TextView cardNoTitleText;
    private TextView kernelTypeText;
    private TextView aidText;
    private TextView aidTitleText;
    private TextView aipText;
    private TextView aipTitleText;
    private TextView secondGACTvrText;
    private TextView firstGACTvrText;
    private TextView secondGACCidText;
    private TextView firstGACCidText;
    private TextView terminalCapText;
    private TextView terminalCapTitleText;
    private TextView secondGACAcTypeText;
    private TextView firstGACAcTypeText;
    private TextView cvmTypeText;
    private TextView cvmListText;
    private TextView cvmListTitleText;
    private TextView secondGACTsiText;
    private TextView firstGACTsiText;
    private TextView atcText;

    private ImageView timeRecordImg;
    private EditText tagEdit;
    private LinearLayout getDataLayout;
    private LinearLayout emvTagLayout;
    private LinearLayout secondGACLayout;
    private RelativeLayout kernelTypeLayout;
    private ScrollView resultSV;
    private int currentTxnType = TXN_TYPE_ICC;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trans_result);
        initView();
        initData();
    }

    private void initView() {
        transAmtText = findViewById(R.id.tv_trans_amt);
        otherAmtText = findViewById(R.id.tv_other_amt);
        transTypeText = findViewById(R.id.tv_trans_type);
        transResultText = findViewById(R.id.tv_trans_result);
        cardNoText = findViewById(R.id.tv_card_no);
        cardNoTitleText = findViewById(R.id.tv_card_no_title);
        kernelTypeText = findViewById(R.id.tv_kernel_type);
        aidText = findViewById(R.id.tv_aid);
        aidTitleText = findViewById(R.id.tv_aid_title);
        aipText = findViewById(R.id.tv_aip);
        aipTitleText = findViewById(R.id.tv_aip_title);
        secondGACTvrText = findViewById(R.id.tv_2nd_tvr);
        firstGACTvrText = findViewById(R.id.tv_1st_tvr);
        secondGACCidText = findViewById(R.id.tv_2nd_cid);
        firstGACCidText = findViewById(R.id.tv_1st_cid);
        terminalCapText = findViewById(R.id.tv_ter_cap);
        terminalCapTitleText = findViewById(R.id.tv_ter_cap_title);
        secondGACAcTypeText = findViewById(R.id.tv_2nd_ac_type);
        firstGACAcTypeText = findViewById(R.id.tv_1st_ac_type);
        cvmTypeText = findViewById(R.id.tv_cvm_type);
        cvmListText = findViewById(R.id.tv_cvm_list);
        cvmListTitleText = findViewById(R.id.tv_cvm_list_title);
        secondGACTsiText = findViewById(R.id.tv_2nd_tsi);
        firstGACTsiText = findViewById(R.id.tv_1st_tsi);
        timeRecordImg = findViewById(R.id.image_time_record);
        atcText = findViewById(R.id.tv_atc);
        tagEdit = findViewById(R.id.edit_tag);
        getDataLayout = findViewById(R.id.layout_result);
        emvTagLayout = findViewById(R.id.layout_emv_tag);
        resultSV = findViewById(R.id.sv_result);
        secondGACLayout = findViewById(R.id.layout_2nd_GAC);
        kernelTypeLayout = findViewById(R.id.layout_kernel_type);
        findViewById(R.id.btn_get_data).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addTlvView();
            }
        });
        timeRecordImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TransResultActivity.this, ApduTimeRecordActivity.class);
                TransResultActivity.this.startActivity(intent);
            }
        });
    }


    private void addTlvView() {
        String tag = tagEdit.getText().toString();
        if (TextUtils.isEmpty(tag)) {
            ToastUtils.showToast(TransResultActivity.this, "Tag invalid");
            return;
        }
        View tlvView = getLayoutInflater().inflate(R.layout.item_get_data, null);
        TextView tlvT = tlvView.findViewById(R.id.tv_tlv_t);
        tlvT.setText(tag);
        TextView tlvV = tlvView.findViewById(R.id.tv_tlv_v);
        tlvV.setTextIsSelectable(true);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dp2px(36));
        layoutParams.leftMargin = dp2px(8);
        layoutParams.rightMargin = dp2px(8);
        ApplicationClass.getApp().runInBackground(new Runnable() {
            @Override
            public void run() {
                String tvlVal = getTagVal(new BigInteger(tag, 16).intValue());
                ApplicationClass.getApp().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tlvV.setText(tvlVal);
                        int childCnt = getDataLayout.getChildCount();
                        LogUtils.i(TAG, "child count:" + childCnt);
                        getDataLayout.addView(tlvView, childCnt, layoutParams);
                        resultSV.post(new Runnable() {
                            @Override
                            public void run() {
                                resultSV.fullScroll(ScrollView.FOCUS_DOWN);//scroll to bottom
                            }
                        });

                    }
                });
            }
        });

    }

    private int dp2px(float dpValue) {
        return ScreenUtils.dp2px(TransResultActivity.this, dpValue);
    }


    private String getTagVal(int tag) {
        ByteArray byteArray = new ByteArray();
        byte[] tempArr;
        int ret = 0;
        if(currentTxnType == TXN_TYPE_ICC) {
            ret = EmvProcess.getInstance().getTlv(tag, byteArray);
        }else if(currentTxnType == TXN_TYPE_PICC){
            ret = ClssProcess.getInstance().getTlv(tag, byteArray);
        }
        LogUtils.d(TAG, "getTagVal, ret:" + ret + ", T:" + decimalToHex(tag) + ",L:" + byteArray.length + ", V:" + ConvertHelper.getConvert().bcdToStr(byteArray.data));
        if (tag == 0x9F03) {
            byteArray.length = 6;
        } else if (tag == 0x9C) {
            return getTransTypeByCode(byteArray.data[0]);
        } else {
            if (ret != RetCode.EMV_OK) {
                return "";
            }
        }
        tempArr = new byte[byteArray.length];
        System.arraycopy(byteArray.data, 0, tempArr, 0, byteArray.length);
        return ConvertHelper.getConvert().bcdToStr(tempArr);
    }


    private void initData() {
        Bundle resultBundle = getIntent().getExtras();
        if (resultBundle == null) {
            return;
        }
        String transResult = resultBundle.getString(TransProcessActivity.EXTRA_TRANS_RESULT);
        int transResultCode = resultBundle.getInt(TransProcessActivity.EXTRA_TRANS_RESULT_CODE, 0);
        String cvmResult = resultBundle.getString(TransProcessActivity.EXTRA_CVM_RESULT);
        String firstGACTVR = resultBundle.getString(TransProcessActivity.EXTRA_1STGAC_TVR);
        String firstGACTSI = resultBundle.getString(TransProcessActivity.EXTRA_1STGAC_TSI);
        String firstGACCID = resultBundle.getString(TransProcessActivity.EXTRA_1STGAC_CID);
        currentTxnType = resultBundle.getInt(TransProcessActivity.EXTRA_CURRENT_TXN_TYPE);
        boolean isOnlineAprovedNo2GAC = resultBundle.getBoolean(TransProcessActivity.EXTRA_IS_ONLINE_APPROVE_WITHOUT_2GAC);
        firstGACTvrText.setText(firstGACTVR);
        firstGACTsiText.setText(firstGACTSI);
        firstGACCidText.setText(firstGACCID);
        cvmTypeText.setText(cvmResult.substring(4).replace("_", " "));
        firstGACAcTypeText.setText("(" + getAcType(firstGACCID) + ")");
        transResultText.setText(transResult.substring(7).replace("_", " ") + "(" + transResultCode + ")");
        if (isOnlineAprovedNo2GAC || TransResultEnum.RESULT_REQ_ONLINE.name().equals(transResult) || TransResultEnum.RESULT_OFFLINE_APPROVED.name().equals(transResult) || TransResultEnum.RESULT_OFFLINE_DENIED.name().equals(transResult)) {
            secondGACLayout.setVisibility(View.GONE);
        }
        if (currentTxnType == TXN_TYPE_PICC) {
            kernelTypeLayout.setVisibility(View.VISIBLE);
            kernelTypeText.setText(getKernelType());
            timeRecordImg.setVisibility(View.VISIBLE);
        }
        ApplicationClass.getApp().runInBackground(new Runnable() {
            @Override
            public void run() {
                getTLVData();
            }
        });

    }


    private String getKernelType() {
        String kernelTypeStr = "";
        KernType kernType = ClssProcess.getInstance().getKernType();
        switch (kernType.kernType) {
            case KernType.KERNTYPE_AE:
                kernelTypeStr = "AE";
                break;
            case KernType.KERNTYPE_VIS:
                kernelTypeStr = "VISA";
                break;
            case KernType.KERNTYPE_MC:
                kernelTypeStr = "MC";
                break;
            case KernType.KERNTYPE_ZIP:
                kernelTypeStr = "DPAS";
                break;
            case KernType.KERNTYPE_JCB:
                kernelTypeStr = "JCB";
                break;
            case KernType.KERNTYPE_RUPAY:
                kernelTypeStr = "RUPAY";
                break;
            case KernType.KERNTYPE_PURE:
                kernelTypeStr = "PURE";
                break;
            case KernType.KERNTYPE_PBOC:
                kernelTypeStr = "PBOC";
                break;
            case KernType.KERNTYPE_FLASH:
                kernelTypeStr = "FLASH";
                break;
        }
        return kernelTypeStr;
    }

    private String getAcType(String cid) {
        if ("40".equals(cid)) {
            return "TC";
        } else if ("00".equals(cid)) {
            return "AAC";
        } else if ("80".equals(cid)) {
            return "ARQC";
        }
        return "";

    }

    private String getTransTypeByCode(byte code) {
        String transTypeStr = "Sale(00)";
        switch (code) {
            case 0x00:
                transTypeStr = "Sale(00)";//Goods/Service
                break;
            case 0x20:
                transTypeStr = "Refund(20)";//
                break;
            case 0x30:
                transTypeStr = "Inquiry(30)";//Balance Inquiry
                break;
            case 0x09:
                transTypeStr = "CashBack(09)";//CashBack
                break;
            case 0x01:
                transTypeStr = "Cash(01)";//Cash
                break;
            default:
                transTypeStr = "Other(" + ConvertHelper.getConvert().bcdToStr(new byte[]{code}) + ")";
                break;
        }
        return transTypeStr;
    }

    private void getTLVData() {
        int tag;
        ArrayList<String> keyList = new ArrayList<>();
        ArrayList<String> valueList = new ArrayList<>();
        for (int i = 0; i < EMV_TAG.length; i++) {
            tag = EMV_TAG[i];
            String val = getTagVal(tag);
            String key = TAG_TITLE[i] + "(" + Integer.toHexString(tag).toUpperCase() + ")";
            LogUtils.w(TAG, "key:" + key + ", val:" + val);
            keyList.add(i, key);
            valueList.add(i, val);
        }
        ApplicationClass.getApp().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                transTypeText.setText(valueList.get(0));
                transAmtText.setText(CurrencyConverter.convert(Long.parseLong(valueList.get(1))));
                otherAmtText.setText(CurrencyConverter.convert(Long.parseLong(valueList.get(2))));
                cardNoText.setText(valueList.get(3));
                cardNoTitleText.setText(keyList.get(3));
                aidText.setText(valueList.get(4));
                aidTitleText.setText(keyList.get(4));
                aipText.setText(valueList.get(5));
                aipTitleText.setText(keyList.get(5));
                String cidVal = valueList.get(6);
                secondGACCidText.setText(cidVal);
                secondGACTsiText.setText(valueList.get(7));
                secondGACTvrText.setText(valueList.get(8));
                secondGACAcTypeText.setText("(" + getAcType(cidVal) + ")");

                if(currentTxnType == TXN_TYPE_ICC) {
                    emvTagLayout.setVisibility(View.VISIBLE);
                    terminalCapText.setText(valueList.get(9));
                    terminalCapTitleText.setText(keyList.get(9));
                    cvmListText.setText(valueList.get(10));
                    cvmListTitleText.setText(keyList.get(10));
                    atcText.setText(valueList.get(11));
                }

            }
        });
    }

    private String decimalToHex(int decimal) {
        String hex = "";
        while (decimal != 0) {
            int hexValue = decimal % 16;
            hex = toHexChar(hexValue) + hex;
            decimal = decimal / 16;
        }
        return hex;
    }

    private char toHexChar(int hexValue) {
        if (hexValue <= 9 && hexValue >= 0)
            return (char) (hexValue + '0');
        else
            return (char) (hexValue - 10 + 'A');
    }

}

package com.paguelofacil.posfacil.pax;

import static com.paguelofacil.posfacil.pax.MainActivity.TXN_TYPE_ICC;
import static com.paguelofacil.posfacil.pax.MainActivity.TXN_TYPE_PICC;

import android.view.View;
import android.widget.TextView;

import com.paguelofacil.posfacil.ApplicationClass;
import com.paguelofacil.posfacil.pax.util.CurrencyConverter;
import com.pax.commonlib.utils.LogUtils;
import com.pax.commonlib.utils.convert.ConvertHelper;
import com.pax.jemv.clcommon.ByteArray;
import com.pax.jemv.clcommon.KernType;
import com.pax.jemv.clcommon.RetCode;
import com.paxsz.module.emv.process.contact.EmvProcess;
import com.paxsz.module.emv.process.contactless.ClssProcess;

import java.util.ArrayList;

public class PaxUtil {
    private int currentTxnType = TXN_TYPE_ICC;
    private static final int[] EMV_TAG = {0x9C, 0x9F02, 0x9F03, 0x5A, 0x4F, 0x82, 0x9F27, 0x9B, 0x95, 0x9F33, 0x8E, 0x9F36};
    private static final String[] TAG_TITLE = {"TransType", "TransAmount", "OtherAmount", "Card No.", "AID", "AIP", "CID", "TSI", "TVR", "Terminal Capabilities", "CVM List", "ATC"};

    byte parseIntToByte(int data) {
        return (byte) data;
    }

    Boolean getVisibilityKernel() {
        return currentTxnType == MainActivity.TXN_TYPE_PICC;
    }

    String getTagVal(int tag) {
        ByteArray byteArray = new ByteArray();
        byte[] tempArr;
        int ret = 0;
        if (currentTxnType == TXN_TYPE_ICC) {
            ret = EmvProcess.getInstance().getTlv(tag, byteArray);
        } else if (currentTxnType == TXN_TYPE_PICC) {
            ret = ClssProcess.getInstance().getTlv(tag, byteArray);
        }
        LogUtils.d("TAG", "getTagVal, ret:" + ret + ", T:" + decimalToHex(tag) + ",L:" + byteArray.length + ", V:" + ConvertHelper.getConvert().bcdToStr(byteArray.data));
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

    String getDataCard() {
        int tag;
        ArrayList<String> keyList = new ArrayList<>();
        ArrayList<String> valueList = new ArrayList<>();
        for (int i = 0; i < EMV_TAG.length; i++) {
            tag = EMV_TAG[i];
            String val = getTagVal(tag);
            String key = TAG_TITLE[i] + "(" + Integer.toHexString(tag).toUpperCase() + ")";
            LogUtils.w("TAG", "key:" + key + ", val:" + val);
            keyList.add(i, key);
            valueList.add(i, val);
        }
        return  valueList.get(3);
    }
}

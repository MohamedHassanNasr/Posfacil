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
 * 20200518  	         JackHuang               Create
 * ===========================================================================================
 */
package com.paxsz.module.emv.xmlparam.parser.pull;

import android.text.TextUtils;
import android.util.Xml;

import com.pax.commonlib.utils.LogUtils;
import com.pax.commonlib.utils.convert.ConvertHelper;
import com.pax.commonlib.utils.convert.IConvert;
import com.paxsz.module.emv.xmlparam.entity.clss.PayPassAid;
import com.paxsz.module.emv.xmlparam.parser.IXmlParser;
import com.paxsz.module.emv.xmlparam.parser.ParseException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import static com.paxsz.module.emv.xmlparam.parser.ParseException.CODE_FILE_OPEN_ERR;
import static com.paxsz.module.emv.xmlparam.parser.ParseException.CODE_NODE_NOT_FOUND;
import static com.paxsz.module.emv.xmlparam.parser.ParseException.CODE_RAW_NAME_EMPTY;

import androidx.annotation.NonNull;

public class PaypassPullParser implements IXmlParser<ArrayList<PayPassAid>> {
    private static final String TAG = "PaypassPullParser";

    private IConvert convert = ConvertHelper.getConvert();

    @Override
    public ArrayList<PayPassAid> parse(@NonNull InputStream inputStream) throws ParseException {
        if (inputStream == null) {
            throw new ParseException(ParseException.CODE_COMMON_ERROR, "InputStream is null");
        }
        ArrayList<PayPassAid> payPassAidArrayList = null;
        PayPassAid payPassAid = null;
        XmlPullParser parser = Xml.newPullParser();

        try {
            parser.setInput(inputStream, "UTF-8");
        } catch (XmlPullParserException e) {
            LogUtils.e(TAG, e);
            throw new ParseException(CODE_FILE_OPEN_ERR, "Set input stream fail");
        }

        int eventType = 0;
        try {
            eventType = parser.getEventType();
        } catch (Exception e) {
            throw new ParseException(ParseException.CODE_COMMON_ERROR, "Event type exception in paypass file", e);
        }

        while (eventType != XmlPullParser.END_DOCUMENT) {
            switch (eventType) {
                case XmlPullParser.START_DOCUMENT:
                    break;

                case XmlPullParser.START_TAG:
                    String rawStartName = parser.getName();
                    String startName = "";
                    if (!TextUtils.isEmpty(rawStartName)) {
                        startName = rawStartName.replaceAll(" ", "");
                    }

                    if(TextUtils.equals("AIDLIST", startName)){
                        payPassAidArrayList = new ArrayList<>();
                    }

                    if(payPassAidArrayList != null){
                        if (TextUtils.equals("AID", startName)) {
                            payPassAid = new PayPassAid();
                        }

                        if(payPassAid != null){
                            try {
                                parsePayPassAid(parser, payPassAid);
                            }catch (ParseException e) {
                                throw e;
                            } catch (Exception e) {
                                throw new ParseException(ParseException.CODE_COMMON_ERROR, "Parse CLSS PayPassAid failed", e);
                            }
                        }
                    }

                    break;

                case XmlPullParser.END_TAG:
                    String rawEndName = parser.getName();
                    String endName = "";
                    if (!TextUtils.isEmpty(rawEndName)) {
                        endName = rawEndName.replaceAll(" ", "");
                    }
                    if(payPassAidArrayList != null) {
                        if (TextUtils.equals("AID", endName)) {
                            payPassAidArrayList.add(payPassAid);
                            payPassAid = null;
                        }
                    }
                    break;
                default:
                    break;
            }

            try {
                eventType = parser.next();
            } catch (Exception e) {
                LogUtils.e(TAG, e);
            }
        }
        return payPassAidArrayList;
    }

    private void parsePayPassAid(XmlPullParser parser, PayPassAid payPassAid) throws IOException, XmlPullParserException,ParseException {
        String rawName  = parser.getName();
        if (TextUtils.isEmpty(rawName)) {
            throw new ParseException(CODE_RAW_NAME_EMPTY, "parser get name err");
        }
        String name = rawName.replaceAll(" ", "");
        if (TextUtils.isEmpty(name)) {
            throw new ParseException(CODE_RAW_NAME_EMPTY, "parser get name err");
        }

        switch (name) {
            case "LocalAIDName":
                payPassAid.setLocalAidName(parser.nextText());
                break;

            case "ApplicationID":
                payPassAid.setApplicationId(convert.strToBcd(parser.nextText(), IConvert.EPaddingPosition.PADDING_RIGHT));
                break;

            case "PartialAIDSelection":
                payPassAid.setPartialAIDSelection(convert.strToBcd(parser.nextText(), IConvert.EPaddingPosition.PADDING_LEFT)[0]);
                break;

            case "TerminalAIDVersion":
                payPassAid.setTerminalAidVersion(convert.strToBcd(parser.nextText(), IConvert.EPaddingPosition.PADDING_RIGHT));
                break;

            case "TACDenial":
                payPassAid.setTacDenial(convert.strToBcd(parser.nextText(), IConvert.EPaddingPosition.PADDING_RIGHT));
                break;

            case "TACOnline":
                payPassAid.setTacOnline(convert.strToBcd(parser.nextText(), IConvert.EPaddingPosition.PADDING_RIGHT));
                break;

            case "TACDefault":
                payPassAid.setTacDefault(convert.strToBcd(parser.nextText(), IConvert.EPaddingPosition.PADDING_RIGHT));
                break;

            case "TerminalRisk":
                payPassAid.setTerminalRisk(convert.strToBcd(parser.nextText(), IConvert.EPaddingPosition.PADDING_RIGHT));
                break;

            case "ContactlessCVMLimit":
                payPassAid.setContactlessCvmLimit(Long.parseLong(parser.nextText()));
                break;

            case "ContactlessTransactionLimit_NoOnDevice":
                payPassAid.setContactlessTransactionLimitNoOnDevice(Long.parseLong(parser.nextText()));
                break;

            case "ContactlessTransactionLimit_OnDevice":
                payPassAid.setContactlessTransactionLimitOnDevice(Long.parseLong(parser.nextText()));
                break;

            case "ContactlessFloorLimit":
                payPassAid.setContactlessFloorLimit(Long.parseLong(parser.nextText()));
                break;

            case "ContactlessTransactionLimitSupported":
                payPassAid.setContactlessTransactionLimitSupported(convert.strToBcd(parser.nextText(), IConvert.EPaddingPosition.PADDING_LEFT)[0]);
                break;

            case "CVMLimitSupported":
                payPassAid.setContactlessCvmLimitSupported(convert.strToBcd(parser.nextText(), IConvert.EPaddingPosition.PADDING_LEFT)[0]);
                break;

            case "ContactlessFloorLimitSupported":
                payPassAid.setContactlessFloorLimitSupported(convert.strToBcd(parser.nextText(), IConvert.EPaddingPosition.PADDING_LEFT)[0]);
                break;

            case "KernelConfiguration":
                payPassAid.setKernelConfiguration(convert.strToBcd(parser.nextText(), IConvert.EPaddingPosition.PADDING_LEFT)[0]);
                break;

            case "TornLeftTime":
                payPassAid.setTornLeftTime(convert.strToBcd(parser.nextText(), IConvert.EPaddingPosition.PADDING_LEFT)[0]);
                break;

            case "MaximumTornNumber":
                payPassAid.setMaximumTornNumber(convert.strToBcd(parser.nextText(), IConvert.EPaddingPosition.PADDING_LEFT)[0]);
                break;

            case "CardDataInput":
                payPassAid.setCardDataInput(convert.strToBcd(parser.nextText(), IConvert.EPaddingPosition.PADDING_LEFT)[0]);
                break;

            case "MagneticCVM":
                payPassAid.setMagneticCvm(convert.strToBcd(parser.nextText(), IConvert.EPaddingPosition.PADDING_LEFT)[0]);
                break;

            case "MageticNoCVM":
                payPassAid.setMageticNoCvm(convert.strToBcd(parser.nextText(), IConvert.EPaddingPosition.PADDING_LEFT)[0]);
                break;

            case "CVMCapability_CVMRequired":
                payPassAid.setCvmCapabilityCvmRequired(convert.strToBcd(parser.nextText(), IConvert.EPaddingPosition.PADDING_LEFT)[0]);
                break;

            case "CVMCapability_NoCVMRequired":
                payPassAid.setCvmCapabilityNoCvmRequired(convert.strToBcd(parser.nextText(), IConvert.EPaddingPosition.PADDING_LEFT)[0]);
                break;

            case "SecurityCapability":
                payPassAid.setSecurityCapability(convert.strToBcd(parser.nextText(), IConvert.EPaddingPosition.PADDING_LEFT)[0]);
                break;

            case "AdditionalTerminalCapability":
                payPassAid.setAdditionalTerminalCapability(convert.strToBcd(parser.nextText(), IConvert.EPaddingPosition.PADDING_RIGHT));
                break;

            case "KernelID":
                payPassAid.setKernelId(convert.strToBcd(parser.nextText(), IConvert.EPaddingPosition.PADDING_LEFT)[0]);
                break;

            case "TerminalType":
                payPassAid.setTerminalType(convert.strToBcd(parser.nextText(), IConvert.EPaddingPosition.PADDING_LEFT)[0]);
                break;
            case "AID":
                break;

            default:
                throw new ParseException(CODE_NODE_NOT_FOUND, "node " + name + " not exist");
        }
    }
}

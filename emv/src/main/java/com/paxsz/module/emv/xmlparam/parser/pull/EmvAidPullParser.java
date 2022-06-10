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

import android.text.TextUtils;
import android.util.Xml;

import com.pax.commonlib.utils.LogUtils;
import com.pax.commonlib.utils.convert.ConvertHelper;
import com.pax.commonlib.utils.convert.IConvert;
import com.paxsz.module.emv.xmlparam.entity.contact.EmvAid;
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

public class EmvAidPullParser implements IXmlParser<ArrayList<EmvAid>> {
    private static final String TAG = "EmvAidPullParser";
    private IConvert convert = ConvertHelper.getConvert();

    @Override
    public ArrayList<EmvAid> parse(@NonNull InputStream inputStream) throws ParseException {
        if (inputStream == null) {
            throw new ParseException(ParseException.CODE_COMMON_ERROR, "InputStream is null");
        }

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
            throw new ParseException(ParseException.CODE_COMMON_ERROR, "Event type exception in emvAid file", e);
        }

        ArrayList<EmvAid> emvAidList = null;
        EmvAid emvAid = null;

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

                    if (TextUtils.equals("AIDLIST", startName)) {
                        emvAidList = new ArrayList<EmvAid>();
                    }

                    if (emvAidList != null) {
                        if (TextUtils.equals("AID", startName)) {
                            emvAid = new EmvAid();
                        }

                        if (emvAid != null) {
                            try {
                                parseEMVAidElements(parser, emvAid);
                            } catch (ParseException e) {
                                throw e;
                            } catch (Exception e) {
                                throw new ParseException(ParseException.CODE_COMMON_ERROR, "Parse Emv AID param failed", e);
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
                    if (emvAidList != null) {
                        if (TextUtils.equals("AID", endName)) {
                            emvAidList.add(emvAid);
                            emvAid = null;
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

        return emvAidList;
    }

    private void parseEMVAidElements(XmlPullParser parser, EmvAid emvAid) throws IOException, XmlPullParserException, ParseException {
        String rawName = parser.getName();

        if (TextUtils.isEmpty(rawName)) {
            throw new ParseException(CODE_RAW_NAME_EMPTY, "parser get name err");
        }

        String name = rawName.replaceAll(" ", "");
        if (TextUtils.isEmpty(name)) {
            throw new ParseException(CODE_RAW_NAME_EMPTY, "parser get name err");
        }


        switch (name) {
            case "PartialAIDSelection":
                emvAid.setPartialAIDSelection(convert.strToBcd(parser.nextText(), IConvert.EPaddingPosition.PADDING_LEFT)[0]);
                break;

            case "ApplicationID":
                emvAid.setApplicationID(convert.strToBcd(parser.nextText(), IConvert.EPaddingPosition.PADDING_RIGHT));
                break;

            case "LocalAIDName":
                emvAid.setLocalAIDName(parser.nextText());
                break;

            case "TACDenial":
                emvAid.setTacDenial(convert.strToBcd(parser.nextText(), IConvert.EPaddingPosition.PADDING_RIGHT));
                break;

            case "TACOnline":
                emvAid.setTacOnline(convert.strToBcd(parser.nextText(), IConvert.EPaddingPosition.PADDING_RIGHT));
                break;

            case "TACDefault":
                emvAid.setTacDefault(convert.strToBcd(parser.nextText(), IConvert.EPaddingPosition.PADDING_RIGHT));
                break;

            case "TerminalAIDVersion":
                emvAid.setTerminalAIDVersion(convert.strToBcd(parser.nextText(), IConvert.EPaddingPosition.PADDING_RIGHT));
                break;

            case "FloorLimit":
                emvAid.setFloorLimit(Long.parseLong(parser.nextText()));
                break;

            case "Threshold":
                emvAid.setThreshold(Long.parseLong(parser.nextText()));
                break;

            case "TargetPercentage":
                emvAid.setTargetPercentage(convert.strToBcd(parser.nextText(), IConvert.EPaddingPosition.PADDING_LEFT)[0]);
                break;
            case "MaxTargetPercentage":
                emvAid.setMaxTargetPercentage(convert.strToBcd(parser.nextText(), IConvert.EPaddingPosition.PADDING_LEFT)[0]);
                break;
            case "TerminalDefaultTDOL":
                emvAid.setTerminalDefaultTDOL(convert.strToBcd(parser.nextText(), IConvert.EPaddingPosition.PADDING_RIGHT));
                break;
            case "TerminalDefaultDDOL":
                emvAid.setTerminalDefaultDDOL(convert.strToBcd(parser.nextText(), IConvert.EPaddingPosition.PADDING_RIGHT));
                break;
            case "TerminalRiskManagementData":
                emvAid.setTerminalRiskManagementData(convert.strToBcd(parser.nextText(), IConvert.EPaddingPosition.PADDING_RIGHT));
                break;
            case "TerminalType":
                emvAid.setTerminalType(convert.strToBcd(parser.nextText(), IConvert.EPaddingPosition.PADDING_LEFT)[0]);
                break;
            case "CardDataInputCapability":
                emvAid.setCardDataInputCapability(convert.strToBcd(parser.nextText(), IConvert.EPaddingPosition.PADDING_LEFT)[0]);
                break;
            case "CVMCapability":
                emvAid.setCvmCapability(convert.strToBcd(parser.nextText(), IConvert.EPaddingPosition.PADDING_LEFT)[0]);
                break;
            case "SecurityCapability":
                emvAid.setSecurityCapability(convert.strToBcd(parser.nextText(), IConvert.EPaddingPosition.PADDING_LEFT)[0]);
                break;
            case "AdditionalTerminalCapabilities":
                emvAid.setAdditionalTerminalCapabilities(convert.strToBcd(parser.nextText(), IConvert.EPaddingPosition.PADDING_RIGHT));
                break;
            case "GetDataForPINTryCounter":
                emvAid.setGetDataForPINTryCounter(Integer.parseInt(parser.nextText()));
                break;
            case "BypassPINEntry":
                emvAid.setBypassPINEntry(Integer.parseInt(parser.nextText()));
                break;
            case "SubsequentBypassPINEntry":
                emvAid.setSubsequentBypassPINEntry(Integer.parseInt(parser.nextText()));
                break;
            case "ForcedOnlineCapability":
                emvAid.setForcedOnlineCapability(Integer.parseInt(parser.nextText()));
                break;
            case "AID":
                break;
            default:
                throw new ParseException(CODE_NODE_NOT_FOUND, "node " + name + " not exist");
        }
    }
}

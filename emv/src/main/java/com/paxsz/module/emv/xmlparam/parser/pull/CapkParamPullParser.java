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
import com.paxsz.module.emv.xmlparam.entity.common.Capk;
import com.paxsz.module.emv.xmlparam.entity.common.CapkParam;
import com.paxsz.module.emv.xmlparam.entity.common.CapkRevoke;
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

public class CapkParamPullParser implements IXmlParser<CapkParam> {
    private static final String TAG = "PaypassPullParser";
    private IConvert convert = ConvertHelper.getConvert();

    @Override
    public CapkParam parse(@NonNull InputStream inputStream) throws ParseException {
        if (inputStream == null) {
            throw new ParseException(ParseException.CODE_COMMON_ERROR, "InputStream is null");
        }
        CapkParam capkParam = new CapkParam();
        ArrayList<Capk> capkArrayList = null;
        ArrayList<CapkRevoke> revokeList = null;
        Capk capk = null;
        CapkRevoke revoke = null;
        XmlPullParser parser = Xml.newPullParser();

        try {
            parser.setInput(inputStream, "UTF-8");
        } catch (XmlPullParserException e) {
            LogUtils.e(TAG, e);
            throw new ParseException(CODE_FILE_OPEN_ERR, "set input stream fail");
        }

        int eventType = 0;
        try {
            eventType = parser.getEventType();
        } catch (Exception e) {
            throw new ParseException(ParseException.CODE_COMMON_ERROR, "Event type exception in capk file", e);
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

                    if (TextUtils.equals("CAPKLIST", startName)) {
                        capkArrayList = new ArrayList<>();
                    }

                    if (TextUtils.equals("REVOCATIONLIST", startName)) {
                        revokeList = new ArrayList<>();
                    }
                    if (capkArrayList != null) {
                        if (TextUtils.equals("CAPK", startName)) {
                            capk = new Capk();
                        }

                        if (capk != null) {
                            try {
                                parseCapkElements(parser, capk);
                            } catch (ParseException e) {
                                throw e;
                            } catch (Exception e) {
                                throw new ParseException(ParseException.CODE_COMMON_ERROR, "Parse capk failed", e);
                            }
                        }
                    }
                    if (revokeList != null) {
                        if (TextUtils.equals("REVOKEDCERTIFICATE", startName)) {
                            revoke = new CapkRevoke();
                        }

                        if (revoke != null) {
                            try {
                                parseRevokeElements(parser, revoke);
                            } catch (ParseException e) {
                                throw e;
                            } catch (Exception e) {
                                throw new ParseException(ParseException.CODE_COMMON_ERROR, "Parse capk revoke failed", e);
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
                    if (capkArrayList != null) {
                        if (TextUtils.equals("CAPK", endName)) {
                            capkArrayList.add(capk);
                            capk = null;
                        }
                    }
                    if (TextUtils.equals("CAPKLIST", endName)) {
                        capkParam.setCapkList(capkArrayList);
                    }

                    if (revokeList != null) {
                        if (TextUtils.equals("REVOKEDCERTIFICATE", endName)) {
                            revokeList.add(revoke);
                            revoke = null;
                        }
                    }
                    if (TextUtils.equals("REVOCATIONLIST", endName)) {
                        capkParam.setCapkRevokeList(revokeList);
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
        return capkParam;
    }

    private void parseCapkElements(XmlPullParser parser, Capk capk) throws IOException, XmlPullParserException, ParseException {
        String rawName = parser.getName();
        if (TextUtils.isEmpty(rawName)) {
            throw new ParseException(CODE_RAW_NAME_EMPTY, "parser get name err");
        }

        String name = rawName.replaceAll(" ", "");
        if (TextUtils.isEmpty(name)) {
            throw new ParseException(CODE_RAW_NAME_EMPTY, "parser get name err");
        }

        switch (name) {
            case "RID":
                capk.setRid(convert.strToBcd(parser.nextText(), IConvert.EPaddingPosition.PADDING_RIGHT));
                break;

            case "KeyID":
                capk.setKeyId(convert.strToBcd(parser.nextText(), IConvert.EPaddingPosition.PADDING_LEFT)[0]);
                break;

            case "HashArithmeticIndex":
                capk.setHashArithmeticIndex(convert.strToBcd(parser.nextText(), IConvert.EPaddingPosition.PADDING_LEFT)[0]);
                break;

            case "RSAArithmeticIndex":
                capk.setRsaArithmeticIndex(convert.strToBcd(parser.nextText(), IConvert.EPaddingPosition.PADDING_LEFT)[0]);
                break;

            case "ModuleLength":
                capk.setModuleLength(Integer.parseInt(parser.nextText()));
                break;

            case "Module":
                capk.setModule(convert.strToBcd(parser.nextText(), IConvert.EPaddingPosition.PADDING_RIGHT));
                break;

            case "ExponentLength":
                capk.setExponentLength(convert.strToBcd(parser.nextText(), IConvert.EPaddingPosition.PADDING_LEFT)[0]);
                break;

            case "Exponent":
                capk.setExponent(convert.strToBcd(parser.nextText(), IConvert.EPaddingPosition.PADDING_RIGHT));
                break;

            case "ExpireDate":
                capk.setExpireDate(convert.strToBcd(parser.nextText(), IConvert.EPaddingPosition.PADDING_RIGHT));
                break;

            case "CheckSum":
                capk.setCheckSum(convert.strToBcd(parser.nextText(), IConvert.EPaddingPosition.PADDING_RIGHT));
                break;
            case "CAPK":
                break;

            default:
                throw new ParseException(CODE_NODE_NOT_FOUND, "node " + name + " not exist");
        }
    }

    private void parseRevokeElements(XmlPullParser parser, CapkRevoke revoke) throws IOException, XmlPullParserException, ParseException {
        String rawName = parser.getName();
        if (TextUtils.isEmpty(rawName)) {
            throw new ParseException(CODE_RAW_NAME_EMPTY, "parser get name err");
        }

        String name = rawName.replaceAll(" ", "");
        if (TextUtils.isEmpty(name)) {
            throw new ParseException(CODE_RAW_NAME_EMPTY, "parser get name err");
        }

        switch (name) {
            case "RID":
                revoke.setRid(convert.strToBcd(parser.nextText(), IConvert.EPaddingPosition.PADDING_RIGHT));
                break;

            case "KeyID":
                revoke.setKeyId(convert.strToBcd(parser.nextText(), IConvert.EPaddingPosition.PADDING_LEFT)[0]);
                break;
            case "CertificateSN":
                revoke.setCertificateSN(convert.strToBcd(parser.nextText(), IConvert.EPaddingPosition.PADDING_RIGHT));
                break;
            case "REVOKEDCERTIFICATE":
                break;
            default:
                throw new ParseException(CODE_NODE_NOT_FOUND, "node " + name + " not exist");
        }
    }

}

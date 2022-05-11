package com.paxsz.module.emv.xmlparam.parser.pull;

import android.text.TextUtils;
import android.util.Xml;

import com.pax.commonlib.utils.LogUtils;
import com.pax.commonlib.utils.convert.ConvertHelper;
import com.pax.commonlib.utils.convert.IConvert;
import com.paxsz.module.emv.xmlparam.entity.common.Config;
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

public class ConfigPullParser implements IXmlParser<Config> {
    private static final String TAG = "ConfigPullParser";
    private IConvert convert = ConvertHelper.getConvert();
    @Override
    public Config parse(@NonNull InputStream inputStream) throws ParseException {
        if (inputStream == null) {
            throw new ParseException(ParseException.CODE_COMMON_ERROR, "InputStream is null");
        }
        Config config = null;
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

                    if(TextUtils.equals("TERMINALCONFIGURATION", startName)){
                        config = new Config();
                    }

                    if(config != null){
                        try {
                            parseConfig(parser, config);
                        } catch (ParseException e) {
                            throw e;
                        } catch (Exception e) {
                            throw new ParseException(ParseException.CODE_COMMON_ERROR, "Parse capk failed", e);
                        }
                    }
                    break;

                case XmlPullParser.END_TAG:
                    String rawEndName = parser.getName();
                    String endName = "";
                    if (!TextUtils.isEmpty(rawEndName)) {
                        endName = rawEndName.replaceAll(" ", "");
                    }

                    if(TextUtils.equals("TERMINALCONFIGURATION", endName)){
                      return config;
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
        return config;
    }

    private void parseConfig(XmlPullParser parser, Config config) throws IOException, XmlPullParserException, ParseException {
        String rawName  = parser.getName();
        if (TextUtils.isEmpty(rawName)) {
            throw new ParseException(CODE_RAW_NAME_EMPTY, "parser get name err");
        }

        String name = rawName.replaceAll(" ", "");
        if (TextUtils.isEmpty(name)) {
            throw new ParseException(CODE_RAW_NAME_EMPTY, "parser get name err");
        }

        switch (name) {
            case "MerchantID":
                config.setMerchantId(parser.nextText());
                break;
            case "MerchantCategoryCode":
                config.setMerchantCategoryCode(convert.strToBcd(parser.nextText(), IConvert.EPaddingPosition.PADDING_RIGHT));
                break;

            case "MerchantNameAndLocation":
                config.setMerchantNameAndLocation(parser.nextText());
                break;
            case "TerminalCountryCode":
                config.setTerminalCountryCode(convert.strToBcd(parser.nextText(), IConvert.EPaddingPosition.PADDING_RIGHT));
                break;

            case "TerminalCurrencySymbol":
                config.setTerminalCurrencySymbol(parser.nextText());
                break;

            case "TransactionReferenceCurrencyCode":
                config.setTransReferenceCurrencyCode(convert.strToBcd(parser.nextText(), IConvert.EPaddingPosition.PADDING_RIGHT));
                break;

            case "TransactionReferenceCurrencyExponent":
                config.setTransReferenceCurrencyExponent(convert.strToBcd(parser.nextText(), IConvert.EPaddingPosition.PADDING_LEFT)[0]);
                break;

            case "ConversionRatio":
                config.setConversionRatio(Long.parseLong(parser.nextText()));
                break;
            case "TERMINALCONFIGURATION":
                break;
            default:
                throw new ParseException(CODE_NODE_NOT_FOUND, "node " + name + " not exist");
        }
    }
}

/*
 * ===========================================================================================
 * = COPYRIGHT
 *          PAX Computer Technology(Shenzhen) CO., LTD PROPRIETARY INFORMATION
 *   This software is supplied under the terms of a license agreement or nondisclosure
 *   agreement with PAX Computer Technology(Shenzhen) CO., LTD and may not be copied or
 *   disclosed except in accordance with the terms in that agreement.
 *     Copyright (C) 2019-? PAX Computer Technology(Shenzhen) CO., LTD All rights reserved.
 * Description: // Detail description about the function of this module,
 *             // interfaces with the other modules, and dependencies.
 * Revision History:
 * Date                  Author	                 Action
 * 20190108  	         Kim.L                   Create
 * ===========================================================================================
 */
package com.paguelofacil.posfacil.pax.util;


import android.os.Build;

import com.pax.commonlib.utils.LogUtils;
import com.pax.commonlib.utils.convert.ConvertHelper;
import com.pax.commonlib.utils.convert.IConvert;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Currency;
import java.util.Locale;

public class CurrencyConverter {

    private static final String TAG = "CurrencyConv";

    private static Locale defLocale = Locale.US;


    private CurrencyConverter() {
        //do nothing
    }


    public static Locale getDefCurrency() {
        return defLocale;
    }

    /**
     * @param amount
     * @return
     */
    public static String convert(long amount) {
        return convert(amount, defLocale);
    }

    /**
     * @param amount
     * @param locale
     * @return
     */
    public static String convert(long amount, Locale locale) {
        Currency currency = Currency.getInstance(locale);
        NumberFormat formatter = NumberFormat.getCurrencyInstance(locale);
        formatter.setMinimumFractionDigits(currency.getDefaultFractionDigits());
        formatter.setMaximumFractionDigits(currency.getDefaultFractionDigits());
        long newAmount = amount < 0 ? -amount : amount; // AET-58
        String prefix = amount < 0 ? "-" : "";
        try {
            double amt = Double.valueOf(newAmount) / (Math.pow(10, currency.getDefaultFractionDigits()));
            return prefix + formatter.format(amt);
        } catch (IllegalArgumentException e) {
            LogUtils.e(TAG, "", e);
        }
        return "";
    }

    public static Long parse(String formatterAmount) {
        return parse(formatterAmount, defLocale);
    }

    public static Long parse(String formatterAmount, Locale locale) {
        Currency currency = Currency.getInstance(locale);
        NumberFormat formatter = NumberFormat.getCurrencyInstance(locale);
        formatter.setMinimumFractionDigits(currency.getDefaultFractionDigits());
        formatter.setMaximumFractionDigits(currency.getDefaultFractionDigits());
        try {
            Number num = formatter.parse(formatterAmount);

            return Math.round(num.doubleValue() * Math.pow(10, currency.getDefaultFractionDigits()));
        } catch (ParseException | NumberFormatException e) {
            LogUtils.e(TAG, "", e);
        }
        return 0L;
    }

    public static byte[] getCurrencyCode() {
        Currency currency = Currency.getInstance(defLocale);
        String currencyCode = currency.getCurrencyCode();
        LogUtils.i(TAG, "currency symbol:" + currency.getSymbol() + ", Currency code:" + currencyCode + ",fraction:" + currency.getDefaultFractionDigits());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            LogUtils.d(TAG, ",numeric code:" + currency.getNumericCode());
            return ConvertHelper.getConvert().intToByteArray(currency.getNumericCode(), IConvert.EEndian.LITTLE_ENDIAN);
        }
        return new byte[]{0x08, 0x40};
    }


    public static int getCurrencyFraction() {
        Currency currency = Currency.getInstance(defLocale);
        return currency.getDefaultFractionDigits();
    }
}

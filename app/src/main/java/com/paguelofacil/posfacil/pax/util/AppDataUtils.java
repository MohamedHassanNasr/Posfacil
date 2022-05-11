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
 *  2020/05/29 	         Qinny Zhou           	      Create
 *  ===========================================================================================
 */

package com.paguelofacil.posfacil.pax.util;


import com.paguelofacil.posfacil.ApplicationClass;
import com.pax.commonlib.utils.LogUtils;
import com.pax.dal.entity.ETermInfoKey;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class AppDataUtils {
    private static final String TAG = "AppDataUtils";

    public static String getSN() {
        String terminalSN = "";
        Map<ETermInfoKey, String> map = ApplicationClass.getApp().getDal().getSys().getTermInfo();
        if (map != null) {
            terminalSN = map.get(ETermInfoKey.SN);
        }
        LogUtils.i(TAG, "Terminal SN : " + terminalSN);
        return terminalSN;
    }


    public static String getCurrDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyMMdd");
        return formatter.format(new Date());

    }

    public static String getCurrTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("HHmmss");
        return formatter.format(new Date());

    }
}

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
 * 20200619  	         JackHuang               Create
 * ===========================================================================================
 */
package com.paguelofacil.posfacil.pax.util;

import com.paguelofacil.posfacil.pax.trans.timerecord.entity.ApduTimeRecord;
import com.pax.commonlib.utils.LogUtils;
import com.pax.commonlib.utils.convert.ConvertHelper;

import java.util.ArrayList;


public class TimeRecordUtils {
    private static ArrayList<ApduTimeRecord> apduTimeRecords = new ArrayList<>();

    private TimeRecordUtils(){}

    public static ArrayList<ApduTimeRecord> getApduTimeRecords(){
        return apduTimeRecords;
    }

    public static void addApduTimeRecords(ApduTimeRecord record){
        apduTimeRecords.add(record);
    }

    public static void clearTimeRecordList(){
        apduTimeRecords.clear();
    }

    public static String getCommandString(byte [] command){
        //get CLA and INS
        String cmd = ConvertHelper.getConvert().bcdToStr(command, 2);
        LogUtils.d("huangwp", "");
        switch (cmd){
            case "00A4":
                return "Select";
            case "80A8":
                return "Get Processing Options";
            case "00B2":
                return "Read Record";
            case "80AE":
                return "Generate AC";
            case "802A":
                return "Compute Cryptographic Checksum";
            case "80EA":
                return "Exchange Relay Resistance Data";
            case "80CA":
                return "Get Data";
            case "80DA":
                return "Put Data";
            default:
                return "Unknown Command";
        }
    }
}

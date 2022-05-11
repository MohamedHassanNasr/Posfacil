/*
 * ===========================================================================================
 * = COPYRIGHT
 *          PAX Computer Technology(Shenzhen) CO., LTD PROPRIETARY INFORMATION
 *   This software is supplied under the terms of a license agreement or nondisclosure
 *   agreement with PAX Computer Technology(Shenzhen) CO., LTD and may not be copied or
 *   disclosed except in accordance with the terms in that agreement.
 *     Copyright (C) $YEAR-? PAX Computer Technology(Shenzhen) CO., LTD All rights reserved.
 * Description: // Detail description about the function of this module,
 *             // interfaces with the other modules, and dependencies.
 * Revision History:
 * Date	                 Author	                Action
 * 2020/5/18  	         Qinny Zhou           	Create/Add/Modify/Delete
 * ===========================================================================================
 */
package com.paguelofacil.posfacil.pax.util;

import com.pax.commonlib.utils.convert.ConvertHelper;

import java.nio.ByteBuffer;

public class CardInfoUtils {

    /**
     * check if it's IC card from track2
     *
     * @param track2 input track2 data
     * @return true/false
     */
    public static boolean isIcCard(String track2) {
        if (track2 == null)
            return false;

        int index = track2.indexOf('=');
        if (index < 0) {
            index = track2.indexOf('D');
            if (index < 0)
                return false;
        }

        if (index + 6 > track2.length())
            return false;

        return "2".equals(track2.substring(index + 5, index + 6)) || "6".equals(track2.substring(index + 5, index + 6));
    }

    /**
     * get account from track2
     *
     * @param track2 input track2 data
     * @return account no
     */
    public static String getPan(String track2) {
        if (track2 == null)
            return null;

        int len = track2.indexOf('=');
        if (len < 0) {
            len = track2.indexOf('D');
            if (len < 0)
                return null;
        }

        if ((len < 13) || (len > 19))
            return null;
        return track2.substring(0, len);
    }
    /**
     * get expiry data from track2
     *
     * @param track2 input track2 data
     * @return expiry data
     */
    public static String getExpDate(String track2) {
        if (track2 == null)
            return null;

        int index = track2.indexOf('=');
        if (index < 0) {
            index = track2.indexOf('D');
            if (index < 0)
                return null;
        }

        if (index + 5 > track2.length())
            return null;
        return track2.substring(index + 1, index + 5);
    }


    public static final int X9_8_WITH_PAN = 0;
    public static final int X9_8_NO_PAN = 1;

    /**
     * shift pan value
     *
     * @param pan  input pan data
     * @param mode {@link CardInfoUtils#X9_8_NO_PAN} or {@link CardInfoUtils#X9_8_WITH_PAN}
     * @return shifted pan value
     */
    public static String getPanBlock(String pan, int mode) {
        String panBlock = null;
        if (pan == null || pan.length() < 13 || pan.length() > 19) {
            return null;
        }
        switch (mode) {
            case X9_8_WITH_PAN:
                panBlock = "0000" + pan.substring(pan.length() - 13, pan.length() - 1);
                break;
            case X9_8_NO_PAN:
                panBlock = "0000000000000000";
                break;

            default:
                break;
        }

        return panBlock;
    }

    /**
     * @param tag57 emv tag57
     * @return track2
     */
    public static String getTrack2FromTag57(byte[] tag57) {
        String strTrack2 = ConvertHelper.getConvert().bcdToStr(tag57);
        return strTrack2.split("F")[0];
    }

    public static byte[] combine7172(byte[] f71, byte[] f72) {
        if (f71 == null || f71.length == 0)
            return f72;
        if (f72 == null || f72.length == 0)
            return f71;

        ByteBuffer bb = ByteBuffer.allocate(f71.length + f72.length + 6);

        bb.put((byte) 0x71);
        if (f71.length > 127)
            bb.put((byte) 0x81);
        bb.put((byte) f71.length);
        bb.put(f71, 0, f71.length);

        bb.put((byte) 0x72);
        if (f72.length > 127)
            bb.put((byte) 0x81);
        bb.put((byte) f72.length);
        bb.put(f72, 0, f72.length);

        int len = bb.position();
        bb.position(0);

        byte[] script = new byte[len];
        bb.get(script, 0, len);

        return script;
    }



}

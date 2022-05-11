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
 *  2020/05/19 	         Qinny Zhou           	Create/Add/Modify/Delete
 *  ===========================================================================================
 */

package com.paguelofacil.posfacil.pax.util;

import com.paguelofacil.posfacil.ApplicationClass;
import com.paguelofacil.posfacil.pax.manager.AppDataManager;
import com.pax.dal.IPed;
import com.pax.dal.entity.ECheckMode;
import com.pax.dal.entity.EPedKeyType;
import com.pax.dal.entity.EPedType;
import com.pax.dal.entity.EPinBlockMode;
import com.pax.dal.exceptions.PedDevException;

public class PedApiUtils {
    private static final byte[] TEST_TPK = new byte[]{0x33, 0x33, 0x33, 0x33, 0x33, 0x33, 0x33, 0x33, 0x33, 0x33, 0x33, 0x33, 0x33, 0x33, 0x33, 0x33};

    /**
     * calculate PIN block
     *
     * @param panBlock shifted pan block
     * @return PIN block
     * @throws PedDevException exception
     */
    public static byte[] getPinBlock(String panBlock, int timeout) throws PedDevException {
        boolean supportBypass = true;
        IPed ped = ApplicationClass.getApp().getDal().getPed(EPedType.INTERNAL);
        String pinLen = "4,5,6,7,8,9,10,11,12";
        if (supportBypass) {
            pinLen = "0," + pinLen;
        }
        ped.setKeyboardLayoutLandscape(false);
        byte tpkIndex = (byte) AppDataManager.getInstance().getInt(AppDataManager.TPK_INDEX, 2);
        return ped.getPinBlock(tpkIndex, pinLen, panBlock.getBytes(), EPinBlockMode.ISO9564_0, timeout);
    }

    /**
     * write TPK
     * @throws PedDevException exception
     */
    public static void writeTPK(byte tpkIndex) throws PedDevException {
        ECheckMode checkMode = ECheckMode.KCV_NONE;
        ApplicationClass.getApp().getDal().getPed(EPedType.INTERNAL).writeKey(EPedKeyType.TMK, (byte) 0x00,
                EPedKeyType.TPK, tpkIndex, TEST_TPK, checkMode, null);

    }

    public static void eraseKey() throws PedDevException {
        ApplicationClass.getApp().getDal().getPed(EPedType.INTERNAL).erase();
    }
}

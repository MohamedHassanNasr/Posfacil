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
 * 2020/5/15  	         Qinny Zhou           	Create/Add/Modify/Delete
 * ===========================================================================================
 */
package com.paxsz.module.pos;

import android.content.Context;

import com.pax.dal.ICardReaderHelper;
import com.pax.dal.ICashDrawer;
import com.pax.dal.IDAL;
import com.pax.dal.IDalCommManager;
import com.pax.dal.IDeviceInfo;
import com.pax.dal.IFingerprintReader;
import com.pax.dal.IIDReader;
import com.pax.dal.IIDReaderEx;
import com.pax.dal.IIcc;
import com.pax.dal.IKeyBoard;
import com.pax.dal.IMag;
import com.pax.dal.IPed;
import com.pax.dal.IPedBg;
import com.pax.dal.IPedNp;
import com.pax.dal.IPedTrSys;
import com.pax.dal.IPhoneManager;
import com.pax.dal.IPicc;
import com.pax.dal.IPrinter;
import com.pax.dal.IPuk;
import com.pax.dal.IScanCodec;
import com.pax.dal.IScanner;
import com.pax.dal.IScannerHw;
import com.pax.dal.ISignPad;
import com.pax.dal.ISys;
import com.pax.dal.IWifiProbe;
import com.pax.dal.entity.EPedType;
import com.pax.dal.entity.EPiccType;
import com.pax.dal.entity.EScannerType;
import com.pax.dal.pedkeyisolation.IPedKeyIsolation;
import com.paxsz.module.pos.icc.PosApiIcc;
import com.paxsz.module.pos.mag.PosApiMag;
import com.paxsz.module.pos.ped.PosApiPed;
import com.paxsz.module.pos.picc.PosApiPicc;

public class PosApiDal implements IDAL {
    private Context mContext;

    public PosApiDal(Context context) {
        this.mContext = context;
    }

    @Override
    public IMag getMag() {
        return new PosApiMag();
    }

    @Override
    public IIcc getIcc() {
        return new PosApiIcc();
    }

    @Override
    public IPuk getPuk() {
        return null;
    }

    @Override
    public IPicc getPicc(EPiccType ePiccType) {
        if (ePiccType == EPiccType.INTERNAL) {
            return new PosApiPicc();
        } else {
            return null;
        }

    }

    @Override
    public IPrinter getPrinter() {
        return null;
    }

    @Override
    public IPed getPed(EPedType ePedType) {
        if (ePedType == EPedType.INTERNAL) {
            return new PosApiPed();
        }
        return null;
    }

    @Override
    public ISys getSys() {
        return null;
    }

    @Override
    public IKeyBoard getKeyBoard() {
        return null;
    }

    @Override
    public IScanner getScanner(EScannerType eScannerType) {
        return null;
    }

    @Override
    public IScannerHw getScannerHw() {
        return null;
    }

    @Override
    public IDalCommManager getCommManager() {
        return null;
    }

    @Override
    public ISignPad getSignPad() {
        return null;
    }

    @Override
    public ICardReaderHelper getCardReaderHelper() {
        return null;
    }

    @Override
    public IPedTrSys getPedTrSys() {
        return null;
    }

    @Override
    public IPedNp getPedNp() {
        return null;
    }

    @Override
    public IPedBg getPedBg() {
        return null;
    }

    @Override
    public IDeviceInfo getDeviceInfo() {
        return null;
    }

    @Override
    public IIDReader getIDReader() {
        return null;
    }

    @Override
    public IPedKeyIsolation getPedKeyIsolation(EPedType ePedType) {
        return null;
    }

    @Override
    public ICashDrawer getCashDrawer() {
        return null;
    }

    @Override
    public IScanCodec getScanCodec() {
        return null;
    }

    @Override
    public IWifiProbe getWifiProbe() {
        return null;
    }

    @Override
    public IPhoneManager getPhoneManager() {
        return null;
    }

    @Override
    public IIDReaderEx getIDReaderEx() {
        return null;
    }

    @Override
    public IFingerprintReader getFingerprintReader() {
        return null;
    }
}

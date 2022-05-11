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
 * 2020/4/30  	         Qinny Zhou           	Create/Add/Modify/Delete
 * ===========================================================================================
 */
package com.paxsz.module.pos.ped;

import android.view.View;

import com.pax.api.PedException;
import com.pax.api.PedManager;
import com.pax.dal.IPed;
import com.pax.dal.entity.DUKPTResult;
import com.pax.dal.entity.EAesCheckMode;
import com.pax.dal.entity.ECheckMode;
import com.pax.dal.entity.ECryptOperate;
import com.pax.dal.entity.ECryptOpt;
import com.pax.dal.entity.EDUKPTDesMode;
import com.pax.dal.entity.EDUKPTMacMode;
import com.pax.dal.entity.EDUKPTPinMode;
import com.pax.dal.entity.EFuncKeyMode;
import com.pax.dal.entity.EIdKeycCalcMode;
import com.pax.dal.entity.EPedDesMode;
import com.pax.dal.entity.EPedKeyType;
import com.pax.dal.entity.EPedMacMode;
import com.pax.dal.entity.EPinBlockMode;
import com.pax.dal.entity.EUartPort;
import com.pax.dal.entity.RSAKeyInfo;
import com.pax.dal.entity.RSAPinKey;
import com.pax.dal.entity.RSARecoverInfo;
import com.pax.dal.entity.SM2KeyPair;
import com.pax.dal.exceptions.PedDevException;

import java.util.LinkedHashMap;

public class PosApiPed implements IPed {
    private PedManager pedManager;
    public PosApiPed(){
        try {
            pedManager = PedManager.getInstance();
        } catch (PedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setInputPinListener(IPedInputPinListener iPedInputPinListener) {

    }

    @Override
    public void writeKey(EPedKeyType ePedKeyType, byte b, EPedKeyType ePedKeyType1, byte b1, byte[] bytes, ECheckMode eCheckMode, byte[] bytes1) throws PedDevException {

    }

    @Override
    public void writeTIK(byte b, byte b1, byte[] bytes, byte[] bytes1, ECheckMode eCheckMode, byte[] bytes2) throws PedDevException {

    }

    @Override
    public byte[] getPinBlock(byte b, String s, byte[] bytes, EPinBlockMode ePinBlockMode, int i) throws PedDevException {
        return new byte[0];
    }

    @Override
    public byte[] getMac(byte b, byte[] bytes, EPedMacMode ePedMacMode) throws PedDevException {
        return new byte[0];
    }

    @Override
    public byte[] calcDes(byte b, byte[] bytes, EPedDesMode ePedDesMode) throws PedDevException {
        return new byte[0];
    }

    @Override
    public byte[] calcDes(byte b, byte[] bytes, byte[] bytes1, byte b1) throws PedDevException {
        return new byte[0];
    }

    @Override
    public DUKPTResult getDUKPTPin(byte b, String s, byte[] bytes, EDUKPTPinMode edukptPinMode, int i) throws PedDevException {
        return null;
    }

    @Override
    public DUKPTResult getDUKPTMac(byte b, byte[] bytes, EDUKPTMacMode edukptMacMode) throws PedDevException {
        return null;
    }

    @Override
    public byte[] getKCV(EPedKeyType ePedKeyType, byte b, byte b1, byte[] bytes) throws PedDevException {
        return new byte[0];
    }

    @Override
    public void writeKeyVar(EPedKeyType ePedKeyType, byte b, byte b1, byte[] bytes, ECheckMode eCheckMode, byte[] bytes1) throws PedDevException {

    }

    @Override
    public String getVersion() throws PedDevException {
        return null;
    }

    @Override
    public boolean erase() throws PedDevException {
        return false;
    }

    @Override
    public void setIntervalTime(int i, int i1) throws PedDevException {

    }

    @Override
    public void setFunctionKey(EFuncKeyMode eFuncKeyMode) throws PedDevException {

    }

    @Override
    public void writeRSAKey(byte b, RSAKeyInfo rsaKeyInfo) throws PedDevException {

    }

    @Override
    public RSARecoverInfo RSARecover(byte b, byte[] bytes) throws PedDevException {
        return null;
    }

    @Override
    public DUKPTResult calcDUKPTDes(byte b, byte b1, byte[] bytes, byte[] bytes1, EDUKPTDesMode edukptDesMode) throws PedDevException {
        return null;
    }

    @Override
    public byte[] getDUKPTKsn(byte b) throws PedDevException {
        return new byte[0];
    }

    @Override
    public void incDUKPTKsn(byte b) throws PedDevException {

    }

    @Override
    public byte[] verifyPlainPin(byte b, String s, byte b1, int i) throws PedDevException {
        return new byte[0];
    }

    @Override
    public byte[] verifyCipherPin(byte b, String s, RSAPinKey rsaPinKey, byte b1, int i) throws PedDevException {
        return new byte[0];
    }

    @Override
    public void setExMode(int i) {

    }

    @Override
    public void clearScreen() throws PedDevException {

    }

    @Override
    public String inputStr(byte b, byte b1, byte b2, int i) throws PedDevException {
        return null;
    }

    @Override
    public void showStr(byte b, byte b1, String s) throws PedDevException {

    }

    @Override
    public String getSN() throws PedDevException {
        return null;
    }

    @Override
    public void showInputBox(boolean b, String s) throws PedDevException {

    }

    @Override
    public SM2KeyPair genSM2KeyPair(int i) throws PedDevException {
        return null;
    }

    @Override
    public void writeSM2CipherKey(EPedKeyType ePedKeyType, byte b, EPedKeyType ePedKeyType1, byte b1, byte[] bytes) throws PedDevException {

    }

    @Override
    public void writeSM2Key(byte b, EPedKeyType ePedKeyType, byte[] bytes) throws PedDevException {

    }

    @Override
    public byte[] SM2Recover(byte b, byte[] bytes, ECryptOperate eCryptOperate) throws PedDevException {
        return new byte[0];
    }

    @Override
    public byte[] SM2Sign(byte b, byte b1, byte[] bytes, byte[] bytes1) throws PedDevException {
        return new byte[0];
    }

    @Override
    public void SM2Verify(byte b, byte[] bytes, byte[] bytes1, byte[] bytes2) throws PedDevException {

    }

    @Override
    public byte[] SM3(byte[] bytes, byte b) throws PedDevException {
        return new byte[0];
    }

    @Override
    public byte[] SM4(byte b, byte[] bytes, byte[] bytes1, ECryptOperate eCryptOperate, ECryptOpt eCryptOpt) throws PedDevException {
        return new byte[0];
    }

    @Override
    public byte[] getMacSM(byte b, byte[] bytes, byte[] bytes1, byte b1) throws PedDevException {
        return new byte[0];
    }

    @Override
    public byte[] getPinBlockSM4(byte b, String s, byte[] bytes, EPinBlockMode ePinBlockMode, int i) throws PedDevException {
        return new byte[0];
    }

    @Override
    public void cancelInput() throws PedDevException {

    }

    @Override
    public void setAmount(String s) throws PedDevException {

    }

    @Override
    public byte[] idKeyCalc(byte b, byte[] bytes, byte[] bytes1, EIdKeycCalcMode eIdKeycCalcMode) throws PedDevException {
        return new byte[0];
    }

    @Override
    public void setKeyboardLayoutLandscape(boolean b) throws PedDevException {

    }

    @Override
    public byte[] setKeyBoardLayout(boolean b, String s) throws PedDevException {
        return new byte[0];
    }

    @Override
    public void writeAesKey(EPedKeyType ePedKeyType, byte b, byte b1, byte[] bytes, EAesCheckMode eAesCheckMode, byte[] bytes1) throws PedDevException {

    }

    @Override
    public byte[] calcAes(byte b, byte[] bytes, byte[] bytes1, ECryptOperate eCryptOperate, ECryptOpt eCryptOpt) throws PedDevException {
        return new byte[0];
    }

    @Override
    public void setKeyboardRandom(boolean b) throws PedDevException {

    }

    @Override
    public void genRSAKey(byte b, byte b1, short i, byte b2) throws PedDevException {

    }

    @Override
    public void setPort(EUartPort eUartPort) {

    }

    @Override
    public byte[] getPinBlock(byte b, String s, byte[] bytes, byte b1, int i) throws PedDevException {
        return new byte[0];
    }

    @Override
    public RSAKeyInfo readRSAKey(byte b) throws PedDevException {
        return null;
    }

    @Override
    public void setFunctionKey(byte b) throws PedDevException {

    }

    @Override
    public DUKPTResult getDUKPTPin(byte b, String s, byte[] bytes, Boolean aBoolean, String s1, String s2, int i) throws PedDevException {
        return null;
    }

    @Override
    public void setKeyBoardType(int i) throws PedDevException {

    }

    @Override
    public int getKeyBoardType() throws PedDevException {
        return 0;
    }

    @Override
    public byte[] getPinBlock(byte b, String s, byte[] bytes, byte b1, int i, int i1) throws PedDevException {
        return new byte[0];
    }

    @Override
    public byte[] verifyPlainPin(byte b, String s, byte b1, int i, int i1) throws PedDevException {
        return new byte[0];
    }

    @Override
    public byte[] verifyCipherPin(byte b, String s, RSAPinKey rsaPinKey, byte b1, int i, int i1) throws PedDevException {
        return new byte[0];
    }

    @Override
    public byte[] setKeyBoardLayout(boolean b, LinkedHashMap<View, String> linkedHashMap) throws PedDevException {
        return new byte[0];
    }

    @Override
    public void writeTIK(byte b, byte b1, byte b2, byte[] bytes) throws PedDevException {

    }

    @Override
    public void writeKeyEx(EPedKeyType ePedKeyType, byte b, EPedKeyType ePedKeyType1, byte b1, byte[] bytes, ECheckMode eCheckMode, byte[] bytes1, byte[] bytes2, byte b2) throws PedDevException {

    }

    @Override
    public byte[] readPaxCA(byte b) throws PedDevException {
        return new byte[0];
    }

    @Override
    public void writeAesKey(byte b, byte b1, byte b2, byte b3, byte[] bytes, EAesCheckMode eAesCheckMode, byte[] bytes1) throws PedDevException {

    }

    @Override
    public DUKPTResult calcDUKPTData(byte b, byte b1, byte[] bytes, byte[] bytes1, byte b2) throws PedDevException {
        return null;
    }

    @Override
    public DUKPTResult getDUKPTMac(byte b, byte[] bytes, byte b1) throws PedDevException {
        return null;
    }

    @Override
    public void eraseKeyEx(byte b) throws PedDevException {

    }

    @Override
    public byte[] challengeWICKey(byte b, byte b1, byte[] bytes, byte[] bytes1) throws PedDevException {
        return new byte[0];
    }
}

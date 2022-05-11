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
package com.paxsz.module.pos.picc;

import com.pax.api.PiccException;
import com.pax.api.PiccManager;
import com.pax.dal.IPicc;
import com.pax.dal.entity.ApduRespInfo;
import com.pax.dal.entity.ApduSendInfo;
import com.pax.dal.entity.EDetectMode;
import com.pax.dal.entity.EM1KeyType;
import com.pax.dal.entity.EM1OperateType;
import com.pax.dal.entity.EPiccRemoveMode;
import com.pax.dal.entity.EUartPort;
import com.pax.dal.entity.PiccCardInfo;
import com.pax.dal.entity.PiccPara;
import com.pax.dal.exceptions.PiccDevException;

public class PosApiPicc implements IPicc {

    private PiccManager piccManager;

    public PosApiPicc() {
        try {
            piccManager = PiccManager.getInstance();
        } catch (PiccException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void open() throws PiccDevException {
        if (piccManager != null) {
            try {
                piccManager.piccOpen();
            } catch (PiccException e) {
                throw new PiccDevException(e.exceptionCode, e.getMessage());
            }
        }
    }

    @Override
    public PiccPara readParam() throws PiccDevException {
        return null;
    }

    @Override
    public void setParam(PiccPara piccPara) throws PiccDevException {

    }

    @Override
    public void setFelicaTimeOut(long l) throws PiccDevException {

    }

    @Override
    public PiccCardInfo detect(EDetectMode eDetectMode) throws PiccDevException {
        PiccCardInfo piccCardInfo = new PiccCardInfo();
        if (piccManager != null) {
            try {
                PiccManager.PiccCardInfo cardInfo = piccManager.piccDetect(eDetectMode.getDetectMode());
                if (cardInfo != null) {
                    piccCardInfo.setCardType(cardInfo.CardType);
                    piccCardInfo.setCID(cardInfo.CID);
                    piccCardInfo.setSerialInfo(cardInfo.SerialInfo);
                    piccCardInfo.setOther(cardInfo.Other);
                }
            } catch (PiccException e) {
                throw new PiccDevException(e.exceptionCode, e.getMessage());
            }
        }
        return piccCardInfo;

    }

    @Override
    public PiccCardInfo detect(byte b) throws PiccDevException {
        PiccCardInfo piccCardInfo = new PiccCardInfo();
        if (piccManager != null) {
            try {
                PiccManager.PiccCardInfo cardInfo = piccManager.piccDetect(b);
                if (cardInfo != null) {
                    piccCardInfo.setCardType(cardInfo.CardType);
                    piccCardInfo.setCID(cardInfo.CID);
                    piccCardInfo.setSerialInfo(cardInfo.SerialInfo);
                    piccCardInfo.setOther(cardInfo.Other);
                }
            } catch (PiccException e) {
                throw new PiccDevException(e.exceptionCode, e.getMessage());
            }
        }
        return piccCardInfo;
    }

    @Override
    public byte[] isoCommand(byte b, byte[] bytes) throws PiccDevException {
        return new byte[0];
    }

    @Override
    public void remove(EPiccRemoveMode ePiccRemoveMode, byte b) throws PiccDevException {

    }

    @Override
    public void close() throws PiccDevException {

    }

    @Override
    public void m1Auth(EM1KeyType em1KeyType, byte b, byte[] bytes, byte[] bytes1) throws PiccDevException {

    }

    @Override
    public byte[] m1Read(byte b) throws PiccDevException {
        return new byte[0];
    }

    @Override
    public void m1Write(byte b, byte[] bytes) throws PiccDevException {

    }

    @Override
    public void m1Operate(EM1OperateType em1OperateType, byte b, byte[] bytes, byte b1) throws PiccDevException {

    }

    @Override
    public void initFelica(byte b, byte b1) throws PiccDevException {

    }

    @Override
    public void setLed(byte b) throws PiccDevException {

    }

    @Override
    public ApduRespInfo isoCommandByApdu(byte b, ApduSendInfo apduSendInfo) throws PiccDevException {
        return null;
    }

    @Override
    public byte[] cmdExchange(byte[] bytes, int i) throws PiccDevException {
        return new byte[0];
    }

    @Override
    public void setPort(EUartPort eUartPort) {

    }

    @Override
    public void setFelicaTimeout(int i) throws PiccDevException {

    }
}

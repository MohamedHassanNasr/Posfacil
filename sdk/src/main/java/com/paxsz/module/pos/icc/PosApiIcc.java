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
package com.paxsz.module.pos.icc;

import com.pax.api.IccException;
import com.pax.api.IccManager;
import com.pax.api.model.APDU_RESP;
import com.pax.api.model.APDU_SEND;
import com.pax.api.model.ICC_PRAR;
import com.pax.dal.IIcc;
import com.pax.dal.entity.ApduRespInfo;
import com.pax.dal.entity.ApduSendInfo;
import com.pax.dal.entity.IccPara;
import com.pax.dal.exceptions.IccDevException;
import com.pax.dal.memorycard.ICardAT24Cxx;
import com.pax.dal.memorycard.ICardAT88SC102;
import com.pax.dal.memorycard.ICardAT88SC153;
import com.pax.dal.memorycard.ICardAT88SC1608;
import com.pax.dal.memorycard.ICardSle4428;
import com.pax.dal.memorycard.ICardSle4442;

public class PosApiIcc implements IIcc {
    private IccManager iccManager;

    public PosApiIcc() {
        try {
            iccManager = IccManager.getInstance();
        } catch (IccException e) {
            e.printStackTrace();
        }
    }

    @Override
    public byte[] init(byte b) throws IccDevException {
        if (iccManager != null) {
            try {
                return iccManager.iccInit(b);
            } catch (IccException e) {
                throw new IccDevException(e.exceptionCode, e.getMessage());
            }
        }
        return new byte[0];
    }

    @Override
    public void close(byte b) throws IccDevException {
        if (iccManager != null) {
            try {
                iccManager.iccClose(b);
            } catch (IccException e) {
                throw new IccDevException(e.exceptionCode, e.getMessage());
            }
        }
    }

    @Override
    public void autoResp(byte b, boolean b1) throws IccDevException {
        if (iccManager != null) {
            try {
                iccManager.iccAutoResp(b, b1 ? (byte) 0x01 : (byte) 0x00);
            } catch (IccException e) {
                throw new IccDevException(e.exceptionCode, e.getMessage());
            }
        }
    }

    @Override
    public byte[] isoCommand(byte b, byte[] bytes) throws IccDevException {
        if (iccManager != null) {
            try {
                APDU_SEND apdu_send = new APDU_SEND();
                apdu_send.serialFromBuffer(bytes);
                APDU_RESP apdu_resp = iccManager.iccIsoCommand(b, apdu_send);
                if (apdu_resp != null) {
                    return apdu_resp.serialToBuffer();
                }
            } catch (IccException e) {
                throw new IccDevException(e.exceptionCode, e.getMessage());
            }
        }
        return new byte[0];
    }

    @Override
    public boolean detect(byte b) throws IccDevException {
        if (iccManager != null) {
            try {
                return iccManager.iccDetect(b);
            } catch (IccException e) {
                throw new IccDevException(e.exceptionCode, e.getMessage());
            }
        }
        return false;
    }

    @Override
    public void light(boolean b) throws IccDevException {
        if (iccManager != null) {
            try {
                iccManager.iccLight(b ? (byte) 0x01 : (byte) 0x00);
            } catch (IccException e) {
                throw new IccDevException(e.exceptionCode, e.getMessage());
            }
        }
    }

    @Override
    public ApduRespInfo isoCommandByApdu(byte b, ApduSendInfo apduSendInfo) throws IccDevException {
        ApduRespInfo apduRespInfo = new ApduRespInfo();
        if (iccManager != null && apduSendInfo != null) {
            try {
                APDU_SEND apdu_send = new APDU_SEND();
                System.arraycopy(apduSendInfo.getCommand(), 0, apdu_send.Command, 0, apduSendInfo.getCommand().length);
                apdu_send.Lc = (short) apduSendInfo.getLc();
                apdu_send.Le = (short) apduSendInfo.getLe();
                System.arraycopy(apduSendInfo.getDataIn(), 0, apdu_send.DataIn, 0, apduSendInfo.getDataIn().length);
                APDU_RESP apdu_resp = iccManager.iccIsoCommand(b, apdu_send);
                if (apdu_resp != null) {
                    apduRespInfo.setSwA(apdu_resp.SWA);
                    apduRespInfo.setSwB(apdu_resp.SWB);
                    apduRespInfo.setLenOut(apdu_resp.LenOut);
                    apduRespInfo.setDataOut(apdu_resp.DataOut);
                }
            } catch (IccException e) {
                throw new IccDevException(e.exceptionCode, e.getMessage());
            }
        }
        return apduRespInfo;
    }

    @Override
    public ICardAT24Cxx getCardAT24Cxx() {
        return null;
    }

    @Override
    public ICardAT88SC102 getCardAT88SC102() {
        return null;
    }

    @Override
    public ICardAT88SC153 getCardAT88SC153() {
        return null;
    }

    @Override
    public ICardSle4428 getCardSle4428() {
        return null;
    }

    @Override
    public ICardAT88SC1608 getCardAT88SC1608() {
        return null;
    }

    @Override
    public ICardSle4442 getCardSle4442() {
        return null;
    }

    @Override
    public IccPara readParam(byte b) throws IccDevException {
        IccPara iccPara = new IccPara();
        if (iccManager != null) {
            try {
                ICC_PRAR icc_prar = new ICC_PRAR();
                ICC_PRAR rsp = iccManager.iccSetup(b, (byte) 0x52, icc_prar);
                ICC_PRAR tmp = new ICC_PRAR();
                if (rsp != null) {
                    tmp = rsp;
                } else {
                    if (icc_prar != null) {
                        tmp = icc_prar;
                    }
                }
                iccPara.setApdu_format_val(tmp.apdu_format_val);
                iccPara.setApdu_format_w(tmp.apdu_format_w);
                iccPara.setReserved(tmp.reserved);
            } catch (IccException e) {
                throw new IccDevException(e.exceptionCode, e.getMessage());
            }
        }
        return iccPara;
    }

    @Override
    public void setParam(byte b, IccPara iccPara) throws IccDevException {
        if (iccManager != null && iccPara != null) {
            try {
                ICC_PRAR icc_prar = new ICC_PRAR();
                icc_prar.apdu_format_val = iccPara.getApdu_format_val();
                icc_prar.apdu_format_w = iccPara.getApdu_format_w();
                System.arraycopy(iccPara.getReserved(), 0, icc_prar.reserved, 0, iccPara.getReserved().length);
                iccManager.iccSetup(b, (byte) 0x57, icc_prar);
            } catch (IccException e) {
                throw new IccDevException(e.exceptionCode, e.getMessage());
            }
        }
    }
}

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
 *  2020/06/01 	         Qinny Zhou           	      Create
 *  ===========================================================================================
 */
package com.paguelofacil.posfacil.pax.trans.mvp.cardprocess;

import com.paguelofacil.posfacil.pax.trans.mvp.BaseView;
import com.paxsz.module.emv.param.EmvTransParam;
import com.paxsz.module.emv.process.entity.IssuerRspData;
import com.paxsz.module.emv.process.entity.TransResult;

public interface TransProcessContract {

    interface View extends BaseView {

        void onUpdatePinLen(String pin);
        String getEnteredPin();

        void onEnterPinFinish(int pinResult);

        void onStartEnterPin(String prompt);

        void onTransFinish(TransResult transResult);

        void onCompleteTrans(TransResult transResult);

        void onRemoveCard();

        void onReadCardOK();
    }

    interface Presenter {
        void preTrans(EmvTransParam transParam, boolean needContact);

        void startEmvTrans();

        void startClssTrans();

        void startMagTrans();

        void startOnlinePin();

        void completeEmvTrans(IssuerRspData issuerRspData);

        void completeClssTrans(IssuerRspData issuerRspData);
    }


}

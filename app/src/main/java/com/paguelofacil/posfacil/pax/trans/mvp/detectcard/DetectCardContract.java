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
package com.paguelofacil.posfacil.pax.trans.mvp.detectcard;

import com.paguelofacil.posfacil.pax.entity.DetectCardResult;
import com.paguelofacil.posfacil.pax.trans.mvp.BaseView;
import com.pax.dal.entity.EReaderType;

public interface DetectCardContract {

    interface  Model {
        DetectCardResult polling(EReaderType readType);
        void stopPolling();
    }

    interface View extends BaseView {
        void onMagDetectOK(String pan,String expiryDate);

        void onIccDetectOK();

        void onPiccDetectOK();

        void onDetectError(DetectCardResult.ERetCode errorCode);
    }

    interface Presenter {
        void startDetectCard(EReaderType readType);

        void stopDetectCard();


    }


}

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
 * 2020/4/29  	         Qinny Zhou           	Create/Add/Modify/Delete
 * ===========================================================================================
 */
package com.paxsz.module.pos;

import android.content.Context;

import com.pax.dal.IDAL;
import com.pax.neptunelite.api.NeptuneLiteUser;

public class Sdk {
    private static Sdk instance = null;
    private Context context;
    private Sdk(Context mCx) {
        this.context = mCx;
    }

    public static Sdk getInstance(Context context) {
        if (instance == null) {
            instance = new Sdk(context);
        }
        return instance;
    }

    public IDAL getDal(){
        try {
            return NeptuneLiteUser.getInstance().getDal(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public IDAL getDal(int type){
        return new PosApiDal(context);
    }
}

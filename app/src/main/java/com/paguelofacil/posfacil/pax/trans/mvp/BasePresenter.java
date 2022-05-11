package com.paguelofacil.posfacil.pax.trans.mvp;/*
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

import com.paguelofacil.posfacil.pax.trans.mvp.BaseView;

public class BasePresenter<V extends BaseView>{
    protected V mView;

    public void attachView(V view){
        this.mView = view;
    }

    public void detachView(){
        this.mView = null;
    }

    public boolean isViewAttached(){
        return mView != null;
    }
}

/*
 * ============================================================================
 * COPYRIGHT
 *              Pax CORPORATION PROPRIETARY INFORMATION
 *   This software is supplied under the terms of a license agreement or
 *   nondisclosure agreement with Pax Corporation and may not be copied
 *   or disclosed except in accordance with the terms in that agreement.
 *      Copyright (C) 2016 - ? Pax Corporation. All rights reserved.
 * Module Date: 2017-5-19
 * Module Author: linhb
 * Description:
 *
 * ============================================================================
 */
package com.paguelofacil.posfacil.pax.view.keyboard;

import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.paguelofacil.posfacil.ApplicationClass;


public abstract class EditorActionListener implements TextView.OnEditorActionListener {


    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            ApplicationClass.getApp().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    onKeyOk(v);
                }
            });
        } else if (actionId == EditorInfo.IME_ACTION_NONE) {
//            EmvDemoApp.getApp().runOnUiThread(EditorActionListener.this::onKeyCancel);
            v.setText("");
        }
        return false;
    }

    protected abstract void onKeyOk(TextView v);

//    protected abstract void onKeyCancel();
}

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
 *  2020/06/02 	         Qinny Zhou           	      Create
 *  ===========================================================================================
 */

package com.paguelofacil.posfacil.pax.trans.mvp.cardprocess;

import android.app.AlertDialog;
import android.content.DialogInterface;

import com.paguelofacil.posfacil.ApplicationClass;
import com.paguelofacil.posfacil.pax.ActivityStack;
import com.paguelofacil.posfacil.pax.util.TickTimer;
import com.pax.commonlib.utils.LogUtils;
import com.pax.commonlib.utils.convert.ConvertHelper;
import com.pax.jemv.clcommon.RetCode;
import com.paxsz.module.emv.process.contact.CandidateAID;

import java.util.List;

public class AppSelectTask {
    private static final String TAG = "AppSelectTask";

    private AppSelectListener mAppSelectListener;
    private AlertDialog appSelectDlg;

    public AppSelectTask() {

    }

    public void registerAppSelectListener(AppSelectListener listener) {
        this.mAppSelectListener = listener;
    }

    public void startSelectApp(boolean isFirstSelect, List<CandidateAID> candList) {
        if (appSelectDlg != null) {
            appSelectDlg.dismiss();
            appSelectDlg = null;
        }
        final TickTimer tickTimer = new TickTimer();
        String title = isFirstSelect ? "PLEASE SELECT APP" : "APP NOT ACCEPTS, TRY AGAIN";
        if (candList != null) {
            LogUtils.i(TAG, "onSelectApp: candList size:" + candList.size());
            String[] appId = new String[candList.size()];
            String appName = "";
            for (int i = 0; i < candList.size(); i++) {
                appName = new String(candList.get(i).getAppName());
                appId[i] = appName;
                LogUtils.i(TAG, "app Name:" + appName + ", aid:" + ConvertHelper.getConvert().bcdToStr(candList.get(i).getAid()));
            }
            ApplicationClass.getApp().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    LogUtils.i(TAG, "top activity:" + ActivityStack.getInstance().top().getClass().getSimpleName());
                    appSelectDlg = new AlertDialog.Builder(ActivityStack.getInstance().top()).setSingleChoiceItems(appId, -1, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            if (mAppSelectListener != null) {
                                mAppSelectListener.onSelectFinish(i);
                            }
                            tickTimer.stop();
                        }
                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            if (mAppSelectListener != null) {
                                mAppSelectListener.onSelectFinish(RetCode.EMV_USER_CANCEL);
                            }
                            tickTimer.stop();
                        }
                    }).setCancelable(false).setTitle(title).create();
                    appSelectDlg.show();
                    tickTimer.start(30, () -> {
                        if (appSelectDlg != null && appSelectDlg.isShowing()) {
                            appSelectDlg.dismiss();
                        }
                        if (mAppSelectListener != null) {
                            mAppSelectListener.onSelectFinish(RetCode.EMV_TIME_OUT);
                        }
                    });
                }
            });
        }


    }

    interface AppSelectListener {
        void onSelectFinish(int selectRetCode);
    }
}

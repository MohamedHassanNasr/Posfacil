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
 *  2020/06/05 	         Qinny Zhou           	      Create
 *  ===========================================================================================
 */

package com.paguelofacil.posfacil.pax;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.paguelofacil.posfacil.ApplicationClass;
import com.paguelofacil.posfacil.R;
import com.paguelofacil.posfacil.pax.manager.AppDataManager;
import com.paguelofacil.posfacil.pax.util.PedApiUtils;
import com.pax.commonlib.utils.LogUtils;
import com.pax.commonlib.utils.ToastUtils;
import com.pax.dal.exceptions.PedDevException;


public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "SettingsActivity";
    private AlertDialog inputTpkIndexDlg;
    private String writeKeyPrompt = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        findViewById(R.id.layout_kernel_info).setOnClickListener(this);
        findViewById(R.id.layout_write_tpk).setOnClickListener(this);

        findViewById(R.id.layout_select_param).setOnClickListener(this);
        ((TextView) findViewById(R.id.tv_app_version)).setText("Version " + getAppVersion());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layout_kernel_info:
                SettingsActivity.this.startActivity(new Intent(SettingsActivity.this,KernelLibInfoActivity.class));
                break;
            case R.id.layout_select_param:
                SettingsActivity.this.startActivity(new Intent(SettingsActivity.this, SelectParamFileActivity.class));
                break;
            case R.id.layout_write_tpk:
                processWriteTPK();
                break;
        }


    }

    private void processWriteTPK() {
        if (inputTpkIndexDlg == null) {
            View view = getLayoutInflater().inflate(R.layout.view_input_text, null);
            EditText tpkIndexEdit = view.findViewById(R.id.edit_tpk_index);
            tpkIndexEdit.setFilters(new InputFilter[]{new InputFilter.LengthFilter(3)});
            inputTpkIndexDlg = new AlertDialog.Builder(SettingsActivity.this)
                    .setTitle("Please input TPK index")
                    .setView(view)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            if (tpkIndexEdit.getText().toString().isEmpty()) {
                                ToastUtils.showToast(SettingsActivity.this, "Please input TPK index");
                                return;
                            }
                            int index = Integer.parseInt(tpkIndexEdit.getText().toString());
                            if (index <= 0 || index > 100) {
                                ToastUtils.showToast(SettingsActivity.this, "Invalid TPK index");
                                return;
                            }
                            ApplicationClass.getApp().runInBackground(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        PedApiUtils.writeTPK((byte) index);
                                        AppDataManager.getInstance().set(AppDataManager.TPK_INDEX, index);
                                        writeKeyPrompt = "Write TPK success";
                                    } catch (PedDevException e) {
                                        writeKeyPrompt = "Write TPK Failed";
                                    } finally {
                                        ApplicationClass.getApp().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                ToastUtils.showToast(SettingsActivity.this, writeKeyPrompt);
                                            }
                                        });
                                    }
                                }
                            });
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .setCancelable(false)
                    .create();
        }

        if (inputTpkIndexDlg != null && !inputTpkIndexDlg.isShowing()) {
            inputTpkIndexDlg.show();
        }
    }

    private String getAppVersion() {
        try {
            PackageManager manager = getPackageManager();
            PackageInfo packageInfo = manager.getPackageInfo(getPackageName(), 0);
            if (packageInfo != null) {
                return packageInfo.versionName;
            }
        } catch (Exception e) {
            LogUtils.e(TAG, e);
        }
        return "";
    }
}

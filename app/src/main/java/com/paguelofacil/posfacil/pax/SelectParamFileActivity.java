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
 *  2020/06/19 	         Qinny Zhou           	      Create
 *  ===========================================================================================
 */

package com.paguelofacil.posfacil.pax;

import static com.paguelofacil.posfacil.pax.util.PermissionHelper.REQUEST_CODE_STORAGE;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.paguelofacil.posfacil.ApplicationClass;
import com.paguelofacil.posfacil.R;
import com.paguelofacil.posfacil.pax.manager.AppDataManager;
import com.paguelofacil.posfacil.pax.manager.ParamManager;
import com.paguelofacil.posfacil.pax.util.FileUtil;
import com.paguelofacil.posfacil.pax.util.PermissionHelper;
import com.paguelofacil.posfacil.pax.view.FileItemView;
import com.paguelofacil.posfacil.pax.view.FlowLayout;
import com.pax.commonlib.utils.LogUtils;
import com.pax.commonlib.utils.ToastUtils;
import com.paxsz.module.emv.xmlparam.parser.ParseException;
import com.paxsz.module.emv.xmlparam.parser.pull.CapkParamPullParser;
import com.paxsz.module.emv.xmlparam.parser.pull.ConfigPullParser;
import com.paxsz.module.emv.xmlparam.parser.pull.EmvAidPullParser;
import com.paxsz.module.emv.xmlparam.parser.pull.PayWavePullParser;
import com.paxsz.module.emv.xmlparam.parser.pull.PaypassPullParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public class SelectParamFileActivity extends AppCompatActivity implements FileItemView.IDeleteListener, View.OnClickListener {
    public static final int FILE_SELECT_CODE = 0x01;
    private static final String TAG = "SelectParamFileAct";
    private static final String FILE_TYPE_CONTACT = ".contact";
    private static final String FILE_TYPE_CLSS_MC = ".clss_mc";
    private static final String FILE_TYPE_CLSS_WAVE = ".clss_wave";
    private static final String FILE_TYPE_CAPK = ".capk";
    private static final String FILE_TYPE_COMMON_CONFIG = ".config";

    private static String[] mSupportFileType = new String[]{FILE_TYPE_CONTACT, FILE_TYPE_CLSS_MC,
            FILE_TYPE_CLSS_WAVE, FILE_TYPE_CAPK, FILE_TYPE_COMMON_CONFIG};

    private FlowLayout fileContainer;

    private List<String> mFileList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_param_file);
        findViewById(R.id.layout_select).setOnClickListener(this);
        findViewById(R.id.view_update).setOnClickListener(this);
        fileContainer = findViewById(R.id.file_container);
        mFileList = new ArrayList<>();

    }

    @Override
    public void onDelete(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return;
        }

        if (mFileList != null) {
            mFileList.remove(filePath);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layout_select:
                selectData();
                break;
            case R.id.view_update:
                uploadParam();
                break;
        }

    }

    private void uploadParam() {
        if (mFileList == null || mFileList.isEmpty()) {
            ToastUtils.showToast(SelectParamFileActivity.this, "Please select data firstly");
            return;
        }
        AppDataManager appDataManager = AppDataManager.getInstance();
        ApplicationClass.getApp().runInBackground(new Runnable() {
            @Override
            public void run() {
                ParamManager paramManager = ParamManager.getInstance(SelectParamFileActivity.this);
                String fileName;
                InputStream inputStream = null;
                File file;
                for (String filePath : mFileList) {
                    if (!TextUtils.isEmpty(filePath)) {
                        file = new File(filePath);
                        if (file.exists()) {
                            fileName = FileUtil.getFileName(filePath);
                            if (!TextUtils.isEmpty(fileName)) {
                                try {
                                    inputStream = new FileInputStream(file);
                                    if (fileName.endsWith(FILE_TYPE_CONTACT)) {
                                        paramManager.setEmvAidList(new EmvAidPullParser().parse(inputStream));
                                        appDataManager.set(AppDataManager.KEY_PARAM_ADDR_CONTACT, filePath);
                                    } else if (fileName.endsWith(FILE_TYPE_CLSS_MC)) {
                                        paramManager.setPayPassAids(new PaypassPullParser().parse(inputStream));
                                        appDataManager.set(AppDataManager.KEY_PARAM_ADDR_CLSS_MC, filePath);
                                    } else if (fileName.endsWith(FILE_TYPE_CLSS_WAVE)) {
                                        paramManager.setPayWaveParam(new PayWavePullParser().parse(inputStream));
                                        appDataManager.set(AppDataManager.KEY_PARAM_ADDR_CLSS_WAVE, filePath);
                                    } else if (fileName.endsWith(FILE_TYPE_CAPK)) {
                                        paramManager.setCapkParam(new CapkParamPullParser().parse(inputStream));
                                        appDataManager.set(AppDataManager.KEY_PARAM_ADDR_CAPK, filePath);
                                    } else if (fileName.endsWith(FILE_TYPE_COMMON_CONFIG)) {
                                        paramManager.setConfigParam(new ConfigPullParser().parse(inputStream));
                                        appDataManager.set(AppDataManager.KEY_PARAM_ADDR_CONFIG, filePath);
                                    }
                                } catch (ParseException | FileNotFoundException e) {
                                    LogUtils.e(TAG, e);
                                }
                            }
                        }

                    }
                }
                ApplicationClass.getApp().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.showToast(SelectParamFileActivity.this, "update param success");
                        if (fileContainer != null) {
                            fileContainer.removeAllViews();
                        }
                        if (mFileList != null) {
                            LogUtils.i(TAG, "size:" + mFileList.size());
                            mFileList.clear();
                            mFileList = null;
                        }

                    }
                });
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    PermissionHelper.shouldShowStoragePermissionWarning(SelectParamFileActivity.this);
                }
                break;
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILE_SELECT_CODE && resultCode == Activity.RESULT_OK) {
            onAddFileSuccess(data);
        }
    }

    private void onAddFileSuccess(Intent data) {
        if (data == null) {
            return;
        }
        if (mFileList == null) {
            mFileList = new ArrayList<>();
        }
        Uri uri = data.getData();
        String filePath = FileUtil.getRealFilePath(SelectParamFileActivity.this, uri);
        if (!isSupportFileType(filePath)) {
            ToastUtils.showToast(SelectParamFileActivity.this, "Not supported file type");
            return;
        }

        if (!mFileList.contains(filePath)) {
            mFileList.add(filePath);
            addItemView(filePath);
        }
    }

    private void addItemView(String filePath) {
        if (fileContainer == null) {
            return;
        }
        FileItemView itemView = new FileItemView(SelectParamFileActivity.this);
        itemView.setFilePath(filePath);
        itemView.setDeleteListener(this);

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
        fileContainer.addView(itemView, params);
    }

    /**
     * 选择文件
     */
    private void selectData() {
        if (!PermissionHelper.checkStoragePermission(SelectParamFileActivity.this)) {
            PermissionHelper.requestStoragePermission(SelectParamFileActivity.this);
            return;
        }

        try {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.setType("*/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            SelectParamFileActivity.this.startActivityForResult(intent, FILE_SELECT_CODE);
        } catch (Exception e) {
            ToastUtils.showToast(SelectParamFileActivity.this, e.getMessage());
        }

    }

    private boolean isSupportFileType(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return false;
        }
        for (String fileType : mSupportFileType) {
            if (filePath.endsWith(fileType)) {
                return true;
            }
        }
        return false;
    }
}

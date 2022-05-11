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
 *  2020/05/29 	         Qinny Zhou           	      Create
 *  ===========================================================================================
 */

package com.paguelofacil.posfacil.pax.manager;

import android.content.Context;
import android.content.res.AssetManager;
import android.text.TextUtils;

import com.pax.commonlib.utils.LogUtils;
import com.paxsz.module.emv.xmlparam.entity.clss.PayPassAid;
import com.paxsz.module.emv.xmlparam.entity.clss.PayWaveParam;
import com.paxsz.module.emv.xmlparam.entity.common.CapkParam;
import com.paxsz.module.emv.xmlparam.entity.common.Config;
import com.paxsz.module.emv.xmlparam.entity.contact.EmvAid;
import com.paxsz.module.emv.xmlparam.parser.ParseException;
import com.paxsz.module.emv.xmlparam.parser.pull.CapkParamPullParser;
import com.paxsz.module.emv.xmlparam.parser.pull.ConfigPullParser;
import com.paxsz.module.emv.xmlparam.parser.pull.EmvAidPullParser;
import com.paxsz.module.emv.xmlparam.parser.pull.PayWavePullParser;
import com.paxsz.module.emv.xmlparam.parser.pull.PaypassPullParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class ParamManager {
    private static final String TAG = "ParamManager";
    private static final String FILE_NAME_CAPK = "capk.capk";
    private static final String FILE_NAME_COMMON = "emv_clss.config";
    private static final String FILE_NAME_EMV = "emv_param.contact";
    private static final String FILE_NAME_CLSS_PAYWAVE = "paywave_param.clss_wave";
    private static final String FILE_NAME_CLSS_PAYPASS = "paypass_param.clss_mc";
    private static ParamManager paramInstance;
    private CapkParam capkParam;
    private ArrayList<EmvAid> emvAidList;
    private Config configParam;
    private PayWaveParam payWaveParam;
    private ArrayList<PayPassAid> payPassAids;
    private Context mContext;
    private AppDataManager mAppDataManager;
    private AssetManager assetManager;


    private ParamManager(Context context) {
        mContext = context;
        mAppDataManager = AppDataManager.getInstance();
        assetManager = mContext.getResources().getAssets();
        parseTransParam();
    }

    public static ParamManager getInstance(Context context) {
        if (paramInstance == null) {
            paramInstance = new ParamManager(context.getApplicationContext());
        }
        return paramInstance;
    }


    private boolean parseTransParam() {
        File file = null;
        InputStream inputStream = null;
        try {
            //CAPK
            String localFilePath = mAppDataManager.getString(AppDataManager.KEY_PARAM_ADDR_CAPK);
            LogUtils.d(TAG, ".capk param file path:" + localFilePath);
            if (!TextUtils.isEmpty(localFilePath)) {
                file = new File(localFilePath);
                if (file.exists()) {
                    inputStream = new FileInputStream(localFilePath);
                }
            } else {
                inputStream = assetManager.open(FILE_NAME_CAPK);
            }
            capkParam = new CapkParamPullParser().parse(inputStream);

            //EMV CONTACT
            localFilePath = mAppDataManager.getString(AppDataManager.KEY_PARAM_ADDR_CONTACT);
            LogUtils.d(TAG, ".contact param file path:" + localFilePath);
            if (!TextUtils.isEmpty(localFilePath)) {
                file = new File(localFilePath);
                if (file.exists()) {
                    inputStream = new FileInputStream(localFilePath);
                }
            } else {
                inputStream = assetManager.open(FILE_NAME_EMV);
            }
            emvAidList = new EmvAidPullParser().parse(inputStream);

            //COMMON CONFIG
            localFilePath = mAppDataManager.getString(AppDataManager.KEY_PARAM_ADDR_CONFIG);
            LogUtils.d(TAG, ".config param file path:" + localFilePath);
            if (!TextUtils.isEmpty(localFilePath)) {
                file = new File(localFilePath);
                if (file.exists()) {
                    inputStream = new FileInputStream(localFilePath);
                }
            } else {
                inputStream = assetManager.open(FILE_NAME_COMMON);
            }
            configParam = new ConfigPullParser().parse(inputStream);

            //PAYWAVE
            localFilePath = mAppDataManager.getString(AppDataManager.KEY_PARAM_ADDR_CLSS_WAVE);
            LogUtils.d(TAG, ".clss_wave param file path:" + localFilePath);
            if (!TextUtils.isEmpty(localFilePath)) {
                file = new File(localFilePath);
                if (file.exists()) {
                    inputStream = new FileInputStream(localFilePath);
                }
            } else {
                inputStream = assetManager.open(FILE_NAME_CLSS_PAYWAVE);
            }
            payWaveParam = new PayWavePullParser().parse(inputStream);

            //MC
            localFilePath = mAppDataManager.getString(AppDataManager.KEY_PARAM_ADDR_CLSS_MC);
            LogUtils.d(TAG, ".clss_mc param file path:" + localFilePath);
            if (!TextUtils.isEmpty(localFilePath)) {
                file = new File(localFilePath);
                if (file.exists()) {
                    inputStream = new FileInputStream(localFilePath);
                }
            } else {
                inputStream = assetManager.open(FILE_NAME_CLSS_PAYPASS);
            }
            payPassAids = new PaypassPullParser().parse(inputStream);
            return true;
        } catch (IOException | ParseException e) {
            LogUtils.e(TAG, e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                    inputStream = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        return false;
    }

    public CapkParam getCapkParam() {
        return capkParam;
    }

    public ArrayList<EmvAid> getEmvAidList() {
        return emvAidList;
    }

    public Config getConfigParam() {
        return configParam;
    }

    public PayWaveParam getPayWaveParam() {
        return payWaveParam;
    }

    public ArrayList<PayPassAid> getPayPassAidList() {
        return payPassAids;
    }

    public void setCapkParam(CapkParam capkParam) {
        this.capkParam = capkParam;
    }

    public void setEmvAidList(ArrayList<EmvAid> emvAidList) {
        this.emvAidList = emvAidList;
    }

    public void setConfigParam(Config configParam) {
        this.configParam = configParam;
    }

    public void setPayWaveParam(PayWaveParam payWaveParam) {
        this.payWaveParam = payWaveParam;
    }

    public void setPayPassAids(ArrayList<PayPassAid> payPassAids) {
        this.payPassAids = payPassAids;
    }
}

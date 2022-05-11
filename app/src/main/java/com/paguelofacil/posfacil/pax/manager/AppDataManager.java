/*
 * ===========================================================================================
 * = COPYRIGHT
 *          PAX Computer Technology(Shenzhen) CO., LTD PROPRIETARY INFORMATION
 *   This software is supplied under the terms of a license agreement or nondisclosure
 *   agreement with PAX Computer Technology(Shenzhen) CO., LTD and may not be copied or
 *   disclosed except in accordance with the terms in that agreement.
 *     Copyright (C) 2020-? PAX Computer Technology(Shenzhen) CO., LTD All rights reserved.
 * Description: // Detail description about the function of this module,
 *             // interfaces with the other modules, and dependencies.
 * Revision History:
 * Date	                 Author	                Action
 * 2020/5/18  	         Qinny Zhou           	Create/Add/Modify/Delete
 * ===========================================================================================
 */
package com.paguelofacil.posfacil.pax.manager;

import android.content.Context;
import android.content.SharedPreferences;

import com.paguelofacil.posfacil.ApplicationClass;

public class AppDataManager {
    /**
     * PIN key
     */
    public static final String TPK_INDEX = "TPK_INDEX";

    public static final String KEY_PARAM_ADDR_CONFIG = "configParamPath";
    public static final String KEY_PARAM_ADDR_CAPK = "capkParamPath";
    public static final String KEY_PARAM_ADDR_CONTACT = "contactParamPath";
    public static final String KEY_PARAM_ADDR_CLSS_MC = "clssMCParamPath";
    public static final String KEY_PARAM_ADDR_CLSS_WAVE = "clssWaveParamPath";

    private static AppDataManager instance;
    private SharedPreferences mPreference;
    private SharedPreferences.Editor editor;



    private AppDataManager() {

        mPreference = ApplicationClass.getApp()
                .getApplicationContext()
                .getSharedPreferences("APP_DATA", Context.MODE_PRIVATE);
        editor = mPreference.edit();
        editor.apply();
    }

    public static AppDataManager getInstance() {
        if (instance == null) {
            init();
        }

        return instance;
    }

    private static synchronized void init() {
        if (instance == null) {
            instance = new AppDataManager();
        }
    }

    public String getString(String key) {
       return mPreference.getString(key,"");

    }

    public int getInt(String key, int defaultVal) {
        return mPreference.getInt(key, defaultVal);

    }

    public void set(String key,String value){
        editor.putString(key, value).apply();
    }

    public void set(String key, int value) {
        editor.putInt(key, value).apply();
    }

}

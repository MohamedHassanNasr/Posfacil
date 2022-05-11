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
 *  2020/06/22 	         Qinny Zhou           	      Create
 *  ===========================================================================================
 */

package com.paguelofacil.posfacil.pax.util;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

public class PermissionHelper {
    public static final String TAG = "PermissionHelper";
    public static final int REQUEST_CODE_STORAGE = 0x01;

    public static final String[] PERMISSION_STORAGE = {Manifest.permission.READ_EXTERNAL_STORAGE};

    public static boolean checkStoragePermission(Context context) {
        for (String permission : PERMISSION_STORAGE) {
            if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    public static void requestStoragePermission(Activity activity) {
        ActivityCompat.requestPermissions(activity, PERMISSION_STORAGE, REQUEST_CODE_STORAGE);
    }

    public static void shouldShowStoragePermissionWarning(Activity activity) {
        for (String permission : PERMISSION_STORAGE) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                Toast.makeText(activity, "Please allow storage permission",
                        Toast.LENGTH_SHORT).show();
                return;
            }
        }
    }
}

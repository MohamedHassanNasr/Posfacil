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

import android.app.Activity;
import android.os.Bundle;
import android.util.ArrayMap;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.paguelofacil.posfacil.ApplicationClass;
import com.paguelofacil.posfacil.R;
import com.paguelofacil.posfacil.pax.util.ScreenUtils;
import com.paxsz.module.emv.utils.EmvLibVersion;

import java.util.Iterator;

import javax.annotation.Nullable;

public class KernelLibInfoActivity extends Activity {

    private LinearLayout kernelTypeListLayout;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disp_version_layout);
        kernelTypeListLayout = findViewById(R.id.layout_kernel_type);
        ApplicationClass.getApp().runInBackground(new Runnable() {
            @Override
            public void run() {
                initInfo();
            }
        });

    }

    private void initInfo() {
        ArrayMap<String, String> hashMap = new EmvLibVersion().getEmvLibVersion();
        hashMap.put("Neptune", ApplicationClass.getApp().getDal().getSys().getDevInterfaceVer());
        Iterator iterator = hashMap.entrySet().iterator();
        ApplicationClass.getApp().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int i = 0;
                while (iterator.hasNext()) {
                    ArrayMap.Entry entry = (ArrayMap.Entry) iterator.next();
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ScreenUtils.dp2px(KernelLibInfoActivity.this, 68));
                    layoutParams.topMargin = ScreenUtils.dp2px(KernelLibInfoActivity.this, 4);
                    View itemView = getLayoutInflater().inflate(R.layout.view_kernel_version, null);
                    if (i % 2 != 0) {
                        itemView.setBackgroundColor(getResources().getColor(R.color.white));
                    } else {
                        itemView.setBackgroundColor(getResources().getColor(R.color.gray));
                    }
                    String key = (String) entry.getKey();
                    TextView libLabel = itemView.findViewById(R.id.tv_kernel_label);
                    TextView libName = itemView.findViewById(R.id.tv_kernel_type);
                    TextView libValue = itemView.findViewById(R.id.tv_kernel_version);
                    i++;
                    if (key.contains("Neptune")) {
                        libName.setText(key + "LiteApi");
                    } else {
                        libName.setText(key + "Lib");
                    }

                    libLabel.setText(key);
                    libValue.setText(((String) entry.getValue()).replaceAll(" ", "_"));
                    kernelTypeListLayout.addView(itemView, kernelTypeListLayout.getChildCount(), layoutParams);

                }
            }
        });

    }

}

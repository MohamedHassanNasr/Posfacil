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
 *  2020/05/19 	         Qinny Zhou           	Create/Add/Modify/Delete
 *  ===========================================================================================
 */

package com.paguelofacil.posfacil.pax.view.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.LinearLayout;

import androidx.annotation.IntRange;

import com.paguelofacil.posfacil.R;

import javax.annotation.Nullable;


public class ClssLightsView extends LinearLayout {

    private ClssLight[] lights = new ClssLight[4];

    private AlphaAnimation blinking;

    private int clssLigthStd;


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    public ClssLightsView(Context context) {
        this(context, null);
    }

    public ClssLightsView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ClssLightsView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ClssLightStd);
        clssLigthStd = array.getInt(R.styleable.ClssLightStd_lightStandard, 0);
        LayoutInflater mInflater = LayoutInflater.from(context);
        View myView = mInflater.inflate(R.layout.clss_light_layout, null);
        LayoutParams parentParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        parentParams.setLayoutDirection(HORIZONTAL);
        addView(myView, parentParams);
        array.recycle();
        init();
    }

    private void init() {
        blinking = new AlphaAnimation(1, 0);
        blinking.setDuration(500);
        blinking.setRepeatCount(Animation.INFINITE);
        blinking.setRepeatMode(Animation.REVERSE);

        lights[0] = (ClssLight) findViewById(R.id.light1);
        lights[1] = (ClssLight) findViewById(R.id.light2);
        lights[2] = (ClssLight) findViewById(R.id.light3);
        lights[3] = (ClssLight) findViewById(R.id.light4);

        if (clssLigthStd == 1) {
            int[] statusSrc = new int[2];
            statusSrc[0] = R.drawable.led_off;
            statusSrc[1] = R.drawable.led_green_on;
            lights[0].setStatusSrc(statusSrc);
            lights[1].setStatusSrc(statusSrc);
            lights[2].setStatusSrc(statusSrc);
            lights[3].setStatusSrc(statusSrc);
        }
    }

    public void setClssLigthStd(int clssLigthStd) {
        if (clssLigthStd == 1) {
            int[] statusSrc = new int[2];
            statusSrc[0] = R.drawable.led_off;
            statusSrc[1] = R.drawable.led_green_on;
            lights[0].setStatusSrc(statusSrc);
            lights[1].setStatusSrc(statusSrc);
            lights[2].setStatusSrc(statusSrc);
            lights[3].setStatusSrc(statusSrc);
        } else {
            lights[0].setStatusSrc(new int[]{R.drawable.led_off, R.drawable.led_blue_on});
            lights[1].setStatusSrc(new int[]{R.drawable.led_off, R.drawable.led_yellow_on});
            lights[2].setStatusSrc(new int[]{R.drawable.led_off, R.drawable.led_green_on});
            lights[3].setStatusSrc(new int[]{R.drawable.led_off, R.drawable.led_red_on});
        }
    }

    public void setLights(final @IntRange(from = -1, to = 3) int index, @ClssLight.STATUS int status) {


        for (int i = 0; i < lights.length; ++i) {
            if (index == -1) {
                lights[i].setStatus(ClssLight.OFF, null);
            }

            if (index == i) {
                lights[i].setStatus(status, blinking);
            }
        }
    }
}

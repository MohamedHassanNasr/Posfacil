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
import android.view.animation.Animation;

import androidx.annotation.DrawableRes;
import androidx.annotation.IntDef;
import androidx.appcompat.widget.AppCompatImageView;

import com.paguelofacil.posfacil.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.annotation.Nullable;

public class ClssLight extends AppCompatImageView {

    public final static int OFF = 0;
    public final static int ON = 1;
    public final static int BLINK = 2;

    @IntDef({OFF, ON, BLINK})
    @Retention(RetentionPolicy.SOURCE)
    public @interface STATUS {
    }

    @DrawableRes
    private int[] statusSrc = new int[]{-1, -1};

    @STATUS
    private int status = OFF;

    public ClssLight(Context context) {
        this(context, null);
    }

    public ClssLight(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ClssLight(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        clearAnimation();
    }

    private void init(Context context, @Nullable AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ClssLightState);
        statusSrc[OFF] = array.getResourceId(R.styleable.ClssLightState_offSrc, -1);
        statusSrc[ON] = array.getResourceId(R.styleable.ClssLightState_onSrc, -1);
        array.recycle();
    }

    public void setStatus(@STATUS int status, final Animation blinking) {
        this.status = status;
        if (status == BLINK) {
            setImageResource(statusSrc[ON]);
            startAnimation(blinking);
        } else {
            clearAnimation();
            setImageResource(statusSrc[status]);
        }
    }

    public void setStatusSrc(int[] statusSrc) {
        this.statusSrc = statusSrc;
        setImageResource(statusSrc[status]);
    }
}

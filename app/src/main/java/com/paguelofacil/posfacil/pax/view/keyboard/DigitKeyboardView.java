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
 * 2020/5/12  	         Qinny Zhou           	Create/Add/Modify/Delete
 * ===========================================================================================
 */
package com.paguelofacil.posfacil.pax.view.keyboard;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.util.AttributeSet;

import androidx.core.content.ContextCompat;

import com.paguelofacil.posfacil.R;

import java.util.List;

public class DigitKeyboardView extends KeyboardView {
//    private Drawable mKeyBg;
//    private Drawable mOpKeyBg;
    private Drawable mOkKeyBg;

    private Paint paint = new Paint();

    private Context mContext;

    public DigitKeyboardView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DigitKeyboardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
//        mKeyBg = ContextCompat.getDrawable(mContext, R.drawable.selector_keyboard_num_bg);
//        mOpKeyBg = ContextCompat.getDrawable(mContext, R.drawable.btn_bg_light);
        mOkKeyBg = ContextCompat.getDrawable(mContext, R.drawable.btn_bg_light);
    }


    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (getKeyboard() == null) {
            return;
        }
        List<Keyboard.Key> keys = getKeyboard().getKeys();
        for (Keyboard.Key key : keys) {
            canvas.save();

            int offsetY = 0;
            if (key.y == 0) {
                offsetY = 6;
            }
            int initDrawY = key.y + offsetY;
            Rect rect = new Rect(key.x, initDrawY, key.x + key.width, key.y + key.height);
            canvas.clipRect(rect);
            drawIcon(canvas, key, rect);
            drawText(canvas, key, initDrawY);
            canvas.restore();
        }

    }

    private void drawIcon(Canvas canvas, Keyboard.Key key, Rect rect) {

        if (key.codes != null && key.codes.length != 0 && key.codes[0] == -4) {
            Drawable drawable = mOkKeyBg;
            if (drawable != null && null == key.icon) {
                int[] state = key.getCurrentDrawableState();
                drawable.setState(state);
                drawable.setBounds(rect);
                drawable.draw(canvas);
            }
        }

        if (key.icon != null) {
            int[] state = key.getCurrentDrawableState();
            key.icon.setState(state);
            key.icon.setBounds(rect);
            key.icon.draw(canvas);
        }
    }

    private void drawText(Canvas canvas, Keyboard.Key key, int initDrawY) {
        paint.setAntiAlias(true);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(mContext.getResources().getDimension(R.dimen.sp_30));
        int keyColor = ContextCompat.getColor(mContext, R.color.black_overlay);
        if (key.codes[0] == -5) {
            keyColor = ContextCompat.getColor(mContext, R.color.colorPrimaryDark);
        } else if (key.codes[0] == -3) {
            keyColor = ContextCompat.getColor(mContext, R.color.colorPrimaryDark);

        } else if (key.codes[0] == -4) {
            keyColor =Color.WHITE;
        }
        paint.setColor(keyColor);

        if (key.label != null) {
            canvas.drawText(
                    key.label.toString(),
                    key.x + (key.width / 2),
                    initDrawY + (key.height + paint.getTextSize() - paint.descent()) / 2,
                    paint
            );
        }
    }


}

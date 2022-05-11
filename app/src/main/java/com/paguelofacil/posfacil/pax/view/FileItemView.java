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

package com.paguelofacil.posfacil.pax.view;

import android.content.Context;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.TouchDelegate;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.paguelofacil.posfacil.R;

import javax.annotation.Nullable;


public class FileItemView extends LinearLayout implements View.OnClickListener {
    public static final String TAG = "FileItemView";

    private TextView mFileName;
    private ImageView mClose;

    private String mFilePath;
    private IDeleteListener mListener;

    public FileItemView(Context context) {
        super(context);
        initView(context);
    }

    public FileItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public FileItemView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    public static String getFileName(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return "";
        }
        return filePath.substring(filePath.lastIndexOf("/") + 1);
    }

    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.item_file_view, this);

        mFileName = findViewById(R.id.name);
        mClose = findViewById(R.id.close);
        mClose.setOnClickListener(this);
        expandViewTouchDelegate(mClose, 15, 15, 15, 15);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.close) {
            removeFromParent();
            if (mListener != null) {
                mListener.onDelete(mFilePath);
            }
        }
    }

    public void setFilePath(String filePath) {
        mFilePath = filePath;

        String text = getFileName(filePath);
        updateFileName(text);
    }

    public void setDeleteListener(IDeleteListener listener) {
        this.mListener = listener;
    }

    private void removeFromParent() {
        ViewGroup parent = (ViewGroup) getParent();
        if (parent != null) {
            parent.removeView(this);
        }
    }

    private void updateFileName(String text) {
        if (mFileName == null) {
            return;
        }
        mFileName.setText(text);
    }

    /**
     * expandViewTouchDelegate,not exceed parent
     *
     * @param view
     * @param top
     * @param bottom
     * @param left
     * @param right
     */
    private void expandViewTouchDelegate(final View view, final int top, final int bottom,
                                         final int left, final int right) {
        ((View) view.getParent()).post(() -> {
            Rect bounds = new Rect();
            view.setEnabled(true);
            view.getHitRect(bounds);

            bounds.top -= top;
            bounds.bottom += bottom;
            bounds.left -= left;
            bounds.right += right;

            TouchDelegate touchDelegate = new TouchDelegate(bounds, view);

            if (View.class.isInstance(view.getParent())) {
                ((View) view.getParent()).setTouchDelegate(touchDelegate);
            }
        });
    }

    public interface IDeleteListener {
        void onDelete(String filePath);
    }
}

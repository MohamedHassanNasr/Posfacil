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

import static android.content.Context.AUDIO_SERVICE;

import android.app.Activity;
import android.content.Context;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.media.AudioManager;
import android.text.Editable;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.paguelofacil.posfacil.R;
import com.pax.commonlib.utils.LogUtils;

import java.util.List;

public class DigitKeyboard {
    private Activity mActivity;
    private Keyboard mKeyboard;
    private DigitKeyboardView mKeyboardView;
    private EditText attchedEdit;

    public DigitKeyboard(Activity activity) {
        mKeyboardView = (DigitKeyboardView) activity.findViewById(R.id.view_keyboard);
        mActivity = activity;
        LogUtils.w("TAG", " DigitKeyboard" + mKeyboardView);
    }

    private void playClick(Context context, int keyCode) {
        AudioManager am = (AudioManager) context.getSystemService(AUDIO_SERVICE);
        switch (keyCode) {
            case 32:
                am.playSoundEffect(AudioManager.FX_KEYPRESS_SPACEBAR);
                break;
            case Keyboard.KEYCODE_DONE:
            case 10:
                am.playSoundEffect(AudioManager.FX_KEYPRESS_RETURN);
                break;
            case Keyboard.KEYCODE_DELETE:
                am.playSoundEffect(AudioManager.FX_KEYPRESS_DELETE);
                break;
            default:
                am.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD);
        }
    }

    public void attachTo(EditText editText) {
        mKeyboard = new Keyboard(mActivity, R.xml.amount_keyboard_large);
        this.attchedEdit = editText;
        hideSystemKeyboard(mActivity, this.attchedEdit);
        showSoftKeyboard();
    }

    private void showSoftKeyboard() {
        if (mKeyboard == null) {
            mKeyboard = new Keyboard(mActivity, R.xml.amount_keyboard_large);
        }
        if (mKeyboardView == null) {
            mKeyboardView = (DigitKeyboardView) mActivity.findViewById(R.id.view_keyboard);
        }
        LogUtils.w("TAG", "showSoftKeyboard: " + mKeyboardView);
        mKeyboardView.setKeyboard(mKeyboard);
        mKeyboardView.setEnabled(true);
        mKeyboardView.setPreviewEnabled(false);
        mKeyboardView.setVisibility(View.VISIBLE);
        mKeyboardView.setOnKeyboardActionListener(new KeyboardView.OnKeyboardActionListener() {
            @Override
            public void onPress(int i) {

            }

            @Override
            public void onRelease(int i) {

            }

            @Override
            public void onKey(int primaryCode, int[] keyCodes) {
                Editable editable = attchedEdit.getText();
                int start = attchedEdit.getText().length();
                playClick(mActivity, primaryCode);

                if (primaryCode == Keyboard.KEYCODE_CANCEL) {// cancel
                    attchedEdit.onEditorAction(EditorInfo.IME_ACTION_NONE);
//                    editable.clear();
                } else if (primaryCode == Keyboard.KEYCODE_DONE) {// done
                    attchedEdit.onEditorAction(EditorInfo.IME_ACTION_DONE);
                } else if (primaryCode == Keyboard.KEYCODE_DELETE) {// delete
                    if (editable != null && editable.length() > 0 && start > 0) {
                        editable.delete(start - 1, start);
                    }
                } else if (0x0 <= primaryCode && primaryCode <= 0x7f) {
//            Log.i("TAG", "onKey: "+primaryCode);
                    attchedEdit.onEditorAction(primaryCode);
                    editable.insert(start, Character.toString((char) primaryCode));
                } else if (primaryCode > 0x7f) {
//            Log.i("TAG", "7f onKey: "+primaryCode);
                    attchedEdit.onEditorAction(primaryCode);
                    Keyboard.Key key = getKeyByKeyCode(primaryCode);
                    if (key != null)
                        editable.insert(start, key.label);
                }
            }

            @Override
            public void onText(CharSequence charSequence) {

            }

            @Override
            public void swipeLeft() {

            }

            @Override
            public void swipeRight() {

            }

            @Override
            public void swipeDown() {

            }

            @Override
            public void swipeUp() {

            }
        });
    }

    private void hideSystemKeyboard(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private Keyboard.Key getKeyByKeyCode(int primaryCode) {
        if (mKeyboard != null) {
            List<Keyboard.Key> keyList = mKeyboard.getKeys();
            for (Keyboard.Key key : keyList) {
                if (key.codes[0] == primaryCode) {
                    return key;
                }
            }
        }

        return null;
    }
}

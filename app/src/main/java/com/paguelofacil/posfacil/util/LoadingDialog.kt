package com.paguelofacil.posfacil.util

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Window
import com.paguelofacil.posfacil.R


/**
 * As Progress dialog API is deprecated by the android
 * in order to overcome that we used android custom dialog with progressbar on it
 *
 * @param context
 */
class LoadingDialog(context: Context) : Dialog(context) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setContentView(R.layout.layout_loading_dialog)
        setCancelable(false)
        setCanceledOnTouchOutside(false)
    }
}


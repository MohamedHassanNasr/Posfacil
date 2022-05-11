package com.paguelofacil.posfacil.ui.view.account.dialog

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.paguelofacil.posfacil.ApplicationClass
import com.paguelofacil.posfacil.R
import com.paguelofacil.posfacil.databinding.LayoutPasswordRecoveredBinding
import com.paguelofacil.posfacil.ui.interfaces.BottomSheetCallback

class ForgotPasswordRecoveredSuccessDialog(
    context: Context,
    private val callback: BottomSheetCallback,
) : BottomSheetDialogFragment(), View.OnClickListener {
    private lateinit var binding: LayoutPasswordRecoveredBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = LayoutPasswordRecoveredBinding.inflate(inflater)
        requireDialog().window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        binding.btnOk.text = ApplicationClass.language.logIn
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        binding.ivClose.setOnClickListener(this)
        binding.btnOk.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view) {
            binding.ivClose -> {
                dismiss()
                callback.onActionOccur(0)
            }
            binding.btnOk -> {
                dismiss()
                callback.onActionOccur(1)
            }
        }
    }
}
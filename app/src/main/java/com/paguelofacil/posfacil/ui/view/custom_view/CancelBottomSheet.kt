package com.paguelofacil.posfacil.ui.view.custom_view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.paguelofacil.posfacil.ApplicationClass
import com.paguelofacil.posfacil.R
import com.paguelofacil.posfacil.databinding.BottomSheetCancelBinding

class CancelBottomSheet(
    val callBackClose: () -> Unit,
    val callbackVolver: () -> Unit,
    val callbackCancelar: () -> Unit,
) : BottomSheetDialogFragment() {
    private lateinit var binding: BottomSheetCancelBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.AppBottomSheetDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetCancelBinding.inflate(inflater)
        requireDialog().window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        binding.btnVolver.text = ApplicationClass.language.volver
        binding.description.text = ApplicationClass.language.cancel_question
        binding.tvMensajeDialog.text = ApplicationClass.language.cancel_operation
        binding.btnSiCancelar.text = ApplicationClass.language.cancel

        initOnClick()

        return binding.root
    }

    private fun initOnClick() {
        binding.ivCloseDg.setOnClickListener {
            callBackClose()
        }

        binding.btnSiCancelar.setOnClickListener {
            callbackCancelar()
        }

        binding.btnVolver.setOnClickListener {
            callbackVolver()
        }

    }
}
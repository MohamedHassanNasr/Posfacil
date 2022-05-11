package com.paguelofacil.posfacil.ui.view.custom_view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.paguelofacil.posfacil.ApplicationClass
import com.paguelofacil.posfacil.R
import com.paguelofacil.posfacil.databinding.BottomSheetCancelBinding
import com.paguelofacil.posfacil.databinding.BottomSheetReporteXBinding

class ReportBottomSheet(
    val message: String,
    val nameButton:String,
    val callBackClose: () -> Unit
) : BottomSheetDialogFragment() {
    private lateinit var binding: BottomSheetReporteXBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.AppBottomSheetDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetReporteXBinding.inflate(inflater)
        binding.tvMensajeDialog.text = message
        binding.btnCloseDgCorteX.text = ApplicationClass.language.finalizar
        requireDialog().window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        initOnClick()

        return binding.root
    }

    private fun initOnClick() {

        binding.btnCloseDgCorteX.setOnClickListener {
            callBackClose()
        }

    }
}
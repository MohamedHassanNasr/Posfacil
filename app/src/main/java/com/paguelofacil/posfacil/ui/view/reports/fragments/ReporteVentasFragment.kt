package com.paguelofacil.posfacil.ui.view.reports.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.paguelofacil.posfacil.ApplicationClass
import com.paguelofacil.posfacil.R
import com.paguelofacil.posfacil.databinding.FragmentReporteVentasBinding
import com.paguelofacil.posfacil.ui.view.custom_view.CancelBottomSheet
import com.paguelofacil.posfacil.ui.view.custom_view.ReportBottomSheet
import kotlinx.android.synthetic.main.bottom_sheet_dialog.view.*


class ReporteVentasFragment : Fragment() {

    lateinit var binding: FragmentReporteVentasBinding
    private var dialog: ReportBottomSheet? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentReporteVentasBinding.inflate(inflater, container, false)

        binding.detailsUserText.text = ApplicationClass.language.detalleUsuario

        loadListeners()

        return binding.root
    }

    private fun loadListeners() {

        binding.btnEnviarReporteX.setOnClickListener {
            showBottomSheet(getString(R.string.reporte_x_generado))
        }

        binding.btnRealizarReporteZ.setOnClickListener {
            showDialogConfirmCorteZ()
        }
    }

    private fun showBottomSheet(mensaje: String) {
        dialog = ReportBottomSheet(
            mensaje,
            getString(R.string.finalizar),
            callBackClose = { dialog?.dismiss() }
        )
        dialog?.show(parentFragmentManager, "")
    }

    private fun showDialogConfirmCorteZ() {
        val dialog = context?.let { BottomSheetDialog(it) }

        val view = layoutInflater.inflate(R.layout.bottom_sheet_alert_z, null)

        val btnClose = view.findViewById<Button>(R.id.btn_volver)
        val btnAceptar = view.findViewById<Button>(R.id.btn_si_aceptar)
        val title = view.findViewById<TextView>(R.id.tv_mensaje_dialog)
        val description = view.findViewById<TextView>(R.id.textView16)
        val rdb = view.findViewById<TextView>(R.id.radioButton)

        title.text = ApplicationClass.language.seguroRealizarCorteZ
        rdb.text = ApplicationClass.language.deseaEnviarCorreoCorte
        btnAceptar.text = ApplicationClass.language.siAceptar
        btnClose.text = ApplicationClass.language.volver

        btnAceptar.setOnClickListener {

            showBottomSheet(ApplicationClass.language.reporteZGenerado)
            dialog?.hide()

        }

        btnClose.setOnClickListener {

            dialog?.hide()

        }

        dialog?.setCancelable(true)

        dialog?.setContentView(view)

        dialog?.show()
    }


}
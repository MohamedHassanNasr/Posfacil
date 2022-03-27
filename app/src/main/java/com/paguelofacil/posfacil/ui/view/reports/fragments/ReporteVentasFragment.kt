package com.paguelofacil.posfacil.ui.view.reports.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.paguelofacil.posfacil.R
import com.paguelofacil.posfacil.databinding.FragmentReporteVentasBinding
import kotlinx.android.synthetic.main.bottom_sheet_dialog.view.*


class ReporteVentasFragment : Fragment() {

    lateinit var binding: FragmentReporteVentasBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding=FragmentReporteVentasBinding.inflate(inflater,container,false)

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

    private fun showBottomSheet(mensaje:String) {

        val dialog = context?.let { BottomSheetDialog(it) }

        val view = layoutInflater.inflate(R.layout.bottom_sheet_reporte_x, null)
        val btnclose=view.findViewById<Button>(R.id.btn_close_dg_corte_x)

        view.tv_mensaje_dialog.text=mensaje

        btnclose.setOnClickListener{

            dialog?.hide()

        }

        dialog?.setCancelable(true)

        dialog?.setContentView(view)

        dialog?.show()

    }

    private fun showDialogConfirmCorteZ()
    {
        val dialog = context?.let { BottomSheetDialog(it) }

        val view = layoutInflater.inflate(R.layout.bottom_sheet_alert_z, null)

        val btnClose = view.findViewById<Button>(R.id.btn_volver)
        val btnAceptar=view.findViewById<Button>(R.id.btn_si_aceptar)

        btnAceptar.setOnClickListener{

            showBottomSheet(getString(R.string.reporte_z_generado))
            dialog?.hide()

        }

        btnClose.setOnClickListener{

            dialog?.hide()

        }

        dialog?.setCancelable(true)

        dialog?.setContentView(view)

        dialog?.show()
    }


}
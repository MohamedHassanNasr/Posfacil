package com.paguelofacil.posfacil.ui.view.reports.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioButton
import android.widget.ScrollView
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.paguelofacil.posfacil.R
import com.paguelofacil.posfacil.databinding.FragmentReporteXBinding
import kotlinx.android.synthetic.main.bottom_sheet_dialog.view.*


class ReporteXFragment : Fragment() {


    lateinit var binding:FragmentReporteXBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding=FragmentReporteXBinding.inflate(inflater,container,false)

        loadListeners()

        return binding.root

    }

    private fun loadListeners() {


        binding.btnEnviarReporteX.setOnClickListener {

            showBottomSheet(getString(R.string.reporte_x_generado),true)

        }


        binding.btnGenerarReporteZ.setOnClickListener {

            //showReportZ()
            showDialogConfirmCorteZ()

        }

        binding.btnVolverReporteX.setOnClickListener {

            showReportX()

        }

    }

    private fun showReportX()
    {
        binding.svReporteZ.visibility=View.GONE
        binding.btnVolverReporteX.visibility=View.GONE


        binding.svReporteX.visibility=View.VISIBLE
        binding.btnEnviarReporteX.visibility=View.VISIBLE
        binding.btnGenerarReporteZ.visibility=View.VISIBLE
        binding.svReporteX.fullScroll(ScrollView.FOCUS_UP)
    }

    private fun showReportZ()
    {
        binding.svReporteX.visibility=View.GONE
        binding.btnEnviarReporteX.visibility=View.GONE
        binding.btnGenerarReporteZ.visibility=View.GONE
        binding.svReporteZ.visibility=View.VISIBLE


        binding.btnVolverReporteX.visibility=View.VISIBLE
        binding.svReporteZ.fullScroll(ScrollView.FOCUS_UP)
    }

    private fun showBottomSheet(mensaje:String,showCorreo:Boolean) {

        val dialog = context?.let { BottomSheetDialog(it) }

        val view = layoutInflater.inflate(R.layout.bottom_sheet_reporte_x, null)
        val btnclose=view.findViewById<Button>(R.id.btn_close_dg_corte_x)
        val rbSendEmail=view.findViewById<RadioButton>(R.id.rb_send_email_report_x)
        val tvSendEmail=view.findViewById<TextView>(R.id.tv_send_email_report_x)

        btnclose.text=getString(R.string.aceptar)

        if (showCorreo)
        {
            rbSendEmail.visibility=View.VISIBLE
            tvSendEmail.visibility=View.VISIBLE
            btnclose.text=getString(R.string.finalizar)
        }
        else
        {
            rbSendEmail.visibility=View.GONE
            tvSendEmail.visibility=View.GONE
            btnclose.text=getString(R.string.aceptar)
        }

        view.tv_mensaje_dialog.text=mensaje

        btnclose.setOnClickListener{

            dialog?.hide()
            if(showCorreo)
            {
                showSuccesReportX(getString(R.string.reporte_x_generado),false)
            }


        }

        dialog?.setCancelable(true)

        dialog?.setContentView(view)

        dialog?.show()

    }
    private fun showSuccesReportX(mensaje:String,showCorreo:Boolean) {

        val dialog = context?.let { BottomSheetDialog(it) }

        val view = layoutInflater.inflate(R.layout.bottom_sheet_reporte_x, null)
        val btnclose=view.findViewById<Button>(R.id.btn_close_dg_corte_x)
        val rbSendEmail=view.findViewById<RadioButton>(R.id.rb_send_email_report_x)
        val tvSendEmail=view.findViewById<TextView>(R.id.tv_send_email_report_x)



        if (showCorreo)
        {
            rbSendEmail.visibility=View.VISIBLE
            tvSendEmail.visibility=View.VISIBLE
            btnclose.text=getString(R.string.finalizar)
        }
        else
        {
            rbSendEmail.visibility=View.GONE
            tvSendEmail.visibility=View.GONE
            btnclose.text=getString(R.string.aceptar)
        }

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

            showBottomSheet(getString(R.string.reporte_z_generado),false)
            showReportZ()
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
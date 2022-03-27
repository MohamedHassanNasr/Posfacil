package com.paguelofacil.posfacil.ui.view.transactions.payment.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.paguelofacil.posfacil.R
import com.paguelofacil.posfacil.databinding.FragmentCobroQrCodeBinding
import com.paguelofacil.posfacil.ui.view.home.activities.HomeActivity


class CobroQrCodeFragment : Fragment() {


    lateinit var binding: FragmentCobroQrCodeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding=FragmentCobroQrCodeBinding.inflate(inflater,container,false)

        loadListeners()

        return binding.root

    }

    private fun goHome()
    {
        var fr = activity?.supportFragmentManager?.beginTransaction()
        fr?.replace(R.id.container_frag_cobro, ComprobanteCobroFragment())
        fr?.commit()
    }

    private fun loadListeners() {


        binding.lnArrowBack.setOnClickListener{

           goHome()

        }

        binding.lnCloseBack.setOnClickListener{

            showBottomSheet()

        }

        binding.btnOtherTransaction.setOnClickListener {

            val intent= Intent(context, HomeActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }

        binding.btnVerificar.setOnClickListener {

            showBottomSheetSucces()

        }

    }

    private fun showBottomSheet() {

        val dialog = context?.let { BottomSheetDialog(it) }

        val view = layoutInflater.inflate(R.layout.bottom_sheet_cancel, null)

        val btnBack = view.findViewById<Button>(R.id.btn_volver)
        val btnCancel=view.findViewById<Button>(R.id.btn_si_cancelar)
        val ivCancel=view.findViewById<ImageView>(R.id.iv_close_dg)

        btnBack.setOnClickListener {

            dialog?.dismiss()


        }
        ivCancel.setOnClickListener {
            dialog?.dismiss()

        }
        btnCancel.setOnClickListener {
            dialog?.dismiss()
            goHome()

        }


        dialog?.setCancelable(true)

        dialog?.setContentView(view)

        dialog?.show()

    }
    private fun showBottomSheetSucces() {

        val dialog = context?.let { BottomSheetDialog(it) }

        val view = layoutInflater.inflate(R.layout.bottom_sheet_pago_success, null)

        val btnAceptar = view.findViewById<Button>(R.id.btn_acept_pago_qr)
        val ivCancel=view.findViewById<ImageView>(R.id.iv_close_dg)


        ivCancel.setOnClickListener {
            dialog?.dismiss()

        }
        btnAceptar.setOnClickListener {
            dialog?.dismiss()
            val intent= Intent(context, HomeActivity::class.java)
            startActivity(intent)
            activity?.finish()


        }


        dialog?.setCancelable(true)

        dialog?.setContentView(view)

        dialog?.show()

    }



}
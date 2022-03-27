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
import com.paguelofacil.posfacil.databinding.FragmentFirmaBinding
import com.paguelofacil.posfacil.ui.view.home.activities.HomeActivity


class FirmaFragment : Fragment() {


    lateinit var binding: FragmentFirmaBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding= FragmentFirmaBinding.inflate(inflater,container,false)

        loadListeners()

        return binding.root

    }

    private fun loadListeners() {

        binding.btnFirmar.setOnClickListener{

            goDetailPay()

        }

        binding.ivBack.setOnClickListener{

            showBottomSheet()

        }

    }

    private fun goDetailPay() {

        var fr = activity?.supportFragmentManager?.beginTransaction()
        fr?.replace(R.id.container_frag_cobro, SendReceiptPaymentFragment())
        fr?.commit()


    }

    private fun showBottomSheet() {

        val dialog = context?.let { BottomSheetDialog(it) }

        val view = layoutInflater.inflate(R.layout.bottom_sheet_cancel, null)

        val btnBack = view.findViewById<Button>(R.id.btn_volver)
        val btnCancel=view.findViewById<Button>(R.id.btn_si_cancelar)
        val ivCancel=view.findViewById<ImageView>(R.id.iv_close_dg)

        btnBack.setOnClickListener {

            dialog?.dismiss()
            //getHome()

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

    private fun goHome()
    {

        val intent= Intent(context, HomeActivity::class.java)
        startActivity(intent)
        activity?.finish()
    }




}
package com.paguelofacil.posfacil.ui.view.transactions.payment.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.paguelofacil.posfacil.R
import com.paguelofacil.posfacil.databinding.FragmentEnvioComprobanteBinding
import com.paguelofacil.posfacil.ui.interfaces.IOnBackPressed
import com.paguelofacil.posfacil.ui.view.home.activities.HomeActivity
import kotlinx.android.synthetic.main.bottom_sheet_dialog.view.*


class EnvioComprobanteFragment : Fragment(), IOnBackPressed {


    lateinit var binding: FragmentEnvioComprobanteBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding=FragmentEnvioComprobanteBinding.inflate(inflater,container,false)

        loadListeners()

        return binding.root

    }

    private fun loadListeners() {

        binding.ivBack.setOnClickListener{

            //getDetailCobro()


        }

        binding.btnSendVoucher.setOnClickListener{

            showBottomSheet(getString(R.string.voucher_enviado))


        }

        binding.ivBack.setOnClickListener {

            onBackPressed()

        }

    }

    private fun showBottomSheet(mensaje:String) {

        val dialog = context?.let { BottomSheetDialog(it) }

        val view = layoutInflater.inflate(R.layout.bottom_sheet_dialog, null)

        val btnClose = view.findViewById<Button>(R.id.btn_aceptar_dg)

        view.tv_mensaje_dialog.text=mensaje

        btnClose.setOnClickListener {

            dialog?.dismiss()
            getHome()

        }

        dialog?.setCancelable(false)

        dialog?.setContentView(view)

        dialog?.show()

    }

    private fun getDetailCobro()
    {
        var fr = activity?.supportFragmentManager?.beginTransaction()
        fr?.replace(R.id.container_frag_transactions, DetailCobroFragment())
        fr?.commit()
    }

    private fun getHome()
    {
        val intent= Intent(context, HomeActivity::class.java)
        startActivity(intent)
        activity?.finish()

    }

    override fun onBackPressed(): Boolean {
        return true
    }


}
package com.paguelofacil.posfacil.ui.view.transactions.payment.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.paguelofacil.posfacil.ApplicationClass
import com.paguelofacil.posfacil.R
import com.paguelofacil.posfacil.databinding.FragmentEnvioComprobanteBinding
import com.paguelofacil.posfacil.model.ComprobanteRequest
import com.paguelofacil.posfacil.model.RefundResult
import com.paguelofacil.posfacil.ui.interfaces.IOnBackPressed
import com.paguelofacil.posfacil.ui.view.adapters.Data
import com.paguelofacil.posfacil.ui.view.home.activities.HomeActivity
import com.paguelofacil.posfacil.ui.view.transactions.payment.viewmodel.ComprobanteViewModel
import com.paguelofacil.posfacil.util.networkErrorConverter
import kotlinx.android.synthetic.main.bottom_sheet_dialog.view.*
import kotlinx.coroutines.launch
import timber.log.Timber


class EnvioComprobanteFragment : Fragment(), IOnBackPressed {

    lateinit var binding: FragmentEnvioComprobanteBinding
    private val viewModel: ComprobanteViewModel by activityViewModels()
    private var detail: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEnvioComprobanteBinding.inflate(inflater, container, false)

        //binding.tvTitle.text = ApplicationClass.language.
        binding.comprobanteSend.text = ApplicationClass.language.sendVoucher
        binding.emailtv.text = ApplicationClass.language.email
        binding.phonetv.text = ApplicationClass.language.phone
        binding.btnSendVoucher.text = ApplicationClass.language.send
        binding.tvTitle.text = ApplicationClass.language.voucher

        loadListeners()

        return binding.root

    }

    private fun loadListeners() {

        binding.ivBack.setOnClickListener {
            Timber.e("IVBACK COMPROBANTE")
            activity?.finish()
        }

        binding.btnSendVoucher.setOnClickListener{
            sendComprobante()
        }

        binding.cbPhone.setOnCheckedChangeListener { compoundButton, b ->

            if (b) {
                binding.etPhone.visibility = View.VISIBLE
            } else {
                binding.etPhone.visibility = View.GONE
            }

        }

        binding.cbEmail.setOnCheckedChangeListener { compoundButton, b ->

            if (b) {
                binding.etEmail.visibility = View.VISIBLE
            } else {
                binding.etEmail.visibility = View.GONE
            }
        }

    }

    private fun sendComprobante(){
        Timber.e("DATA ${arguments?.getString("PRUEBA")}")
        lifecycleScope.launch {
            viewModel.sendComprobante(
                request = ComprobanteRequest(
                    codOper = arguments?.getString("PRUEBA") ?: "",
                    email = binding.etEmail.text.toString(),
                    phone = binding.etPhone.text.toString()
                ),
                onSuccess = {
                    showBottomSheet(getString(R.string.voucher_enviado))
                },
                onFailure = {
                    showWarningDialog(it){
                        sendComprobante()
                    }
                }
            )
        }
    }

    private fun showWarningDialog(message: String, onFailure: ()-> Unit){
        val dialog = context?.let { BottomSheetDialog(it) }

        val view = layoutInflater.inflate(R.layout.bottom_sheet_warning, null)
        val title =view.findViewById<TextView>(R.id.titleError)
        val description =view.findViewById<TextView>(R.id.descriptionError)
        val btn = view.findViewById<MaterialButton>(R.id.btnAccept)

        title.text = ApplicationClass.language.error
        description.text = if ((message == "400") or (message == "400") or (message == "400")){
            ApplicationClass.language.check_data
        }else{
            networkErrorConverter(message)
        }

        btn.text = ApplicationClass.language.try_againg
        btn.setOnClickListener {
            dialog?.dismiss()
            onFailure()
        }

        dialog?.setCancelable(true)

        dialog?.setContentView(view)

        dialog?.show()
    }

    private fun showBottomSheet(mensaje: String) {

        val dialog = context?.let { BottomSheetDialog(it) }

        val view = layoutInflater.inflate(R.layout.bottom_sheet_dialog, null)

        val btnClose = view.findViewById<Button>(R.id.btn_aceptar_dg)

        view.tv_mensaje_dialog.text = mensaje

        btnClose.setOnClickListener {

            dialog?.dismiss()
            getHome()

        }

        dialog?.setCancelable(false)

        dialog?.setContentView(view)

        dialog?.show()

    }

    private fun getDetailCobro() {
        var fr = activity?.supportFragmentManager?.beginTransaction()
        fr?.replace(R.id.container_frag_transactions, DetailCobroFragment())
        fr?.commit()
    }

    private fun getHome() {
        val intent = Intent(context, HomeActivity::class.java)
        startActivity(intent)
        activity?.finish()

    }

    override fun onBackPressed(): Boolean {
        return true
    }


}
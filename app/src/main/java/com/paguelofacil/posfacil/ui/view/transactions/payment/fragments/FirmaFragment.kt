package com.paguelofacil.posfacil.ui.view.transactions.payment.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.paguelofacil.posfacil.ApplicationClass
import com.paguelofacil.posfacil.R
import com.paguelofacil.posfacil.databinding.FragmentFirmaBinding
import com.paguelofacil.posfacil.ui.view.home.activities.HomeActivity
import com.paguelofacil.posfacil.ui.view.transactions.payment.viewmodel.CobroViewModel


class FirmaFragment : Fragment() {

    lateinit var binding: FragmentFirmaBinding
    private val viewModel: CobroViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFirmaBinding.inflate(inflater, container, false)

        binding.tvTitle.text = ApplicationClass.language.validacionPago
        binding.textViewRegalanosFirma.text = ApplicationClass.language.titleFirma
        binding.textViewMessage.text = ApplicationClass.language.subtitleFirma
        binding.btnFirmar.text = ApplicationClass.language.finalizar

        loadListeners()
        loadLanguage()
        return binding.root
    }

    private fun loadListeners() {
        binding.imageViewClearSignature.setOnClickListener { binding.drawByTouch.clearTouch() }

        binding.btnFirmar.setOnClickListener { goDetailPay() }

        binding.ivBack.setOnClickListener { showBottomSheet() }

    }

    private fun loadLanguage() {
        binding.tvTitle.text = ApplicationClass.language.validacionPago
        binding.textViewRegalanosFirma.text = ApplicationClass.language.titleFirma
        binding.textViewMessage.text = ApplicationClass.language.subtitleFirma
        binding.btnFirmar.text = ApplicationClass.language.finalizar
    }

    private fun goDetailPay() {
        val bundle = Bundle()
        val email = arguments?.getString("EMAIL")
        val phone = arguments?.getString("PHONE")
        val frg = SendReceiptPaymentFragment()
        bundle.putString("EMAIL", email)
        bundle.putString("PHONE", phone)
        bundle.putString("CARD_NUMBER", viewModel.mutableCardNumberSuccess.value ?: "")
        frg.setArguments(bundle)
        val fr = activity?.supportFragmentManager?.beginTransaction()
        fr?.replace(R.id.container_frag_cobro, SendReceiptPaymentFragment())
        fr?.commit()
    }

    private fun showBottomSheet() {
        val dialog = context?.let { BottomSheetDialog(it) }
        val view = layoutInflater.inflate(R.layout.bottom_sheet_cancel, null)
        val btnBack = view.findViewById<Button>(R.id.btn_volver)
        val btnCancel = view.findViewById<Button>(R.id.btn_si_cancelar)
        val ivCancel = view.findViewById<ImageView>(R.id.iv_close_dg)

        btnBack.text = ApplicationClass.language.volver

        btnBack.setOnClickListener { dialog?.dismiss() }

        ivCancel.setOnClickListener { dialog?.dismiss() }

        btnCancel.setOnClickListener {
            dialog?.dismiss()
            goHome()
        }

        dialog?.setCancelable(true)
        dialog?.setContentView(view)
        dialog?.show()
    }

    private fun goHome() {
        val intent = Intent(context, HomeActivity::class.java)
        startActivity(intent)
        activity?.finish()
    }


}
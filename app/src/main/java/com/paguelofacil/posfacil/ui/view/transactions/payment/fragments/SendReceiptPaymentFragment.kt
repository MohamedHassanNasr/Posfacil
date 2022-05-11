package com.paguelofacil.posfacil.ui.view.transactions.payment.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.paguelofacil.posfacil.ApplicationClass
import androidx.lifecycle.lifecycleScope
import com.paguelofacil.posfacil.data.entity.CobroEntity
import com.paguelofacil.posfacil.databinding.FragmentSendReceiptPaymentBinding
import com.paguelofacil.posfacil.model.ComprobanteRequest
import com.paguelofacil.posfacil.ui.view.home.activities.HomeActivity
import com.paguelofacil.posfacil.ui.view.transactions.payment.viewmodel.CobroViewModel
import kotlinx.coroutines.launch

class SendReceiptPaymentFragment : Fragment() {

    lateinit var binding: FragmentSendReceiptPaymentBinding
    private val viewModel: CobroViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSendReceiptPaymentBinding.inflate(inflater, container, false)

        loadListeners()
        loadLanguage()
        loadData()

        return binding.root
    }

    private fun loadListeners() {
        binding.ivBack.setOnClickListener { goHome() }

        binding.btnOtherTransaction.setOnClickListener { goHome() }
    }

    private fun loadLanguage() {
        binding.tvTitle.text = ApplicationClass.language.sendVoucher
        binding.tvMadeAPaymentOf.text = ApplicationClass.language.importeCobrado
        binding.tvCurrencySign.text = ApplicationClass.language.currency
        binding.textView5.text = ApplicationClass.language.importeBase
        binding.tvReceivedBy.text = ApplicationClass.language.impuestoReceiptDetail
        binding.textView.text = ApplicationClass.language.propina
        binding.textView3.text = ApplicationClass.language.metodoPago
        binding.tvDateHint.text = ApplicationClass.language.date
        binding.tvCodeOperation.text = ApplicationClass.language.codeOperation
        binding.btnOtherTransaction.text = ApplicationClass.language.otherTransaction
    }

    private fun goHome() {
        val intent = Intent(context, HomeActivity::class.java)
        startActivity(intent)
        activity?.finish()
    }

    private fun loadData() {
        //TODO ESPERAR QUE CRISTIAN LOGRE REALIZAR EL COBRO PARA REALIZAR EL COMPROBANTE
        val email = arguments?.getString("EMAIL")
        val phone = arguments?.getString("PHONE")
        val cardNumber = arguments?.getString("CARD_NUMBER")

        binding.textViewCardNumber.text = viewModel.mutableCardNumberSuccess.value ?:"TEST"

        viewModel.liveDataCobro.value?.run {
            binding.tvPaymentAmount.text = this.importeTotal
            binding.textView10.text = this.importeCobro
            binding.tvReceivedBy2.text = this.impuesto
            binding.textView2.text = this.propina
        }
    }
}
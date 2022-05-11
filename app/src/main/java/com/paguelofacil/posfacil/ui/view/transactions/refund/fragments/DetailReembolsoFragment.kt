package com.paguelofacil.posfacil.ui.view.transactions.refund.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import com.paguelofacil.posfacil.ApplicationClass
import com.paguelofacil.posfacil.R
import com.paguelofacil.posfacil.databinding.FragmentDetailReembolsoBinding
import com.paguelofacil.posfacil.model.Transaction
import com.paguelofacil.posfacil.ui.view.transactions.payment.fragments.EnvioComprobanteFragment
import timber.log.Timber


class DetailReembolsoFragment : Fragment() {

    lateinit var binding: FragmentDetailReembolsoBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding=FragmentDetailReembolsoBinding.inflate(inflater,container,false)

        binding.montoText.text = ApplicationClass.language.monto
        binding.itbmText.text = ApplicationClass.language.itbms
        binding.propinaText.text = ApplicationClass.language.propina
        binding.totalText.text = ApplicationClass.language.total
        binding.tipoText.text = ApplicationClass.language.tipo
        binding.paymentText.text = ApplicationClass.language.metodoPago
        binding.codOperText.text = ApplicationClass.language.codeOperation
        binding.statusText.text = ApplicationClass.language.transactionStatus
        binding.tvTitle.text = ApplicationClass.language.detalleReembolso

        loadListeners()

        loadData()

        return binding.root
    }

    private fun loadListeners() {

        binding.btnResendVoucher.setOnClickListener{

            var fr = activity?.supportFragmentManager?.beginTransaction()
            val frg = EnvioComprobanteFragment()
            val bundle = Bundle()
            bundle.putString("PRUEBA", binding.tvOpCode.text.toString())
            frg.setArguments(bundle)
            fr?.replace(R.id.container_frag_transactions, frg)
            fr?.commit()


        }

        binding.ivBack.setOnClickListener {

            activity?.finish()
        }

    }

    private fun loadData() {
        val detail = arguments?.getParcelable<Transaction>("detail")
        Timber.e("BODY $detail")
        binding.tvMainTotalAmount.text = detail?.negativeCurrencyAmountStr
        binding.tvCode.text = detail?.opCode.toString()
        binding.tvDatetime.text = detail?.formattedDateTime
        binding.tvAmount.text = detail?.currencyOriginalAmountStr
        binding.tvTaxes.text = detail?.currencyTaxAmountStr
        binding.tvTip.text = detail?.currencyTipAmountStr
        binding.tvTotalAmount.text = detail?.currencyAmountStr
        binding.tvType.text = detail?.typeStr
//        binding.tvPaymentMethod.text = String.format(getString(R.string.visible_card_pattern), detail?.shortVisibleCardNumber)
        binding.tvPaymentMethod.text = detail?.cardNumber
        binding.tvPaymentMethod.setCompoundDrawablesRelativeWithIntrinsicBounds(
            AppCompatResources.getDrawable(requireContext(),
                if(detail?.cardType.equals("Visa", true))
                    R.drawable.visa else R.drawable.ic_mastercard_logo),
            null, null, null)
        binding.tvOpCode.text = detail?.opCode.toString()
        //binding.tvStatus.setText()
    }
}
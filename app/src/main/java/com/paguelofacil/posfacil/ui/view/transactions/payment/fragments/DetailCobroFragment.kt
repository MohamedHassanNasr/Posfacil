package com.paguelofacil.posfacil.ui.view.transactions.payment.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.paguelofacil.posfacil.ApplicationClass
import com.paguelofacil.posfacil.R
import com.paguelofacil.posfacil.databinding.FragmentDetailCobroBinding
import com.paguelofacil.posfacil.model.Transaction
import com.paguelofacil.posfacil.model.TransactionBundle
import com.paguelofacil.posfacil.ui.view.transactions.payment.viewmodel.DetailCobroViewModel
import com.paguelofacil.posfacil.ui.view.transactions.refund.fragments.ReembolsoFragment
import timber.log.Timber


class DetailCobroFragment : Fragment() {
    lateinit var binding: FragmentDetailCobroBinding
    private val viewModel: DetailCobroViewModel by activityViewModels()
    private var logo: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentDetailCobroBinding.inflate(inflater, container, false)

        binding.btnResendVoucher.text = ApplicationClass.language.voucher
        binding.montoText.text = ApplicationClass.language.monto
        binding.itbmText.text = ApplicationClass.language.itbms
        binding.propinaText.text = ApplicationClass.language.propina
        binding.totalText.text = ApplicationClass.language.total
        binding.tipoText.text = ApplicationClass.language.tipo
        binding.paymentText.text = ApplicationClass.language.metodoPago
        binding.codOperText.text = ApplicationClass.language.codeOperation
        binding.statusText.text = ApplicationClass.language.transactionStatus
        binding.tvTitle.text = ApplicationClass.language.detalleCobro
        binding.btnReembolar.text = ApplicationClass.language.reembolsar
        loadData()

        loadListeners()

        return binding.root
    }

    private fun loadListeners() {

        binding.btnResendVoucher.setOnClickListener {

            var fr = activity?.supportFragmentManager?.beginTransaction()
            val frg = EnvioComprobanteFragment()
            val bundle = Bundle()
            bundle.putString("PRUEBA", binding.tvOpCode.text.toString())
            frg.setArguments(bundle)
            fr?.replace(R.id.container_frag_transactions, frg)
            fr?.commit()

        }

        binding.btnReembolar.setOnClickListener {
            Timber.e("ddd ${binding.tvTotalAmount.text.toString()}")
            var fr = activity?.supportFragmentManager?.beginTransaction()
            val fragment = ReembolsoFragment()
            val bundle = Bundle()
            bundle.putParcelable(
                "detailTX",
                TransactionBundle(
                    opCode = binding.tvOpCode.text.toString(),
                    amount = binding.tvTotalAmount.text.toString(),
                    cardNumber = binding.tvPaymentMethod.text.toString(),
                    cardType = logo
                )
            )
            fragment.arguments = bundle
            fr?.replace(R.id.container_frag_transactions, fragment)
            fr?.commit()
        }

        binding.ivBack.setOnClickListener {
            activity?.finish()
        }

    }

    private fun loadData() {
        val detail = arguments?.getParcelable<Transaction>("detail")

        Timber.e("DETA $detail")
        binding.tvMainTotalAmount.text = if (detail?.opCode?.contains("REV") == true) {
            detail?.negativeCurrencyAmountStr
        } else {
            detail?.possitiveCurrencyAmountStr
        }
        binding.tvMainTotalAmount.setTextColor(
            if (detail?.opCode?.contains("REV") == true) {
                ContextCompat.getColor(requireActivity(), R.color.color_EDB600)
            } else {
                ContextCompat.getColor(requireActivity(), R.color.color_4CA80B)
            }
        )
        binding.imageView3.setImageResource(
            if (detail?.opCode?.contains("REV") == true) {
                R.drawable.ic_send_black
            } else {
                R.drawable.ic_cobro_black
            }
        )
        binding.tvCode.text = detail?.opCode.toString()
        binding.tvDatetime.text = detail?.formattedDateTime
        binding.tvAmount.text = String.format("$%.2f", detail?.amount)
        binding.tvTaxes.text = String.format("$%.2f", detail?.tax)
        binding.tvTip.text = String.format("$%.2f", detail?.tip)
        binding.tvTotalAmount.text = String.format(
            "$%.2f",
            ((detail?.amount ?: 0f).toFloat() + (detail?.tax ?: 0f).toFloat() + (detail?.tip
                ?: 0f).toFloat())
        )
        binding.tvType.text = if (detail?.opCode?.contains("REV") == true) {
            "Reembolso"
        } else {
            "Cobro"
        }
//        binding.tvPaymentMethod.text = String.format(getString(R.string.visible_card_pattern), detail?.shortVisibleCardNumber)
        binding.tvPaymentMethod.text = detail?.cardNumber
        binding.logoDetails.setImageResource(
            when(detail?.cardType){
                "VISA"->{
                    R.drawable.visa
                }
                "MC"->{
                    R.drawable.ic_mastercard_logo
                }
                "WALLET"->{
                    R.drawable.ic_app_icon
                }
                else -> {
                    R.drawable.ic_app_icon
                }
            })
        binding.tvPaymentMethod.text = detail?.cardNumber
        binding.tvOpCode.text = detail?.opCode.toString()
        binding.tvStatus.text = detail?.status
        if (detail?.opCode?.contains("REV") == true) {
            binding.btnReembolar.visibility = View.GONE
        } else {
            binding.btnReembolar.visibility = View.VISIBLE
        }

        logo = detail?.cardType ?: ""
    }
}
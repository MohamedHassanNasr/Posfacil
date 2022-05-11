package com.paguelofacil.posfacil.ui.view.transactions.refund.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.paguelofacil.posfacil.ApplicationClass
import com.paguelofacil.posfacil.R
import com.paguelofacil.posfacil.databinding.FragmentComprobanteReembolsoBinding
import com.paguelofacil.posfacil.model.RefundResult
import com.paguelofacil.posfacil.model.TransactionBundle
import com.paguelofacil.posfacil.ui.view.adapters.Data
import com.paguelofacil.posfacil.ui.view.home.activities.HomeActivity
import com.paguelofacil.posfacil.ui.view.home.fragments.HomeFragment
import com.paguelofacil.posfacil.ui.view.transactions.payment.fragments.DetailCobroFragment
import com.paguelofacil.posfacil.ui.view.transactions.payment.fragments.EnvioComprobanteFragment
import com.paguelofacil.posfacil.ui.view.transactions.payment.fragments.TransactionsFragment
import com.paguelofacil.posfacil.util.dateFormattedByDate
import com.paguelofacil.posfacil.util.dateFormattedByHour
import timber.log.Timber


class ComprobanteReembolsoFragment : Fragment() {


    lateinit var binding: FragmentComprobanteReembolsoBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding=FragmentComprobanteReembolsoBinding.inflate(inflater,container,false)

        binding.tvMadeAPaymentOf.text = ApplicationClass.language.importeReembolsado
        binding.tvDateHint.text = ApplicationClass.language.date
        binding.textView3.text = ApplicationClass.language.metodoPago
        binding.textView11.text = ApplicationClass.language.motivo
        binding.textView13.text = ApplicationClass.language.codeOperation
        binding.btnTransaction.text = ApplicationClass.language.volverTransactions
        binding.btnResendVoucher.text = ApplicationClass.language.resendReceipt

        loadListeners()

        loadData()

        return binding.root

    }

    private fun loadData(){
        val detail = arguments?.getParcelable<RefundResult>("data")
        Timber.e("DATA $detail")
        binding.tvDate.text = "${dateFormattedByDate(detail?.date ?: "")} ${dateFormattedByHour(detail?.date ?: "", true)}"
        binding.tvPaymentAmount.text = String.format("%.2f", detail?.amount?.toDouble() ?: 0.00)
        binding.textView4.text = detail?.cardNumber
        binding.textView12.text = detail?.motivo
        binding.textView14.text = detail?.opCode
        binding.imageView.setImageResource(
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
                else->{
                    R.drawable.ic_app_icon
                }
            }
        )
    }

    private fun loadListeners() {

        binding.btnResendVoucher.setOnClickListener {

            var fr = activity?.supportFragmentManager?.beginTransaction()
            val frg = EnvioComprobanteFragment()
            val bundle = Bundle()
            bundle.putString("PRUEBA", binding.textView14.text.toString())
            frg.setArguments(bundle)
            fr?.replace(R.id.container_frag_transactions, frg)
            fr?.commit()

        }

        binding.btnTransaction.setOnClickListener {

            /*var fr = activity?.supportFragmentManager?.beginTransaction()
            fr?.replace(R.id.container_frag_transactions, TransactionsFragment())
            fr?.commit()*/
            /*activity?.supportFragmentManager?.popBackStack();*/
            /*val intent = Intent(context, HomeActivity::class.java)
            startActivity(intent)
            activity?.finish()*/
            activity?.finish()
        }

        binding.ivBack.setOnClickListener{

            /*var fr = activity?.supportFragmentManager?.beginTransaction()
            fr?.replace(R.id.container_frag_transactions, HomeFragment())
            fr?.commit()*/
            val intent = Intent(context, HomeActivity::class.java)
            startActivity(intent)
            activity?.finish()

        }

    }


}
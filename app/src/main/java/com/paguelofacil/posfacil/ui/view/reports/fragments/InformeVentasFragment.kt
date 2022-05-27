package com.paguelofacil.posfacil.ui.view.reports.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.paguelofacil.posfacil.ApplicationClass
import com.paguelofacil.posfacil.base.BaseFragment
import com.paguelofacil.posfacil.databinding.FragmentInformeVentasBinding
import com.paguelofacil.posfacil.model.TransactionByUser
import com.paguelofacil.posfacil.repository.UserRepo
import com.paguelofacil.posfacil.ui.view.adapters.TransactionByUserListAdapter
import com.paguelofacil.posfacil.util.getRamdomColor
import kotlinx.coroutines.launch
import timber.log.Timber

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [InformeVentasFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class InformeVentasFragment : BaseFragment() {
    private val viewModelTransaction: InformeVentasViewModel by activityViewModels()
    lateinit var binding: FragmentInformeVentasBinding
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= FragmentInformeVentasBinding.inflate(inflater,container,false)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        Timber.e("PARAM $param1 $param2")
        lifecycleScope.launch {
            viewModelTransaction.getReportesVentas(Sys)
        }
        initObservers()
        binding.titleResumen.text = ApplicationClass.language.resumenVentasHoy
        binding.titlePayment.text = ApplicationClass.language.metodoPago
        binding.cantTxs.text = ApplicationClass.language.cantTransacciones
        binding.cantidadRefund.text = ApplicationClass.language.cantReembolsos
        binding.amountTxs.text = ApplicationClass.language.montoTransacciones
        binding.totalToReveive.text = ApplicationClass.language.totalRecibir
        binding.paymentVisa.text = ApplicationClass.language.montoVisa
        binding.paymentMc.text = ApplicationClass.language.montoMastercard
        binding.paymentPf.text = ApplicationClass.language.montoPaguelofacil
        binding.txsUser.text = ApplicationClass.language.transaccionesUsuario
        binding.amountRefund.text = ApplicationClass.language.montoReembolsos

        return binding.root

    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initObservers() {

        val recycler = binding.rvTransactionsByUsers
        val listFormatted = mutableListOf<TransactionByUser>()
        val cardPercet = binding.cardPercent

        /*if (listFormatted.isEmpty()){
            binding.cardRv.visibility = View.GONE
        }else{
            binding.cardRv.visibility = View.VISIBLE
        }*/

        recycler.hasFixedSize()
        recycler.adapter = TransactionByUserListAdapter(listFormatted)

        viewModelTransaction.liveDataTransactionList.observe(viewLifecycleOwner){
            listFormatted.clear()
            try {
                UserRepo.getUser()?.let {user->
                    user.merchantProfile?.idProfile?.let {rolId->
                        if (rolId != 3){
                            it.data.operatorsTxs.filter { item-> item.idUser == user.id }.let {list->
                                binding.transactionsSize.text = if (it.data.txs.toString().length == 1){it.data.txs.toString().padStart(2, '0')}else{it.data.txs.toString()}
                                binding.transactionsAmount.text = String.format("%.2f", it.data.amount).replace(',', '.')
                                binding.rembolsoAmount.text = String.format("%.2f", it.data.refunds).replace(',', '.')
                                binding.rembolsoSize.text = if (it.data.refundsTxs.toString().length == 1){it.data.refundsTxs.toString().padStart(2, '0')}else{it.data.refundsTxs.toString()}
                                //Timber.e("LSISISI ${it.data.paymentMethods.filter { it.name == "VISA" }}")
                                binding.visaAmount.text = if (it.data.paymentMethods.isNotEmpty()){
                                    String.format("%.2f", it.data.paymentMethods.filter { it.name == "VISA" }.fold(
                                        initial = 0f, operation = {acc, paymentMethods ->
                                            acc + paymentMethods.total.toFloat()
                                        }
                                    )).replace(',', '.')
                                }else{
                                    "0.00"
                                }
                                binding.mastecardAmount.text = if (it.data.paymentMethods.isNotEmpty()){
                                    String.format("%.2f", it.data.paymentMethods.filter { it.name == "MC" }.fold(
                                        initial = 0f, operation = {acc, paymentMethods ->
                                            acc + paymentMethods.total.toFloat()
                                        }
                                    )).replace(',', '.')
                                }else{
                                    "0.00"
                                }
                                binding.pagueloFacilAmount.text = if (it.data.paymentMethods.isNotEmpty()){
                                    String.format("%.2f", it.data.paymentMethods.filter { it.name == "WALLET" }.fold(
                                        initial = 0f, operation = {acc, paymentMethods ->
                                            acc + paymentMethods.total.toFloat()
                                        }
                                    )).replace(',', '.')
                                }else{
                                    "0.00"
                                }
                                binding.totalReceive.text = String.format("%.2f", (it.data.amount - it.data.refunds).toFloat()).replace(',', '.')

                                it.data.operatorsTxs.map { operator->
                                    listFormatted.add(TransactionByUser(operator.name, operator.txs, operator.total, color = getRamdomColor()))
                                }

                                recycler.adapter?.notifyDataSetChanged()
                                val percentMax = listFormatted.fold(initial = 0f, operation = {acc, transactionByUser ->
                                    acc + transactionByUser.transactionSize
                                })

                                cardPercet.removeAllViews()
                                if (listFormatted.isNotEmpty()){
                                    listFormatted.forEach {txs->
                                        val cardView = CardView(requireActivity())
                                        val percent = ((txs.transactionSize * 100) / percentMax) / 100
                                        val layoutparams = LinearLayout.LayoutParams(
                                            RelativeLayout.LayoutParams.WRAP_CONTENT,
                                            40,
                                            100f * percent
                                        )

                                        cardView.layoutParams = layoutparams
                                        cardView.setBackgroundColor(txs.color!!)
                                        cardPercet.addView(cardView)

                                    }
                                }else{
                                    binding.cardRv.visibility = View.GONE
                                }
                            }
                        }else{
                            it?.let {
                                binding.transactionsSize.text = if (it.data.txs.toString().length == 1){it.data.txs.toString().padStart(2, '0')}else{it.data.txs.toString()}
                                binding.transactionsAmount.text = String.format("%.2f", it.data.amount).replace(',', '.')
                                binding.rembolsoAmount.text = String.format("%.2f", it.data.refunds).replace(',', '.')
                                binding.rembolsoSize.text = if (it.data.refundsTxs.toString().length == 1){it.data.refundsTxs.toString().padStart(2, '0')}else{it.data.refundsTxs.toString()}
                                //Timber.e("LSISISI ${it.data.paymentMethods.filter { it.name == "VISA" }}")
                                binding.visaAmount.text = if (it.data.paymentMethods.isNotEmpty()){
                                    String.format("%.2f", it.data.paymentMethods.filter { it.name == "VISA" }.fold(
                                        initial = 0f, operation = {acc, paymentMethods ->
                                            acc + paymentMethods.total.toFloat()
                                        }
                                    )).replace(',', '.')
                                }else{
                                    "0.00"
                                }
                                binding.mastecardAmount.text = if (it.data.paymentMethods.isNotEmpty()){
                                    String.format("%.2f", it.data.paymentMethods.filter { it.name == "MC" }.fold(
                                        initial = 0f, operation = {acc, paymentMethods ->
                                            acc + paymentMethods.total.toFloat()
                                        }
                                    )).replace(',', '.')
                                }else{
                                    "0.00"
                                }
                                binding.pagueloFacilAmount.text = if (it.data.paymentMethods.isNotEmpty()){
                                    String.format("%.2f", it.data.paymentMethods.filter { it.name == "WALLET" }.fold(
                                        initial = 0f, operation = {acc, paymentMethods ->
                                            acc + paymentMethods.total.toFloat()
                                        }
                                    )).replace(',', '.')
                                }else{
                                    "0.00"
                                }
                                binding.totalReceive.text = String.format("%.2f", (it.data.amount - it.data.refunds).toFloat()).replace(',', '.')

                                it.data.operatorsTxs.map { operator->
                                    listFormatted.add(TransactionByUser(operator.name, operator.txs, operator.total, color = getRamdomColor()))
                                }

                                recycler.adapter?.notifyDataSetChanged()
                                val percentMax = listFormatted.fold(initial = 0f, operation = {acc, transactionByUser ->
                                    acc + transactionByUser.transactionSize
                                })

                                cardPercet.removeAllViews()
                                if (listFormatted.isNotEmpty()){
                                    listFormatted.forEach {txs->
                                        val cardView = CardView(requireActivity())
                                        val percent = ((txs.transactionSize * 100) / percentMax) / 100
                                        val layoutparams = LinearLayout.LayoutParams(
                                            RelativeLayout.LayoutParams.WRAP_CONTENT,
                                            40,
                                            100f * percent
                                        )

                                        cardView.layoutParams = layoutparams
                                        cardView.setBackgroundColor(txs.color!!)
                                        cardPercet.addView(cardView)

                                    }
                                }else{
                                    binding.cardRv.visibility = View.GONE
                                }
                            }
                        }
                    }
                }
            }catch (e: Exception){
                Timber.e("EXCEPTION $e")
                Toast.makeText(activity, "No se encontraron cobros realizados"/*ApplicationClass.language.no_charge_done*/, Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment InformeVentasFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            InformeVentasFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
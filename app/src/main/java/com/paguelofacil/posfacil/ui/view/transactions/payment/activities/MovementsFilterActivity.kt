package com.paguelofacil.posfacil.ui.view.transactions.payment.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.setPadding
import androidx.lifecycle.lifecycleScope
import com.paguelofacil.posfacil.ApplicationClass
import com.paguelofacil.posfacil.base.BaseActivity
import com.paguelofacil.posfacil.data.network.api.ApiError
import com.paguelofacil.posfacil.databinding.ActivityMovementsFilterBinding
import com.paguelofacil.posfacil.ui.view.reports.fragments.InformeVentasViewModel
import com.pax.dal.entity.ETermInfoKey
import kotlinx.coroutines.launch
import timber.log.Timber

class MovementsFilterActivity : BaseActivity() {

    lateinit var binding:ActivityMovementsFilterBinding
    var parent = listOf<Int>(0, 0)
    var current = 0
    var statusAprov = false
    var statusDeclined = false
    /** metodo de pago*/
    var parentPay = listOf<Int>(0, 0, 0)
    var currentPay = 0
    var statusVisa = false
    var statusMC = false
    var statusPf = false
    var operatorCurrent: Int? = null
    val currentUser = mutableListOf<Int>()
    val statusUser = mutableListOf<Pair<Int, Boolean>>()
    var idCheck: Int? = null

    private val viewModel: InformeVentasViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding= ActivityMovementsFilterBinding.inflate(layoutInflater)

        setContentView(binding.root)

        Timber.e("SSS ${ApplicationClass.language.filter}  ${ApplicationClass.language.selectFilter} ")

        binding.rdbVisa.text = ApplicationClass.language.cardFilterVisa
        binding.rdbMastercard.text = ApplicationClass.language.cardFilterMastercard
        binding.rdbWallet.text = ApplicationClass.language.cardFilterPaguelofacil
        binding.tvFilterByPaymentMethod.text = ApplicationClass.language.methodPayFilter
        binding.tvFilterByOperator.text = ApplicationClass.language.userFilters
        binding.btnAplicarFiltros.text = ApplicationClass.language.selectFilter
        binding.tvFilterByStatus.text = ApplicationClass.language.transactionStatus
        binding.tvTitle.text = ApplicationClass.language.filter
        binding.rdbApproved.text = ApplicationClass.language.successTransaction
        binding.rdbDeclined.text = ApplicationClass.language.errorTransactions
        loadListeners()

        loadFilters()
    }

    override fun onExceptionData(requestCode: Int, exception: ApiError, data: Any?) {

    }

    private fun loadFilters() {
        val bundle = intent.extras
        val role = bundle?.getString("role")

        if(role != "Admin") {
            binding.rdgUsers.visibility = View.GONE
            binding.tvFilterByOperator.visibility = View.GONE
        }else{
            binding.rdgUsers.visibility = View.VISIBLE
            binding.tvFilterByOperator.visibility = View.VISIBLE
        }

        val filtersApplied = bundle?.getBoolean("filters_applied")
        filtersApplied?.let {
            if(it) {
                val resultFilter = bundle.getString("result")
                val paymentMethodFilter = bundle.getString("payment_method")
                idCheck = bundle.getInt("operator")

                resultFilter?.let { result ->
                    binding.rdgStatusTransaction.check(
                        if(result == "APPROVED") binding.rdbApproved.id else binding.rdbDeclined.id
                    )
                }

                paymentMethodFilter?.let { paymentMethod ->
                    binding.rdgMetodoPago.check(
                        when(paymentMethod) {
                            "VISA" -> binding.rdbVisa.id
                            "MASTERCARD" -> binding.rdbMastercard.id
                            else -> binding.rdbWallet.id
                        }
                    )
                }
            }
        }
    }

    private fun loadListeners() {

        binding.ivBack.setOnClickListener{
            onBackPressed()
        }

        binding.rdgStatusTransaction.setOnCheckedChangeListener { radioGroup, i ->
            Timber.e("PARER $radioGroup $i")
            current = i
            if (binding.rdbApproved.isChecked){
                parent = listOf(i, 0)
            }else{
                parent = listOf(0, i)
            }
        }

        binding.rdbApproved.setOnClickListener {
            Timber.e("APROVVED ${binding.rdbApproved.isChecked}")
            statusAprov = !statusAprov
            statusDeclined = false
            if (!statusAprov){
                if (current == parent.first()){
                    binding.rdgStatusTransaction.clearCheck()
                }
            }
        }

        binding.rdbDeclined.setOnClickListener {
            Timber.e("DECLINED ${binding.rdbApproved.isChecked}")
            statusAprov = false
            statusDeclined = !statusDeclined
            if (!statusDeclined){
                if (current == parent.last()){
                    binding.rdgStatusTransaction.clearCheck()
                }
            }
        }

        /** metodo de pago*/
        binding.rdgMetodoPago.setOnCheckedChangeListener { radioGroup, i ->
            Timber.e("PARER $radioGroup $i")
            currentPay = i
            if (binding.rdbVisa.isChecked){
                parentPay = listOf(i, 0, 0)
            }else if (binding.rdbMastercard.isChecked){
                parentPay = listOf(0, i, 0)
            }else{
                parentPay = listOf(0, 0, i)
            }
        }

        binding.rdbVisa.setOnClickListener {
            Timber.e("VISA ${binding.rdbVisa.isChecked}")
            statusMC = false
            statusPf = false
            statusVisa = !statusVisa
            if (!statusVisa){
                Timber.e("VISA $currentPay ${parent[0]}")
                if (currentPay == parentPay[0]){
                    binding.rdgMetodoPago.clearCheck()
                }
            }
        }

        binding.rdbMastercard.setOnClickListener {
            Timber.e("VISA ${binding.rdbVisa.isChecked}")
            statusMC = !statusMC
            statusPf = false
            statusVisa = false
            if (!statusMC){
                if (currentPay == parentPay[1]){
                    binding.rdgMetodoPago.clearCheck()
                }
            }
        }

        binding.rdbWallet.setOnClickListener {
            Timber.e("VISA ${binding.rdbVisa.isChecked}")
            statusMC = false
            statusPf = !statusPf
            statusVisa = false
            if (!statusPf){
                if (currentPay == parentPay[2]){
                    binding.rdgMetodoPago.clearCheck()
                }
            }
        }

        getUsers(binding.rdgUsers)

        binding.btnAplicarFiltros.setOnClickListener {
            val resultId = binding.rdgStatusTransaction.checkedRadioButtonId
            val paymentMethodId = binding.rdgMetodoPago.checkedRadioButtonId
            val operatorId = binding.rdgUsers.checkedRadioButtonId

            val resultOption = findViewById<RadioButton>(resultId)
            val paymentMethodOption = findViewById<RadioButton>(paymentMethodId)
            val operatorOption = findViewById<RadioButton>(operatorId)

            val intent = Intent()
            intent.putExtra("result", resultOption?.text)
            intent.putExtra("payment_method", paymentMethodOption?.text)
            intent.putExtra("operator", operatorCurrent.toString())
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }

    private fun getUsers(rdgUserParent: RadioGroup) {
        lifecycleScope.launch {
            Sys?.termInfo?.let {
                viewModel.getReportesVentas(it[ETermInfoKey.SN] ?: "")
            }
        }
        val listCount = mutableListOf<Int>()

        viewModel.liveDataTransactionList.observe(this){reporte->
            var count = 0
            if (!reporte.data.operatorsTxs.isNullOrEmpty()){
                reporte.data.operatorsTxs.forEach { ope->
                    statusUser.add(Pair(ope.idUser.toInt(),false))
                    listCount.add(0)
                }
                reporte.data.operatorsTxs.forEachIndexed {index, operatorsTxs ->
                    currentUser.add(operatorsTxs.idUser.toInt())
                    val rb = RadioButton(this)
                    rb.text = operatorsTxs.name
                    rb.textSize = 18f
                    rb.setPadding(12)
                    rb.id = operatorsTxs.idUser.toInt()
                    rb.isChecked = operatorsTxs.idUser.toInt() == idCheck
                    rdgUserParent.addView(rb)

                    rb.setOnClickListener {
                        //listCount[index] = listCount[index]++
                        count++
                        Timber.e("COUNT ${count}")
                        val handle = Handler()
                        handle.postDelayed(Runnable {
                            if (count == 1){
                                Timber.e("IN 1")
                                operatorCurrent = rb.id

                            }else if (count == 2){
                                Timber.e("IN 2")
                                rdgUserParent.clearCheck()
                                operatorCurrent = null
                                count = 0
                            }
                        }, 100)
                    }

                }
/*
                rdgUserParent.getChildAt(rdgUserParent.checkedRadioButtonId).setOnClickListener {
                    statusUser.find { item-> item.first == rdgUserParent.checkedRadioButtonId}?.copy(second = !statusUser.find { item-> item.first == rdgUserParent.checkedRadioButtonId}?.second!!)

                    statusUser.filter { item-> item.first != rdgUserParent.checkedRadioButtonId}.map { result->
                        result.copy(second = false)
                    }

                    if (!statusUser.find { item-> item.first != rdgUserParent.checkedRadioButtonId }?.second!!){
                        rdgUserParent.clearCheck()
                    }
                }*/
                /*rb.setOnClickListener {
                    Timber.e("item ${rb.text}")

                    statusUser[index] = !statusUser[index]
                    if (!statusUser[index]){
                        rdgUserParent.clearCheck()
                    }
                    operatorCurrent = rb.id
                }*/

            }else{
                binding.rdgUsers.visibility = View.GONE
                binding.tvFilterByOperator.visibility = View.GONE
            }
        }
    }
}
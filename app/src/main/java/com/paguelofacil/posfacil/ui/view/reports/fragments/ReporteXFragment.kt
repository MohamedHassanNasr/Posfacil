package com.paguelofacil.posfacil.ui.view.reports.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.paguelofacil.posfacil.ApplicationClass
import com.paguelofacil.posfacil.R
import com.paguelofacil.posfacil.databinding.FragmentReporteXBinding
import com.paguelofacil.posfacil.model.*
import com.paguelofacil.posfacil.repository.UserRepo
import com.paguelofacil.posfacil.ui.view.adapters.*
import com.paguelofacil.posfacil.ui.view.home.fragments.HomeFragment
import com.paguelofacil.posfacil.util.dateFormattedByDate
import com.paguelofacil.posfacil.util.dateFormattedByHour
import com.paguelofacil.posfacil.util.networkErrorConverter
import kotlinx.android.synthetic.main.bottom_sheet_dialog.view.*
import kotlinx.coroutines.launch
import timber.log.Timber


class ReporteXFragment : Fragment() {

    private val viewModel: ReporteXViewModel by activityViewModels()
    lateinit var binding:FragmentReporteXBinding
    private var merchantFind: DataMerchant? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding=FragmentReporteXBinding.inflate(inflater,container,false)

        loadListeners()
        lifecycleScope.launch {
            viewModel.getMerchantDetail()
        }
        remote()
        setReportX()

        binding.titleReport.text = ApplicationClass.language.reporteX
        binding.txsTitle.text = ApplicationClass.language.transactionsMenu
        binding.cantsTxs.text = ApplicationClass.language.cantTransacciones
        binding.sellsTitle.text = ApplicationClass.language.ventas
        binding.baseTitle.text = ApplicationClass.language.importeBase
        binding.itbmsTitle.text = ApplicationClass.language.impuestos
        binding.tipsTitle.text = ApplicationClass.language.propina
        binding.refundsTitle.text = ApplicationClass.language.reembolso
        binding.totalTitle.text = ApplicationClass.language.total
        binding.detailsUser.text = ApplicationClass.language.detalleUsuario
        binding.dateTitle.text = ApplicationClass.language.date
        binding.btnEnviarReporteX.text = ApplicationClass.language.enviarReporte


        return binding.root

    }

    private fun remote(){
        try {
            lifecycleScope.launch {
                viewModel.getUserOwner()
                getReporteXApi()
            }
        }catch (e: Exception){
            showWarningDialog(e.message ?: "Error inesperado"){
                remote()
            }
        }
    }

    private suspend fun getReporteXApi(){
        viewModel.getReporteX(
            sendEmail = false,
            onSuccess ={

            },
            onFailure = {
                /*showWarningDialog(it){
                    lifecycleScope.launch {
                        getReporteXApi()
                    }
                }*/
            }
        )
    }

    private fun setUser(){
        viewModel.userValue.observe(this){
            //obtener el nombre merchant
        }
    }

    private fun showWarningDialog(message: String, onFailure: ()-> Unit){
        val dialog = context?.let { BottomSheetDialog(it) }

        val view = layoutInflater.inflate(R.layout.bottom_sheet_warning, null)
        val title =view.findViewById<TextView>(R.id.titleError)
        val description =view.findViewById<TextView>(R.id.descriptionError)
        val btn = view.findViewById<MaterialButton>(R.id.btnAccept)

        title.text = "!Ha ocurrido un error!"
        description.text = if ((message == "400") or (message == "400") or (message == "400")){
            ApplicationClass.language.errorPaidTryAgainOrContactOurSupportTeam
        }else{
            networkErrorConverter(message)
        }

        btn.text = "Intentar nuevamente"
        btn.setOnClickListener {
            dialog?.dismiss()
            onFailure()
        }

        dialog?.setCancelable(true)

        dialog?.setContentView(view)

        dialog?.show()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setReportX(){
        val recycler = binding.rvListTransactionX
        val recyclerPaymentMethodsGlobal = binding.rvListPaymentMethodsGlobal
        val listFormatted = mutableListOf<TransactionReportX>()
        val listPaymentMethodsGlobal = mutableListOf<PaymentMethodsGlobal>()
        val recyclerUser = binding.rvUserX
        val listUser = mutableListOf<UserReportX>()
        //val listPayment = mutableListOf<PaymentMethodsGlobal>()

        listFormatted.clear()
        //listPayment.clear()
        listPaymentMethodsGlobal.clear()
        listUser.clear()

        recycler.hasFixedSize()
        recycler.adapter = ListTransactionReportAdapter(listFormatted)

        recyclerPaymentMethodsGlobal.hasFixedSize()
        recyclerPaymentMethodsGlobal.adapter = ListPaymentMethodsGlobalAdapter(listPaymentMethodsGlobal)

        recyclerUser.hasFixedSize()
        recyclerUser.adapter = ReportXUserAdapter(listUserReporX = listUser)

        if (listUser.isEmpty()){
            binding.detailsUser.visibility = View.GONE
        }else{
            binding.detailsUser.visibility = View.VISIBLE
        }

        if (listFormatted.isEmpty()){
            binding.layoutTxsReport.visibility = View.GONE
            binding.titleReport.visibility = View.GONE
            binding.txsTitle.visibility = View.GONE
        }else{
            binding.layoutTxsReport.visibility = View.VISIBLE
            binding.titleReport.visibility = View.VISIBLE
            binding.txsTitle.visibility = View.VISIBLE
        }

        viewModel.merchant.observe(viewLifecycleOwner){merchantResponse->
            val user = UserRepo.getUser()
            Timber.e("USER MERCHAN ${String.format("%.0f", user.idMerchant?.toFloat())} Merchan ${merchantResponse.data[0].idMerchant}")
            merchantFind = merchantResponse.data.find { merchant-> merchant.idMerchant.toLong().toString() == String.format("%.0f", user.idMerchant?.toFloat()) }
            user.idMerchant?.let {
                binding.propietario.text = merchantFind?.legalName ?: ""
                binding.idPos.text = merchantFind?.idMerchant.toString()
                binding.dateReportSellX.text = dateFormattedByDate(merchantResponse.serverTime)
                binding.hourDate.text = dateFormattedByHour(merchantResponse.serverTime, getHourCode = true)
            }
        }

        viewModel.liveDataTransactionList.observe(viewLifecycleOwner){
            try {
                listFormatted.clear()
                //listPayment.clear()
                listPaymentMethodsGlobal.clear()
                listUser.clear()
                Timber.e("XREPORT ${it.data.operators}")

                val listUserGrpuped = it.data.operators.groupBy { operator->
                    operator.operator
                }

                val listPayUser = mutableListOf<PaymentMethodsGlobal>()

                listUserGrpuped.forEach { group->
                    group.value.map { operators ->
                        Timber.e("operator $group")
                        Timber.e("operator2 $operators")

                        val listGroupPay = operators.paymentMethods.groupBy {
                            it.name
                        }

                        Timber.e("PAYLOL $listGroupPay ${listGroupPay.keys}")

                        listGroupPay.forEach {pay->
                            listPayUser.add(
                                PaymentMethodsGlobal(
                                    name = if (pay.key == "MC"){"Mastercard"}else{pay.key},
                                    sells = pay.value.fold(
                                        initial = 0f,
                                        operation = {acc, paymentMethodsX ->
                                            acc + paymentMethodsX.sells.toFloat()
                                        }
                                    ).toString(),
                                    base = ((pay.value.fold(
                                        initial = 0f,
                                        operation = {acc, paymentMethodsX ->
                                            acc + paymentMethodsX.sells.toFloat()
                                        }
                                    ) - pay.value.fold(
                                        initial = 0f,
                                        operation = {acc, paymentMethodsX ->
                                            acc + paymentMethodsX.taxes.toFloat()
                                        }
                                    )) - pay.value.fold(
                                        initial = 0f,
                                        operation = {acc, paymentMethodsX ->
                                            acc + paymentMethodsX.tips.toFloat()
                                        }
                                    )).toString(),
                                    impuesto = pay.value.fold(
                                        initial = 0f,
                                        operation = {acc, paymentMethodsX ->
                                            acc + paymentMethodsX.taxes.toFloat()
                                        }
                                    ).toString(),
                                    propina = pay.value.fold(
                                        initial = 0f,
                                        operation = {acc, paymentMethodsX ->
                                            acc + paymentMethodsX.tips.toFloat()
                                        }
                                    ).toString(),
                                    devoluciones = pay.value.fold(
                                        initial = 0f,
                                        operation = {acc, paymentMethodsX ->
                                            acc + paymentMethodsX.refunds.toFloat()
                                        }
                                    ).toString(),
                                    total = pay.value.fold(
                                        initial = 0f,
                                        operation = {acc, paymentMethodsX ->
                                            acc + paymentMethodsX.total.toFloat()
                                        }
                                    ).toString()
                                )
                            )
                        }
                    }
                }

                Timber.e("sllss $listUserGrpuped")
                recyclerUser.adapter?.notifyDataSetChanged()

                it.data.operators.groupBy { operators ->
                    operators.operator
                }.map {operators->
                    val list = mutableListOf<PaymentMethodsGlobal>()
                    list.clear()

                    operators.value.forEach {
                        it.paymentMethods.forEach {pay->
                            list.add(
                                PaymentMethodsGlobal(
                                    name = if (pay.name == "MC"){"Mastercard"}else{pay.name},
                                    sells = String.format("%.2f", pay.sells),
                                    base = String.format("%.2f", ((pay.sells - pay.taxes) - pay.tips)),
                                    impuesto = String.format("%.2f", pay.taxes),
                                    propina = String.format("%.2f", pay.tips),
                                    devoluciones = String.format("%.2f", pay.refunds),
                                    total = String.format("%.2f", pay.total)
                                ))
                        }

                        Timber.e("LISTLOL $list")

                        listUser.add(
                            UserReportX(
                                name = it.operator,
                                globalReportX = GlobalReportX(
                                    txs = it.global.txs,
                                    sells = it.global.sells,
                                    taxes = it.global.taxes,
                                    tips = it.global.tips,
                                    refunds = it.global.refunds,
                                    total = it.global.total
                                ),
                                payment = list
                            )
                        )
                    }
                }

                it.data.txs.map { txs->
                    Timber.e("${txs.cardNumber}")
                    listFormatted.add(
                        TransactionReportX(
                            id = txs.codOper,
                            amountGan = txs.amount,
                            date = txs.date,
                            paymentMethod = txs.cardType,
                            cardNumber = txs.cardNumber
                        )
                    )
                }
                recycler.adapter?.notifyDataSetChanged()
                binding.globalSize.text = it.data.global.txs.toString()
                binding.globalAmount.text = String.format("$%.2f", it.data.global.sells)
                binding.globalBase.text = String.format("$%.2f", (it.data.global.sells - it.data.global.tips) - it.data.global.taxes)
                binding.globalTotal.text = String.format("$%.2f", it.data.global.total)
                binding.propinas.text = String.format("$%.2f", it.data.global.tips)
                binding.refunds.text = String.format("-$%.2f", it.data.global.refunds)
                binding.tbms.text = String.format("$%.2f", it.data.global.taxes)

                val listGrouped = it.data.paymentMethods.groupBy { payment->
                    payment.name
                }

                val listPay = mutableListOf<PaymentMethodsGlobal>()

                listGrouped.forEach { group->
                    Timber.e("PAYMENT $group")
                    listPay.add(
                        PaymentMethodsGlobal(
                            name = if (group.key == "MC"){"Mastercard"}else{group.key},
                            sells = String.format("%.2f", group.value.fold(
                                initial = 0f,
                                operation = {acc, paymentMethodsX ->
                                    acc + paymentMethodsX.sells.toFloat()
                                }
                            )),
                            base = String.format("%.2f", ((group.value.fold(
                                initial = 0f,
                                operation = {acc, paymentMethodsX ->
                                    acc + paymentMethodsX.sells.toFloat()
                                }
                            ) - group.value.fold(
                                initial = 0f,
                                operation = {acc, paymentMethodsX ->
                                    acc + paymentMethodsX.taxes.toFloat()
                                }
                            )) - group.value.fold(
                                initial = 0f,
                                operation = {acc, paymentMethodsX ->
                                    acc + paymentMethodsX.tips.toFloat()
                                }
                            ))),
                            impuesto = String.format("%.2f", group.value.fold(
                                initial = 0f,
                                operation = {acc, paymentMethodsX ->
                                    acc + paymentMethodsX.taxes.toFloat()
                                }
                            )),
                            propina = String.format("%.2f", group.value.fold(
                                initial = 0f,
                                operation = {acc, paymentMethodsX ->
                                    acc + paymentMethodsX.tips.toFloat()
                                }
                            )),
                            devoluciones = String.format("%.2f", group.value.fold(
                                initial = 0f,
                                operation = {acc, paymentMethodsX ->
                                    acc + paymentMethodsX.refunds.toFloat()
                                }
                            )),
                            total = String.format("%.2f", group.value.fold(
                                initial = 0f,
                                operation = {acc, paymentMethodsX ->
                                    acc + paymentMethodsX.total.toFloat()
                                }
                            ))
                        )
                    )
                }

                if (listUser.isEmpty()){
                    binding.detailsUser.visibility = View.GONE
                }else{
                    binding.detailsUser.visibility = View.VISIBLE
                }

                if (listFormatted.isEmpty()){
                    binding.layoutTxsReport.visibility = View.GONE
                    binding.titleReport.visibility = View.GONE
                    binding.txsTitle.visibility = View.GONE
                }else{
                    binding.layoutTxsReport.visibility = View.VISIBLE
                    binding.titleReport.visibility = View.VISIBLE
                    binding.txsTitle.visibility = View.VISIBLE
                }

                listPaymentMethodsGlobal.addAll(listPay)
                recyclerPaymentMethodsGlobal.adapter?.notifyDataSetChanged()
            }catch (e: Exception){

            }
        }

        viewModel.reportez.observe(viewLifecycleOwner){
            Timber.e("REPORTE Z")
            binding.titleReport.text = getString(R.string.titleReportZ)
            binding.txsTitle.visibility = View.GONE
            binding.layoutTxs.visibility = View.GONE

            binding.globalSize.text = it.data.global.txs.toString()
            binding.globalAmount.text = String.format("$%.2f", it.data.global.sells)
            binding.globalBase.text = String.format("$%.2f", (it.data.global.sells - it.data.global.tips) - it.data.global.taxes)
            binding.globalTotal.text = String.format("$%.2f", it.data.global.total)
            binding.propinas.text = String.format("$%.2f", it.data.global.tips)
            binding.refunds.text = String.format("-$%.2f", it.data.global.refunds)
            binding.tbms.text = String.format("$%.2f", it.data.global.taxes)

            val listUserGrpuped = it.data.operators.groupBy { operator->
                operator.operator
            }

            val listPayUser = mutableListOf<PaymentMethodsGlobal>()

            listUserGrpuped.forEach { group->
                group.value.map { operators ->
                    Timber.e("operator $group")
                    Timber.e("operator2 $operators")

                    val listGroupPay = operators.paymentMethods.groupBy {
                        it.name
                    }

                    listGroupPay.forEach {pay->
                        listPayUser.add(
                            PaymentMethodsGlobal(
                                name = if (pay.key == "MC"){"Mastercard"}else{pay.key},
                                sells = String.format("%.2f", pay.value.fold(
                                    initial = 0f,
                                    operation = {acc, paymentMethodsX ->
                                        acc + paymentMethodsX.sells.toFloat()
                                    }
                                )),
                                base = String.format("%.2f",((pay.value.fold(
                                    initial = 0f,
                                    operation = {acc, paymentMethodsX ->
                                        acc + paymentMethodsX.sells.toFloat()
                                    }
                                ) - pay.value.fold(
                                    initial = 0f,
                                    operation = {acc, paymentMethodsX ->
                                        acc + paymentMethodsX.taxes.toFloat()
                                    }
                                )) - pay.value.fold(
                                    initial = 0f,
                                    operation = {acc, paymentMethodsX ->
                                        acc + paymentMethodsX.tips.toFloat()
                                    }
                                ))),
                                impuesto = String.format("%.2f", pay.value.fold(
                                    initial = 0f,
                                    operation = {acc, paymentMethodsX ->
                                        acc + paymentMethodsX.taxes.toFloat()
                                    }
                                )),
                                propina = String.format("%.2f", pay.value.fold(
                                    initial = 0f,
                                    operation = {acc, paymentMethodsX ->
                                        acc + paymentMethodsX.tips.toFloat()
                                    }
                                )),
                                devoluciones = String.format("%.2f", pay.value.fold(
                                    initial = 0f,
                                    operation = {acc, paymentMethodsX ->
                                        acc + paymentMethodsX.refunds.toFloat()
                                    }
                                )),
                                total = String.format("%.2f",pay.value.fold(
                                    initial = 0f,
                                    operation = {acc, paymentMethodsX ->
                                        acc + paymentMethodsX.total.toFloat()
                                    }
                                ))
                            )
                        )
                    }
                }
            }

            Timber.e("sllss $listUserGrpuped")
            recyclerUser.adapter?.notifyDataSetChanged()

            it.data.operators.groupBy { operators ->
                operators.operator
            }.map {operators->
                val list = mutableListOf<PaymentMethodsGlobal>()
                list.clear()

                operators.value.forEach {
                    it.paymentMethods.forEach {pay->
                        list.add(
                            PaymentMethodsGlobal(
                                name = if (pay.name == "MC"){"Mastercard"}else{pay.name},
                                sells = String.format("%.2f", pay.sells),
                                base = String.format("%.2f", ((pay.sells - pay.taxes) - pay.tips)),
                                impuesto = String.format("%.2f", pay.taxes),
                                propina = String.format("%.2f", pay.tips),
                                devoluciones = String.format("%.2f", pay.refunds),
                                total = String.format("%.2f", pay.total)
                            ))
                    }

                    Timber.e("LISTLOL $list")

                    listUser.add(
                        UserReportX(
                            name = it.operator,
                            globalReportX = GlobalReportX(
                                txs = it.global.txs,
                                sells = it.global.sells,
                                taxes = it.global.taxes,
                                tips = it.global.tips,
                                refunds = it.global.refunds,
                                total = it.global.total
                            ),
                            payment = list
                        )
                    )
                }
            }

            val listGrouped = it.data.paymentMethods.groupBy { payment->
                payment.name
            }

            val listPay = mutableListOf<PaymentMethodsGlobal>()

            listGrouped.forEach { group->
                listPay.add(
                    PaymentMethodsGlobal(
                        name = if (group.key == "MC"){"Mastercard"}else{group.key},
                        sells = String.format("%.2f", group.value.fold(
                            initial = 0f,
                            operation = {acc, paymentMethodsX ->
                                acc + paymentMethodsX.sells.toFloat()
                            }
                        )),
                        base = String.format("%.2f",((group.value.fold(
                            initial = 0f,
                            operation = {acc, paymentMethodsX ->
                                acc + paymentMethodsX.sells.toFloat()
                            }
                        ) - group.value.fold(
                            initial = 0f,
                            operation = {acc, paymentMethodsX ->
                                acc + paymentMethodsX.taxes.toFloat()
                            }
                        )) - group.value.fold(
                            initial = 0f,
                            operation = {acc, paymentMethodsX ->
                                acc + paymentMethodsX.tips.toFloat()
                            }
                        ))),
                        impuesto = String.format("%.2f", group.value.fold(
                            initial = 0f,
                            operation = {acc, paymentMethodsX ->
                                acc + paymentMethodsX.taxes.toFloat()
                            }
                        )),
                        propina = String.format("%.2f", group.value.fold(
                            initial = 0f,
                            operation = {acc, paymentMethodsX ->
                                acc + paymentMethodsX.tips.toFloat()
                            }
                        )),
                        devoluciones = String.format("%.2f", group.value.fold(
                            initial = 0f,
                            operation = {acc, paymentMethodsX ->
                                acc + paymentMethodsX.refunds.toFloat()
                            }
                        )),
                        total = String.format("%.2f",group.value.fold(
                            initial = 0f,
                            operation = {acc, paymentMethodsX ->
                                acc + paymentMethodsX.total.toFloat()
                            }
                        ))
                    )
                )
            }

            binding.svReporteX.fling(0)
            binding.svReporteX.scrollTo(0, 0)

            listPaymentMethodsGlobal.addAll(listPay)
            recyclerPaymentMethodsGlobal.adapter?.notifyDataSetChanged()

        }
    }

    private fun loadListeners() {


        binding.btnEnviarReporteX.setOnClickListener {

            showBottomSheet(ApplicationClass.language.reporteXGenerado,true)

        }


        binding.btnGenerarReporteZ.setOnClickListener {

            //showReportZ()
            showDialogConfirmCorteZ(showButton = {
                Timber.e("SHOWBUTTON")
                binding.btnVolverReporteX.visibility = View.GONE    //TODO CAMBIAR
                binding.btnGenerarReporteZ.visibility = View.GONE
            })

        }

        binding.btnVolverReporteX.setOnClickListener {

            gotoHome()

        }

    }

    private fun gotoHome(){
        val fr = activity?.supportFragmentManager?.beginTransaction()
        val frg = HomeFragment()
        fr?.replace(R.id.vp_home_detail, frg)
        fr?.addToBackStack(null)?.commit()
    }

    private fun showBottomSheet(mensaje:String,showCorreo:Boolean) {

        val dialog = context?.let { BottomSheetDialog(it) }

        val view = layoutInflater.inflate(R.layout.bottom_sheet_reporte_x, null)
        val btnclose=view.findViewById<Button>(R.id.btn_close_dg_corte_x)
        val rbSendEmail=view.findViewById<RadioButton>(R.id.rb_send_email_report_x)
        val tvSendEmail=view.findViewById<TextView>(R.id.tv_send_email_report_x)
        var checked = false

        rbSendEmail.setOnClickListener {
            checked = !checked
            rbSendEmail.isChecked = checked
        }

        btnclose.text=getString(R.string.aceptar)
        if (showCorreo)
        {
            rbSendEmail.visibility=View.VISIBLE
            tvSendEmail.visibility=View.VISIBLE
            tvSendEmail.text = UserRepo.getUser().email ?: ""
            btnclose.text=getString(R.string.finalizar)
        }
        else
        {
            rbSendEmail.visibility=View.GONE
            tvSendEmail.visibility=View.GONE
            btnclose.text=getString(R.string.aceptar)
        }

        view.tv_mensaje_dialog.text=mensaje

        btnclose.setOnClickListener{
            //todo enviar reporte x
            getReporteXEvent(rbSendEmail) {
                dialog?.hide()
            }
            dialog?.hide()
        }

        dialog?.setCancelable(true)

        dialog?.setContentView(view)

        dialog?.show()

    }

    private fun getReporteXEvent(rbSendEmail: RadioButton, success:() -> Unit){
        lifecycleScope.launch {
            viewModel.getReporteX(sendEmail = rbSendEmail.isChecked,onSuccess = {
                showSuccesReportX(getString(R.string.reporte_x_generado),false)
                Timber.e("susccer reportx")
                success()
            }){
                /*showWarningDialog(it, onFailure = {
                    getReporteXEvent(
                        rbSendEmail = rbSendEmail,
                        success = {
                            success()
                        }
                    )
                })*/
            }
        }
    }

    private fun showSuccesReportX(mensaje:String,showCorreo:Boolean) {

        val dialog = context?.let { BottomSheetDialog(it) }

        val view = layoutInflater.inflate(R.layout.bottom_sheet_reporte_x, null)
        val btnclose=view.findViewById<Button>(R.id.btn_close_dg_corte_x)
        val rbSendEmail=view.findViewById<RadioButton>(R.id.rb_send_email_report_x)
        val tvSendEmail=view.findViewById<TextView>(R.id.tv_send_email_report_x)

        btnclose.text = ApplicationClass.language.finalizar

        if (showCorreo)
        {
            rbSendEmail.visibility=View.VISIBLE
            tvSendEmail.visibility=View.VISIBLE
            btnclose.text=getString(R.string.finalizar)
        }
        else
        {
            rbSendEmail.visibility=View.GONE
            tvSendEmail.visibility=View.GONE
            btnclose.text=getString(R.string.aceptar)
        }

        view.tv_mensaje_dialog.text=mensaje

        btnclose.setOnClickListener{

            dialog?.hide()

        }

        dialog?.setCancelable(true)

        dialog?.setContentView(view)

        dialog?.show()

    }


    private fun showDialogConfirmCorteZ(showButton: ()-> Unit)
    {
        val dialog = context?.let { BottomSheetDialog(it) }

        val view = layoutInflater.inflate(R.layout.bottom_sheet_alert_z, null)

        val btnClose = view.findViewById<Button>(R.id.btn_volver)
        val btnAceptar=view.findViewById<Button>(R.id.btn_si_aceptar)
        val rdCortez = view.findViewById<RadioButton>(R.id.radioButton)
        val emailText = view.findViewById<TextView>(R.id.emailReportz)
        var checked = false
        val title = view.findViewById<TextView>(R.id.tv_mensaje_dialog)
        val description = view.findViewById<TextView>(R.id.textView16)

        title.text = ApplicationClass.language.seguroRealizarCorteZ
        rdCortez.text = ApplicationClass.language.deseaEnviarCorreoCorte
        btnAceptar.text = ApplicationClass.language.siAceptar
        btnClose.text = ApplicationClass.language.volver

        rdCortez.setOnClickListener {
            checked = !checked
            rdCortez.isChecked = checked
        }

        emailText.text = merchantFind?.email ?: ""

        btnAceptar.setOnClickListener{
            //todo generar reporte z
            lifecycleScope.launch {
                getReportez(rdCortez){
                    dialog?.hide()
                    showButton()
                }
            }

        }

        btnClose.setOnClickListener{

            dialog?.hide()

        }

        dialog?.setCancelable(true)

        dialog?.setContentView(view)

        dialog?.show()
    }

    private suspend fun getReportez(rdCortez: RadioButton, success: () -> Unit) {
        viewModel.getReportZ(
            sendEmail = rdCortez.isChecked,
            onSuccess = {
                showBottomSheet(ApplicationClass.language.reporteZGenerado,false)
                //showReportZ()
                success()
            },
            onFailure = {
                /*showWarningDialog(it){
                    lifecycleScope.launch {
                        getReportez(
                            rdCortez = rdCortez,
                            success = {
                                success()
                            }
                        )
                    }
                }*/
            }
        )
    }

}
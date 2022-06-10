package com.paguelofacil.posfacil.ui.view.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.paguelofacil.posfacil.ApplicationClass
import com.paguelofacil.posfacil.R
import com.paguelofacil.posfacil.model.ReportXResponse
import com.paguelofacil.posfacil.model.UserReportX

class ReportXUserAdapter(
    listUserReporX: MutableList<UserReportX>
): RecyclerView.Adapter<ReportXUserAdapter.ReportXUserVH>() {

    private val listUser = listUserReporX/*
    private val listPayment = listPaymentUser as MutableList<PaymentMethodsGlobal>
    private val listDataUser = listData*/

    class ReportXUserVH(view: View): RecyclerView.ViewHolder(view){
        val nameUser: TextView = view.findViewById(R.id.nameUser)
        val sells: TextView = view.findViewById(R.id.selssuser)
        val base: TextView = view.findViewById(R.id.baseUser)
        val impuesto: TextView = view.findViewById(R.id.impuestoUser)
        val propinaPay: TextView = view.findViewById(R.id.propinasUser)
        val rembolsoPay: TextView = view.findViewById(R.id.refundsUser)
        val totalGlobal: TextView = view.findViewById(R.id.totalUser)
        val rv: RecyclerView = view.findViewById(R.id.rv_listPaymentMethodsUser)

        val sell: TextView = view.findViewById(R.id.sellUser)
        val basePay: TextView = view.findViewById(R.id.baseUserTitle)
        val itmbs: TextView = view.findViewById(R.id.itbmsUser)
        val propina: TextView = view.findViewById(R.id.propinaUser)
        val devoluciones: TextView = view.findViewById(R.id.devolucionUser)
        val total: TextView = view.findViewById(R.id.totalUserTitle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportXUserVH {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.report_by_user, parent, false)
        return ReportXUserVH(view)
    }

    override fun onBindViewHolder(holder: ReportXUserVH, position: Int) {
        with(holder) {
            nameUser.text = listUser[position].name.toString()
            sells.text = String.format("$%.2f", listUser[position].globalReportX.sells)
            base.text = String.format("$%.2f", (listUser[position].globalReportX.sells - listUser[position].globalReportX.taxes) - listUser[position].globalReportX.tips)
            impuesto.text = String.format("$%.2f", listUser[position].globalReportX.taxes)
            propinaPay.text = String.format("$%.2f", listUser[position].globalReportX.tips)
            rembolsoPay.text = String.format("-$%.2f", listUser[position].globalReportX.refunds)
            totalGlobal.text = String.format("$%.2f", listUser[position].globalReportX.total)

            sell.text = ApplicationClass.language.ventas
            basePay.text = ApplicationClass.language.importeBase
            itmbs.text = ApplicationClass.language.impuestos
            propina.text = ApplicationClass.language.propina
            devoluciones.text = ApplicationClass.language.reembolso
            total.text = ApplicationClass.language.total

            rv.hasFixedSize()

            /*val listPay = mutableListOf<PaymentMethodsGlobal>()
            val listGrouped = listDataUser.data.paymentMethods.groupBy { payment->
                payment.name
            }

            listGrouped.forEach { group->
                listPay.add(
                    PaymentMethodsGlobal(
                        name = group.key,
                        sells = group.value.fold(
                            initial = 0f,
                            operation = {acc, paymentMethodsX ->
                                acc + paymentMethodsX.sells.toFloat()
                            }
                        ).toString(),
                        base = "0000",
                        impuesto = group.value.fold(
                            initial = 0f,
                            operation = {acc, paymentMethodsX ->
                                acc + paymentMethodsX.taxes.toFloat()
                            }
                        ).toString(),
                        propina = group.value.fold(
                            initial = 0f,
                            operation = {acc, paymentMethodsX ->
                                acc + paymentMethodsX.tips.toFloat()
                            }
                        ).toString(),
                        devoluciones = group.value.fold(
                            initial = 0f,
                            operation = {acc, paymentMethodsX ->
                                acc + paymentMethodsX.refunds.toFloat()
                            }
                        ).toString(),
                        total = group.value.fold(
                            initial = 0f,
                            operation = {acc, paymentMethodsX ->
                                acc + paymentMethodsX.total.toFloat()
                            }
                        ).toString()
                    )
                )
            }

            listPayment.addAll(listPay)*/

            rv.adapter = ListPaymentMethodsGlobalAdapter(listUser[position].payment)
        }
    }

    override fun getItemCount(): Int = listUser.size
}
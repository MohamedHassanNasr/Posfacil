package com.paguelofacil.posfacil.ui.view.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.paguelofacil.posfacil.ApplicationClass
import com.paguelofacil.posfacil.R
import com.paguelofacil.posfacil.ui.view.adapters.ListPaymentMethodsGlobalAdapter.ViewHolderList

class ListPaymentMethodsGlobalAdapter(
    listPayment: List<PaymentMethodsGlobal>
): RecyclerView.Adapter<ViewHolderList>() {

    private val list = listPayment

    class ViewHolderList(view: View): RecyclerView.ViewHolder(view) {
        val namePay: TextView = view.findViewById(R.id.namePay)
        val sells: TextView = view.findViewById(R.id.sells)
        val base: TextView = view.findViewById(R.id.base)
        val impuesto: TextView = view.findViewById(R.id.impuesto)
        val propinaPay: TextView = view.findViewById(R.id.propinaPay)
        val rembolsoPay: TextView = view.findViewById(R.id.rembolsoPay)
        val totalGlobal: TextView = view.findViewById(R.id.total)

        val sell: TextView = view.findViewById(R.id.sellsPay)
        val basePay: TextView = view.findViewById(R.id.basePay)
        val itmbs: TextView = view.findViewById(R.id.itbmsPay)
        val propina: TextView = view.findViewById(R.id.propinaTitle)
        val devoluciones: TextView = view.findViewById(R.id.refundPay)
        val total: TextView = view.findViewById(R.id.totalPay)
        val payment: TextView = view.findViewById(R.id.paymentTitle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderList {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.payment_global_list, parent, false)
        return ViewHolderList(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolderList, position: Int) {
        with(holder) {
            namePay.text = if (list[position].name == "MC"){"Mastercard"}else{list[position].name}
            sells.text = "$${list[position].sells}"
            base.text = "$${list[position].base}"
            impuesto.text = "$${list[position].impuesto}"
            propinaPay.text = "$${list[position].propina}"
            rembolsoPay.text = "-$${list[position].devoluciones}"
            totalGlobal.text = "$${list[position].total}"

            sell.text = ApplicationClass.language.ventas
            basePay.text = ApplicationClass.language.importeBase
            itmbs.text = ApplicationClass.language.impuestos
            propina.text = ApplicationClass.language.propina
            devoluciones.text = ApplicationClass.language.reembolso
            total.text = ApplicationClass.language.total
            payment.text = ApplicationClass.language.metodoPago
        }
    }

    override fun getItemCount(): Int  = list.size
}
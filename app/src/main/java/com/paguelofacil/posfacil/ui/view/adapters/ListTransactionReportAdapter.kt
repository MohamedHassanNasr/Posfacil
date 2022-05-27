package com.paguelofacil.posfacil.ui.view.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.paguelofacil.posfacil.ApplicationClass
import com.paguelofacil.posfacil.R
import com.paguelofacil.posfacil.model.TransactionReportX
import com.paguelofacil.posfacil.ui.view.adapters.ListTransactionReportAdapter.*
import com.paguelofacil.posfacil.util.dateFormattedByDate
import com.paguelofacil.posfacil.util.dateFormattedByHour

class ListTransactionReportAdapter(
    listTransaction: List<TransactionReportX>
): RecyclerView.Adapter<TransactionReportXVH>() {

    private val transactionList: List<TransactionReportX> = listTransaction

    class TransactionReportXVH(view: View) : RecyclerView.ViewHolder(view) {
        val idTransaction: TextView = view.findViewById(R.id.idTransaction)
        val amountTransaction: TextView = view.findViewById(R.id.gan)
        val dateTransaction: TextView = view.findViewById(R.id.dateTransaction)
        val paymentMethod: TextView = view.findViewById(R.id.typeCard)
        val logoCard: TextView = view.findViewById(R.id.logoText)
        val codOperTitle: TextView = view.findViewById(R.id.codOperTitle)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionReportXVH {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.list_transactions_report_x_card, parent, false)
        return TransactionReportXVH(view)
    }

    override fun onBindViewHolder(holder: TransactionReportXVH, position: Int) {
        with(holder) {
            idTransaction.text = transactionList[position].id.toString()
            codOperTitle.text = "${ApplicationClass.language.codeOperation}: "
            amountTransaction.text = String.format("${if (transactionList[position].id.contains("POS")){"+"}else{"-"}}$%.2f", transactionList[position].amountGan)
            dateTransaction.text = "${dateFormattedByDate(transactionList[position].date)} ${dateFormattedByHour(transactionList[position].date, getHourCode = true)}"
            paymentMethod.text = transactionList[position].cardNumber.toString()
            logoCard.visibility = View.VISIBLE
            logoCard.text = (
                when(transactionList[position].paymentMethod){
                    "VISA"->{
                        "Visa ****"
                    }
                    "WALLET"->{
                        "Paguelo Facil ****"
                    }
                    "MC"->{
                        "Mastercard ****"
                    }
                    else->{
                        "Paguelo Facil ****"
                    }
                }
            )
        }
    }

    override fun getItemCount(): Int = transactionList.size

}
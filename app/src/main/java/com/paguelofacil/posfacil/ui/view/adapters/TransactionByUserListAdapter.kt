package com.paguelofacil.posfacil.ui.view.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView
import com.paguelofacil.posfacil.ApplicationClass
import com.paguelofacil.posfacil.R
import com.paguelofacil.posfacil.model.TransactionByUser
import com.paguelofacil.posfacil.ui.view.adapters.TransactionByUserListAdapter.TransactionByUserVH

class TransactionByUserListAdapter(
    listTransactionByUser: List<TransactionByUser>
): RecyclerView.Adapter<TransactionByUserVH>() {

    private val transactionList: List<TransactionByUser> = listTransactionByUser

    class TransactionByUserVH(view: View) : RecyclerView.ViewHolder(view) {
        val nameUser: TextView = view.findViewById(R.id.userNameTransaction)
        val amountTransaction: TextView = view.findViewById(R.id.transactionAmount)
        val transactionSize: TextView = view.findViewById(R.id.transactionQuantity)
        val colorIcon: ShapeableImageView = view.findViewById(R.id.colorIcon)
        val titleTxs: TextView = view.findViewById(R.id.txsField)
        val titleAmount: TextView = view.findViewById(R.id.amountField)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionByUserVH {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.user_transaction_card, parent, false)
        return TransactionByUserVH(view)
    }

    override fun onBindViewHolder(holder: TransactionByUserVH, position: Int) {
        with(holder) {
            nameUser.text = transactionList[position].name
            amountTransaction.text = String.format("$%.2f", transactionList[position].amount)
            transactionSize.text = transactionList[position].transactionSize.toString()
            colorIcon.setBackgroundColor(transactionList[position].color!!)
            titleTxs.text = ApplicationClass.language.userTransaccione
            titleAmount.text = ApplicationClass.language.monto
        }
    }

    override fun getItemCount(): Int = transactionList.size

}
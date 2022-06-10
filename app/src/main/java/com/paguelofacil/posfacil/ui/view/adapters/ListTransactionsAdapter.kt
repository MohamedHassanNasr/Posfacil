package com.paguelofacil.posfacil.ui.view.adapters

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.paguelofacil.posfacil.ApplicationClass
import com.paguelofacil.posfacil.R
import com.paguelofacil.posfacil.databinding.RowTransactionBinding
import com.paguelofacil.posfacil.model.Transaction
import com.paguelofacil.posfacil.ui.view.transactions.TransactionsActivity
import com.paguelofacil.posfacil.util.Constantes.ConstantesView


class ListTransactionsAdapter(val layout:Int) : RecyclerView.Adapter<ListTransactionsAdapter.ListTransactionsHolder>() {
    private var listTransactions: List<Transaction> = emptyList()
    var listTransactionsTmp: List<Transaction> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListTransactionsHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ListTransactionsHolder(layoutInflater.inflate(layout, parent, false))
    }

    override fun onBindViewHolder(holder: ListTransactionsHolder, position: Int) {

        val transaction:Transaction=listTransactions[position]
        holder.asignarDatos(transaction)
    }

    override fun getItemCount(): Int= listTransactions.size

    fun setTransactions(transactions: List<Transaction>) {
        this.listTransactions = transactions
        notifyDataSetChanged()
    }

    fun getTransactions(): List<Transaction> {
        return listTransactions
    }

    class ListTransactionsHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        val binding = RowTransactionBinding.bind(itemView)

        fun asignarDatos(transaction: Transaction){

            var drawable = R.drawable.ic_cobro_white
            val context: Context = binding.tvIdTransaction.context

            binding.tvId.text = ApplicationClass.language.codeOperation

            binding.tvIdTransaction.text = transaction.opCode.toString()
            binding.tvDateTransaction.text = transaction.formattedDateTime
            binding.tvCardTransaction.text = transaction.cardNumber
            binding.logo.setImageResource(
                if(transaction.cardType.equals("Visa", true)){
                    R.drawable.visa
                }else if(transaction.cardType.equals("MC", true)){
                    R.drawable.ic_mastercard_logo
                }else{
                    R.drawable.ic_app_icon
                }
            )
            binding.tvCardTransaction.text = transaction.cardNumber

            binding.clDetailTransation.setOnClickListener {
                if (transaction.typeStr == "Cobro") {
                    val intent= Intent(context, TransactionsActivity::class.java)
                    intent.putExtra(ConstantesView.PARAM_TIPO_TRANSACTION, ConstantesView.PARAM_TRANSACTION_COBRO)
                    intent.putExtra("detail", transaction)
                    binding.tvCardTransaction.context.startActivity(intent)
                }
                else {
                    val intent= Intent(context, TransactionsActivity::class.java)
                    intent.putExtra(ConstantesView.PARAM_TIPO_TRANSACTION, ConstantesView.PARAM_TRANSACTION_REEMBOLSO)
                    intent.putExtra("detail", transaction)
                    binding.tvCardTransaction.context.startActivity(intent)
                }
            }

            if (transaction.opCode.contains("POS")) {
                binding.tvAmountTransaction.text = transaction.possitiveCurrencyAmountStr
                binding.tvAmountTransaction.setTextColor(ContextCompat.getColor(context,R.color.color_4CA80B));
                drawable=R.drawable.ic_cobro_white
            }
            else {
                binding.tvAmountTransaction.text = transaction.negativeCurrencyAmountStr
                binding.tvAmountTransaction.setTextColor(ContextCompat.getColor(context,R.color.color_EDB600));
                drawable=R.drawable.ic_send_white
            }

            Glide.with(binding.ivIconLogo.context)
                .load(drawable).centerInside()
                .into(binding.ivIconLogo)
        }

    }

}
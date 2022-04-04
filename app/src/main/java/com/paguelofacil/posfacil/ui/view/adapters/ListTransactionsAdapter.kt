package com.paguelofacil.posfacil.ui.view.adapters

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.paguelofacil.posfacil.R
import com.paguelofacil.posfacil.databinding.RowTransactionBinding
import com.paguelofacil.posfacil.model.Transaction
import com.paguelofacil.posfacil.ui.view.transactions.TransactionsActivity
import com.paguelofacil.posfacil.util.Constantes.ConstantesView


class ListTransactionsAdapter(val listTransactions:ArrayList<Transaction>,val layout:Int) : RecyclerView.Adapter<ListTransactionsAdapter.ListTransactionsHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListTransactionsHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ListTransactionsHolder(layoutInflater.inflate(layout, parent, false))
    }

    override fun onBindViewHolder(holder: ListTransactionsHolder, position: Int) {

        val transaction:Transaction=listTransactions[position]
        holder.asignarDatos(transaction)


    }

    override fun getItemCount(): Int= listTransactions.size

    class ListTransactionsHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        val binding=RowTransactionBinding.bind(itemView)

        fun asignarDatos(transaction: Transaction){


            var drawable=R.drawable.ic_cobro_white
            val context: Context =binding.tvIdTransaction.context

            binding.tvIdTransaction.text=transaction.id.toString()
            binding.tvDateTransaction.text=transaction.fechaHora
            binding.tvAmountTransaction.text=transaction.mount
            binding.tvCardTransaction.text=transaction.detailNameCard

            binding.clDetailTransation.setOnClickListener {

                if (transaction.tipo==1)
                {
                    val intent= Intent(context, TransactionsActivity::class.java)
                    intent.putExtra(ConstantesView.PARAM_TIPO_TRANSACTION,ConstantesView.PARAM_TRANSACTION_COBRO)
                    binding.tvCardTransaction.context.startActivity(intent)

                }
                else
                {
                    val intent= Intent(context, TransactionsActivity::class.java)
                    intent.putExtra(ConstantesView.PARAM_TIPO_TRANSACTION,ConstantesView.PARAM_TRANSACTION_REEMBOLSO)
                    binding.tvCardTransaction.context.startActivity(intent)
                }


            }

            if (transaction.tipo==1)
            {
                binding.tvAmountTransaction.setTextColor(ContextCompat.getColor(context,R.color.color_4CA80B));
                drawable=R.drawable.ic_cobro_white
            }
            else
            {
                binding.tvAmountTransaction.setTextColor(ContextCompat.getColor(context,R.color.color_EDB600));
                drawable=R.drawable.ic_send_white
            }

            Glide.with(binding.ivIconLogo.context)
                .load(drawable).centerInside()
                .into(binding.ivIconLogo)


        }

    }

}
package com.paguelofacil.posfacil.ui.view.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.paguelofacil.posfacil.R
import com.paguelofacil.posfacil.ui.view.adapters.ListItemSolucionAdapter.SolucionVh

class ListItemSolucionAdapter(listSoluciones: List<String>): RecyclerView.Adapter<SolucionVh>() {

    private val list = listSoluciones

    class SolucionVh(view: View): RecyclerView.ViewHolder(view){
        val idTransaction: TextView = view.findViewById(R.id.itemText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SolucionVh {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_list_warning, parent, false)
        return SolucionVh(view)
    }

    override fun onBindViewHolder(holder: SolucionVh, position: Int) {
        with(holder){
            idTransaction.text = list[position]
        }
    }

    override fun getItemCount(): Int = list.size


}
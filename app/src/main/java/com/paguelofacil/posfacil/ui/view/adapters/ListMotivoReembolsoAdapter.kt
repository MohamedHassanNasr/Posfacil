package com.paguelofacil.posfacil.ui.view.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.paguelofacil.posfacil.databinding.RowMotivosReembolsoBinding
import com.paguelofacil.posfacil.model.ParamReembolsoPropina

class ListMotivoReembolsoAdapter(val listMotivos:ArrayList<ParamReembolsoPropina>, val layout:Int, val bottonSheet: BottomSheetDialog?):
    RecyclerView.Adapter<ListMotivoReembolsoAdapter.ListMotivosHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListMotivosHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ListMotivoReembolsoAdapter.ListMotivosHolder(
            layoutInflater.inflate(
                layout,
                parent,
                false
            )
        )


    }

    override fun onBindViewHolder(holder: ListMotivosHolder, position: Int) {

        val motivo:ParamReembolsoPropina=listMotivos[position]
        holder.asignarDatos(motivo,bottonSheet)

    }

    override fun getItemCount(): Int=listMotivos.size

    class ListMotivosHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        val binding= RowMotivosReembolsoBinding.bind(itemView)

        fun asignarDatos(motivo:ParamReembolsoPropina, bottonSheet: BottomSheetDialog?)
        {
            binding.tvNameMotivo.text=motivo.valueToShow

            binding.rbMotivoReembolso.setOnClickListener{

                bottonSheet?.dismiss()

            }


        }

    }

}
package com.paguelofacil.posfacil.ui.view.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.paguelofacil.posfacil.databinding.RowMotivosReembolsoBinding
import com.paguelofacil.posfacil.model.ParamReembolsoPropina
import com.paguelofacil.posfacil.ui.interfaces.MotivosEvent
import timber.log.Timber

class ListMotivoReembolsoAdapter(
    val listMotivos: ArrayList<ParamReembolsoPropina>,
    val layout: Int,
    val bottonSheet: BottomSheetDialog?,
    val motivosEvent: MotivosEvent
):
    RecyclerView.Adapter<ListMotivoReembolsoAdapter.ListMotivosHolder>() {

    private fun getMotivo(motivo: ParamReembolsoPropina){
        motivosEvent.getMotivos(motivo.valueToShow)
    }

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

        with(holder){
            rb.isChecked = listMotivos[position].isChecked
            rb.setOnClickListener {
                //listMotivos[position].isChecked = !listMotivos[position].isChecked
                getMotivo(listMotivos[position])
                /*listMotivos.forEachIndexed {index, item->
                    if (index == position){
                        Timber.e("YES")
                        listMotivos[position].isChecked = !listMotivos[position].isChecked
                    }else{
                        Timber.e("YES")
                        listMotivos[index].isChecked = false
                    }
                }*/

                rb.isChecked = listMotivos[position].isChecked
                bottonSheet?.dismiss()
            }
        }

    }

    override fun getItemCount(): Int=listMotivos.size

    class ListMotivosHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        val binding= RowMotivosReembolsoBinding.bind(itemView)
        val rb = binding.rbMotivoReembolso

        fun asignarDatos(motivo:ParamReembolsoPropina, bottonSheet: BottomSheetDialog?)
        {
            binding.tvNameMotivo.text=motivo.valueToShow

            binding.rbMotivoReembolso.setOnClickListener{
                motivo.isChecked = !motivo.isChecked
                bottonSheet?.dismiss()

            }

        }

    }

}
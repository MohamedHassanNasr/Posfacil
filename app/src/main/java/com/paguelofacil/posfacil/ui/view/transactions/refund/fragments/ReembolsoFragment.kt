package com.paguelofacil.posfacil.ui.view.transactions.refund.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.paguelofacil.posfacil.R
import com.paguelofacil.posfacil.databinding.FragmentReembolsoBinding
import com.paguelofacil.posfacil.model.ParamReembolsoPropina
import com.paguelofacil.posfacil.repository.ConfigurationsRepo
import com.paguelofacil.posfacil.ui.view.adapters.ListMotivoReembolsoAdapter
import org.json.JSONArray


class ReembolsoFragment : Fragment(){

    lateinit var binding: FragmentReembolsoBinding
    private var listMotivos= arrayListOf<ParamReembolsoPropina>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        binding=FragmentReembolsoBinding.inflate(inflater,container,false)

        getMotivos()

        loadListeners()

        return binding.root

    }

    /**
     *
     * Obtener los motivos de reembolso
     * Mostrar el valor segun el diccionario de datos(api de traducciones trans#)
     */

    private fun getMotivos() {

        val systemsParam= ConfigurationsRepo.getSystemParamsLocal()
        val dataRefund=systemsParam._default_values_refund

        val json = JSONArray(dataRefund)

        val type = object : TypeToken<ParamReembolsoPropina>() {}.type


        for (i in 0 until json.length()) {

            val item = json.getString(i)
            val motivoJson = Gson().fromJson<ParamReembolsoPropina>(item, type)

            var motivo:ParamReembolsoPropina= ParamReembolsoPropina(motivoJson.key,
                motivoJson.valueToShow.replace("trans#", ""),
                motivoJson.value)

            listMotivos.add(motivo)

        }

    }

    private fun loadListeners() {


        binding.btnConfirmReembolso.setOnClickListener{

            var fr = activity?.supportFragmentManager?.beginTransaction()
            fr?.replace(R.id.container_frag_transactions, ComprobanteReembolsoFragment())
            fr?.commit()

        }

        binding.tvSelectMotivos.setOnClickListener {

            showBottomSheet()


        }

        binding.ivBack.setOnClickListener{

            activity?.supportFragmentManager?.popBackStack();

        }


    }

    private fun showBottomSheet() {

        val dialog = context?.let { BottomSheetDialog(it) }

        val view = layoutInflater.inflate(R.layout.bottom_sheet_dg_motivos, null)
        val recyclerView=view.findViewById<RecyclerView>(R.id.rv_motivos_reembolso)


        recyclerView.layoutManager= LinearLayoutManager(context)
        recyclerView.adapter=ListMotivoReembolsoAdapter(listMotivos,R.layout.row_motivos_reembolso,dialog)



        dialog?.setCancelable(true)

        dialog?.setContentView(view)

        dialog?.show()

    }



}
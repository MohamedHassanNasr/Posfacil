package com.paguelofacil.posfacil.ui.view.settings.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.paguelofacil.posfacil.databinding.FragmentSupportBinding
import com.paguelofacil.posfacil.repository.ConfigurationsRepo
import com.paguelofacil.posfacil.ui.view.settings.activities.AboutActivity
import org.json.JSONObject


class SupportFragment : Fragment() {


    lateinit var binding:FragmentSupportBinding
    private var urlTerminos:String=""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        binding=FragmentSupportBinding.inflate(inflater,container,false)


        getDataLegal()

        loadListeners()

        return binding.root

    }

    /**
     *
     * Cargar terminos legales segun idioma del usuario
     * "es" -> español , "en" -> ingles
     *
     */

    private fun getDataLegal() {

            val systemsParam= ConfigurationsRepo.getSystemParamsLocal()
            val dataScreen=systemsParam._url_terms

            val json = JSONObject(dataScreen)

            urlTerminos = json.getString("es")


    }



    private fun loadListeners() {

        binding.llAboutApp.setOnClickListener {

            val intent= Intent(context, AboutActivity::class.java)
            startActivityForResult(intent,100)


        }

        binding.llLegalApp.setOnClickListener{

            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(urlTerminos)
            startActivity(i)

        }



    }


}
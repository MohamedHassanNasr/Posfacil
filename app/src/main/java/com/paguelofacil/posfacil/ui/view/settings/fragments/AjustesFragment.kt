package com.paguelofacil.posfacil.ui.view.settings.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.paguelofacil.posfacil.databinding.FragmentAjustesBinding
import com.paguelofacil.posfacil.ui.view.settings.activities.AjustesActivity
import com.paguelofacil.posfacil.util.Constantes


class AjustesFragment : Fragment() {


    lateinit var binding:FragmentAjustesBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding= FragmentAjustesBinding.inflate(inflater,container,false)

        loadListeners()

        return binding.root


    }

    private fun loadListeners() {

        binding.llChangePass.setOnClickListener{

            goViewAjustes(Constantes.PARAM_PASSWORD)
        }

        binding.llMyInfo.setOnClickListener{


            goViewAjustes(Constantes.PARAM_PROFILE)

        }

    }

    private fun goViewAjustes(value:String)
    {
        val params = Bundle()

        params.putString(Constantes.PARAM_FRAGMENT, value)

        val intent= Intent(context, AjustesActivity::class.java)

        intent.putExtras(params)

        startActivityForResult(intent,200)
    }

}
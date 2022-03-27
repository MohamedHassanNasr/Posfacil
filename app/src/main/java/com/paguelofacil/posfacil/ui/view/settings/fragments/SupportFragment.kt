package com.paguelofacil.posfacil.ui.view.settings.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.paguelofacil.posfacil.databinding.FragmentSupportBinding
import com.paguelofacil.posfacil.ui.view.settings.activities.AboutActivity


class SupportFragment : Fragment() {


    lateinit var binding:FragmentSupportBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        binding=FragmentSupportBinding.inflate(inflater,container,false)

        loadListeners()

        return binding.root

    }

    private fun loadListeners() {

        binding.llAboutApp.setOnClickListener {

            val intent= Intent(context, AboutActivity::class.java)
            startActivityForResult(intent,100)


        }



    }


}
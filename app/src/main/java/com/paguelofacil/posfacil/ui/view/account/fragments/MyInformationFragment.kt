package com.paguelofacil.posfacil.ui.view.account.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.paguelofacil.posfacil.R
import com.paguelofacil.posfacil.databinding.FragmentMyInformationBinding


class MyInformationFragment : Fragment() {


    lateinit var binding: FragmentMyInformationBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding=FragmentMyInformationBinding.inflate(inflater,container,false)

        loadListeners()

        return binding.root

    }

    private fun loadListeners() {

        binding.ivBack.setOnClickListener {

            activity?.finish()
        }

    }


}
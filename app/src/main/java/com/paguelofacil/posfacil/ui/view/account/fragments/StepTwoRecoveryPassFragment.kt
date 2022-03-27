package com.paguelofacil.posfacil.ui.view.account.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.paguelofacil.posfacil.R
import com.paguelofacil.posfacil.databinding.FragmentStepTwoRecoveryPassBinding


class StepTwoRecoveryPassFragment : Fragment() {


    lateinit var binding: FragmentStepTwoRecoveryPassBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        binding= FragmentStepTwoRecoveryPassBinding.inflate(inflater,container,false)


        loadListeners()

        return binding.root


    }

    private fun loadListeners() {

        binding.btnValidateCode.setOnClickListener{

            var fr = activity?.supportFragmentManager?.beginTransaction()
            fr?.replace(R.id.container_login_fragment, StepThreeRecoveryPassFragment())
            fr?.commit()

        }

        binding.lnArrowBack.setOnClickListener{

            var fr = activity?.supportFragmentManager?.beginTransaction()
            fr?.replace(R.id.container_login_fragment, StepOneRecoveryPassFragment())
            fr?.commit()

        }

    }

}
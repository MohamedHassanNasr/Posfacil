package com.paguelofacil.posfacil.ui.view.transactions.payment.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.paguelofacil.posfacil.R
import com.paguelofacil.posfacil.databinding.FragmentDetailCobroBinding
import com.paguelofacil.posfacil.ui.interfaces.IOnBackPressed
import com.paguelofacil.posfacil.ui.view.transactions.refund.fragments.ReembolsoFragment


class DetailCobroFragment : Fragment() {


    lateinit var binding:FragmentDetailCobroBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding= FragmentDetailCobroBinding.inflate(inflater,container,false)

        loadListeners()

        return binding.root


    }

    private fun loadListeners() {

        binding.btnResendVoucher.setOnClickListener{

            var fr = activity?.supportFragmentManager?.beginTransaction()
            fr?.replace(R.id.container_frag_transactions, EnvioComprobanteFragment())
            fr?.commit()


        }

        binding.btnReembolar.setOnClickListener {

            var fr = activity?.supportFragmentManager?.beginTransaction()
            fr?.replace(R.id.container_frag_transactions, ReembolsoFragment())
            fr?.commit()

        }

        binding.ivBack.setOnClickListener{

            activity?.finish()
        }



    }



}
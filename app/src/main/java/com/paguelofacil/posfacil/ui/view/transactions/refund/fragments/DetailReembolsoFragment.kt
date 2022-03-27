package com.paguelofacil.posfacil.ui.view.transactions.refund.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.paguelofacil.posfacil.R
import com.paguelofacil.posfacil.databinding.FragmentDetailReembolsoBinding
import com.paguelofacil.posfacil.ui.view.transactions.payment.fragments.EnvioComprobanteFragment


class DetailReembolsoFragment : Fragment() {

    lateinit var binding: FragmentDetailReembolsoBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding=FragmentDetailReembolsoBinding.inflate(inflater,container,false)

        loadListeners()

        return binding.root

    }

    private fun loadListeners() {

        binding.btnResendVoucher.setOnClickListener{

            var fr = activity?.supportFragmentManager?.beginTransaction()
            fr?.replace(R.id.container_frag_transactions, EnvioComprobanteFragment())
            fr?.commit()


        }

        binding.ivBack.setOnClickListener {

            activity?.finish()
        }

    }

}
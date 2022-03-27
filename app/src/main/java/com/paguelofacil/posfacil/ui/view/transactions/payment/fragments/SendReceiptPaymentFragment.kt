package com.paguelofacil.posfacil.ui.view.transactions.payment.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.paguelofacil.posfacil.databinding.FragmentSendReceiptPaymentBinding
import com.paguelofacil.posfacil.ui.view.home.activities.HomeActivity

class SendReceiptPaymentFragment : Fragment() {


    lateinit var binding: FragmentSendReceiptPaymentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding=FragmentSendReceiptPaymentBinding.inflate(inflater,container,false)

        loadListeners()

        return binding.root

    }

    private fun loadListeners() {

        binding.ivBack.setOnClickListener{

            goHome()

        }

        binding.btnOtherTransaction.setOnClickListener{

            goHome()

        }

    }

    private fun goHome()
    {
        val intent= Intent(context, HomeActivity::class.java)
        startActivity(intent)
        activity?.finish()
    }

}
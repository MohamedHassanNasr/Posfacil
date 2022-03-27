package com.paguelofacil.posfacil.ui.view.transactions.payment.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.paguelofacil.posfacil.R
import com.paguelofacil.posfacil.databinding.FragmentComprobanteCobroBinding


class ComprobanteCobroFragment : Fragment() {


    lateinit var binding: FragmentComprobanteCobroBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding=FragmentComprobanteCobroBinding.inflate(inflater,container,false)

        loadListeners()

        return binding.root

    }

    private fun loadListeners() {

        binding.cvMetodoCard.setOnClickListener{

            var fr = activity?.supportFragmentManager?.beginTransaction()
            fr?.replace(R.id.container_frag_cobro, DetectCreditCardFragment())
            fr?.commit()


        }

        binding.lnArrowBack.setOnClickListener{

            goBackFragment()


        }

        binding.lnCloseBack.setOnClickListener{

            goBackFragment()

        }

        binding.swInputDestinationReceipt.setOnCheckedChangeListener { compoundButton, b ->

            if (b)
            {
                binding.lnCustomDestination.visibility=View.VISIBLE
            }
            else
            {
                binding.lnCustomDestination.visibility=View.GONE
            }

        }

        binding.cbPhone.setOnCheckedChangeListener { compoundButton, b ->

            if (b)
            {
                binding.etPhone.visibility=View.VISIBLE
            }
            else
            {
                binding.etPhone.visibility=View.GONE
            }

        }

        binding.cbEmail.setOnCheckedChangeListener { compoundButton, b ->

            if (b)
            {
                binding.etEmail.visibility=View.VISIBLE
            }
            else
            {
                binding.etEmail.visibility=View.GONE
            }
        }

        binding.cvPaguelofacil.setOnClickListener {

            var fr = activity?.supportFragmentManager?.beginTransaction()
            fr?.replace(R.id.container_frag_cobro, CobroQrCodeFragment())
            fr?.commit()
        }

    }
    private fun goBackFragment()
    {
        var fr = activity?.supportFragmentManager?.beginTransaction()
        fr?.replace(R.id.container_frag_cobro, VerificarCobroFragment())
        fr?.commit()
    }



}
package com.paguelofacil.posfacil.ui.view.home.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.paguelofacil.posfacil.databinding.FragmentHomeBinding
import com.paguelofacil.posfacil.ui.view.transactions.payment.activities.CobroActivity
import com.paguelofacil.posfacil.util.KeyboardUtil


class HomeFragment : Fragment() {

    lateinit var binding:FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding= FragmentHomeBinding.inflate(inflater,container,false)

        loadListeners()

        return binding.root


    }


    private fun loadListeners() {


        binding.etMontoCobrar.setOnFocusChangeListener { view, b ->

            if (b)
            {
                KeyboardUtil.showKeyboard(activity)
            }

        }


        binding.btnNext.setOnClickListener{


            KeyboardUtil.hideKeyboard(activity)

            val intent= Intent(context, CobroActivity::class.java)

            startActivity(intent)


        }


    }



}
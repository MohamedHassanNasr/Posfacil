package com.paguelofacil.posfacil.ui.view.home.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.paguelofacil.posfacil.databinding.FragmentSliderTwoBinding
import com.paguelofacil.posfacil.ui.view.account.activities.LoginActivity


class SliderTwoFragment : Fragment() {


    lateinit var binding: FragmentSliderTwoBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding=FragmentSliderTwoBinding.inflate(inflater,container,false)

        binding.btnGoLogin.setOnClickListener {

            val intent = Intent(context, LoginActivity::class.java)
            startActivity(intent)


        }


        return binding.root;


    }

}
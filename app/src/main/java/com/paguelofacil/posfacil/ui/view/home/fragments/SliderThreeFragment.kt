package com.paguelofacil.posfacil.ui.view.home.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.paguelofacil.posfacil.databinding.FragmentSliderThreeBinding
import com.paguelofacil.posfacil.ui.view.account.activities.LoginActivity


class SliderThreeFragment : Fragment() {

    lateinit var binding: FragmentSliderThreeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding=FragmentSliderThreeBinding.inflate(inflater,container,false)

        binding.btnGoLogin.setOnClickListener {

            val intent = Intent(context, LoginActivity::class.java)
            startActivity(intent)


        }


        return binding.root;


    }


}
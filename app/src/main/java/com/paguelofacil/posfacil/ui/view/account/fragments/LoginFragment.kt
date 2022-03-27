package com.paguelofacil.posfacil.ui.view.account.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.paguelofacil.posfacil.R
import com.paguelofacil.posfacil.databinding.FragmentLoginBinding
import com.paguelofacil.posfacil.ui.view.home.activities.HomeActivity


class LoginFragment : Fragment() {


    lateinit var binding: FragmentLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        binding=FragmentLoginBinding.inflate(inflater,container,false)

        loadListeners()

        return binding.root



    }

    private fun loadListeners() {

        binding.btnLogin.setOnClickListener{

            val intent= Intent(context, HomeActivity::class.java)
            startActivity(intent)
            activity?.finish()

        }

        binding.tvForgotPassword.setOnClickListener {

            var fr = activity?.supportFragmentManager?.beginTransaction()
            fr?.replace(R.id.container_login_fragment, StepOneRecoveryPassFragment())
            fr?.commit()
        }

    }


}
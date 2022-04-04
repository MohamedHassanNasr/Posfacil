package com.paguelofacil.posfacil.ui.view.account.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.paguelofacil.posfacil.R
import com.paguelofacil.posfacil.base.BaseFragment
import com.paguelofacil.posfacil.data.network.api.ApiRequestCode
import com.paguelofacil.posfacil.databinding.FragmentStepTwoRecoveryPassBinding
import com.paguelofacil.posfacil.repository.UserRepo
import com.paguelofacil.posfacil.ui.view.account.viewmodel.ForgotPasswordViewModel


class StepTwoRecoveryPassFragment : BaseFragment(), View.OnClickListener  {


    lateinit var binding: FragmentStepTwoRecoveryPassBinding

    private lateinit var vm: ForgotPasswordViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vm = ViewModelProvider(requireActivity())[ForgotPasswordViewModel::class.java]

        setBaseViewModel(vm)
        vm.getResponseObserver()
            .observe(this@StepTwoRecoveryPassFragment, this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding= FragmentStepTwoRecoveryPassBinding.inflate(inflater,container,false)


        getInfoUser()

        loadListeners()

        return binding.root


    }

    private fun getInfoUser() {
        UserRepo.getUser().email?.let {
            binding.tvEmailUser.text=it

        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        vm.btnClickEvent.observe(viewLifecycleOwner) {
            if (validated()) {
                vm.verifyOtpStep2(
                    binding.tvOtpPos1.text.toString()
                            + binding.tvOtpPos2.text.toString()
                            + binding.tvOtpPos3.text.toString()
                            + binding.tvOtpPos4.text.toString()
                            + binding.tvOtpPos5.text.toString()
                            + binding.tvOtpPos6.text.toString()
                )
            }
        }


    }

    private fun validated(): Boolean {
        val otp: String = binding.tvOtpPos1.text.toString() +
                binding.tvOtpPos2.text.toString() +
                binding.tvOtpPos3.text.toString() +
                binding.tvOtpPos4.text.toString() +
                binding.tvOtpPos5.text.toString() +
                binding.tvOtpPos6.text.toString()
        if (otp.isEmpty()) {
            showSnack("Por favor ingrese su codigo de autenticacion")
            return false
        }
        if (otp.length < 6) {
            showSnack("El codigo de autenticacion no puede ser menor a 6 digitos")
            return false
        }
        return true
    }

    override fun onStart() {
        super.onStart()
        binding.tvOtpPos1.addTextChangedListener(otp1TW)
        binding.tvOtpPos2.addTextChangedListener(otp2TW)
        binding.tvOtpPos3.addTextChangedListener(otp3TW)
        binding.tvOtpPos4.addTextChangedListener(otp4TW)
        binding.tvOtpPos5.addTextChangedListener(otp5TW)
        binding.tvResendCode.setOnClickListener(this)
    } private val otp1TW: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
        override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
            if (!binding.tvOtpPos1.text.isNullOrEmpty()) {
                binding.tvOtpPos2.requestFocus()
            }
        }

        override fun afterTextChanged(editable: Editable) {}
    }

    private val otp2TW: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
        override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
            if (!binding.tvOtpPos2.text.isNullOrEmpty()) {
                binding.tvOtpPos3.requestFocus()
            }
        }

        override fun afterTextChanged(editable: Editable) {}
    }

    private val otp3TW: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
        override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
            if (!binding.tvOtpPos3.text.isNullOrEmpty()) {
                binding.tvOtpPos4.requestFocus()
            }
        }

        override fun afterTextChanged(editable: Editable) {}
    }

    private val otp4TW: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
        override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
            if (!binding.tvOtpPos4.text.isNullOrEmpty()) {
                binding.tvOtpPos5.requestFocus()
            }
        }

        override fun afterTextChanged(editable: Editable) {}
    }

    private val otp5TW: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
        override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
            if (!binding.tvOtpPos5.text.isNullOrEmpty()) {
                binding.tvOtpPos6.requestFocus()
            }
        }

        override fun afterTextChanged(editable: Editable) {}
    }

    override fun onResponseSuccess(requestCode: Int, responseCode: Int, msg: String?, data: Any?) {
        super.onResponseSuccess(requestCode, responseCode, msg, data)
        when (requestCode) {
            ApiRequestCode.PASSWORD_RECOVERY_STEP2 -> {
                /*NavHostFragment.findNavController(this).navigate(R.id.action_forgotPasswordOtpFragment_to_forgotPasswordConfirmationFragment)*/
                val user = UserRepo.getUser()
                user.tempCodeAuth=binding.tvOtpPos1.text.toString()+ binding.tvOtpPos2.text.toString() + binding.tvOtpPos3.text.toString() + binding.tvOtpPos4.text.toString() + binding.tvOtpPos5.text.toString()  + binding.tvOtpPos6.text.toString()
                UserRepo.setOrUpdateUser(user, true)
                goStep3RecoveryPass()
            }
        }
    }
    private fun goStep3RecoveryPass()
    {
        var fr = activity?.supportFragmentManager?.beginTransaction()
        fr?.replace(R.id.container_login_fragment, StepThreeRecoveryPassFragment())
        fr?.commit()
    }

    override fun onClick(v: View?) {
        when (v) {
            binding.tvResendCode -> {
                UserRepo.getUser().email?.let {
                    vm.sendOtpStep1(it)
                    showSnack("Se reenvio el codigo a $it")
                }

            }
        }
    }

    private fun loadListeners() {


        binding.btnValidateCode.setOnClickListener{

            if (validated()) {
                vm.verifyOtpStep2(binding.tvOtpPos1.text.toString()
                        + binding.tvOtpPos2.text.toString()
                        + binding.tvOtpPos3.text.toString()
                        + binding.tvOtpPos4.text.toString()
                        + binding.tvOtpPos5.text.toString()
                        + binding.tvOtpPos6.text.toString())
            }


        }


        binding.lnArrowBack.setOnClickListener{

            var fr = activity?.supportFragmentManager?.beginTransaction()
            fr?.replace(R.id.container_login_fragment, StepOneRecoveryPassFragment())
            fr?.commit()

        }

    }

}
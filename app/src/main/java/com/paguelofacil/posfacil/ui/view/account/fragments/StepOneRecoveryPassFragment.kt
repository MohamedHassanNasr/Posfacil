package com.paguelofacil.posfacil.ui.view.account.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.paguelofacil.posfacil.R
import com.paguelofacil.posfacil.base.BaseFragment
import com.paguelofacil.posfacil.data.database.entity.ContactSearchEntity
import com.paguelofacil.posfacil.data.network.api.ApiRequestCode
import com.paguelofacil.posfacil.data.network.response.Contact
import com.paguelofacil.posfacil.databinding.FragmentStepOneRecoveryPassBinding
import com.paguelofacil.posfacil.repository.UserRepo
import com.paguelofacil.posfacil.ui.view.account.viewmodel.ForgotPasswordViewModel
import com.paguelofacil.posfacil.ui.view.account.viewmodel.LoginViewModel
import com.paguelofacil.posfacil.util.Constantes.ConstantesView


class StepOneRecoveryPassFragment : BaseFragment(), View.OnFocusChangeListener {

    lateinit var binding:FragmentStepOneRecoveryPassBinding

    private lateinit var vm: ForgotPasswordViewModel

    private var emailTemp:String=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vm = ViewModelProvider(requireActivity())[ForgotPasswordViewModel::class.java]

        setBaseViewModel(vm)

        vm.getResponseObserver()
            .observe(this@StepOneRecoveryPassFragment, this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding=FragmentStepOneRecoveryPassBinding.inflate(inflater,container,false)


        loadListeners()

        captureParams()

        return binding.root


    }


    private fun validated(): Boolean {
        if (binding.etEmail.text.isNullOrBlank()) {
            showSnack(getString(R.string.Please_enter_your_email_or_Alias))
            return false
        }

        return true
    }

    fun isEmailValidated(): Boolean {
        if (binding.etEmail.text.isNullOrBlank()) {
            return false
        }

        return true
    }

    private fun captureParams() {

        val user = UserRepo.getUser()
        emailTemp=user.tempEmailLogin?:""

        binding.etEmail.setText(emailTemp)

    }

    private fun loadListeners() {

        binding.btnSendCode.setOnClickListener{

            if (validated()) {
                vm.getUnsavedUserAccountDetail(binding.etEmail.text.toString())
            }

        }

        binding.lnArrowBack.setOnClickListener{



        }

    }
    override fun onStart() {
        super.onStart()
        binding.etEmail.onFocusChangeListener = this
        binding.etEmail.addTextChangedListener(emailTextWatcher)
    }

    override fun onStop() {
        super.onStop()
        binding.etEmail.removeTextChangedListener(emailTextWatcher)
    }

    override fun onFocusChange(v: View?, hasFocus: Boolean) {
        when (v) {
            binding.etEmail -> {
                updateTick(binding.etEmail, isEmailValidated(), hasFocus)
            }
        }
    }

    fun updateTick(et: EditText, validated: Boolean, hasFocus: Boolean) {
        if (!hasFocus && validated) {
            et.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(requireContext(), R.drawable.ic_add_mail), null, ContextCompat.getDrawable(requireContext(), R.drawable.ic_check_green), null)
        } else {
            et.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(requireContext(), R.drawable.ic_add_mail), null, null, null)
        }
    }

    private val emailTextWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
        override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
            updateTick(binding.etEmail, isEmailValidated(), false)
        }

        override fun afterTextChanged(editable: Editable) {}
    }

    override fun onResponseSuccess(requestCode: Int, responseCode: Int, msg: String?, data: Any?) {
        super.onResponseSuccess(requestCode, responseCode, msg, data)

        when (requestCode) {
            ApiRequestCode.SEARCH_CONTACT_BY_EMAIL -> {
                val type = object : TypeToken<MutableList<ContactSearchEntity>>() {}.type
                val entities = Gson().fromJson<MutableList<ContactSearchEntity>>(Gson().toJson(data), type)

                if (entities.isNotEmpty()) {
                    if (!entities[0].platform.equals("PF", ignoreCase = true)) {
                        showSnack(getString(R.string.platformIsNotWallet))
                        return
                    } else {
                        vm.sendOtpStep1(binding.etEmail.text.toString())
                    }
                } else {
                    showSnack(getString(R.string.invalidUser))
                }
            }

            ApiRequestCode.PASSWORD_RECOVERY_STEP1 -> {
                val type = object : TypeToken<Contact>() {}.type
                val response = Gson().fromJson<Contact>(Gson().toJson(data), type)

                val user = UserRepo.getUser()
                user.id = response.idUsr
                user.email = response.email
                UserRepo.setOrUpdateUser(user, true)


                goStep2RecoveryPass()
            }
        }
    }

    private fun goStep2RecoveryPass()
    {
        var fr = activity?.supportFragmentManager?.beginTransaction()
        fr?.replace(R.id.container_login_fragment, StepTwoRecoveryPassFragment())
        fr?.commit()
    }


}
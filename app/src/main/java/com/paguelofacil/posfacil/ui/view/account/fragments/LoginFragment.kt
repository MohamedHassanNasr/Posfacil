package com.paguelofacil.posfacil.ui.view.account.fragments

import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.paguelofacil.posfacil.R
import com.paguelofacil.posfacil.base.BaseFragment
import com.paguelofacil.posfacil.data.network.api.ApiError
import com.paguelofacil.posfacil.data.network.api.ApiRequestCode
import com.paguelofacil.posfacil.data.network.response.LoginApiResponse
import com.paguelofacil.posfacil.databinding.FragmentLoginBinding
import com.paguelofacil.posfacil.repository.UserRepo
import com.paguelofacil.posfacil.ui.view.account.viewmodel.LoginViewModel
import com.paguelofacil.posfacil.ui.view.home.activities.HomeActivity
import com.paguelofacil.posfacil.ui.view.home.activities.IntroActivity
import com.paguelofacil.posfacil.util.Constantes.CoreConstants
import com.paguelofacil.posfacil.util.isValidPassword


class LoginFragment : BaseFragment(), View.OnFocusChangeListener, View.OnClickListener{


    lateinit var binding: FragmentLoginBinding

    private lateinit var vm: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vm = ViewModelProvider(requireActivity())[LoginViewModel::class.java]

        setBaseViewModel(vm)

        vm.getResponseObserver()
            .observe(this@LoginFragment, this)

        /*  if (!user.fingerprintAuthenticatedEmail.isNullOrEmpty()) {
            showFingerPrintPrompt()
          }
        */
    }

    override fun onStart() {
        super.onStart()
        binding.etEmail.onFocusChangeListener = this
        binding.etPassword.onFocusChangeListener = this
        binding.etPassword.addTextChangedListener(passwordTextWatcher)
        //binding.tvRegister.setOnClickListener(this)
        binding.tvForgotPassword.setOnClickListener(this)
        //binding.btnScan.setOnClickListener(this)
        binding.btnLogin.setOnClickListener(this)
        binding.ivClearPassword.setOnClickListener(this)
        binding.ivPasswordVisibility.setOnClickListener(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding=FragmentLoginBinding.inflate(inflater,container,false)
        val user = UserRepo.getUser()

        user.fingerPrintAuthenticatedLogin = false
        user.introShown = true
        UserRepo.setOrUpdateUser(user, true)


        if (!user.email.isNullOrEmpty()) {
            binding.etEmail.setText(user.email.toString())
            binding.etEmail.setSelection(binding.etEmail.text?.length ?: 0)
        }

        loadListeners()

        return binding.root
    }

    private fun goHome() {
        val intent= Intent(context, IntroActivity::class.java)
        startActivity(intent)
        activity?.finish()
    }

    private fun loadListeners() {

        binding.tvForgotPassword.setOnClickListener {

           // var fr = activity?.supportFragmentManager?.beginTransaction()
            //fr?.replace(R.id.container_login_fragment, StepOneRecoveryPassFragment())
            //fr?.commit()

            //var fr = activity?.supportFragmentManager?.beginTransaction()
           // fr?.replace(R.id.container_login_fragment, StepThreeRecoveryPassFragment())
          //  fr?.commit()
        }

    }

    private val passwordTextWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
        override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
            /*updateTickMark(binding.etPassword, isValidPassword(binding.etPassword.text.toString()), false, ContextCompat.getDrawable(this@LoginActivity, R.drawable.ic_add_password))*/
            if (charSequence.toString().isNotEmpty()) {
                binding.ivClearPassword.visibility = View.VISIBLE
                binding.ivPasswordVisibility.visibility = View.VISIBLE
            } else {
                binding.ivClearPassword.visibility = View.GONE
                binding.ivPasswordVisibility.visibility = View.VISIBLE
            }
        }

        override fun afterTextChanged(editable: Editable) {}
    }

    override fun onClick(v: View?) {
        when (v) {

            binding.tvForgotPassword -> {

                val user = UserRepo.getUser()
                user.tempEmailLogin=binding.etEmail.text.toString()
                UserRepo.setOrUpdateUser(user,true)

                var fr = activity?.supportFragmentManager?.beginTransaction()
                fr?.replace(R.id.container_login_fragment, StepOneRecoveryPassFragment())
                fr?.commit()
            }
            binding.btnLogin -> {
                if (validated()) {
                    vm.signIn(binding.etEmail.text.toString(), binding.etPassword.text.toString())
                }
            }

            binding.ivClearPassword -> {
                binding.etPassword.text?.clear()
            }
            binding.ivPasswordVisibility -> {
                if (binding.etPassword.transformationMethod == PasswordTransformationMethod.getInstance()) {
                    binding.etPassword.transformationMethod =
                        HideReturnsTransformationMethod.getInstance()
                    binding.etPassword.text?.let {
                        binding.etPassword.setSelection(it.length)
                    }
                    binding.ivPasswordVisibility.setImageDrawable(

                        ResourcesCompat.getDrawable(requireActivity().resources, R.drawable.ic_visibility_off, null)
                    )
                } else {
                    binding.etPassword.transformationMethod =
                        PasswordTransformationMethod.getInstance()
                    binding.etPassword.text?.let {
                        binding.etPassword.setSelection(it.length)
                    }
                    binding.ivPasswordVisibility.setImageDrawable(

                        ResourcesCompat.getDrawable(requireActivity().resources, R.drawable.ic_visibility_on, null)
                    )
                }
            }
        }
    }

    private fun isEmailValidated(): Boolean {
        if (binding.etEmail.text.isNullOrBlank()) {
            return false
        }

        return true
    }

    override fun onFocusChange(v: View?, hasFocus: Boolean) {
        when (v) {
            binding.etEmail -> {
                updateTickMark(
                    binding.etEmail,
                    isEmailValidated(),
                    hasFocus,

                    ResourcesCompat.getDrawable(requireActivity().resources, R.drawable.ic_add_mail, null)
                )
            }
            binding.etPassword -> {
                if (hasFocus) {
                    binding.etPassword.text?.let {
                        if (it.isNotEmpty()) {
                            binding.ivClearPassword.visibility = View.VISIBLE
                            binding.ivPasswordVisibility.visibility = View.VISIBLE
                        } else {
                            binding.ivClearPassword.visibility = View.GONE
                            binding.ivPasswordVisibility.visibility = View.GONE
                        }
                    }
                } else {
                    binding.ivClearPassword.visibility = View.GONE
                    binding.ivPasswordVisibility.visibility = View.GONE
                }

                updateTickMark(
                    binding.etPassword,
                    isValidPassword(binding.etPassword.text.toString()),
                    hasFocus,

                    ResourcesCompat.getDrawable(requireActivity().resources, R.drawable.ic_add_password, null)
                )
            }
        }
    }
    private fun updateTickMark(
        et: EditText,
        validated: Boolean,
        hasFocus: Boolean,
        leftDrawable: Drawable?
    ) {
        if (!hasFocus && validated) {
            et.setCompoundDrawablesWithIntrinsicBounds(
                leftDrawable,
                null,

                ResourcesCompat.getDrawable(requireActivity().resources, R.drawable.ic_check_green, null),
                null
            )
        } else {
            et.setCompoundDrawablesWithIntrinsicBounds(leftDrawable, null, null, null)
        }
    }

    private fun validated(): Boolean {
        if (binding.etEmail.text.toString().isEmpty()) {
            showSnack(getString(R.string.Please_enter_your_email_or_Alias))
            return false
        }

        if (binding.etPassword.text.toString().isEmpty()) {
            showSnack(getString(R.string.Please_enter_your_password))
            return false
        }
        if (!isValidPassword(binding.etPassword.text.toString())) {
            showSnack(getString(R.string.invalid_password_short))
            return false
        }
        return true
    }
    override fun onResponseSuccess(requestCode: Int, responseCode: Int, msg: String?, data: Any?) {
        super.onResponseSuccess(requestCode, responseCode, msg, data)

        when (requestCode) {
            ApiRequestCode.SIGN_IN -> {
                val type = object : TypeToken<LoginApiResponse>() {}.type
                val response = Gson().fromJson<LoginApiResponse>(Gson().toJson(data), type)


                UserRepo.saveUserData(response, binding.etPassword.text.toString())
                vm.updateUserLocal()


                goHome()
            }
        }

    }


    override fun onExceptionData(requestCode: Int, exception: ApiError, data: Any?) {

        val typeMap = object : TypeToken<Map<String, Object>>() {}.type
        val jsonParse=Gson().toJson(data)
        val  map=Gson().fromJson<Map<String, Object>>(jsonParse,typeMap)



    }
}
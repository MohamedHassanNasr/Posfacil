package com.paguelofacil.posfacil.ui.view.account.fragments

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.activityViewModels
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.paguelofacil.posfacil.ApplicationClass
import com.paguelofacil.posfacil.R
import com.paguelofacil.posfacil.base.BaseFragment
import com.paguelofacil.posfacil.data.network.api.ApiRequestCode
import com.paguelofacil.posfacil.data.network.response.LoginApiResponse
import com.paguelofacil.posfacil.databinding.FragmentLoginBinding
import com.paguelofacil.posfacil.repository.UserRepo
import com.paguelofacil.posfacil.ui.view.account.viewmodel.LoginViewModel
import com.paguelofacil.posfacil.ui.view.home.activities.IntroActivity
import com.paguelofacil.posfacil.util.KeyboardUtil
import com.paguelofacil.posfacil.util.isValidPassword
import com.pax.dal.entity.ETermInfoKey
import kotlinx.coroutines.CoroutineExceptionHandler
import timber.log.Timber


class LoginFragment : BaseFragment(), View.OnFocusChangeListener, View.OnClickListener {


    lateinit var binding: FragmentLoginBinding

    private val vm: LoginViewModel by activityViewModels()

    private val error = CoroutineExceptionHandler { _, exception ->
        Timber.e("Error ${exception.message.toString()}")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vm.getResponseObserver().observe(this@LoginFragment, this)
    }

    private fun loadLanguage() {
        binding.welcome.text = ApplicationClass.language.welcome
        binding.iniciarSesion.text = ApplicationClass.language.logIn
        binding.btnLogin.text = ApplicationClass.language.logIn
        binding.tvEmailLabel.text = ApplicationClass.language.emailUsername
        binding.tvPasswordLabel.text = ApplicationClass.language.password
        binding.tvForgotPassword.text = ApplicationClass.language.didYouForgetYourPassword
    }

    override fun onStart() {
        super.onStart()
        binding.etEmail.onFocusChangeListener = this
        binding.etPassword.onFocusChangeListener = this
        binding.etPassword.addTextChangedListener(passwordTextWatcher)
        binding.tvForgotPassword.setOnClickListener(this)
        binding.btnLogin.setOnClickListener(this)
        binding.ivClearPassword.setOnClickListener(this)
        binding.ivPasswordVisibility.setOnClickListener(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        val user = UserRepo.getUser()
        user.fingerPrintAuthenticatedLogin = false
        user.introShown = true
        UserRepo.setOrUpdateUser(user, true)
        setBaseViewModel(vm)
//        binding.etEmail.clearFocus()
//        binding.etPassword.clearFocus()
        Timber.e("ANTES DE HIDE")
        KeyboardUtil.hideKeyboard(requireActivity())
        Timber.e("ANTES DE HIDE from view")
        KeyboardUtil.hideKeyboard(requireActivity(), view)
        loadLanguage()

        return binding.root
    }

    private fun goHome() {
        val intent= Intent(context, IntroActivity::class.java)
        intent.putExtra("isMain", true)
        startActivity(intent)
        requireActivity().finish()
    }

    private val passwordTextWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
        override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
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
                user.tempEmailLogin = binding.etEmail.text.toString()
                UserRepo.setOrUpdateUser(user, true)

                val fr = activity?.supportFragmentManager?.beginTransaction()
                fr?.replace(R.id.container_login_fragment, StepOneRecoveryPassFragment())
                fr?.addToBackStack(null)?.commit()
            }

            binding.btnLogin -> {
                if (validated()) {
                    vm.signIn(binding.etEmail.text.toString().trim(), binding.etPassword.text.toString().trim())
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

                        ResourcesCompat.getDrawable(
                            requireActivity().resources,
                            R.drawable.ic_visibility_off,
                            null
                        )
                    )
                } else {
                    binding.etPassword.transformationMethod =
                        PasswordTransformationMethod.getInstance()
                    binding.etPassword.text?.let {
                        binding.etPassword.setSelection(it.length)
                    }
                    binding.ivPasswordVisibility.setImageDrawable(

                        ResourcesCompat.getDrawable(
                            requireActivity().resources,
                            R.drawable.ic_visibility_on,
                            null
                        )
                    )
                }
            }
        }
    }

    private fun isEmailValidated(): Boolean {
        return if (!binding.etEmail.text.isNullOrEmpty()) {
            binding.etEmail.text!!.matches(Patterns.EMAIL_ADDRESS.toRegex())
        }else{
            false
        }
    }

    override fun onFocusChange(v: View?, hasFocus: Boolean) {
        when (v) {
            binding.etEmail -> {
                updateTickMark(
                    binding.etEmail,
                    isEmailValidated(),
                    hasFocus,

                    ResourcesCompat.getDrawable(
                        requireActivity().resources,
                        R.drawable.ic_add_mail,
                        null
                    )
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

                    ResourcesCompat.getDrawable(
                        requireActivity().resources,
                        R.drawable.ic_add_password,
                        null
                    )
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

                ResourcesCompat.getDrawable(
                    requireActivity().resources,
                    R.drawable.ic_check_green,
                    null
                ),
                null
            )
        } else {
            et.setCompoundDrawablesWithIntrinsicBounds(leftDrawable, null, null, null)
        }
    }

    private fun validated(): Boolean {
        if (binding.etEmail.text.toString().isEmpty()) {
            showSnack(ApplicationClass.language.pleaseEnterYourEmailOrAlias)
            return false
        }

        if (binding.etPassword.text.toString().isEmpty()) {
            showSnack(ApplicationClass.language.pleaseEnterYourPassword)
            return false
        }
        if (!isValidPassword(binding.etPassword.text.toString())) {
            showSnack(ApplicationClass.language.invalidPasswordShort)
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
}
package com.paguelofacil.posfacil.ui.view.account.fragments

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.paguelofacil.posfacil.ApplicationClass
import com.paguelofacil.posfacil.R
import com.paguelofacil.posfacil.base.BaseFragment
import com.paguelofacil.posfacil.data.network.api.ApiRequestCode
import com.paguelofacil.posfacil.databinding.FragmentStepThreeRecoveryPassBinding
import com.paguelofacil.posfacil.repository.UserRepo
import com.paguelofacil.posfacil.ui.interfaces.BottomSheetCallback
import com.paguelofacil.posfacil.ui.view.account.dialog.ForgotPasswordRecoveredSuccessDialog
import com.paguelofacil.posfacil.ui.view.account.viewmodel.ForgotPasswordViewModel
import com.paguelofacil.posfacil.ui.view.home.activities.IntroActivity
import com.paguelofacil.posfacil.util.isValidPassword
import com.paguelofacil.posfacil.util.networkErrorConverter
import java.util.regex.Matcher
import java.util.regex.Pattern


class StepThreeRecoveryPassFragment : BaseFragment(), View.OnFocusChangeListener,
    View.OnClickListener {

    lateinit var binding: FragmentStepThreeRecoveryPassBinding

    private lateinit var vm: ForgotPasswordViewModel

    var error = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vm = ViewModelProvider(requireActivity())[ForgotPasswordViewModel::class.java]

        setBaseViewModel(vm)

        vm.getResponseObserver()
            .observe(this@StepThreeRecoveryPassFragment, this)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentStepThreeRecoveryPassBinding.inflate(inflater, container, false)

        loadListeners()
        loadLanguage()
        return binding.root
    }

    private fun loadLanguage() {
        binding.titlePassword.text = ApplicationClass.language.recuperarPassword
        binding.textViewTitle.text = ApplicationClass.language.stepThree_title_recovery_pass
        binding.textViewMessage.text = ApplicationClass.language.stepTwoRecoveryPass
        binding.tvPasswordLabel.text = ApplicationClass.language.newPass
        binding.tvNewPasswordLabel.text = ApplicationClass.language.repeatNewPass
        binding.tvValidatePass1.text = getString(R.string.validate_8_characters)
        binding.tvValidatePass2.text = getString(R.string.validate_1_mayus)
        binding.tvValidatePass3.text = getString(R.string.validate_1_minu)
        binding.tvValidatePass4.text = getString(R.string.validate_1_number)
        binding.tvValidatePass5.text = getString(R.string.validate_1_especial)
        binding.btnUpdatePassword.text = ApplicationClass.language.btnUpdatePass
    }

    private fun loadListeners() {

        binding.btnUpdatePassword.setOnClickListener {
            if (validated()) {
                if (UserRepo.getUser().tempCodeAuth != null) {
                    resetPassword()
                } else {
                    showSnack(getString(R.string.error_code_auth_new_pass))
                }
            }
        }

        binding.lnArrowBack.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
        }

    }

    private fun resetPassword(){
        vm.verifyOtpStep2(UserRepo.getUser().tempCodeAuth!!, onFailure = {
            showWarningDialog(it){
                resetPassword()
            }
        })
    }

    private fun showWarningDialog(message: String, onFailure: ()-> Unit){
        val dialog = context?.let { BottomSheetDialog(it) }

        val view = layoutInflater.inflate(R.layout.bottom_sheet_warning, null)
        val title =view.findViewById<TextView>(R.id.titleError)
        val description =view.findViewById<TextView>(R.id.descriptionError)
        val btn = view.findViewById<MaterialButton>(R.id.btnAccept)

        title.text = ApplicationClass.language.error
        description.text = if ((message == "400") or (message == "400") or (message == "400")){
            ApplicationClass.language.pwd_not_update
        }else{
            networkErrorConverter(message)
        }

        btn.text = ApplicationClass.language.try_againg
        btn.setOnClickListener {
            dialog?.dismiss()
            onFailure()
        }

        dialog?.setCancelable(true)

        dialog?.setContentView(view)

        dialog?.show()
    }


    override fun onStart() {
        super.onStart()
        binding.etPassword.onFocusChangeListener = this
        binding.etNewPassword.onFocusChangeListener = this
        binding.etPassword.addTextChangedListener(passwordTextWatcher)
        binding.etNewPassword.addTextChangedListener(newPasswordTextWatcher)
        binding.ivViewPass.setOnClickListener(this)
        binding.ivViewRepeatPass.setOnClickListener(this)
    }

    override fun onStop() {
        super.onStop()
        binding.etPassword.removeTextChangedListener(passwordTextWatcher)
        binding.etNewPassword.removeTextChangedListener(passwordTextWatcher)
        //binding.etConfirmationCode.removeTextChangedListener(confirmationCodeTextWatcher)
    }

    override fun onFocusChange(v: View?, hasFocus: Boolean) {
        when (v) {
            binding.etPassword -> {
                if (hasFocus) {
                    binding.etPassword.text?.let {
                        if (it.isNotEmpty()) {
                            // binding.ivClearPassword.visibility = View.VISIBLE
                            binding.ivViewPass.visibility = View.VISIBLE
                        } else {
                            // binding.ivClearPassword.visibility = View.GONE
                            binding.ivViewPass.visibility = View.GONE
                        }
                    }
                } else {
                    //  binding.ivClearPassword.visibility = View.GONE
                    binding.ivViewPass.visibility = View.GONE
                }

                updateTickMark(
                    binding.etPassword,
                    isValidPassword(binding.etPassword.text.toString()),
                    hasFocus
                )
            }
            binding.etNewPassword -> {
                if (hasFocus) {
                    binding.etPassword.text?.let {
                        if (it.isNotEmpty()) {
                            // binding.ivClearPassword.visibility = View.VISIBLE
                            binding.ivViewRepeatPass.visibility = View.VISIBLE
                        } else {
                            // binding.ivClearPassword.visibility = View.GONE
                            binding.ivViewRepeatPass.visibility = View.GONE
                        }
                    }
                } else {
                    //  binding.ivClearPassword.visibility = View.GONE
                    binding.ivViewRepeatPass.visibility = View.GONE
                }

                updateTickMark(
                    binding.etNewPassword,
                    isValidPassword(binding.etNewPassword.text.toString()),
                    hasFocus
                )
            }

        }
    }

    private fun validateText() {
        clearValidate()

        //VALIDATE 1
        if (binding.etPassword.text.toString().length < 12) {
            binding.tvValidatePass1.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.color_E11900
                )
            );

            Glide.with(binding.ivValidatePass1.context)
                .load(R.drawable.ic_error_validate).centerInside()
                .into(binding.ivValidatePass1)

            error++
        } else {
            binding.tvValidatePass1.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.color_30850F
                )
            );

            Glide.with(binding.ivValidatePass1.context)
                .load(R.drawable.ic_sucess_validate).centerInside()
                .into(binding.ivValidatePass1)


        }

        //VALIDATE 2

        val str: String = binding.etPassword.text.toString()

        var ch: Char
        for (i in 0 until str.length) {
            ch = str.get(i);

            if (Character.isDigit(ch)) {
                showValidate4(true) // numero
            }

            if (Character.isUpperCase(ch)) {
                showValidate2(true) // mayuscula
            }
            if (Character.isLowerCase(ch)) {
                showValidate3(true) // minuscula
            }


        }

        if (isValidPassEspecial(str)) {
            showValidate5(true) // character especial
        } else {
            showValidate5(false) // character especial
        }

        binding.lnDetailNewPass.visibility = View.VISIBLE
    }


    private fun validated(): Boolean {

        error = 0


        if (binding.etPassword.text.toString().isEmpty()) {
            error++
            binding.etPassword.requestFocus()
            showSnack(getString(R.string.Please_enter_your_new_password))
        } else if (binding.etNewPassword.text.toString().isEmpty()) {
            error++
            binding.etNewPassword.requestFocus()
            showSnack(getString(R.string.Please_enter_your_confirm_password))
        }


        if (!binding.etNewPassword.text.toString().isEmpty() &&
            !binding.etPassword.text.toString().isEmpty()
        ) {
            if (!binding.etNewPassword.text.toString().equals(binding.etPassword.text.toString())) {
                error++
                showSnack(getString(R.string.password_no_identicos))
            } else {
                validateText()

            }
        }


        println("++++++++++++++++++++++ cant error ++++++++++ $error")
        return error == 0


    }

    private fun clearValidate() {
        binding.tvValidatePass1.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.color_E11900
            )
        );

        Glide.with(binding.ivValidatePass1.context)
            .load(R.drawable.ic_error_validate).centerInside()
            .into(binding.ivValidatePass1)

        binding.tvValidatePass2.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.color_E11900
            )
        );

        Glide.with(binding.ivValidatePass2.context)
            .load(R.drawable.ic_error_validate).centerInside()
            .into(binding.ivValidatePass2)

        binding.tvValidatePass3.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.color_E11900
            )
        );

        Glide.with(binding.ivValidatePass3.context)
            .load(R.drawable.ic_error_validate).centerInside()
            .into(binding.ivValidatePass3)

        binding.tvValidatePass4.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.color_E11900
            )
        );

        Glide.with(binding.ivValidatePass4.context)
            .load(R.drawable.ic_error_validate).centerInside()
            .into(binding.ivValidatePass4)

        binding.tvValidatePass5.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.color_E11900
            )
        );

        Glide.with(binding.ivValidatePass5.context)
            .load(R.drawable.ic_error_validate).centerInside()
            .into(binding.ivValidatePass5)

    }


    fun isValidPassEspecial(password: String): Boolean {

        val p: Pattern = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE)
        val m: Matcher = p.matcher(password)
        return m.find()


    }

    fun isValidPassMayus(password: String): Boolean {
        val specialCharacters = "(?=.*[A-Z])"
        return password.matches(Regex(specialCharacters))
    }

    private fun showValidate2(validate: Boolean) {
        if (!validate) {
            binding.tvValidatePass2.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.color_E11900
                )
            );

            Glide.with(binding.ivValidatePass2.context)
                .load(R.drawable.ic_error_validate).centerInside()
                .into(binding.ivValidatePass2)

            error++
        } else {
            binding.tvValidatePass2.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.color_30850F
                )
            );

            Glide.with(binding.ivValidatePass2.context)
                .load(R.drawable.ic_sucess_validate).centerInside()
                .into(binding.ivValidatePass2)


        }

    }

    private fun showValidate3(validate: Boolean) {
        if (!validate) {
            binding.tvValidatePass3.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.color_E11900
                )
            );

            Glide.with(binding.ivValidatePass3.context)
                .load(R.drawable.ic_error_validate).centerInside()
                .into(binding.ivValidatePass3)

            error++
        } else {
            binding.tvValidatePass3.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.color_30850F
                )
            );

            Glide.with(binding.ivValidatePass3.context)
                .load(R.drawable.ic_sucess_validate).centerInside()
                .into(binding.ivValidatePass3)


        }

    }

    private fun showValidate4(validate: Boolean) {
        if (!validate) {
            binding.tvValidatePass4.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.color_E11900
                )
            );

            Glide.with(binding.ivValidatePass4.context)
                .load(R.drawable.ic_error_validate).centerInside()
                .into(binding.ivValidatePass4)

            error++
        } else {
            binding.tvValidatePass4.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.color_30850F
                )
            );

            Glide.with(binding.ivValidatePass4.context)
                .load(R.drawable.ic_sucess_validate).centerInside()
                .into(binding.ivValidatePass4)


        }

    }

    private fun showValidate5(validate: Boolean) {
        if (!validate) {
            binding.tvValidatePass5.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.color_E11900
                )
            );

            Glide.with(binding.ivValidatePass5.context)
                .load(R.drawable.ic_error_validate).centerInside()
                .into(binding.ivValidatePass5)

            error++
        } else {
            binding.tvValidatePass5.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.color_30850F
                )
            );

            Glide.with(binding.ivValidatePass5.context)
                .load(R.drawable.ic_sucess_validate).centerInside()
                .into(binding.ivValidatePass5)


        }

    }

    private val passwordTextWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
        override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
            updateTickMark(
                binding.etPassword,
                isValidPassword(binding.etPassword.text.toString()),
                false
            )
            validateText()
            if (charSequence.toString().isNotEmpty()) {
                // binding.ivClearPassword.visibility = View.VISIBLE
                binding.ivViewPass.visibility = View.VISIBLE
            } else {
                // binding.ivClearPassword.visibility = View.GONE
                binding.ivViewPass.visibility = View.GONE
            }
        }

        override fun afterTextChanged(editable: Editable) {}
    }

    private val newPasswordTextWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
        override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
            updateTickMark(
                binding.etNewPassword,
                isValidPassword(binding.etNewPassword.text.toString()),
                false
            )
            if (charSequence.toString().isNotEmpty()) {
                // binding.ivClearPassword.visibility = View.VISIBLE
                binding.ivViewRepeatPass.visibility = View.VISIBLE
            } else {
                // binding.ivClearPassword.visibility = View.GONE
                binding.ivViewRepeatPass.visibility = View.GONE
            }
        }

        override fun afterTextChanged(editable: Editable) {}
    }


    override fun onResponseSuccess(requestCode: Int, responseCode: Int, msg: String?, data: Any?) {
        super.onResponseSuccess(requestCode, responseCode, msg, data)
        when (requestCode) {
            ApiRequestCode.PASSWORD_RECOVERY_STEP2 -> {
                verifyStep()
            }
            ApiRequestCode.PASSWORD_RECOVERY_STEP3 -> {
                ForgotPasswordRecoveredSuccessDialog(
                    requireContext(), object : BottomSheetCallback {
                        override fun onActionOccur(
                            eventType: Int
                        ) {
                            val intent = Intent(context, IntroActivity::class.java)
                            startActivity(intent)
                            activity?.finish()
                            //requireActivity().finish()
                        }
                    }
                ).show(parentFragmentManager, "")
            }
        }
    }

    private fun verifyStep(){
        vm.verifyOtpStep3(
            binding.etPassword.text.toString(),
            UserRepo.getUser().tempCodeAuth!!,
            onFailure = {
                showWarningDialog(it){
                    verifyStep()
                }
            }
        )
    }

    override fun onClick(v: View?) {
        when (v) {

            binding.ivViewPass -> {
                if (binding.etPassword.transformationMethod == PasswordTransformationMethod.getInstance()) {
                    binding.etPassword.transformationMethod =
                        HideReturnsTransformationMethod.getInstance()
                    binding.etPassword.text?.let {
                        binding.etPassword.setSelection(it.length)
                    }
                    binding.ivViewPass.setImageDrawable(
                        ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.ic_visibility_on
                        )
                    )
                } else {
                    binding.etPassword.transformationMethod =
                        PasswordTransformationMethod.getInstance()
                    binding.etPassword.text?.let {
                        binding.etPassword.setSelection(it.length)
                    }
                    binding.ivViewPass.setImageDrawable(
                        ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.ic_visibility_off
                        )
                    )
                }
            }
            binding.ivViewRepeatPass -> {
                if (binding.etNewPassword.transformationMethod == PasswordTransformationMethod.getInstance()) {
                    binding.etNewPassword.transformationMethod =
                        HideReturnsTransformationMethod.getInstance()
                    binding.etNewPassword.text?.let {
                        binding.etNewPassword.setSelection(it.length)
                    }
                    binding.ivViewRepeatPass.setImageDrawable(
                        ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.ic_visibility_on
                        )
                    )
                } else {
                    binding.etNewPassword.transformationMethod =
                        PasswordTransformationMethod.getInstance()
                    binding.etNewPassword.text?.let {
                        binding.etNewPassword.setSelection(it.length)
                    }
                    binding.ivViewRepeatPass.setImageDrawable(
                        ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.ic_visibility_off
                        )
                    )
                }
            }
        }
    }


}
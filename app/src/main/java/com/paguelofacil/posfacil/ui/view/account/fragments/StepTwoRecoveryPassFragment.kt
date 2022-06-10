package com.paguelofacil.posfacil.ui.view.account.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.paguelofacil.posfacil.ApplicationClass
import com.paguelofacil.posfacil.R
import com.paguelofacil.posfacil.base.BaseFragment
import com.paguelofacil.posfacil.data.network.api.ApiRequestCode
import com.paguelofacil.posfacil.databinding.FragmentStepTwoRecoveryPassBinding
import com.paguelofacil.posfacil.repository.UserRepo
import com.paguelofacil.posfacil.ui.view.account.viewmodel.ForgotPasswordViewModel
import com.paguelofacil.posfacil.util.networkErrorConverter


class StepTwoRecoveryPassFragment : BaseFragment(), View.OnClickListener {

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
    ): View {
        binding = FragmentStepTwoRecoveryPassBinding.inflate(inflater, container, false)

        getInfoUser()

        loadListeners()

        loadLanguage()

        return binding.root
    }

    private fun loadLanguage() {
        binding.textViewTitle.text = ApplicationClass.language.recuperarPassword
        binding.textViewSubTitle.text = ApplicationClass.language.stepTwoTitleRecoveryPass
        binding.textViewMessage.text = ApplicationClass.language.stepTwoRecoveryPass
        binding.btnValidateCode.text = ApplicationClass.language.tvNext
        binding.tvResendCode.text = ApplicationClass.language.tvResendCode
    }

    private fun getInfoUser() {
        UserRepo.getUser().email?.let {
            binding.tvEmailUser.text = it

        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        vm.btnClickEvent.observe(viewLifecycleOwner) {
            if (validated()) {
                verifiOtp2()
            }
        }


    }

    private fun verifiOtp2(){
        vm.verifyOtpStep2(
            binding.tvOtpPos1.text.toString()
                    + binding.tvOtpPos2.text.toString()
                    + binding.tvOtpPos3.text.toString()
                    + binding.tvOtpPos4.text.toString()
                    + binding.tvOtpPos5.text.toString()
                    + binding.tvOtpPos6.text.toString()
        ){
            showWarningDialog(message = it){
                verifiOtp2()
            }
        }
    }

    private fun showWarningDialog(message: String, onFailure: ()-> Unit){
        val dialog = context?.let { BottomSheetDialog(it) }

        val view = layoutInflater.inflate(R.layout.bottom_sheet_warning, null)
        val title =view.findViewById<TextView>(R.id.titleError)
        val description =view.findViewById<TextView>(R.id.descriptionError)
        val btn = view.findViewById<MaterialButton>(R.id.btnAccept)

        title.text = ApplicationClass.language.error
        description.text = if ((message == "400") or (message == "400") or (message == "400")){
            ApplicationClass.language.errorPaidTryAgainOrContactOurSupportTeam
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
    }

    private val otp1TW: TextWatcher = object : TextWatcher {
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
                user.tempCodeAuth =
                    binding.tvOtpPos1.text.toString() + binding.tvOtpPos2.text.toString() + binding.tvOtpPos3.text.toString() + binding.tvOtpPos4.text.toString() + binding.tvOtpPos5.text.toString() + binding.tvOtpPos6.text.toString()
                UserRepo.setOrUpdateUser(user, true)
                goStep3RecoveryPass()
            }
        }
    }

    private fun goStep3RecoveryPass() {
        val fr = activity?.supportFragmentManager?.beginTransaction()
        fr?.replace(R.id.container_login_fragment, StepThreeRecoveryPassFragment())
        fr?.addToBackStack(null)?.commit()
    }

    override fun onClick(v: View?) {
        when (v) {
            binding.tvResendCode -> {
                UserRepo.getUser().email?.let {
                    sendotp(it)
                    showSnack("Se reenvio el codigo a $it")
                }

            }
        }
    }

    private fun sendotp(text: String){
        vm.sendOtpStep1(text){
            showWarningDialog(it){
                sendotp(text)
            }
        }
    }

    private fun loadListeners() {
        binding.btnValidateCode.setOnClickListener {

            if (validated()) {

            }
        }

        binding.lnArrowBack.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
        }

    }
    private fun sendOtp2(){
        vm.verifyOtpStep2(
            binding.tvOtpPos1.text.toString()
                    + binding.tvOtpPos2.text.toString()
                    + binding.tvOtpPos3.text.toString()
                    + binding.tvOtpPos4.text.toString()
                    + binding.tvOtpPos5.text.toString()
                    + binding.tvOtpPos6.text.toString()
        ){
            showWarningDialog(it){
                sendOtp2()
            }
        }
    }

}
package com.paguelofacil.posfacil.ui.view.account.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.paguelofacil.posfacil.ApplicationClass
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
import com.paguelofacil.posfacil.util.networkErrorConverter
import timber.log.Timber


class StepOneRecoveryPassFragment : BaseFragment(), View.OnFocusChangeListener {

    lateinit var binding: FragmentStepOneRecoveryPassBinding

    private lateinit var vm: ForgotPasswordViewModel

    private var emailTemp: String = ""

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
    ): View {
        binding = FragmentStepOneRecoveryPassBinding.inflate(inflater, container, false)

        println("dasdasdasdasdasdasda")
        loadListeners()

        loadLanguage()

        captureParams()

        return binding.root
    }

    private fun loadLanguage() {
        binding.titlePassword.text = ApplicationClass.language.recuperarPassword
        binding.textViewTitle.text = ApplicationClass.language.stepOneTitleRecoveryPass
        binding.textViewMessage.text = ApplicationClass.language.stepOneRecoveryPass
        binding.tvPasswordLabel.text = ApplicationClass.language.email
        binding.btnSendCode.text = ApplicationClass.language.btnRecoveryPass
    }

    private fun validated(): Boolean {
        if (binding.etEmail.text.isNullOrBlank()) {
            showSnack(ApplicationClass.language.pleaseEnterYourEmailOrAlias)
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
        emailTemp = user.tempEmailLogin ?: ""
        binding.etEmail.setText(emailTemp)
    }

    private fun loadListeners() {
        binding.btnSendCode.setOnClickListener {
            if (validated()) {
                getAndSave()
            }
        }

        binding.lnArrowBack.setOnClickListener {
            val args = arguments?.getBoolean("pass")
            Timber.e("PASSWORD STATUS $args")
            if (args!=null){
                if (args){
                    Timber.e("IS TRUES")
                    activity?.finish()
                }else{
                    Timber.e("IS FALSE")
                    activity?.supportFragmentManager?.popBackStack()
                }
            }else{
                Timber.e("IS FALSE I NULL")
                activity?.supportFragmentManager?.popBackStack()
            }
            /*activity?.supportFragmentManager?.popBackStack()*/
        }
    }

    private fun getAndSave(){
        vm.getUnsavedUserAccountDetail(binding.etEmail.text.toString()){
            showWarningDialog(it){
                getAndSave()
            }
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
            et.setCompoundDrawablesWithIntrinsicBounds(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ic_add_mail
                ),
                null,
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_check_green),
                null
            )
        } else {
            et.setCompoundDrawablesWithIntrinsicBounds(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ic_add_mail
                ), null, null, null
            )
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
                val entities =
                    Gson().fromJson<MutableList<ContactSearchEntity>>(Gson().toJson(data), type)

                if (entities.isNotEmpty()) {
                    if (!entities[0].platform.equals("PF", ignoreCase = true)) {
                        showSnack(getString(R.string.platformIsNotWallet))
                        return
                    } else {
                        sendOtp()
                    }
                } else {
                    showSnack(ApplicationClass.language.invalidAccount)
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

    private fun sendOtp(){
        vm.sendOtpStep1(binding.etEmail.text.toString()){
            showWarningDialog(it){
                sendOtp()
            }
        }
    }

    private fun showWarningDialog(message: String, onFailure: ()-> Unit){
        val dialog = context?.let { BottomSheetDialog(it) }

        val view = layoutInflater.inflate(R.layout.bottom_sheet_warning, null)
        val title =view.findViewById<TextView>(R.id.titleError)
        val description =view.findViewById<TextView>(R.id.descriptionError)
        val btn = view.findViewById<MaterialButton>(R.id.btnAccept)

        title.text = "!Ha ocurrido un error!"
        description.text = if ((message == "400") or (message == "400") or (message == "400")){
            "Su contraseña\nno ha podido ser actualizada"
        }else{
            networkErrorConverter(message)
        }

        btn.text = "Intentar nuevamente"
        btn.setOnClickListener {
            dialog?.dismiss()
            onFailure()
        }

        dialog?.setCancelable(true)

        dialog?.setContentView(view)

        dialog?.show()
    }

    private fun goStep2RecoveryPass() {
        val fr = activity?.supportFragmentManager?.beginTransaction()
        fr?.replace(R.id.container_login_fragment, StepTwoRecoveryPassFragment())
        fr?.addToBackStack(null)?.commit()
    }


}
package com.paguelofacil.posfacil.ui.view.home.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.Toolbar
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.paguelofacil.posfacil.R
import com.paguelofacil.posfacil.base.BaseFragment
import com.paguelofacil.posfacil.data.network.api.ApiRequestCode
import com.paguelofacil.posfacil.data.network.response.LoginApiResponse
import com.paguelofacil.posfacil.data.network.response.PosStatusApiResponse
import com.paguelofacil.posfacil.databinding.FragmentHomeBinding
import com.paguelofacil.posfacil.repository.UserRepo
import com.paguelofacil.posfacil.ui.view.account.viewmodel.LoginViewModel
import com.paguelofacil.posfacil.ui.view.home.activities.IntroActivity
import com.paguelofacil.posfacil.ui.view.home.viewmodel.HomeViewModel
import com.paguelofacil.posfacil.ui.view.settings.activities.AjustesActivity
import com.paguelofacil.posfacil.ui.view.transactions.payment.activities.CobroActivity
import com.paguelofacil.posfacil.util.Constantes.ConstantesView
import com.paguelofacil.posfacil.util.KeyboardUtil
import kotlinx.android.synthetic.main.app_bar_home.*
import kotlinx.android.synthetic.main.fragment_home.*


class HomeFragment : BaseFragment() {

    private var toolbar: Toolbar? =null

    private var tmpValue = ""
    private var isAdding = false

    lateinit var binding:FragmentHomeBinding

    private lateinit var vm: HomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState)

        vm = ViewModelProvider(requireActivity())[HomeViewModel::class.java]

        setBaseViewModel(vm)

        vm.getResponseObserver()
            .observe(this@HomeFragment, this)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= FragmentHomeBinding.inflate(inflater,container,false)
        loadListeners()

        return binding.root
    }


    private fun loadListeners() {

        binding.etMontoCobrar.setOnFocusChangeListener { view, b ->
            if (b) {
                KeyboardUtil.showKeyboard(activity)
            }
        }

        binding.etMontoCobrar.setOnClickListener {
            binding.etMontoCobrar.setSelection(binding.etMontoCobrar.length())
        }

        binding.etMontoCobrar.addTextChangedListener(montoTextWatcher)

        binding.btnNext.setOnClickListener{
//            goVerifyPayment()
            vm.checkZReport()
        }
    }

    private val montoTextWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
            tmpValue = binding.etMontoCobrar.text.toString()
        }

        override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
            isAdding = charSequence.toString().length > tmpValue.length
        }

        override fun afterTextChanged(editable: Editable) {
            binding.etMontoCobrar.removeTextChangedListener(this)
            val montoStr = binding.etMontoCobrar.text.toString()
            val newValue = when {
                montoStr.isEmpty() -> {
                    0f
                }
                isAdding -> {
                    val newChar = montoStr.last().digitToInt().toFloat()
                    val newCharSequence = montoStr.substring(0, montoStr.length - 1)
                    val preValue = newCharSequence.toFloat()
                    (preValue * 10) + (newChar / 100)
                }
                else -> {
                    if(montoStr.length == 1)
                        montoStr.toFloat() / 100
                    else montoStr.toFloat() / 10
                }
            }
            binding.etMontoCobrar.setText(String.format("%.2f", newValue))
            binding.etMontoCobrar.setSelection(binding.etMontoCobrar.length())
            binding.etMontoCobrar.addTextChangedListener(this)
        }
    }

    override fun onResume() {
        KeyboardUtil.showKeyboard(context, binding.etMontoCobrar)
        binding.etMontoCobrar.requestFocus()
        super.onResume()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater!!.inflate(R.menu.menu_profile_home, menu)
        true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item!!.itemId
        if (id == R.id.item_menu_profile){
            goViewAjustes(ConstantesView.PARAM_PROFILE)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun goViewAjustes(value:String) {
        val params = Bundle()
        params.putString(ConstantesView.PARAM_FRAGMENT, value)
        val intent= Intent(context, AjustesActivity::class.java)
        intent.putExtras(params)
        startActivityForResult(intent,200)
    }

    override fun onResponseSuccess(requestCode: Int, responseCode: Int, msg: String?, data: Any?) {
        super.onResponseSuccess(requestCode, responseCode, msg, data)

        when (requestCode) {
            ApiRequestCode.SUCCESS -> {
                val type = object : TypeToken<PosStatusApiResponse>() {}.type
                val response = Gson().fromJson<PosStatusApiResponse>(Gson().toJson(data), type)
                if(response.status == "OPEN") {
                    goVerifyPayment()
                }
//                UserRepo.saveUserData(response, binding.etPassword.text.toString())
//                vm.updateUserLocal()
            }
        }

    }

    private fun goVerifyPayment() {
        val intent= Intent(context, CobroActivity::class.java)
        startActivity(intent)
        activity?.finish()
    }
}
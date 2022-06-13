package com.paguelofacil.posfacil.ui.view.home.fragments

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.paguelofacil.posfacil.ApplicationClass
import com.paguelofacil.posfacil.R
import com.paguelofacil.posfacil.base.BaseFragment
import com.paguelofacil.posfacil.data.network.response.ReportZResponse
import com.paguelofacil.posfacil.databinding.FragmentHomeBinding
import com.paguelofacil.posfacil.ui.view.custom_view.ReportBottomSheet
import com.paguelofacil.posfacil.ui.view.home.viewmodel.HomeViewModel
import com.paguelofacil.posfacil.ui.view.transactions.payment.activities.CobroActivity
import com.paguelofacil.posfacil.util.Constantes.AppConstants
import com.paguelofacil.posfacil.util.KeyboardUtil
import com.pax.dal.entity.ETermInfoKey
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber


class HomeFragment : BaseFragment() {

    private var tmpValue = ""
    private var isAdding = false

    private val error = CoroutineExceptionHandler { _, exception ->
        Timber.e("Error ${exception.message.toString()}")
    }

    lateinit var binding: FragmentHomeBinding
    private val viewModel: HomeViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState)
        setBaseViewModel(viewModel)

        initObservers()
    }

    private fun changeTitle(){
        /*val view = layoutInflater.inflate(R.layout.app_bar_home, null)
        val title = view.findViewById<TextView>(R.id.titleApp)

        title.text = "Ajustes"*/
        val vm = ViewModelProvider(requireActivity()).get<HomeViewModel>(modelClass = HomeViewModel::class.java)

        vm.setTitle(ApplicationClass.language.billing_panel, true)
        Timber.e("VIEEE ${viewModel.x}")
    }

    private fun initObservers() {
        viewModel.liveDataValidateReportZ.observe(this) {
            processValidateResponse(it)
        }

        viewModel.liveDataGenerateReportZ.observe(this) {
            if (it != null) generateReportZSuccess()
        }

        viewModel.liveDataException.observe(this) { proccessException(it) }

        viewModel.mutableUpdateLanguage.observe(this) {
            if (it) {
                loadLanguage()
            }
        }
    }

    private fun generateReportZSuccess() {
        val dialog = ReportBottomSheet(
            getString(R.string.se_ha_enviado_el_reporte_z_exitosamente),
            getString(R.string.aceptar),
            callBackClose = { viewModel.generateReportZ() }
        )
        dialog.show(parentFragmentManager, "")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        loadListeners()
        loadLanguage()
        changeTitle()

        return binding.root
    }

    private fun loadLanguage() {
        binding.textViewTitle.text = ApplicationClass.language.montoCobrar
        binding.btnNext.text = ApplicationClass.language.cobrar
    }

    private fun processValidateResponse(reportZResponse: ReportZResponse?) {
        if (reportZResponse?.status == "OPEN") {
            goVerifyPayment()
        } else {
            val dialog = ReportBottomSheet(
                getString(R.string.aun_no_genero_el_reporte_z),
                getString(R.string.generar_reporte_z),
                callBackClose = { viewModel.generateReportZ() }
            )
            dialog.show(parentFragmentManager, "")
        }
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

        binding.btnNext.setOnClickListener {
            try {
                Sys?.termInfo?.let {
                    lifecycleScope.launch(Dispatchers.IO + error) {
                        try {
                            if (importeMinimoSuccess()) {
                                try {
                                    KeyboardUtil.hideKeyboard(requireActivity())
                                    viewModel.checkZReport(it[ETermInfoKey.SN] ?: "")
                                }catch (e: NoClassDefFoundError){
                                    Timber.e("ERRO EN CHECK Z")
                                }
                            } else {
                                showSnack(ApplicationClass.language.theMinimumAllowedAmount)
                            }
                        }catch (e: NoClassDefFoundError){
                            Timber.e("ERRO EN CHECK Z")
                        }
                    }
                }
            }catch (e: NoClassDefFoundError){
                Timber.e("ERRO EN CHECK Z")
            }
        }

        binding.etMontoCobrar.imeOptions = EditorInfo.IME_ACTION_DONE
        binding.etMontoCobrar.setOnEditorActionListener { textView, i, keyEvent ->
            KeyboardUtil.hideKeyboard(requireActivity())
            binding.etMontoCobrar.clearFocus()
            true
        }
    }

    private fun importeMinimoSuccess(): Boolean {//
        return getImportString().replace(',', '.').toDouble() >= 1.0
    }

    private val montoTextWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
            tmpValue = getImportString()
        }

        override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
            isAdding = charSequence.toString().length > tmpValue.length
        }

        override fun afterTextChanged(editable: Editable) {
            binding.etMontoCobrar.removeTextChangedListener(this)
            val montoStr = getImportString().replace(',', '.')
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
                    if (montoStr.length == 1)
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
        //KeyboardUtil.showKeyboard(context, binding.etMontoCobrar)
        binding.etMontoCobrar.requestFocus()
        changeTitle()
        super.onResume()
    }

    private fun getImportString(): String {
        return binding.etMontoCobrar.text.toString().replace(',', '.')
    }

    private fun goVerifyPayment() {
        val intent = Intent(context, CobroActivity::class.java)
        intent.putExtra(
            AppConstants.IntentConstants.HomeFragment().IMPORT,
            getImportString().replace(',', '.').toDouble()
        )
        intent.putExtra("monto", getImportString())
        startActivity(intent)
        requireActivity().finish()
    }


}
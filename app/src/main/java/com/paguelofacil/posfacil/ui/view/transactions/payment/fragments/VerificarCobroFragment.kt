package com.paguelofacil.posfacil.ui.view.transactions.payment.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.getSystemService
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.paguelofacil.posfacil.ApplicationClass
import com.paguelofacil.posfacil.R
import com.paguelofacil.posfacil.data.entity.CobroEntity
import com.paguelofacil.posfacil.databinding.FragmentVerificarCobroBinding
import com.paguelofacil.posfacil.model.ParamReembolsoPropina
import com.paguelofacil.posfacil.model.QrSend
import com.paguelofacil.posfacil.repository.ConfigurationsRepo
import com.paguelofacil.posfacil.ui.view.custom_view.CancelBottomSheet
import com.paguelofacil.posfacil.ui.view.home.activities.HomeActivity
import com.paguelofacil.posfacil.ui.view.transactions.payment.viewmodel.CobroViewModel
import com.paguelofacil.posfacil.util.KeyboardUtil
import org.json.JSONArray
import timber.log.Timber


class VerificarCobroFragment : Fragment() {

    lateinit var binding: FragmentVerificarCobroBinding
    var cvSelected: Int = 0
    private var listPropinas = arrayListOf<ParamReembolsoPropina>()

    private val viewModel: CobroViewModel by activityViewModels()

    private var dialog: CancelBottomSheet? = null
    private var importeImpuestoDefault = 0.07
    private var valueCeroString = "0.00"
    private var impuestoTotal = 0.0
    private var importeCobro = 0.0
    private var importePropina = 0.0

    private var tmpImpuestoValue = ""
    private var isAddingImpuesto = false
    private var tmpPropinaValue = ""
    private var isAddingPropina = false
    private var tip1Double = 0.0
    private var tip2Double = 0.0
    private var tip3Double = 0.0


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentVerificarCobroBinding.inflate(inflater, container, false)

        val details = arguments?.getString("monto")

        if (details != null){
            Timber.e("MONTOOO 2 $details")
            binding.montoImporteTitle.text = details
        }

        updateImporteCobro()
        getPropinas()
        addListenerETCustomTip()
        loadListeners()
        initOnClick()
        loadLanguage()
        binding.swDefaultImpuesto.isChecked = true
        return binding.root
    }


    private fun loadLanguage() {
        binding.textViewTitle.text = ApplicationClass.language.verificarCobro
        binding.textViewImporteCobro.text = ApplicationClass.language.importeCobro
        binding.textViewPropinas.text = ApplicationClass.language.propinas
        binding.textViewImpuestos.text = ApplicationClass.language.impuestos
        binding.impuestoSugerido.text = ApplicationClass.language.impuestoSugerido
        binding.textViewResumenCobro.text = ApplicationClass.language.resume_cobro
        binding.textViewImporteBaseTitle.text = ApplicationClass.language.importeBase
        binding.textViewPropinasTitle.text = ApplicationClass.language.propinas
        binding.textViewImpuestosTitle.text = ApplicationClass.language.impuestos
        binding.btnCobrarNext.text = ApplicationClass.language.cobrar
    }

    private fun showBottomSheet() {
        dialog = CancelBottomSheet(
            callBackClose = { dismissBottomSheetCancel() },
            callbackVolver = { dismissBottomSheetCancel() },
            callbackCancelar = {
                dismissBottomSheetCancel()
                goHome()
            }
        )
        dialog?.show(parentFragmentManager, "")
    }

    private fun dismissBottomSheetCancel() {
        dialog?.dismiss()
    }

    private fun goHome() {
        val intent = Intent(context, HomeActivity::class.java)
        startActivity(intent)
        activity?.finish()
    }

    private fun initOnClick() {
        binding.etCustomTip.setOnClickListener {
            binding.etCustomTip.setSelection(binding.etCustomTip.length())
        }
        binding.etCustomTaxe.setOnClickListener {
            binding.etCustomTaxe.setSelection(binding.etCustomTaxe.length())
        }
    }

    private fun getPropinas() {

        val systemsParam = ConfigurationsRepo.getSystemParamsLocal()
        val dataRefund = systemsParam._default_values_tip

        val json = JSONArray(dataRefund)

        val type = object : TypeToken<ParamReembolsoPropina>() {}.type

        for (i in 1..3) {

            val item = json.getString(i)
            val propinaJson = Gson().fromJson<ParamReembolsoPropina>(item, type)

            val propina: ParamReembolsoPropina = ParamReembolsoPropina(
                propinaJson.key,
                propinaJson.valueToShow.replace("trans#", ""),
                propinaJson.value
            )

            listPropinas.add(propina)

        }
        val tip1 = listPropinas[0].value
        val tip2 = listPropinas[1].value
        val tip3 = listPropinas[2].value

        tip1Double = tip1.toDouble() / 100 * importeCobro
        tip2Double = tip2.toDouble() / 100 * importeCobro
        tip3Double = tip3.toDouble() / 100 * importeCobro

        binding.tvTip1.text = "$tip1%"
        binding.tvTip2.text = "$tip2%"
        binding.tvTip3.text = "$tip3%"

        binding.tvMontoTip1.text = calculateImporte(tip1.toDouble() / 100, importeCobro)
        binding.tvMontoTip2.text = calculateImporte(tip2.toDouble() / 100, importeCobro)
        binding.tvMontoTip3.text = calculateImporte(tip3.toDouble() / 100, importeCobro)

    }

    private fun calculateImporte(
        value1: Double,
        value2: Double = 1.0,
        isSymbol: Boolean = true
    ): String {
        val symbol = if (isSymbol) "$" else ""
        return "$symbol${String.format("%.2f", (value1 * value2))}"
    }

    private fun loadListeners() {

        binding.lnCloseBack.setOnClickListener {
            showBottomSheet()
        }

        binding.lnArrowBack.setOnClickListener {
            Timber.e("BACK PRESSED")
            val intent = Intent(context, HomeActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }

        binding.btnCobrarNext.setOnClickListener {
            val fr = activity?.supportFragmentManager?.beginTransaction()
            val bundle= Bundle()
            val fg = ComprobanteCobroFragment()
            Timber.e("MONTOOOOO UWU ${binding.textViewImporteTotal.text.toString().trim()}/${binding.textViewImporteImpuesto.text.toString().trim()}")
            bundle.putParcelable("data", QrSend(
                amount = binding.textViewImporteTotal.text.toString().trim(),
                taxes = binding.textViewImporteImpuesto.text.toString().trim(),
                tip = binding.textViewPropina.text.toString().trim()
            ))
            fg.setArguments(bundle)
            fr?.replace(R.id.container_frag_cobro, fg)
            fr?.addToBackStack(null)?.commit()
        }


        binding.cardTip1.setOnClickListener {

            if (cvSelected != 1) {
                clearSelectTips()
                paintSelectCard(binding.cardTip1, binding.tvTip1, binding.tvMontoTip1)
                cvSelected = 1
                updateImportePropina(tip1Double)
            } else {
                if (cvSelected == 1) {
                    clearSelectTips()
                    cvSelected = 0
                    updateImportePropina()
                }
            }
        }

        binding.cardTip2.setOnClickListener {
            if (cvSelected != 2) {
                clearSelectTips()
                paintSelectCard(binding.cardTip2, binding.tvTip2, binding.tvMontoTip2)
                cvSelected = 2
                updateImportePropina(tip2Double)
            } else {

                if (cvSelected == 2) {
                    clearSelectTips()
                    cvSelected = 0
                    updateImportePropina()
                }
            }
        }

        binding.cardTip3.setOnClickListener {
            if (cvSelected != 3) {
                clearSelectTips()
                paintSelectCard(binding.cardTip3, binding.tvTip3, binding.tvMontoTip3)
                cvSelected = 3
                updateImportePropina(tip3Double)
            } else {

                if (cvSelected == 3) {
                    clearSelectTips()
                    cvSelected = 0
                    updateImportePropina()
                }
            }
        }

        binding.cvOtherTip.setOnClickListener {
            updateImportePropina()
            if (cvSelected != 4) {
                clearSelectTips()
                binding.cvOtherTip.setCardBackgroundColor(
                    ContextCompat.getColor(
                        binding.cardTip1.context,
                        R.color.color_6BBE22
                    )
                )

                Glide.with(binding.ivPointsTips.context)
                    .load(R.drawable.ic_equis_tips).centerInside()
                    .into(binding.ivPointsTips)

                cvSelected = 4
                binding.etCustomTip.visibility = View.VISIBLE
                binding.etCustomTip.setText(valueCeroString)
                //KeyboardUtil.showKeyboard(activity, binding.etCustomTip)
                binding.etCustomTip.requestFocus()
            } else if (cvSelected == 4) {
                clearSelectTips()
                cvSelected = 0
            }
        }

        binding.etCustomTaxe.setOnFocusChangeListener { view, b ->
            Timber.e("FOCUS ET $b")
            binding.etCustomTaxe.isCursorVisible = b
        }


        binding.swDefaultImpuesto.setOnCheckedChangeListener { compoundButton, b ->
            val inputMethodManager = activity?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            activity?.currentFocus?.let {
                Timber.e("VIEWWW NONUL")
                inputMethodManager.hideSoftInputFromWindow(it.windowToken, 0)
            }
            binding.etCustomTaxe.setSelection(binding.etCustomTaxe.length())
            KeyboardUtil.hideKeyboard(activity)
            binding.etCustomTaxe.isEnabled = !b
            if (!b) {
                binding.textViewImporteImpuesto.text = valueCeroString
                impuestoTotal = 0.00
                calculateImporteTotal()
                binding.etCustomTaxe.setText(valueCeroString)
                //KeyboardUtil.showKeyboard(activity, binding.etCustomTaxe)
                binding.etCustomTip.setSelection(binding.etCustomTip.length())
                addListenerETCustomTaxe()
            } else {
                removeListenerETCustomTaxe()
                binding.etCustomTaxe.clearFocus()
                val importeCalculado =
                    calculateImporte(importeCobro, importeImpuestoDefault, false).replace(',', '.')
                binding.etCustomTaxe.setText(importeCalculado)
                binding.textViewImporteImpuesto.text = importeCalculado
                impuestoTotal = importeCalculado.toDouble()
                calculateImporteTotal()
            }
        }
    }

    private val montoPropinaTextWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
            tmpPropinaValue = getTextCustomTip()
        }

        override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
            isAddingPropina = charSequence.toString().length > tmpPropinaValue.length
        }

        override fun afterTextChanged(editable: Editable) {
            removeListenerETCustomTip()
            val montoStr = getTextCustomTip()
            val newValue = when {
                montoStr.isEmpty() -> {
                    0f
                }
                isAddingPropina -> {
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
            binding.etCustomTip.setText(String.format("%.2f", newValue))
            binding.etCustomTip.setSelection(binding.etCustomTip.length())
            binding.etCustomTip.addTextChangedListener(this)
            updateImportePropina(newValue.toDouble())
        }
    }

    private val montoImpuestoTextWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
            tmpImpuestoValue = getTextCustomTaxe()
        }

        override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
            isAddingImpuesto = charSequence.toString().length > tmpImpuestoValue.length
        }

        override fun afterTextChanged(editable: Editable) {
            removeListenerETCustomTaxe()
            val montoStr = getTextCustomTaxe()
            val newValue = when {
                montoStr.isEmpty() -> {
                    0f
                }
                isAddingImpuesto -> {
                    val newChar = montoStr.last().digitToInt().toFloat()
                    val newCharSequence = montoStr.substring(0, montoStr.length - 1)
                    val preValue = newCharSequence.replace(',','.').toFloat()
                    (preValue * 10) + (newChar / 100)
                }
                else -> {
                    if (montoStr.length == 1)
                        montoStr.replace(',','.').toFloat() / 100
                    else montoStr.replace(',','.').toFloat() / 10
                }
            }
            impuestoTotal = newValue.toDouble()
            binding.textViewImporteImpuesto.text = String.format("%.2f", newValue)
            binding.etCustomTaxe.setText(String.format("%.2f", newValue))
            binding.etCustomTaxe.setSelection(binding.etCustomTaxe.length())
            binding.etCustomTaxe.addTextChangedListener(this)
            calculateImporteTotal()
        }
    }

    private fun removeListenerETCustomTaxe() {
        binding.etCustomTaxe.removeTextChangedListener(montoImpuestoTextWatcher)
    }

    private fun addListenerETCustomTaxe() {
        binding.etCustomTaxe.addTextChangedListener(montoImpuestoTextWatcher)
    }

    private fun removeListenerETCustomTip() {
        binding.etCustomTip.removeTextChangedListener(montoPropinaTextWatcher)
    }

    private fun addListenerETCustomTip() {
        binding.etCustomTip.addTextChangedListener(montoPropinaTextWatcher)
    }

    private fun getTextCustomTaxe(): String {
        return binding.etCustomTaxe.text.toString()
    }

    private fun getTextCustomTip(): String {
        return binding.etCustomTip.text.toString()
    }

    private fun paintSelectCard(cardView: CardView, textView: TextView, textMount: TextView) {
        cardView.setCardBackgroundColor(
            ContextCompat.getColor(
                binding.cardTip1.context,
                R.color.color_6BBE22
            )
        )
        textView.setTextColor(Color.WHITE);
        textMount.visibility = View.VISIBLE
    }


    private fun clearSelectTips() {
        binding.etCustomTip.visibility = View.GONE

        Glide.with(binding.ivPointsTips.context)
            .load(R.drawable.ic_points).centerInside()
            .into(binding.ivPointsTips)

        binding.cardTip1.setCardBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.color_E0E3E7
            )
        )
        binding.cardTip2.setCardBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.color_E0E3E7
            )
        )
        binding.cardTip3.setCardBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.color_E0E3E7
            )
        )
        binding.cvOtherTip.setCardBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.color_E0E3E7
            )
        )

        binding.tvTip1.setTextColor(ContextCompat.getColor(requireContext(), R.color.color_515A69));
        binding.tvTip2.setTextColor(ContextCompat.getColor(requireContext(), R.color.color_515A69));
        binding.tvTip3.setTextColor(ContextCompat.getColor(requireContext(), R.color.color_515A69));

    }

    private fun updateImportePropina(importe: Double = 0.0) {
        importePropina = importe
        binding.textViewPropina.text = String.format("%.2f", importe.toFloat())
        calculateImporteTotal()
    }

    private fun updateImporteCobro() {
        importeCobro = viewModel.importeCobro
        binding.montoImporteTitle.text = String.format("%.2f", importeCobro.toFloat())
        binding.textViewImporteBase.text = String.format(" % .2f", importeCobro.toFloat())
    }

    private fun calculateImporteTotal() {
        val importeTotal = importeCobro + impuestoTotal + importePropina
        val cobroEntity = CobroEntity(
            importeCobro = String.format(" % .2f", importeCobro.toFloat()),
            impuesto = String.format(" % .2f", impuestoTotal.toFloat()),
            propina = String.format(" % .2f", importePropina.toFloat()),
            importeTotal = String.format(" % .2f", importeTotal.toFloat())
        )
        viewModel.updateDataCobro(cobroEntity)
        binding.textViewImporteTotal.text = String.format(" % .2f", importeTotal.toFloat())
    }
}
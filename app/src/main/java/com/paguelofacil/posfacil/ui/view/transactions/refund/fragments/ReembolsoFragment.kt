package com.paguelofacil.posfacil.ui.view.transactions.refund.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.paguelofacil.posfacil.ApplicationClass
import com.paguelofacil.posfacil.R
import com.paguelofacil.posfacil.base.BaseFragment
import com.paguelofacil.posfacil.databinding.FragmentReembolsoBinding
import com.paguelofacil.posfacil.model.*
import com.paguelofacil.posfacil.repository.ConfigurationsRepo
import com.paguelofacil.posfacil.repository.UserRepo
import com.paguelofacil.posfacil.ui.interfaces.MotivosEvent
import com.paguelofacil.posfacil.ui.view.adapters.ListMotivoReembolsoAdapter
import com.paguelofacil.posfacil.ui.view.transactions.refund.viewmodel.ReembolsoViewModel
import com.paguelofacil.posfacil.util.KeyboardUtil
import com.pax.dal.entity.ETermInfoKey
import kotlinx.coroutines.launch
import org.json.JSONArray
import timber.log.Timber
import kotlin.math.log


class ReembolsoFragment : BaseFragment(), MotivosEvent{

    lateinit var binding: FragmentReembolsoBinding
    private val viewModel: ReembolsoViewModel by activityViewModels()

    private var listMotivos = arrayListOf<ParamReembolsoPropina>()
    var montoMax = 0f
    var logo = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentReembolsoBinding.inflate(inflater,container,false)
        //setBaseViewModel(viewModel)
        binding.etMontoCobrar.setSelection(binding.etMontoCobrar.length())
        binding.tvTitle.text = ApplicationClass.language.reembolsoToolbar
        binding.emitRefund.text = ApplicationClass.language.emitirasReembolso
        binding.importRefund.text = ApplicationClass.language.importeReembolsar
        binding.methodRefund.text = ApplicationClass.language.metodoReembolso
        binding.motivoRefund.text = ApplicationClass.language.motivoReembolso
        binding.btnConfirmReembolso.text = ApplicationClass.language.confirmarReembolso
        binding.motivos.text = ApplicationClass.language.select
        /*binding.motivos.text = ApplicationClass.language*/

        initObservers()
        getMotivos()

        loadListeners()

        loadData()

        return binding.root
    }

    /**
     *
     * Obtener los motivos de reembolso
     * Mostrar el valor segun el diccionario de datos(api de traducciones trans#)
     */

    private fun getMotivos() {

        val systemsParam= ConfigurationsRepo.getSystemParamsLocal()
        val dataRefund=systemsParam._default_values_refund

        val json = JSONArray(dataRefund)

        val type = object : TypeToken<ParamReembolsoPropina>() {}.type

        for (i in 0 until json.length()) {

            val item = json.getString(i)
            val motivoJson = Gson().fromJson<ParamReembolsoPropina>(item, type)

            val motivo = ParamReembolsoPropina(motivoJson.key,
                motivoJson.valueToShow.replace("trans#", ""),
                motivoJson.value)

            listMotivos.add(motivo)

        }

    }

    private fun loadListeners() {
        binding.btnConfirmReembolso.setOnClickListener{
            lifecycleScope.launch {
                val user = UserRepo.getUser()
                Timber.e("IDD ${user.idMerchant}")
                user?.let {
                    Sys?.termInfo?.let {serial->
                        viewModel.setRefund(
                            request = RefundApiRequest(
                                amount = binding.etMontoCobrar.text.toString().replace(',', '.').toDouble(),
                                taxAmount = 0f.toDouble(),
                                email = it.email ?: "",
                                phone = it.phone ?: "",
                                concept = binding.motivos.text.toString(),
                                description = binding.motivos.text.toString(),
                                idMerchant = it.idMerchant?.dropLast(2)?.toLong() ?: 0,
                                idMerchantService = 5230,
                                codOperRelatedTransaction = binding.tvOpCode.text.toString(),
                                additionalData = AddionalData(
                                    pos = PosAddionalData(
                                        serial = serial[ETermInfoKey.SN] ?: "",
                                        idUser = it.id ?: 0,
                                        idMerchant = it.idMerchant?.dropLast(2)?.toLong() ?: 0
                                    )
                                )
                            ),
                            onSuccess = {
                                //todo navegar
                                val fr = activity?.supportFragmentManager?.beginTransaction()
                                val fragment = ComprobanteReembolsoFragment()
                                val bundle = Bundle()
                                it?.let { bundle.putParcelable("data", RefundResult(
                                    opCode = it.data.codOper,
                                    amount = it.data.totalPay,
                                    cardNumber = binding.tvPaymentMethod.text.toString(),
                                    cardType = it.data.cardType,
                                    date = it.serverTime,
                                    motivo = binding.motivos.text.toString() //todo cambiar
                                )) }
                                fragment.arguments = bundle
                                fr?.replace(R.id.container_frag_transactions, fragment)
                                fr?.commit()
                            },
                            onFailure = {
                                Toast.makeText(activity, it, Toast.LENGTH_SHORT).show()
                            }
                        )
                    }

                }
            }
        }

        binding.motivos.setOnClickListener {
            showBottomSheet()
        }
        binding.motivos.text = listMotivos.find { it.isChecked }?.value

        binding.etMontoCobrar.setOnClickListener {
            binding.etMontoCobrar.setSelection(binding.etMontoCobrar.length())
        }
        binding.etMontoCobrar.addTextChangedListener(montoTextWatcher)
        /*binding.etMontoCobrar.addTextChangedListener {
            if (it.toString().toFloat() <= montoMax){
                Timber.e("MENOS")
            }else{
                Timber.e("MAYOR")
                binding.etMontoCobrar.text.toString().dropLast(1)
            }
        }*/

        binding.ivBack.setOnClickListener{
            activity?.finish()
        }

        binding.etMontoCobrar.imeOptions = EditorInfo.IME_ACTION_DONE
        binding.etMontoCobrar.setOnEditorActionListener { textView, i, keyEvent ->
            KeyboardUtil.hideKeyboard(requireActivity())
            binding.etMontoCobrar.clearFocus()
            true
        }
    }
    private var isAdding = false
    private var tmpValue = ""

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

    private fun showBottomSheet() {

        val dialog = context?.let { BottomSheetDialog(it) }

        val view = layoutInflater.inflate(R.layout.bottom_sheet_dg_motivos, null)
        val recyclerView = view.findViewById<RecyclerView>(R.id.rv_motivos_reembolso)
        val etMotivo = view.findViewById<AppCompatEditText>(R.id.motivoCustom)
        val tituloMotivo = view.findViewById<TextView>(R.id.tituloMotivos)

        tituloMotivo.text = ApplicationClass.language.motivoReembolso

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = ListMotivoReembolsoAdapter(listMotivos,R.layout.row_motivos_reembolso,dialog, this)

        etMotivo.imeOptions = EditorInfo.IME_ACTION_SEND
        etMotivo.setOnEditorActionListener { textView, i, keyEvent ->
            binding.motivos.text = etMotivo.text
            dialog?.hide()
            true
        }
        dialog?.setCancelable(true)

        dialog?.setContentView(view)

        dialog?.show()
    }

    private fun getImportString(): String {
        return binding.etMontoCobrar.text.toString().replace(',', '.')
    }

    private fun loadData() {
        val detail = arguments?.getParcelable<TransactionBundle>("detailTX")
        Timber.e("DETAIL ${detail?.amount}")
        binding.tvOpCode.text = detail?.opCode.toString()
        binding.tvAmount.text = detail?.amount
        montoMax = detail?.amount?.drop(1)?.replace(',', '.')?.toFloat() ?: 0f
        //binding.etMontoCobrar.setText(detail?.currencyAmountStr)
        binding.tvPaymentMethod.text = String.format(getString(R.string.visible_card_pattern), detail?.cardNumber)
        binding.imageView.setImageResource(
            when(detail?.cardType){
                "VISA"->{
                    R.drawable.visa
                }
                "MC"->{
                    R.drawable.ic_mastercard_logo
                }
                "WALLET"->{
                    R.drawable.ic_app_icon
                }
                else -> {
                    R.drawable.ic_app_icon
                }
            })
        logo = detail?.cardType ?: ""
    }

    private fun initObservers() {
        viewModel.liveDataRefundResponse.observe(viewLifecycleOwner) {
            val fr = activity?.supportFragmentManager?.beginTransaction()
            val fragment = ComprobanteReembolsoFragment()
            val bundle = Bundle()
            it?.let { bundle.putParcelable("data", it) }
            fragment.arguments = bundle
            fr?.replace(R.id.container_frag_transactions, fragment)
            fr?.commit()
        }

        //viewModel.liveDataException.observe(this) { proccessException(it) }
    }

    override fun getMotivos(motivo: String) {
        binding.motivos.text = motivo
    }
}
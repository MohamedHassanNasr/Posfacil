package com.paguelofacil.posfacil.ui.view.transactions.payment.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.paguelofacil.posfacil.ApplicationClass
import com.paguelofacil.posfacil.R
import com.paguelofacil.posfacil.base.BaseFragment
import com.paguelofacil.posfacil.databinding.FragmentTransactionsBinding
import com.paguelofacil.posfacil.model.Transaction
import com.paguelofacil.posfacil.repository.ReportRepo
import com.paguelofacil.posfacil.repository.UserRepo
import com.paguelofacil.posfacil.ui.view.adapters.ListTransactionsAdapter
import com.paguelofacil.posfacil.ui.view.home.viewmodel.HomeViewModel
import com.paguelofacil.posfacil.ui.view.transactions.payment.activities.MovementsFilterActivity
import com.paguelofacil.posfacil.ui.view.transactions.payment.viewmodel.TransactionsViewModel
import com.paguelofacil.posfacil.util.KeyboardUtil
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*


class TransactionsFragment : BaseFragment() {

    lateinit var binding: FragmentTransactionsBinding

    private lateinit var viewModel: TransactionsViewModel

    private lateinit var adapter: ListTransactionsAdapter

    var transactions = listOf<Transaction>()
    var transactionsOriginal = listOf<Transaction>()

    private var filtersApplied = false
    private var resultFilter: String? = null
    private var paymentMethodFilter: String? = null
    private var operatorFilter: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(requireActivity()).get(TransactionsViewModel::class.java)

        setBaseViewModel(viewModel)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentTransactionsBinding.inflate(inflater, container, false)

        initRecyclerView()

        Timber.e("TRAD ${ApplicationClass.language}")
        binding.today.text = ApplicationClass.language.today

        transactions = if (filtersApplied) {
            adapter.listTransactionsTmp
        } else {
            adapter.getTransactions()
        }

        binding.swipe.setOnRefreshListener {
            viewModel.getAllTransactions()
        }

        changeTitle(boolean = false)

        Timber.e("TRANSACTION CREATE $transactions")
        binding.etSearch.hint = (ApplicationClass.language.filter_search)

        loadListeners()

        KeyboardUtil.hideKeyboard(context, binding.etSearch)

        viewModel.getAllTransactions()
        initObservers()
        return binding.root
    }

    private fun changeTitle(text: String? = null, boolean: Boolean){
        /*val view = layoutInflater.inflate(R.layout.app_bar_home, null)
        val title = view.findViewById<TextView>(R.id.titleApp)

        title.text = "Ajustes"*/
        val viewModel = ViewModelProvider(requireActivity()).get<HomeViewModel>(modelClass = HomeViewModel::class.java)

        viewModel.setTitle(text ?: ApplicationClass.language.transactionsMenu, boolean)
        Timber.e("VIEEE ${viewModel.x}")
    }

    private fun loadListeners() {

        binding.ivFilter.setOnClickListener {
            val intent = Intent(context, MovementsFilterActivity::class.java)
//            context?.startActivity(intent)
            Timber.e("ROLE ${UserRepo.getUser().merchantProfile?.idProfile}")

            val role: String = when(UserRepo.getUser().merchantProfile?.idProfile){
                0->{
                    "Visor"
                }
                1->{
                    "Visor"
                }
                2->{
                    "Basico"
                }
                3->{
                    "Admin"
                }
                else->{
                    "Visor"
                }
            }
            val transactions = adapter.getTransactions()
            val operatorId = transactions.firstOrNull()?.operatorId

            intent.putExtra("role", role)
            intent.putExtra("filters_applied", filtersApplied)
            resultFilter?.let { intent.putExtra("result", it) }
            paymentMethodFilter?.let { intent.putExtra("payment_method", it) }
            operatorFilter?.let { intent.putExtra("operator", it); Timber.e("IDDDS $it") }
            startActivityForResult(intent, 0)
        }

        binding.clSearch.setOnFocusChangeListener { view, b ->
            KeyboardUtil.hideKeyboard(context, binding.etSearch)
        }

        binding.etSearch.addTextChangedListener {
            it?.let { text ->
                Timber.e("TEXT CHANE $text")
                viewModel.setFilterText(text.toString())
            }
        }

    }

    private fun initObservers() {
        viewModel.liveDataTransactionList.observe(viewLifecycleOwner) {
            it?.let {
                binding.swipe.isRefreshing = false
                val parsedResponse = it.map { item -> Transaction.fromApiResponse(item) }
                transactions = parsedResponse
                transactionsOriginal = parsedResponse
                adapter.setTransactions(parsedResponse)
            }
        }

        viewModel.textSearch.observe(viewLifecycleOwner) { text ->
            Timber.e("TEXT OBSERVER $text original $transactionsOriginal")
            lifecycleScope.launch {
                if (filtersApplied) {
                    Timber.e("FILTER TRUE")
                    if (text.isEmpty()) {
                        Timber.e("TEXT EMPTY")
                        resultFilter.let {
                            if (it != null){
                                if (paymentMethodFilter != null){
                                    when(paymentMethodFilter){
                                        "VISA"->{
                                            transactions = transactionsOriginal.filter { item -> ((item.cardType == paymentMethodFilter)and (item.status == it)) }
                                        }
                                        "MASTERCARD"->{
                                            transactions = transactionsOriginal.filter { item -> ((item.cardType == "MC")and (item.status == it)) }
                                        }
                                        "WALLET"->{
                                            transactions = transactionsOriginal.filter { item -> ((item.cardType == paymentMethodFilter)and (item.status == it)) }
                                        }
                                    }
                                }else if (operatorFilter != null){
                                    transactions = transactionsOriginal.filter { item -> ((item.operatorId == operatorFilter)and (item.status == it)) }
                                }else if ((operatorFilter != null)and(paymentMethodFilter != null)){
                                    when(paymentMethodFilter){
                                        "VISA"->{
                                            transactions = transactionsOriginal.filter { item -> ((item.operatorId == operatorFilter)and (item.status == it)and(item.cardType == paymentMethodFilter)) }
                                        }
                                        "MASTERCARD"->{
                                            transactions = transactionsOriginal.filter { item -> ((item.operatorId == operatorFilter)and (item.status == it)and(item.cardType == "MC")) }
                                        }
                                        "WALLET"->{
                                            transactions = transactionsOriginal.filter { item -> ((item.operatorId == operatorFilter)and (item.status == it)and(item.cardType == paymentMethodFilter)) }
                                        }
                                    }
                                }else{
                                    transactions = transactionsOriginal.filter { item -> item.status == it }
                                }
                                adapter.setTransactions(transactions)
                                adapter.notifyDataSetChanged()
                                //transactionsOriginal = transactionsOriginal.filter { item-> item.status == it }
                            }else{
                                if (paymentMethodFilter != null){
                                    when(paymentMethodFilter){
                                        "VISA"->{
                                            transactions = transactionsOriginal.filter { item -> ((item.cardType == paymentMethodFilter)) }
                                        }
                                        "MASTERCARD"->{
                                            transactions = transactionsOriginal.filter { item -> ((item.cardType == "MC")) }
                                        }
                                        "WALLET"->{
                                            transactions = transactionsOriginal.filter { item -> ((item.cardType == paymentMethodFilter)) }
                                        }
                                    }
                                }else if (operatorFilter != null){
                                    transactions = transactionsOriginal.filter { item -> ((item.operatorId == operatorFilter)) }
                                }else if ((operatorFilter != null)and(paymentMethodFilter != null)){
                                    when(paymentMethodFilter){
                                        "VISA"->{
                                            transactions = transactionsOriginal.filter { item -> ((item.operatorId == operatorFilter)and(item.cardType == paymentMethodFilter)) }
                                        }
                                        "MASTERCARD"->{
                                            transactions = transactionsOriginal.filter { item -> ((item.operatorId == operatorFilter)and(item.cardType == "MC")) }
                                        }
                                        "WALLET"->{
                                            transactions = transactionsOriginal.filter { item -> ((item.operatorId == operatorFilter)and(item.cardType == paymentMethodFilter)) }
                                        }
                                    }
                                }else{
                                    transactions = transactionsOriginal
                                }
                                adapter.setTransactions(transactions)
                                adapter.notifyDataSetChanged()
                                //transactionsOriginal = transactionsOriginal.filter { item-> item.status == it }
                            }
                        }

                        paymentMethodFilter.let {
                            if (it != null){
                                when(it){
                                    "VISA"->{
                                        when {
                                            resultFilter != null -> {
                                                transactions = transactionsOriginal.filter { item -> ((item.cardType == it)and (item.status == resultFilter)) }
                                            }
                                            operatorFilter != null -> {
                                                transactions = transactionsOriginal.filter { item -> ((item.cardType == it)and (item.operatorId == operatorFilter)) }
                                            }
                                            (operatorFilter != null)and(resultFilter != null) -> {
                                                transactions = transactionsOriginal.filter { item -> ((item.cardType == it)and (item.status == resultFilter)and(item.operatorId == operatorFilter)) }
                                            }
                                            else -> {
                                                transactions = transactionsOriginal.filter { item -> ((item.cardType == it))}
                                            }
                                        }
                                    }
                                    "MASTERCARD"->{
                                        when {
                                            resultFilter != null -> {
                                                transactions = transactionsOriginal.filter { item -> ((item.cardType == "MC")and (item.status == resultFilter)) }
                                            }
                                            operatorFilter != null -> {
                                                transactions = transactionsOriginal.filter { item -> ((item.cardType == "MC")and (item.operatorId == operatorFilter)) }
                                            }
                                            (operatorFilter != null)and(resultFilter != null) -> {
                                                transactions = transactionsOriginal.filter { item -> ((item.cardType == "MC")and (item.status == resultFilter)and(item.operatorId == operatorFilter)) }
                                            }
                                            else -> {
                                                transactions = transactionsOriginal.filter { item -> ((item.cardType == "MC"))}
                                            }
                                        }
                                    }
                                    "WALLET"->{
                                        when {
                                            resultFilter != null -> {
                                                transactions = transactionsOriginal.filter { item -> ((item.cardType == it)and (item.status == resultFilter)) }
                                            }
                                            operatorFilter != null -> {
                                                transactions = transactionsOriginal.filter { item -> ((item.cardType == it)and (item.operatorId == operatorFilter)) }
                                            }
                                            (operatorFilter != null)and(resultFilter != null) -> {
                                                transactions = transactionsOriginal.filter { item -> ((item.cardType == it)and (item.status == resultFilter)and(item.operatorId == operatorFilter)) }
                                            }
                                            else -> {
                                                transactions = transactionsOriginal.filter { item -> ((item.cardType == it))}
                                            }
                                        }
                                    }
                                }
                                adapter.setTransactions(transactions)
                                adapter.notifyDataSetChanged()
                            }else{
                                when {
                                    resultFilter != null -> {
                                        transactions = transactionsOriginal.filter { item -> ((item.status == resultFilter)) }
                                    }
                                    operatorFilter != null -> {
                                        transactions = transactionsOriginal.filter { item -> ((item.operatorId == operatorFilter)) }
                                    }
                                    (operatorFilter != null)and(resultFilter != null) -> {
                                        transactions = transactionsOriginal.filter { item -> ((item.status == resultFilter)and(item.operatorId == operatorFilter)) }
                                    }
                                    else -> {
                                        transactions = transactionsOriginal
                                    }
                                }
                                adapter.setTransactions(transactions)
                                adapter.notifyDataSetChanged()
                            }
                            //transactionsOriginal = transactionsOriginal.filter { item -> item.cardType == it }
                        }

                        operatorFilter.let {
                            if (it != null){
                                when {
                                    resultFilter != null -> {
                                        transactions = transactionsOriginal.filter { item -> ((item.operatorId == it)and (item.status == resultFilter)) }
                                    }
                                    paymentMethodFilter != null -> {
                                        when(paymentMethodFilter){
                                            "VISA"->{
                                                transactions = transactionsOriginal.filter { item -> ((item.operatorId == it)and(item.cardType == paymentMethodFilter)) }
                                            }
                                            "MASTERCARD"->{
                                                transactions = transactionsOriginal.filter { item -> ((item.operatorId == it)and(item.cardType == "MC")) }
                                            }
                                            "WALLET"->{
                                                transactions = transactionsOriginal.filter { item -> ((item.operatorId == it)and(item.cardType == paymentMethodFilter)) }
                                            }
                                        }
                                    }
                                    (paymentMethodFilter != null)and(resultFilter != null) -> {
                                        when(paymentMethodFilter){
                                            "VISA"->{
                                                transactions = transactionsOriginal.filter { item -> ((item.operatorId == it)and(item.cardType == paymentMethodFilter)and(item.status == resultFilter)) }
                                            }
                                            "MASTERCARD"->{
                                                transactions = transactionsOriginal.filter { item -> ((item.operatorId == it)and(item.cardType == "MC")and(item.status == resultFilter)) }
                                            }
                                            "WALLET"->{
                                                transactions = transactionsOriginal.filter { item -> ((item.operatorId == it)and(item.cardType == paymentMethodFilter)and(item.status == resultFilter)) }
                                            }
                                        }
                                    }
                                    else -> {
                                        transactions = transactionsOriginal.filter { item -> ((item.operatorId == it))}
                                    }
                                }
                                adapter.setTransactions(transactions)
                                adapter.notifyDataSetChanged()
                                //transactionsOriginal = transactionsOriginal.filter { item -> item.operatorId == it }
                            }else{
                                when {
                                    resultFilter != null -> {
                                        transactions = transactionsOriginal.filter { item -> ((item.status == resultFilter)) }
                                    }
                                    paymentMethodFilter != null -> {
                                        when(paymentMethodFilter){
                                            "VISA"->{
                                                transactions = transactionsOriginal.filter { item -> ((item.cardType == paymentMethodFilter)) }
                                            }
                                            "MASTERCARD"->{
                                                transactions = transactionsOriginal.filter { item -> ((item.cardType == "MC")) }
                                            }
                                            "WALLET"->{
                                                transactions = transactionsOriginal.filter { item -> ((item.cardType == paymentMethodFilter)) }
                                            }
                                        }
                                    }
                                    (paymentMethodFilter != null)and(resultFilter != null) -> {
                                        when(paymentMethodFilter){
                                            "VISA"->{
                                                transactions = transactionsOriginal.filter { item -> ((item.cardType == paymentMethodFilter)and(item.status == resultFilter)) }
                                            }
                                            "MASTERCARD"->{
                                                transactions = transactionsOriginal.filter { item -> ((item.cardType == "MC")and(item.status == resultFilter)) }
                                            }
                                            "WALLET"->{
                                                transactions = transactionsOriginal.filter { item -> ((item.cardType == paymentMethodFilter)and(item.status == resultFilter)) }
                                            }
                                        }
                                    }
                                    else -> {
                                        transactions = transactionsOriginal
                                    }
                                }
                                adapter.setTransactions(transactions)
                                adapter.notifyDataSetChanged()
                                //transactionsOriginal = transactionsOriginal.filter { item -> item.operatorId == it }
                            }
                        }
                    } else {
                        resultFilter.let {
                            if (it!=null){
                                if (paymentMethodFilter != null){
                                    when(paymentMethodFilter){
                                        "VISA"->{
                                            transactions = transactionsOriginal.filter { item ->
                                                Timber.e("AMOUNT ${item.amount}")
                                                (item.opCode.toLowerCase(Locale.getDefault()).contains(text.toLowerCase(Locale.getDefault()))) or
                                                        (item.amount.toString().toLowerCase(Locale.getDefault()).contains(text.toLowerCase(Locale.getDefault())))
                                            }.filter { item->
                                                ((item.cardType == paymentMethodFilter)and (item.status == it))
                                            }
                                        }
                                        "MASTERCARD"->{
                                            transactions = transactionsOriginal.filter { item ->
                                                Timber.e("AMOUNT ${item.amount}")
                                                (item.opCode.toLowerCase(Locale.getDefault()).contains(text.toLowerCase(Locale.getDefault()))) or
                                                        (item.amount.toString().toLowerCase(Locale.getDefault()).contains(text.toLowerCase(Locale.getDefault())))
                                            }.filter { item->
                                                ((item.cardType == "MC")and (item.status == it))
                                            }
                                        }
                                        "WALLET"->{
                                            transactions = transactionsOriginal.filter { item ->
                                                Timber.e("AMOUNT ${item.amount}")
                                                (item.opCode.toLowerCase(Locale.getDefault()).contains(text.toLowerCase(Locale.getDefault()))) or
                                                        (item.amount.toString().toLowerCase(Locale.getDefault()).contains(text.toLowerCase(Locale.getDefault())))
                                            }.filter { item->
                                                ((item.cardType == paymentMethodFilter)and (item.status == it))
                                            }
                                        }
                                    }
                                }else if (operatorFilter != null){
                                    transactions = transactionsOriginal.filter { item ->
                                        Timber.e("AMOUNT ${item.amount}")
                                        (item.opCode.toLowerCase(Locale.getDefault()).contains(text.toLowerCase(Locale.getDefault()))) or
                                                (item.amount.toString().toLowerCase(Locale.getDefault()).contains(text.toLowerCase(Locale.getDefault())))
                                    }.filter { item->
                                        ((item.operatorId == operatorFilter)and (item.status == it))
                                    }
                                }else if ((operatorFilter != null)and (paymentMethodFilter!=null)){
                                    when(paymentMethodFilter){
                                        "VISA"->{
                                            transactions = transactionsOriginal.filter { item ->
                                                Timber.e("AMOUNT ${item.amount}")
                                                (item.opCode.toLowerCase(Locale.getDefault()).contains(text.toLowerCase(Locale.getDefault()))) or
                                                        (item.amount.toString().toLowerCase(Locale.getDefault()).contains(text.toLowerCase(Locale.getDefault())))
                                            }.filter { item->
                                                ((item.cardType == paymentMethodFilter)and (item.status == it)and(item.operatorId == operatorFilter))
                                            }
                                        }
                                        "MASTERCARD"->{
                                            transactions = transactionsOriginal.filter { item ->
                                                Timber.e("AMOUNT ${item.amount}")
                                                (item.opCode.toLowerCase(Locale.getDefault()).contains(text.toLowerCase(Locale.getDefault()))) or
                                                        (item.amount.toString().toLowerCase(Locale.getDefault()).contains(text.toLowerCase(Locale.getDefault())))
                                            }.filter { item->
                                                ((item.cardType == "MC")and (item.status == it)and(item.operatorId == operatorFilter))
                                            }
                                        }
                                        "WALLET"->{
                                            transactions = transactionsOriginal.filter { item ->
                                                Timber.e("AMOUNT ${item.amount}")
                                                (item.opCode.toLowerCase(Locale.getDefault()).contains(text.toLowerCase(Locale.getDefault()))) or
                                                        (item.amount.toString().toLowerCase(Locale.getDefault()).contains(text.toLowerCase(Locale.getDefault())))
                                            }.filter { item->
                                                ((item.cardType == paymentMethodFilter)and (item.status == it)and(item.operatorId == operatorFilter))
                                            }
                                        }
                                    }
                                }else{
                                    transactions = transactionsOriginal.filter { item ->
                                        Timber.e("AMOUNT ${item.amount}")
                                        (item.opCode.toLowerCase(Locale.getDefault()).contains(text.toLowerCase(Locale.getDefault()))) or
                                                (item.amount.toString().toLowerCase(Locale.getDefault()).contains(text.toLowerCase(Locale.getDefault())))
                                    }.filter { item->
                                        ((item.status == it))
                                    }
                                }
                                adapter.setTransactions(transactions)
                                adapter.notifyDataSetChanged()
                            }else{
                                if (paymentMethodFilter != null){
                                    when(paymentMethodFilter){
                                        "VISA"->{
                                            transactions = transactionsOriginal.filter { item ->
                                                Timber.e("AMOUNT ${item.amount}")
                                                (item.opCode.toLowerCase(Locale.getDefault()).contains(text.toLowerCase(Locale.getDefault()))) or
                                                        (item.amount.toString().toLowerCase(Locale.getDefault()).contains(text.toLowerCase(Locale.getDefault())))
                                            }.filter { item->
                                                ((item.cardType == paymentMethodFilter))
                                            }
                                        }
                                        "MASTERCARD"->{
                                            transactions = transactionsOriginal.filter { item ->
                                                Timber.e("AMOUNT ${item.amount}")
                                                (item.opCode.toLowerCase(Locale.getDefault()).contains(text.toLowerCase(Locale.getDefault()))) or
                                                        (item.amount.toString().toLowerCase(Locale.getDefault()).contains(text.toLowerCase(Locale.getDefault())))
                                            }.filter { item->
                                                ((item.cardType == "MC"))
                                            }
                                        }
                                        "WALLET"->{
                                            transactions = transactionsOriginal.filter { item ->
                                                Timber.e("AMOUNT ${item.amount}")
                                                (item.opCode.toLowerCase(Locale.getDefault()).contains(text.toLowerCase(Locale.getDefault()))) or
                                                        (item.amount.toString().toLowerCase(Locale.getDefault()).contains(text.toLowerCase(Locale.getDefault())))
                                            }.filter { item->
                                                ((item.cardType == paymentMethodFilter))
                                            }
                                        }
                                    }
                                }else if (operatorFilter != null){
                                    transactions = transactionsOriginal.filter { item ->
                                        Timber.e("AMOUNT ${item.amount}")
                                        (item.opCode.toLowerCase(Locale.getDefault()).contains(text.toLowerCase(Locale.getDefault()))) or
                                                (item.amount.toString().toLowerCase(Locale.getDefault()).contains(text.toLowerCase(Locale.getDefault())))
                                    }.filter { item->
                                        ((item.operatorId == operatorFilter))
                                    }
                                }else if ((operatorFilter != null)and (paymentMethodFilter!=null)){
                                    when(paymentMethodFilter){
                                        "VISA"->{
                                            transactions = transactionsOriginal.filter { item ->
                                                Timber.e("AMOUNT ${item.amount}")
                                                (item.opCode.toLowerCase(Locale.getDefault()).contains(text.toLowerCase(Locale.getDefault()))) or
                                                        (item.amount.toString().toLowerCase(Locale.getDefault()).contains(text.toLowerCase(Locale.getDefault())))
                                            }.filter { item->
                                                ((item.cardType == paymentMethodFilter)and(item.operatorId == operatorFilter))
                                            }
                                        }
                                        "MASTERCARD"->{
                                            transactions = transactionsOriginal.filter { item ->
                                                Timber.e("AMOUNT ${item.amount}")
                                                (item.opCode.toLowerCase(Locale.getDefault()).contains(text.toLowerCase(Locale.getDefault()))) or
                                                        (item.amount.toString().toLowerCase(Locale.getDefault()).contains(text.toLowerCase(Locale.getDefault())))
                                            }.filter { item->
                                                ((item.cardType == "MC")and(item.operatorId == operatorFilter))
                                            }
                                        }
                                        "WALLET"->{
                                            transactions = transactionsOriginal.filter { item ->
                                                Timber.e("AMOUNT ${item.amount}")
                                                (item.opCode.toLowerCase(Locale.getDefault()).contains(text.toLowerCase(Locale.getDefault()))) or
                                                        (item.amount.toString().toLowerCase(Locale.getDefault()).contains(text.toLowerCase(Locale.getDefault())))
                                            }.filter { item->
                                                ((item.cardType == paymentMethodFilter)and(item.operatorId == operatorFilter))
                                            }
                                        }
                                    }
                                }else{
                                    transactions = transactionsOriginal.filter { item ->
                                        Timber.e("AMOUNT ${item.amount}")
                                        (item.opCode.toLowerCase(Locale.getDefault()).contains(text.toLowerCase(Locale.getDefault()))) or
                                                (item.amount.toString().toLowerCase(Locale.getDefault()).contains(text.toLowerCase(Locale.getDefault())))
                                    }
                                }
                            }
                            adapter.setTransactions(transactions)
                            adapter.notifyDataSetChanged()
                            //transactionsOriginal = transactionsOriginal.filter { item-> item.status == it }
                        }

                        paymentMethodFilter.let {

                            if (it != null){
                                when(it){
                                    "VISA"->{
                                        if (resultFilter != null){
                                            transactions = transactionsOriginal.filter { item ->
                                                Timber.e("AMOUNT ${item.amount}")
                                                (item.opCode.toLowerCase(Locale.getDefault()).contains(text.toLowerCase(Locale.getDefault()))) or
                                                        (item.amount.toString().toLowerCase(Locale.getDefault()).contains(text.toLowerCase(Locale.getDefault())))
                                            }.filter { item->
                                                ((item.cardType == it)and (item.status == resultFilter))
                                            }
                                        }else if (operatorFilter != null){
                                            transactions = transactionsOriginal.filter { item ->
                                                Timber.e("AMOUNT ${item.amount}")
                                                (item.opCode.toLowerCase(Locale.getDefault()).contains(text.toLowerCase(Locale.getDefault()))) or
                                                        (item.amount.toString().toLowerCase(Locale.getDefault()).contains(text.toLowerCase(Locale.getDefault())))
                                            }.filter { item->
                                                ((item.cardType == it)and (item.operatorId == operatorFilter))
                                            }
                                        }else if((resultFilter != null) and (operatorFilter != null)){
                                            transactions = transactionsOriginal.filter { item ->
                                                Timber.e("AMOUNT ${item.amount}")
                                                (item.opCode.toLowerCase(Locale.getDefault()).contains(text.toLowerCase(Locale.getDefault()))) or
                                                        (item.amount.toString().toLowerCase(Locale.getDefault()).contains(text.toLowerCase(Locale.getDefault())))
                                            }.filter { item->
                                                ((item.cardType == it)and (item.status == resultFilter)and(item.operatorId == operatorFilter))
                                            }
                                        }else{
                                            transactions = transactionsOriginal.filter { item ->
                                                Timber.e("AMOUNT ${item.amount}")
                                                (item.opCode.toLowerCase(Locale.getDefault()).contains(text.toLowerCase(Locale.getDefault()))) or
                                                        (item.amount.toString().toLowerCase(Locale.getDefault()).contains(text.toLowerCase(Locale.getDefault())))
                                            }.filter { item->
                                                ((item.cardType == it))
                                            }
                                        }
                                    }
                                    "MASTERCARD"->{
                                        if (resultFilter != null){
                                            transactions = transactionsOriginal.filter { item ->
                                                Timber.e("AMOUNT ${item.amount}")
                                                (item.opCode.toLowerCase(Locale.getDefault()).contains(text.toLowerCase(Locale.getDefault()))) or
                                                        (item.amount.toString().toLowerCase(Locale.getDefault()).contains(text.toLowerCase(Locale.getDefault())))
                                            }.filter { item->
                                                ((item.cardType == "MC")and (item.status == resultFilter))
                                            }
                                        }else if (operatorFilter != null){
                                            transactions = transactionsOriginal.filter { item ->
                                                Timber.e("AMOUNT ${item.amount}")
                                                (item.opCode.toLowerCase(Locale.getDefault()).contains(text.toLowerCase(Locale.getDefault()))) or
                                                        (item.amount.toString().toLowerCase(Locale.getDefault()).contains(text.toLowerCase(Locale.getDefault())))
                                            }.filter { item->
                                                ((item.cardType == "MC")and (item.operatorId == operatorFilter))
                                            }
                                        }else if((resultFilter != null) and (operatorFilter != null)){
                                            transactions = transactionsOriginal.filter { item ->
                                                Timber.e("AMOUNT ${item.amount}")
                                                (item.opCode.toLowerCase(Locale.getDefault()).contains(text.toLowerCase(Locale.getDefault()))) or
                                                        (item.amount.toString().toLowerCase(Locale.getDefault()).contains(text.toLowerCase(Locale.getDefault())))
                                            }.filter { item->
                                                ((item.cardType == "MC")and (item.status == resultFilter)and(item.operatorId == operatorFilter))
                                            }
                                        }else{
                                            transactions = transactionsOriginal.filter { item ->
                                                Timber.e("AMOUNT ${item.amount}")
                                                (item.opCode.toLowerCase(Locale.getDefault()).contains(text.toLowerCase(Locale.getDefault()))) or
                                                        (item.amount.toString().toLowerCase(Locale.getDefault()).contains(text.toLowerCase(Locale.getDefault())))
                                            }.filter { item->
                                                ((item.cardType == "MC"))
                                            }
                                        }
                                    }
                                    "WALLET"->{
                                        if (resultFilter != null){
                                            transactions = transactionsOriginal.filter { item ->
                                                Timber.e("AMOUNT ${item.amount}")
                                                (item.opCode.toLowerCase(Locale.getDefault()).contains(text.toLowerCase(Locale.getDefault()))) or
                                                        (item.amount.toString().toLowerCase(Locale.getDefault()).contains(text.toLowerCase(Locale.getDefault())))
                                            }.filter { item->
                                                ((item.cardType == it)and (item.status == resultFilter))
                                            }
                                        }else if (operatorFilter != null){
                                            transactions = transactionsOriginal.filter { item ->
                                                Timber.e("AMOUNT ${item.amount}")
                                                (item.opCode.toLowerCase(Locale.getDefault()).contains(text.toLowerCase(Locale.getDefault()))) or
                                                        (item.amount.toString().toLowerCase(Locale.getDefault()).contains(text.toLowerCase(Locale.getDefault())))
                                            }.filter { item->
                                                ((item.cardType == it)and (item.operatorId == operatorFilter))
                                            }
                                        }else if((resultFilter != null) and (operatorFilter != null)){
                                            transactions = transactionsOriginal.filter { item ->
                                                Timber.e("AMOUNT ${item.amount}")
                                                (item.opCode.toLowerCase(Locale.getDefault()).contains(text.toLowerCase(Locale.getDefault()))) or
                                                        (item.amount.toString().toLowerCase(Locale.getDefault()).contains(text.toLowerCase(Locale.getDefault())))
                                            }.filter { item->
                                                ((item.cardType == it)and (item.status == resultFilter)and(item.operatorId == operatorFilter))
                                            }
                                        }else{
                                            transactions = transactionsOriginal.filter { item ->
                                                Timber.e("AMOUNT ${item.amount}")
                                                (item.opCode.toLowerCase(Locale.getDefault()).contains(text.toLowerCase(Locale.getDefault()))) or
                                                        (item.amount.toString().toLowerCase(Locale.getDefault()).contains(text.toLowerCase(Locale.getDefault())))
                                            }.filter { item->
                                                ((item.cardType == it))
                                            }
                                        }
                                    }
                                }
                                adapter.setTransactions(transactions)
                                adapter.notifyDataSetChanged()
                            }else{
                                if (resultFilter != null){
                                    transactions = transactionsOriginal.filter { item ->
                                        Timber.e("AMOUNT ${item.amount}")
                                        (item.opCode.toLowerCase(Locale.getDefault()).contains(text.toLowerCase(Locale.getDefault()))) or
                                                (item.amount.toString().toLowerCase(Locale.getDefault()).contains(text.toLowerCase(Locale.getDefault())))
                                    }.filter { item->
                                        ((item.status == resultFilter))
                                    }
                                }else if (operatorFilter != null){
                                    transactions = transactionsOriginal.filter { item ->
                                        Timber.e("AMOUNT ${item.amount}")
                                        (item.opCode.toLowerCase(Locale.getDefault()).contains(text.toLowerCase(Locale.getDefault()))) or
                                                (item.amount.toString().toLowerCase(Locale.getDefault()).contains(text.toLowerCase(Locale.getDefault())))
                                    }.filter { item->
                                        ((item.operatorId == operatorFilter))
                                    }
                                }else if((resultFilter != null) and (operatorFilter != null)){
                                    transactions = transactionsOriginal.filter { item ->
                                        Timber.e("AMOUNT ${item.amount}")
                                        (item.opCode.toLowerCase(Locale.getDefault()).contains(text.toLowerCase(Locale.getDefault()))) or
                                                (item.amount.toString().toLowerCase(Locale.getDefault()).contains(text.toLowerCase(Locale.getDefault())))
                                    }.filter { item->
                                        ((item.status == resultFilter)and(item.operatorId == operatorFilter))
                                    }
                                }else{
                                    transactions = transactionsOriginal.filter { item ->
                                        Timber.e("AMOUNT ${item.amount}")
                                        (item.opCode.toLowerCase(Locale.getDefault()).contains(text.toLowerCase(Locale.getDefault()))) or
                                                (item.amount.toString().toLowerCase(Locale.getDefault()).contains(text.toLowerCase(Locale.getDefault())))
                                    }
                                }
                                adapter.setTransactions(transactions)
                                adapter.notifyDataSetChanged()
                            }


                            /*transactions = transactionsOriginal.filter { item ->
                                (item.opCode.toLowerCase(Locale.getDefault())
                                    .contains(text.toLowerCase(Locale.getDefault()))) or
                                        (item.amount.toString().toLowerCase(Locale.getDefault())
                                            .contains(text.toLowerCase(Locale.getDefault())))
                            }.filter { item ->
                                item.cardType == it
                            }*/
                            //transactionsOriginal = transactionsOriginal.filter { item -> item.cardType == it }
                        }

                        operatorFilter.let {
                            if (it != null){
                                if (resultFilter != null){
                                    transactions = transactionsOriginal.filter { item ->
                                        Timber.e("AMOUNT ${item.amount}")
                                        (item.opCode.toLowerCase(Locale.getDefault()).contains(text.toLowerCase(Locale.getDefault()))) or
                                                (item.amount.toString().toLowerCase(Locale.getDefault()).contains(text.toLowerCase(Locale.getDefault())))
                                    }.filter { item->
                                        ((item.status == resultFilter)and(item.operatorId == it))
                                    }
                                }else if(paymentMethodFilter != null){
                                    when(paymentMethodFilter){
                                        "VISA"->{
                                            transactions = transactionsOriginal.filter { item ->
                                                Timber.e("AMOUNT ${item.amount}")
                                                (item.opCode.toLowerCase(Locale.getDefault()).contains(text.toLowerCase(Locale.getDefault()))) or
                                                        (item.amount.toString().toLowerCase(Locale.getDefault()).contains(text.toLowerCase(Locale.getDefault())))
                                            }.filter { item->
                                                ((item.cardType == paymentMethodFilter)and(item.operatorId == operatorFilter))
                                            }
                                        }
                                        "MASTERCARD"->{
                                            transactions = transactionsOriginal.filter { item ->
                                                Timber.e("AMOUNT ${item.amount}")
                                                (item.opCode.toLowerCase(Locale.getDefault()).contains(text.toLowerCase(Locale.getDefault()))) or
                                                        (item.amount.toString().toLowerCase(Locale.getDefault()).contains(text.toLowerCase(Locale.getDefault())))
                                            }.filter { item->
                                                ((item.cardType == "MC")and(item.operatorId == it))
                                            }
                                        }
                                        "WALLET"->{
                                            transactions = transactionsOriginal.filter { item ->
                                                Timber.e("AMOUNT ${item.amount}")
                                                (item.opCode.toLowerCase(Locale.getDefault()).contains(text.toLowerCase(Locale.getDefault()))) or
                                                        (item.amount.toString().toLowerCase(Locale.getDefault()).contains(text.toLowerCase(Locale.getDefault())))
                                            }.filter { item->
                                                ((item.cardType == paymentMethodFilter)and(item.operatorId == it))
                                            }
                                        }
                                    }
                                }else if ((paymentMethodFilter != null) and (resultFilter != null)){
                                    when(paymentMethodFilter){
                                        "VISA"->{
                                            transactions = transactionsOriginal.filter { item ->
                                                Timber.e("AMOUNT ${item.amount}")
                                                (item.opCode.toLowerCase(Locale.getDefault()).contains(text.toLowerCase(Locale.getDefault()))) or
                                                        (item.amount.toString().toLowerCase(Locale.getDefault()).contains(text.toLowerCase(Locale.getDefault())))
                                            }.filter { item->
                                                ((item.cardType == paymentMethodFilter)and(item.operatorId == operatorFilter)and(item.status == resultFilter))
                                            }
                                        }
                                        "MASTERCARD"->{
                                            transactions = transactionsOriginal.filter { item ->
                                                Timber.e("AMOUNT ${item.amount}")
                                                (item.opCode.toLowerCase(Locale.getDefault()).contains(text.toLowerCase(Locale.getDefault()))) or
                                                        (item.amount.toString().toLowerCase(Locale.getDefault()).contains(text.toLowerCase(Locale.getDefault())))
                                            }.filter { item->
                                                ((item.cardType == "MC")and(item.operatorId == it)and(item.status == resultFilter))
                                            }
                                        }
                                        "WALLET"->{
                                            transactions = transactionsOriginal.filter { item ->
                                                Timber.e("AMOUNT ${item.amount}")
                                                (item.opCode.toLowerCase(Locale.getDefault()).contains(text.toLowerCase(Locale.getDefault()))) or
                                                        (item.amount.toString().toLowerCase(Locale.getDefault()).contains(text.toLowerCase(Locale.getDefault())))
                                            }.filter { item->
                                                ((item.cardType == paymentMethodFilter)and(item.operatorId == it)and(item.status == resultFilter))
                                            }
                                        }
                                    }
                                }else{
                                    transactions = transactionsOriginal.filter { item ->
                                        Timber.e("AMOUNT ${item.amount}")
                                        (item.opCode.toLowerCase(Locale.getDefault()).contains(text.toLowerCase(Locale.getDefault()))) or
                                                (item.amount.toString().toLowerCase(Locale.getDefault()).contains(text.toLowerCase(Locale.getDefault())))
                                    }.filter { item->
                                        ((item.operatorId == it))
                                    }
                                }
                                adapter.setTransactions(transactions)
                                adapter.notifyDataSetChanged()
                                //transactionsOriginal = transactionsOriginal.filter { item -> item.operatorId == it }
                            }else{
                                if (resultFilter != null){
                                    transactions = transactionsOriginal.filter { item ->
                                        Timber.e("AMOUNT ${item.amount}")
                                        (item.opCode.toLowerCase(Locale.getDefault()).contains(text.toLowerCase(Locale.getDefault()))) or
                                                (item.amount.toString().toLowerCase(Locale.getDefault()).contains(text.toLowerCase(Locale.getDefault())))
                                    }.filter { item->
                                        ((item.status == resultFilter))
                                    }
                                }else if(paymentMethodFilter != null){
                                    when(paymentMethodFilter){
                                        "VISA"->{
                                            transactions = transactionsOriginal.filter { item ->
                                                Timber.e("AMOUNT ${item.amount}")
                                                (item.opCode.toLowerCase(Locale.getDefault()).contains(text.toLowerCase(Locale.getDefault()))) or
                                                        (item.amount.toString().toLowerCase(Locale.getDefault()).contains(text.toLowerCase(Locale.getDefault())))
                                            }.filter { item->
                                                ((item.cardType == paymentMethodFilter))
                                            }
                                        }
                                        "MASTERCARD"->{
                                            transactions = transactionsOriginal.filter { item ->
                                                Timber.e("AMOUNT ${item.amount}")
                                                (item.opCode.toLowerCase(Locale.getDefault()).contains(text.toLowerCase(Locale.getDefault()))) or
                                                        (item.amount.toString().toLowerCase(Locale.getDefault()).contains(text.toLowerCase(Locale.getDefault())))
                                            }.filter { item->
                                                ((item.cardType == "MC"))
                                            }
                                        }
                                        "WALLET"->{
                                            transactions = transactionsOriginal.filter { item ->
                                                Timber.e("AMOUNT ${item.amount}")
                                                (item.opCode.toLowerCase(Locale.getDefault()).contains(text.toLowerCase(Locale.getDefault()))) or
                                                        (item.amount.toString().toLowerCase(Locale.getDefault()).contains(text.toLowerCase(Locale.getDefault())))
                                            }.filter { item->
                                                ((item.cardType == paymentMethodFilter))
                                            }
                                        }
                                    }
                                }else if ((paymentMethodFilter != null) and (resultFilter != null)){
                                    when(paymentMethodFilter){
                                        "VISA"->{
                                            transactions = transactionsOriginal.filter { item ->
                                                Timber.e("AMOUNT ${item.amount}")
                                                (item.opCode.toLowerCase(Locale.getDefault()).contains(text.toLowerCase(Locale.getDefault()))) or
                                                        (item.amount.toString().toLowerCase(Locale.getDefault()).contains(text.toLowerCase(Locale.getDefault())))
                                            }.filter { item->
                                                ((item.cardType == paymentMethodFilter)and(item.status == resultFilter))
                                            }
                                        }
                                        "MASTERCARD"->{
                                            transactions = transactionsOriginal.filter { item ->
                                                Timber.e("AMOUNT ${item.amount}")
                                                (item.opCode.toLowerCase(Locale.getDefault()).contains(text.toLowerCase(Locale.getDefault()))) or
                                                        (item.amount.toString().toLowerCase(Locale.getDefault()).contains(text.toLowerCase(Locale.getDefault())))
                                            }.filter { item->
                                                ((item.cardType == "MC")and(item.status == resultFilter))
                                            }
                                        }
                                        "WALLET"->{
                                            transactions = transactionsOriginal.filter { item ->
                                                Timber.e("AMOUNT ${item.amount}")
                                                (item.opCode.toLowerCase(Locale.getDefault()).contains(text.toLowerCase(Locale.getDefault()))) or
                                                        (item.amount.toString().toLowerCase(Locale.getDefault()).contains(text.toLowerCase(Locale.getDefault())))
                                            }.filter { item->
                                                ((item.cardType == paymentMethodFilter)and(item.status == resultFilter))
                                            }
                                        }
                                    }
                                }else{
                                    transactions = transactionsOriginal.filter { item ->
                                        Timber.e("AMOUNT ${item.amount}")
                                        (item.opCode.toLowerCase(Locale.getDefault()).contains(text.toLowerCase(Locale.getDefault()))) or
                                                (item.amount.toString().toLowerCase(Locale.getDefault()).contains(text.toLowerCase(Locale.getDefault())))
                                    }
                                }
                                adapter.setTransactions(transactions)
                                adapter.notifyDataSetChanged()
                            }
                        }
                    }
                } else {
                    Timber.e("FILTER FALSE")
                    if (text.isEmpty()) {
                        Timber.e("TEXT EMPTY")
                        adapter.setTransactions(transactionsOriginal)
                        adapter.notifyDataSetChanged()
                    } else {
                        transactions = transactionsOriginal.filter { item ->
                            Timber.e("AMOUNT ${item.amount}")
                            (item.opCode.toLowerCase(Locale.getDefault())
                                .contains(text.toLowerCase(Locale.getDefault()))) or
                                    (item.amount.toString().toLowerCase(Locale.getDefault())
                                        .contains(text.toLowerCase(Locale.getDefault())))
                        }
                        adapter.setTransactions(transactions)
                        adapter.notifyDataSetChanged()
                    }
                }
            }
        }

        viewModel.liveDataException.observe(viewLifecycleOwner) { proccessException(it) }
    }

    private fun initRecyclerView() {
        adapter = ListTransactionsAdapter(R.layout.row_transaction)
        binding.rvTransactions.layoutManager = LinearLayoutManager(context)
        binding.rvTransactions.adapter = adapter
    }

    private fun applyFilters() {

        Timber.e("TRANSACTION STATUS ${transactionsOriginal[0].status}")

        if ((operatorFilter != null) or (resultFilter != null) or (paymentMethodFilter != null)){
            if (!filtersApplied) adapter.listTransactionsTmp = transactions

            resultFilter?.let {
                if (paymentMethodFilter != null){
                    when(paymentMethodFilter){
                        "VISA"->{
                            transactions = transactionsOriginal.filter { item -> ((item.cardType == paymentMethodFilter)and (item.status == it)) }
                        }
                        "MASTERCARD"->{
                            transactions = transactionsOriginal.filter { item -> ((item.cardType == "MC")and (item.status == it)) }
                        }
                        "WALLET"->{
                            transactions = transactionsOriginal.filter { item -> ((item.cardType == paymentMethodFilter)and (item.status == it)) }
                        }
                    }
                }else if (operatorFilter != null){
                    transactions = transactionsOriginal.filter { item -> ((item.operatorId == operatorFilter)and (item.status == it)) }
                }else if ((operatorFilter != null)and(paymentMethodFilter != null)){
                    when(paymentMethodFilter){
                        "VISA"->{
                            transactions = transactionsOriginal.filter { item -> ((item.operatorId == operatorFilter)and (item.status == it)and(item.cardType == paymentMethodFilter)) }
                        }
                        "MASTERCARD"->{
                            transactions = transactionsOriginal.filter { item -> ((item.operatorId == operatorFilter)and (item.status == it)and(item.cardType == "MC")) }
                        }
                        "WALLET"->{
                            transactions = transactionsOriginal.filter { item -> ((item.operatorId == operatorFilter)and (item.status == it)and(item.cardType == paymentMethodFilter)) }
                        }
                    }
                }else{
                    transactions = transactionsOriginal.filter { item -> item.status == it }
                }
                //transactionsOriginal = transactionsOriginal.filter { item-> item.status == it }
            }

            paymentMethodFilter?.let {
                when(it){
                    "VISA"->{
                        when {
                            resultFilter != null -> {
                                transactions = transactionsOriginal.filter { item -> ((item.cardType == it)and (item.status == resultFilter)) }
                            }
                            operatorFilter != null -> {
                                transactions = transactionsOriginal.filter { item -> ((item.cardType == it)and (item.operatorId == operatorFilter)) }
                            }
                            (operatorFilter != null)and(resultFilter != null) -> {
                                transactions = transactionsOriginal.filter { item -> ((item.cardType == it)and (item.status == resultFilter)and(item.operatorId == operatorFilter)) }
                            }
                            else -> {
                                transactions = transactionsOriginal.filter { item -> ((item.cardType == it))}
                            }
                        }
                    }
                    "MASTERCARD"->{
                        when {
                            resultFilter != null -> {
                                transactions = transactionsOriginal.filter { item -> ((item.cardType == "MC")and (item.status == resultFilter)) }
                            }
                            operatorFilter != null -> {
                                transactions = transactionsOriginal.filter { item -> ((item.cardType == "MC")and (item.operatorId == operatorFilter)) }
                            }
                            (operatorFilter != null)and(resultFilter != null) -> {
                                transactions = transactionsOriginal.filter { item -> ((item.cardType == "MC")and (item.status == resultFilter)and(item.operatorId == operatorFilter)) }
                            }
                            else -> {
                                transactions = transactionsOriginal.filter { item -> ((item.cardType == "MC"))}
                            }
                        }
                    }
                    "WALLET"->{
                        when {
                            resultFilter != null -> {
                                transactions = transactionsOriginal.filter { item -> ((item.cardType == it)and (item.status == resultFilter)) }
                            }
                            operatorFilter != null -> {
                                transactions = transactionsOriginal.filter { item -> ((item.cardType == it)and (item.operatorId == operatorFilter)) }
                            }
                            (operatorFilter != null)and(resultFilter != null) -> {
                                transactions = transactionsOriginal.filter { item -> ((item.cardType == it)and (item.status == resultFilter)and(item.operatorId == operatorFilter)) }
                            }
                            else -> {
                                transactions = transactionsOriginal.filter { item -> ((item.cardType == it))}
                            }
                        }
                    }
                }
                //transactionsOriginal = transactionsOriginal.filter { item -> item.cardType == it }
            }

            operatorFilter?.let {
                when {
                    resultFilter != null -> {
                        transactions = transactionsOriginal.filter { item -> ((item.operatorId == it)and (item.status == resultFilter)) }
                    }
                    paymentMethodFilter != null -> {
                        when(paymentMethodFilter){
                            "VISA"->{
                                transactions = transactionsOriginal.filter { item -> ((item.operatorId == it)and(item.cardType == paymentMethodFilter)) }
                            }
                            "MASTERCARD"->{
                                transactions = transactionsOriginal.filter { item -> ((item.operatorId == it)and(item.cardType == "MC")) }
                            }
                            "WALLET"->{
                                transactions = transactionsOriginal.filter { item -> ((item.operatorId == it)and(item.cardType == paymentMethodFilter)) }
                            }
                        }
                    }
                    (paymentMethodFilter != null)and(resultFilter != null) -> {
                        when(paymentMethodFilter){
                            "VISA"->{
                                transactions = transactionsOriginal.filter { item -> ((item.operatorId == it)and(item.cardType == paymentMethodFilter)and(item.status == resultFilter)) }
                            }
                            "MASTERCARD"->{
                                transactions = transactionsOriginal.filter { item -> ((item.operatorId == it)and(item.cardType == "MC")and(item.status == resultFilter)) }
                            }
                            "WALLET"->{
                                transactions = transactionsOriginal.filter { item -> ((item.operatorId == it)and(item.cardType == paymentMethodFilter)and(item.status == resultFilter)) }
                            }
                        }
                    }
                    else -> {
                        transactions = transactionsOriginal.filter { item -> ((item.operatorId == it))}
                    }
                }
                //transactionsOriginal = transactionsOriginal.filter { item -> item.operatorId == it }
            }
            Timber.e("nada nulo $filtersApplied")

            adapter.setTransactions(transactions)
            filtersApplied = true
        }else{
            Timber.e("TOD NULL $filtersApplied")
            adapter.setTransactions(transactionsOriginal)
            filtersApplied = false
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            val resultOption = data?.getStringExtra("result")
            val paumentMethodOption = data?.getStringExtra("payment_method")
            val operatorOption = data?.getStringExtra("operator")

            Timber.e("FILTER TEST $resultOption $paumentMethodOption $operatorOption")

            resultFilter = resultOption.let {
                Timber.e("IT $it")
                if (it!= null){
                    if (it == ApplicationClass.language.successTransaction) "APPROVED" else "DECLINED"
                }else{
                    null
                }
            }

            paymentMethodFilter = paumentMethodOption.let {
                Timber.e("IT2 $it")
                if (it != null){
                    when (it) {
                        getString(R.string.card_filter_visa) -> "VISA"
                        getString(R.string.card_filter_mastercard) -> "MASTERCARD"
                        else -> "WALLET"
                    }
                }else{
                    null
                }
            }

            Timber.e("OPERATIOR $operatorOption")
            if ((operatorOption != null) and (operatorOption != "null")){
                Timber.e("IT3 $operatorOption")
                operatorFilter = operatorOption?.toInt()
            }else{
                operatorFilter = null
            }

            applyFilters()
        }
    }
}
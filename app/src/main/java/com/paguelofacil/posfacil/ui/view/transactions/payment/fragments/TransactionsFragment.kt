package com.paguelofacil.posfacil.ui.view.transactions.payment.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.paguelofacil.posfacil.R
import com.paguelofacil.posfacil.base.BaseFragment
import com.paguelofacil.posfacil.data.network.api.ApiRequestCode
import com.paguelofacil.posfacil.data.network.response.PosStatusApiResponse
import com.paguelofacil.posfacil.data.network.response.TransactionApiResponse
import com.paguelofacil.posfacil.ui.view.adapters.ListTransactionsAdapter
import com.paguelofacil.posfacil.databinding.FragmentTransactionsBinding
import com.paguelofacil.posfacil.model.Transaction
import com.paguelofacil.posfacil.model.TransactionProvider
import com.paguelofacil.posfacil.ui.view.home.viewmodel.HomeViewModel
import com.paguelofacil.posfacil.ui.view.transactions.payment.activities.MovementsFilterActivity
import com.paguelofacil.posfacil.ui.view.transactions.payment.viewmodel.TransactionsViewModel
import com.paguelofacil.posfacil.util.KeyboardUtil


class TransactionsFragment : BaseFragment() {

    lateinit var binding:FragmentTransactionsBinding

    private lateinit var vm: TransactionsViewModel

    private lateinit var adapter: ListTransactionsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        vm = ViewModelProvider(requireActivity())[TransactionsViewModel::class.java]

        setBaseViewModel(vm)

        vm.getResponseObserver()
            .observe(this@TransactionsFragment, this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentTransactionsBinding.inflate(inflater,container,false)

        initRecyclerView()

        loadListeners()

        KeyboardUtil.hideKeyboard(context,binding.etSearch)

        vm.getAllTransactions()

        return binding.root
    }

    private fun loadListeners() {

        binding.ivFilter.setOnClickListener{
            val intent= Intent(context, MovementsFilterActivity::class.java)
            context?.startActivity(intent)
        }

        binding.clSearch.setOnFocusChangeListener { view, b ->
            KeyboardUtil.hideKeyboard(context,binding.etSearch)
        }

    }

    private fun initRecyclerView() {

        adapter = ListTransactionsAdapter(R.layout.row_transaction)
        binding.rvTransactions.layoutManager = LinearLayoutManager(context)
        binding.rvTransactions.adapter = adapter
    }

    override fun onResponseSuccess(requestCode: Int, responseCode: Int, msg: String?, data: Any?) {
        super.onResponseSuccess(requestCode, responseCode, msg, data)

        when (requestCode) {
            ApiRequestCode.SUCCESS -> {
                val type = object : TypeToken<List<TransactionApiResponse>>() {}.type
                val response = Gson().fromJson<List<TransactionApiResponse>>(Gson().toJson(data), type)
                val parsedResponse = response.map { Transaction.fromApiResponse(it) }
                adapter.setTransactions(parsedResponse)
            }
        }
    }
}
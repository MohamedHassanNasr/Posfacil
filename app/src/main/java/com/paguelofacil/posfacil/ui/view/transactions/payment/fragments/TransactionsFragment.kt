package com.paguelofacil.posfacil.ui.view.transactions.payment.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.paguelofacil.posfacil.R
import com.paguelofacil.posfacil.ui.view.adapters.ListTransactionsAdapter
import com.paguelofacil.posfacil.databinding.FragmentTransactionsBinding
import com.paguelofacil.posfacil.model.TransactionProvider
import com.paguelofacil.posfacil.ui.view.transactions.payment.activities.MovementsFilterActivity
import com.paguelofacil.posfacil.util.KeyboardUtil


class TransactionsFragment : Fragment() {


    lateinit var binding:FragmentTransactionsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        binding=FragmentTransactionsBinding.inflate(inflater,container,false)

        initRecyclerView()

        loadListeners()

        KeyboardUtil.hideKeyboard(context,binding.etSearch)

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

        binding.rvTransactions.layoutManager=LinearLayoutManager(context)
        binding.rvTransactions.adapter=ListTransactionsAdapter(TransactionProvider.listaTransactions,R.layout.row_transaction)



    }


}
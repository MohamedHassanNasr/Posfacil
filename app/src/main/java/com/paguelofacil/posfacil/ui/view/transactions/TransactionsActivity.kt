package com.paguelofacil.posfacil.ui.view.transactions

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.paguelofacil.posfacil.R
import com.paguelofacil.posfacil.databinding.ActivityTransactionsBinding
import com.paguelofacil.posfacil.ui.interfaces.IOnBackPressed
import com.paguelofacil.posfacil.ui.view.transactions.payment.fragments.DetailCobroFragment
import com.paguelofacil.posfacil.ui.view.transactions.refund.fragments.DetailReembolsoFragment
import com.paguelofacil.posfacil.util.Constantes.ConstantesView


class TransactionsActivity : AppCompatActivity() {

    lateinit var binding: ActivityTransactionsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityTransactionsBinding.inflate(layoutInflater)

        setContentView(binding.root)

        loadFragments()


    }

    private fun loadFragments() {
        val bundle = intent.extras
        val tipo = bundle?.get(ConstantesView.PARAM_TIPO_TRANSACTION).toString()

        if (tipo == ConstantesView.PARAM_TRANSACTION_COBRO) {
            val fragmentManager = supportFragmentManager
            val fragmentTransaction=fragmentManager.beginTransaction()
            val fragment = DetailCobroFragment()
            fragment.arguments = bundle
            fragmentTransaction.replace(R.id.container_frag_transactions, fragment)
            fragmentTransaction.commit()
        }
        else if (tipo == ConstantesView.PARAM_TRANSACTION_REEMBOLSO){
            val fragmentManager=supportFragmentManager
            val fragmentTransaction=fragmentManager.beginTransaction()
            val fragment = DetailReembolsoFragment()
            fragment.arguments = bundle
            fragmentTransaction.replace(R.id.container_frag_transactions, fragment)
            fragmentTransaction.commit()
        }
    }

}
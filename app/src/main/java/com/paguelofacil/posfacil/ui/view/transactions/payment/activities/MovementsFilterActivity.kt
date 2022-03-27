package com.paguelofacil.posfacil.ui.view.transactions.payment.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.paguelofacil.posfacil.R
import com.paguelofacil.posfacil.databinding.ActivityMovementsFilterBinding

class MovementsFilterActivity : AppCompatActivity() {

    lateinit var binding:ActivityMovementsFilterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding= ActivityMovementsFilterBinding.inflate(layoutInflater)

        loadListeners()

        setContentView(binding.root)




    }

    private fun loadListeners() {

        binding.ivBack.setOnClickListener{

            onBackPressed()

        }

        binding.btnAplicarFiltros.setOnClickListener {

            onBackPressed()

        }
    }
}
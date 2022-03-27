package com.paguelofacil.posfacil.ui.view.transactions.payment.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.paguelofacil.posfacil.R
import com.paguelofacil.posfacil.databinding.ActivityCobroBinding
import com.paguelofacil.posfacil.ui.view.transactions.payment.fragments.VerificarCobroFragment

class CobroActivity : AppCompatActivity() {

    lateinit var binding: ActivityCobroBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding=ActivityCobroBinding.inflate(layoutInflater)

        setContentView(binding.root)


        loadFragment()




    }

    private fun loadFragment() {

        val fragmentManager=supportFragmentManager
        val fragmentTransaction=fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.container_frag_cobro, VerificarCobroFragment())
        fragmentTransaction.commit()


    }
}
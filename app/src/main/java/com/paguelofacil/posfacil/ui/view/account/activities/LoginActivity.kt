package com.paguelofacil.posfacil.ui.view.account.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.paguelofacil.posfacil.R
import com.paguelofacil.posfacil.databinding.ActivityLoginBinding
import com.paguelofacil.posfacil.ui.view.account.fragments.LoginFragment
import com.paguelofacil.posfacil.ui.view.account.fragments.StepOneRecoveryPassFragment
import com.paguelofacil.posfacil.util.Constantes.ConstantesView

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_login)

        binding = ActivityLoginBinding.inflate(layoutInflater)


        setContentView(binding.root)

        loadFragments()



    }

    private fun loadFragments() {

        val bundle=intent.extras
        var destino= bundle?.get(ConstantesView.PARAM_CHANGE_PASS).toString()

        if(destino!=null && destino==ConstantesView.PARAM_PASSWORD)
        {
            val fragmentManager=supportFragmentManager
            val fragmentTransaction=fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.container_login_fragment, StepOneRecoveryPassFragment())
            fragmentTransaction.commit()
        }
        else
        {
            val fragmentManager=supportFragmentManager
            val fragmentTransaction=fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.container_login_fragment, LoginFragment())
            fragmentTransaction.commit()
        }



    }
}
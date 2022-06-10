package com.paguelofacil.posfacil.ui.view.settings.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.paguelofacil.posfacil.R
import com.paguelofacil.posfacil.databinding.ActivityAjustesBinding
import com.paguelofacil.posfacil.ui.view.account.activities.LoginActivity
import com.paguelofacil.posfacil.ui.view.account.fragments.MyInformationFragment
import com.paguelofacil.posfacil.ui.view.account.viewmodel.MyInformationViewModel
import com.paguelofacil.posfacil.util.Constantes.ConstantesView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AjustesActivity : AppCompatActivity() {

    lateinit var binding: ActivityAjustesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ajustes)

        binding = ActivityAjustesBinding.inflate(layoutInflater)

        setContentView(binding.root)

        loadFragment()

    }

    private fun loadFragment() {
        val bundle = intent.extras
        val destino = bundle?.get(ConstantesView.PARAM_FRAGMENT).toString()

        if (destino == ConstantesView.PARAM_PROFILE){
            val fragmentManager = supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.container_frag_ajustes, MyInformationFragment())
            fragmentTransaction.commit()
        } else if (destino == ConstantesView.PARAM_PASSWORD) {//show change password user
            val params = Bundle()
            val intent = Intent(this, LoginActivity::class.java)
            params.putString(ConstantesView.PARAM_CHANGE_PASS, destino)
            intent.putExtras(params)
            startActivity(intent)
            finish()
        }
    }
}
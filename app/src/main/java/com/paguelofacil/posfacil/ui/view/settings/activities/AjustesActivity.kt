package com.paguelofacil.posfacil.ui.view.settings.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.paguelofacil.posfacil.R
import com.paguelofacil.posfacil.databinding.ActivityAjustesBinding
import com.paguelofacil.posfacil.ui.view.account.activities.LoginActivity
import com.paguelofacil.posfacil.ui.view.account.fragments.MyInformationFragment
import com.paguelofacil.posfacil.ui.view.account.fragments.StepOneRecoveryPassFragment
import com.paguelofacil.posfacil.ui.view.home.activities.HomeActivity
import com.paguelofacil.posfacil.util.Constantes

class AjustesActivity : AppCompatActivity() {

    lateinit var binding: ActivityAjustesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ajustes)

        binding= ActivityAjustesBinding.inflate(layoutInflater)

         setContentView(binding.root)

        loadFragment()


    }

    private fun loadFragment() {

        val bundle=intent.extras
        var destino= bundle?.get(Constantes.PARAM_FRAGMENT).toString()


        if (destino==Constantes.PARAM_PROFILE) //show information user
        {
            val fragmentManager=supportFragmentManager
            val fragmentTransaction=fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.container_frag_ajustes, MyInformationFragment())
            fragmentTransaction.commit()
        }
        else if(destino==Constantes.PARAM_PASSWORD){//show change password user

            val params = Bundle()

            val intent= Intent(this, LoginActivity::class.java)

            params.putString(Constantes.PARAM_FRAGMENT, destino)
            intent.putExtras(params)
            startActivity(intent)
            finish()



        }




    }
}
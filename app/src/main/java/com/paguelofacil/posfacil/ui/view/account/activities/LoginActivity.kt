package com.paguelofacil.posfacil.ui.view.account.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.fragment.app.activityViewModels
import com.paguelofacil.posfacil.R
import com.paguelofacil.posfacil.databinding.ActivityLoginBinding
import com.paguelofacil.posfacil.ui.view.account.fragments.LoginFragment
import com.paguelofacil.posfacil.ui.view.account.fragments.StepOneRecoveryPassFragment
import com.paguelofacil.posfacil.ui.view.account.viewmodel.LoginViewModel
import com.paguelofacil.posfacil.util.Constantes.ConstantesView
import dagger.hilt.android.AndroidEntryPoint
import java.nio.charset.Charset

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadFragments()
    }

    private fun loadFragments() {

        val bundle = intent.extras
        val destino = bundle?.get(ConstantesView.PARAM_CHANGE_PASS).toString()

        if (destino == ConstantesView.PARAM_PASSWORD) {
            val fragmentManager = supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            val fr = StepOneRecoveryPassFragment()
            val bn = Bundle()
            bn.putBoolean("pass", true)
            fr.setArguments(bn)
            fragmentTransaction.replace(
                R.id.container_login_fragment,
                fr
            )
            fragmentTransaction.addToBackStack(null).commit()
        } else {
            val fragmentManager = supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.container_login_fragment, LoginFragment())
            fragmentTransaction.commit()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (supportFragmentManager.backStackEntryCount > 0) {
            //supportFragmentManager.popBackStack()
        } else {
            finish()
        }
    }
}
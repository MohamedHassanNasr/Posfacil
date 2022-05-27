package com.paguelofacil.posfacil.ui.view.transactions.payment.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.paguelofacil.posfacil.R
import com.paguelofacil.posfacil.databinding.ActivityCobroBinding
import com.paguelofacil.posfacil.pax.DetectedCardActivity.Companion.EXTRA_RESULT_CARD_NUMBER
import com.paguelofacil.posfacil.ui.view.home.activities.HomeActivity
import com.paguelofacil.posfacil.ui.view.transactions.payment.fragments.FirmaFragment
import com.paguelofacil.posfacil.ui.view.transactions.payment.fragments.VerificarCobroFragment
import com.paguelofacil.posfacil.ui.view.transactions.payment.viewmodel.CobroViewModel
import com.paguelofacil.posfacil.util.Constantes.AppConstants
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class CobroActivity : AppCompatActivity() {

    lateinit var binding: ActivityCobroBinding
    private val viewModel: CobroViewModel by viewModels()
    private var monto: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCobroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getDataExtra()
        loadFragment()
    }

    private fun getDataExtra() {
        val importe = intent.getDoubleExtra(AppConstants.IntentConstants.HomeFragment().IMPORT, 0.0)
        monto = intent.getStringExtra("monto")
        viewModel.importeCobro = importe
    }

    private fun loadFragment() {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        val fg = VerificarCobroFragment()
        val bundle = Bundle()
        Timber.e("MONTO $monto")
        bundle.putString("monto", monto)
        fg.setArguments(bundle)
        fragmentTransaction.replace(R.id.container_frag_cobro, fg)
        fragmentTransaction.commit()
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        } else {
            goHome()
        }
    }

    private fun goHome() {
        val intent = Intent(baseContext, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1000 && resultCode == Activity.RESULT_OK) {
            val cardNumber = data?.getStringExtra(EXTRA_RESULT_CARD_NUMBER)
            viewModel.mutableCardNumberSuccess.value = cardNumber
            goViewFirma()
        }
    }

    private fun goViewFirma() {
        val fr = supportFragmentManager.beginTransaction()
        fr.replace(R.id.container_frag_cobro, FirmaFragment())
        fr.addToBackStack(null).commit()
    }

}
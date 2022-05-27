package com.paguelofacil.posfacil.pax

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.paguelofacil.posfacil.ApplicationClass
import com.paguelofacil.posfacil.R
import com.paguelofacil.posfacil.base.BaseActivity
import com.paguelofacil.posfacil.data.network.api.ApiError
import com.paguelofacil.posfacil.databinding.ActivityDetectedCardBinding
import com.paguelofacil.posfacil.ui.view.custom_view.CancelBottomSheet
import com.paguelofacil.posfacil.ui.view.home.activities.HomeActivity
import com.paguelofacil.posfacil.ui.view.transactions.payment.fragments.ComprobanteCobroFragment.Companion.OPTION_CARD_SELECTED
import com.paguelofacil.posfacil.ui.view.transactions.payment.viewmodel.CobroViewModel
import com.pax.dal.*
import com.pax.dal.entity.EBeepMode
import com.pax.dal.entity.EPedType
import com.pax.dal.entity.EPiccType
import com.pax.dal.entity.EScannerType
import com.pax.dal.exceptions.IccDevException
import com.pax.neptunelite.api.NeptuneLiteUser
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.bottom_sheet_dialog.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.HashMap

@AndroidEntryPoint
class DetectedCardActivity : BaseActivity() {
    private var dialog: CancelBottomSheet? = null
    private val viewModel: CobroViewModel by viewModels()

    private lateinit var binding: ActivityDetectedCardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetectedCardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loadListeners()
        loadLanguage()

    }

    override fun onResume() {
        super.onResume()

        GlobalScope.launch {
            withContext(Dispatchers.IO){
                intent?.run {
                    val optionSelected = extras?.getInt(OPTION_CARD_SELECTED) ?: 0
                    if (optionSelected == 0) {
                        accionDetectCardChip()
                    } else if (optionSelected == 1){
                        accionLeerBanda()
                    }
                }
            }
        }

    }



    private fun accionDetectCardChip() {
        try {
            do {
            } while (ICC.detect(0.toByte()) == false)
            Sys.beep(EBeepMode.FREQUENCE_LEVEL_1, 500)

            val map = HashMap<String, Any?>()
            GlobalScope.launch {
                withContext(Dispatchers.Main) {
                    map["track"] = ""
                    viewModel.saveDataCardFirestore(map)
                    toTransResultPage()
                }
            }

        } catch (e: IccDevException) {
            e.printStackTrace()
        }
    }

    private fun accionLeerBanda() {
        try {
            Mag.open()
            Mag.reset()
            do {
            } while (Mag.isSwiped == false)
            val tracks = Mag.read()
            Sys.beep(EBeepMode.FREQUENCE_LEVEL_1, 500)

            GlobalScope.launch {
                withContext(Dispatchers.Main) {
                    val map = HashMap<String, Any?>()
                    map["track1"] = tracks?.track1
                    map["track2"] = tracks?.track2
                    map["track3"] = tracks?.track3
                    map["track4"] = tracks?.track4
                    viewModel.saveDataCardFirestore(map)
                    toTransResultPage()
                }
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun loadListeners() {
        binding.lnArrowBack.setOnClickListener { onBackPressed() }

        binding.lnCloseBack.setOnClickListener { showBottomSheet() }
    }

    private fun showBottomSheet(mensaje: String, origen: Int) {
        val dialog = baseContext?.let { BottomSheetDialog(it) }
        val view = layoutInflater.inflate(R.layout.bottom_sheet_dialog, null)
        val btnClose = view.findViewById<Button>(R.id.btn_aceptar_dg)
        view.tv_mensaje_dialog.text = mensaje
        btnClose.setOnClickListener {
            dialog?.dismiss()
            if (origen == 1)
                finish()
            else
                goViewFirma()
        }
        dialog?.setCancelable(false)
        dialog?.setContentView(view)
        dialog?.show()
    }

    private fun showBottomSheet() {
        dialog = CancelBottomSheet(
            callBackClose = { dismissBottomSheetCancel() },
            callbackVolver = { dismissBottomSheetCancel() },
            callbackCancelar = {
                dismissBottomSheetCancel()
                goHome()
            }
        )
        dialog?.show(supportFragmentManager, "")
    }

    private fun dismissBottomSheetCancel() {
        dialog?.dismiss()
    }

    private fun goHome() {
        val intent = Intent(baseContext, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun loadLanguage() {
        binding.textViewTitle.text = ApplicationClass.language.verificarCobro
        binding.tvMessageValideCard.text = ApplicationClass.language.esperandoTarjeta
    }

    private fun goViewFirma() {
//        val fr = activity?.supportFragmentManager?.beginTransaction()
//        fr?.replace(R.id.container_frag_cobro, FirmaFragment())
//        fr?.addToBackStack(null)?.commit()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        }
    }

    override fun onExceptionData(requestCode: Int, exception: ApiError, data: Any?) {

    }

    private fun toTransResultPage() {
        val intent = Intent()
        intent.putExtra(EXTRA_RESULT_PAY_IS_SUCCESS, true)
//        intent.putExtra(EXTRA_RESULT_CARD_NUMBER, paxUtil.getDataCard())
        setResult(Activity.RESULT_OK, intent)
        this@DetectedCardActivity.finish()
    }

    companion object {
        const val EXTRA_RESULT_PAY_IS_SUCCESS = "EXTRA_RESULT_PAY_SUCCESS";
        const val EXTRA_RESULT_CARD_NUMBER = "EXTRA_RESULT_CARD_NUMBER";

        const val TXN_TYPE_ICC = 0x101
    }

}
package com.paguelofacil.posfacil.ui.view.transactions.payment.fragments

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import com.paguelofacil.posfacil.ApplicationClass
import com.paguelofacil.posfacil.R
import com.paguelofacil.posfacil.databinding.FragmentCobroQrCodeBinding
import com.paguelofacil.posfacil.model.QrSend
import com.paguelofacil.posfacil.ui.view.home.activities.HomeActivity
import com.paguelofacil.posfacil.ui.view.transactions.payment.viewmodel.CobroViewModel
import com.paguelofacil.posfacil.util.LoadingDialog
import com.paguelofacil.posfacil.util.networkErrorConverter
import kotlinx.coroutines.launch
import timber.log.Timber


class CobroQrCodeFragment : Fragment() {

    lateinit var binding: FragmentCobroQrCodeBinding
    private val viewModel: CobroViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCobroQrCodeBinding.inflate(inflater, container, false)

        binding.titleQr.text = ApplicationClass.language.scaneaQR
        binding.btnVerificar.text = ApplicationClass.language.verificar
        binding.btnOtherTransaction.text = ApplicationClass.language.otherTransaction
        loadListeners()
        Timber.e("BUNDLE ${arguments?.getString("EMAIL")} BUNDLE2 ${arguments?.getString("PHONE")}")

        val details = arguments?.getParcelable<QrSend>("data")

        lifecycleScope.launch {
            Timber.e("DATA QR $details")
            details.let {
                viewModel.getSystemUrlQr(
                    amount = details?.amount ?: "",
                    taxes = details?.taxes ?: "",
                    tip = details?.tip ?: "",
                    onSuccess = {url, code->
                        Timber.e("ONSUCCES")
                        val enconder = QRCodeWriter()
                        val bitMatrix = enconder.encode(url, BarcodeFormat.QR_CODE, 512, 512)
                        val width = bitMatrix.width
                        val height = bitMatrix.height

                        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
                        Timber.e("BITMAP $bitmap")
                        for (x in 0 until width) {
                            for (y in 0 until height) {
                                bitmap.setPixel(x, y, if (bitMatrix.get(x, y)) Color.BLACK else Color.WHITE)
                            }
                        }
                        binding.ivQr.setImageBitmap(bitmap)
                    },
                    onFailure = {
                        Toast.makeText(activity, it, Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
        binding.backButton.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack();
        }
        binding.finishOper.setOnClickListener {
            val intent = Intent(context, HomeActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }
        initObservers()

        return binding.root
    }

    private fun goHome() {
        val fr = activity?.supportFragmentManager
        fr?.popBackStack()
    }

    private fun initObservers() {
        viewModel.liveDataCobro.observe(viewLifecycleOwner, { })
    }

    private fun loadListeners() {
        binding.lnArrowBack.setOnClickListener { goHome() }

        binding.lnCloseBack.setOnClickListener { showBottomSheet() }

        binding.btnOtherTransaction.setOnClickListener {
            val intent = Intent(context, HomeActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }

        binding.btnVerificar.setOnClickListener { showBottomSheetSucces() } //todo si hay error llamar al warningdialog

    }

    private fun showWarningDialog(message: String, onFailure: ()-> Unit){
        val dialog = context?.let { BottomSheetDialog(it) }

        val view = layoutInflater.inflate(R.layout.bottom_sheet_warning, null)
        val title =view.findViewById<TextView>(R.id.titleError)
        val description =view.findViewById<TextView>(R.id.descriptionError)
        val btn = view.findViewById<MaterialButton>(R.id.btnAccept)

        title.text = "!Ha ocurrido un error!"
        description.text = if ((message == "400") or (message == "400") or (message == "400")){
            "Intente nuevamente"
        }else{
            networkErrorConverter(message)
        }

        btn.text = "Intentar nuevamente"
        btn.setOnClickListener {
            dialog?.dismiss()
            onFailure()
        }

        dialog?.setCancelable(true)

        dialog?.setContentView(view)

        dialog?.show()
    }

    private fun showBottomSheet() {

        val dialog = context?.let { BottomSheetDialog(it) }

        val view = layoutInflater.inflate(R.layout.bottom_sheet_cancel, null)

        val btnBack = view.findViewById<Button>(R.id.btn_volver)
        val btnCancel = view.findViewById<Button>(R.id.btn_si_cancelar)
        val ivCancel = view.findViewById<ImageView>(R.id.iv_close_dg)

        btnBack.text = ApplicationClass.language.volver

        btnBack.setOnClickListener {
            dialog?.dismiss()
        }
        ivCancel.setOnClickListener {
            dialog?.dismiss()

        }
        btnCancel.setOnClickListener {
            dialog?.dismiss()
            goHome()
        }

        dialog?.setCancelable(true)

        dialog?.setContentView(view)

        dialog?.show()

    }

    private fun showBottomSheetSucces() {
        val dialog = context?.let { BottomSheetDialog(it) }

        val view = layoutInflater.inflate(R.layout.bottom_sheet_pago_success, null)

        val btnAceptar = view.findViewById<Button>(R.id.btn_acept_pago_qr)
        val ivCancel = view.findViewById<ImageView>(R.id.iv_close_dg)


        ivCancel.setOnClickListener {
            dialog?.dismiss()
        }

        btnAceptar.setOnClickListener {
            dialog?.dismiss()
            val intent = Intent(context, HomeActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }
        dialog?.setCancelable(true)
        dialog?.setContentView(view)
        dialog?.show()

    }

}
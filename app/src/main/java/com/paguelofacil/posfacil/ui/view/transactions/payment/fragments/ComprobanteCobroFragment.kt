package com.paguelofacil.posfacil.ui.view.transactions.payment.fragments

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.paguelofacil.posfacil.ApplicationClass
import com.paguelofacil.posfacil.R
import com.paguelofacil.posfacil.databinding.FragmentComprobanteCobroBinding
import com.paguelofacil.posfacil.model.QrSend
import com.paguelofacil.posfacil.pax.DetectedCardActivity
import com.paguelofacil.posfacil.ui.view.custom_view.CancelBottomSheet
import com.paguelofacil.posfacil.ui.view.home.activities.HomeActivity
import com.paguelofacil.posfacil.util.KeyboardUtil
import timber.log.Timber


class ComprobanteCobroFragment : Fragment() {

    lateinit var binding: FragmentComprobanteCobroBinding
    private var dialog: CancelBottomSheet? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding=FragmentComprobanteCobroBinding.inflate(inflater,container,false)
        val inputMethodManager: InputMethodManager? = activity?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager?
        inputMethodManager?.hideSoftInputFromWindow(view?.windowToken, 0)

        loadListeners()
        loadLanguage()

        return binding.root
    }

    private fun loadLanguage() {
        binding.textViewMetodoPago.text = ApplicationClass.language.metodoPago
        binding.textViewDeseaRecibirComprobante.text = ApplicationClass.language.recibirComprobante
        binding.textViewCorreoElectronico.text = ApplicationClass.language.correoElectronico
        binding.textViewTelefono.text = ApplicationClass.language.phone
        binding.textViewSeleccioneMetodoPago.text = ApplicationClass.language.seleccioneMetodoPago
        binding.textViewTarjeta.text = ApplicationClass.language.tarjeta
        binding.textViewPagueloFacil.text = ApplicationClass.language.paguelofacilApp
    }

    private fun loadListeners() {

        binding.finishOper.setOnClickListener {
            showBottomSheetClose()
            /*val intent = Intent(context, HomeActivity::class.java)
            startActivity(intent)
            requireActivity().finish()*/
        }

        binding.cvMetodoCard.setOnClickListener {
            showDialogDetectedCard()
        }


        binding.lnArrowBack.setOnClickListener {
            goBackFragment()
        }

        binding.lnCloseBack.setOnClickListener {
            showBottomSheet()
        }

        binding.swInputDestinationReceipt.setOnCheckedChangeListener { compoundButton, b ->
            KeyboardUtil.hideKeyboard(activity)
            if (b) {
                binding.lnCustomDestination.visibility = View.VISIBLE
            } else {
                binding.lnCustomDestination.visibility = View.GONE
            }
        }

        binding.cbPhone.setOnCheckedChangeListener { compoundButton, b ->
            if (b) {
                binding.etPhone.visibility = View.VISIBLE
            } else {
                binding.etPhone.visibility = View.GONE
            }
        }

        binding.cbEmail.setOnCheckedChangeListener { compoundButton, b ->
            if (b) {
                binding.etEmail.visibility = View.VISIBLE
            } else {
                binding.etEmail.visibility = View.GONE
            }
        }

        binding.cvPaguelofacil.setOnClickListener {
            val details = arguments?.getParcelable<QrSend>("data")
            Timber.e("DATA DETAILS $details")
            if (binding.swInputDestinationReceipt.isChecked) {
                val bundle = Bundle()
                val fr = activity?.supportFragmentManager?.beginTransaction()
                val frg = CobroQrCodeFragment()
                bundle.putString("EMAIL", binding.etEmail.text.toString())
                bundle.putString("PHONE", binding.etPhone.text.toString())
                Timber.e("MONTOOOOO UWU ${details?.amount}/${details?.taxes}")
                bundle.putParcelable(
                    "data", QrSend(
                        amount = details?.amount ?: "0.00",
                        taxes = details?.taxes ?: "0.00",
                        tip = details?.tip ?: "0.00"
                    )
                )
                frg.setArguments(bundle)
                fr?.replace(R.id.container_frag_cobro, frg)
                fr?.addToBackStack(null)?.commit()
            } else {
                val fr = activity?.supportFragmentManager?.beginTransaction()
                fr?.replace(R.id.container_frag_cobro, CobroQrCodeFragment())
                fr?.addToBackStack(null)?.commit()
            }
        }
    }
    private fun showDialogDetectedCard() {
        val builderSingle: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        builderSingle.setIcon(R.drawable.ic_app_icon)
        builderSingle.setTitle("Selecciona una opción:-")

        val arrayAdapter =
            ArrayAdapter<String>(requireContext(), android.R.layout.select_dialog_singlechoice)
        arrayAdapter.add("Chip")
        arrayAdapter.add("Banda")
        arrayAdapter.add("Contact-less")

        builderSingle.setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }

        builderSingle.setAdapter(
            arrayAdapter
        ) { _, which -> goToDetectedCard(which) }
        builderSingle.show()
    }

    private fun showBottomSheetClose(){
        dialog = CancelBottomSheet(
            callBackClose = { dismissBottomSheetCancel() },
            callbackVolver = { dismissBottomSheetCancel() },
            callbackCancelar = {
                dismissBottomSheetCancel()
                goHome()
            }
        )
        dialog?.show(parentFragmentManager, "")
    }

    private fun goBackFragment() {
        val fr = activity?.supportFragmentManager
        fr?.popBackStack()
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
        dialog?.show(parentFragmentManager, "")
    }

    private fun goToDetectedCard(optionSelected: Int) {
        val details = arguments?.getParcelable<QrSend>("data")
        if (binding.swInputDestinationReceipt.isChecked) {
            val intent = Intent(requireContext(), DetectedCardActivity::class.java)
            intent.putExtra(OPTION_CARD_SELECTED, optionSelected)
            intent.putExtra("EMAIL", binding.etEmail.text.toString())
            intent.putExtra("PHONE", binding.etPhone.text.toString())
            intent.putExtra("AMOUNT", details?.amount?.replace(',','.'))
            intent.putExtra("TAXES", details?.taxes?.replace(',','.'))
            intent.putExtra("TIP", details?.tip?.replace(',','.'))
            Timber.e("MONTOOOOO UWU ${details?.amount}/${details?.taxes}")
            requireActivity().startActivityForResult(intent, 1000)
        } else {
            val intent = Intent(requireContext(), DetectedCardActivity::class.java)
            intent.putExtra(OPTION_CARD_SELECTED, optionSelected)
            intent.putExtra("AMOUNT", details?.amount?.replace(',','.'))
            intent.putExtra("TAXES", details?.taxes?.replace(',','.'))
            intent.putExtra("TIP", details?.tip?.replace(',','.'))
            requireActivity().startActivityForResult(intent, 1000)
        }
    }

    private fun dismissBottomSheetCancel() {
        dialog?.dismiss()
    }

    private fun goHome() {
        val intent = Intent(context, HomeActivity::class.java)
        startActivity(intent)
        activity?.finish()
    }

    companion object {
        const val OPTION_CARD_SELECTED = "OPTION_CARD_SELECTED"
    }
}
package com.paguelofacil.posfacil.ui.view.transactions.payment.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import com.paguelofacil.posfacil.ApplicationClass
import com.paguelofacil.posfacil.R
import com.paguelofacil.posfacil.databinding.FragmentComprobanteCobroBinding
import com.paguelofacil.posfacil.model.QrSend
import com.paguelofacil.posfacil.pax.MainActivity
import com.paguelofacil.posfacil.ui.view.custom_view.CancelBottomSheet
import com.paguelofacil.posfacil.ui.view.home.activities.HomeActivity
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
            if (binding.swInputDestinationReceipt.isChecked){
//                val bundle = Bundle()
//                val fr = activity?.supportFragmentManager?.beginTransaction()
//                val frg = DetectCreditCardFragment()
//                bundle.putString("EMAIL", binding.etEmail.text.toString())
//                bundle.putString("PHONE", binding.etPhone.text.toString())
//                frg.setArguments(bundle)
//                fr?.replace(R.id.container_frag_cobro, frg)
//                fr?.addToBackStack(null)?.commit()
                val intent = Intent(requireContext(),MainActivity::class.java)
                requireActivity().startActivityForResult(intent,1000)
            }else{
                val intent = Intent(requireContext(),MainActivity::class.java)
                requireActivity().startActivityForResult(intent,1000)
//                val fr = activity?.supportFragmentManager?.beginTransaction()
//                fr?.replace(R.id.container_frag_cobro, DetectCreditCardFragment())
//                fr?.addToBackStack(null)?.commit()
            }
        }

        binding.lnArrowBack.setOnClickListener{
            goBackFragment()
        }

        binding.lnCloseBack.setOnClickListener{
            showBottomSheet()
        }

        binding.swInputDestinationReceipt.setOnCheckedChangeListener { compoundButton, b ->
            if (b) {
                binding.lnCustomDestination.visibility=View.VISIBLE
            } else {
                binding.lnCustomDestination.visibility=View.GONE
            }
        }

        binding.cbPhone.setOnCheckedChangeListener { compoundButton, b ->
            if (b) {
                binding.etPhone.visibility=View.VISIBLE
            } else {
                binding.etPhone.visibility=View.GONE
            }
        }

        binding.cbEmail.setOnCheckedChangeListener { compoundButton, b ->
            if (b) {
                binding.etEmail.visibility=View.VISIBLE
            } else {
                binding.etEmail.visibility=View.GONE
            }
        }

        binding.cvPaguelofacil.setOnClickListener {
            val details = arguments?.getParcelable<QrSend>("data")
            Timber.e("DATA DETAILS $details")
            if (binding.swInputDestinationReceipt.isChecked){
                val bundle = Bundle()
                val fr = activity?.supportFragmentManager?.beginTransaction()
                val frg = CobroQrCodeFragment()
                bundle.putString("EMAIL", binding.etEmail.text.toString())
                bundle.putString("PHONE", binding.etPhone.text.toString())
                bundle.putParcelable("data", QrSend(
                    amount = details?.amount?: "",
                    taxes = details?.taxes ?: "",
                    tip = details?.tip ?: ""
                ))
                frg.setArguments(bundle)
                fr?.replace(R.id.container_frag_cobro, frg)
                fr?.addToBackStack(null)?.commit()
            }else{
                val fr = activity?.supportFragmentManager?.beginTransaction()
                fr?.replace(R.id.container_frag_cobro, CobroQrCodeFragment())
                fr?.addToBackStack(null)?.commit()
            }
        }

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

    private fun dismissBottomSheetCancel() {
        dialog?.dismiss()
    }

    private fun goHome() {
        val intent = Intent(context, HomeActivity::class.java)
        startActivity(intent)
        activity?.finish()
    }

}
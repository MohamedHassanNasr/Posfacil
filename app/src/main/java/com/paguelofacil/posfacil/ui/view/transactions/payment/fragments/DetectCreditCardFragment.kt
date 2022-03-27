package com.paguelofacil.posfacil.ui.view.transactions.payment.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.paguelofacil.posfacil.R
import com.paguelofacil.posfacil.databinding.FragmentDetectCreditCardBinding
import kotlinx.android.synthetic.main.bottom_sheet_dialog.view.*


class DetectCreditCardFragment : Fragment() {


    lateinit var binding: FragmentDetectCreditCardBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = FragmentDetectCreditCardBinding.inflate(inflater, container, false)


        loadListeners()


        Handler(Looper.getMainLooper()).postDelayed({
            showSuccesCard()
        }, 2000)


        return binding.root


    }

    private fun showSuccesCard()
    {
        binding.pbWaitingCard.visibility=View.GONE
        binding.ivSucces.visibility=View.VISIBLE
        binding.tvMessageValideCard.text=getString(R.string.tarjeta_reconocida_correctamente)

        Handler(Looper.getMainLooper()).postDelayed({
            validatingCard()
        }, 2000)



    }

    private fun validatingCard()
    {
        binding.ivSucces.visibility=View.GONE
        binding.ivWaitCard.visibility=View.GONE
        binding.pbWaitingCard.visibility=View.VISIBLE
        binding.tvMessageValideCard.text = getString(R.string.verificando_card)

        Handler(Looper.getMainLooper()).postDelayed({
            showSuccesTransaction()
        }, 2000)



    }

    private fun showSuccesTransaction()
    {
        binding.ivSucces.visibility=View.VISIBLE
        binding.ivWaitCard.visibility=View.GONE
        binding.pbWaitingCard.visibility=View.GONE
        binding.tvMessageValideCard.text = getString(R.string.cobro_succes)

        Handler(Looper.getMainLooper()).postDelayed({
            goViewFirma()
        }, 1000)

    }

    private fun loadListeners() {

        binding.lnArrowBack.setOnClickListener{

            goBackFragment()


        }

        binding.lnCloseBack.setOnClickListener{

            goBackFragment()

        }


    }
    private fun goBackFragment()
    {
        var fr = activity?.supportFragmentManager?.beginTransaction()
        fr?.replace(R.id.container_frag_cobro, MetodoPagoFragment())
        fr?.commit()
    }

    private fun showBottomSheet(mensaje:String,origen:Int) {

        val dialog = context?.let { BottomSheetDialog(it) }

        val view = layoutInflater.inflate(R.layout.bottom_sheet_dialog, null)

        val btnClose = view.findViewById<Button>(R.id.btn_aceptar_dg)

        view.tv_mensaje_dialog.text=mensaje

        btnClose.setOnClickListener {

            dialog?.dismiss()
            if (origen==1)
            performTransaction()
            else

            goViewFirma()

        }

        dialog?.setCancelable(false)

        dialog?.setContentView(view)

        dialog?.show()

    }

    private fun goViewFirma()
    {
        var fr = activity?.supportFragmentManager?.beginTransaction()
        fr?.replace(R.id.container_frag_cobro, FirmaFragment())
        fr?.commit()
    }

    private fun goDetailPay() {

        var fr = activity?.supportFragmentManager?.beginTransaction()
        fr?.replace(R.id.container_frag_cobro, SendReceiptPaymentFragment())
        fr?.commit()


    }

    private fun performTransaction() {

        binding.tvMessageValideCard.text = getString(R.string.verificando_card)
        binding.ivWaitCard.visibility=View.GONE

        showBottomSheet(getString(R.string.cobro_succes),2)



    }










}
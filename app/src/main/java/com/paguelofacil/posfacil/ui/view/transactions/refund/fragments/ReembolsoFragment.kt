package com.paguelofacil.posfacil.ui.view.transactions.refund.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.paguelofacil.posfacil.R
import com.paguelofacil.posfacil.databinding.FragmentReembolsoBinding
import com.paguelofacil.posfacil.ui.interfaces.IOnBackPressed
import kotlinx.android.synthetic.main.bottom_sheet_dialog.view.*


class ReembolsoFragment : Fragment(){

    lateinit var binding: FragmentReembolsoBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        binding=FragmentReembolsoBinding.inflate(inflater,container,false)

        loadListeners()

        return binding.root

    }

    private fun loadListeners() {


        binding.btnConfirmReembolso.setOnClickListener{

            var fr = activity?.supportFragmentManager?.beginTransaction()
            fr?.replace(R.id.container_frag_transactions, ComprobanteReembolsoFragment())
            fr?.commit()

        }

        binding.tvSelectMotivos.setOnClickListener {

            showBottomSheet()


        }

        binding.ivBack.setOnClickListener{

            activity?.supportFragmentManager?.popBackStack();

        }


    }

    private fun showBottomSheet() {

        val dialog = context?.let { BottomSheetDialog(it) }

        val view = layoutInflater.inflate(R.layout.bottom_sheet_dg_motivos, null)


        dialog?.setCancelable(true)

        dialog?.setContentView(view)

        dialog?.show()

    }



}
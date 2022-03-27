package com.paguelofacil.posfacil.ui.view.transactions.payment.fragments

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.paguelofacil.posfacil.R
import com.paguelofacil.posfacil.databinding.FragmentVerificarCobroBinding
import com.paguelofacil.posfacil.ui.view.home.activities.HomeActivity
import com.paguelofacil.posfacil.util.KeyboardUtil
import kotlinx.android.synthetic.main.bottom_sheet_dialog.view.*


class VerificarCobroFragment : Fragment() {


    lateinit var binding:FragmentVerificarCobroBinding
    var cvSelected:Int=0


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding=FragmentVerificarCobroBinding.inflate(inflater,container,false)

        loadListeners()


        return binding.root

    }

    private fun showBottomSheet() {

        val dialog = context?.let { BottomSheetDialog(it) }

        val view = layoutInflater.inflate(R.layout.bottom_sheet_cancel, null)

        val btnBack = view.findViewById<Button>(R.id.btn_volver)
        val btnCancel=view.findViewById<Button>(R.id.btn_si_cancelar)
        val ivCancel=view.findViewById<ImageView>(R.id.iv_close_dg)

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

    private fun goHome()
    {

        val intent= Intent(context, HomeActivity::class.java)
        startActivity(intent)
        activity?.finish()
    }


    private fun loadListeners() {


        binding.lnCloseBack.setOnClickListener {

            showBottomSheet()

        }


        binding.lnArrowBack.setOnClickListener{

            goHome()

        }

        binding.btnCobrarNext.setOnClickListener{

            var fr = activity?.supportFragmentManager?.beginTransaction()
            fr?.replace(R.id.container_frag_cobro, ComprobanteCobroFragment())
            fr?.commit()

        }


       binding.cardTip1.setOnClickListener {

           if(cvSelected!=1){
               clearSelectTips()

               paintSelectCard(binding.cardTip1,binding.tvTip1,binding.tvMontoTip1)

               cvSelected=1
           }
           else
           {
               if(cvSelected==1)
               {
                   clearSelectTips()
                   cvSelected=0
               }

           }
       }

        binding.cardTip2.setOnClickListener {

            if(cvSelected!=2){
                clearSelectTips()

                paintSelectCard(binding.cardTip2,binding.tvTip2,binding.tvMontoTip2)

                cvSelected=2
            }
            else
            {

                if(cvSelected==2)
                {
                    clearSelectTips()
                    cvSelected=0
                }

            }
        }

        binding.cardTip3.setOnClickListener {

            if(cvSelected!=3){
                clearSelectTips()

                paintSelectCard(binding.cardTip3,binding.tvTip3,binding.tvMontoTip3)

                cvSelected=3
            }
            else
            {

                if(cvSelected==3)
                {
                    clearSelectTips()
                    cvSelected=0
                }

            }
        }

        binding.cvOtherTip.setOnClickListener {

            if(cvSelected!=4){
                clearSelectTips()
                binding.cvOtherTip.setCardBackgroundColor(ContextCompat.getColor(binding.cardTip1.context,R.color.color_6BBE22))

                Glide.with(binding.ivPointsTips.context)
                    .load(R.drawable.ic_equis_tips).centerInside()
                    .into(binding.ivPointsTips)


                binding.etCustomTip.visibility=View.VISIBLE

                cvSelected=4
            }
            else
            {

                if(cvSelected==4)
                {
                    clearSelectTips()

                    cvSelected=0
                }

            }
        }


        binding.swDefaultImpuesto.setOnCheckedChangeListener { compoundButton, b ->


            binding.etCustomTaxe.isEnabled=!b

            if(!b)
            {
                KeyboardUtil.showKeyboard(activity)
                binding.etCustomTaxe.requestFocus()


            }
            else
            {
                binding.etCustomTaxe.clearFocus()
                binding.etCustomTaxe.setText("0.07")

            }


        }

    }

    private fun paintSelectCard(cardView: CardView,textView:TextView,textMount:TextView)
    {
        cardView.setCardBackgroundColor(ContextCompat.getColor(binding.cardTip1.context,R.color.color_6BBE22))
        textView.setTextColor(Color.WHITE);
        textMount.visibility=View.VISIBLE
    }


    private fun clearSelectTips()
    {
        binding.etCustomTip.visibility=View.GONE

        Glide.with(binding.ivPointsTips.context)
            .load(R.drawable.ic_points).centerInside()
            .into(binding.ivPointsTips)

        binding.cardTip1.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.color_E0E3E7))
        binding.cardTip2.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.color_E0E3E7))
        binding.cardTip3.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.color_E0E3E7))
        binding.cvOtherTip.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.color_E0E3E7))



        binding.tvTip1.setTextColor(ContextCompat.getColor(requireContext(), R.color.color_515A69));
        binding.tvTip2.setTextColor(ContextCompat.getColor(requireContext(), R.color.color_515A69));
        binding.tvTip3.setTextColor(ContextCompat.getColor(requireContext(), R.color.color_515A69));


        binding.tvMontoTip1.visibility=View.GONE
        binding.tvMontoTip2.visibility=View.GONE
        binding.tvMontoTip3.visibility=View.GONE




    }


}
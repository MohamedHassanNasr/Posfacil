package com.paguelofacil.posfacil.ui.view.home.fragments

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.paguelofacil.posfacil.R
import com.paguelofacil.posfacil.databinding.FragmentHomeBinding
import com.paguelofacil.posfacil.ui.view.MainActivity
import com.paguelofacil.posfacil.ui.view.account.fragments.MyInformationFragment
import com.paguelofacil.posfacil.ui.view.home.activities.HomeActivity
import com.paguelofacil.posfacil.ui.view.settings.activities.AjustesActivity
import com.paguelofacil.posfacil.ui.view.transactions.payment.activities.CobroActivity
import com.paguelofacil.posfacil.ui.view.transactions.payment.fragments.ComprobanteCobroFragment
import com.paguelofacil.posfacil.util.Constantes.ConstantesView
import com.paguelofacil.posfacil.util.KeyboardUtil
import kotlinx.android.synthetic.main.app_bar_home.*


class HomeFragment : Fragment() {

    private var toolbar: Toolbar? =null

    lateinit var binding:FragmentHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState)




    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding= FragmentHomeBinding.inflate(inflater,container,false)



        loadListeners()

        return binding.root


    }


    private fun loadListeners() {


        binding.etMontoCobrar.setOnFocusChangeListener { view, b ->

            if (b)
            {
                KeyboardUtil.showKeyboard(activity)
            }

        }


        binding.btnNext.setOnClickListener{


            KeyboardUtil.hideKeyboard(activity)

            val intent= Intent(context, CobroActivity::class.java)

            startActivity(intent)


        }


    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {

        inflater!!.inflate(R.menu.menu_profile_home, menu)
        true


    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val id = item!!.itemId

        if (id == R.id.item_menu_profile){

            goViewAjustes(ConstantesView.PARAM_PROFILE)


        }

        return super.onOptionsItemSelected(item)
    }

    private fun goViewAjustes(value:String)
    {
        val params = Bundle()

        params.putString(ConstantesView.PARAM_FRAGMENT, value)

        val intent= Intent(context, AjustesActivity::class.java)

        intent.putExtras(params)

        startActivityForResult(intent,200)
    }




}
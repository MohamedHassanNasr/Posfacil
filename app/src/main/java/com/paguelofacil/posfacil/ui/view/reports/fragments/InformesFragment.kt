package com.paguelofacil.posfacil.ui.view.reports.fragments

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import com.paguelofacil.posfacil.ApplicationClass
import com.paguelofacil.posfacil.R
import com.paguelofacil.posfacil.ui.view.adapters.InformesPagerAdapter
import com.paguelofacil.posfacil.databinding.FragmentInformesBinding
import com.paguelofacil.posfacil.ui.view.home.viewmodel.HomeViewModel
import timber.log.Timber


class InformesFragment : Fragment() {

    lateinit var binding: FragmentInformesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding= FragmentInformesBinding.inflate(inflater,container,false)

        Timber.e("LENGUA ")

        changeTitle(boolean = false)

        return binding.root


    }

    private fun changeTitle(text: String? = null, boolean: Boolean){
        /*val view = layoutInflater.inflate(R.layout.app_bar_home, null)
        val title = view.findViewById<TextView>(R.id.titleApp)

        title.text = "Ajustes"*/
        val viewModel = ViewModelProvider(requireActivity()).get<HomeViewModel>(modelClass = HomeViewModel::class.java)

        viewModel.setTitle(text ?: ApplicationClass.language.informe, boolean)
        Timber.e("VIEEE ${viewModel.x}")
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        configureTopNavigation()
    }

    private fun configureTopNavigation(){

        binding.vpHomeDetail.adapter = InformesPagerAdapter(childFragmentManager, 2,context)

        binding.vpHomeDetail.offscreenPageLimit = 1

        binding.tlHomeDetailBar.setupWithViewPager(binding.vpHomeDetail)


    }

}
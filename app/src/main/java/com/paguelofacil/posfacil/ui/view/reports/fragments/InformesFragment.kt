package com.paguelofacil.posfacil.ui.view.reports.fragments

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.paguelofacil.posfacil.R
import com.paguelofacil.posfacil.ui.view.adapters.InformesPagerAdapter
import com.paguelofacil.posfacil.databinding.FragmentInformesBinding


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


        return binding.root


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
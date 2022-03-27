package com.paguelofacil.posfacil.ui.view.reports.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.paguelofacil.posfacil.R
import com.paguelofacil.posfacil.databinding.FragmentReporteZBinding


class ReporteZFragment : Fragment() {


    lateinit var binding: FragmentReporteZBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding=FragmentReporteZBinding.inflate(inflater,container,false)

        return binding.root

    }

}
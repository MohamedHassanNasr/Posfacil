package com.paguelofacil.posfacil.ui.view.adapters

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.paguelofacil.posfacil.R
import com.paguelofacil.posfacil.ui.view.reports.fragments.InformeVentasFragment
import com.paguelofacil.posfacil.ui.view.reports.fragments.ReporteXFragment

class InformesPagerAdapter (fm : FragmentManager, val fragmentCount : Int,context: Context?): FragmentStatePagerAdapter(fm){

    private val fragmentTitleList = mutableListOf(context?.getString(R.string.ventas), context?.getString(R.string.reportes))

    override fun getItem(position:Int): Fragment {

        when(position){
            0-> return InformeVentasFragment()
            1-> return ReporteXFragment()
            else -> return InformeVentasFragment()
        }
    }

    override fun getPageTitle(position: Int):CharSequence?{
        return fragmentTitleList[position]
    }
    override fun getCount(): Int = fragmentCount
}
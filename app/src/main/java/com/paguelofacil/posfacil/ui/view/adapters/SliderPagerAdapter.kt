package com.paguelofacil.posfacil.ui.view.adapters


import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.paguelofacil.posfacil.ui.view.home.fragments.SliderOneFragment
import com.paguelofacil.posfacil.ui.view.home.fragments.SliderThreeFragment
import com.paguelofacil.posfacil.ui.view.home.fragments.SliderTwoFragment

@Suppress("DEPRECATION")

class SliderPagerAdapter (fm: FragmentManager) :
    FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> SliderOneFragment()
            1 -> SliderTwoFragment()
            2 -> SliderThreeFragment()
            else -> throw IllegalStateException("Unexpected position $position")
        }
    }
    override fun getCount(): Int {
        return 3
    }
}
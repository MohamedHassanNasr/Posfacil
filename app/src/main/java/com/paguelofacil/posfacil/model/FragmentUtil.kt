package com.paguelofacil.posfacil.model

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

class FragmentInflator(val fragmentManager: FragmentManager){
    fun fragmentTransaction(containerView: Int, fragment: Fragment){
        fragmentManager
            .beginTransaction()
            .replace(containerView, fragment)
            .setTransition(androidx.fragment.app.FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .commit()
    }
}
package com.paguelofacil.posfacil.ui.view

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.paguelofacil.posfacil.R
import com.paguelofacil.posfacil.ui.view.adapters.SliderPagerAdapter
import com.paguelofacil.posfacil.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initSlider()



    }


    private fun initSlider() {

        val sectionsPagerAdapter = SliderPagerAdapter(supportFragmentManager)
        binding.viewPager.adapter=sectionsPagerAdapter

        for (i in 0 until sectionsPagerAdapter.count) {
            val points = TextView(this)
            points.layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            points.text = "__ "

            points.setTextColor(ContextCompat.getColor(this, R.color.color_FFE4E4E4))
            points.textSize = 64F

            binding.viewPager.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
                if (i == binding.viewPager.currentItem) {
                    points.setTextColor(ContextCompat.getColor(this, R.color.color_FF959595))
                }else{
                    points.setTextColor(ContextCompat.getColor(this, R.color.color_FFE4E4E4))

                }
            }

            binding.bottomNavLinearLayout.addView(points)
        }


    }
}
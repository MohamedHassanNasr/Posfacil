package com.paguelofacil.posfacil.ui.view.settings.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.paguelofacil.posfacil.R
import com.paguelofacil.posfacil.databinding.ActivityAboutBinding
import com.paguelofacil.posfacil.ui.view.home.activities.HomeActivity

class AboutActivity : AppCompatActivity() {


    lateinit var binding: ActivityAboutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding= ActivityAboutBinding.inflate(layoutInflater)

        loadListeners()

        setContentView(binding.root)
    }

    private fun loadListeners() {

        binding.ivBack.setOnClickListener{

            goHome()
        }
    }

    private fun goHome()
    {

        finish()
    }


}
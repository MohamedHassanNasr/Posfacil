package com.paguelofacil.posfacil.ui.view.settings.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.paguelofacil.posfacil.ApplicationClass
import com.paguelofacil.posfacil.databinding.ActivityAboutBinding

class AboutActivity : AppCompatActivity() {

    lateinit var binding: ActivityAboutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAboutBinding.inflate(layoutInflater)

        loadListeners()
        loadLanguage()
        setContentView(binding.root)
    }

    private fun loadListeners() {
        binding.ivBack.setOnClickListener {
            goHome()
        }
    }

    private fun loadLanguage() {
        binding.tvTitle.text = ApplicationClass.language.aboutSupport
        binding.textViewApplication.text = ApplicationClass.language.about_app
        binding.textViewVersionAplicacion.text = ApplicationClass.language.aboutVersionApp
        binding.textViewVersion.text = ApplicationClass.language.aboutVersionSpoc
    }

    private fun goHome() {
        finish()
    }


}
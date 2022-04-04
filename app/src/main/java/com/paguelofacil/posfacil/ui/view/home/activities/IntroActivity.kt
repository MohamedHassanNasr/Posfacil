package com.paguelofacil.posfacil.ui.view.home.activities

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.paguelofacil.posfacil.R
import com.paguelofacil.posfacil.base.BaseActivity
import com.paguelofacil.posfacil.base.BaseViewModel
import com.paguelofacil.posfacil.data.network.api.ApiError
import com.paguelofacil.posfacil.data.network.api.ApiRequestCode
import com.paguelofacil.posfacil.data.network.response.LoginApiResponse
import com.paguelofacil.posfacil.data.network.response.SystemParamsResponse
import com.paguelofacil.posfacil.databinding.ActivityIntroBinding
import com.paguelofacil.posfacil.repository.ConfigurationsRepo
import com.paguelofacil.posfacil.repository.UserRepo
import com.paguelofacil.posfacil.ui.view.account.viewmodel.SystemConfigViewModel
import org.json.JSONObject

class IntroActivity :  BaseActivity(), View.OnClickListener, View.OnFocusChangeListener {

    private lateinit var binding: ActivityIntroBinding
    private val vm: SystemConfigViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityIntroBinding.inflate(layoutInflater)

        setContentView(binding.root)

        setBaseViewModel(vm)
        vm.response.observe(this, this)

        vm.getParamsSystem()

        loasListeners()

    }
    override fun onResponseSuccess(requestCode: Int, responseCode: Int, msg: String?, data: Any?) {
        super.onResponseSuccess(requestCode, responseCode, msg, data)


        when (responseCode) {

            ApiRequestCode.SUCCESS ->{

                val type = object : TypeToken<SystemParamsResponse>() {}.type
                val response = Gson().fromJson<SystemParamsResponse>(Gson().toJson(data), type)

                ConfigurationsRepo.saveSystemsParamsData(response)

                loadDataScreen()

            }


        }

    }

    /**
     *
     * Cargar imagen segun idioma del usuario
     * "es" -> español , "en" -> ingles
     *
     */
    private fun loadDataScreen()
    {
        val systemsParam=ConfigurationsRepo.getSystemParamsLocal()
        val dataScreen=systemsParam._screen_saver

        val json = JSONObject(dataScreen)


        val urlScreen = json.getString("es")

        Glide.with(this)
            .load(urlScreen).fitCenter()
            .placeholder(R.drawable.stactic_image)
            .into(binding.ivIntroMage)



    }




    override fun onExceptionData(requestCode: Int, exception: ApiError, data: Any?) {
        TODO("Not yet implemented")
    }

    private fun loasListeners() {

        binding.ivIntroMage.setOnClickListener {

            val intent= Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()

        }

    }

    override fun onClick(p0: View?) {
        TODO("Not yet implemented")
    }

    override fun onFocusChange(p0: View?, p1: Boolean) {
        TODO("Not yet implemented")
    }

}
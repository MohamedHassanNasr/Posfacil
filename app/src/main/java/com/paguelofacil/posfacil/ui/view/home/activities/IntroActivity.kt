package com.paguelofacil.posfacil.ui.view.home.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.activity.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.paguelofacil.posfacil.ApplicationClass
import com.paguelofacil.posfacil.R
import com.paguelofacil.posfacil.base.BaseActivity
import com.paguelofacil.posfacil.data.network.api.ApiError
import com.paguelofacil.posfacil.data.network.api.ApiRequestCode
import com.paguelofacil.posfacil.data.network.response.SystemParamsResponse
import com.paguelofacil.posfacil.databinding.ActivityIntroBinding
import com.paguelofacil.posfacil.repository.ConfigurationsRepo
import com.paguelofacil.posfacil.repository.UserRepo
import com.paguelofacil.posfacil.ui.view.account.viewmodel.SystemConfigViewModel
import com.paguelofacil.posfacil.ui.view.home.viewmodel.HomeViewModel
import com.paguelofacil.posfacil.util.networkErrorConverter
import kotlinx.coroutines.launch
import org.json.JSONObject
import timber.log.Timber
import java.util.*

class IntroActivity :  BaseActivity(), View.OnClickListener, View.OnFocusChangeListener {

    private lateinit var binding: ActivityIntroBinding
    private val vm: SystemConfigViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val bundle = intent.getBooleanExtra("isMain", false)
        Timber.e("boolea $bundle")

        binding = ActivityIntroBinding.inflate(layoutInflater)

        setContentView(binding.root)

        setBaseViewModel(vm)
        vm.response.observe(this, this)

        getParam(bundle)

    }

    private fun getParam(bundle: Boolean) {
        vm.getParamsSystem(
            onSuccess = {
                loasListeners(bundle)
            },
            onFailure = {
                showWarningDialog(it) {
                    getParam(bundle)
                }
            }
        )
    }

    private fun showWarningDialog(message: String, onFailure: ()-> Unit){
        val dialog = this?.let { BottomSheetDialog(it) }

        val view = layoutInflater.inflate(R.layout.bottom_sheet_warning, null)
        val title =view.findViewById<Button>(R.id.titleError)
        val description =view.findViewById<Button>(R.id.descriptionError)
        val btn = view.findViewById<MaterialButton>(R.id.btnAccept)

        title.text = ApplicationClass.language.error
        description.text = if ((message == "400") or (message == "400") or (message == "400")){
            ApplicationClass.language.errorPaidTryAgainOrContactOurSupportTeam
        }else{
            networkErrorConverter(message)
        }

        btn.text = ApplicationClass.language.try_againg
        btn.setOnClickListener {
            dialog?.dismiss()
            onFailure()
        }

        dialog?.setCancelable(true)

        dialog?.setContentView(view)

        dialog?.show()
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
        refreshUser()
        val systemsParam=ConfigurationsRepo.getSystemParamsLocal()
        val dataScreen=systemsParam._screen_saver
        Timber.e("image $dataScreen")

        val json = JSONObject(dataScreen)

        val urlScreen = json.getString(vm.getLanguageDeviceLocal().toLowerCase(Locale.getDefault()))

        Glide.with(this)
            .load(urlScreen).fitCenter()
            //.placeholder(R.drawable.stactic_image)
            .into(binding.ivIntroMage)
            .waitForLayout()

    }

    private fun refreshUser(){
        Timber.e("REFRESH!")
        lifecycleScope.launch {
            vm.refreshLogin()
        }
    }


    override fun onExceptionData(requestCode: Int, exception: ApiError, data: Any?) {
        TODO("Not yet implemented")
    }

    private fun loasListeners(isMain: Boolean) {
        Timber.e("isMain $isMain")

        binding.ivIntroMage.setOnClickListener {
            if (isMain){
                val intent= Intent(this, HomeActivity::class.java)
                startActivity(intent)
                finish()
            }else{
                this.finish()
            }

        }

    }

    override fun onClick(p0: View?) {
        TODO("Not yet implemented")
    }

    override fun onFocusChange(p0: View?, p1: Boolean) {
        TODO("Not yet implemented")
    }
}
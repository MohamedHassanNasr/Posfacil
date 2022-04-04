package com.paguelofacil.posfacil.repository

import androidx.core.content.edit
import com.google.gson.Gson
import com.paguelofacil.posfacil.base.BaseRepo
import com.paguelofacil.posfacil.data.database.PreferenceManager
import com.paguelofacil.posfacil.data.database.entity.SystemsParamsEntity
import com.paguelofacil.posfacil.data.database.entity.UserEntity
import com.paguelofacil.posfacil.data.network.api.ApiEndpoints
import com.paguelofacil.posfacil.data.network.api.ApiRequestCode
import com.paguelofacil.posfacil.data.network.api.BaseResponse
import com.paguelofacil.posfacil.data.network.response.LoginApiResponse
import com.paguelofacil.posfacil.data.network.response.SystemParamsResponse
import com.paguelofacil.posfacil.util.Constantes.ApiParams
import com.paguelofacil.posfacil.util.Constantes.LastUpdatedOn
import com.paguelofacil.posfacil.util.clearLocalDataForPreviousUser

object ConfigurationsRepo: BaseRepo() {

    private val sharedPref = PreferenceManager.sharedPref

    const val SYSTEM_PARAMS_ENTITY = "SYSTEMS_PARAMS_ENTITY"

    suspend fun getParamsSystem(): BaseResponse<Any> {
        val map = HashMap<String, Any>()

        map[ApiParams.CONDITIONAL] = "props::POSFACIL"
        return apiRequest(ApiRequestCode.SEARCH_PARAMS_SYSTEM) {
            remoteDao.get(ApiEndpoints.PARAMS_SYSTEM, map)
        }
    }

    fun getSystemParamsLocal(): SystemsParamsEntity {
        val defaultValue = Gson().toJson(SystemsParamsEntity())

        return Gson().fromJson(sharedPref.getString(SYSTEM_PARAMS_ENTITY, defaultValue), SystemsParamsEntity::class.java)

    }

    fun saveSystemsParamsData(response: SystemParamsResponse) {

        PreferenceManager.sharedPref.edit {
           remove(LastUpdatedOn.SYSTEM_PARAMS)
        }


        val systemParams = ConfigurationsRepo.getSystemParamsLocal()

        systemParams._default_values_refund = response.values._default_values_refund
        systemParams._default_values_tip = response.values._default_values_tip
        systemParams._screen_saver = response.values._screen_saver
        systemParams._url_terms = response.values._url_terms


        ConfigurationsRepo.setOrUpdateParams(systemParams, true)
    }

    fun setOrUpdateParams(systemParamsEntity: SystemsParamsEntity?, updateOnMainThread: Boolean = false) {
        val editor = sharedPref.edit()
        if (systemParamsEntity == null) {
            editor.remove(SYSTEM_PARAMS_ENTITY)
        } else {
            editor.putString(SYSTEM_PARAMS_ENTITY, Gson().toJson(systemParamsEntity))
        }
        if (updateOnMainThread) {
            editor.commit()
        } else {
            editor.apply()
        }
    }



}
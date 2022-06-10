package com.paguelofacil.posfacil.repository.language

import com.paguelofacil.posfacil.data.database.PreferenceManager
import com.paguelofacil.posfacil.data.network.api.BaseResponse
import com.paguelofacil.posfacil.model.LanguageDataResponse
import com.paguelofacil.posfacil.util.Resultado
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LanguageRepository @Inject constructor(val languageService: LanguageService) {
    private val preferenceManager = PreferenceManager

    suspend fun getLanguageRemote(): Resultado<BaseResponse<LanguageDataResponse>> {
        val language = preferenceManager.getLanguageDevice()
        val conditional = "lang\$eq$language%7Cgroup\$eqposfacil"
        return withContext(Dispatchers.IO) { languageService.getLanguage(conditional) }
    }

    fun getLanguageDeviceLocal(): String {
        return preferenceManager.getLanguageDevice()
    }

    fun setLanguageDeviceLocal(language: String) {
        preferenceManager.setLanguageDevice(language)
    }
}
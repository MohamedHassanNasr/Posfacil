package com.paguelofacil.posfacil.ui.view.account.viewmodel

import androidx.lifecycle.viewModelScope
import com.paguelofacil.posfacil.base.BaseViewModel
import com.paguelofacil.posfacil.data.database.PreferenceManager
import com.paguelofacil.posfacil.model.toLanguageData
import com.paguelofacil.posfacil.repository.ConfigurationsRepo
import com.paguelofacil.posfacil.repository.UserRepo
import com.paguelofacil.posfacil.repository.language.LanguageRepository
import com.paguelofacil.posfacil.repository.language.LanguageService
import com.paguelofacil.posfacil.util.Constantes.CoroutinesBase
import com.paguelofacil.posfacil.util.Constantes.LoadingState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SystemConfigViewModel: BaseViewModel() {

    private val repo=ConfigurationsRepo
    private val repoUser= UserRepo
    private val preferenceManager = PreferenceManager

    fun getParamsSystem(onSuccess: (BaseViewModel.ApiResponse)-> Unit, onFailure: (String)-> Unit)= CoroutinesBase.main {
        setLoadingState(LoadingState.LOADING)
        updateResponseObserver(repo.getParamsSystem(), onFailure = {onFailure(it)}, callback = {onSuccess(it)})
        setLoadingState(LoadingState.LOADED)
        //onSuccess()
    }

    suspend fun refreshLogin(){
        repoUser.refreshLogin()
    }

    fun getLanguageDeviceLocal(): String {
        return preferenceManager.getLanguageDevice()
    }
}
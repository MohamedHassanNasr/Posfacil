package com.paguelofacil.posfacil.ui.view.account.viewmodel

import com.paguelofacil.posfacil.base.BaseViewModel
import com.paguelofacil.posfacil.repository.ConfigurationsRepo
import com.paguelofacil.posfacil.repository.UserRepo
import com.paguelofacil.posfacil.util.Constantes.CoroutinesBase
import com.paguelofacil.posfacil.util.Constantes.LoadingState
import kotlinx.coroutines.delay

class SystemConfigViewModel: BaseViewModel() {

    private val repo=ConfigurationsRepo
    private val repoUser= UserRepo

    fun getParamsSystem(onSuccess: (BaseViewModel.ApiResponse)-> Unit, onFailure: (String)-> Unit)= CoroutinesBase.main {
        setLoadingState(LoadingState.LOADING)
        updateResponseObserver(repo.getParamsSystem(), onFailure = {onFailure(it)}, callback = {onSuccess(it)})
        setLoadingState(LoadingState.LOADED)
        //onSuccess()
    }

    suspend fun refreshLogin(){
        repoUser.refreshLogin()
    }
}
package com.paguelofacil.posfacil.ui.view.account.viewmodel

import com.paguelofacil.posfacil.base.BaseViewModel
import com.paguelofacil.posfacil.repository.ConfigurationsRepo
import com.paguelofacil.posfacil.util.Constantes.CoroutinesBase
import com.paguelofacil.posfacil.util.Constantes.LoadingState

class SystemConfigViewModel: BaseViewModel() {

    private val repo=ConfigurationsRepo

    fun getParamsSystem()= CoroutinesBase.main {
        setLoadingState(LoadingState.LOADING)
        updateResponseObserver(repo.getParamsSystem()) {}
        setLoadingState(LoadingState.LOADED)
    }
}
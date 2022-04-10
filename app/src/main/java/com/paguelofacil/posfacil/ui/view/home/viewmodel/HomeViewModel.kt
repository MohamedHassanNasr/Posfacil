package com.paguelofacil.posfacil.ui.view.home.viewmodel

import com.paguelofacil.posfacil.base.BaseViewModel
import com.paguelofacil.posfacil.repository.ReportRepo
import com.paguelofacil.posfacil.util.Constantes.CoroutinesBase
import com.paguelofacil.posfacil.util.Constantes.LoadingState

class HomeViewModel: BaseViewModel() {
    private val repo = ReportRepo

    fun checkZReport() = CoroutinesBase.main {
        setLoadingState(LoadingState.LOADING)
        updateResponseObserver(repo.checkZReport()) {}
        setLoadingState(LoadingState.LOADED)
    }
}
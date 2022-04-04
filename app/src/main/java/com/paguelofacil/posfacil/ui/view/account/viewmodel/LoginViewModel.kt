package com.paguelofacil.posfacil.ui.view.account.viewmodel

import com.paguelofacil.posfacil.base.BaseViewModel
import com.paguelofacil.posfacil.repository.UserRepo
import com.paguelofacil.posfacil.util.Constantes.CoroutinesBase
import com.paguelofacil.posfacil.util.Constantes.LoadingState

class LoginViewModel: BaseViewModel() {
    private val repo = UserRepo

    fun signIn(email: String, password: String) = CoroutinesBase.main {
        setLoadingState(LoadingState.LOADING)
        updateResponseObserver(repo.login(email, password)) {}
        setLoadingState(LoadingState.LOADED)
    }

}
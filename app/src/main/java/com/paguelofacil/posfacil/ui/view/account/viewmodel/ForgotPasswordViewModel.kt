package com.paguelofacil.posfacil.ui.view.account.viewmodel

import com.paguelofacil.posfacil.base.BaseViewModel
import com.paguelofacil.posfacil.repository.UserRepo
import com.paguelofacil.posfacil.tools.SingleLiveData
import com.paguelofacil.posfacil.util.Constantes.CoroutinesBase
import com.paguelofacil.posfacil.util.Constantes.LoadingState

class ForgotPasswordViewModel : BaseViewModel() {
    val repo = UserRepo
    val btnClickEvent = SingleLiveData<Boolean>()

    fun getUnsavedUserAccountDetail(email: String) = CoroutinesBase.main {
        setLoadingState(LoadingState.LOADING)
        updateResponseObserver(contactsRepo.getPagueloContactWithEmail(email)) {}
        setLoadingState(LoadingState.LOADED)
    }

    fun sendOtpStep1(email: String) = CoroutinesBase.main {
        setLoadingState(LoadingState.LOADING)
        updateResponseObserver(repo.sendOtpStep1(email)) {}
        setLoadingState(LoadingState.LOADED)
    }

    fun verifyOtpStep2(otp: String) = CoroutinesBase.main {
        setLoadingState(LoadingState.LOADING)
        updateResponseObserver(repo.verifyOtpStep2(otp)) {}
        setLoadingState(LoadingState.LOADED)
    }

    fun verifyOtpStep3(password: String, otp: String) = CoroutinesBase.main {
        setLoadingState(LoadingState.LOADING)
        updateResponseObserver(repo.verifyOtpStep3(password, otp)) {}
        setLoadingState(LoadingState.LOADED)
    }
}
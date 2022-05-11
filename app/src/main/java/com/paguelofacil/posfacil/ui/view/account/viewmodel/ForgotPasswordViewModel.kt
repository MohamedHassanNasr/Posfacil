package com.paguelofacil.posfacil.ui.view.account.viewmodel

import com.paguelofacil.posfacil.base.BaseViewModel
import com.paguelofacil.posfacil.repository.UserRepo
import com.paguelofacil.posfacil.tools.SingleLiveData
import com.paguelofacil.posfacil.util.Constantes.CoroutinesBase
import com.paguelofacil.posfacil.util.Constantes.LoadingState

class ForgotPasswordViewModel : BaseViewModel() {
    val repo = UserRepo
    val btnClickEvent = SingleLiveData<Boolean>()

    fun getUnsavedUserAccountDetail(email: String, onFailure: (String)-> Unit) = CoroutinesBase.main {
        setLoadingState(LoadingState.LOADING)
        updateResponseObserver(contactsRepo.getPagueloContactWithEmail(email), onFailure = {onFailure(it)}, callback = {})
        setLoadingState(LoadingState.LOADED)
    }

    fun sendOtpStep1(email: String, onFailure: (String) -> Unit) = CoroutinesBase.main {
        setLoadingState(LoadingState.LOADING)
        updateResponseObserver(repo.sendOtpStep1(email), onFailure = {onFailure(it)}, callback = {})
        setLoadingState(LoadingState.LOADED)
    }

    fun verifyOtpStep2(otp: String, onFailure: (String) -> Unit) = CoroutinesBase.main {
        setLoadingState(LoadingState.LOADING)
        updateResponseObserver(repo.verifyOtpStep2(otp), onFailure = {onFailure(it)}, callback = {})
        setLoadingState(LoadingState.LOADED)
    }

    fun verifyOtpStep3(password: String, otp: String, onFailure: (String) -> Unit) = CoroutinesBase.main {
        setLoadingState(LoadingState.LOADING)
        updateResponseObserver(repo.verifyOtpStep3(password, otp), onFailure = {onFailure(it)}, callback = {})
        setLoadingState(LoadingState.LOADED)
    }
}
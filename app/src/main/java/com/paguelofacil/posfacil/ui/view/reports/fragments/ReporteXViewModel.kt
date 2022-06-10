package com.paguelofacil.posfacil.ui.view.reports.fragments

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.paguelofacil.posfacil.base.BaseViewModel
import com.paguelofacil.posfacil.data.database.entity.UserEntity
import com.paguelofacil.posfacil.model.MerchantResponse
import com.paguelofacil.posfacil.model.ReportXResponse
import com.paguelofacil.posfacil.repository.ReportRepo
import com.paguelofacil.posfacil.repository.UserRepo
import com.paguelofacil.posfacil.util.Constantes.LoadingState
import com.pax.dal.ISys
import kotlinx.coroutines.launch
import timber.log.Timber

class ReporteXViewModel: BaseViewModel() {

    private val mutableTransactionList = MutableLiveData<ReportXResponse>()
    val liveDataTransactionList: LiveData<ReportXResponse> = mutableTransactionList

    private val _reporteXState = MutableLiveData<ReportXResponse>()
    val reporteXState: LiveData<ReportXResponse> get() = _reporteXState

    private val _reportez = MutableLiveData<ReportXResponse>()
    val reportez: LiveData<ReportXResponse> = _reportez

    private val _userValue = MutableLiveData<UserEntity>()
    val userValue: LiveData<UserEntity> = _userValue

    private val _merchant = MutableLiveData<MerchantResponse>()
    val merchant: LiveData<MerchantResponse> = _merchant


    suspend fun getReporteX(onSuccess: () -> Unit, sendEmail: Boolean, serial: String,onFailure: (String)-> Unit){
        execute {
            viewModelScope.launch {
                val user = UserRepo.getUser()
                user.let {
                    it.email?.let {email->
                        val response = ReportRepo.getReporteX(if (sendEmail){email}else{""}, serial)
                        if (response.headerStatus.code.toString() == "200"){
                            Timber.e("200")
                            mutableTransactionList.postValue(response)
                            _reporteXState.postValue(response)
                        }else{
                            Timber.e("ERROr")
                            mutableTransactionList.postValue(response)
                            _reporteXState.postValue(response)
                            onFailure(response.headerStatus.code.toString())
                        }
                        setLoadingState(LoadingState.LOADED)
                        if (response.headerStatus.code == 200){
                            onSuccess()
                        }
                    }
                }
            }
        }

    }

    fun setReporteX(){
        mutableTransactionList.postValue(reporteXState.value)
    }

    suspend fun getMerchantDetail(){
        execute {
            viewModelScope.launch {
                val user = UserRepo.getUser()
                user?.let {
                    it.idMerchant?.let {id->
                        val response = UserRepo.getMerchant(idMerchant = id)
                        Timber.e("RESPONSE MERCHANR $response")
                        if (response.headerStatus.code.toString() == "200"){
                            _merchant.postValue(response)
                        }
                        setLoadingState(LoadingState.LOADED)
                    }
                }
            }
        }
    }

    fun getUserOwner(){
        execute {
            viewModelScope.launch {
                val user = UserRepo.getUser()
                user?.let {
                    _userValue.postValue(user)
                }
            }
        }
    }

    suspend fun getReportZ(sendEmail: Boolean, serial: String, onSuccess: () -> Unit, onFailure: (String) -> Unit){
        execute {
            viewModelScope.launch {
                val user = UserRepo.getUser()
                user?.let {
                    it.email?.let {email->
                        val response = ReportRepo.getReporteZ(if (sendEmail){email}else{""}, serial)
                        if (response.headerStatus.code.toString() == "200"){
                            _reportez.postValue(response)
                        }else{
                            onFailure(response.headerStatus.code.toString())
                        }
                        setLoadingState(LoadingState.LOADED)
                        if (response.headerStatus.code == 200){
                            onSuccess()
                        }
                    }
                }
            }
        }
    }

}
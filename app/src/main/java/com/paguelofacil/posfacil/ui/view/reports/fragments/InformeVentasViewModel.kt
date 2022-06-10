package com.paguelofacil.posfacil.ui.view.reports.fragments

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.paguelofacil.posfacil.base.BaseViewModel
import com.paguelofacil.posfacil.data.network.api.ApiEndpoints
import com.paguelofacil.posfacil.model.ReporteVentaResponse
import com.paguelofacil.posfacil.repository.ReportRepo
import com.paguelofacil.posfacil.util.Constantes.LoadingState
import com.pax.dal.ISys
import kotlinx.coroutines.launch
import timber.log.Timber

class InformeVentasViewModel: BaseViewModel() {

    private val mutableTransactionList = MutableLiveData<ReporteVentaResponse>()
    val liveDataTransactionList: LiveData<ReporteVentaResponse> = mutableTransactionList

    suspend fun getReportesVentas(serial: String) {
        execute {
            viewModelScope.launch {
                val response = ReportRepo.getReporte(serial)
                if (response.headerStatus.code.toString() == "200"){
                    mutableTransactionList.postValue(response)
                }else{
                    Timber.e("ERRORR")
                    mutableTransactionList.postValue(response)
                }
                setLoadingState(LoadingState.LOADED)
            }
        }

    }
}
package com.paguelofacil.posfacil.ui.view.transactions.refund.viewmodel

import android.os.AsyncTask
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paguelofacil.posfacil.base.BaseViewModel
import com.paguelofacil.posfacil.data.network.request.RefundRequest
import com.paguelofacil.posfacil.data.network.response.RefundApiResponse
import com.paguelofacil.posfacil.model.RefundApiRequest
import com.paguelofacil.posfacil.model.RefundResponse
import com.paguelofacil.posfacil.model.TransactionRepo
import com.paguelofacil.posfacil.repository.UserRepo
import com.paguelofacil.posfacil.repository.refund.RefundRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class ReembolsoViewModel : ViewModel() {

    private val mutableRefundResponse = MutableLiveData<RefundApiResponse?>()
    val liveDataRefundResponse: LiveData<RefundApiResponse?> = mutableRefundResponse

    /*fun processRefund(idTransaction: String, amount: Double, description: String) {
        execute {
            viewModelScope.launch {
                val user = UserRepo.getUser()
                val refundRequest = RefundRequest(
                    idTransaction, amount, description, user.idMerchant ?: ""
                )
                val response = refundRepository.processRefund(refundRequest)
                processResponseResultado(response) {
                    mutableRefundResponse.postValue(it)
                }
            }
        }
    }*/

    suspend fun setRefund(request: RefundApiRequest, onSuccess: (RefundResponse)-> Unit, onFailure: (String)-> Unit){
        viewModelScope.launch {
            val response = TransactionRepo.setRefund(request)
            Timber.e("CODE ${response.headerStatus}")
            if (response.headerStatus.code == 200) {
                onSuccess(response)
            } else {
                onFailure("Algo salio mal ${response.headerStatus.code}")
            }
        }
    }
}
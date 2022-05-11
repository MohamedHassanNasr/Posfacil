package com.paguelofacil.posfacil.ui.view.transactions.payment.viewmodel

import android.os.AsyncTask.execute
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paguelofacil.posfacil.model.RefundApiRequest
import com.paguelofacil.posfacil.model.TransactionRepo
import com.paguelofacil.posfacil.repository.ReportRepo
import com.paguelofacil.posfacil.repository.UserRepo
import kotlinx.coroutines.launch
import timber.log.Timber

class DetailCobroViewModel: ViewModel() {

    suspend fun setRefund(request: RefundApiRequest, onSuccess: ()-> Unit, onFailure: (String)-> Unit){
        execute {
            viewModelScope.launch {
                val response = TransactionRepo.setRefund(request)
                Timber.e("CODE $response")
                if (response.headerStatus.code == 200){
                    onSuccess()
                }else{
                    onFailure("Algo salio mal ${response.headerStatus.code}")
                }
            }
        }
    }

}
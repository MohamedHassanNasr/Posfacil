package com.paguelofacil.posfacil.ui.view.transactions.payment.viewmodel

import android.os.AsyncTask.execute
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paguelofacil.posfacil.model.ComprobanteRequest
import com.paguelofacil.posfacil.model.TransactionRepo
import kotlinx.coroutines.launch

class ComprobanteViewModel: ViewModel() {

    fun sendComprobante(request: ComprobanteRequest, onSuccess: ()-> Unit, onFailure: (String)-> Unit){
        execute{
            viewModelScope.launch {
                val response = TransactionRepo.sendComprobante(
                    request = request,
                    isWithField = true
                )

                if (response?.headerStatus?.code == 200){
                    onSuccess()
                }else{
                    onFailure("Error ${response?.headerStatus?.code}")
                }
            }
        }
    }

}
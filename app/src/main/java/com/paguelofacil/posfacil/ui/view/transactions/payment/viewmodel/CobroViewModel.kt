package com.paguelofacil.posfacil.ui.view.transactions.payment.viewmodel

import android.os.AsyncTask
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.paguelofacil.posfacil.base.BaseViewModel
import com.paguelofacil.posfacil.data.entity.CobroEntity
import com.paguelofacil.posfacil.data.network.api.ApiEndpoints
import com.paguelofacil.posfacil.data.network.api.BaseResponse
import com.paguelofacil.posfacil.data.network.response.ReportZResponse
import com.paguelofacil.posfacil.model.ComprobanteRequest
import com.paguelofacil.posfacil.model.QRRequest
import com.paguelofacil.posfacil.model.QRResponse
import com.paguelofacil.posfacil.model.TransactionRepo
import com.paguelofacil.posfacil.repository.cobro.CobroRepository
import com.paguelofacil.posfacil.repository.qr.QRRepository
import com.paguelofacil.posfacil.util.Resultado
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class CobroViewModel @Inject constructor(
    private val cobroRepository: CobroRepository,
    private val qrRepository: QRRepository
) : BaseViewModel() {

    private val mutableDataCobro = MutableLiveData<CobroEntity?>()
    val liveDataCobro: LiveData<CobroEntity?> = mutableDataCobro

    private val mutableQRData = MutableLiveData<QRResponse?>()
    val liveDataQRData: LiveData<QRResponse?> = mutableQRData

    val mutableFlags = MutableLiveData<Map<String, Boolean>?>()

    val mutableCardNumberSuccess = MutableLiveData<String>();

    var importeCobro: Double = 0.0

    suspend fun getSystemUrlQr(
        amount: String,
        taxes: String,
        tip: String,
        onSuccess: (String, String) -> Unit,
        onFailure: (String) -> Unit
    ){
        cobroRepository.getSystem(
            amount = amount,
            taxes = taxes,
            tip = tip,
            onSuucces = {url, code->
                Timber.e("URL $url code $code")
                val urlOld = url
                val newUrl = urlOld.replace("{{QRCODE}}", code)
                Timber.e("NEW URL $newUrl")
                onSuccess(newUrl, code)
            },
            onFailure = {
                Timber.e("FAILURE SYSTEM")
                onFailure(it)
            }
        )
    }

    fun updateDataCobro(entity: CobroEntity) {
        mutableDataCobro.value = entity
    }

    fun saveDataCardFirestore(data: HashMap<String, Any>) {
        cobroRepository.saveDataCardFirestore(data)
    }

    fun getFlagsDeteccionTarjetaFirestore() {
        cobroRepository.getFlagsDeteccionTarjetaFirestore(mutableFlags)
    }

    fun getCodeQR(code: String) {
        execute {
            viewModelScope.launch {
                val response = qrRepository.getCodeQR("code::$code")
                processResponseResultado(response) {
                    mutableQRData.postValue(it)
                }
            }
        }
    }

    suspend fun sendComprobante(request: ComprobanteRequest, onSuccess: ()-> Unit, onFailure: (String)-> Unit){
        AsyncTask.execute {
            viewModelScope.launch {
                val response = TransactionRepo.sendComprobante(
                    request = request,
                    isWithField = true
                )

                if (response?.headerStatus?.code == 200) {
                    onSuccess()
                } else {
                    onFailure("Error ${response?.headerStatus?.code}")
                }
            }
        }
    }

    fun postCodeQR(request: QRRequest) {
        execute {
            viewModelScope.launch {
                val response = qrRepository.postCodeQR(request)
                processResponseResultado(response) {
                    mutableQRData.postValue(it)
                }
            }
        }
    }

}
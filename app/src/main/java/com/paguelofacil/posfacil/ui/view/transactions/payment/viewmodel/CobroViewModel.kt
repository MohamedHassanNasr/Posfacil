package com.paguelofacil.posfacil.ui.view.transactions.payment.viewmodel

import android.os.AsyncTask
import android.provider.Settings
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.paguelofacil.posfacil.ApplicationClass
import com.paguelofacil.posfacil.base.BaseViewModel
import com.paguelofacil.posfacil.data.entity.CobroEntity
import com.paguelofacil.posfacil.data.network.api.ApiEndpoints
import com.paguelofacil.posfacil.data.network.api.BaseResponse
import com.paguelofacil.posfacil.data.network.response.ReportZResponse
import com.paguelofacil.posfacil.model.*
import com.paguelofacil.posfacil.repository.UserRepo
import com.paguelofacil.posfacil.repository.cobro.CobroRepository
import com.paguelofacil.posfacil.repository.qr.QRRepository
import com.paguelofacil.posfacil.tools.TransaccionResponse
import com.paguelofacil.posfacil.tools.TransactionRequest
import com.paguelofacil.posfacil.tools.convertStringToHex
import com.paguelofacil.posfacil.util.Resultado
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
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

    private val error = CoroutineExceptionHandler{ _, exception ->
        Timber.e("Error ${exception.message.toString()}")
    }

    private val mutableDataCobro = MutableLiveData<CobroEntity?>()
    val liveDataCobro: LiveData<CobroEntity?> = mutableDataCobro

    private val mutableQRData = MutableLiveData<QRResponse?>()
    val liveDataQRData: LiveData<QRResponse?> = mutableQRData

    val mutableFlags = MutableLiveData<Map<String, Boolean>?>()

    val mutableCardNumberSuccess = MutableLiveData<String>();

    private val _qrResponse = MutableLiveData<Triple<Boolean, String?, String?>>()
        val qrRespose: LiveData<Triple<Boolean, String?, String?>> get() = _qrResponse

    private val _qrVerifyResponse = MutableLiveData<Boolean>()
        val qrVerifyResponse: LiveData<Boolean> get() = _qrVerifyResponse

    var importeCobro: Double = 0.0

    private var _statusResponseTx = MutableLiveData<ModelResponseTx>()
    val statusResponseTx: LiveData<ModelResponseTx>
        get() = _statusResponseTx

    suspend fun getSystemUrlQr(
        amount: String,
        taxes: String,
        tip: String,
        tx: String,
        idSearch: String
    ){
        cobroRepository.getSystem(
            amount = amount,
            taxes = taxes,
            tip = tip,
            tx,
            idSearch = idSearch,
            onSuucces = {url, code->
                Timber.e("URL $url code $code")
                val urlOld = url
                val newUrl = urlOld.replace("{{QRCODE}}", code)
                Timber.e("NEW URL $newUrl")
                _qrResponse.postValue(Triple(true, newUrl, code))
            },
            onFailure = {
                Timber.e("FAILURE SYSTEM")
                _qrResponse.postValue(Triple(true, it, null))
            }
        )
    }

    fun updateDataCobro(entity: CobroEntity) {
        mutableDataCobro.value = entity
    }

    fun saveDataCardFirestore(data: HashMap<String, Any?>) {
        cobroRepository.saveDataCardFirestore(data)
    }

    fun saveDataTracksFirestore(data: HashMap<String, Any?>) {
        cobroRepository.saveTracksFirestore(data)
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

    suspend fun setTransaction(transactionRequest: TransactionRequest){
        execute {
            viewModelScope.launch {
                cobroRepository.setTransaction(
                    transactionRequest = transactionRequest,
                    onSuucces = {
                        _statusResponseTx.postValue(ModelResponseTx(true, null, null))
                    },
                    onFailure = {
                        _statusResponseTx.postValue(ModelResponseTx(false, it, transactionRequest))
                    }
                )
            }
        }
    }

    suspend fun loadUtilsQr(email: String, password: String, codeByJson: GetSearchCodeByJson, onFailure: (String) -> Unit){
        val response = qrRepository.loadUtilsQr()

        if (response != null){
            Timber.e("SUCCES CONSULTAS QR, 1/4")
            loginQr(
                email,
                password,
                codeByJson = codeByJson,
                onFailure = {
                    onFailure(it)
                }
            )
        }else{
            onFailure(ApplicationClass.language.error)
        }
    }

    private suspend fun loginQr(email: String, password: String, codeByJson: GetSearchCodeByJson, onFailure: (String) -> Unit){
        val body = HashMap<String, Any>()

        val user = UserRepo.getUser()
        body["user"] = email
        body["password"] = password
        body["fcmToken"] = user.fcmToken ?: ""

        val response = qrRepository.loginWallet(ApiEndpoints.LOGIN, body)

        if (response.headerStatus.code == 200){
            Timber.e("SUCCES CONSULTAS QR, 2/4")
            searchActivity(
                codeByJson = codeByJson,
                onFailure = {
                    onFailure(it)
                }
            )
        }else{
            onFailure(response.headerStatus.description)
        }
    }

    private suspend fun searchActivity(codeByJson: GetSearchCodeByJson, onFailure: (String) -> Unit){
        /**
         * GetSearchCodeByJson(
        type = "POS",
        idSearch = "556ASDF65AFSDF5",
        amount = 7.30,
        discount = 0.0,
        taxes = 0.0,
        currency = null,
        description = "Test-POS",
        others = GetSearchCodeByJson.Others(
        txChannel = "PWA",
        idUser = 67823,
        idMerchant = 28,
        tip = 1
        )
        )
         * */
        Timber.e("HEXXXXXXXXXX ${
            convertStringToHex(
                Gson().toJson(
                    codeByJson
                )
            )
        }")
        //val response = qrRepository.searchActivity("code%3A%3A${convertStringToHex(Gson().toJson(codeByJson))}")
        execute {
            viewModelScope.launch(Dispatchers.IO + error) {
                val response = qrRepository.searchActivity("code%3A%3A7b0d0a092274797065223a2022504f53222c0d0a09226964536561726368223a202231373630303130323933222c0d0a0922616d6f756e74223a2031312e30302c0d0a0922646973636f756e74223a20302e302c0d0a09227461786573223a20302e302c0d0a092263757272656e6379223a206e756c6c2c0d0a09226465736372697074696f6e223a2022546573742d504f53222c0d0a09226f7468657273223a207b0d0a09092274784368616e6e656c223a2022505741222c0d0a090922696455736572223a2036383734342c0d0a09092269644d65726368616e74223a2032373737392c0d0a090922746970223a20310d0a097d0d0a7d")
                /*processResponseResultado(response) {
                    it?.let {response->
                        if (response.headerStatus.code == 200){
                            Timber.e("SUCCES CONSULTAS QR, 3/4")
                            viewModelScope.launch {
                                postPayActivity(
                                    payload = PayLoadModel(
                                        idUsrCard = 1269,
                                        idUsrService = 1301,
                                        sourceSearch = response.data.sourceSearch,
                                        txChannel = "PWA",
                                        useFunds = false
                                    ),
                                    onFailure = {
                                        onFailure(it)
                                    }
                                )
                            }
                        }else{
                            onFailure(response.headerStatus.description)
                        }
                    }
                }*/
                if (response.headerStatus.code == 200){
                    Timber.e("SUCCES CONSULTAS QR, 3/4")
                    postPayActivity(
                        payload = PayLoadModel(
                            idUsrCard = 1269,
                            idUsrService = 1301,
                            sourceSearch = response.data.sourceSearch,
                            txChannel = "PWA",
                            useFunds = false
                        ),
                        onFailure = {
                            onFailure(it)
                        }
                    )
                }else{
                    onFailure(response.headerStatus.description)
                }
            }
        }


        /*if (response.headerStatus.code == 200){
            Timber.e("SUCCES CONSULTAS QR, 3/4")
            postPayActivity(
                payload = PayLoadModel(
                    idUsrCard = 1269,
                    idUsrService = 1301,
                    sourceSearch = response.data.sourceSearch,
                    txChannel = "PWA",
                    useFunds = false
                ),
                onFailure = {
                    onFailure(it)
                }
            )
        }else{
            onFailure(response.headerStatus.description)
        }*/
    }

    private suspend fun postPayActivity(payload: PayLoadModel, onFailure: (String) -> Unit){
        Timber.e("Hexxx payload ${
            convertStringToHex(
                Gson().toJson(
                    payload
                )
            )
        }")

        val response = qrRepository.payActivity(convertStringToHex(Gson().toJson(payload)))

        if (response.headerStatus.code == 200){
            Timber.e("SUCCES CONSULTAS QR, 4/4")
            Timber.e("TODAS LAS CONSULTAS DE QR ESTAN LISTA, QR REGISTRADO")
        }else{
            onFailure(response.headerStatus.description)
        }
    }

    suspend fun verificarQr(tx: String){
        execute {
            viewModelScope.launch(Dispatchers.IO + error) {
                val response = qrRepository.verificarQr("code::$tx")
                if (response.headerStatus.code == 200){
                    _qrVerifyResponse.postValue(true)
                }else{
                    _qrVerifyResponse.postValue(false)
                }
                /*processResponseResultado(response) {
                    it?.let {response->
                        if (response.headerStatus.code == 200){
                            Timber.e("SUCCES VERIFICANDO QR")

                        }else{

                        }
                    }
                }*/
            }
        }
    }

}
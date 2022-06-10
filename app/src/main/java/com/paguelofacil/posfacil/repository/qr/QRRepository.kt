package com.paguelofacil.posfacil.repository.qr

import com.paguelofacil.posfacil.data.network.api.BaseResponse
import com.paguelofacil.posfacil.data.network.response.ReportZResponse
import com.paguelofacil.posfacil.model.*
import com.paguelofacil.posfacil.repository.cobro.CobroService
import com.paguelofacil.posfacil.util.Resultado
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class QRRepository  @Inject constructor(val qrService: QRService) {

    suspend fun getCodeQR(conditional:String): Resultado<BaseResponse<QRResponse>> {
        return withContext(Dispatchers.IO) { qrService.getCodeQR(conditional) }
    }

    suspend fun postCodeQR(request:QRRequest): Resultado<BaseResponse<QRResponse>> {
        return withContext(Dispatchers.IO) { qrService.postCodeQR(request) }
    }

    suspend fun loadUtilsQr(): UtilsQrResponse?{
        return withContext(Dispatchers.IO) { qrService.getUtilsQr() }
    }

    suspend fun loginWallet(url: String, body: HashMap<String, Any>): LoginWalletResponse {
        return withContext(Dispatchers.IO) { qrService.loginWallet(url, body) }
    }

    suspend fun searchActivity(conditional: String/*url: String, body: HashMap<String, Any>*/): SearchActivityResponse {
        Timber.e("CONDICTIONAA $conditional")
        return withContext(Dispatchers.IO) { qrService.searchActivity(conditional) }
    }

    suspend fun payActivity(payload: String): PayActivityResponse {
        return withContext(Dispatchers.IO) { qrService.postPayActivity(payload) }
    }

    suspend fun search(conditional:String): Resultado<BaseResponse<SearchActivityResponse>> {
        return withContext(Dispatchers.IO) { qrService.search(conditional) }
    }

    suspend fun verificarQr(tx: String): StatusQrResponse{
        return withContext(Dispatchers.IO) { qrService.verificar(tx) }
    }
}
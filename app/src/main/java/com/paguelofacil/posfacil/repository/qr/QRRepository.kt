package com.paguelofacil.posfacil.repository.qr

import com.paguelofacil.posfacil.data.network.api.BaseResponse
import com.paguelofacil.posfacil.data.network.response.ReportZResponse
import com.paguelofacil.posfacil.model.QRRequest
import com.paguelofacil.posfacil.model.QRResponse
import com.paguelofacil.posfacil.repository.cobro.CobroService
import com.paguelofacil.posfacil.util.Resultado
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class QRRepository  @Inject constructor(val qrService: QRService) {

    suspend fun getCodeQR(conditional:String): Resultado<BaseResponse<QRResponse>> {
        return withContext(Dispatchers.IO) { qrService.getCodeQR(conditional) }
    }

    suspend fun postCodeQR(request:QRRequest): Resultado<BaseResponse<QRResponse>> {
        return withContext(Dispatchers.IO) { qrService.postCodeQR(request) }
    }

}
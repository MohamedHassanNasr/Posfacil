package com.paguelofacil.posfacil.repository.qr

import com.paguelofacil.posfacil.data.network.api.ApiEndpoints
import com.paguelofacil.posfacil.data.network.api.BaseResponse
import com.paguelofacil.posfacil.data.network.response.TransactionApiResponse
import com.paguelofacil.posfacil.model.QRRequest
import com.paguelofacil.posfacil.model.QRResponse
import com.paguelofacil.posfacil.util.Resultado
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface QRService {

    @GET(ApiEndpoints.QR_PROCESS_INFO)
    suspend fun getCodeQR(@Path("conditional") code: String): Resultado<BaseResponse<QRResponse>>

    @POST(ApiEndpoints.QR_PROCESS_INFO)
    suspend fun postCodeQR(@Body request: QRRequest): Resultado<BaseResponse<QRResponse>>

}
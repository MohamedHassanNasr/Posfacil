package com.paguelofacil.posfacil.repository.refund

import com.paguelofacil.posfacil.data.network.api.ApiEndpoints
import com.paguelofacil.posfacil.data.network.api.BaseResponse
import com.paguelofacil.posfacil.data.network.request.RefundRequest
import com.paguelofacil.posfacil.data.network.response.RefundApiResponse
import com.paguelofacil.posfacil.util.Resultado
import retrofit2.http.Body
import retrofit2.http.POST


interface RefundService {

    @POST(ApiEndpoints.REFUND_TX)
    suspend fun processRefund(@Body request: RefundRequest = RefundRequest()): Resultado<BaseResponse<RefundApiResponse>>

}
package com.paguelofacil.posfacil.repository.report

import com.paguelofacil.posfacil.data.network.api.ApiEndpoints
import com.paguelofacil.posfacil.data.network.api.BaseResponse
import com.paguelofacil.posfacil.data.network.request.ReportZRequest
import com.paguelofacil.posfacil.data.network.response.ReportZResponse
import com.paguelofacil.posfacil.util.Resultado
import retrofit2.http.Body
import retrofit2.http.POST

interface ReportService {

    @POST(ApiEndpoints.POS_STATUS)
    suspend fun reportZ(@Body request: ReportZRequest = ReportZRequest()): Resultado<BaseResponse<ReportZResponse>>

}
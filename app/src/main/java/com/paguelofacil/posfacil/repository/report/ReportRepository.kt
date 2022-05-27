package com.paguelofacil.posfacil.repository.report

import com.paguelofacil.posfacil.ApplicationClass
import com.paguelofacil.posfacil.data.network.api.BaseResponse
import com.paguelofacil.posfacil.data.network.request.ReportZRequest
import com.paguelofacil.posfacil.data.network.response.ReportZResponse
import com.paguelofacil.posfacil.util.Resultado
import com.pax.dal.ISys
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ReportRepository @Inject constructor(private val reportService: ReportService) {

    suspend fun checkReportZ(Sys: String): Resultado<BaseResponse<ReportZResponse>> {
        return withContext(Dispatchers.IO) { reportService.reportZ(ReportZRequest(Sys)) }
    }

    suspend fun generateReportZ(request: ReportZRequest): Resultado<BaseResponse<ReportZResponse>> {
        return withContext(Dispatchers.IO) { reportService.reportZ(request) }
    }

}
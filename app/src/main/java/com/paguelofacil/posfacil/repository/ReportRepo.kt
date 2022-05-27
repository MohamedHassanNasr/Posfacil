package com.paguelofacil.posfacil.repository

import com.paguelofacil.posfacil.BuildConfig.API_BASE_URL
import com.paguelofacil.posfacil.base.BaseRepo
import com.paguelofacil.posfacil.data.network.api.ApiEndpoints
import com.paguelofacil.posfacil.data.network.api.ApiRequestCode
import com.paguelofacil.posfacil.data.network.api.BaseResponse
import com.paguelofacil.posfacil.data.network.api.RetrofitClient
import com.paguelofacil.posfacil.model.MerchantResponse
import com.paguelofacil.posfacil.model.ReportXResponse
import com.paguelofacil.posfacil.model.ReporteVentaResponse
import com.paguelofacil.posfacil.util.Resultado
import com.pax.dal.ISys
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject


/**
 * Repository for Report related API calls
 *
 * @constructor Create empty Report repo
 */
object ReportRepo : BaseRepo() {

    /**
     * Check Z report
     *
     * @return
     */
    suspend fun checkZReport(): BaseResponse<Any> {
        val body = HashMap<String, Any>()

        body["serial"] = "test-atik-dev-serial"
        return apiRequest(ApiRequestCode.SUCCESS) {
            remoteDao.post(ApiEndpoints.POS_STATUS, body)
        }
    }

    suspend fun getReporte(Sys: String): ReporteVentaResponse {
        val body = HashMap<String, Any>()

        body["serial"] = Sys
        //url https://middle-test.pfserver.net/PFManagementServices/api/v2/PosReport/test-atik-dev-serial
        return remoteDao.getSpecify("$API_BASE_URL/${ApiEndpoints.REPORTS_SELL}/${Sys}")
    }

    suspend fun getReporteX(email: String, Sys: ISys): ReportXResponse {
        val body = HashMap<String, Any>()
        body["command"] = "X"
        body["serial"] = Sys.baseInfo.sn
        body["email"] = email

        return remoteDao.postReportX(ApiEndpoints.POS_COMMAND,body)
    }

    suspend fun getReporteZ(email: String, Sys: ISys): ReportXResponse {
        val body = HashMap<String, Any>()
        body["command"] = "Z"
        body["serial"] = Sys.baseInfo.sn
        body["email"] = email

        return remoteDao.postReportX(ApiEndpoints.POS_COMMAND,body)
    }
}
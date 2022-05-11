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

    suspend fun getReporte(): ReporteVentaResponse {
        val body = HashMap<String, Any>()

        body["serial"] = ApiEndpoints.ATIK_SERIAL
        //url https://middle-test.pfserver.net/PFManagementServices/api/v2/PosReport/test-atik-dev-serial
        return remoteDao.getSpecify("$API_BASE_URL/${ApiEndpoints.REPORTS_SELL}/${ApiEndpoints.ATIK_SERIAL}")
    }

    suspend fun getReporteX(email: String): ReportXResponse {
        val body = HashMap<String, Any>()
        body["command"] = "X"
        body["serial"] = ApiEndpoints.ATIK_SERIAL
        body["email"] = email

        return remoteDao.postReportX(ApiEndpoints.POS_COMMAND,body)
    }

    suspend fun getReporteZ(email: String): ReportXResponse {
        val body = HashMap<String, Any>()
        body["command"] = "Z"
        body["serial"] = ApiEndpoints.ATIK_SERIAL
        body["email"] = email

        return remoteDao.postReportX(ApiEndpoints.POS_COMMAND,body)
    }
}
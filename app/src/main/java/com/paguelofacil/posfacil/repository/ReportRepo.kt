package com.paguelofacil.posfacil.repository

import com.paguelofacil.posfacil.base.BaseRepo
import com.paguelofacil.posfacil.data.network.api.ApiEndpoints
import com.paguelofacil.posfacil.data.network.api.ApiRequestCode
import com.paguelofacil.posfacil.data.network.api.BaseResponse


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
}
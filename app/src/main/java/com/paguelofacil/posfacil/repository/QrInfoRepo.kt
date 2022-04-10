package com.paguelofacil.posfacil.repository

import com.paguelofacil.posfacil.base.BaseRepo
import com.paguelofacil.posfacil.data.network.api.ApiEndpoints
import com.paguelofacil.posfacil.data.network.api.ApiRequestCode
import com.paguelofacil.posfacil.data.network.api.BaseResponse

/**
 * Repository for QrInfo related API calls
 *
 * @constructor Create empty QrInfo repo
 */
object QrInfoRepo : BaseRepo() {

    /**
     * Process QR Info
     *
     * @return
     */
    suspend fun process(): BaseResponse<Any> {
        return apiRequest(ApiRequestCode.SUCCESS) {
            remoteDao.get(ApiEndpoints.QR_PROCESS_INFO)
        }
    }
}
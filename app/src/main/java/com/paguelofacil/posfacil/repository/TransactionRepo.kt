package com.paguelofacil.posfacil.repository

import com.paguelofacil.posfacil.base.BaseRepo
import com.paguelofacil.posfacil.data.network.api.ApiEndpoints
import com.paguelofacil.posfacil.data.network.api.ApiRequestCode
import com.paguelofacil.posfacil.data.network.api.BaseResponse

/**
 * Repository for Transaction related API calls
 *
 * @constructor Create empty Transaction repo
 */
object TransactionRepo : BaseRepo() {

    /**
     * Get transactions filtered by datetime
     *
     * @param conditional operation dateTms
     */
    suspend fun getAllTransactions(): BaseResponse<Any> {

        return apiRequest(ApiRequestCode.SUCCESS) {
            remoteDao.get(ApiEndpoints.V_ADMIN_TXS)
        }
    }
}
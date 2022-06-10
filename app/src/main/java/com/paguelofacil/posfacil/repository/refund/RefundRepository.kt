package com.paguelofacil.posfacil.repository.refund

import com.paguelofacil.posfacil.data.network.api.BaseResponse
import com.paguelofacil.posfacil.data.network.request.RefundRequest
import com.paguelofacil.posfacil.data.network.response.RefundApiResponse
import com.paguelofacil.posfacil.util.Resultado
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Repository for Refund related API calls
 *
 * @constructor Create empty Refund repository
 */

class RefundRepository @Inject constructor(private val refundService: RefundService) {
    /**
     * Process refund
     *
     * @param refundRequest data related to previous transaction to refund
     */
    suspend fun processRefund(refundRequest: RefundRequest): Resultado<BaseResponse<RefundApiResponse>> {
        return withContext(Dispatchers.IO) { refundService.processRefund(refundRequest) }
    }
}
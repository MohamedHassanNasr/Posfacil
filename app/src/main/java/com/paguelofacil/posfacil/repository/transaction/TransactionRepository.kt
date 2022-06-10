package com.paguelofacil.posfacil.repository.transaction

import com.paguelofacil.posfacil.data.network.api.ApiEndpoints
import com.paguelofacil.posfacil.data.network.api.BaseResponse
import com.paguelofacil.posfacil.data.network.response.TransactionApiResponse
import com.paguelofacil.posfacil.util.Resultado
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Repository for Transaction related API calls
 *
 * @constructor Create empty Transaction repository
 */

class TransactionRepository @Inject constructor(private val transactionService: TransactionService) {

    /**
     * Get transactions filtered by datetime
     *
     * @param conditional operation dateTms
     */
    suspend fun getAllTransactions(serial: String): Resultado<BaseResponse<MutableList<TransactionApiResponse>>> {
        return withContext(Dispatchers.IO) { transactionService.getAllTransactions(serial) }
    }
}
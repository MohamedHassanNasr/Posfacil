package com.paguelofacil.posfacil.repository.transaction

import com.paguelofacil.posfacil.data.network.api.ApiEndpoints
import com.paguelofacil.posfacil.data.network.api.BaseResponse
import com.paguelofacil.posfacil.data.network.response.TransactionApiResponse
import com.paguelofacil.posfacil.util.Resultado
import retrofit2.http.GET
import retrofit2.http.Path

interface TransactionService {

    @GET(ApiEndpoints.POS_TRANSACTION)
    suspend fun getAllTransactions(@Path("serial") serial: String = "test-atik-dev-serial"): Resultado<BaseResponse<MutableList<TransactionApiResponse>>>

}
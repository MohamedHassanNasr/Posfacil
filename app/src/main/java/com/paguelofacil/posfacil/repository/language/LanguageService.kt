package com.paguelofacil.posfacil.repository.language

import com.paguelofacil.posfacil.data.network.api.ApiEndpoints
import com.paguelofacil.posfacil.data.network.api.BaseResponse
import com.paguelofacil.posfacil.data.network.response.TransactionApiResponse
import com.paguelofacil.posfacil.model.LanguageDataResponse
import com.paguelofacil.posfacil.model.QRRequest
import com.paguelofacil.posfacil.model.QRResponse
import com.paguelofacil.posfacil.util.Resultado
import retrofit2.http.*

interface LanguageService {

    @GET(ApiEndpoints.LANGUAGE_FILES)
    suspend fun getLanguage(
        @Query(
            "conditional", encoded = true
        ) language: String
    ): Resultado<BaseResponse<LanguageDataResponse>>

}
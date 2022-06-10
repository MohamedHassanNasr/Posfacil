package com.paguelofacil.posfacil.repository

import com.paguelofacil.posfacil.data.network.api.ApiEndpoints
import com.paguelofacil.posfacil.data.network.api.BaseResponse
import com.paguelofacil.posfacil.model.ReporteVentaResponse
import com.paguelofacil.posfacil.util.Resultado
import retrofit2.http.GET

interface ReporteVentasService {

    @GET("https://middle-test.pfserver.net/PFManagementServices/api/v2/PosReport/test-atik-dev-serial")
    suspend fun getReporteVentas(): ReporteVentaResponse
}
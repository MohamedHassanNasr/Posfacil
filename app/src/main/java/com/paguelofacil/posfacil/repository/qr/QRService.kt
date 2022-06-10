package com.paguelofacil.posfacil.repository.qr

import com.paguelofacil.posfacil.data.network.api.ApiEndpoints
import com.paguelofacil.posfacil.data.network.api.BaseResponse
import com.paguelofacil.posfacil.data.network.response.TransactionApiResponse
import com.paguelofacil.posfacil.model.*
import com.paguelofacil.posfacil.util.Resultado
import retrofit2.http.*

interface QRService {

    @GET(ApiEndpoints.QR_PROCESS_INFO)
    suspend fun getCodeQR(@Path("conditional") code: String): Resultado<BaseResponse<QRResponse>>

    @POST(ApiEndpoints.QR_PROCESS_INFO)
    suspend fun postCodeQR(@Body request: QRRequest): Resultado<BaseResponse<QRResponse>>

    @GET(ApiEndpoints.LOAD_UTILS_QR)
    suspend fun getUtilsQr(): UtilsQrResponse?

    @POST("{url}")
    suspend fun loginWallet(
        @Path("url", encoded = true) url: String,
        @Body body: HashMap<String, Any>,
    ): LoginWalletResponse

    @GET(ApiEndpoints.SEARCH_ACTIVITY_EP)
    suspend fun searchActivity(
        /*@Path("url", encoded = true) url: String,
        @QueryMap queryMap: HashMap<String, Any>*/
        @Query("conditional") code: String
    ): SearchActivityResponse

    @GET(ApiEndpoints.SEARCH_ACTIVITY_EP + "{conditional}")
    suspend fun search(@Path("conditional") code: String): Resultado<BaseResponse<SearchActivityResponse>>

    @POST(ApiEndpoints.QR_PROCESS_INFO)
    suspend fun postPayActivity(@Body request: String): PayActivityResponse

    @GET(ApiEndpoints.VERIFY_QR + "{filter}")
    suspend fun verificarQr(@Path("filter") tx: String): Resultado<BaseResponse<StatusQrResponse>>

    @GET(ApiEndpoints.VERIFY_QR)
    suspend fun verificar(
        /*@Path("url", encoded = true) url: String,
        @QueryMap queryMap: HashMap<String, Any>*/
        @Query("filter") code: String
    ): StatusQrResponse

}
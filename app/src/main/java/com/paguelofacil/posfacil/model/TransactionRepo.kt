package com.paguelofacil.posfacil.model

import com.paguelofacil.posfacil.base.BaseRepo
import com.paguelofacil.posfacil.data.network.api.ApiEndpoints
import com.paguelofacil.posfacil.data.network.api.BaseResponse
import com.paguelofacil.posfacil.repository.ReportRepo
import retrofit2.Response

object TransactionRepo: BaseRepo() {

    suspend fun setRefund(request: RefundApiRequest): RefundResponse {
        return remoteDao.setRefund(url = ApiEndpoints.PROCESS_TX, request = request)
    }

    suspend fun sendComprobante(request: ComprobanteRequest? = null, isWithField: Boolean, requestNoOptionRequest: ComprobanteNoOptionRequest? = null): ComprobanteResponse?{
        return if (isWithField){
            request?.let {
                remoteDao.sendComprobante(url = ApiEndpoints.COMPROBANTE, request = request)
            }
        }else{
            requestNoOptionRequest?.let {
                remoteDao.sendComprobanteNoOption(url = ApiEndpoints.COMPROBANTE_NO_OPTION, request = requestNoOptionRequest)
            }
        }
    }
}
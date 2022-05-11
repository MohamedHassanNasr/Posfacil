package com.paguelofacil.posfacil.model

import com.paguelofacil.posfacil.data.network.api.ApiEndpoints

data class RefundApiRequest(
    val amount: Double,
    val taxAmount: Double,
    val isOldVersion: Boolean = false,
    val email: String,
    val phone: String,
    val concept: String,
    val description: String,
    val transactionsType: String = "REVERSE_AUTH",
    val idMerchantService: Long,
    val idMerchant: Long,
    val codOperRelatedTransaction: String,
    val additionalData: AddionalData
)

data class AddionalData(
    val pos: PosAddionalData
)

data class PosAddionalData(
    val serial: String = ApiEndpoints.ATIK_SERIAL,
    val idUser: Long,
    val idMerchant: Long
)

data class RefundResponse(
    val headerStatus: HeaderStatus,
    val serverTime: String,
    val data: DataRefund
)

data class DataRefund(
    val date: String,
    val relatedTx: String,
    val totalPay: String,
    val cardType: String,
    val codOper: String
)
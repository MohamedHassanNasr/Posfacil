package com.paguelofacil.posfacil.model

data class MerchantResponse(
    val headerStatus: HeaderStatus,
    val serverTime: String,
    val message: String?,
    val requestId: String,
    val data: List<DataMerchant>,
    val success: Boolean
)

data class DataMerchant(
    val idMerchant: Long,
    val merchantName: String,
    val legalName: String,
    val address: String,
    val phone: String,
    val mobile: String,
    val email: String
)
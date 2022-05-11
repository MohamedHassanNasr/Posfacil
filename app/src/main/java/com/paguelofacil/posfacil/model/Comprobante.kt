package com.paguelofacil.posfacil.model

data class ComprobanteNoOptionRequest(
    val idtx: Long,
    val lang: String = "ES",
    val codOper: String
)

data class ComprobanteResponse(
    val headerStatus: HeaderStatus,
    val serverTime: String
)

data class ComprobanteRequest(
    val codOper: String,
    val email: String,
    val phone: String,
    val lang: String = "ES"
)
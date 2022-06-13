package com.paguelofacil.posfacil.model

data class StatusQrResponse(
    val data: List<Any>,
    val headerStatus: HeaderStatus,
    val message: String?,
    val requestId: String,
    val serverTime: String,
    val success: Boolean
) {
    data class HeaderStatus(
        val code: Int,
        val description: String
    )
}
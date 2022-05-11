package com.paguelofacil.posfacil.data.network.request

data class ReportZRequest(
    val serial: String = "test-atik-dev-serial",
    val generate: Boolean = false,
    val command: String? = if (generate) "Z" else null,
    val email: String? = null
)
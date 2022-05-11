package com.paguelofacil.posfacil.model

data class ReporteVentaResponse(
    val headerStatus: HeaderStatus,
    val serverTime: String,
    val data: DataReporte
)

data class HeaderStatus(
    val code: Int,
    val description: String
)

data class DataReporte(
    val txs: Int,
    val amount: Double,
    val refundsTxs: Int,
    val refunds: Double,
    val total: Double,
    val paymentMethods: List<PaymentMethods>,
    val operatorsTxs: List<OperatorsTxs>,
    val success: Boolean
)

data class PaymentMethods(
    val name: String,
    val total: Double
)

data class OperatorsTxs(
    val idUser: Long,
    val name: String,
    val txs: Int,
    val total: Double
)
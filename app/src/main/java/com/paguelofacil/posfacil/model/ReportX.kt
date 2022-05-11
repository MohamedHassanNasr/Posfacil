package com.paguelofacil.posfacil.model

data class ReportXResponse(
    val headerStatus: HeaderStatus,
    val serverTime: String,
    val requestId: String,
    val data: DataReportX
)

data class DataReportX(
    val command: String,
    val global: GlobalReportX,
    val paymentMethods: List<PaymentMethodsX>,
    val operators: List<Operators>,
    val txs: List<Txs>,
    val success: Boolean
)

data class GlobalReportX(
    val txs: Int,
    val sells: Double,
    val taxes: Double,
    val tips: Double,
    val refunds: Double,
    val total: Double
)

data class PaymentMethodsX(
    val name: String,
    val txs: Int,
    val sells: Double,
    val taxes: Double,
    val tips: Double,
    val refunds: Double,
    val total: Double
)

data class Operators(
    val id: Long,
    val operator: String,
    val global: GlobalReportX,
    val paymentMethods: List<PaymentMethodsX>
)

data class Txs(
    val idTx: Long,
    val codOper: String,
    val amount: Double,
    val tax: Double,
    val tip: Double,
    val date: String,
    val status: String,
    val cardType: String,
    val cardNumber: String,
    val operatorId: Long
)

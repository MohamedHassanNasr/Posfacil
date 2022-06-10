package com.paguelofacil.posfacil.model

data class TransactionReportX(
    val id: String,
    val amountGan: Double,
    val date: String,
    val paymentMethod: String,
    val cardNumber: String
)

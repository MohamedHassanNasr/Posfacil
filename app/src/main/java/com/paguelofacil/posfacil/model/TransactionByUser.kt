package com.paguelofacil.posfacil.model

data class TransactionByUser(
    val name: String,
    val transactionSize: Int,
    val amount: Double,
    val color: Int? = null
)

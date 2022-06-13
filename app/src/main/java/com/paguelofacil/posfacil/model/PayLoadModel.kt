package com.paguelofacil.posfacil.model

data class PayLoadModel(
    val idUsrCard: Int,
    val idUsrService: Int,
    val sourceSearch: String,
    val txChannel: String,
    val useFunds: Boolean
)
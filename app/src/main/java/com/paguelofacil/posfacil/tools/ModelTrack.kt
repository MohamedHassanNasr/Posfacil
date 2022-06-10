package com.paguelofacil.posfacil.tools

data class ModelTrack(
    val cardNumber: String,
    val cardHolder: String? = null,
    val dateExpiry: String,
    val cvv: String,
    val dataExtra: String
)
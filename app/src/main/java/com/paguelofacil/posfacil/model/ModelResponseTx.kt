package com.paguelofacil.posfacil.model

import com.paguelofacil.posfacil.tools.ModelTrack
import com.paguelofacil.posfacil.tools.TransactionRequest

data class ModelResponseTx(
    val status: Boolean,
    val message: String?,
    val track: TransactionRequest?
)

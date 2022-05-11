package com.paguelofacil.posfacil.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TransactionBundle(
    val opCode: String,
    val amount: String,
    val cardNumber: String,
    val cardType: String,
): Parcelable

@Parcelize
data class RefundResult(
    val opCode: String,
    val amount: String,
    val cardNumber: String,
    val cardType: String,
    val date: String,
    val motivo: String
): Parcelable
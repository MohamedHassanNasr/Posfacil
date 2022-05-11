package com.paguelofacil.posfacil.data.network.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TransactionApiResponse(
    @field:SerializedName("idTx")
    val idTx: Int? = null,

    @field:SerializedName("amount")
    val amount: Double? = null,

    @field:SerializedName("tax")
    val tax: Double? = null,

    @field:SerializedName("tip")
    val tip: Double? = null,

    @field:SerializedName("codOper")
    val codOper: String? = null,

    @field:SerializedName("status")
    val status: String? = null,

    @field:SerializedName("date")
    val date: String? = null,

    @field:SerializedName("operatorId")
    val operatorId: Int? = null,

    @field:SerializedName("cardNumber")
    val cardNumber: String? = null,

    @field:SerializedName("cardType")
    val cardType: String? = null
) : Parcelable
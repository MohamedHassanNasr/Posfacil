package com.paguelofacil.posfacil.data.network.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class RefundApiResponse (

    @field:SerializedName("cardType")
    val cardType: String? = null,

    @field:SerializedName("displayNum")
    val displayNum: String? = null,

    @field:SerializedName("totalPay")
    val totalPay: String? = null,

    @field:SerializedName("inRevision")
    val inRevision: Boolean? = null,

    @field:SerializedName("requestPayAmount")
    val requestPayAmount: Double? = null,

    @field:SerializedName("status")
    val status: String? = null,

) : Parcelable
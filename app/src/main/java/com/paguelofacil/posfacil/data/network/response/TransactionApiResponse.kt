package com.paguelofacil.posfacil.data.network.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TransactionApiResponse(
    @field:SerializedName("idTransaction")
    val idTransaction: Int? = null,

    @field:SerializedName("amount")
    val amount: Float? = null,

    @field:SerializedName("codOper")
    val codOper: String? = null,

    @field:SerializedName("status")
    val status: String? = null,

    @field:SerializedName("dateTms")
    val dateTms: String? = null,

    @field:SerializedName("merchantName")
    val merchantName: String? = null,

    //
    // many fields
    //

    @field:SerializedName("cardVisibleNum")
    val cardVisibleNum: String? = null,
) : Parcelable
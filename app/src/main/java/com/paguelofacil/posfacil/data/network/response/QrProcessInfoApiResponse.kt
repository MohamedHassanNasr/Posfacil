package com.paguelofacil.posfacil.data.network.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class QrProcessInfoApiResponse(
    @field:SerializedName("code")
    val code: String? = null,

    @field:SerializedName("type")
    val type: String? = null,

    @field:SerializedName("processTx")
    val processTx: String? = null,

    @field:SerializedName("qrInfo")
    val qrInfo: String? = null,

    @field:SerializedName("expireDate")
    val expireDate: String? = null,

    @field:SerializedName("_id")
    val _id: String? = null
) : Parcelable
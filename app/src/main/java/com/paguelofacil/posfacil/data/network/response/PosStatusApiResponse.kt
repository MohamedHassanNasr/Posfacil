package com.paguelofacil.posfacil.data.network.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PosStatusApiResponse(
    @field:SerializedName("idPosBatch")
    val idPosBatch: Int? = null,

    @field:SerializedName("batch")
    val batch: String? = null,

    @field:SerializedName("start")
    val start: String? = null,

    @field:SerializedName("end")
    val end: String? = null,

    @field:SerializedName("status")
    val status: String? = null,

    @field:SerializedName("idPos")
    val idPos: Int? = null,

    @field:SerializedName("createdAt")
    val createdAt: String? = null,

    @field:SerializedName("createdBy")
    val createdBy: Int? = null,

    @field:SerializedName("serial")
    val serial: String? = null,

    @field:SerializedName("expired")
    val expired: Boolean? = null
) : Parcelable
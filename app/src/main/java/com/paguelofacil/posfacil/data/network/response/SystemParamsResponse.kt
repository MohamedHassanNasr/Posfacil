package com.paguelofacil.posfacil.data.network.response

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

@Parcelize
data class SystemParamsResponse(

    @field:SerializedName("prop")
    val prop: String? = null,

    @field:SerializedName("values")
    val values: ParamsDefault = ParamsDefault(),

) : Parcelable

@Entity
@Parcelize
data class ParamsDefault(

    @field:SerializedName("_default_values_refund")
    val _default_values_refund: String? = null,

    @field:SerializedName("_default_values_tip")
    val _default_values_tip: String? = null,

    @field:SerializedName("_screen_saver")
    val _screen_saver: String? = null,

    @field:SerializedName("_url_terms")
    val _url_terms: String? = null,


) : Parcelable

package com.paguelofacil.posfacil.data.database.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity
@Parcelize
data class SystemsParamsEntity(
    @PrimaryKey
    var id: Long? = null,
    var _default_values_refund: String? = null,
    var _default_values_tip: String? = null,
    var _screen_saver: String? = null,
    var _url_terms: String? = null,

) : Parcelable

package com.paguelofacil.posfacil.data.database.entity

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize


/**
 * Contact search response
 *
 * @property nationality
 * @property phone
 * @property bod
 * @property name
 * @property pfLevel
 * @property photo
 * @property login
 * @property userId
 * @property email
 * @property platform
 * @property lastname
 * @property selected
 * @constructor Create empty Contact search entity
 */
@Parcelize
data class ContactSearchEntity(

    @field:SerializedName("nationality")
    val nationality: String? = null,

    @field:SerializedName("phone")
    val phone: String? = null,

    @field:SerializedName("bod")
    val bod: String? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("pfLevel")
    val pfLevel: String? = null,

    @field:SerializedName("photo")
    val photo: String? = null,

    @field:SerializedName("login")
    val login: String? = null,

    @field:SerializedName("userId")
    val userId: Long = System.currentTimeMillis(),

    @field:SerializedName("email")
    val email: String? = null,

    @field:SerializedName("platform")
    val platform: String? = null,

    @field:SerializedName("lastname")
    val lastname: String? = null,

    @Transient var selected: Boolean = false
) : Parcelable {
    fun getFullName(): String? {
        val stringBuilder = StringBuilder()
        if (!name.isNullOrEmpty()) {
            stringBuilder.append(name)
        }
        if (!lastname.isNullOrEmpty()) {
            stringBuilder.append(" ")
            stringBuilder.append(lastname)
        }
        return stringBuilder.toString()
    }
}

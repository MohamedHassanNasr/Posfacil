package com.paguelofacil.posfacil.data.network.response

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue


/**
 * Login api response
 *
 * @property sessionId
 * @property contact
 * @property token
 * @constructor Create empty Login api response
 */
@Parcelize
data class LoginApiResponse(

    @field:SerializedName("sessionId")
    val sessionId: String? = null,

    @field:SerializedName("user")
    val contact: Contact = Contact(),

    @field:SerializedName("token")
    val token: String? = null,
) : Parcelable

@Entity
@Parcelize
data class Contact(

    @field:SerializedName("secretQuestion")
    val secretQuestion: String? = null,

    @field:SerializedName("country")
    val country: String? = null,

    @field:SerializedName("emailVerifiedDate")
    val emailVerifiedDate: String? = null,

    @field:SerializedName("ipLastUpdate")
    val ipLastUpdate: String? = null,

    @field:SerializedName("acceptTerms")
    val acceptTerms: Boolean? = null,

    @field:SerializedName("addressImgVerifiedDate")
    val addressImgVerifiedDate: String? = null,

    @field:SerializedName("pfLevel")
    val pfLevel: String? = null,

    @PrimaryKey
    @field:SerializedName("idUsr")
    val idUsr: Long = System.currentTimeMillis(),

    @field:SerializedName("verifiedDate")
    val verifiedDate: String? = null,

    @field:SerializedName("idMerchant")
    val idMerchant: String? = null,

    @field:SerializedName("validMerchants")
    val validMerchants: List<String>? = null,

    @field:SerializedName("howArrive")
    val howArrive: String? = null,

    @field:SerializedName("zip")
    val zip: String? = null,

    @field:SerializedName("imageId")
    val imageId: String? = null,

    @field:SerializedName("phoneVerifiedDate")
    val phoneVerifiedDate: String? = null,

    //  @field:SerializedName("merchant")
    //val merchant: String? = null,

    @field:SerializedName("acceptedtermsVers")
    val acceptedtermsVers: String? = null,

    @field:SerializedName("receiveInfo")
    val receiveInfo: Boolean? = null,

    @field:SerializedName("merchantNameRegister")
    val merchantNameRegister: String? = null,

    @field:SerializedName("ipRegister")
    val ipRegister: String? = null,

    @field:SerializedName("isMerchantOwner")
    val isMerchantOwner: String? = null,

    //@field:SerializedName("merchantProfile")
    //val merchantProfile: String? = null,

    @field:SerializedName("cfgProfile")
    val cfgProfile: String? = null,

    @field:SerializedName("lastname")
    var lastname: String? = null,

    @field:SerializedName("nationality")
    val nationality: String? = null,

    @field:SerializedName("phone")
    var phone: String? = null,

    @field:SerializedName("bod")
    val bod: String? = null,

    @field:SerializedName("digicelBan")
    val digicelBan: String? = null,

    @field:SerializedName("lastUpdate")
    val lastUpdate: String? = null,

    @field:SerializedName("digicelNumber")
    val digicelNumber: String? = null,

    @field:SerializedName("name")
    var name: String? = null,

    @field:SerializedName("typeId")
    val typeId: String? = null,

    @field:SerializedName("merchantRegister")
    val merchantRegister: String? = null,

    @field:SerializedName("status")
    val status: Int? = null,

    @field:SerializedName("acceptedtermsDate")
    val acceptedTermsDate: String? = null,

    @field:SerializedName("additionalAttributes")
    val additionalAttributes: @RawValue Any? = null,

    @field:SerializedName("addressImg")
    val addressImg: String? = null,

    @field:SerializedName("isPwdReset")
    val isPwdReset: Boolean? = null,

    @field:SerializedName("city")
    val city: String? = null,

    @field:SerializedName("countryRegister")
    val countryRegister: String? = null,

    @field:SerializedName("numberId")
    val numberId: String? = null,

    @field:SerializedName("login")
    var login: String? = null,

    @field:SerializedName("sys")
    val sys: Boolean? = null,

    @field:SerializedName("secretAnswer")
    val secretAnswer: String? = null,

    @field:SerializedName("multipleRelatedMerchants")
    val multipleRelatedMerchants: Boolean? = null,

    @field:SerializedName("platform")
    val platform: String? = null,

    @field:SerializedName("financialAttributes")
    val financialAttributes: String? = null,

    @field:SerializedName("loginBlockDate")
    val loginBlockDate: String? = null,

    @field:SerializedName("imageIdVerifiedDate")
    val imageIdVerifiedDate: String? = null,

    @field:SerializedName("isVerifiedUser")
    val isVerifiedUser: Boolean = false,

    @field:SerializedName("fbId")
    val fbId: String? = null,

    @field:SerializedName("merchantWebUrl")
    val merchantWebUrl: String? = null,

    @field:SerializedName("lang")
    val lang: String? = null,

    @field:SerializedName("email")
    var email: String? = null,

    @field:SerializedName("createDate")
    val createDate: String? = null,

    @field:SerializedName("isEmployeePf")
    val isEmployeePf: Boolean? = null,

    @field:SerializedName("loginErrorAccess")
    val loginErrorAccess: String? = null,

    @field:SerializedName("receiveNotifications")
    val receiveNotifications: Boolean? = null,

    @field:SerializedName("address2")
    val address2: String? = null,

    @field:SerializedName("address1")
    val address1: String? = null,

    @field:SerializedName("photo")
    var photo: String? = null,

    @field:SerializedName("pfPoints")
    val pfPoints: Int? = null,

    @field:SerializedName("lastAccess")
    val lastAccess: String? = null,

    @field:SerializedName("officerVerified")
    val officerVerified: String? = null,

   // @field:SerializedName("accounts")
    //val accounts: MutableList<BankAccountEntity>? = null,

    var selected: Boolean = false,
) : Parcelable {

    fun getFullName(): String {
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

package com.paguelofacil.posfacil.data.database.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.paguelofacil.posfacil.data.network.response.Contact
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue
import java.util.*


/**
 * Login user info. This entity is stored sharedpreferences to save the login user data
 *
 * @property token token from the server
 * @property session sessionId
 * @property fcmToken firebase token
 * @property introShown if intro screen has been shown to teh user
 * @property loggedIn true if user is logged in. false otherwise
 * @property id
 * @property pass logged in user password
 * @property userName
 * @property firstName
 * @property lastName
 * @property email
 * @property phone
 * @property profilePic
 * @property identificationImage
 * @property identificationImageVerified
 * @property phoneVerified
 * @property fingerprintPromptShown
 * @property fingerprintAuthenticatedEmail
 * @property fingerprintAuthenticatedPass
 * @property fingerPrintAuthenticatedLogin
 * @property idForChargeService the id of the charge service corresponding to the logged in user
 * @constructor Create empty User entity
 */
@Entity
@Parcelize
data class UserEntity(
    var token: String? = null,
    var session: String? = null,
    var fcmToken: String? = null,
    var introShown: Boolean = false,
    var loggedIn: Boolean = false,
    @PrimaryKey
    var id: Long? = null,
    var pass: String? = null,
    var userName: String? = null,
    var firstName: String? = null,
    var lastName: String? = null,
    var email: String? = null,
    var merchantEmail: String? = null,
    var phone: String? = null,
    var idMerchant: String? = null,
    var phoneVerified: Boolean = false,
    var profilePic: String? = null,
    var lastUpdatedOn: String? = null,
    var identificationImage: String? = null,
    var identificationImageVerified: Boolean = false,
    var fingerprintPromptShown: Boolean = false,
    var fingerprintAuthenticatedEmail: String? = null,
    var fingerprintAuthenticatedPass: String? = null,
    var fingerPrintAuthenticatedLogin: Boolean = false,
    var idForChargeService: String? = null,
    var idForSendService: String? = null,
    var locale: String? = null,
    var tempCodeAuth: String? = null,
    var tempEmailLogin: String? = null,
    var merchantProfile: @RawValue Contact.MerchantProfile? = null
) : Parcelable {

    fun getFullName(): String {
        val stringBuilder = StringBuilder()
        if (!firstName.isNullOrEmpty()) {
            stringBuilder.append(firstName)
        }
        if (!lastName.isNullOrEmpty()) {
            if (stringBuilder.isNotEmpty()) {
                stringBuilder.append(" ")
            }
            stringBuilder.append(lastName)
        }
        return stringBuilder.toString().capitalize(Locale.getDefault())
    }

    fun getInitiales(): String {
        val firstDigit = if (!firstName.isNullOrEmpty()) firstName?.first().toString() else ""
        val secondDigit = if (!lastName.isNullOrEmpty()) lastName?.first().toString() else ""
        return "${firstDigit.toUpperCase()}${secondDigit.toUpperCase()}"
    }
}

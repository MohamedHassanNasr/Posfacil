package com.paguelofacil.posfacil.repository

import android.content.SharedPreferences
import androidx.core.content.edit
import com.google.gson.Gson
import com.paguelofacil.posfacil.BuildConfig
import com.paguelofacil.posfacil.base.BaseRepo
import com.paguelofacil.posfacil.data.database.PreferenceManager
import com.paguelofacil.posfacil.data.database.entity.UserEntity
import com.paguelofacil.posfacil.data.network.api.ApiEndpoints
import com.paguelofacil.posfacil.data.network.api.ApiRequestCode
import com.paguelofacil.posfacil.data.network.api.BaseResponse
import com.paguelofacil.posfacil.data.network.response.LoginApiResponse
import com.paguelofacil.posfacil.model.MerchantResponse
import com.paguelofacil.posfacil.util.Constantes.LastUpdatedOn
import com.paguelofacil.posfacil.util.clearLocalDataForPreviousUser
import timber.log.Timber
import java.util.*
import kotlin.collections.HashMap


const val USER_ENTITY = "USER_ENTITY"

/**
 * Repository for User related API calls
 *
 * @constructor Create empty User repo
 */

object UserRepo : BaseRepo() {
    private val sharedPref: SharedPreferences = PreferenceManager.sharedPref

    /**
     * Save logged in user detail or update an existing logged in user detail in the local source.
     *
     * @param userEntity the user detail
     * @param updateOnMainThread true of required to be done on the main thread. false if to be done on an another thread
     */
    fun setOrUpdateUser(userEntity: UserEntity?, updateOnMainThread: Boolean = false) {
        val editor = sharedPref.edit()
        if (userEntity == null) {
            editor.remove(USER_ENTITY)
        } else {
            editor.putString(USER_ENTITY, Gson().toJson(userEntity))
        }
        if (updateOnMainThread) {
            editor.commit()
        } else {
            editor.apply()
        }
    }

    /**
     * Get logged in user detail from local source
     *
     * @return
     */
    fun getUser(): UserEntity {
        val defaultValue = Gson().toJson(UserEntity())
        return Gson().fromJson(sharedPref.getString(USER_ENTITY, defaultValue), UserEntity::class.java)
    }

    suspend fun updateUserLocal() {
        val user = getUser()
        val locale = Locale.getDefault().language.toUpperCase(Locale.ROOT)
        if (user.loggedIn && user.locale != locale) {
            val body = HashMap<String, Any>()
            body["actionSave"] = "UPDATE_ONLY_VALUES"
            body["lang"] = locale
            val response = apiRequest(ApiRequestCode.UPDATE_USER_LOCALE) {
                remoteDao.put(ApiEndpoints.USERS + "/" + user.id, body)
            }
            Timber.e("DATAAAA USER ${response.data}")
            if (response.isInternetOn) {
                if (response.headerStatus.code != null && (response.headerStatus.code == ApiRequestCode.SUCCESS || response.headerStatus.code == 202 || response.headerStatus.code == ApiRequestCode.CREATED)) {
                    user.locale = locale
                    setOrUpdateUser(user)
                }
            }
        }
    }

    /**
     * Login a user
     *
     * @param email
     * @param password
     * @return
     */
    suspend fun login(email: String, password: String): BaseResponse<Any> {
        val body = HashMap<String, Any>()

        val user = getUser()
        body["user"] = email
        body["password"] = password
        body["fcmToken"] = user.fcmToken ?: ""
        return apiRequest(ApiRequestCode.SIGN_IN) {
            remoteDao.post(ApiEndpoints.LOGIN, body)
        }
    }


    /**
     * Send otp for verifying user phone number while signup
     *
     * @param email
     * @return
     */
    suspend fun sendOtpStep1(email: String): BaseResponse<Any> {
        val body = HashMap<String, Any>()
        body["loginOrEmail"] = email
        body["step"] = 1
        return apiRequest(ApiRequestCode.PASSWORD_RECOVERY_STEP1) {
            remoteDao.post(ApiEndpoints.PASSWORD_RECOVERY, body)
        }
    }

    /**
     * Verify otp received on the user phone for verifying user phone number while signup
     *
     * @param otp received on the user phone
     * @return
     */
    suspend fun verifyOtpStep2(otp: String): BaseResponse<Any> {
        val body = HashMap<String, Any>()
        body["step"] = 2
        getUser().id?.let {
            body["idUsr"] = it
        }
        body["confirmationCode"] = otp
        return apiRequest(ApiRequestCode.PASSWORD_RECOVERY_STEP2) {
            remoteDao.post(ApiEndpoints.PASSWORD_RECOVERY, body)
        }
    }

    /**
     * Verify otp received on the user phone for verifying user while password recovery
     *
     * @param password
     * @param otp
     * @return
     */
    suspend fun verifyOtpStep3(password: String, otp: String): BaseResponse<Any> {
        val body = HashMap<String, Any>()
        body["step"] = 3
        getUser().id?.let {
            body["idUsr"] = it
        }
        body["newPassword"] = password
        body["confirmationCode"] = otp
        return apiRequest(ApiRequestCode.PASSWORD_RECOVERY_STEP3) {
            remoteDao.post(ApiEndpoints.PASSWORD_RECOVERY, body)
        }
    }


    fun saveUserData(response: LoginApiResponse, password: String) {
        val user = getUser()
        //if logged in user is not the last logged in user then delete the local data
        // of the previously logged in user
        if (user.email != response.contact.email) {
            clearLocalDataForPreviousUser()
        } else {
            PreferenceManager.sharedPref.edit {
                remove(LastUpdatedOn.SERVICES)
                remove(LastUpdatedOn.SHOPS)
                remove(LastUpdatedOn.CONTACTS)
                remove(LastUpdatedOn.PENDING_ACTIVITIES)
                remove(LastUpdatedOn.COMPLETED_ACTIVITIES)
            }
        }
        user.loggedIn = true
        user.token = response.token
        user.session = response.sessionId

        user.id = response.contact.idUsr
        user.userName = response.contact.login
        user.firstName = response.contact.name
        user.lastName = response.contact.lastname
        user.idMerchant = response.contact.idMerchant
        user.merchantProfile = response.contact.merchantProfile

        //if the user has not used fingerprint authentication for login
        if (!user.fingerPrintAuthenticatedLogin) {
            user.email = response.contact.email
            user.pass = password

            //if logged in user email is not saved in the biometric authentication
            // then mark that we need to ask for saving it in the Biometric auth system
            user.fingerprintPromptShown = user.fingerprintAuthenticatedEmail == response.contact.email
        } else {
            //mark that we don't need to show biometric auth dialog
            user.fingerprintPromptShown = true
            user.email = user.fingerprintAuthenticatedEmail
            user.pass = user.fingerprintAuthenticatedPass
        }
        user.phone = response.contact.phone
        user.profilePic = response.contact.photo
        user.identificationImage = response.contact.imageId
        user.phoneVerified = false
        response.contact.phoneVerifiedDate?.let {
            user.phoneVerified = it.length > 1
        }

        setOrUpdateUser(user, true)
    }

    suspend fun refreshLogin(){
        remoteDao.get(
            url = ApiEndpoints.REFRESH_LOGIN_USER
        )
    }

    suspend fun getMerchant(idMerchant: String): MerchantResponse {
        val url = "${BuildConfig.API_BASE_URL}/${ApiEndpoints.MERCHANT_URL}$${idMerchant}"
        return remoteDao.getMerchant(url)
    }
}
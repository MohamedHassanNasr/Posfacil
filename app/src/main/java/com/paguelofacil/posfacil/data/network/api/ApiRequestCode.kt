package com.paguelofacil.posfacil.data.network.api


/**
 * Api codes. Every API error code defined here
 *
 * @constructor Create empty Api codes
 */
object ApiRequestCode {
    const val EMPTY_RESPONSE = -1
    const val NO_API = 0
    const val SUCCESS = 200
    const val CREATED = 201

    const val SESSION_EXPIRED1: Int = 401
    const val SESSION_EXPIRED2: Int = 406
    const val SESSION_EXPIRED3: Int = 407
    const val SESSION_EXPIRED4: Int = 428
    const val SESSION_EXPIRED5: Int = 500
    const val SESSION_EXPIRED6: Int = 510
    const val USER_NOT_LOGGED_IN: Int = 520

    const val REGISTER_USER = 1
    const val VERIFY_OTP = 2
    const val RESEND_OTP = 3
    const val SEND_OTP = 4
    const val SIGN_IN = 5
    const val UPLOAD_IDENTITY_CARD = 6

    const val PASSWORD_RECOVERY_STEP1 = 7
    const val PASSWORD_RECOVERY_STEP2 = 8
    const val PASSWORD_RECOVERY_STEP3 = 9

    const val UPDATE_USER_INFO = 10
    const val GET_USER_INFO = 11

    const val SEARCH_CONTACT_BY_EMAIL = 12

    const val UPDATE_USER_LOCALE = 13

    const val SEARCH_PARAMS_SYSTEM = 14









}
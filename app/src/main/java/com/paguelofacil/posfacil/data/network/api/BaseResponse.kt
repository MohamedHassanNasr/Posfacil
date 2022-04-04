package com.paguelofacil.posfacil.data.network.api

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


/**
 * Base response for all API response
 *
 * @param T
 * @constructor Create empty Base response
 */
open class BaseResponse<T> {
    var requestCode: Int = 0
    var apiError: ApiError? = null
    var isInternetOn = true

    @SerializedName("headerStatus")
    @Expose
    var headerStatus: HeaderStatus = HeaderStatus()

    @SerializedName("success")
    @Expose
    var success: Boolean = false

    @SerializedName("message")
    @Expose
    var message: String? = null

    @SerializedName("data")
    @Expose
    var data: T? = null

}

class HeaderStatus {
    val code: Int? = null
    val description: String? = null
}
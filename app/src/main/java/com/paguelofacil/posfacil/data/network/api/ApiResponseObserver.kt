package com.paguelofacil.posfacil.data.network.api

import androidx.lifecycle.Observer
import com.paguelofacil.posfacil.ApplicationClass
import com.paguelofacil.posfacil.R


/**
 * Base class for API response callback type decision
 *
 * @param T
 * @constructor Create empty Live data api observer
 */
interface ApiResponseObserver<T> : Observer<BaseResponse<T>> {
    fun onResponseSuccess(requestCode: Int, responseCode: Int, msg: String?, data: T?)
    fun onException(requestCode: Int, exception: ApiError)
    fun onExceptionData(requestCode: Int, exception: ApiError,data: T?)
    fun noInternetConnection(requestCode: Int, msg: String?)

    override fun onChanged(baseData: BaseResponse<T>?) {
        if (baseData != null) {

            if (baseData.isInternetOn) {
                if (baseData.headerStatus.code != null && (baseData.headerStatus.code == ApiRequestCode.SUCCESS ||
                            baseData.headerStatus.code == 202 || baseData.headerStatus.code == ApiRequestCode.CREATED)) {
                    onResponseSuccess(
                        baseData.requestCode,
                        baseData.headerStatus.code!!,
                        baseData.message,
                        baseData.data
                    )
                } else {

                        onException(baseData.requestCode, baseData.apiError!!)


                }
                return
            } else {
                noInternetConnection(baseData.requestCode, baseData.apiError!!.message)
                return
            }
        }
        val error = ApiError()
        error.message = ApplicationClass.instance.getString(R.string.Something_went_wrong)
        onException(ApiRequestCode.EMPTY_RESPONSE, error)
    }
}
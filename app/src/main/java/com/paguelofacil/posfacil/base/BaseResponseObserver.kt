package com.paguelofacil.posfacil.base

import androidx.lifecycle.Observer
import com.paguelofacil.posfacil.ApplicationClass
import com.paguelofacil.posfacil.R
import com.paguelofacil.posfacil.data.network.api.ApiError
import com.paguelofacil.posfacil.data.network.api.ApiRequestCode
import com.paguelofacil.posfacil.data.network.api.BaseResponse


/**
 * Base class for API response callback type decision
 *
 * @param T
 * @constructor Create empty Live data api observer
 */
interface BaseResponseObserver<T> : Observer<BaseResponse<T>> {
    fun onResponseSuccess(requestCode: Int, responseCode: Int, msg: String?, data: T?)
    fun onException(requestCode: Int, exception: ApiError)
    fun noInternetConnection(requestCode: Int, msg: String?)

    override fun onChanged(baseData: BaseResponse<T>?) {
        if (baseData != null) {

            if (baseData.isInternetOn) {
                if (baseData.headerStatus.code != null && (baseData.headerStatus.code == ApiRequestCode.SUCCESS ||
                            baseData.headerStatus.code == 202 || baseData.headerStatus.code == ApiRequestCode.CREATED)
                ) {
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
    }
}
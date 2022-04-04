package com.paguelofacil.posfacil.base

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.paguelofacil.posfacil.data.network.api.GenericApiRequest
import com.paguelofacil.posfacil.data.network.api.RemoteDataSource
import com.paguelofacil.posfacil.data.network.api.WebService


import retrofit2.Retrofit

/**
 * Base repo that needs to be inherited by all the repositories that need to perform API calls.
 * It provides the Retrofit instance and some useful methods required during every API call.
 *
 * @constructor Create empty Base repo
 */
open class BaseRepo :  GenericApiRequest<Any>() {
    private val remoteDataSource: Retrofit by lazy { RemoteDataSource.instance }
    val remoteDao: WebService by lazy { remoteDataSource.create(WebService::class.java) }




}
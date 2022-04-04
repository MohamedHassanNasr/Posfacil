package com.paguelofacil.posfacil.base

import android.provider.ContactsContract
import android.telephony.PhoneNumberUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.paguelofacil.posfacil.data.network.api.ApiRequestCode
import com.paguelofacil.posfacil.data.network.api.BaseResponse
import com.paguelofacil.posfacil.repository.ContactsRepo
import com.paguelofacil.posfacil.repository.UserRepo
import com.paguelofacil.posfacil.tools.SingleLiveData
import com.paguelofacil.posfacil.util.Constantes.CoroutinesBase
import com.paguelofacil.posfacil.util.Constantes.LoadingState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


/**
 * Base viewmodel. To be inherited by all the viewmodels in the project for having common structure
 * and some utility functions
 *
 * @constructor Create empty Base view model
 */
@Suppress("UNCHECKED_CAST")
open class BaseViewModel : ViewModel() {
    private val _response = SingleLiveData<BaseResponse<Any>>()
    private val _loadingState = SingleLiveData<LoadingState>()

    val loadingState: LiveData<LoadingState>
        get() = _loadingState as LiveData<LoadingState>
    val response: LiveData<BaseResponse<Any>>
        get() = _response as LiveData<BaseResponse<Any>>

    val baseRepo = BaseRepo()
    val contactsRepo: ContactsRepo by lazy { ContactsRepo() }

    fun updateResponseObserver(response: BaseResponse<Any>, passToBase: Boolean = true, callback: (ApiResponse) -> Unit) {
        if (response.isInternetOn) {
            if (response.headerStatus.code != null && (response.headerStatus.code == ApiRequestCode.SUCCESS || response.headerStatus.code == 202 || response.headerStatus.code == ApiRequestCode.CREATED)) {
                callback(ApiResponse.Success(response.requestCode, response.data!!))
            }
        }
        if (passToBase) {
            _response.postValue(response)
        }
    }

    fun getResponseObserver() = response

    fun setLoadingState(state: LoadingState) {
        _loadingState.postValue(state)
    }

    sealed class ApiResponse {
        class Success(val requestCode: Int, val data: Any?) : ApiResponse()
    }

    fun updateUserLocal() = CoroutinesBase.main {
        UserRepo.updateUserLocal()
    }









}
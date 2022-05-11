package com.paguelofacil.posfacil.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paguelofacil.posfacil.ApplicationClass
import com.paguelofacil.posfacil.R
import com.paguelofacil.posfacil.data.network.api.ApiError
import com.paguelofacil.posfacil.data.network.api.ApiRequestCode
import com.paguelofacil.posfacil.data.network.api.BaseResponse
import com.paguelofacil.posfacil.data.network.api.GenericApiRequest
import com.paguelofacil.posfacil.repository.ContactsRepo
import com.paguelofacil.posfacil.repository.UserRepo
import com.paguelofacil.posfacil.tools.SingleLiveData
import com.paguelofacil.posfacil.util.Action
import com.paguelofacil.posfacil.util.Constantes.AppConstants
import com.paguelofacil.posfacil.util.Constantes.AppConstants.Companion.getBasicError
import com.paguelofacil.posfacil.util.Constantes.AppConstants.Companion.getErrorMsgBasedOnResponseCode
import com.paguelofacil.posfacil.util.Constantes.CoroutinesBase
import com.paguelofacil.posfacil.util.Constantes.LoadingState
import com.paguelofacil.posfacil.util.Resultado
import com.paguelofacil.posfacil.util.isInternetAvailable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response


/**
 * Base viewmodel. To be inherited by all the viewmodels in the project for having common structure
 * and some utility functions
 *
 * @constructor Create empty Base view model
 */
@Suppress("UNCHECKED_CAST")
abstract class BaseViewModel : ViewModel() {
    private val _response = SingleLiveData<BaseResponse<Any>>()
    private val _loadingState = SingleLiveData<LoadingState>()
    private val mutableException = MutableLiveData<ApiError>()

    val loadingState: LiveData<LoadingState>
        get() = _loadingState as LiveData<LoadingState>
    val response: LiveData<BaseResponse<Any>>
        get() = _response as LiveData<BaseResponse<Any>>
    val liveDataException: LiveData<ApiError> = mutableException

    val baseRepo = BaseRepo()
    val contactsRepo: ContactsRepo by lazy { ContactsRepo() }

    fun updateResponseObserver(
        response: BaseResponse<Any>,
        passToBase: Boolean = true,
        callback: (ApiResponse) -> Unit,
        onFailure: (String)-> Unit
    ) {
        if (response.isInternetOn) {
            if (response.headerStatus.code != null && (response.headerStatus.code == ApiRequestCode.SUCCESS || response.headerStatus.code == 202 || response.headerStatus.code == ApiRequestCode.CREATED)) {
                callback(ApiResponse.Success(response.requestCode, response.data!!))
            }else{
                onFailure(response.headerStatus.code.toString())
            }
        }
        if (passToBase) {
            _response.postValue(response)
        }
    }

    fun execute(func: Action): Job {
        return viewModelScope.launch {
            try {
                setLoadingState(LoadingState.LOADING)
                func()
            } catch (ex: Exception) {
                ex.printStackTrace()
                setLoadingState(LoadingState.LOADED)
            }
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

    fun <T> processResponseResultado(
        baseData: Resultado<BaseResponse<T>>?,
        callbackSuccess: (T?) -> Unit
    ) {
        val response = when (baseData) {
            is Resultado.Success -> processResponse(baseData.data)
            else -> processResponse(BaseResponse(), 500)
        }
        if (response?.apiError == null) {
            callbackSuccess(response?.data)
        } else {
            mutableException.postValue(response.apiError)
        }
        setLoadingState(LoadingState.LOADED)
    }

    fun <T> processResponse(
        baseData: BaseResponse<T>?,
        apiCode: Int = baseData?.headerStatus?.code ?: 0
    ): BaseResponse<T>? {
        if (isInternetAvailable(ApplicationClass.instance)) {
            if (baseData?.headerStatus?.code != null && (baseData.headerStatus.code == ApiRequestCode.SUCCESS ||
                        baseData.headerStatus.code == 202 || baseData.headerStatus.code == ApiRequestCode.CREATED)
            ) {
                return baseData
            } else {
                baseData?.run { this.apiError = getBasicError<Any>(apiCode, null, true) }
                return baseData
            }
        } else {
            baseData?.run {
                this.isInternetOn = false
                this.apiError = getBasicError<Any>(apiCode, null, false)
            }
            return baseData
        }
    }
}
package com.paguelofacil.posfacil.ui.view.home.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.paguelofacil.posfacil.base.BaseViewModel
import com.paguelofacil.posfacil.data.database.entity.UserEntity
import com.paguelofacil.posfacil.data.network.api.ApiEndpoints
import com.paguelofacil.posfacil.data.network.request.ReportZRequest
import com.paguelofacil.posfacil.data.network.response.ReportZResponse
import com.paguelofacil.posfacil.model.LanguageData
import com.paguelofacil.posfacil.model.SessionStatus
import com.paguelofacil.posfacil.model.toLanguageData
import com.paguelofacil.posfacil.repository.UserRepo
import com.paguelofacil.posfacil.repository.language.LanguageRepository
import com.paguelofacil.posfacil.repository.report.ReportRepository
import com.paguelofacil.posfacil.repository.user.UserRepository
import com.pax.dal.ISys
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val reportRepository: ReportRepository,
    private val userRepository: UserRepository,
    private val languageRepository: LanguageRepository,
) : BaseViewModel() {

    private val mutableLanguageResponse = MutableLiveData<LanguageData?>()
    val liveDataLanguageResponse: LiveData<LanguageData?> = mutableLanguageResponse
    val x = "HOLA"
    private val title = MutableLiveData<Pair<String?, Boolean>>()
    val titleObserver: LiveData<Pair<String?, Boolean>> = title

    val mutableUpdateLanguage = MutableLiveData<Boolean>()

    companion object {
        private var counter = 0L
        private var counterRefresh = 0L
    }

    private val mutableValidateReportZ = MutableLiveData<ReportZResponse?>()
    val liveDataValidateReportZ: LiveData<ReportZResponse?> = mutableValidateReportZ

    private val mutableGenerateReportZ = MutableLiveData<ReportZResponse?>()
    val liveDataGenerateReportZ: LiveData<ReportZResponse?> = mutableGenerateReportZ

    private val repoUser = UserRepo

    private val mutableDataUser = MutableLiveData<UserEntity>()
    val liveDataUser: LiveData<UserEntity> = mutableDataUser

    fun setTitle(titleSet: String, bool: Boolean){
        title.postValue(Pair(titleSet, bool))
    }

    fun getDataUser() {
        val dataUser = userRepository.getUser()
        mutableDataUser.value = dataUser
    }

    fun checkZReport(Sys: ISys?) {
        execute {
            viewModelScope.launch {
                val response = reportRepository.checkReportZ(Sys?.baseInfo?.sn ?: ApiEndpoints.ATIK_SERIAL)
                processResponseResultado(response) {
                    mutableValidateReportZ.postValue(it)
                }
            }
        }
    }

    fun getLanguageDevice():String {
        return languageRepository.getLanguageDeviceLocal()
    }

    fun setLanguageDevice(language: String) {
        languageRepository.setLanguageDeviceLocal(language)
    }

    fun generateReportZ() {
        execute {
            viewModelScope.launch {
                val request = ReportZRequest(
                    generate = true,
                    email = repoUser.getUser().email
                )
                val response = reportRepository.generateReportZ(request)
                processResponseResultado(response) {
                    mutableValidateReportZ.postValue(it)
                }
            }
        }
    }

    /** TIMER PARA LAS SESIONES */
    val sessionStatusTimer: Flow<SessionStatus> = flow {
        while (currentCoroutineContext().isActive) {
            counter += 1
            counterRefresh += 1
            when {
                ((counter > 120) and (counter < 150)) -> emit(SessionStatus.SHOW_IMAGE)
                (counterRefresh > 60) -> emit(SessionStatus.REFRESH)
                else -> {
                    emit(SessionStatus.ACTIVE)
                }
            }
            // Timber.e("Emited Timer: $counter")
            delay(500)
        }
    }.flowOn(Dispatchers.IO)

    fun checkLanguage() {
        execute {
            viewModelScope.launch {
                val response = languageRepository.getLanguageRemote()
                processResponseResultado(response) {
                    mutableLanguageResponse.postValue(it?.toLanguageData)
                }
            }
        }
    }

    fun resetTimerRefresh() {
        counterRefresh = 0L
    }

    fun resetTimer() {
        counter = 0L
    }

    suspend fun refreshUser() {
        repoUser.refreshLogin()
    }
}
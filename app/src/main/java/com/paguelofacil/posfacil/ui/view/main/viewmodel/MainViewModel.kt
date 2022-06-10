package com.paguelofacil.posfacil.ui.view.main.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.paguelofacil.posfacil.base.BaseViewModel
import com.paguelofacil.posfacil.model.LanguageData
import com.paguelofacil.posfacil.model.toLanguageData
import com.paguelofacil.posfacil.repository.language.LanguageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val languageRepository: LanguageRepository
) : BaseViewModel() {

    private val mutableLanguageResponse = MutableLiveData<LanguageData?>()
    val liveDataLanguageResponse: LiveData<LanguageData?> = mutableLanguageResponse

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

}
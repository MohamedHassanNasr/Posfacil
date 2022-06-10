package com.paguelofacil.posfacil.ui.view.account.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.paguelofacil.posfacil.base.BaseViewModel
import com.paguelofacil.posfacil.data.database.entity.UserEntity
import com.paguelofacil.posfacil.repository.UserRepo
import com.paguelofacil.posfacil.repository.information.MyInformationRepository
import com.paguelofacil.posfacil.util.Constantes.CoroutinesBase
import com.paguelofacil.posfacil.util.Constantes.LoadingState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MyInformationViewModel @Inject constructor(
    private val repository: MyInformationRepository
) : BaseViewModel() {

    private val mutableDataUser = MutableLiveData<UserEntity>()
    val liveDataUser: LiveData<UserEntity> = mutableDataUser

    fun getDataUser() {
        val dataUser = repository.getUser()
        mutableDataUser.value = dataUser
    }
}
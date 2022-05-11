package com.paguelofacil.posfacil.repository.information

import com.google.gson.Gson
import com.paguelofacil.posfacil.data.database.PreferenceManager
import com.paguelofacil.posfacil.data.database.entity.UserEntity
import com.paguelofacil.posfacil.repository.USER_ENTITY
import javax.inject.Inject

class MyInformationRepository @Inject constructor(){
    private val sharedPref = PreferenceManager.sharedPref

    fun getUser(): UserEntity {
        val defaultValue = Gson().toJson(UserEntity())
        return Gson().fromJson(sharedPref.getString(USER_ENTITY, defaultValue), UserEntity::class.java)
    }

}
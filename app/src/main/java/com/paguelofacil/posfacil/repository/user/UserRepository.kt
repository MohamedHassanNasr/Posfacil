package com.paguelofacil.posfacil.repository.user

import com.google.gson.Gson
import com.paguelofacil.posfacil.data.database.PreferenceManager
import com.paguelofacil.posfacil.data.database.entity.UserEntity
import com.paguelofacil.posfacil.data.network.api.BaseResponse
import com.paguelofacil.posfacil.data.network.response.TransactionApiResponse
import com.paguelofacil.posfacil.repository.USER_ENTITY
import com.paguelofacil.posfacil.util.Resultado
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Repository for Transaction related API calls
 *
 * @constructor Create empty Transaction repository
 */

class UserRepository @Inject constructor() {
    private val sharedPref = PreferenceManager.sharedPref

    fun getUser(): UserEntity {
        val defaultValue = Gson().toJson(UserEntity())
        return Gson().fromJson(
            sharedPref.getString(USER_ENTITY, defaultValue),
            UserEntity::class.java
        )
    }

}
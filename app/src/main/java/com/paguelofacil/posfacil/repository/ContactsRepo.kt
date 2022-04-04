package com.paguelofacil.posfacil.repository

import com.google.gson.Gson
import com.paguelofacil.posfacil.base.BaseRepo
import com.paguelofacil.posfacil.data.database.PreferenceManager
import com.paguelofacil.posfacil.data.network.api.ApiEndpoints
import com.paguelofacil.posfacil.data.network.api.ApiRequestCode
import com.paguelofacil.posfacil.data.network.api.BaseResponse
import com.paguelofacil.posfacil.util.Constantes.ApiParams


/**
 * Repository for Contacts/Users related API calls
 *
 * @constructor Create empty Contacts repo
 */
class ContactsRepo : BaseRepo() {


    /**
     * Get contacts having the provided email
     *
     * @param email to be searched for
     * @return the users having the email
     */
    suspend fun getPagueloContactWithEmail(email: String): BaseResponse<Any> {
        val map = HashMap<String, Any>()
        map["platform"] = "WALLET"
        map[ApiParams.LIMIT] = 1
        map[ApiParams.CONDITIONAL] = "email\$eq$email"
        return apiRequest(ApiRequestCode.SEARCH_CONTACT_BY_EMAIL) {
            remoteDao.get(ApiEndpoints.SEARCH_USER, map)
        }
    }



}
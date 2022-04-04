package com.paguelofacil.posfacil.data.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.paguelofacil.posfacil.data.network.response.Contact


/**
 * Contacts local dao
 * Provides CRUD operation functions on Contact table
 *
 * @constructor Create empty Contacts local dao
 */
@Dao
interface ContactsLocalDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: Contact): Long

}
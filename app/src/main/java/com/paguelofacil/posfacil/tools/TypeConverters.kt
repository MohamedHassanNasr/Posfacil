package com.paguelofacil.posfacil.tools

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.paguelofacil.posfacil.data.network.response.Contact


/**
 * Type converters utility class. It holds all the Room db type converters used on this project
 *
 * @constructor Create empty Type converters
 */
class TypeConverters {

    class AnyConverter {
        @TypeConverter
        fun toString(value: Any?): String? {
            return value?.toString()
        }

        @TypeConverter
        fun toAny(data: String?): Any? = data
    }


    class ContactConverter {
        @TypeConverter
        fun objectToJsonString(jsonString: String?): Contact? {
            val type = object : TypeToken<Contact>() {}.type
            return Gson().fromJson(jsonString, type)
        }

        @TypeConverter
        fun jsonStringToObject(`object`: Contact?): String? {
            return Gson().toJson(`object`)
        }
    }



}
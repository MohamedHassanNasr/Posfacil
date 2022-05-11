package com.paguelofacil.posfacil.data.database

import android.content.SharedPreferences
import com.paguelofacil.posfacil.ApplicationClass
import com.paguelofacil.posfacil.util.Constantes.AppConstants.Companion.IDIOMA_INGLES


/**
 * Preference manager to store data in the SharedPreference storage of Android framework
 *
 * @constructor Create empty Preference manager
 */
object PreferenceManager {
    val APP_FORCE_CLOSED_LAST_TIME = "app_force_closed_last_time"
    val keyLanguage = "language"
    var sharedPref: SharedPreferences =
        androidx.preference.PreferenceManager.getDefaultSharedPreferences(ApplicationClass.instance)

    /**
     * Helper method to retrieve a String value from [SharedPreferences].
     *
     * @param key
     * @return The value from shared preferences, or null if the value could not be read.
     */
    fun getStringPreference(key: String, defValue: String?): String? {
        return sharedPref.getString(key, defValue)
    }

    fun getLanguageDevice(): String {
        return sharedPref.getString(keyLanguage, IDIOMA_INGLES) ?: IDIOMA_INGLES
    }

    fun setLanguageDevice(language:String) {
        sharedPref.edit().putString(keyLanguage, language).commit()
    }

    /**
     * Helper method to write a String value to [SharedPreferences].
     *
     * @param key
     * @param value
     * @return true if the new value was successfully written to persistent storage.
     */
    fun setStringPreference(key: String, value: String?): Boolean {
        val editor = sharedPref.edit()
        editor.putString(key, value)
        return editor.commit()
    }

    /**
     * Helper method to retrieve a float value from [SharedPreferences].
     *
     * @param key
     * @param defaultValue A default to return if the value could not be read.
     * @return The value from shared preferences, or the provided default.
     */
    fun getFloatPreference(key: String, defaultValue: Float): Float {
        return sharedPref.getFloat(key, defaultValue)
    }

    /**
     * Helper method to write a float value to [SharedPreferences].
     *
     * @param key
     * @param value
     * @return true if the new value was successfully written to persistent storage.
     */
    fun setFloatPreference(key: String, value: Float): Boolean {
        val editor = sharedPref.edit()
        editor.putFloat(key, value)
        return editor.commit()
    }

    /**
     * Helper method to retrieve a long value from [SharedPreferences].
     *
     * @param key
     * @param defaultValue A default to return if the value could not be read.
     * @return The value from shared preferences, or the provided default.
     */
    fun getLongPreference(key: String, defaultValue: Long): Long {
        return sharedPref.getLong(key, defaultValue)
    }

    /**
     * Helper method to write a long value to [SharedPreferences].
     *
     * @param key
     * @param value
     * @return true if the new value was successfully written to persistent storage.
     */
    fun setLongPreference(key: String, value: Long): Boolean {
        val editor = sharedPref.edit()
        editor.putLong(key, value)
        return editor.commit()
    }

    /**
     * Helper method to retrieve an integer value from [SharedPreferences].
     *
     * @param key
     * @param defaultValue A default to return if the value could not be read.
     * @return The value from shared preferences, or the provided default.
     */
    fun getIntegerPreference(key: String, defaultValue: Int): Int {
        return sharedPref.getInt(key, defaultValue)
    }

    /**
     * Helper method to write an integer value to [SharedPreferences].
     *
     * @param key
     * @param value
     * @return true if the new value was successfully written to persistent storage.
     */
    fun setIntegerPreference(key: String, value: Int): Boolean {
        val editor = sharedPref.edit()
        editor.putInt(key, value)
        return editor.commit()
    }

    /**
     * Helper method to retrieve a boolean value from [SharedPreferences].
     *
     * @param key
     * @param defaultValue A default to return if the value could not be read.
     * @return The value from shared preferences, or the provided default.
     */
    fun getBooleanPreference(key: String, defaultValue: Boolean): Boolean {
        return sharedPref.getBoolean(key, defaultValue)
    }

    /**
     * Helper method to write a boolean value to [SharedPreferences].
     *
     * @param key
     * @param value
     * @return true if the new value was successfully written to persistent storage.
     */
    fun setBooleanPreference(key: String, value: Boolean): Boolean {
        val editor = sharedPref.edit()
        editor.putBoolean(key, value)
        return editor.commit()
    }
}
package com.paguelofacil.posfacil

import android.app.Application
import android.os.Handler
import android.widget.Toast
import com.paguelofacil.posfacil.model.LanguageFile
import com.pax.dal.*
import com.pax.dal.entity.EPedType
import com.pax.dal.entity.EPiccType
import com.pax.dal.entity.EScannerType
import com.pax.neptunelite.api.NeptuneLiteUser
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import timber.log.Timber.DebugTree
import java.util.concurrent.ExecutorService

/**
 * Application class of the project. It is loaded first in the memory when the app is launched.
 * Use it to perform any task globally required for the whole application
 *
 * @constructor Create empty Application class
 */

@HiltAndroidApp
class ApplicationClass : Application() {

    companion object {
        //application class instance
        private val TAG = "EmvDemoApp"
        private var handler: Handler? = null
        private var backgroundExecutor: ExecutorService? = null
        lateinit var instance: ApplicationClass

        lateinit var language: LanguageFile

        @JvmStatic
        fun getApp(): ApplicationClass {
            return instance
        }
    }

    override fun onCreate() {
        super.onCreate()
        Timber.e("ONCREATE APP")
        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        }
        instance = this
    }
    /*
    * If system is running out of low memory this function is called to GC all the referenced memory
    * */
    override fun onLowMemory() {
        super.onLowMemory()
        System.gc()
    }

}
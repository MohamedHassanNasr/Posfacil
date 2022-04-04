package com.paguelofacil.posfacil

import android.app.Application
import androidx.lifecycle.LifecycleObserver
import timber.log.Timber
import timber.log.Timber.DebugTree


/**
 * Application class of the project. It is loaded first in the memory when the app is launched.
 * Use it to perform any task globally required for the whole application
 *
 * @constructor Create empty Application class
 */
class ApplicationClass : Application(), LifecycleObserver {

    companion object {
        //application class instance
        lateinit var instance: ApplicationClass
    }

    override fun onCreate() {
        super.onCreate()
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
package com.paguelofacil.posfacil

import android.app.Application
import android.os.Handler
import androidx.lifecycle.LifecycleObserver
import com.paguelofacil.posfacil.model.LanguageFile
import com.paguelofacil.posfacil.pax.AppActLifecycleCallback
import com.paguelofacil.posfacil.pax.manager.ParamManager
import com.paguelofacil.posfacil.pax.util.ThreadPoolManager

import com.pax.commonlib.utils.LogUtils
import com.pax.dal.IDAL
import com.paxsz.module.emv.process.EmvBase
import com.paxsz.module.pos.Sdk

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
        private var mParamManager: ParamManager? = null
        private var handler: Handler? = null
        private var backgroundExecutor: ExecutorService? = null
        private var dal: IDAL? = null
        lateinit var instance: ApplicationClass

        lateinit var language: LanguageFile

        private fun initSdkModule() {
            getApp().runInBackground {
                LogUtils.d(TAG, " initSdkModule start")
                val startT = System.currentTimeMillis()
                getDalInstance()
                val endT = System.currentTimeMillis()
                LogUtils.d(TAG, "initSdkModule  end:" + (endT - startT))
            }
        }

        private fun getDalInstance() {
            dal = Sdk.getInstance(instance).getDal()
        }

        private fun initEmvModule() {
            getApp()?.runInBackground {
                LogUtils.d(TAG, " initEmvModule start")
                val startT = System.currentTimeMillis()
                mParamManager = ParamManager.getInstance(instance)
                val endT = System.currentTimeMillis()
                LogUtils.d(TAG, "initEmvModule  end:" + (endT - startT))
                EmvBase.loadLibrary()
            }
        }

        @JvmStatic
        fun getParamManager(): ParamManager? {
            return mParamManager
        }

        @JvmStatic
        fun getApp(): ApplicationClass {
            return instance
        }
    }

    fun runInBackground(runnable: Runnable?) {
        backgroundExecutor?.execute(runnable)
    }

    fun runOnUiThread(runnable: Runnable?) {
        if (runnable != null) {
            handler?.post(runnable)
        }
    }

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        }
        instance = this

        handler = Handler()
        registerActivityLifecycleCallbacks(AppActLifecycleCallback())
        backgroundExecutor = ThreadPoolManager.getInstance().getExecutor()
        initSdkModule()
        initEmvModule()
    }
    /*
    * If system is running out of low memory this function is called to GC all the referenced memory
    * */
    override fun onLowMemory() {
        super.onLowMemory()
        System.gc()
    }

    fun getDal(): IDAL? {
        if (dal == null) {
            getDalInstance()
        }
        return dal
    }



}
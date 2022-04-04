package com.paguelofacil.posfacil.data.network.api

import android.Manifest
import android.content.pm.PackageManager
import android.hardware.usb.UsbDevice.getDeviceId
import android.os.Environment
import androidx.core.app.ActivityCompat
import com.paguelofacil.posfacil.BuildConfig
import com.google.gson.GsonBuilder
import com.paguelofacil.posfacil.ApplicationClass
import com.paguelofacil.posfacil.repository.UserRepo
import com.paguelofacil.posfacil.util.CalenderUtil
import com.paguelofacil.posfacil.util.CalenderUtil.getTimeZoneOffset
import com.paguelofacil.posfacil.util.Constantes.ApiParams
import com.paguelofacil.posfacil.util.getDeviceId
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.io.File
import java.util.*
import java.util.concurrent.TimeUnit


/**
 * RemoteDataSource used to create create the instance ot Retrofit service to hit the APIs
 *
 * @constructor Create empty Retrofit manager
 */
private const val CONNECT_TIMEOUT = "CONNECT_TIMEOUT"
private const val READ_TIMEOUT = "READ_TIMEOUT"
private const val WRITE_TIMEOUT = "WRITE_TIMEOUT"

abstract class RemoteDataSource {

    companion object {
        // Singleton prevents multiple instances of retrofit opening at the
        // same time.
        @Volatile
        private var INSTANCE: Retrofit? = null

        val instance: Retrofit
            get() {
                // if the INSTANCE is not null, then return it,
                // if it is, then create the retrofit
                return INSTANCE ?: synchronized(this) {
                    val newInstance = getRetrofitInstance().also { INSTANCE = it }
                    INSTANCE = newInstance
                    // return instance
                    newInstance
                }
            }

        /**
         * Get a new retrofit instance
         *
         * @return
         */
        private fun getRetrofitInstance(): Retrofit {
            val httpClient = OkHttpClient.Builder()
            httpClient
                .readTimeout(30000, TimeUnit.MILLISECONDS)
                .writeTimeout(20000, TimeUnit.MILLISECONDS)
                .connectTimeout(10000, TimeUnit.MILLISECONDS)
                .addInterceptor(SupportInterceptor())
                .addInterceptor(getLoggingInterceptor())

            val gson = GsonBuilder()
                .setLenient()
                .create()

            return Retrofit.Builder()
                .baseUrl(BuildConfig.API_BASE_URL)
                .client(httpClient.build())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
        }

        /**
         * Get a new logging interceptor object
         *
         * @return
         */
        private fun getLoggingInterceptor(): HttpLoggingInterceptor {
            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BODY
            return logging
        }

        /**
         * Support interceptor used for modifying API calls during runtime such as adding authntication, timeout period, headings etc
         *
         * @constructor Create empty Support interceptor
         */
        private class SupportInterceptor : Interceptor {
            override fun intercept(chain: Interceptor.Chain): Response {
                var connectTimeout = chain.connectTimeoutMillis()
                var readTimeout = chain.readTimeoutMillis()
                var writeTimeout = chain.writeTimeoutMillis()

                val oldRequest = chain.request()
                val connectNew = oldRequest.header(CONNECT_TIMEOUT)
                val readNew = oldRequest.header(READ_TIMEOUT)
                val writeNew = oldRequest.header(WRITE_TIMEOUT)

                connectNew?.let {
                    connectTimeout = Integer.valueOf(it)
                }
                readNew?.let {
                    readTimeout = Integer.valueOf(it)
                }
                writeNew?.let {
                    writeTimeout = Integer.valueOf(it)
                }

                val builder = oldRequest.newBuilder().apply {
                    removeHeader(CONNECT_TIMEOUT)
                    removeHeader(READ_TIMEOUT)
                    removeHeader(WRITE_TIMEOUT)
                    addHeader(ApiParams.DEVICE_TOKEN, UserRepo.getUser().fcmToken ?: "")
                    addHeader(ApiParams.DEVICE_ID, getDeviceId(ApplicationClass.instance))
                    addHeader(ApiParams.PLATFORM, "1")
                    addHeader(ApiParams.OFFSET, getTimeZoneOffset())
                    addHeader(ApiParams.TIMEZONE, TimeZone.getDefault().id)
                    val user = UserRepo.getUser()
                    if (user.loggedIn && user.token != null) {
                        addHeader(ApiParams.AUTHORIZATION, BuildConfig.AUTHORIZATION + "|" + user.token)
                    } else {
                        addHeader(ApiParams.AUTHORIZATION, BuildConfig.AUTHORIZATION)
                    }
                }

                //log events to a file
                GlobalScope.launch(Dispatchers.IO) {
                    val calender = Calendar.getInstance()
                    val params = StringBuilder()
                    val dateTime = CalenderUtil.getFullDate(calender) + " " + CalenderUtil.getFullTimeWithSecAndMillis(calender)
                    params.append(dateTime)
                    params.append("\n")
                    params.append(oldRequest.method + " " + oldRequest.url.toString())
                    params.append("\n")
                    val body = oldRequest.body.bodyToString()
                    if (body.isNotEmpty() && body != "null") {
                        params.append("params: $body")
                    }
                    params.append("\n")

                    try {
                        /*if (BuildConfig.BUILD_TYPE == "production") {*/
                        if (ActivityCompat.checkSelfPermission(ApplicationClass.instance, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                            val fileName = "PagueloFacil_log.txt"
                            val f = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName)
                            f.appendText(params.toString() + "\n")
                        }
                    } catch (e: Exception) {
                        Timber.e(e)
                    }
                }
                return chain
                    .withConnectTimeout(connectTimeout, TimeUnit.MILLISECONDS)
                    .withReadTimeout(readTimeout, TimeUnit.MILLISECONDS)
                    .withWriteTimeout(writeTimeout, TimeUnit.MILLISECONDS)
                    .proceed(builder.build())
            }
        }

        fun RequestBody?.bodyToString(): String {
            if (this == null) return ""
            val buffer = okio.Buffer()
            writeTo(buffer)
            return buffer.readUtf8()
        }
    }

}
package com.paguelofacil.posfacil.di

import android.content.Context
import android.content.pm.PackageManager
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.google.gson.GsonBuilder
import com.paguelofacil.posfacil.ApplicationClass
import com.paguelofacil.posfacil.BuildConfig
import com.paguelofacil.posfacil.repository.UserRepo
import com.paguelofacil.posfacil.util.CalenderUtil
import com.paguelofacil.posfacil.util.Constantes.ApiParams
import com.paguelofacil.posfacil.util.Constantes.AppConstants.Companion.CONNECT_TIMEOUT
import com.paguelofacil.posfacil.util.Constantes.AppConstants.Companion.READ_TIMEOUT
import com.paguelofacil.posfacil.util.Constantes.AppConstants.Companion.WRITE_TIMEOUT
import com.paguelofacil.posfacil.util.MyCallAdapterFactory
import com.paguelofacil.posfacil.util.getDeviceId
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Headers
import java.lang.reflect.Modifier
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Provides
    @Singleton
    internal fun provideGson(): GsonConverterFactory {
        return GsonConverterFactory.create(
            GsonBuilder()
                .excludeFieldsWithModifiers(Modifier.TRANSIENT)
                .disableHtmlEscaping().create()
        )
    }

    @Provides
    @Singleton
    internal fun providesAppVersion(context: Context): String {
        var appVersion = "Desconocido (>= 1.1.8)"
        try {
            val packageInfo =
                context.packageManager.getPackageInfo(context.getPackageName(), 0)
            appVersion = packageInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        println("NetworkModule providesAppVersion appVersion : $appVersion")
        return appVersion
    }

    @QualificadoresModule.InterceptorMiddle
    @Provides
    @Singleton
    internal fun providesInterceptorMiddle(appVersion: String): Interceptor {
        return Interceptor { chain ->
            val original = chain.request()
            val user = UserRepo.getUser()
            val requestBuilder = original.newBuilder()
                .header("X-Version-App", appVersion)
                .header(ApiParams.DEVICE_TOKEN, user.fcmToken ?: "")
                .header(ApiParams.DEVICE_ID, getDeviceId(ApplicationClass.instance))
                .header(ApiParams.PLATFORM, "1")
                .header(ApiParams.OFFSET, CalenderUtil.getTimeZoneOffset())
                .header(ApiParams.TIMEZONE, TimeZone.getDefault().id)
                .header(
                    ApiParams.AUTHORIZATION,
                    if (user.loggedIn && user.token != null) (BuildConfig.AUTHORIZATION + "|" + user.token) else BuildConfig.AUTHORIZATION
                )
                .method(original.method, original.body)
            val request = requestBuilder.build()
            chain.proceed(request)
        }
    }

    @QualificadoresModule.InterceptorPagueloFacil
    @Provides
    @Singleton
    internal fun providesInterceptor(appVersion: String): Interceptor {
        return Interceptor { chain ->
            val original = chain.request()
            val user = UserRepo.getUser()
            val requestBuilder = original.newBuilder()

                .header("Accept", "*/*")
                .header("Accept-Encoding", "gzip, deflate, br")
                .header(
                    ApiParams.AUTHORIZATION, BuildConfig.AUTHORIZATION_PAGUELOFACIL
                )
                .method(original.method, original.body)
            val request = requestBuilder.build()
            chain.proceed(request)
        }
    }

    @QualificadoresModule.OkHttpMiddle
    @Provides
    @Singleton
    internal fun provideOkHttpClientMiddle(
        @QualificadoresModule.InterceptorMiddle interceptor: Interceptor,
        context: Context
    ): OkHttpClient {
        val okHttpClient = OkHttpClient.Builder()
        okHttpClient.connectTimeout(CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
        okHttpClient.readTimeout(READ_TIMEOUT, TimeUnit.MILLISECONDS)
        okHttpClient.writeTimeout(WRITE_TIMEOUT, TimeUnit.MILLISECONDS)
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        okHttpClient.addInterceptor(interceptor)
            .addInterceptor(httpLoggingInterceptor.apply {
                httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            })
        if (BuildConfig.DEBUG) {
            okHttpClient.addInterceptor(ChuckerInterceptor(context))
        }
        return okHttpClient.build()
    }

    @QualificadoresModule.OkHttpPagueloFacil
    @Provides
    @Singleton
    internal fun provideOkHttpClient(
        @QualificadoresModule.InterceptorPagueloFacil interceptor: Interceptor,
        context: Context
    ): OkHttpClient {
        val okHttpClient = OkHttpClient.Builder()
        okHttpClient.connectTimeout(CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
        okHttpClient.readTimeout(READ_TIMEOUT, TimeUnit.MILLISECONDS)
        okHttpClient.writeTimeout(WRITE_TIMEOUT, TimeUnit.MILLISECONDS)
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        okHttpClient.addInterceptor(interceptor)
            .addInterceptor(httpLoggingInterceptor.apply {
                httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            })
        if (BuildConfig.DEBUG) {
            okHttpClient.addInterceptor(ChuckerInterceptor(context))
        }
        return okHttpClient.build()
    }

    @QualificadoresModule.RetrofitMiddle
    @Provides
    @Singleton
    internal fun provideRetrofitMiddle(
        gsonConverterFactory: GsonConverterFactory,
        @QualificadoresModule.OkHttpMiddle okHttpClient: OkHttpClient
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(gsonConverterFactory) // Serialize Objects
            .addCallAdapterFactory(MyCallAdapterFactory()) //Set call to return {@link Observable}
            .build()
    }

    @QualificadoresModule.RetrofitPagueloFacil
    @Provides
    @Singleton
    internal fun provideRetrofitPagueloFacil(
        gsonConverterFactory: GsonConverterFactory,
        @QualificadoresModule.OkHttpPagueloFacil okHttpClient: OkHttpClient
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL_PAGUELOFACIL)
            .client(okHttpClient)
            .addConverterFactory(gsonConverterFactory) // Serialize Objects
            .addCallAdapterFactory(MyCallAdapterFactory()) //Set call to return {@link Observable}
            .build()
    }
}
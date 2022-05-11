package com.paguelofacil.posfacil.di

import android.app.Application
import android.content.Context
import com.paguelofacil.posfacil.repository.UserService
import com.paguelofacil.posfacil.repository.cobro.CobroService
import com.paguelofacil.posfacil.repository.language.LanguageService
import com.paguelofacil.posfacil.repository.qr.QRService
import com.paguelofacil.posfacil.repository.refund.RefundService
import com.paguelofacil.posfacil.repository.report.ReportService
import com.paguelofacil.posfacil.repository.transaction.TransactionService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ServicesModule {

    @Singleton
    @Provides
    internal fun provideCobroService(@QualificadoresModule.RetrofitMiddle retrofit: Retrofit): CobroService {
        return retrofit.create(CobroService::class.java)
    }

    @Singleton
    @Provides
    internal fun provideUserService(@QualificadoresModule.RetrofitMiddle retrofit: Retrofit): UserService {
        return retrofit.create(UserService::class.java)
    }

    @Singleton
    @Provides
    internal fun provideTransactionService(@QualificadoresModule.RetrofitMiddle retrofit: Retrofit): TransactionService {
        return retrofit.create(TransactionService::class.java)
    }

    @Singleton
    @Provides
    internal fun provideRefundService(@QualificadoresModule.RetrofitMiddle retrofit: Retrofit): RefundService {
        return retrofit.create(RefundService::class.java)
    }

    @Singleton
    @Provides
    internal fun provideReportService(@QualificadoresModule.RetrofitMiddle retrofit: Retrofit): ReportService {
        return retrofit.create(ReportService::class.java)
    }

    @Singleton
    @Provides
    internal fun provideQRService(@QualificadoresModule.RetrofitMiddle retrofit: Retrofit): QRService {
        return retrofit.create(QRService::class.java)
    }

    @Singleton
    @Provides
    internal fun provideLanguageService(@QualificadoresModule.RetrofitPagueloFacil retrofit: Retrofit): LanguageService {
        return retrofit.create(LanguageService::class.java)
    }


    @Provides
    @Singleton
    internal fun provideContext(application: Application): Context {
        return application.applicationContext
    }

}
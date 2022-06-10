package com.paguelofacil.posfacil.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier

@Module
@InstallIn(SingletonComponent::class)
class QualificadoresModule {

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class RetrofitPagueloFacil

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class RetrofitMiddle

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class InterceptorPagueloFacil

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class InterceptorMiddle

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class OkHttpPagueloFacil

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class OkHttpMiddle

}
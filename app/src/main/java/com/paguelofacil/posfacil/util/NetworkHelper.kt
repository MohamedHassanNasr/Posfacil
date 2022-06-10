package com.paguelofacil.posfacil.util

import com.google.gson.GsonBuilder
import okhttp3.Request
import okio.Timeout
import retrofit2.*
import java.io.IOException
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

sealed class Resultado<out T> {
    data class Success<T>(val data: T?) : Resultado<T>()
    data class Failure(val statusCode: Int, val responseError: ErrorResponse?) :
        Resultado<Nothing>()
}

class MyCallAdapterFactory : CallAdapter.Factory() {
    override fun get(
        returnType: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ) = when (getRawType(returnType)) {
        Call::class.java -> {
            val callType = getParameterUpperBound(0, returnType as ParameterizedType)
            when (getRawType(callType)) {
                Resultado::class.java -> {
                    val resultType = getParameterUpperBound(0, callType as ParameterizedType)
                    ResultAdapter(resultType)
                }
                else -> null
            }
        }
        else -> null
    }
}

class ResultAdapter(
    private val type: Type
) : CallAdapter<Type, Call<Resultado<Type>>> {
    override fun responseType() = type
    override fun adapt(call: Call<Type>): Call<Resultado<Type>> =
        ResultCall(call)
}

class ResultCall<T>(proxy: Call<T>) : CallDelegate<T, Resultado<T>>(proxy) {
    override fun enqueueImpl(callback: Callback<Resultado<T>>) =
        proxy.enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                val code = response.code()
                val result = if (response.isSuccessful) {
                    val body = response.body()
                    Resultado.Success(body)
                } else {
                    val responseError =
                        GsonBuilder().create().fromJson(
                            response.errorBody()!!.string(), ErrorResponse::class.java
                        )
                    Resultado.Failure(
                        code,
                        responseError
                    )
                }

                callback.onResponse(this@ResultCall, Response.success(result))
            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                val result = if (t is IOException) {
                    Resultado.Failure(1, null)
                } else {
                    Resultado.Failure(0, null)
                }
                callback.onResponse(this@ResultCall, Response.success(result))
            }
        })

    override fun cloneImpl() = ResultCall(proxy.clone())
    override fun timeout(): Timeout {
        TODO("Not yet implemented")
    }
}

abstract class CallDelegate<TIn, TOut>(
    protected val proxy: Call<TIn>
) : Call<TOut> {
    override fun execute(): Response<TOut> = throw NotImplementedError()
    override final fun enqueue(callback: Callback<TOut>) = enqueueImpl(callback)
    override final fun clone(): Call<TOut> = cloneImpl()

    override fun cancel() = proxy.cancel()
    override fun request(): Request = proxy.request()
    override fun isExecuted() = proxy.isExecuted
    override fun isCanceled() = proxy.isCanceled

    abstract fun enqueueImpl(callback: Callback<TOut>)
    abstract fun cloneImpl(): Call<TOut>
}

data class ErrorResponse(
    val apiEstado: String?,
    val apiMensaje: String?
)
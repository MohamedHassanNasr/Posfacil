package com.paguelofacil.posfacil.model

import com.google.gson.annotations.SerializedName

data class UtilsQrResponse(
    val args: Args,
    val headers: Headers,
    val url: String
) {
    class Args

    data class Headers(
        val accept: String,
        @SerializedName("accept-encoding") val acceptEncoding: String,
        val authorization: String,
        @SerializedName("cache-control") val cacheControl: String,
        val cookie: String,
        val host: String,
        @SerializedName("postman-token") val postmanToken: String,
        @SerializedName("user-agent") val userAgent: String,
        @SerializedName("x-amzn-trace-id") val xAmznTraceId: String,
        @SerializedName("x-forwarded-port") val xForwardedPort: String,
        @SerializedName("x-forwarded-proto") val xForwardedProto: String
    )
}
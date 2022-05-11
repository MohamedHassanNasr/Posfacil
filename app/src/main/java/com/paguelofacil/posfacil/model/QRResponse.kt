package com.paguelofacil.posfacil.model

import com.google.gson.annotations.SerializedName

data class QRResponse (
    @SerializedName("code")
    val code: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("processTx")
    val processTx: Any? = null,
    @SerializedName("payActivity")
    val payActivity: PayActivity,
    @SerializedName("qrInfo")
    val qrInfo: String,
    @SerializedName("expireDate")
    val expireDate: Any? = null,
    @SerializedName("_id")
    val id: String
)

data class PayActivity (
    val useFunds: Boolean,
    val idUSRService: Long,
    val idMerchantService: Any? = null,
    val sourceSearch: String,
    val customFieldValues: List<Any?>,
    val batchInfo: String,
    val serviceCode: Any? = null,
    val codeProcess: Any? = null,
    val txChannel: Any? = null,
    val idActivity: Any? = null,
    val idUSRCard: Long,
    val cvv: Any? = null,
    val dataPayment: Any? = null,
    val processWithCodeInfo: Any? = null,
    val currency: Any? = null,
    val conversionDetails: Any? = null,
    val codOper: Any? = null,
    val forcePayWithUser: Any? = null,
    val paymentMethod: Any? = null,
    val isBatchProcessing: Boolean
)

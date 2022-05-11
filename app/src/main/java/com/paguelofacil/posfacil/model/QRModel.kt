package com.paguelofacil.posfacil.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

data class SystemParam(
    val headerStatus: HeaderStatus,
    val serverTime: String,
    val data: DataSystem
)

data class DataSystem(
    val prop: String,
    val values: ValuesSystem
)

data class ValuesSystem(
    val _url_qr: String
)

data class RequestQr(
    val type: String,
    val qrInfo: String
)

data class QrInfo(
    val type: String = "POS",
    val idSearch: String,
    val tx: String,
    val amount: String,
    val taxes: String,
    val description: String,
    val others: Others
)

data class Others(
    val txChannel: String = "PWA",
    val idUser: String,
    val idMerchant: String,
    val tip: String
)

data class QrResult(
    val headerStatus: HeaderStatus,
    val serverTime: String,
    val data: DataQr
)

data class DataQr(
    val code: String,
    val type: String,
    val qrInfo: String
)

@Parcelize
data class QrSend(
    val amount: String,
    val taxes: String,
    val tip: String
): Parcelable

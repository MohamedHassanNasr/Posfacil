package com.paguelofacil.posfacil.tools

import com.google.android.gms.common.util.Hex
import okio.ByteString.Companion.decodeHex
import timber.log.Timber


fun convertStringToHex(str: String): String {

    Timber.e("JSON CONER $str")
    var hex = ""

    for (i in str.indices) {
        val ch = str[i]

        val integer = ch.code

        val part = Integer.toHexString(integer)

        hex += part
    }

    return hex
}

data class ModelName(
    val firstName: String,
    val lastName: String
)

fun getNames(fullName: String): ModelName{
    if (fullName.startsWith("^S/")){
        var parts  = fullName.drop(3).split(" ").toMutableList()
        val firstName = parts.firstOrNull()
        parts.removeAt(0)
        val lastName = parts.joinToString(" ")

        Timber.e("SEPARADOR ${fullName.substring(IntRange(0, 1))}")

        return ModelName(
            firstName ?: "",
            lastName
        )
    }else if (fullName.startsWith("^")){
        var parts  = fullName.drop(1).split(" ").toMutableList()
        val firstName = parts.firstOrNull()
        parts.removeAt(0)
        val lastName = parts.joinToString(" ")

        Timber.e("SEPARADOR ${fullName.substring(IntRange(0, 1))}")

        return ModelName(
            firstName ?: "",
            lastName
        )
    }else{
        var parts  = fullName.split(" ").toMutableList()
        val firstName = parts.firstOrNull()
        parts.removeAt(0)
        val lastName = parts.joinToString(" ")

        Timber.e("SEPARADOR ${fullName.substring(IntRange(0, 1))}")

        return ModelName(
            firstName ?: "",
            lastName
        )
    }

}
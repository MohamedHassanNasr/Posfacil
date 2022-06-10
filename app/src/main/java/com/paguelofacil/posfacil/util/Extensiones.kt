package com.paguelofacil.posfacil.util


fun String?.nullToEmpty(): String {
    if (this == null) return ""
    return this
}

fun Double?.nullToCero(): Double {
    if (this == null) return 0.0
    return this
}

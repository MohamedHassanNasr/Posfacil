package com.paguelofacil.posfacil.model

data class GetSearchCodeByJson(
    val type: String = "POS",
    val idSearch: String = "556ASDF65AFSDF5",
    val amount: Double,
    val discount: Double,
    val taxes: Double,
    val currency: Double? = null,
    val description: String,
    val others: Others
) {
    data class Others(
        val txChannel: String = "PWA",
        val idUser: Int,
        val idMerchant: Int,
        val tip: Double
    )
}
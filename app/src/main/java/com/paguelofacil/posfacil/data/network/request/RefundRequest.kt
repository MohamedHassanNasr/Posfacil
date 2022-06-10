package com.paguelofacil.posfacil.data.network.request

data class RefundRequest (
    val idRelatedTransaction:String = "",
    val amount:Double = 0.00,
    val description:String = "",
    val idMerchant:String = "",
    val refundAction:String = "REQUEST",
    val txType:String = "REVERSE_AUTH",
)
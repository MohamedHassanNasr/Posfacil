package com.paguelofacil.posfacil.model

import com.paguelofacil.posfacil.data.network.response.TransactionApiResponse
import java.util.*

data class Transaction(val id:Int, val mount:String, val fechaHora: String,val detailNameCard:String,val tipo:Int) {
    companion object {
        fun fromApiResponse(apiResponse: TransactionApiResponse) : Transaction {
            return Transaction(
                apiResponse.idTransaction?.toInt() ?: 0,
                String.format("%.2f", apiResponse.amount ?: 0),
                apiResponse.dateTms ?: Calendar.getInstance().toString(),
                apiResponse.cardVisibleNum ?: "9932",
                apiResponse.status?.toInt() ?: 1
            )
        }
    }
}




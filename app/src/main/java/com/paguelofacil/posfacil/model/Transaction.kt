package com.paguelofacil.posfacil.model

import android.os.Parcelable
import com.paguelofacil.posfacil.data.network.response.TransactionApiResponse
import com.paguelofacil.posfacil.util.*
import kotlinx.android.parcel.Parcelize
import kotlin.math.absoluteValue

@Parcelize
data class Transaction(val id: Int, val opCode: String,
                       val amount: Double, val tax: Double,
                       val tip: Double, val date: String,
                       val cardNumber: String, val cardType: String,
                       val status: String, val operatorId: Int
                       ) : Parcelable {
    companion object {
        fun fromApiResponse(apiResponse: TransactionApiResponse) : Transaction {
            return Transaction(
                apiResponse.idTx ?: 0,
                apiResponse.codOper ?: "",
                apiResponse.amount ?: 0.00,
                apiResponse.tax ?: 0.00,
                apiResponse.tip ?: 0.00,
                apiResponse.date ?: "",
                apiResponse.cardNumber ?: "Visa",
                apiResponse.cardType ?: "Visa",
                apiResponse.status ?: "",
                apiResponse.operatorId ?: 0,
            )
        }
    }

    val typeStr = if(amount > 0) "Cobro" else "Reembolso"

    val absoluteAmount = amount.absoluteValue
    val amountStr = formatAmount(absoluteAmount)
    val currencyAmountStr = formatCurrencyAmount(absoluteAmount)
    val possitiveCurrencyAmountStr = formatPossitiveCurrencyAmount(absoluteAmount)
    val negativeCurrencyAmountStr = formatNegativeCurrencyAmount(absoluteAmount)

    val taxAmountStr = formatAmount(tax)
    val currencyTaxAmountStr = formatCurrencyAmount(tax)

    val tipAmountStr = formatAmount(tip)
    val currencyTipAmountStr = formatCurrencyAmount(tip)

    val originalAmount = absoluteAmount - (tax + tip)
    val originalAmountStr = formatAmount(originalAmount)
    val currencyOriginalAmountStr = formatCurrencyAmount(originalAmount)

    val formattedDateTime = convertISOToLocalDateTime(date)
    val formattedDate = convertISOToLocalDate(date)
}
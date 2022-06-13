package com.paguelofacil.posfacil.tools

data class TransactionRequest(
    val additionalData: AdditionalData,
    val amount: Double,
    val codeProcess: String = "123",
    val concept: String,
    val customFieldValues: List<CustomFieldValue> = listOf(CustomFieldValue()),
    val description: String,
    val email: String,
    val idMerchant: Int,
    val idMerchantService: Int = 5462,
    val isOldVersion: Boolean = false,
    val phone: String,
    val requestPay: RequestPay,
    val taxAmount: Double,
    val transactionsType: String = "AUTH",
    val txChannel: String = "POS"
) {
    data class AdditionalData(
        val pos: Pos
    ) {
        data class Pos(
            val idMerchant: Int,
            val idUser: Int,
            val serial: String
        )
    }

    data class CustomFieldValue(
        val id: String = "TIP",
        val nameOrLabel: String = "TIP",
        val value: Int = 1
    )

    data class RequestPay(
        val cardInformation: CardInformation
    ) {
        data class CardInformation(
            val cardNumber: String,
            val cardType: String,
            val cvv: String,
            val expMonth: String,
            val expYear: String,
            val firstName: String,
            val lastName: String
        )
    }
}
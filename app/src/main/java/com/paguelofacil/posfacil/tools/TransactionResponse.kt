package com.paguelofacil.posfacil.tools

data class TransaccionResponse(
    val data: Data,
    val headerStatus: HeaderStatus,
    val message: Any?,
    val requestId: String,
    val serverTime: String,
    val success: Boolean
) {
    data class Data(
        val authStatus: String,
        val binInfo: BinInfo,
        val cardToken: String,
        val cardType: String,
        val codOper: String,
        val date: String,
        val displayNum: String,
        val email: String,
        val idUsr: Int,
        val inRevision: Boolean,
        val isExternalUrl: Boolean,
        val messageSys: String,
        val name: String,
        val operationType: String,
        val requestPayAmount: Double,
        val returnUrl: String,
        val revisionLevel: Any?,
        val revisionOptions: Any?,
        val status: Int,
        val totalPay: String,
        val type: String,
        val userLogn: String,
        val userName: String
    ) {
        data class BinInfo(
            val credit_card: CreditCard,
            val risk_score: Double
        ) {
            data class CreditCard(
                val country: String,
                val issuer: Issuer
            ) {
                data class Issuer(
                    val name: String
                )
            }
        }
    }

    data class HeaderStatus(
        val code: Int,
        val description: String
    )
}
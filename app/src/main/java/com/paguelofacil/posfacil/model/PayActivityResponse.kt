package com.paguelofacil.posfacil.model

data class PayActivityResponse(
    val data: Data,
    val headerStatus: HeaderStatus,
    val message: Any?,
    val requestId: String,
    val serverTime: String,
    val success: Boolean
) {
    data class Data(
        val activity: Activity,
        val authStatus: String,
        val codOper: String,
        val code: String,
        val idCustomer: Any?,
        val idCustomerCard: Any?,
        val messageSys: String,
        val printData: PrintData,
        val status: Int,
        val statusDesc: String,
        val totalPay: Double
    ) {
        data class Activity(
            val amount: Double,
            val amountAfterTaxes: Double,
            val anonymousInfo: Any?,
            val code: String,
            val commisions: Double,
            val creationDate: String,
            val currency: String,
            val description: String,
            val discount: Double,
            val endDate: String,
            val idActivity: Int,
            val idActivityRelated: Int,
            val idMerchant: Int,
            val idPaymentStation: Any?,
            val idPromo: Any?,
            val idService: Int,
            val idUsr: Int,
            val isParent: Any?,
            val nameConcept: String,
            val othersDiscounts: Double,
            val processData: Any?,
            val providerStatus: Int,
            val providerStatusDesc: String,
            val reserveIsLiberated: Boolean,
            val reserveLiberationDate: Any?,
            val sourceSearch: String,
            val status: Int,
            val taxes: Double,
            val totalCharge: Double,
            val totalReserve: Double,
            val txIdMerchant: Int,
            val txIdUsr: Int,
            val type: String
        )

        class PrintData
    }

    data class HeaderStatus(
        val code: Int,
        val description: String
    )
}
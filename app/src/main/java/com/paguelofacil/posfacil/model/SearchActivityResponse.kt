package com.paguelofacil.posfacil.model

data class SearchActivityResponse(
    val data: Data,
    val headerStatus: HeaderStatus,
    val message: Any?,
    val requestId: String,
    val serverTime: String,
    val success: Boolean
) {
    data class Data(
        val amount: Double,
        val amountAfterTaxes: Double,
        val anonymousInfo: Any?,
        val code: Any?,
        val commisions: Double,
        val creationDate: Any?,
        val currency: Any?,
        val description: String,
        val discount: Double,
        val endDate: Any?,
        val idActivity: Any?,
        val idActivityRelated: Any?,
        val idMerchant: Int,
        val idPaymentStation: Any?,
        val idPromo: Any?,
        val idService: Int,
        val idUsr: Int,
        val isParent: Any?,
        val nameConcept: String,
        val othersDiscounts: Double,
        val payMethodCommission: PayMethodCommission,
        val payMethods: List<PayMethod>,
        val processData: Any?,
        val providerStatus: Any?,
        val providerStatusDesc: Any?,
        val reserveIsLiberated: Boolean,
        val reserveLiberationDate: Any?,
        val sourceSearch: String,
        val status: Int,
        val taxes: Double,
        val totalCharge: Double,
        val totalReserve: Double,
        val txIdMerchant: Int,
        val txIdUsr: Any?,
        val type: String
    ) {
        data class PayMethodCommission(
            val DEFAULT_: DEFAULT
        ) {
            data class DEFAULT(
                val card: Card,
                val funds: Funds
            ) {
                data class Card(
                    val amount: Double,
                    val amountAfterTaxes: Double,
                    val commissionAmount: Double,
                    val commissions: Double,
                    val costTx: Double,
                    val discount: Double,
                    val othersDiscounts: Double,
                    val taxes: Double,
                    val totalCharge: Double,
                    val totalCommission: Double
                )

                data class Funds(
                    val amount: Double,
                    val amountAfterTaxes: Double,
                    val commissionAmount: Double,
                    val commissions: Double,
                    val costTx: Double,
                    val discount: Double,
                    val othersDiscounts: Double,
                    val taxes: Double,
                    val totalCharge: Double,
                    val totalCommission: Double
                )
            }
        }

        data class PayMethod(
            val gateway: Gateway,
            val method: Method,
            val myMethods: List<MyMethod>
        ) {
            data class Gateway(
                val config: Config,
                val currency: String?,
                val currencyCode: String,
                val currencySymbol: String,
                val gatewayCode: String,
                val gatewayName: String,
                val idGateway: Int,
                val status: Int,
                val typeGroup: List<String>,
                val typeProcess: String
            ) {
                data class Config(
                    val config: List<Config>,
                    val idMerchant: Int,
                    val idMerchantService: Int,
                    val urlProcess: String
                ) {
                    data class Config(
                        val coin: String,
                        val decimals: Int,
                        val format: String,
                        val network: String?,
                        val pair: String
                    )
                }
            }

            data class Method(
                val bigLogo: Any?,
                val cardNumberLength: Int,
                val code: String,
                val cvvLength: Int,
                val description: String,
                val expression: String,
                val idCreditCard: Int,
                val logo: Any?,
                val masked: String,
                val name: String,
                val status: Int,
                val typeGroup: String
            )

            data class MyMethod(
                val active: Boolean,
                val blocked: Boolean,
                val daysToExpired: Any?,
                val expired: Boolean,
                val forceReStorage: Boolean,
                val idRelCard: Int,
                val idUsr: Int,
                val idUsrPfCard: Int?,
                val idUsrVerified: Int?,
                val isDefault: Boolean,
                val lastUsed: String,
                val nickName: String,
                val parentIdRelCard: Any?,
                val regCard: RegCard,
                val regDate: String,
                val validationRequired: Boolean,
                val verifiedDate: String
            ) {
                data class RegCard(
                    val cardNumber: String,
                    val cardToken: String,
                    val cardType: String,
                    val deleteIn: Int,
                    val expMonth: String,
                    val expYear: String,
                    val expired: Boolean,
                    val gateway: String,
                    val idGateway: Int,
                    val idRegCard: Int,
                    val idUsrPfCard: Int,
                    val lastUsed: String,
                    val nameOnCard: String,
                    val regDate: String,
                    val regStorage: Boolean,
                    val status: Int,
                    val typeGroup: String,
                    val visibleNum: String
                )
            }
        }
    }

    data class HeaderStatus(
        val code: Int,
        val description: String
    )
}
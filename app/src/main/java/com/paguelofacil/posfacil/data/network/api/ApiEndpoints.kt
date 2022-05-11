package com.paguelofacil.posfacil.data.network.api

/**
 * Every API endpoint used in the project for any API call
 *
 * @constructor Create empty Api endpoints
 */
object ApiEndpoints {
    const val ATIK_SERIAL = "test-atik-dev-serial"
    /**
     * Auth / Account
     */
    const val LOGIN = "Login"
    const val PASSWORD_RECOVERY = "RecoveryPassword"
    const val USERS = "Users"
    const val PARAMS_SYSTEM="SystemConfig"
    const val SEARCH_USER = "ShortUsrUniversalUsers"

    /**
     * RefreshLogin
     * */
    const val  REFRESH_LOGIN_USER = "RefreshUserLogin"

    /** reporte de ventas*/
    const val REPORTS_SELL = "PosReport/"

    /**
     * Payment
     */
    const val QR_PROCESS_INFO = "QrProcessInfo"

    /**
     * Language
     */
    const val LANGUAGE_FILES = "LanguageFiles/release"

    /**
     * Transactions
     */
    const val V_ADMIN_TXS = "V_AdminTxs"
    const val POS_TRANSACTION = "PosTransaction/{serial}"

    /**
     * Refunds
     */
    const val REFUND_TX = "RefundTx"

    /**
     * Reports
     */
    const val POS_STATUS = "PosStatus"

    /** REPORT X*/
    const val POS_COMMAND = "PosCommand"

    const val MERCHANT_URL = "PfsysMerchants?conditional=idMerchant"

    const val PROCESS_TX = "ProcessTx"

    const val COMPROBANTE_NO_OPTION = "SendReceiptPay"

    const val COMPROBANTE = "PosPaymentReceipt"

    const val QR_ENDPOINT = "QrProcessInfo"
}
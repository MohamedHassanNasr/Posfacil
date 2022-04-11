package com.paguelofacil.posfacil.data.network.api

/**
 * Every API endpoint used in the project for any API call
 *
 * @constructor Create empty Api endpoints
 */
object ApiEndpoints {
    /**
     * Auth / Account
     */
    const val LOGIN = "Login"
    const val PASSWORD_RECOVERY = "RecoveryPassword"
    const val USERS = "Users"
    const val PARAMS_SYSTEM="SystemConfig"
    const val SEARCH_USER = "ShortUsrUniversalUsers"

    /**
     * Payment
     */
    const val QR_PROCESS_INFO = "QrProcessInfo"

    /**
     * Transactions
     */
    const val V_ADMIN_TXS = "V_AdminTxs"

    /**
     * Reports
     */
    const val POS_STATUS = "PosStatus"
}
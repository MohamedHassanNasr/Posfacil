package com.paguelofacil.posfacil.util.Constantes


import android.app.NotificationManager
import android.os.Build
import androidx.annotation.RequiresApi

/**
 * Used for constants and classes used throughout the project
 */

// duration in milliseconds after which a local data will get expired
const val GLOBAL_EXPIRE_DURATION = 60 * 60 * 1000

/**
 * Loading state for common loaded on every page
 *
 */
enum class LoadingState {
    LOADING,
    LOADED
}

/**
 * Notification channels for the project
 */
object NotificationChannels {
    @RequiresApi(api = Build.VERSION_CODES.N)
    object Channel1 {
        const val id = "1"
        const val name = "general_notification_channel"
        const val importance = NotificationManager.IMPORTANCE_DEFAULT
    }
}

/**
 * Process types
 */
object ProcessType {
    const val PAYMENT = "PAY"
    const val REQUEST = "CHARGE"
}

object QuickActionType {
    const val CONTACT = 1
    const val SHOP = 2
    const val SERVICE = 3
}

object AccountTypes {
    const val SAVING = "Saving"
    const val CURRENT = "Current"
}

object Banks {
    const val BANK1 = "Banco General"
    const val BANK2 = "Bac International Bank"
    const val BANK3 = "Banistmo"
    const val BANK4 = "Banesco"
}

object ApiParams {
    const val FIELD = "field"
    const val AUTHORIZATION = "Authorization"
    const val TIMEZONE = "timezone"
    const val DEVICE_TOKEN = "devicetoken"
    const val DEVICE_ID = "deviceid"
    const val PLATFORM = "platform"
    const val OFFSET = "offset"
    const val LIMIT = "limit"
    const val SORT = "sort"
    const val FILTER_IN_CAPS = "Filter"
    const val STATUS = "status"
    const val CONDITIONAL = "conditional"
    const val PAYMENT_TYPE = "payment_type"
    const val FILTER_BY_DATE = "filter_by_date"
    const val DATE_FROM = "date_from"
    const val DATE_TO = "date_to"
}

object LastUpdatedOn {
    const val FUND_REQUESTS = "fund_requests"
    const val SERVICES = "services"
    const val SHOPS = "shops"
    const val CONTACTS = "contacts"
    const val PENDING_ACTIVITIES = "pending_activities"
    const val COMPLETED_ACTIVITIES = "completed_activities"
    const val SYSTEM_PARAMS= "system_params"

}

object CoreConstants {
    const val AMOUNT = "amount"
    const val DEEPLINK_DATA = "deep_link_data"
    const val DEVICE_LOCALE = "device_locale"
}
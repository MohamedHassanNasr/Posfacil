package com.paguelofacil.posfacil.util.Constantes


import android.app.NotificationManager
import android.os.Build
import androidx.annotation.RequiresApi
import com.paguelofacil.posfacil.ApplicationClass
import com.paguelofacil.posfacil.R
import com.paguelofacil.posfacil.data.network.api.ApiError
import com.paguelofacil.posfacil.data.network.api.ApiRequestCode
import com.paguelofacil.posfacil.data.network.api.BaseResponse
import com.paguelofacil.posfacil.data.network.api.GenericApiRequest
import retrofit2.Response

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
    const val SYSTEM_PARAMS = "system_params"

}

object CoreConstants {
    const val AMOUNT = "amount"
    const val DEEPLINK_DATA = "deep_link_data"
    const val DEVICE_LOCALE = "device_locale"
}

class AppConstants {

    class IntentConstants {

        class HomeFragment {
            val IMPORT = "IMPORT"
        }
    }

    companion object {
        const val CONNECT_TIMEOUT: Long = 60 * 1000
        const val READ_TIMEOUT: Long = 60 * 1000
        const val WRITE_TIMEOUT: Long = 60 * 1000

        const val IDIOMA_INGLES = "EN"
        const val IDIOMA_ESPANOL = "ES"


        /**
         * Is email valid
         *
         * @param emailId
         * @return true if the [emailId] matched with email pattern. Otherwise false
         */
        fun isEmailValid(emailId: String): Boolean {
            val EMAIL_PATTERN = ("[A-Z0-9a-z-!#$%^&*()_+=|:;'<,>.?/]+@[A-Za-z0-9.]+\\.[A-Za-z]{2,50}")
            return if (emailId.length < 3 || emailId.length > 265) false else {
                emailId.matches(Regex(EMAIL_PATTERN))
            }
        }

        /**
         * Get basic error
         *
         * @param response is the Retrofit response received after an API call
         * @param internetOn would be true if internet is on. False otherwise
         * @return
         */
        fun <T> getBasicError(
            apiCode: Int,
            response: Response<BaseResponse<T>>?,
            internetOn: Boolean,
        ): ApiError {
            val error = ApiError()
            if (response == null && !internetOn) {
                error.message =
                    ApplicationClass.instance.getString(R.string.It_seems_like_you_are_not_connected_with_a_stable_internet)
            } else if (response == null && internetOn) {
                error.message =
                    ApplicationClass.instance.getString(R.string.Unable_to_process_your_request_Please_try_again)
            } else {
                if (response?.body()?.headerStatus?.code == GenericApiRequest.ResponseCode.nonWalletPlatformUser && response.body()!!.message == "Invalid Login Platform WALLET") {
                    error.message =
                        ApplicationClass.instance.getString(R.string.platformIsNotWallet)
                } else {
                    getErrorMsgBasedOnResponseCode(apiCode, response, error)
                }
                error.code = response?.body()!!.headerStatus.code
                error.data = response.body()!!.data
            }
            return error
        }

        /**
         * Check response code received from the server and pass the corresponding error message
         *
         * @param response
         * @param error
         */
        fun <T> getErrorMsgBasedOnResponseCode(
            apiCode: Int,
            response: Response<BaseResponse<T>>?,
            error: ApiError
        ) {
            when (response?.body()?.headerStatus?.code) {
                ApiRequestCode.USER_NOT_LOGGED_IN, ApiRequestCode.SESSION_EXPIRED1, ApiRequestCode.SESSION_EXPIRED2, ApiRequestCode.SESSION_EXPIRED3,
                ApiRequestCode.SESSION_EXPIRED4, ApiRequestCode.SESSION_EXPIRED5, ApiRequestCode.SESSION_EXPIRED6,
                -> {
                    error.message = ApplicationClass.language.sessionTimeout
                }
                GenericApiRequest.ResponseCode.invalidEmailPassword -> {
                    error.message =
                        ApplicationClass.language.invalidEmailPassword
                }
                GenericApiRequest.ResponseCode.weCannotVerifyYourBusiness -> {
                    error.message =
                        ApplicationClass.instance.getString(R.string.weCannotVerifyYourBusiness)    //cambiar
                }
                GenericApiRequest.ResponseCode.disabledAccount -> {
                    error.message = ApplicationClass.language.disabledAccount
                }
                GenericApiRequest.ResponseCode.theInformationDoesNotMatchTheDocumentProvide -> {
                    error.message =
                        ApplicationClass.language.theInformationDoesnotMatchTheDocumentProvide
                }
                GenericApiRequest.ResponseCode.theMinimumAllowedAmount -> {
                    error.message =
                        ApplicationClass.language.theMinimumAllowedAmount
                }
                GenericApiRequest.ResponseCode.theAmountToBeReceivedIsGreater -> {
                    error.message =
                        ApplicationClass.language.theAmountToBeReceivedIsGreater
                }
                GenericApiRequest.ResponseCode.theCardIsNotValid -> {
                    error.message = ApplicationClass.language.theCardIsNotValid
                }
                GenericApiRequest.ResponseCode.theSecurityCodeInNotValid -> {
                    error.message =
                        ApplicationClass.language.theSecurityCodeInNotValid
                }
                GenericApiRequest.ResponseCode.creditCardNumberNotValid -> {
                    error.message =
                        ApplicationClass.language.creditCardNumberNotValid
                }
                GenericApiRequest.ResponseCode.theEmailEnteredIsNotCorrect -> {
                    error.message =
                        ApplicationClass.language.theEmailEnteredIsNotCorrect
                }
                GenericApiRequest.ResponseCode.theNameEnteredIsInvalid -> {
                    error.message =
                        ApplicationClass.language.theNameEnteredIsInvalid
                }
                GenericApiRequest.ResponseCode.theLastNameEnteredIsInvalid -> {
                    error.message =
                        ApplicationClass.language.theLastNameEnteredIsInvalid
                }
                GenericApiRequest.ResponseCode.thePhoneNumberEnteredIsInvalid -> {
                    error.message =
                        ApplicationClass.language.thePhoneNumberEnteredIsInvalid
                }
                GenericApiRequest.ResponseCode.forSecurityReasonsYouCannotMakeMoreThanTransactionsWithTheSameCreditCard -> {
                    error.message =
                        ApplicationClass.language.forSecurityReasonsYouCannotMakeMoreThanTransactionsWithTheSameCreditCard
                }

                GenericApiRequest.ResponseCode.oopsSomethingWentWrongProblemWithTheService -> {
                    error.message =
                        ApplicationClass.language.oppsSomethingWentWrongProblemWithTheService
                }
                GenericApiRequest.ResponseCode.oopsSomethingWentWrongWalletServiceEnabled -> {
                    error.message =
                        ApplicationClass.language.oppsSomethingWentWrongWalletServiceEnabled
                }
                GenericApiRequest.ResponseCode.theSelectedActivityIsInvalid -> {
                    error.message =
                        ApplicationClass.language.theSelectedActivityIsEnvalid
                }
                GenericApiRequest.ResponseCode.theActivityWasCancelledPleaseTryAgain -> {
                    error.message =
                        ApplicationClass.language.theActivityWasCancelledPleaseTryAgain
                }
                GenericApiRequest.ResponseCode.thisPaymentHasAlreadyBeenMadePleaseVerify -> {
                    error.message =
                        ApplicationClass.language.thisPaymentHasAlreadyBeenMadePleaseVerify
                }
                GenericApiRequest.ResponseCode.theActivityHasBeenRejectedTryAgainOrContactOurSupportTeam -> {
                    error.message =
                        ApplicationClass.language.theActivityHasBeenRejectedTryAgainOrContactOurSupportTeam
                }
                GenericApiRequest.ResponseCode.loggingInErrorTryAgain -> {
                    error.message =
                        ApplicationClass.language.loggingInErrorTryAgain
                }
                GenericApiRequest.ResponseCode.wrongCodeTryAgain -> {
                    error.message = ApplicationClass.language.wrongCodeTryAgain
                }
                GenericApiRequest.ResponseCode.thereIsAProblemWithTheMerchantPleaseContactOurSupportTeam -> {
                    error.message =
                        ApplicationClass.language.thereIsAProblemWithTheMerchantPleaseContactOurSupportTeam
                }
                GenericApiRequest.ResponseCode.merchantServerDisabledPleaseContactOurSupportTeam -> {
                    error.message =
                        ApplicationClass.language.merchantServerDisabledPleaseContactOurSupportTeam
                }
                GenericApiRequest.ResponseCode.insufficientFundYouMustHaveAvailableAmountNecessaryToMakeYourPayments -> {
                    error.message =
                        ApplicationClass.language.insufficientFundYouMustHaveAvailableAmountNecessaryToMakeYourPayments
                }
                GenericApiRequest.ResponseCode.makeSureYouHaveValidatedYourCardOrThatYouHaveTheNecessaryAmountToMakeThisAction -> {
                    error.message =
                        ApplicationClass.language.makeSureYouHaveValidatedYourCardOrThatYouHaveTheNecessaryamountToMakeThisAction
                }
                GenericApiRequest.ResponseCode.verifyYourPhoneNumberToContinue -> {
                    error.message =
                        ApplicationClass.language.verifyYourPhoneNumberToContinue
                }
                GenericApiRequest.ResponseCode.firstWeNeedToVerifyYourEmailGoToYourEmailAndTryAgain -> {
                    error.message =
                        ApplicationClass.language.firstWeNeedToVerifyYourEmailGoToYourEmailAndTryAgain
                }
                GenericApiRequest.ResponseCode.firstWeNeedToVerifyYourEmailGoToYourEmailAndTryAgain_1 -> {
                    error.message =
                        ApplicationClass.language.firstWeNeedToVerifyYourEmailGoToYourEmailAndTryAgain
                }
                GenericApiRequest.ResponseCode.firstWeNeedToVerifyYourEmailGoToYourEmailAndTryAgain_2 -> {
                    error.message =
                        ApplicationClass.language.firstWeNeedToVerifyYourEmailGoToYourEmailAndTryAgain
                }
                GenericApiRequest.ResponseCode.addYourIdentityDocumentToContinueYouCanDoItFromTheMainMenu -> {
                    error.message =
                        ApplicationClass.language.addYourIdentityDocumentToContinueYouCanDoItFromTheMainMenu
                }
                GenericApiRequest.ResponseCode.addYourIdentityDocumentToContinueYouCanDoItFromTheMainMenu_1 -> {
                    error.message =
                        ApplicationClass.language.addYourIdentityDocumentToContinueYouCanDoItFromTheMainMenu
                }
                GenericApiRequest.ResponseCode.pleaseTryAgain -> {
                    error.message = ApplicationClass.language.pleaseTryAgain
                }
                GenericApiRequest.ResponseCode.pleaseTryAgain_1 -> {
                    error.message = ApplicationClass.language.pleaseTryAgain
                }
                GenericApiRequest.ResponseCode.pleaseTryAgain_2 -> {
                    error.message = ApplicationClass.language.pleaseTryAgain
                }
                GenericApiRequest.ResponseCode.theAmountExceedsTheMaximumAmountAllowed -> {
                    error.message =
                        ApplicationClass.language.theAmountExceedsTheMaximumAmountAllowed
                }
                GenericApiRequest.ResponseCode.pleaseEnterAValidAmount -> {
                    error.message =
                        ApplicationClass.language.pleaseEnterAValidAmount
                }
                GenericApiRequest.ResponseCode.thePaymentHasBeenDeclined -> {
                    error.message =
                        ApplicationClass.language.thePaymetHasBeenDeclined
                }
                GenericApiRequest.ResponseCode.pleaseVerifyTheInformation -> {
                    error.message =
                        ApplicationClass.language.pleaseVerifyTheInformation
                }
                GenericApiRequest.ResponseCode.pleaseVerifyTheRecipientAndTryAgain -> {
                    error.message =
                        ApplicationClass.language.pleaseVerifyTheRecipientAndTryAgain
                }
                GenericApiRequest.ResponseCode.pleaseVerifyTheRecipientAndTryAgain_1 -> {
                    error.message =
                        ApplicationClass.language.pleaseVerifyTheRecipientAndTryAgain
                }
                GenericApiRequest.ResponseCode.invalidExternalService -> {
                    error.message =
                        ApplicationClass.language.invalidExternalService
                }
                GenericApiRequest.ResponseCode.externalServiceDisable -> {
                    error.message =
                        ApplicationClass.language.externalServiceDisable
                }
                GenericApiRequest.ResponseCode.invalidAccount -> {
                    error.message = ApplicationClass.language.invalidAccount
                }
                GenericApiRequest.ResponseCode.invalidTransactionTryAgainOrContactOurSupportTeam -> {
                    error.message =
                        ApplicationClass.language.invalidTransactionTryAgainOrContactOurSupportTeam
                }
                GenericApiRequest.ResponseCode.transactionErrorTryAgainOrContactOurSupportTeam -> {
                    error.message =
                        ApplicationClass.language.transactionErrorTryAgainOrContactOurSupportTeam
                }
                GenericApiRequest.ResponseCode.invalidPaidTransaction -> {
                    error.message =
                        ApplicationClass.language.invalidPaidTransaction
                }
                GenericApiRequest.ResponseCode.errorPaidTryAgainOrContactOurSupportTeam -> {
                    error.message =
                        ApplicationClass.language.errorPaidTryAgainOrContactOurSupportTeam
                }
                GenericApiRequest.ResponseCode.invalidCheckCodePleaseTryAgain -> {
                    error.message =
                        ApplicationClass.language.invalidCheckCodePleaseTryAgain
                }
                GenericApiRequest.ResponseCode.invalidConfirmationCode -> {
                    error.message =
                        ApplicationClass.language.invalidConfirmationCode
                }
                GenericApiRequest.ResponseCode.invalidNewPassword -> {
                    error.message = ApplicationClass.language.invalidNewPassword
                }
                GenericApiRequest.ResponseCode.CardIsAlreadyAssociatedWithAUser -> {
                    error.message =
                        ApplicationClass.instance.getString(R.string.CardIsAlreadyAssociatedWithAUser)
                }

                GenericApiRequest.ResponseCode.emailAlreadyRegistered -> {
                    error.message =
                        ApplicationClass.language.emailAlreadyRegistered
                }
                else -> {
                    error.message = response?.body()?.headerStatus?.description
                }
            }
        }

    }
}

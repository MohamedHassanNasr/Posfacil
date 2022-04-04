package com.paguelofacil.posfacil.data.network.api

import android.Manifest
import android.content.pm.PackageManager
import android.os.Environment
import androidx.core.app.ActivityCompat
import com.google.gson.Gson
import com.paguelofacil.posfacil.ApplicationClass
import com.paguelofacil.posfacil.R
import com.paguelofacil.posfacil.util.CalenderUtil
import com.paguelofacil.posfacil.util.isInternetAvailable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Response
import timber.log.Timber
import java.io.File
import java.util.*


/**
 * Base class that would perform a network call and check for the response status and decide for
 * the corresponding response to be passed on to the calling function
 *
 * @param T response type
 * @constructor Create empty Generic api request
 */
abstract class GenericApiRequest<T> {

    suspend fun apiRequest(
        apiCode: Int = ApiRequestCode.NO_API,
        call: suspend () -> Response<BaseResponse<T>>,
    ): BaseResponse<T> {
        var baseResponse = BaseResponse<T>()
        if (isInternetAvailable(ApplicationClass.instance)) {
            baseResponse.isInternetOn = true
            try {
                val response: Response<BaseResponse<T>> = call.invoke()
                return if (response.body() != null) {

                    //log events to a file
                    GlobalScope.launch(Dispatchers.IO) {
                        val calender = Calendar.getInstance()
                        val params = StringBuilder()
                        val dateTime =
                            CalenderUtil.getFullDate(calender) + " " + CalenderUtil.getFullTimeWithSecAndMillis(
                                calender
                            )
                        params.append(dateTime)
                        params.append("\n")
                        params.append(response.raw().request.method + " " + response.raw().request.url.toString())
                        params.append("\n")
                        params.append("response: " + Gson().toJson(response.body()).toString())
                        params.append("\n")

                        try {
                            if (ActivityCompat.checkSelfPermission(
                                    ApplicationClass.instance,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                                ) == PackageManager.PERMISSION_GRANTED
                            ) {
                                val fileName = "PagueloFacil_log.txt"
                                val f = File(
                                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                                    fileName
                                )
                                f.appendText(params.toString() + "\n")
                            }
                        } catch (e: Exception) {
                            Timber.e(e)
                        }
                    }

                    if (response.isSuccessful
                        && (response.body()!!.headerStatus.code == 200
                                || response.body()!!.headerStatus.code == 202
                                || response.body()!!.headerStatus.code == 201)
                    ) {
                        baseResponse = response.body()!!
                        baseResponse.requestCode = apiCode
                        baseResponse
                    } else {
                        baseResponse = response.body()!!//mandar body
                        baseResponse.requestCode = apiCode
                        baseResponse.apiError =
                            getBasicError(apiCode, response, baseResponse.isInternetOn)
                        baseResponse
                    }
                } else {
                    baseResponse.requestCode = apiCode
                    baseResponse.apiError = getBasicError(
                        apiCode,
                        response,
                        baseResponse.isInternetOn
                    )
                    baseResponse
                }
            } catch (e: Exception) {
                baseResponse.requestCode = apiCode
                baseResponse.apiError = getBasicError(apiCode, null, true)
                return baseResponse
            }
        } else {
            baseResponse.requestCode = apiCode
            baseResponse.isInternetOn = false
            baseResponse.apiError = getBasicError(apiCode, null, false)
            return baseResponse
        }
    }

    /**
     * Get basic error
     *
     * @param response is the Retrofit response received after an API call
     * @param internetOn would be true if internet is on. False otherwise
     * @return
     */
    private fun getBasicError(
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
            if (response?.body()?.headerStatus?.code == ResponseCode.nonWalletPlatformUser && response.body()!!.message == "Invalid Login Platform WALLET") {
                error.message = ApplicationClass.instance.getString(R.string.platformIsNotWallet)
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
    private fun getErrorMsgBasedOnResponseCode(
        apiCode: Int,
        response: Response<BaseResponse<T>>?,
        error: ApiError
    ) {
        when (response?.body()?.headerStatus?.code) {
            ApiRequestCode.USER_NOT_LOGGED_IN, ApiRequestCode.SESSION_EXPIRED1, ApiRequestCode.SESSION_EXPIRED2, ApiRequestCode.SESSION_EXPIRED3,
            ApiRequestCode.SESSION_EXPIRED4, ApiRequestCode.SESSION_EXPIRED5, ApiRequestCode.SESSION_EXPIRED6,
            -> {
                error.message = ApplicationClass.instance.getString(R.string.sessionTimeout)
            }
            ResponseCode.invalidEmailPassword -> {
                error.message = ApplicationClass.instance.getString(R.string.invalidEmailPassword)
            }
            ResponseCode.weCannotVerifyYourBusiness -> {
                error.message =
                    ApplicationClass.instance.getString(R.string.weCannotVerifyYourBusiness)
            }
            ResponseCode.disabledAccount -> {
                error.message = ApplicationClass.instance.getString(R.string.disabledAccount)
            }
            ResponseCode.theInformationDoesNotMatchTheDocumentProvide -> {
                error.message =
                    ApplicationClass.instance.getString(R.string.theInformationDoesnotMatchTheDocumentProvide)
            }
            ResponseCode.theMinimumAllowedAmount -> {
                error.message =
                    ApplicationClass.instance.getString(R.string.theMinimumAllowedAmount)
            }
            ResponseCode.theAmountToBeReceivedIsGreater -> {
                error.message =
                    ApplicationClass.instance.getString(R.string.theAmountToBeReceivedIsGreater)
            }
            ResponseCode.theCardIsNotValid -> {
                error.message = ApplicationClass.instance.getString(R.string.theCardIsNotValid)
            }
            ResponseCode.theSecurityCodeInNotValid -> {
                error.message =
                    ApplicationClass.instance.getString(R.string.theSecurityCodeInNotValid)
            }
            ResponseCode.creditCardNumberNotValid -> {
                error.message =
                    ApplicationClass.instance.getString(R.string.creditCardNumberNotValid)
            }
            ResponseCode.theEmailEnteredIsNotCorrect -> {
                error.message =
                    ApplicationClass.instance.getString(R.string.theEmailEnteredIsNotCorrect)
            }
            ResponseCode.theNameEnteredIsInvalid -> {
                error.message =
                    ApplicationClass.instance.getString(R.string.theNameEnteredIsInvalid)
            }
            ResponseCode.theLastNameEnteredIsInvalid -> {
                error.message =
                    ApplicationClass.instance.getString(R.string.theLastNameEnteredIsInvalid)
            }
            ResponseCode.thePhoneNumberEnteredIsInvalid -> {
                error.message =
                    ApplicationClass.instance.getString(R.string.thePhoneNumberEnteredIsInvalid)
            }
            ResponseCode.forSecurityReasonsYouCannotMakeMoreThanTransactionsWithTheSameCreditCard -> {
                error.message =
                    ApplicationClass.instance.getString(R.string.forSecurityReasonsYouCannotMakeMoreThanTransactionsWithTheSameCreditCard)
            }

            ResponseCode.oopsSomethingWentWrongProblemWithTheService -> {
                error.message =
                    ApplicationClass.instance.getString(R.string.oppsSomethingWentWrongProblemWithTheService)
            }
            ResponseCode.oopsSomethingWentWrongWalletServiceEnabled -> {
                error.message =
                    ApplicationClass.instance.getString(R.string.oppsSomethingWentWrongWalletServiceEnabled)
            }
            ResponseCode.theSelectedActivityIsInvalid -> {
                error.message =
                    ApplicationClass.instance.getString(R.string.theSelectedActivityIsEnvalid)
            }
            ResponseCode.theActivityWasCancelledPleaseTryAgain -> {
                error.message =
                    ApplicationClass.instance.getString(R.string.theActivityWasCancelledPleaseTryAgain)
            }
            ResponseCode.thisPaymentHasAlreadyBeenMadePleaseVerify -> {
                error.message =
                    ApplicationClass.instance.getString(R.string.thisPaymentHasAlreadyBeenMadePleaseVerify)
            }
            ResponseCode.theActivityHasBeenRejectedTryAgainOrContactOurSupportTeam -> {
                error.message =
                    ApplicationClass.instance.getString(R.string.theActivityHasBeenRejectedTryAgainOrContactOurSupportTeam)
            }
            ResponseCode.loggingInErrorTryAgain -> {
                error.message = ApplicationClass.instance.getString(R.string.loggingInErrorTryAgain)
            }
            ResponseCode.wrongCodeTryAgain -> {
                error.message = ApplicationClass.instance.getString(R.string.wrongCodeTryAgain)
            }
            ResponseCode.thereIsAProblemWithTheMerchantPleaseContactOurSupportTeam -> {
                error.message =
                    ApplicationClass.instance.getString(R.string.thereIsAProblemWithTheMerchantPleaseContactOurSupportTeam)
            }
            ResponseCode.merchantServerDisabledPleaseContactOurSupportTeam -> {
                error.message =
                    ApplicationClass.instance.getString(R.string.merchantServerDisabledPleaseContactOurSupportTeam)
            }
            ResponseCode.insufficientFundYouMustHaveAvailableAmountNecessaryToMakeYourPayments -> {
                error.message =
                    ApplicationClass.instance.getString(R.string.insufficientFundYouMustHaveAvailableAmountNecessaryToMakeYourPayments)
            }
            ResponseCode.makeSureYouHaveValidatedYourCardOrThatYouHaveTheNecessaryAmountToMakeThisAction -> {
                error.message =
                    ApplicationClass.instance.getString(R.string.makeSureYouHaveValidatedYourCardOrThatYouHaveTheNecessaryAmountToMakeThisAction)
            }
            ResponseCode.verifyYourPhoneNumberToContinue -> {
                error.message =
                    ApplicationClass.instance.getString(R.string.verifyYourPhoneNumberToContinue)
            }
            ResponseCode.firstWeNeedToVerifyYourEmailGoToYourEmailAndTryAgain -> {
                error.message =
                    ApplicationClass.instance.getString(R.string.firstWeNeedToVerifyYourEmailGoToYourEmailAndTryAgain)
            }
            ResponseCode.firstWeNeedToVerifyYourEmailGoToYourEmailAndTryAgain_1 -> {
                error.message =
                    ApplicationClass.instance.getString(R.string.firstWeNeedToVerifyYourEmailGoToYourEmailAndTryAgain)
            }
            ResponseCode.firstWeNeedToVerifyYourEmailGoToYourEmailAndTryAgain_2 -> {
                error.message =
                    ApplicationClass.instance.getString(R.string.firstWeNeedToVerifyYourEmailGoToYourEmailAndTryAgain)
            }
            ResponseCode.addYourIdentityDocumentToContinueYouCanDoItFromTheMainMenu -> {
                error.message =
                    ApplicationClass.instance.getString(R.string.addYourIdentityDocumentToContinueYouCanDoItFromTheMainMenu)
            }
            ResponseCode.addYourIdentityDocumentToContinueYouCanDoItFromTheMainMenu_1 -> {
                error.message =
                    ApplicationClass.instance.getString(R.string.addYourIdentityDocumentToContinueYouCanDoItFromTheMainMenu)
            }
            ResponseCode.pleaseTryAgain -> {
                error.message = ApplicationClass.instance.getString(R.string.pleaseTryAgain)
            }
            ResponseCode.pleaseTryAgain_1 -> {
                error.message = ApplicationClass.instance.getString(R.string.pleaseTryAgain)
            }
            ResponseCode.pleaseTryAgain_2 -> {
                error.message = ApplicationClass.instance.getString(R.string.pleaseTryAgain)
            }
            ResponseCode.theAmountExceedsTheMaximumAmountAllowed -> {
                error.message =
                    ApplicationClass.instance.getString(R.string.theAmountExceedsTheMaximumAmountAllowed)
            }
            ResponseCode.pleaseEnterAValidAmount -> {
                error.message =
                    ApplicationClass.instance.getString(R.string.pleaseEnterAValidAmount)
            }
            ResponseCode.thePaymentHasBeenDeclined -> {
                error.message =
                    ApplicationClass.instance.getString(R.string.thePaymetHasBeenDeclined)
            }
            ResponseCode.pleaseVerifyTheInformation -> {
                error.message =
                    ApplicationClass.instance.getString(R.string.pleaseVerifyTheInformation)
            }
            ResponseCode.pleaseVerifyTheRecipientAndTryAgain -> {
                error.message =
                    ApplicationClass.instance.getString(R.string.pleaseVerifyTheRecipientAndTryAgain)
            }
            ResponseCode.pleaseVerifyTheRecipientAndTryAgain_1 -> {
                error.message =
                    ApplicationClass.instance.getString(R.string.pleaseVerifyTheRecipientAndTryAgain)
            }
            ResponseCode.invalidExternalService -> {
                error.message = ApplicationClass.instance.getString(R.string.invalidExternalService)
            }
            ResponseCode.externalServiceDisable -> {
                error.message = ApplicationClass.instance.getString(R.string.externalServiceDisable)
            }
            ResponseCode.invalidAccount -> {
                error.message = ApplicationClass.instance.getString(R.string.invalidAccount)
            }
            ResponseCode.invalidTransactionTryAgainOrContactOurSupportTeam -> {
                error.message =
                    ApplicationClass.instance.getString(R.string.invalidTransactionTryAgainOrContactOurSupportTeam)
            }
            ResponseCode.transactionErrorTryAgainOrContactOurSupportTeam -> {
                error.message =
                    ApplicationClass.instance.getString(R.string.transactionErrorTryAgainOrContactOurSupportTeam)
            }
            ResponseCode.invalidPaidTransaction -> {
                error.message = ApplicationClass.instance.getString(R.string.invalidPaidTransaction)
            }
            ResponseCode.errorPaidTryAgainOrContactOurSupportTeam -> {
                error.message =
                    ApplicationClass.instance.getString(R.string.errorPaidTryAgainOrContactOurSupportTeam)
            }
            ResponseCode.invalidCheckCodePleaseTryAgain -> {
                error.message =
                    ApplicationClass.instance.getString(R.string.invalidCheckCodePleaseTryAgain)
            }
            ResponseCode.invalidConfirmationCode -> {
                error.message =
                    ApplicationClass.instance.getString(R.string.invalidConfirmationCode)
            }
            ResponseCode.invalidNewPassword -> {
                error.message = ApplicationClass.instance.getString(R.string.invalidNewPassword)
            }
            ResponseCode.CardIsAlreadyAssociatedWithAUser -> {
                error.message =
                    ApplicationClass.instance.getString(R.string.CardIsAlreadyAssociatedWithAUser)
            }

            ResponseCode.emailAlreadyRegistered -> {
                error.message = ApplicationClass.instance.getString(R.string.emailAlreadyRegistered)
            }
            else -> {
                error.message = response?.body()?.headerStatus?.description
            }
        }
    }

    /**
     * Error code received from the server for which customised message front end needs to be shown
     *
     * @constructor Create empty Error code types
     */
    object ResponseCode {
        const val weCannotVerifyYourBusiness = 600
        const val disabledAccount = 601
        const val theInformationDoesNotMatchTheDocumentProvide = 602
        const val theMinimumAllowedAmount = 603
        const val theAmountToBeReceivedIsGreater = 604
        const val theCardIsNotValid = 605
        const val theSecurityCodeInNotValid = 606
        const val creditCardNumberNotValid = 607
        const val theEmailEnteredIsNotCorrect = 608
        const val theNameEnteredIsInvalid = 609
        const val theLastNameEnteredIsInvalid = 610
        const val thePhoneNumberEnteredIsInvalid = 611
        const val forSecurityReasonsYouCannotMakeMoreThanTransactionsWithTheSameCreditCard = 612
        const val monthlyLimitReachedRequesting = 613
        const val dailyLimitReachedRequesting = 614

        const val oopsSomethingWentWrongProblemWithTheService = 700
        const val oopsSomethingWentWrongWalletServiceEnabled = 701
        const val theSelectedActivityIsInvalid = 702
        const val theActivityWasCancelledPleaseTryAgain = 703
        const val thisPaymentHasAlreadyBeenMadePleaseVerify = 704
        const val theActivityHasBeenRejectedTryAgainOrContactOurSupportTeam = 705
        const val loggingInErrorTryAgain = 706
        const val wrongCodeTryAgain = 707
        const val thereIsAProblemWithTheMerchantPleaseContactOurSupportTeam = 708
        const val merchantServerDisabledPleaseContactOurSupportTeam = 709
        const val insufficientFundYouMustHaveAvailableAmountNecessaryToMakeYourPayments = 710
        const val makeSureYouHaveValidatedYourCardOrThatYouHaveTheNecessaryAmountToMakeThisAction =
            711
        const val verifyYourPhoneNumberToContinue = 712
        const val firstWeNeedToVerifyYourEmailGoToYourEmailAndTryAgain = 713
        const val firstWeNeedToVerifyYourEmailGoToYourEmailAndTryAgain_1 = 715
        const val firstWeNeedToVerifyYourEmailGoToYourEmailAndTryAgain_2 = 716 //, 715, 716
        const val addYourIdentityDocumentToContinueYouCanDoItFromTheMainMenu = 714
        const val addYourIdentityDocumentToContinueYouCanDoItFromTheMainMenu_1 = 717 //, 717
        const val pleaseTryAgain = 718
        const val pleaseTryAgain_1 = 719
        const val pleaseTryAgain_2 = 724 //, 719, 724
        const val theAmountExceedsTheMaximumAmountAllowed = 720
        const val pleaseEnterAValidAmount = 721
        const val thePaymentHasBeenDeclined = 722
        const val pleaseVerifyTheInformation = 723
        const val pleaseVerifyTheRecipientAndTryAgain = 725 //, 726
        const val pleaseVerifyTheRecipientAndTryAgain_1 = 726

        const val invalidExternalService = 800
        const val externalServiceDisable = 801
        const val invalidAccount = 802
        const val invalidTransactionTryAgainOrContactOurSupportTeam = 803
        const val transactionErrorTryAgainOrContactOurSupportTeam = 804
        const val invalidPaidTransaction = 805
        const val errorPaidTryAgainOrContactOurSupportTeam = 806
        const val invalidCheckCodePleaseTryAgain = 807

        const val invalidConfirmationCode = 101
        const val invalidNewPassword = 102
        const val nonWalletPlatformUser = 100
        const val invalidEmailPassword = 100
        const val CardIsAlreadyAssociatedWithAUser = 620
        const val invalidOtp = 300
        const val emailAlreadyRegistered = 550
    }
}
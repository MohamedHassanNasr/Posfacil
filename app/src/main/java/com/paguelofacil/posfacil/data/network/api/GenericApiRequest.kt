package com.paguelofacil.posfacil.data.network.api

import android.Manifest
import android.content.pm.PackageManager
import android.os.Environment
import androidx.core.app.ActivityCompat
import com.google.gson.Gson
import com.paguelofacil.posfacil.ApplicationClass
import com.paguelofacil.posfacil.R
import com.paguelofacil.posfacil.util.CalenderUtil
import com.paguelofacil.posfacil.util.Constantes.AppConstants.Companion.getBasicError
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
                baseResponse.apiError = getBasicError<Any>(apiCode, null, true)
                return baseResponse
            }
        } else {
            baseResponse.requestCode = apiCode
            baseResponse.isInternetOn = false
            baseResponse.apiError = getBasicError<Any>(apiCode, null, false)
            return baseResponse
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
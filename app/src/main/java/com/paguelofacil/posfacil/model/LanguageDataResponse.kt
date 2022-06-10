package com.paguelofacil.posfacil.model

import com.google.gson.annotations.SerializedName
import com.paguelofacil.posfacil.ApplicationClass
import com.paguelofacil.posfacil.R
import com.paguelofacil.posfacil.util.nullToCero
import com.paguelofacil.posfacil.util.nullToEmpty

data class LanguageDataResponse(
    @field:SerializedName("lang")
    val lang: String?,
    @field:SerializedName("group")
    val group: String?,
    @field:SerializedName("version")
    val version: Double?,
    @field:SerializedName("file")
    val file: LanguageFileResponse?,
    @field:SerializedName("release")
    val lanzamiento: String?,
    @field:SerializedName("dtRegister")
    val dtRegister: String?
)

data class LanguageFileResponse(
    @field:SerializedName("card_filter_mastercard")
    val cardFilterMastercard: String?,
    @field:SerializedName("something_went_wrong")
    val somethingWentWrong: String?,
    @field:SerializedName("step_one_recovery_pass")
    val stepOneRecoveryPass: String?,
    @field:SerializedName("id_transaction")
    val idTransaction: String?,
    @field:SerializedName("propina")
    val propina: String?,
    @field:SerializedName("error_transactions")
    val errorTransactions: String?,
    @field:SerializedName("advertencia_corte_z")
    val advertenciaCorteZ: String?,
    @field:SerializedName("monto_cobrar")
    val montoCobrar: String?,
    @field:SerializedName("password")
    val password: String?,
    @field:SerializedName("monto_visa")
    val montoVisa: String?,
    @field:SerializedName("code_operation")
    val codeOperation: String?,
    @field:SerializedName("welcome")
    val welcome: String?,
    @field:SerializedName("pleaseVerifyTheRecipientAndTryAgain")
    val pleaseVerifyTheRecipientAndTryAgain: String?,
    @field:SerializedName("impuesto_receipt_detail")
    val impuestoReceiptDetail: String?,
    @field:SerializedName("verificando_card")
    val verificandoCard: String?,
    @field:SerializedName("invalidNewPassword")
    val invalidNewPassword: String?,
    @field:SerializedName("recibir_comprobante")
    val recibirComprobante: String?,
    @field:SerializedName("verificar")
    val verificar: String?,
    @field:SerializedName("pleaseEnterAValidAmount")
    val pleaseEnterAValidAmount: String?,
    @field:SerializedName("dailyLimitReachedRequesting")
    val dailyLimitReachedRequesting: String?,
    @field:SerializedName("firstWeNeedToVerifyYourEmailGoToYourEmailAndTryAgain")
    val firstWeNeedToVerifyYourEmailGoToYourEmailAndTryAgain: String?,
    @field:SerializedName("menu_powered_by")
    val menuPoweredBy: String?,
    @field:SerializedName("externalServiceDisable")
    val externalServiceDisable: String?,
    @field:SerializedName("forSecurityReasonsYouCannotMakeMoreThanTransactionsWithTheSameCreditCard")
    val forSecurityReasonsYouCannotMakeMoreThanTransactionsWithTheSameCreditCard: String?,
    @field:SerializedName("title_support")
    val titleSupport: String?,
    @field:SerializedName("insufficientFundYouMustHaveAvailableAmountNecessaryToMakeYourPayments")
    val insufficientFundYouMustHaveAvailableAmountNecessaryToMakeYourPayments: String?,
    @field:SerializedName("invalidExternalService")
    val invalidExternalService: String?,
    @field:SerializedName("text_splash_three")
    val textSplashThree: String?,
    @field:SerializedName("creditCardNumberNotValid")
    val creditCardNumberNotValid: String?,
    @field:SerializedName("addYourIdentityDocumentToContinueYouCanDoItFromTheMainMenu")
    val addYourIdentityDocumentToContinueYouCanDoItFromTheMainMenu: String?,
    @field:SerializedName("theEmailEnteredIsNotCorrect")
    val theEmailEnteredIsNotCorrect: String?,
    @field:SerializedName("about_app")
    val about_app: String?,
    @field:SerializedName("success_transaction")
    val successTransaction: String?,
    @field:SerializedName("global")
    val global: String?,
    @field:SerializedName("theActivityHasBeenRejectedTryAgainOrContactOurSupportTeam")
    val theActivityHasBeenRejectedTryAgainOrContactOurSupportTeam: String?,
    @field:SerializedName("hoy")
    val hoy: String?,
    @field:SerializedName("tip_menos1")
    val tipMenos1: String?,
    @field:SerializedName("card_filter_paguelofacil")
    val cardFilterPaguelofacil: String?,
    @field:SerializedName("currency")
    val currency: String?,
    @field:SerializedName("total_recibir")
    val totalRecibir: String?,
    @field:SerializedName("volver")
    val volver: String?,
    @field:SerializedName("tarjeta")
    val tarjeta: String?,
    @field:SerializedName("email")
    val email: String?,
    @field:SerializedName("monto_mastercard")
    val montoMastercard: String?,
    @field:SerializedName("step_one_title_recovery_pass")
    val stepOneTitleRecoveryPass: String?,
    @field:SerializedName("correo_electronico")
    val correoElectronico: String?,
    @field:SerializedName("other_transaction")
    val otherTransaction: String?,
    @field:SerializedName("cant_reembolsos")
    val cantReembolsos: String?,
    @field:SerializedName("reportes")
    val reportes: String?,
    @field:SerializedName("default_import")
    val defaultImport: String?,
    @field:SerializedName("nav_header_title")
    val navHeaderTitle: String?,
    @field:SerializedName("app_name")
    val appName: String?,
    @field:SerializedName("impuestos")
    val impuestos: String?,
    @field:SerializedName("about_support")
    val aboutSupport: String?,
    @field:SerializedName("invalidTransactionTryAgainOrContactOurSupportTeam")
    val invalidTransactionTryAgainOrContactOurSupportTeam: String?,
    @field:SerializedName("importe_reembolsado")
    val importeReembolsado: String?,
    @field:SerializedName("reembolso_toolbar")
    val reembolsoToolbar: String?,
    @field:SerializedName("detalle_Total_reporte")
    val detalleTotalReporte: String?,
    @field:SerializedName("my_information")
    val myInformation: String?,
    @field:SerializedName("thisPaymentHasAlreadyBeenMadePleaseVerify")
    val thisPaymentHasAlreadyBeenMadePleaseVerify: String?,
    @field:SerializedName("monthlyLimitReachedSending")
    val monthlyLimitReachedSending: String?,
    @field:SerializedName("importe_reembolsar")
    val importeReembolsar: String?,
    @field:SerializedName("change_password")
    val changePassword: String?,
    @field:SerializedName("theLastNameEnteredIsInvalid")
    val theLastNameEnteredIsInvalid: String?,
    @field:SerializedName("log_in")
    val logIn: String?,
    @field:SerializedName("detalle_operador_reporte")
    val detalleOperadorReporte: String?,
    @field:SerializedName("card_filter_visa")
    val cardFilterVisa: String?,
    @field:SerializedName("web_support")
    val webSupport: String?,
    @field:SerializedName("metodo_pago")
    val metodoPago: String?,
    @field:SerializedName("menu_settings")
    val menuSettings: String?,
    @field:SerializedName("about_version_app")
    val aboutVersionApp: String?,
    @field:SerializedName("menu_transactions")
    val menuTransactions: String?,
    @field:SerializedName("finalizar")
    val finalizar: String?,
    @field:SerializedName("wrongCodeTryAgain")
    val wrongCodeTryAgain: String?,
    @field:SerializedName("resumeCobro")
    val resume_cobro: String?,
    @field:SerializedName("monto")
    val monto: String?,
    @field:SerializedName("motivo_reembolso")
    val motivoReembolso: String?,
    @field:SerializedName("ventas")
    val ventas: String?,
    @field:SerializedName("sessionTimeout")
    val sessionTimeout: String?,
    @field:SerializedName("reembolsar")
    val reembolsar: String?,
    @field:SerializedName("emailAlreadyRegistered")
    val emailAlreadyRegistered: String?,
    @field:SerializedName("step_two_recovery_pass")
    val stepTwoRecoveryPass: String?,
    @field:SerializedName("si_aceptar")
    val siAceptar: String?,
    @field:SerializedName("transaction_status")
    val transactionStatus: String?,
    @field:SerializedName("motivo")
    val motivo: String?,
    @field:SerializedName("text_splash_two")
    val textSplashTwo: String?,
    @field:SerializedName("cant_transacciones")
    val cantTransacciones: String?,
    @field:SerializedName("phone_support")
    val phoneSupport: String?,
    @field:SerializedName("card_reconocida")
    val cardReconocida: String?,
    @field:SerializedName("resumen_ventas_hoy")
    val resumenVentasHoy: String?,
    @field:SerializedName("title_activity_home")
    val titleActivityHome: String?,
    @field:SerializedName("tx_description")
    val txDescription: String?,
    @field:SerializedName("monto_transacciones")
    val montoTransacciones: String?,
    @field:SerializedName("validacion_pago")
    val validacionPago: String?,
    @field:SerializedName("invalidCheckCodePleaseTryAgain")
    val invalidCheckCodePleaseTryAgain: String?,
    @field:SerializedName("oppsSomethingWentWrongProblemWithTheService")
    val oppsSomethingWentWrongProblemWithTheService: String?,
    @field:SerializedName("makeSureYouHaveValidatedYourCardOrThatYouHaveTheNecessaryamountToMakeThisAction")
    val makeSureYouHaveValidatedYourCardOrThatYouHaveTheNecessaryamountToMakeThisAction: String?,
    @field:SerializedName("pleaseTryAgain")
    val pleaseTryAgain: String?,
    @field:SerializedName("step_two_title_recovery_pass")
    val stepTwoTitleRecoveryPass: String?,
    @field:SerializedName("text_splash_one")
    val textSplashOne: String?,
    @field:SerializedName("theSecurityCodeInNotValid")
    val theSecurityCodeInNotValid: String?,
    @field:SerializedName("email_username")
    val emailUsername: String?,
    @field:SerializedName("invalidAccount")
    val invalidAccount: String?,
    @field:SerializedName("resend_receipt")
    val resendReceipt: String?,
    @field:SerializedName("detalle_visa_reporte")
    val detalleVisaReporte: String?,
    @field:SerializedName("emitiras_reembolso")
    val emitirasReembolso: String?,
    @field:SerializedName("pleaseVerifyTheInformation")
    val pleaseVerifyTheInformation: String?,
    @field:SerializedName("detalle_mastercard_reporte")
    val detalleMastercardReporte: String?,
    @field:SerializedName("invalidEmailPassword")
    val invalidEmailPassword: String?,
    @field:SerializedName("send_voucher")
    val sendVoucher: String?,
    @field:SerializedName("username")
    val username: String?,
    @field:SerializedName("tipo")
    val tipo: String?,
    @field:SerializedName("theAmountExceedsTheMaximumAmountAllowed")
    val theAmountExceedsTheMaximumAmountAllowed: String?,
    @field:SerializedName("tip_5")
    val tip5: String?,
    @field:SerializedName("metodos_pago")
    val metodosPago: String?,
    @field:SerializedName("reporte_x_generado")
    val reporteXGenerado: String?,
    @field:SerializedName("tv_resend_code")
    val tvResendCode: String?,
    @field:SerializedName("seguro_realizar_corte_z")
    val seguroRealizarCorteZ: String?,
    @field:SerializedName("detalle_ventas_reporte")
    val detalleVentasReporte: String?,
    @field:SerializedName("user_transaccione")
    val userTransaccione: String?,
    @field:SerializedName("loggingInErrorTryAgain")
    val loggingInErrorTryAgain: String?,
    @field:SerializedName("step_three_title_recovery_pass")
    val stepThree_title_recovery_pass: String?,
    @field:SerializedName("importe_cobrado")
    val importeCobrado: String?,
    @field:SerializedName("invalid_password_short")
    val invalidPasswordShort: String?,
    @field:SerializedName("tip_0")
    val tip0: String?,
    @field:SerializedName("id")
    val id: String?,
    @field:SerializedName("recuperar_password")
    val recuperarPassword: String?,
    @field:SerializedName("enviar_reporte")
    val enviarReporte: String?,
    @field:SerializedName("Tomorrow")
    val tomorrow: String?,
    @field:SerializedName("propinas")
    val propinas: String?,
    @field:SerializedName("phone")
    val phone: String?,
    @field:SerializedName("importe_base")
    val importeBase: String?,
    @field:SerializedName("indica_destino_comproban")
    val indicaDestinoComproban: String?,
    @field:SerializedName("theAmountToBeReceivedIsGreater")
    val theAmountToBeReceivedIsGreater: String?,
    @field:SerializedName("Please_enter_your_email_or_Alias")
    val pleaseEnterYourEmailOrAlias: String?,
    @field:SerializedName("esperando_tarjeta")
    val esperandoTarjeta: String?,
    @field:SerializedName("some_id")
    val someId: String?,
    @field:SerializedName("select_filter")
    val selectFilter: String?,
    @field:SerializedName("menu_home")
    val menuHome: String?,
    @field:SerializedName("theActivityWasCancelledPleaseTryAgain")
    val theActivityWasCancelledPleaseTryAgain: String?,
    @field:SerializedName("new_pass")
    val newPass: String?,
    @field:SerializedName("monthlyLimitReachedRequesting")
    val monthlyLimitReachedRequesting: String?,
    @field:SerializedName("invalidConfirmationAmount")
    val invalidConfirmationAmount: String?,
    @field:SerializedName("menu_idioma_espanol")
    val menuIdiomaEspanol: String?,
    @field:SerializedName("thereIsAProblemWithTheMerchantPleaseContactOurSupportTeam")
    val thereIsAProblemWithTheMerchantPleaseContactOurSupportTeam: String?,
    @field:SerializedName("reporte_z_generado")
    val reporteZGenerado: String?,
    @field:SerializedName("thePaymetHasBeenDeclined")
    val thePaymetHasBeenDeclined: String?,
    @field:SerializedName("volver_transactions")
    val volverTransactions: String?,
    @field:SerializedName("menu_support")
    val menuSupport: String?,
    @field:SerializedName("transacciones_usuario")
    val transaccionesUsuario: String?,
    @field:SerializedName("detalle_cobro")
    val detalleCobro: String?,
    @field:SerializedName("step_three_recovery_pass")
    val stepThreeRecoveryPass: String?,
    @field:SerializedName("legal_support")
    val legalSupport: String?,
    @field:SerializedName("menu_reports")
    val menuReports: String?,
    @field:SerializedName("method_pay_filter")
    val methodPayFilter: String?,
    @field:SerializedName("detalle_usuario")
    val detalleUsuario: String?,
    @field:SerializedName("impuesto_sugerido")
    val impuestoSugerido: String?,
    @field:SerializedName("last_name")
    val lastName: String?,
    @field:SerializedName("filter")
    val filter: String?,
    @field:SerializedName("disabledAccount")
    val disabledAccount: String?,
    @field:SerializedName("merchantServerDisabledPleaseContactOurSupportTeam")
    val merchantServerDisabledPleaseContactOurSupportTeam: String?,
    @field:SerializedName("metodo_reembolso")
    val metodoReembolso: String?,
    @field:SerializedName("It_seems_like_you_are_not_connected_with_a_stable_internet")
    val itSeemsLikeYouAreNotConnectedWithAStableInternet: String?,
    @field:SerializedName("voucher_enviado")
    val voucherEnviado: String?,
    @field:SerializedName("detalle_impuestos_reporte")
    val detalleImpuestosReporte: String?,
    @field:SerializedName("repeat_new_pass")
    val repeatNewPass: String?,
    @field:SerializedName("date")
    val date: String?,
    @field:SerializedName("itbms")
    val itbms: String?,
    @field:SerializedName("estado")
    val estado: String?,
    @field:SerializedName("transactions_menu")
    val transactionsMenu: String?,
    @field:SerializedName("user_filters")
    val userFilters: String?,
    @field:SerializedName("example_email_hint")
    val exampleEmailHint: String?,
    @field:SerializedName("thePhoneNumberEnteredIsInvalid")
    val thePhoneNumberEnteredIsInvalid: String?,
    @field:SerializedName("tip_20")
    val tip20: String?,
    @field:SerializedName("tv_next")
    val tvNext: String?,
    @field:SerializedName("verifyYourPhoneNumberToContinue")
    val verifyYourPhoneNumberToContinue: String?,
    @field:SerializedName("verificar_cobro")
    val verificarCobro: String?,
    @field:SerializedName("detalle_reembolso")
    val detalleReembolso: String?,
    @field:SerializedName("reporte_x")
    val reporteX: String?,
    @field:SerializedName("string_email")
    val stringEmail: String?,
    @field:SerializedName("cobro_succes")
    val cobroSucces: String?,
    @field:SerializedName("reembolso")
    val reembolso: String?,
    @field:SerializedName("refund_reason4")
    val refundReason4: String?,
    @field:SerializedName("refund_reason3")
    val refundReason3: String?,
    @field:SerializedName("confirmar_reembolso")
    val confirmarReembolso: String?,
    @field:SerializedName("dailyLimitReachedSending")
    val dailyLimitReachedSending: String?,
    @field:SerializedName("invalidConfirmationCode")
    val invalidConfirmationCode: String?,
    @field:SerializedName("tip_15")
    val tip15: String?,
    @field:SerializedName("Yesterday")
    val yesterday: String?,
    @field:SerializedName("transactionErrorTryAgainOrContactOurSupportTeam")
    val transactionErrorTryAgainOrContactOurSupportTeam: String?,
    @field:SerializedName("tip_10")
    val tip10: String?,
    @field:SerializedName("refund_reason2")
    val refundReason2: String?,
    @field:SerializedName("refund_reason1")
    val refundReason1: String?,
    @field:SerializedName("about_version_spoc")
    val aboutVersionSpoc: String?,
    @field:SerializedName("send")
    val send: String?,
    @field:SerializedName("title_firma")
    val titleFirma: String?,
    @field:SerializedName("menu_idioma_ingles")
    val menuIdiomaIngles: String?,
    @field:SerializedName("oppsSomethingWentWrongWalletServiceEnabled")
    val oppsSomethingWentWrongWalletServiceEnabled: String?,
    @field:SerializedName("impuesto_default")
    val impuestoDefault: String?,
    @field:SerializedName("escriba_aqui")
    val escribaAqui: String?,
    @field:SerializedName("scanea_qr")
    val scaneaQR: String?,
    @field:SerializedName("aceptar")
    val aceptar: String?,
    @field:SerializedName("example_phone_hint")
    val examplePhoneHint: String?,
    @field:SerializedName("detalle_propinas_reporte")
    val detallePropinasReporte: String?,
    @field:SerializedName("seleccione_metodo_pago")
    val seleccioneMetodoPago: String?,
    @field:SerializedName("detalle_devoluciones_reporte")
    val detalleDevolucionesReporte: String?,
    @field:SerializedName("menu_cerrar_session")
    val menuCerrarSession: String?,
    @field:SerializedName("invalidPaidTransaction")
    val invalidPaidTransaction: String?,
    @field:SerializedName("email_support")
    val emailSupport: String?,
    @field:SerializedName("total")
    val total: String?,
    @SerializedName("did_you_forget_your_password")
    val didYouForgetYourPassword: String?,
    @field:SerializedName("Unable_to_process_your_request_Please_try_again")
    val unableToProcessYourRequestPleaseTryAgain: String?,
    @field:SerializedName("theInformationDoesnotMatchTheDocumentProvide")
    val theInformationDoesnotMatchTheDocumentProvide: String?,
    @field:SerializedName("cobrar")
    val cobrar: String?,
    @field:SerializedName("theNameEnteredIsInvalid")
    val theNameEnteredIsInvalid: String?,
    @field:SerializedName("theCardIsNotValid")
    val theCardIsNotValid: String?,
    @field:SerializedName("monto_paguelofacil")
    val montoPaguelofacil: String?,
    @field:SerializedName("number_phone")
    val numberPhone: String?,
    @field:SerializedName("monto_reembolsos")
    val montoReembolsos: String?,
    @field:SerializedName("paguelofacil_app")
    val paguelofacilApp: String?,
    @field:SerializedName("theMinimumAllowedAmount")
    val theMinimumAllowedAmount: String?,
    @field:SerializedName("theSelectedActivityIsEnvalid")
    val theSelectedActivityIsEnvalid: String?,
    @field:SerializedName("btn_update_pass")
    val btnUpdatePass: String?,
    @field:SerializedName("btn_recovery_pass")
    val btnRecoveryPass: String?,
    @field:SerializedName("informe")
    val informe: String?,
    @field:SerializedName("errorPaidTryAgainOrContactOurSupportTeam")
    val errorPaidTryAgainOrContactOurSupportTeam: String?,
    @field:SerializedName("tx_ concept")
    val txConcept: String?,
    @field:SerializedName("tarjeta_reconocida_correctamente")
    val tarjetaReconocidaCorrectamente: String?,
    @field:SerializedName("searchHere")
    val search_here: String?,
    @field:SerializedName("Please_enter_your_password")
    val pleaseEnterYourPassword: String?,
    @field:SerializedName("Today")
    val today: String?,
    @field:SerializedName("importe_cobro")
    val importeCobro: String?,
    @field:SerializedName("realizar_reporte")
    val realizarReporte: String?,
    @field:SerializedName("subtitle_firma")
    val subtitleFirma: String?,
    @field:SerializedName("desea_enviar_correo_corte")
    val deseaEnviarCorreoCorte: String?,
    val pf_qr: String?,
    val select: String?,
    val voucher: String?,
    val billing_panel: String?,
    val no_charge_day: String?,
    val cancel_operation: String?,
    val cancel_question: String?,
    val cancel: String?,
    val error: String?,
    val pwd_not_update: String?,
    val try_againg: String?,
    val check_data: String?,
    val completed: String?,
    val sales_reports: String?,
    val report_z: String?,
    val grand_totals: String?,
    val filter_search: String?,
    val recovered_pwd: String?/*,
    val no_charge_done: String?,
    val close_session_confirmation: String?*/
) {
    fun toLanguageFile() = LanguageFile(
        cardFilterMastercard = this.cardFilterMastercard.nullToEmpty(),
        somethingWentWrong = this.somethingWentWrong.nullToEmpty(),
        stepOneRecoveryPass = this.stepOneRecoveryPass.nullToEmpty(),
        idTransaction = this.idTransaction.nullToEmpty(),
        propina = this.propina.nullToEmpty(),
        errorTransactions = this.errorTransactions.nullToEmpty(),
        advertenciaCorteZ = this.advertenciaCorteZ.nullToEmpty(),
        montoCobrar = this.montoCobrar.nullToEmpty(),
        password = this.password.nullToEmpty(),
        montoVisa = this.montoVisa.nullToEmpty(),
        codeOperation = this.codeOperation.nullToEmpty(),
        welcome = this.welcome.nullToEmpty(),
        pleaseVerifyTheRecipientAndTryAgain = this.pleaseVerifyTheRecipientAndTryAgain.nullToEmpty(),
        impuestoReceiptDetail = this.impuestoReceiptDetail.nullToEmpty(),
        verificandoCard = this.verificandoCard.nullToEmpty(),
        invalidNewPassword = this.invalidNewPassword.nullToEmpty(),
        recibirComprobante = this.recibirComprobante.nullToEmpty(),
        verificar = this.verificar.nullToEmpty(),
        pleaseEnterAValidAmount = this.pleaseEnterAValidAmount.nullToEmpty(),
        dailyLimitReachedRequesting = this.dailyLimitReachedRequesting.nullToEmpty(),
        firstWeNeedToVerifyYourEmailGoToYourEmailAndTryAgain = this.firstWeNeedToVerifyYourEmailGoToYourEmailAndTryAgain.nullToEmpty(),
        menuPoweredBy = this.menuPoweredBy.nullToEmpty(),
        externalServiceDisable = this.externalServiceDisable.nullToEmpty(),
        forSecurityReasonsYouCannotMakeMoreThanTransactionsWithTheSameCreditCard = this.forSecurityReasonsYouCannotMakeMoreThanTransactionsWithTheSameCreditCard.nullToEmpty(),
        titleSupport = this.titleSupport.nullToEmpty(),
        insufficientFundYouMustHaveAvailableAmountNecessaryToMakeYourPayments = this.insufficientFundYouMustHaveAvailableAmountNecessaryToMakeYourPayments.nullToEmpty(),
        invalidExternalService = this.invalidExternalService.nullToEmpty(),
        textSplashThree = this.textSplashThree.nullToEmpty(),
        creditCardNumberNotValid = this.creditCardNumberNotValid.nullToEmpty(),
        addYourIdentityDocumentToContinueYouCanDoItFromTheMainMenu = this.addYourIdentityDocumentToContinueYouCanDoItFromTheMainMenu.nullToEmpty(),
        theEmailEnteredIsNotCorrect = this.theEmailEnteredIsNotCorrect.nullToEmpty(),
        about_app = this.about_app.nullToEmpty(),
        successTransaction = this.successTransaction.nullToEmpty(),
        global = this.global.nullToEmpty(),
        theActivityHasBeenRejectedTryAgainOrContactOurSupportTeam = this.theActivityHasBeenRejectedTryAgainOrContactOurSupportTeam.nullToEmpty(),
        hoy = this.hoy.nullToEmpty(),
        tipMenos1 = this.tipMenos1.nullToEmpty(),
        cardFilterPaguelofacil = this.cardFilterPaguelofacil.nullToEmpty(),
        currency = this.currency.nullToEmpty(),
        totalRecibir = this.totalRecibir.nullToEmpty(),
        volver = this.volver.nullToEmpty(),
        tarjeta = this.tarjeta.nullToEmpty(),
        email = this.email.nullToEmpty(),
        montoMastercard = this.montoMastercard.nullToEmpty(),
        stepOneTitleRecoveryPass = this.stepOneTitleRecoveryPass.nullToEmpty(),
        correoElectronico = this.correoElectronico.nullToEmpty(),
        otherTransaction = this.otherTransaction.nullToEmpty(),
        cantReembolsos = this.cantReembolsos.nullToEmpty(),
        reportes = this.reportes.nullToEmpty(),
        defaultImport = this.defaultImport.nullToEmpty(),
        navHeaderTitle = this.navHeaderTitle.nullToEmpty(),
        appName = this.appName.nullToEmpty(),
        impuestos = this.impuestos.nullToEmpty(),
        aboutSupport = this.aboutSupport.nullToEmpty(),
        invalidTransactionTryAgainOrContactOurSupportTeam = this.invalidTransactionTryAgainOrContactOurSupportTeam.nullToEmpty(),
        importeReembolsado = this.importeReembolsado.nullToEmpty(),
        reembolsoToolbar = this.reembolsoToolbar.nullToEmpty(),
        detalleTotalReporte = this.detalleTotalReporte.nullToEmpty(),
        myInformation = this.myInformation.nullToEmpty(),
        thisPaymentHasAlreadyBeenMadePleaseVerify = this.thisPaymentHasAlreadyBeenMadePleaseVerify.nullToEmpty(),
        monthlyLimitReachedSending = this.monthlyLimitReachedSending.nullToEmpty(),
        importeReembolsar = this.importeReembolsar.nullToEmpty(),
        changePassword = this.changePassword.nullToEmpty(),
        theLastNameEnteredIsInvalid = this.theLastNameEnteredIsInvalid.nullToEmpty(),
        logIn = this.logIn.nullToEmpty(),
        detalleOperadorReporte = this.detalleOperadorReporte.nullToEmpty(),
        cardFilterVisa = this.cardFilterVisa.nullToEmpty(),
        webSupport = this.webSupport.nullToEmpty(),
        metodoPago = this.metodoPago.nullToEmpty(),
        menuSettings = this.menuSettings.nullToEmpty(),
        aboutVersionApp = this.aboutVersionApp.nullToEmpty(),
        menuTransactions = this.menuTransactions.nullToEmpty(),
        finalizar = this.finalizar.nullToEmpty(),
        wrongCodeTryAgain = this.wrongCodeTryAgain.nullToEmpty(),
        resume_cobro = this.resume_cobro.nullToEmpty(),
        monto = this.monto.nullToEmpty(),
        motivoReembolso = this.motivoReembolso.nullToEmpty(),
        ventas = this.ventas.nullToEmpty(),
        sessionTimeout = this.sessionTimeout.nullToEmpty(),
        reembolsar = this.reembolsar.nullToEmpty(),
        emailAlreadyRegistered = this.emailAlreadyRegistered.nullToEmpty(),
        stepTwoRecoveryPass = this.stepTwoRecoveryPass.nullToEmpty(),
        siAceptar = this.siAceptar.nullToEmpty(),
        transactionStatus = this.transactionStatus.nullToEmpty(),
        motivo = this.motivo.nullToEmpty(),
        textSplashTwo = this.textSplashTwo.nullToEmpty(),
        cantTransacciones = this.cantTransacciones.nullToEmpty(),
        phoneSupport = this.phoneSupport.nullToEmpty(),
        cardReconocida = this.cardReconocida.nullToEmpty(),
        resumenVentasHoy = this.resumenVentasHoy.nullToEmpty(),
        titleActivityHome = this.titleActivityHome.nullToEmpty(),
        txDescription = this.txDescription.nullToEmpty(),
        montoTransacciones = this.montoTransacciones.nullToEmpty(),
        validacionPago = this.validacionPago.nullToEmpty(),
        invalidCheckCodePleaseTryAgain = this.invalidCheckCodePleaseTryAgain.nullToEmpty(),
        oppsSomethingWentWrongProblemWithTheService = this.oppsSomethingWentWrongProblemWithTheService.nullToEmpty(),
        makeSureYouHaveValidatedYourCardOrThatYouHaveTheNecessaryamountToMakeThisAction = this.makeSureYouHaveValidatedYourCardOrThatYouHaveTheNecessaryamountToMakeThisAction.nullToEmpty(),
        pleaseTryAgain = this.pleaseTryAgain.nullToEmpty(),
        stepTwoTitleRecoveryPass = this.stepTwoTitleRecoveryPass.nullToEmpty(),
        textSplashOne = this.textSplashOne.nullToEmpty(),
        theSecurityCodeInNotValid = this.theSecurityCodeInNotValid.nullToEmpty(),
        emailUsername = this.emailUsername.nullToEmpty(),
        invalidAccount = this.invalidAccount.nullToEmpty(),
        resendReceipt = this.resendReceipt.nullToEmpty(),
        detalleVisaReporte = this.detalleVisaReporte.nullToEmpty(),
        emitirasReembolso = this.emitirasReembolso.nullToEmpty(),
        pleaseVerifyTheInformation = this.pleaseVerifyTheInformation.nullToEmpty(),
        detalleMastercardReporte = this.detalleMastercardReporte.nullToEmpty(),
        invalidEmailPassword = this.invalidEmailPassword.nullToEmpty(),
        sendVoucher = this.sendVoucher.nullToEmpty(),
        username = this.username.nullToEmpty(),
        tipo = this.tipo.nullToEmpty(),
        theAmountExceedsTheMaximumAmountAllowed = this.theAmountExceedsTheMaximumAmountAllowed.nullToEmpty(),
        tip5 = this.tip5.nullToEmpty(),
        metodosPago = this.metodosPago.nullToEmpty(),
        reporteXGenerado = this.reporteXGenerado.nullToEmpty(),
        tvResendCode = this.tvResendCode.nullToEmpty(),
        seguroRealizarCorteZ = this.seguroRealizarCorteZ.nullToEmpty(),
        detalleVentasReporte = this.detalleVentasReporte.nullToEmpty(),
        userTransaccione = this.userTransaccione.nullToEmpty(),
        loggingInErrorTryAgain = this.loggingInErrorTryAgain.nullToEmpty(),
        stepThree_title_recovery_pass = this.stepThree_title_recovery_pass.nullToEmpty(),
        importeCobrado = this.importeCobrado.nullToEmpty(),
        invalidPasswordShort = this.invalidPasswordShort.nullToEmpty(),
        tip0 = this.tip0.nullToEmpty(),
        id = this.id.nullToEmpty(),
        recuperarPassword = this.recuperarPassword.nullToEmpty(),
        enviarReporte = this.enviarReporte.nullToEmpty(),
        tomorrow = this.tomorrow.nullToEmpty(),
        propinas = this.propinas.nullToEmpty(),
        phone = this.phone.nullToEmpty(),
        importeBase = this.importeBase.nullToEmpty(),
        indicaDestinoComproban = this.indicaDestinoComproban.nullToEmpty(),
        theAmountToBeReceivedIsGreater = this.theAmountToBeReceivedIsGreater.nullToEmpty(),
        pleaseEnterYourEmailOrAlias = this.pleaseEnterYourEmailOrAlias.nullToEmpty(),
        esperandoTarjeta = this.esperandoTarjeta.nullToEmpty(),
        someId = this.someId.nullToEmpty(),
        selectFilter = this.selectFilter.nullToEmpty(),
        menuHome = this.menuHome.nullToEmpty(),
        theActivityWasCancelledPleaseTryAgain = this.theActivityWasCancelledPleaseTryAgain.nullToEmpty(),
        newPass = this.newPass.nullToEmpty(),
        monthlyLimitReachedRequesting = this.monthlyLimitReachedRequesting.nullToEmpty(),
        invalidConfirmationAmount = this.invalidConfirmationAmount.nullToEmpty(),
        menuIdiomaEspanol = this.menuIdiomaEspanol.nullToEmpty(),
        thereIsAProblemWithTheMerchantPleaseContactOurSupportTeam = this.thereIsAProblemWithTheMerchantPleaseContactOurSupportTeam.nullToEmpty(),
        reporteZGenerado = this.reporteZGenerado.nullToEmpty(),
        thePaymetHasBeenDeclined = this.thePaymetHasBeenDeclined.nullToEmpty(),
        volverTransactions = this.volverTransactions.nullToEmpty(),
        menuSupport = this.menuSupport.nullToEmpty(),
        transaccionesUsuario = this.transaccionesUsuario.nullToEmpty(),
        detalleCobro = this.detalleCobro.nullToEmpty(),
        stepThreeRecoveryPass = this.stepThreeRecoveryPass.nullToEmpty(),
        legalSupport = this.legalSupport.nullToEmpty(),
        menuReports = this.menuReports.nullToEmpty(),
        methodPayFilter = this.methodPayFilter.nullToEmpty(),
        detalleUsuario = this.detalleUsuario.nullToEmpty(),
        impuestoSugerido = this.impuestoSugerido.nullToEmpty(),
        lastName = this.lastName.nullToEmpty(),
        filter = this.filter.nullToEmpty(),
        disabledAccount = this.disabledAccount.nullToEmpty(),
        merchantServerDisabledPleaseContactOurSupportTeam = this.merchantServerDisabledPleaseContactOurSupportTeam.nullToEmpty(),
        metodoReembolso = this.metodoReembolso.nullToEmpty(),
        itSeemsLikeYouAreNotConnectedWithAStableInternet = this.itSeemsLikeYouAreNotConnectedWithAStableInternet.nullToEmpty(),
        voucherEnviado = this.voucherEnviado.nullToEmpty(),
        detalleImpuestosReporte = this.detalleImpuestosReporte.nullToEmpty(),
        repeatNewPass = this.repeatNewPass.nullToEmpty(),
        date = this.date.nullToEmpty(),
        itbms = this.itbms.nullToEmpty(),
        estado = this.estado.nullToEmpty(),
        transactionsMenu = this.transactionsMenu.nullToEmpty(),
        userFilters = this.userFilters.nullToEmpty(),
        exampleEmailHint = this.exampleEmailHint.nullToEmpty(),
        thePhoneNumberEnteredIsInvalid = this.thePhoneNumberEnteredIsInvalid.nullToEmpty(),
        tip20 = this.tip20.nullToEmpty(),
        tvNext = this.tvNext.nullToEmpty(),
        verifyYourPhoneNumberToContinue = this.verifyYourPhoneNumberToContinue.nullToEmpty(),
        verificarCobro = this.verificarCobro.nullToEmpty(),
        detalleReembolso = this.detalleReembolso.nullToEmpty(),
        reporteX = this.reporteX.nullToEmpty(),
        stringEmail = this.stringEmail.nullToEmpty(),
        cobroSucces = this.cobroSucces.nullToEmpty(),
        reembolso = this.reembolso.nullToEmpty(),
        refundReason4 = this.refundReason4.nullToEmpty(),
        refundReason3 = this.refundReason3.nullToEmpty(),
        confirmarReembolso = this.confirmarReembolso.nullToEmpty(),
        dailyLimitReachedSending = this.dailyLimitReachedSending.nullToEmpty(),
        invalidConfirmationCode = this.invalidConfirmationCode.nullToEmpty(),
        tip15 = this.tip15.nullToEmpty(),
        yesterday = this.yesterday.nullToEmpty(),
        transactionErrorTryAgainOrContactOurSupportTeam = this.transactionErrorTryAgainOrContactOurSupportTeam.nullToEmpty(),
        tip10 = this.tip10.nullToEmpty(),
        refundReason2 = this.refundReason2.nullToEmpty(),
        refundReason1 = this.refundReason1.nullToEmpty(),
        aboutVersionSpoc = this.aboutVersionSpoc.nullToEmpty(),
        send = this.send.nullToEmpty(),
        titleFirma = this.titleFirma.nullToEmpty(),
        menuIdiomaIngles = this.menuIdiomaIngles.nullToEmpty(),
        oppsSomethingWentWrongWalletServiceEnabled = this.oppsSomethingWentWrongWalletServiceEnabled.nullToEmpty(),
        impuestoDefault = this.impuestoDefault.nullToEmpty(),
        escribaAqui = this.escribaAqui.nullToEmpty(),
        scaneaQR = this.scaneaQR.nullToEmpty(),
        aceptar = this.aceptar.nullToEmpty(),
        examplePhoneHint = this.examplePhoneHint.nullToEmpty(),
        detallePropinasReporte = this.detallePropinasReporte.nullToEmpty(),
        seleccioneMetodoPago = this.seleccioneMetodoPago.nullToEmpty(),
        detalleDevolucionesReporte = this.detalleDevolucionesReporte.nullToEmpty(),
        menuCerrarSession = this.menuCerrarSession.nullToEmpty(),
        invalidPaidTransaction = this.invalidPaidTransaction.nullToEmpty(),
        emailSupport = this.emailSupport.nullToEmpty(),
        total = this.total.nullToEmpty(),
        didYouForgetYourPassword = this.didYouForgetYourPassword.nullToEmpty(),
        unableToProcessYourRequestPleaseTryAgain = this.unableToProcessYourRequestPleaseTryAgain.nullToEmpty(),
        theInformationDoesnotMatchTheDocumentProvide = this.theInformationDoesnotMatchTheDocumentProvide.nullToEmpty(),
        cobrar = this.cobrar.nullToEmpty(),
        theNameEnteredIsInvalid = this.theNameEnteredIsInvalid.nullToEmpty(),
        theCardIsNotValid = this.theCardIsNotValid.nullToEmpty(),
        montoPaguelofacil = this.montoPaguelofacil.nullToEmpty(),
        numberPhone = this.numberPhone.nullToEmpty(),
        montoReembolsos = this.montoReembolsos.nullToEmpty(),
        paguelofacilApp = this.paguelofacilApp.nullToEmpty(),
        theMinimumAllowedAmount = this.theMinimumAllowedAmount.nullToEmpty(),
        theSelectedActivityIsEnvalid = this.theSelectedActivityIsEnvalid.nullToEmpty(),
        btnUpdatePass = this.btnUpdatePass.nullToEmpty(),
        btnRecoveryPass = this.btnRecoveryPass.nullToEmpty(),
        informe = this.informe.nullToEmpty(),
        errorPaidTryAgainOrContactOurSupportTeam = this.errorPaidTryAgainOrContactOurSupportTeam.nullToEmpty(),
        txConcept = this.txConcept.nullToEmpty(),
        tarjetaReconocidaCorrectamente = this.tarjetaReconocidaCorrectamente.nullToEmpty(),
        search_here = this.search_here.nullToEmpty(),
        pleaseEnterYourPassword = this.pleaseEnterYourPassword.nullToEmpty(),
        today = this.today.nullToEmpty(),
        importeCobro = this.importeCobro.nullToEmpty(),
        realizarReporte = this.realizarReporte.nullToEmpty(),
        subtitleFirma = this.subtitleFirma.nullToEmpty(),
        deseaEnviarCorreoCorte = this.deseaEnviarCorreoCorte.nullToEmpty(),
        pf_qr = this.pf_qr.nullToEmpty(),
        select = this.select.nullToEmpty(),
        voucher = this.voucher.nullToEmpty(),
        billing_panel = this.billing_panel.nullToEmpty(),
        no_charge_day = this.no_charge_day.nullToEmpty(),
        cancel_operation = this.cancel_operation.nullToEmpty(),
        cancel_question = this.cancel_question.nullToEmpty(),
        cancel = this.cancel.nullToEmpty(),
        error = this.error.nullToEmpty(),
        pwd_not_update = this.pwd_not_update.nullToEmpty(),
        try_againg = this.try_againg.nullToEmpty(),
        check_data = this.check_data.nullToEmpty(),
        completed = this.completed.nullToEmpty(),
        sales_reports = this.sales_reports.nullToEmpty(),
        report_z = this.report_z.nullToEmpty(),
        grand_totals = this.grand_totals.nullToEmpty(),
        filter_search = this.filter_search.nullToEmpty(),
        recovered_pwd = this.recovered_pwd.nullToEmpty()/*,
        no_charge_done = ApplicationClass.instance.resources.getString(R.string.noChrgeDay),
        close_session_confirmation = ApplicationClass.instance.resources.getString(R.string.closeSession)*/
    )

}


val LanguageDataResponse.toLanguageData: LanguageData
    get() {
        return LanguageData(
            lang = this.lang.nullToEmpty(),
            group = this.group.nullToEmpty(),
            version = this.version.nullToCero(),
            lanzamiento = this.lanzamiento.nullToEmpty(),
            dtRegister = this.dtRegister.nullToEmpty(),
            file = this.file?.toLanguageFile()!!,
        )
    }

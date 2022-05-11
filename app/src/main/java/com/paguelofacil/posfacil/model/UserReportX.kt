package com.paguelofacil.posfacil.model

import com.paguelofacil.posfacil.ui.view.adapters.PaymentMethodsGlobal

data class UserReportX(
    val name: String,
    val globalReportX: GlobalReportX,
    val payment: List<PaymentMethodsGlobal>
)

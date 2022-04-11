package com.paguelofacil.posfacil.ui.view.transactions.payment.viewmodel

import com.paguelofacil.posfacil.base.BaseViewModel
import com.paguelofacil.posfacil.repository.TransactionRepo
import com.paguelofacil.posfacil.util.Constantes.CoroutinesBase
import com.paguelofacil.posfacil.util.Constantes.LoadingState

class TransactionsViewModel : BaseViewModel() {
    private val repo = TransactionRepo

    fun getAllTransactions() = CoroutinesBase.main {
        setLoadingState(LoadingState.LOADING)
        updateResponseObserver(repo.getAllTransactions()) {}
        setLoadingState(LoadingState.LOADED)
    }
}
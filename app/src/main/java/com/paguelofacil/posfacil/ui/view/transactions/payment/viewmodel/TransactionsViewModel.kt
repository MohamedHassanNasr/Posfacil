package com.paguelofacil.posfacil.ui.view.transactions.payment.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.paguelofacil.posfacil.base.BaseViewModel
import com.paguelofacil.posfacil.data.network.response.TransactionApiResponse
import com.paguelofacil.posfacil.repository.UserRepo
import com.paguelofacil.posfacil.repository.transaction.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class TransactionsViewModel @Inject constructor(private val transactionRepository: TransactionRepository) :
    BaseViewModel() {

    private val mutableTransactionList = MutableLiveData<MutableList<TransactionApiResponse>?>()
    val liveDataTransactionList: LiveData<MutableList<TransactionApiResponse>?> = mutableTransactionList

    private val _textSearch = MutableLiveData<String>()
    val textSearch : LiveData<String> = _textSearch

    fun getAllTransactions() {
        execute {
            viewModelScope.launch {
                val response = transactionRepository.getAllTransactions()
                processResponseResultado(response) {
                    if (UserRepo.getUser().merchantProfile?.idProfile != 3){
                        mutableTransactionList.postValue(it?.filter { item->
                            Timber.e("ID ${item.operatorId} IDS ${UserRepo.getUser().id}")
                            item.operatorId?.toLong() == UserRepo.getUser().id
                        } as MutableList<TransactionApiResponse>)
                    }else{
                        mutableTransactionList.postValue(it)
                    }
                }
            }
        }
    }

    fun setFilterText(text: String){
        Timber.e("TEXT VM $text")
        _textSearch.postValue(text)
    }
}
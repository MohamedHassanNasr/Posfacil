package com.paguelofacil.posfacil.model

class TransactionProvider {

    companion object{

        //DATA TEST

        val listaTransactions:ArrayList<Transaction> = arrayListOf(

            Transaction(1234565,"+ \$ 10.00","2022-03-23 15:21:20","Mastercard 6978",1),
            Transaction(1233445,"+ \$ 15.50","2022-03-23 15:21:20","Mastercard 1245",1),
            Transaction(1234565,"+ \$ 6.10","2022-03-23 15:21:20","Mastercard 3100",1),
            Transaction(1233445,"+ \$ 6.10","2022-03-23 15:21:20","Mastercard 2533",1),
            Transaction(1233434,"- \$ 18.50","2022-03-23 15:21:20","Mastercard 2314",0),


        )


    }
}
package com.paguelofacil.posfacil.tools

import timber.log.Timber

/**
 * @param selector Es el selector de tipo de tarjeta para saber que track se traer y procesara, 1-> Banda, 2-> chip, 3-> contactless
 * @param trackNumber el numero de track detectado
 * */
fun getInfoByTrackNumber(selector: Int, trackNumber: String): ModelTrack?{
    return when(selector){
        1->{
            bandInfo(trackNumber)
        }
        2->{
            chipInfo(trackNumber)
        }
        3->{
            contactlessInfo(trackNumber)
        }
        else->{
            null
        }
    }
}

private fun bandInfo(trackNumber: String): ModelTrack?{
    val regex = Regex("^B([0-9]{1,19})\\^([^\\^]{2,26})\\^([0-9]{4}|\\^)([0-9]{3}|\\^)([^\\?]+)\$")
    val matchResult = regex.find(trackNumber)
    return matchResult?.let {
        val (cardNumber, cardHolder, dateExpiry, cvv, data) = it.destructured

        Timber.e("CARDTRACKKKK banda->>> CardNumber: $cardNumber, CardHolder: $cardHolder, Date: $dateExpiry, CVV: $cvv, DATA Extra: $data")

        return@let ModelTrack(cardNumber, cardHolder, dateExpiry, cvv, data)
    }
}

private fun chipInfo(trackNumber: String): ModelTrack?{
    val regex = Regex("^([0-9]{1,16})d([^\\^]{4})([0-9]{3}|\\^)([^\\?]+)\$")
    val matchResult = regex.find(trackNumber)
    return matchResult?.let {
        val (cardNumber, dateExpiry, cvv, data) = it.destructured

        Timber.e("CARDTRACKKKK chip->>> CardNumber: $cardNumber, Date: $dateExpiry, CVV: $cvv, DATA Extra: $data")

        return@let ModelTrack(cardNumber, null, dateExpiry, cvv, data)
    }
}

private fun contactlessInfo(trackNumber: String): ModelTrack?{
    val regex = Regex("^([0-9]{1,16})d([^\\^]{4})([0-9]{3}|\\^)([^\\?]+)\$")
    val matchResult = regex.find(trackNumber)
    return matchResult?.let {
        val (cardNumber, dateExpiry, cvv, data) = it.destructured

        Timber.e("CARDTRACKKKK chip->>> CardNumber: $cardNumber, Date: $dateExpiry, CVV: $cvv, DATA Extra: $data")

        return@let ModelTrack(cardNumber, null, dateExpiry, cvv, data)
    }
}
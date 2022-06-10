package com.paguelofacil.posfacil.util

import android.annotation.SuppressLint
import timber.log.Timber
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

fun timeToUnix(): Long {
    return System.currentTimeMillis() / 1000
}

@SuppressLint("NewApi")
fun stringDateToUnix(stringDate: String): Long{
    val l = LocalDate.parse(stringDate, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH-mm-ss"))

    return l.atStartOfDay(ZoneId.systemDefault()).toInstant().epochSecond
}

@SuppressLint("SimpleDateFormat")
fun dateFormattedByDate(stringDate: String): String{
    Timber.e("dateInput $stringDate")
    val outputFormat: DateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val inputFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)

    val date: Date = inputFormat.parse(stringDate)
    Timber.e("timeDate ${outputFormat.format(date)}")
    return outputFormat.format(date)
}

@SuppressLint("SimpleDateFormat")
fun dateFormattedByHour(stringDate: String, getHourCode: Boolean): String{
    Timber.e("dateInput $stringDate")
    val outputFormat: DateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    val inputFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)

    val date: Date = inputFormat.parse(stringDate)
    Timber.e("timeDate ${outputFormat.format(date)}")

    if (!getHourCode){
        return outputFormat.format(date)
    }else{
        return setCodeHour(outputFormat.format(date))
    }
}

private fun setCodeHour(stringDate: String): String {
    val dayString = mutableListOf<Char>()
    val size = stringDate.length
    var day = ""
    var date = ""
    var code = ""

    dayString.add(stringDate[(size-1)-4])
    dayString.add(stringDate[(size-1)-3])

    Timber.e("day $dayString")

    dayString.forEach {
        day += it
    }

    if (day.toInt() > 11){
        date = "$stringDate pm"
    }else{
        date = "$stringDate am"
    }

    when(day.toInt()){
        13->{
            day = "01"
        }
        14->{
            day = "02"
        }
        15->{
            day = "03"
        }
        16->{
            day = "04"
        }
        17->{
            day = "05"
        }
        18->{
            day = "06"
        }
        19->{
            day = "07"
        }
        20->{
            day = "08"
        }
        21->{
            day = "09"
        }
        22->{
            day = "10"
        }
        23->{
            day = "11"
        }
        24 or 0->{
            day = "12"
        }
        else->{

        }
    }

    return date
}
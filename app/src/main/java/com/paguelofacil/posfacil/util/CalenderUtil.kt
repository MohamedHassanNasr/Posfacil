package com.paguelofacil.posfacil.util

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.paguelofacil.posfacil.R
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import com.paguelofacil.posfacil.ApplicationClass


/**
 * Calender util class for performing various tasks related to Calender. It also has some DataBinding adapters for various common function related to calender
 *
 * @constructor Create empty Calender util
 */
object CalenderUtil {
    private const val UTC_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss"

    @JvmStatic
    fun isSameYear(calendarOrTimeStampInMillis: Any): Boolean {
        val calendar = Calendar.getInstance()
        val currentCalender = Calendar.getInstance()
        if (calendarOrTimeStampInMillis is Calendar) {
            calendar.timeInMillis = calendarOrTimeStampInMillis.timeInMillis
        } else {
            calendar.timeInMillis = (calendarOrTimeStampInMillis as Long)
        }
        return calendar[Calendar.YEAR] == currentCalender[Calendar.YEAR]
    }

    @JvmStatic
    fun isPreviousMonth(calendarOrTimeStampInMillis: Any): Boolean {
        val calendar = Calendar.getInstance()
        val currentCalender = Calendar.getInstance()
        if (calendarOrTimeStampInMillis is Calendar) {
            calendar.timeInMillis = calendarOrTimeStampInMillis.timeInMillis
        } else {
            calendar.timeInMillis = (calendarOrTimeStampInMillis as Long?)!!
        }
        calendar.add(Calendar.MONTH, 1)
        return (calendar[Calendar.YEAR] == currentCalender[Calendar.YEAR]
                && calendar[Calendar.MONTH] == currentCalender[Calendar.MONTH])
    }

    @JvmStatic
    fun isSameMonth(calendarOrTimeStampInMillis: Any): Boolean {
        val calendar = Calendar.getInstance()
        val currentCalender = Calendar.getInstance()
        if (calendarOrTimeStampInMillis is Calendar) {
            calendar.timeInMillis = calendarOrTimeStampInMillis.timeInMillis
        } else {
            calendar.timeInMillis = (calendarOrTimeStampInMillis as Long?)!!
        }
        return (calendar[Calendar.YEAR] == currentCalender[Calendar.YEAR]
                && calendar[Calendar.MONTH] == currentCalender[Calendar.MONTH])
    }

    @JvmStatic
    fun isSameWeek(calendarOrTimeStampInMillis: Any): Boolean {
        val calendar = Calendar.getInstance()
        val currentCalender = Calendar.getInstance()
        if (calendarOrTimeStampInMillis is Calendar) {
            calendar.timeInMillis = calendarOrTimeStampInMillis.timeInMillis
        } else {
            calendar.timeInMillis = (calendarOrTimeStampInMillis as Long?)!!
        }
        return (calendar[Calendar.YEAR] == currentCalender[Calendar.YEAR]
                && calendar[Calendar.WEEK_OF_YEAR] == currentCalender[Calendar.WEEK_OF_YEAR])
    }

    @JvmStatic
    fun isLastWeek(calendarOrTimeStampInMillis: Any): Boolean {
        val calendar = Calendar.getInstance()
        val currentCalender = Calendar.getInstance()
        if (calendarOrTimeStampInMillis is Calendar) {
            calendar.timeInMillis = calendarOrTimeStampInMillis.timeInMillis
        } else {
            calendar.timeInMillis = (calendarOrTimeStampInMillis as Long?)!!
        }
        return (calendar[Calendar.YEAR] == currentCalender[Calendar.YEAR]
                && calendar[Calendar.WEEK_OF_YEAR] == (currentCalender[Calendar.WEEK_OF_YEAR]) - 1)
    }

    @JvmStatic
    fun isNextMonth(calendarOrTimeStampInMillis: Any): Boolean {
        val calendar = Calendar.getInstance()
        val currentCalender = Calendar.getInstance()
        if (calendarOrTimeStampInMillis is Calendar) {
            calendar.timeInMillis = calendarOrTimeStampInMillis.timeInMillis
        } else {
            calendar.timeInMillis = (calendarOrTimeStampInMillis as Long?)!!
        }
        calendar.add(Calendar.MONTH, -1)
        return (calendar[Calendar.YEAR] == currentCalender[Calendar.YEAR]
                && calendar[Calendar.MONTH] == currentCalender[Calendar.MONTH])
    }

    @JvmStatic
    fun isYesterday(calendarOrTimeStampInMillis: Any): Boolean {
        val calendar = Calendar.getInstance()
        if (calendarOrTimeStampInMillis is Calendar) {
            calendar.timeInMillis = calendarOrTimeStampInMillis.timeInMillis
        } else {
            calendar.timeInMillis = (calendarOrTimeStampInMillis as Long)
        }
        calendar.add(Calendar.DAY_OF_YEAR, 1)
        return isSameDay(calendar)
    }

    fun isSameDay(calendarOrTimeStampInMillis: Any): Boolean {
        val calendar = Calendar.getInstance()
        val currentCalender = Calendar.getInstance()
        if (calendarOrTimeStampInMillis is Calendar) {
            calendar.timeInMillis = calendarOrTimeStampInMillis.timeInMillis
        } else {
            calendar.timeInMillis = (calendarOrTimeStampInMillis as Long?)!!
        }
        return (calendar[Calendar.YEAR] == currentCalender[Calendar.YEAR]
                && calendar[Calendar.DAY_OF_YEAR] == currentCalender[Calendar.DAY_OF_YEAR])
    }

    fun isTomorrow(calendarOrTimeStampInMillis: Any): Boolean {
        val calendar = Calendar.getInstance()
        if (calendarOrTimeStampInMillis is Calendar) {
            calendar.timeInMillis = calendarOrTimeStampInMillis.timeInMillis
        } else {
            calendar.timeInMillis = (calendarOrTimeStampInMillis as Long)
        }
        calendar.add(Calendar.DAY_OF_YEAR, -1)
        return isSameDay(calendar)
    }

    fun getTime(calendarOrTimeStampInMillis: Any): String {
        val calendar = Calendar.getInstance()
        if (calendarOrTimeStampInMillis is Calendar) {
            calendar.timeInMillis = calendarOrTimeStampInMillis.timeInMillis
        } else {
            calendar.timeInMillis = (calendarOrTimeStampInMillis as Long?)!!
        }
        return (change0To12(calendar[Calendar.HOUR]).toString() + ":"
                + addZeroPrefixIfOneDigitValue(calendar[Calendar.MINUTE]) + ApplicationClass.instance.getString(R.string.blank_space)
                + calendar.getDisplayName(Calendar.AM_PM, Calendar.SHORT, Locale.getDefault()))
    }

    fun getFullTimeWithSecAndMillis(calendar: Calendar): String {
        return (change0To12(calendar[Calendar.HOUR]).toString() + ":"
                + addZeroPrefixIfOneDigitValue(calendar[Calendar.MINUTE]) + ":"
                + calendar[Calendar.SECOND] + ":"
                + calendar[Calendar.MILLISECOND]
                + calendar.getDisplayName(Calendar.AM_PM, Calendar.LONG, Locale.getDefault()))
    }

    fun getDateDdMmYy(calendarOrTimeStampInMillis: Any): String {
        val calendar = Calendar.getInstance()
        if (calendarOrTimeStampInMillis is Calendar) {
            calendar.timeInMillis = calendarOrTimeStampInMillis.timeInMillis
        } else {
            calendar.timeInMillis = (calendarOrTimeStampInMillis as Long?)!!
        }
        return (calendar[Calendar.DAY_OF_MONTH].toString() + ApplicationClass.instance.getString(R.string.blank_space)
                + calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault())
                + ApplicationClass.instance.getString(R.string.blank_space) + calendar[Calendar.YEAR])
    }

    fun getDateDdMm(calendarOrTimeStampInMillis: Any): String {
        val calendar = Calendar.getInstance()
        if (calendarOrTimeStampInMillis is Calendar) {
            calendar.timeInMillis = calendarOrTimeStampInMillis.timeInMillis
        } else {
            calendar.timeInMillis = (calendarOrTimeStampInMillis as Long?)!!
        }
        return (calendar[Calendar.DAY_OF_MONTH].toString() + ApplicationClass.instance.getString(R.string.blank_space)
                + calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault()))
    }

    fun getDateMmYy(calendarOrTimeStampInMillis: Any?): String {
        val calendar = Calendar.getInstance()
        if (calendarOrTimeStampInMillis is Calendar) {
            calendar.timeInMillis = calendarOrTimeStampInMillis.timeInMillis
        } else {
            calendar.timeInMillis = (calendarOrTimeStampInMillis as Long?)!!
        }
        return (calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault())
                + ApplicationClass.instance.getString(R.string.blank_space) + calendar[Calendar.YEAR])
    }

    fun getFormattedDate(calendarOrTimeStampInMillis: Any): String {
        return if (isYesterday(calendarOrTimeStampInMillis)) {
            ApplicationClass.instance.getString(R.string.Yesterday)
        } else if (isSameDay(calendarOrTimeStampInMillis)) {
            ApplicationClass.instance.getString(R.string.Today)
        } else if (isTomorrow(calendarOrTimeStampInMillis)) {
            ApplicationClass.instance.getString(R.string.Tomorrow)
        } else {
            val calendar = Calendar.getInstance()
            if (calendarOrTimeStampInMillis is Calendar) {
                calendar.timeInMillis = calendarOrTimeStampInMillis.timeInMillis
            } else {
                calendar.timeInMillis = (calendarOrTimeStampInMillis as Long)
            }
            if (isSameYear(calendarOrTimeStampInMillis)) {
                (calendar[Calendar.DAY_OF_MONTH].toString() + ApplicationClass.instance.getString(R.string.blank_space)
                        + calendar.getDisplayName(
                    Calendar.MONTH,
                    Calendar.SHORT,
                    Locale.getDefault()
                ))
            } else {
                getDateDdMmYy(calendar)
            }
        }
    }

    fun getFormattedDateTime(calendarOrTimeStampInMillis: Any): String {
        return if (isYesterday(calendarOrTimeStampInMillis)) {
            ApplicationClass.instance.getString(R.string.Yesterday) + ApplicationClass.instance.getString(R.string.blank_space) + getTime(calendarOrTimeStampInMillis)
        } else if (isSameDay(calendarOrTimeStampInMillis)) {
            ApplicationClass.instance.getString(R.string.Today) + ApplicationClass.instance.getString(R.string.blank_space) + getTime(calendarOrTimeStampInMillis)
        } else if (isTomorrow(calendarOrTimeStampInMillis)) {
            ApplicationClass.instance.getString(R.string.Tomorrow) + ApplicationClass.instance.getString(R.string.blank_space) + getTime(calendarOrTimeStampInMillis)
        } else {
            val calendar = Calendar.getInstance()
            if (calendarOrTimeStampInMillis is Calendar) {
                calendar.timeInMillis = calendarOrTimeStampInMillis.timeInMillis
            } else {
                calendar.timeInMillis = (calendarOrTimeStampInMillis as Long)
            }
            if (isSameYear(calendarOrTimeStampInMillis)) {
                (calendar[Calendar.DAY_OF_MONTH].toString() + ApplicationClass.instance.getString(R.string.blank_space)
                        + calendar.getDisplayName(
                    Calendar.MONTH,
                    Calendar.SHORT,
                    Locale.getDefault()
                ) + ApplicationClass.instance.getString(R.string.blank_space) + getTime(calendarOrTimeStampInMillis))
            } else {
                (calendar[Calendar.DAY_OF_MONTH].toString() + ApplicationClass.instance.getString(R.string.blank_space)
                        + calendar.getDisplayName(
                    Calendar.MONTH,
                    Calendar.SHORT,
                    Locale.getDefault()
                )
                        + ApplicationClass.instance.getString(R.string.blank_space) + calendar[Calendar.YEAR] + ApplicationClass.instance.getString(R.string.blank_space) + getTime(
                    calendarOrTimeStampInMillis
                ))
            }
        }
    }

    fun getPagueloFormatDate(timeStamp: Long): String {
        val date = StringBuilder()
        when {
            isYesterday(timeStamp) -> {
                date.append(ApplicationClass.instance.getString(R.string.Yesterday))
                date.append(" ")
            }
            isSameDay(timeStamp) -> {
                date.append(ApplicationClass.instance.getString(R.string.Today))
                date.append(" ")
            }
            isTomorrow(timeStamp) -> {
                date.append(ApplicationClass.instance.getString(R.string.Tomorrow))
                date.append(" ")
            }
        }

        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timeStamp
        date.append(calendar[Calendar.DAY_OF_MONTH].toString() + ApplicationClass.instance.getString(R.string.blank_space) + calendar.getDisplayName(
            Calendar.MONTH,
            Calendar.SHORT,
            Locale.getDefault()
        ) + ApplicationClass.instance.getString(R.string.blank_space) + calendar[Calendar.YEAR].toString())

        return date.toString()
    }


    fun getPagueloMovementsWithFilterFormatDate(timeStamp: Long): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timeStamp

        return addZeroPrefixIfOneDigitValue(calendar[Calendar.DAY_OF_MONTH]) + ApplicationClass.instance.getString(R.string.blank_space) + calendar.getDisplayName(
            Calendar.MONTH,
            Calendar.SHORT,
            Locale.getDefault()
        )
    }

    fun getPagueloNotificationFormatDate(timeStamp: Long): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timeStamp
        val date = StringBuilder()
        if (isSameDay(timeStamp)) {
            date.append("at")
            date.append(" ")
            date.append(addZeroPrefixIfOneDigitValue(change0To12(calendar[Calendar.HOUR])) + ":" + addZeroPrefixIfOneDigitValue(
                calendar[Calendar.MINUTE]
            ) + ApplicationClass.instance.getString(R.string.blank_space) + calendar.getDisplayName(
                Calendar.AM_PM, Calendar.LONG, Locale.getDefault()))
        } else {
            date.append("on")
            date.append(" ")
            date.append(addZeroPrefixIfOneDigitValue(change0To12(calendar[Calendar.DAY_OF_MONTH])) + "/" + addZeroPrefixIfOneDigitValue(
                (calendar[Calendar.MONTH] + 1)
            ) + "/" + calendar[Calendar.YEAR].toString().substring(2))

            date.append(" ")
            date.append("at")
            date.append(" ")
            date.append(change0To12(calendar[Calendar.HOUR]).toString() + ":" + addZeroPrefixIfOneDigitValue(
                calendar[Calendar.MINUTE]
            ) + ApplicationClass.instance.getString(R.string.blank_space) + calendar.getDisplayName(
                Calendar.AM_PM, Calendar.LONG, Locale.getDefault()))
        }
        return date.toString()
    }

    fun getPagueloNewsNotificationFormatDate(timeStamp: Long): String {
        val date = StringBuilder()
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timeStamp

        date.append(calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault()))
        date.append(" ")
        date.append(addZeroPrefixIfOneDigitValue(calendar[Calendar.DAY_OF_MONTH]))
        date.append("\n")
        date.append(change0To12(calendar[Calendar.HOUR]).toString() + ":" + addZeroPrefixIfOneDigitValue(
            calendar[Calendar.MINUTE]
        ) + ApplicationClass.instance.getString(R.string.blank_space) + calendar.getDisplayName(
            Calendar.AM_PM, Calendar.LONG, Locale.getDefault()))

        return date.toString()
    }

    fun getPagueloPendingActivityFormatDate(timeStamp: Long): String {
        val date = StringBuilder()
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timeStamp

        date.append(calendar[Calendar.DAY_OF_MONTH])
        date.append(" ")
        date.append(calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault()))
        date.append(" ")
        date.append(calendar[Calendar.YEAR])
        date.append(" - ")
        date.append(change0To12(calendar[Calendar.HOUR]).toString())
        date.append(":")
        date.append(addZeroPrefixIfOneDigitValue(calendar[Calendar.MINUTE]))
        date.append(" ")
        date.append(calendar.getDisplayName(Calendar.AM_PM, Calendar.LONG, Locale.getDefault()))
        return date.toString()
    }

    @JvmStatic
    fun getPagueloActivityDetailFormatDate(timeStamp: Long): String {
        val date = StringBuilder()
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timeStamp

        date.append(calendar[Calendar.DAY_OF_MONTH])
        date.append(" ")
        date.append(calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault()))
        date.append(" ")
        date.append(calendar[Calendar.YEAR])
        date.append(" ")
        date.append(change0To12(calendar[Calendar.HOUR]).toString())
        date.append(":")
        date.append(addZeroPrefixIfOneDigitValue(calendar[Calendar.MINUTE]))
        date.append(" ")
        date.append(calendar.getDisplayName(Calendar.AM_PM, Calendar.LONG, Locale.getDefault()))
        return date.toString()
    }

    fun getPagueloNotificationDateBannerFormatDate(timeStamp: Long): String? {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timeStamp
        return when {
            isSameDay(timeStamp) -> {
                ApplicationClass.instance.getString(R.string.Today)
            }
            isSameWeek(timeStamp) -> {
                ApplicationClass.instance.getString(R.string.this_week)
            }
            isSameMonth(timeStamp) -> {
                ApplicationClass.instance.getString(R.string.this_month)
            }
            else -> calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault()).capitalize(
                Locale.getDefault()) + ApplicationClass.instance.getString(R.string.blank_space) + calendar[Calendar.YEAR]
        }
    }

    fun getPagueloHomeWalletLastUpdatedFormatDate(timeStamp: Long): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timeStamp

        return calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()) + ApplicationClass.instance.getString(R.string.blank_space) + addZeroPrefixIfOneDigitValue(change0To12(calendar[Calendar.DAY_OF_MONTH]))
    }

    fun getFullDate(calendarOrTimeStampInMillis: Any): String {
        val calendar = Calendar.getInstance()
        if (calendarOrTimeStampInMillis is Calendar) {
            calendar.timeInMillis = calendarOrTimeStampInMillis.timeInMillis
        } else {
            calendar.timeInMillis = (calendarOrTimeStampInMillis as Long?)!!
        }
        return addZeroPrefixIfOneDigitValue(calendar[Calendar.DAY_OF_MONTH]) + "/" + addZeroPrefixIfOneDigitValue(
            calendar[Calendar.MONTH] + 1
        ) + "/" + calendar[Calendar.YEAR]
    }

    fun getFullTime(calendar: Calendar): String {
        return change0To12(calendar[Calendar.HOUR]).toString() + ":" + addZeroPrefixIfOneDigitValue(
            calendar[Calendar.MINUTE]
        ) + ApplicationClass.instance.getString(R.string.blank_space) + calendar.getDisplayName(
            Calendar.AM_PM, Calendar.LONG, Locale.getDefault())
    }

    fun getFullDateTime(calendar: Calendar): String {
        return if (isSameDay(calendar)) {
            change0To12(calendar[Calendar.HOUR])
                .toString() + ":" + addZeroPrefixIfOneDigitValue(calendar[Calendar.MINUTE]) + ApplicationClass.instance.getString(R.string.blank_space) + calendar.getDisplayName(
                Calendar.AM_PM,
                Calendar.SHORT,
                Locale.getDefault()
            ) + " \u2022 Today"
        } else if (isYesterday(calendar)) {
            change0To12(calendar[Calendar.HOUR])
                .toString() + ":" + addZeroPrefixIfOneDigitValue(calendar[Calendar.MINUTE]) + ApplicationClass.instance.getString(R.string.blank_space) + calendar.getDisplayName(
                Calendar.AM_PM,
                Calendar.SHORT,
                Locale.getDefault()
            ) + " \u2022 Yesterday"
        } else if (isSameYear(calendar)) {
            change0To12(calendar[Calendar.HOUR])
                .toString() + ":" + addZeroPrefixIfOneDigitValue(calendar[Calendar.MINUTE]) + ApplicationClass.instance.getString(R.string.blank_space) + calendar.getDisplayName(
                Calendar.AM_PM,
                Calendar.SHORT,
                Locale.getDefault()
            ) + " \u2022 " + calendar[Calendar.DAY_OF_MONTH] + ApplicationClass.instance.getString(R.string.blank_space) + calendar.getDisplayName(
                Calendar.MONTH,
                Calendar.SHORT,
                Locale.getDefault()
            )
        } else {
            change0To12(calendar[Calendar.HOUR])
                .toString() + ":" + addZeroPrefixIfOneDigitValue(calendar[Calendar.MINUTE]) + ApplicationClass.instance.getString(R.string.blank_space) + calendar.getDisplayName(
                Calendar.AM_PM,
                Calendar.SHORT,
                Locale.getDefault()
            ) + " \u2022 " + calendar[Calendar.DAY_OF_MONTH] + ApplicationClass.instance.getString(R.string.blank_space) + calendar.getDisplayName(
                Calendar.MONTH,
                Calendar.SHORT,
                Locale.getDefault()
            ) + ApplicationClass.instance.getString(R.string.blank_space) + calendar[Calendar.YEAR]
        }
    }

    fun change0To12(value: Int): Int {
        return if (value == 0) 12 else value
    }

    fun addZeroPrefixIfOneDigitValue(value: Any): String {
        val strValue = value.toString()
        return if (strValue.length == 1) "0$strValue" else strValue
    }

    /*----------------------------Binding Adapters---------------------------------*/
    @JvmStatic
    @BindingAdapter("Time")
    fun setTime(textView: TextView, calendarOrTimeStampInMillis: Any?) {
        if (calendarOrTimeStampInMillis != null) {
            textView.text = getTime(calendarOrTimeStampInMillis)
        } else {
            textView.text = ""
        }
    }

    @JvmStatic
    @BindingAdapter("date")
    fun setDate(textView: TextView, calendarOrTimeStampInMillis: Any?) {
        if (calendarOrTimeStampInMillis != null) {
            textView.text = getDateDdMm(calendarOrTimeStampInMillis)
        } else {
            textView.text = ""
        }
    }

    @JvmStatic
    @BindingAdapter("dateTime")
    fun setDateTime(textView: TextView, timeStamp: Long) {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = formatTimeInMillis(timeStamp)
        textView.text = getFormattedDateTime(calendar)
    }

    @JvmStatic
    @BindingAdapter("formattedDate")
    fun setFormattedDate(textView: TextView, calendarOrTimeStampInMillis: Any?) {
        if (calendarOrTimeStampInMillis != null) {
            if (calendarOrTimeStampInMillis is Calendar) {
                if (calendarOrTimeStampInMillis.timeInMillis == 0L) {
                    return
                }
            } else if (calendarOrTimeStampInMillis.toString().toLong() == 0L) {
                return
            }

            if (isYesterday(calendarOrTimeStampInMillis)) {
                textView.text = ApplicationClass.instance.getString(R.string.Yesterday)
            } else if (isSameDay(calendarOrTimeStampInMillis)) {
                textView.text = ApplicationClass.instance.getString(R.string.Today)
            } else if (isTomorrow(calendarOrTimeStampInMillis)) {
                textView.text = ApplicationClass.instance.getString(R.string.Tomorrow)
            } else {
                val calendar = Calendar.getInstance()
                if (calendarOrTimeStampInMillis is Calendar) {
                    calendar.timeInMillis = calendarOrTimeStampInMillis.timeInMillis
                } else {
                    calendar.timeInMillis = (calendarOrTimeStampInMillis as Long)
                }
                if (isSameYear(calendarOrTimeStampInMillis)) {
                    textView.text = (calendar[Calendar.DAY_OF_MONTH].toString() + ApplicationClass.instance.getString(R.string.blank_space)
                            + calendar.getDisplayName(
                        Calendar.MONTH,
                        Calendar.SHORT,
                        Locale.getDefault()
                    ))
                } else {
                    textView.text = (calendar[Calendar.DAY_OF_MONTH].toString() + ApplicationClass.instance.getString(R.string.blank_space)
                            + calendar.getDisplayName(
                        Calendar.MONTH,
                        Calendar.SHORT,
                        Locale.getDefault()
                    )
                            + ApplicationClass.instance.getString(R.string.blank_space) + calendar[Calendar.YEAR])
                }
            }
        } else {
            textView.text = ""
        }
    }

    @JvmStatic
    @BindingAdapter("formattedDateTime")
    fun setFormattedDateTime(textView: TextView, timestamp: Long?) {
        if (timestamp != null) {
            textView.text = getFormattedDateTime(timestamp)
        } else {
            textView.text = ""
        }
    }

    @JvmStatic
    @BindingAdapter("pagueloFormatDate")
    fun setPagueloFormatDate(textView: TextView, timestamp: Long?) {
        textView.text = if (timestamp == null) "" else getPagueloFormatDate(timestamp)
    }

    @JvmStatic
    fun sqlDateTimeFormatToTimeStampInMillis(dateTimeSql: String?): Long {
        val formatter: DateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        try {
            formatter.parse(dateTimeSql)
        } catch (e: ParseException) {
        }
        val calendar = formatter.calendar
        return calendar.timeInMillis
    }

    @JvmStatic
    fun utcDateTimeFormatToTimeStampInMillis(dateTimeSql: String): Long {
        val formatter: DateFormat = SimpleDateFormat(UTC_DATE_FORMAT, Locale.getDefault())
        try {
            formatter.parse(dateTimeSql)
        } catch (e: ParseException) {
        }
        val calendar = formatter.calendar
        calendar.timeInMillis
        return calendar.timeInMillis
    }

    @JvmStatic
    fun sqlDateFormatToTimeStampInMillis(dateTimeSql: String?): Long {
        val formatter: DateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        try {
            formatter.parse(dateTimeSql)
        } catch (e: ParseException) {
        }
        val calendar = formatter.calendar
        return calendar.timeInMillis
    }

    @JvmStatic
    fun sqlDateFormat1ToTimeStampInMillis(dateTimeSql: String?): Long {
        val formatter: DateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        try {
            formatter.parse(dateTimeSql)
        } catch (e: ParseException) {
        }
        val calendar = formatter.calendar
        return calendar.timeInMillis
    }

    @JvmStatic
    fun formatTimeInMillis(timeStamp: Long): Long {
        var timeStamp = timeStamp
        val strTimeStamp = timeStamp.toString()
        if (strTimeStamp.length < 13) {
            timeStamp = timeStamp * 1000
        }
        return timeStamp
    }

    @JvmStatic
    fun getFormattedTime(timeStamp: Long): String {
        /*Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeStamp);*/
        val hours = TimeUnit.MILLISECONDS.toHours(timeStamp)
        var remainingTimeInMillis = timeStamp - TimeUnit.HOURS.toMillis(hours)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(remainingTimeInMillis)
        remainingTimeInMillis =
            timeStamp - (TimeUnit.HOURS.toMillis(hours) + TimeUnit.MINUTES.toMillis(minutes))
        val seconds = TimeUnit.MILLISECONDS.toSeconds(remainingTimeInMillis)
        return (addZeroPrefixIfOneDigitValue(hours) + ":"
                + addZeroPrefixIfOneDigitValue(minutes) + ":"
                + addZeroPrefixIfOneDigitValue(seconds))
    }

    /**
     * function to get times in milies
     */
    @JvmStatic
    fun getTimeInMilliFromUtcFormat(date: String?): Long {
        var timeInMilliseconds = 0L
        val sdf = SimpleDateFormat(UTC_DATE_FORMAT, Locale.getDefault())
        try {
            sdf.timeZone = TimeZone.getTimeZone("GMT")
            date?.let { date ->
                val mDate = sdf.parse(date)
                mDate?.let {
                    val formatDate =
                        formatDate(sdf.format(it), UTC_DATE_FORMAT, UTC_DATE_FORMAT)
                    val newDate =
                        SimpleDateFormat(UTC_DATE_FORMAT, Locale.getDefault()).parse(formatDate)
                    timeInMilliseconds = newDate!!.time
                }
            }
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return timeInMilliseconds
    }

    /**
     * this method is used to get the date with month name
     *
     * @param completeDate
     * @return
     */
    @JvmStatic
    private fun formatDate(
        completeDate: String,
        inputFormat: String,
        outputFormat: String,
    ): String {
        val originalFormat = SimpleDateFormat(inputFormat, Locale.ENGLISH)
        val targetFormat = SimpleDateFormat(outputFormat, Locale.getDefault())
        val date: Date?
        var formattedDate = ""
        try {
            originalFormat.timeZone = TimeZone.getTimeZone("GMT")
            date = originalFormat.parse(completeDate)
            formattedDate = targetFormat.format(date!!)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return formattedDate
    }

    @JvmStatic
    fun getFormattedDate(timeStamp: Long): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timeStamp
        calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault())?.let {
            return it + ApplicationClass.instance.getString(R.string.blank_space) + calendar[Calendar.DAY_OF_MONTH].toString() + ", " + calendar[Calendar.YEAR]
        }
        return ""
    }

    @JvmStatic
    fun getTimeZoneOffset(): String {
        val mCalendar: Calendar = GregorianCalendar()
        val mTimeZone = mCalendar.timeZone
        return ((mTimeZone.rawOffset) / (1000 * 60)).toString()
    }

    fun getUtcDateFromTimeStamp(timeStamp: Long?): String {
        val sdf = SimpleDateFormat(UTC_DATE_FORMAT, Locale.getDefault())
        return sdf.format(timeStamp)
    }
}
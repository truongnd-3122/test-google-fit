package com.example.myapplication.utils

import android.text.TextUtils
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * convert string to date
 * if string is blank or format is blank then return null
 * if string cannot be parsed then return null
 * else return date
 */
fun String.toDate(
    format: String, locale: Locale = Locale.getDefault()
): Date? {
    if (this.isBlank() || format.isBlank()) return null
    return try {
        SimpleDateFormat(format, locale).parse(this)
    } catch (e: Exception) {
        e.safeLog()
        null
    }
}

/**
 * convert string to time long milliseconds
 * use function string to date
 */
fun String.toTimeLong(
    format: String, locale: Locale = Locale.getDefault()
): Long? = toDate(format, locale)?.time

/**
 * convert time long milliseconds to string with predefined format
 * if format is blank return null
 * if format is not java date time format then catch Exception and return null
 * else return formatted string
 */
fun Long.toTimeString(
    format: String, locale: Locale = Locale.getDefault()
): String? {
    if (format.isBlank()) return null
    return try {
        SimpleDateFormat(format, locale).format(Date(this))
    } catch (e: Exception) {
        e.safeLog()
        null
    }
}

fun String.changeTimeFormat(
    oldFormat: String,
    newFormat: String,
    locale: Locale = Locale.getDefault(),
    alternateFormat: String = ""
): String? {
    if (TextUtils.isEmpty(this) || (TextUtils.isEmpty(oldFormat) && TextUtils.isEmpty(
            alternateFormat
        )) || TextUtils.isEmpty(
            newFormat
        )
    ) return null
    return try {
        val simpleDateFormat = SimpleDateFormat(oldFormat, locale)
        val date = simpleDateFormat.parse(this)
        simpleDateFormat.applyPattern(newFormat)
        simpleDateFormat.format(date)
    } catch (e: ParseException) {
        return try {
            val simpleDateFormat = SimpleDateFormat(alternateFormat, locale)
            val date = simpleDateFormat.parse(this)
            simpleDateFormat.applyPattern(newFormat)
            simpleDateFormat.format(date)
        } catch (e: ParseException) {
            null
        }
    }
}

fun getDaysInMonth(month: Int, year: Int): Int {
    return when (month) {
        Calendar.JANUARY, Calendar.MARCH, Calendar.MAY, Calendar.JULY, Calendar.AUGUST, Calendar.OCTOBER, Calendar.DECEMBER -> 31
        Calendar.APRIL, Calendar.JUNE, Calendar.SEPTEMBER, Calendar.NOVEMBER -> 30
        Calendar.FEBRUARY -> if (year % 4 == 0 && year % 100 != 0 || year % 400 == 0) 29 else 28
        else -> throw IllegalArgumentException("Invalid Month")
    }
}


fun Date.toTimeString(format: String, locale: Locale = Locale.getDefault()): String? {
    return if (format.isBlank()) null
    else try {
        SimpleDateFormat(format, locale).format(this)
    } catch (e: Exception) {
        e.safeLog()
        null
    }
}

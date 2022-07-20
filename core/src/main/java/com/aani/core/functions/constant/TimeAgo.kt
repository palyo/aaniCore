package com.aani.core.functions.constant

import android.content.Context
import android.text.format.DateFormat
import java.util.*
import java.util.concurrent.TimeUnit

fun covertTimeToText(dataDate: Long): String {
    var convertTime: String? = null
    val suffix = "ago"
    val pasTime = Date(dataDate)
    val nowTime = Date()
    val dateDiff = nowTime.time - pasTime.time
    val second = TimeUnit.MILLISECONDS.toSeconds(dateDiff)
    val minute = TimeUnit.MILLISECONDS.toMinutes(dateDiff)
    val hour = TimeUnit.MILLISECONDS.toHours(dateDiff)
    val day = TimeUnit.MILLISECONDS.toDays(dateDiff)
    convertTime = if (second < 60) {
        if (second == 1L) {
            "$second second $suffix"
        } else {
            "$second seconds $suffix"
        }
    } else if (minute < 60) {
        if (minute == 1L) {
            "$minute minute $suffix"
        } else {
            "$minute minutes $suffix"
        }
    } else if (hour < 24) {
        if (hour == 1L) {
            "$hour hour $suffix"
        } else {
            "$hour hours $suffix"
        }
    } else if (day >= 7) {
        if (day >= 365) {
            val tempYear = day / 365
            if (tempYear == 1L) {
                "$tempYear year $suffix"
            } else {
                "$tempYear years $suffix"
            }
        } else if (day >= 30) {
            val tempMonth = day / 30
            if (tempMonth == 1L) {
                (day / 30).toString() + " month " + suffix
            } else {
                (day / 30).toString() + " months " + suffix
            }
        } else {
            val tempWeek = day / 7
            if (tempWeek == 1L) {
                (day / 7).toString() + " week " + suffix
            } else {
                (day / 7).toString() + " weeks " + suffix
            }
        }
    } else {
        if (day == 1L) {
            "$day day $suffix"
        } else {
            "$day days $suffix"
        }
    }
    return convertTime
}

fun getFormattedDate(context: Context?, smsTimeInMillis: Long): String {
    val smsTime = Calendar.getInstance()
    smsTime.timeInMillis = smsTimeInMillis
    val now = Calendar.getInstance()
    val timeFormatString = "h:mm aa"
    val dateTimeFormatString = "EEEE, MMMM d, h:mm aa"
    val HOURS = (60 * 60 * 60).toLong()
    return if (now[Calendar.DATE] == smsTime[Calendar.DATE]) {
        "Today " + DateFormat.format(timeFormatString, smsTime)
    } else if (now[Calendar.DATE] - smsTime[Calendar.DATE] == 1) {
        "Yesterday " + DateFormat.format(timeFormatString, smsTime)
    } else if (now[Calendar.YEAR] == smsTime[Calendar.YEAR]) {
        DateFormat.format(dateTimeFormatString, smsTime).toString()
    } else {
        DateFormat.format("MMMM dd yyyy, h:mm aa", smsTime).toString()
    }
}

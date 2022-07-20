package com.aani.core.functions.constant

import android.text.format.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

val currentMonth: Int
    get() = Calendar.getInstance()[Calendar.MONTH]

fun getMonth(date: Date?): Int {
    val calendar = Calendar.getInstance()
    calendar.time = date
    return calendar[Calendar.MONTH]
}

val currentYear: Int
    get() = Calendar.getInstance()[Calendar.MONTH]

fun getYear(date: Date?): Int {
    val calendar = Calendar.getInstance()
    calendar.time = date
    return calendar[Calendar.MONTH]
}

fun decrementDateByOne(date: Date?): Date {
    val c = Calendar.getInstance()
    c.time = date
    c.add(Calendar.DATE, -1)
    return c.time
}

val currentDate: Date
    get() = Calendar.getInstance().time

fun getDateFrommMilli(date: Long, format: String?): String {
    return DateFormat.format(format, Date(date)).toString()
}

fun getCurrentDateString(format: String?): String {
    val df = SimpleDateFormat(format)
    return df.format(Calendar.getInstance().time)
}

@Throws(ParseException::class)
fun changeDateFormatInString(
    date: String?,
    formatFrom: String?,
    formatTo: String?,
): String {
    val from = SimpleDateFormat(formatFrom,Locale.getDefault())
    val to = SimpleDateFormat(formatTo,Locale.getDefault())
    val dt = from.parse(date!!)
    return to.format(dt!!)
}

val currentTimeInMilli: Long
    get() = Calendar.getInstance().timeInMillis

fun getTimeInMilli(date: Date?): Long {
    val calendar = Calendar.getInstance()
    calendar.time = date
    return calendar.timeInMillis
}

@Throws(ParseException::class)
fun getTimeInMilli(date: String?, format: String?): Long {
    val calendar = Calendar.getInstance()
    val from = SimpleDateFormat(format,Locale.getDefault())
    val date1 = from.parse(date!!)
    calendar.time = date1!!
    return calendar.timeInMillis
}

val currentDay: Int
    get() = Calendar.getInstance()[Calendar.DAY_OF_MONTH]

fun getDay(date: Date?): Int {
    val calendar = Calendar.getInstance()
    calendar.time = date!!
    return calendar[Calendar.DAY_OF_MONTH]
}

@Throws(ParseException::class)
fun getDay(date: String?, format: String?): Int {
    val calendar = Calendar.getInstance()
    val from = SimpleDateFormat(format,Locale.getDefault())
    val date1 = from.parse(date!!)
    calendar.time = date1!!
    return calendar[Calendar.DAY_OF_MONTH]
}

@Throws(ParseException::class)
fun getMonth(date: String?, format: String?): Int {
    val calendar = Calendar.getInstance()
    val from = SimpleDateFormat(format,Locale.getDefault())
    val date1 = from.parse(date!!)
    calendar.time = date1!!
    return calendar[Calendar.MONTH]
}

fun getMonthName(date: Date?): String {
    val cal = Calendar.getInstance()
    cal.time = date
    val month_date = SimpleDateFormat("MMMM",Locale.getDefault())
    return month_date.format(cal.time)
}

@Throws(ParseException::class)
fun getMonthName(date: String?, format: String?): String {
    val cal = Calendar.getInstance()
    val monthDate = SimpleDateFormat("MMMM",Locale.getDefault())
    val monthFormat = SimpleDateFormat("format",Locale.getDefault())
    val date1 = monthFormat.parse(date!!)
    cal.time = date1!!
    return monthDate.format(cal.time)
}

fun getMonthName(month: Int): String {
    val cal = Calendar.getInstance()
    val monthDate = SimpleDateFormat("MMMM",Locale.getDefault())
    cal[Calendar.MONTH] = month
    return monthDate.format(cal.time)
}

@Throws(ParseException::class)
fun getYear(date: String?, format: String?): Int {
    val calendar = Calendar.getInstance()
    val from = SimpleDateFormat(format,Locale.getDefault())
    val date1 = from.parse(date!!)
    calendar.time = date1!!
    return calendar[Calendar.YEAR]
}

fun getDayOfWeek(year: Int, month: Int): Int {
    val cal = Calendar.getInstance()
    cal[year, month] = 1
    return cal[Calendar.DAY_OF_WEEK]
}

fun numberOfDaysInMonth(month: Int, year: Int): Int {
    val monthStart: Calendar = GregorianCalendar(year, month - 1, 1)
    return monthStart.getActualMaximum(Calendar.DAY_OF_MONTH)
}

fun incrementDateByOne(date: Date?): Date {
    val c = Calendar.getInstance()
    c.time = date!!
    c.add(Calendar.DATE, 1)
    return c.time
}


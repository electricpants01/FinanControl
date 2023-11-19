package com.locotoDevTeam.financontrol.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


fun String.toDate(): Date{
    return Date(this.toLong())
}

fun String.formatDateAndTimeString(): String {
    val date = Date(this.toLong())
    val simple = SimpleDateFormat(Constants.monthDayHour,Locale.getDefault())
    return simple.format(date)
}

fun String.formatDateString(): String {
    val date = Date(this.toLong())
        val simple = SimpleDateFormat(Constants.monthDayYear,Locale.getDefault())
    return simple.format(date)
}

fun String.formatHourString(): String {
    val date = Date(this.toLong())
    val simple = SimpleDateFormat(Constants.hour,Locale.getDefault())
    return simple.format(date)
}

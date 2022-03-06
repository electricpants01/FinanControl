package com.locotoDevTeam.financontrol.util

import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*


fun String.toDate(): Date{
    return Date(this.toLong())
}

fun String.formatDateAndTimeString(): String {
    val date = Date(this.toLong())
    val simple = SimpleDateFormat("MMMM', 'dd' 'h':'mm a",Locale.getDefault())
    return simple.format(date)
}

fun String.formatDateString(): String {
    val date = Date(this.toLong())
    val simple = SimpleDateFormat("MM', 'dd",Locale.getDefault())
    return simple.format(date)
}

fun String.formatHourString(): String {
    val date = Date(this.toLong())
    val simple = SimpleDateFormat("H':'mm",Locale.getDefault())
    return simple.format(date)
}

package com.locotoDevTeam.financontrol.util

import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*


fun Long.toDate(): Date{
    return Date(this)
}

fun Long.formatDateAndTimeString(): String {
    val date = Date(this)
    val simple = SimpleDateFormat("MMMM', 'dd' 'h':'mm a",Locale.getDefault())
    return simple.format(date)
}

fun Long.formatDateString(): String {
    val date = Date(this)
    val simple = SimpleDateFormat("MM', 'dd",Locale.getDefault())
    return simple.format(date)
}

fun Long.formatHourString(): String {
    val date = Date(this)
    val simple = SimpleDateFormat("H':'mm",Locale.getDefault())
    return simple.format(date)
}

package com.locotoDevTeam.financontrol.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun Date.toStringSectionName(): String {
    val dateFormatter = SimpleDateFormat(Constants.monthDay, Locale.getDefault())
    return dateFormatter.format(this)
}
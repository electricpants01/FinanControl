package com.locotoDevTeam.financontrol.util

import java.text.SimpleDateFormat
import java.util.*

fun Date.toStringSectionName(): String {
    val dateFormatter = SimpleDateFormat(Constants.monthDay, Locale.getDefault())
    return dateFormatter.format(this)
}
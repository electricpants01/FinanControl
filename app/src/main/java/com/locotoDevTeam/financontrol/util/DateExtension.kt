package com.locotoDevTeam.financontrol.util

import java.text.SimpleDateFormat
import java.util.*

fun Date.toStringSectionName(): String {
    val dateFormatter = SimpleDateFormat("MMMM', 'dd", Locale.getDefault())
    return dateFormatter.format(this)
}
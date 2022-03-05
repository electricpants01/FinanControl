package com.locotoDevTeam.financontrol.util

import java.time.LocalDate
import java.util.*


fun String.toDate(): Date{
    return Date(this.toLong())
}


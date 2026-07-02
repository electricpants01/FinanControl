package com.locotoDevTeam.financontrol.database.converter

import androidx.room.TypeConverter
import com.locotoDevTeam.financontrol.database.entity.TransactionType

/**
 * Room TypeConverters for the FinanControl database.
 * Currently handles [TransactionType] enum persistence.
 */
class Converters {

    /**
     * Converts a [TransactionType] enum to its stored database String value.
     * e.g. TransactionType.INCOME -> "Income"
     */
    @TypeConverter
    fun fromTransactionType(type: TransactionType): String = type.dbValue

    /**
     * Converts a stored database String back to a [TransactionType] enum.
     * e.g. "Expense" -> TransactionType.EXPENSE
     */
    @TypeConverter
    fun toTransactionType(value: String): TransactionType =
        TransactionType.entries.first { it.dbValue == value }
}

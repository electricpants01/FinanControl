package com.locotoDevTeam.financontrol.database.entity

/**
 * Type-safe enum replacing the fragile String-based Income.type field.
 * Each constant maps to its stored database String value.
 */
enum class TransactionType(val dbValue: String) {
    INCOME("Income"),
    EXPENSE("Expense");
}

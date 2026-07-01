package com.locotoDevTeam.financontrol.ui.insight

import androidx.lifecycle.LiveData
import com.locotoDevTeam.financontrol.database.dao.IncomeDao
import com.locotoDevTeam.financontrol.database.entity.Income

/**
 * Repository that wraps Room DAO calls for Insight (per-category income/expense) operations.
 * All data access from ViewModels/Fragments MUST go through this repository,
 * never through DAOs directly.
 */
class InsightRepository(
    private val incomeDao: IncomeDao
) {

    /**
     * Returns a LiveData list of all income/expense entries for a specific category.
     * Observed by the UI for reactive updates.
     */
    fun getAllByCategoryId(categoryId: Long): LiveData<List<Income>> =
        incomeDao.getAllByCategoryId(categoryId)

    /**
     * Deletes an income/expense entry from the database.
     */
    suspend fun deleteIncome(income: Income) {
        incomeDao.delete(income)
    }
}
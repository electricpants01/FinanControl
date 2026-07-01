package com.locotoDevTeam.financontrol.ui.category

import androidx.lifecycle.LiveData
import com.locotoDevTeam.financontrol.database.dao.CategoryDao
import com.locotoDevTeam.financontrol.database.dao.IncomeDao
import com.locotoDevTeam.financontrol.database.entity.Category

/**
 * Repository that wraps Room DAO calls for Category-related operations.
 * All data access from ViewModels/Fragments MUST go through this repository,
 * never through DAOs directly.
 */
class CategoryRepository(
    private val categoryDao: CategoryDao,
    private val incomeDao: IncomeDao
) {

    /**
     * Returns a LiveData list of all categories. Observed by the UI for reactive updates.
     */
    fun getAllCategories(): LiveData<List<Category>> = categoryDao.getAll()

    /**
     * Inserts a new category into the database.
     */
    suspend fun insertCategory(category: Category) {
        categoryDao.insert(category)
    }

    /**
     * Deletes a category and all its associated income/expense entries by category ID.
     * Order matters: delete incomes first, then the category itself.
     */
    suspend fun deleteCategoryById(categoryId: Long) {
        categoryDao.deleteIncomesFromACategory(categoryId)
        categoryDao.deleteCategoryById(categoryId)
    }

    /**
     * Inserts an income/expense entry into the database.
     */
    suspend fun insertIncome(income: com.locotoDevTeam.financontrol.database.entity.Income) {
        incomeDao.insert(income)
    }

    /**
     * Returns the total sum of all Income-type amounts across all categories.
     */
    suspend fun getSumIncome(): Double = incomeDao.getSumIncome()

    /**
     * Returns the total sum of all Expense-type amounts across all categories.
     */
    suspend fun getSumExpense(): Double = incomeDao.getSumExpense()
}
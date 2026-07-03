package com.locotoDevTeam.financontrol.page

import com.locotoDevTeam.financontrol.database.dao.CategoryDao
import com.locotoDevTeam.financontrol.database.dao.IncomeDao
import com.locotoDevTeam.financontrol.database.entity.Category
import com.locotoDevTeam.financontrol.database.entity.Income
import com.locotoDevTeam.financontrol.database.entity.TransactionType
import com.locotoDevTeam.financontrol.ui.category.CategoryRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf

class CategoryRepositoryPage {

    val categoryDao: CategoryDao = mockk(relaxed = true)
    val incomeDao: IncomeDao = mockk(relaxed = true)
    val repository = CategoryRepository(categoryDao, incomeDao)

    // ── Stub helpers ──

    fun stubGetAllCategories(vararg categories: Category) {
        every { categoryDao.getAll() } returns flowOf(categories.toList())
    }

    fun stubGetSumIncome(amount: Double) {
        every { incomeDao.getSumIncome() } returns amount
    }

    fun stubGetSumExpense(amount: Double) {
        every { incomeDao.getSumExpense() } returns amount
    }

    // ── Assertion helpers (verify interactions) ──

    fun verifyInsertCategoryCalled(name: String) {
        verify { categoryDao.insert(match { it.name == name }) }
    }

    fun verifyInsertIncomeCalled(type: TransactionType, amount: Double, categoryId: Long) {
        verify {
            incomeDao.insert(match {
                it.type == type && it.amount == amount && it.categoryId == categoryId
            })
        }
    }

    fun verifyDeleteCategoryByIdCalled(categoryId: Long) {
        verify { categoryDao.deleteIncomesFromACategory(categoryId) }
        verify { categoryDao.deleteCategoryById(categoryId) }
    }

    fun verifyGetSumIncomeCalled() {
        verify { incomeDao.getSumIncome() }
    }

    fun verifyGetSumExpenseCalled() {
        verify { incomeDao.getSumExpense() }
    }

    fun verifyNoInsertCategoryCalled() {
        verify(exactly = 0) { categoryDao.insert(any()) }
    }

    fun verifyNoInsertIncomeCalled() {
        verify(exactly = 0) { incomeDao.insert(any()) }
    }
}

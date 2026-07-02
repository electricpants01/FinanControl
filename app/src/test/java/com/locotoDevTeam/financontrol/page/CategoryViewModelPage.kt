package com.locotoDevTeam.financontrol.page

import com.locotoDevTeam.financontrol.database.dao.CategoryDao
import com.locotoDevTeam.financontrol.database.dao.IncomeDao
import com.locotoDevTeam.financontrol.ui.category.CategoryRepository
import com.locotoDevTeam.financontrol.ui.category.CategoryViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.CoroutineDispatcher

class CategoryViewModelPage {

    val categoryDao: CategoryDao = mockk(relaxed = true)
    val incomeDao: IncomeDao = mockk(relaxed = true)
    val repository: CategoryRepository = mockk(relaxed = true)
    val viewModel = CategoryViewModel(repository)

    fun setTestDispatcher(dispatcher: CoroutineDispatcher) {
        viewModel.setTestDispatcher(dispatcher)
    }

    // ── Stub helpers ──

    fun stubInsertCategory() {
        coEvery { repository.insertCategory(any()) } returns Unit
    }

    fun stubInsertIncome() {
        coEvery { repository.insertIncome(any()) } returns Unit
    }

    fun stubDeleteCategoryById() {
        coEvery { repository.deleteCategoryById(any()) } returns Unit
    }

    fun stubGetSumIncome(amount: Double) {
        coEvery { repository.getSumIncome() } returns amount
    }

    fun stubGetSumExpense(amount: Double) {
        coEvery { repository.getSumExpense() } returns amount
    }

    // ── Assertion helpers ──

    fun verifyInsertCategoryCalled(name: String) {
        coVerify { repository.insertCategory(match { it.name == name }) }
    }

    fun verifyInsertIncomeCalled(type: String, amount: Double, categoryId: Long) {
        coVerify {
            repository.insertIncome(match {
                it.type == type && it.amount == amount && it.categoryId == categoryId
            })
        }
    }

    fun verifyDeleteCategoryByIdCalled(categoryId: Long) {
        coVerify { repository.deleteCategoryById(categoryId) }
    }

    fun verifyGetSumIncomeCalled() {
        coVerify { repository.getSumIncome() }
    }

    fun verifyGetSumExpenseCalled() {
        coVerify { repository.getSumExpense() }
    }
}

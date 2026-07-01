package com.locotoDevTeam.financontrol.repository

import com.locotoDevTeam.financontrol.database.entity.Category
import com.locotoDevTeam.financontrol.database.entity.Income
import com.locotoDevTeam.financontrol.page.CategoryRepositoryPage
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for CategoryRepository.
 * Uses Page Object pattern: CategoryRepositoryPage encapsulates mock setup and assertions.
 */
class CategoryRepositoryTest {

    private val page = CategoryRepositoryPage()

    @Before
    fun setUp() {
        // Page object already initializes mocks and repository
    }

    // ── getAllCategories ──

    @Test
    fun `getAllCategories returns LiveData from DAO`() {
        val categories = listOf(Category(1L, "Salary"), Category(2L, "Rent"))
        page.stubGetAllCategories(*categories.toTypedArray())

        val result = page.repository.getAllCategories()

        assert(result.value == categories)
    }

    @Test
    fun `getAllCategories returns empty list when no categories`() {
        page.stubGetAllCategories()

        val result = page.repository.getAllCategories()

        assert(result.value!!.isEmpty())
    }

    // ── insertCategory ──

    @Test
    fun `insertCategory delegates to DAO`() = runTest {
        val category = Category(name = "Groceries")

        page.repository.insertCategory(category)

        page.verifyInsertCategoryCalled("Groceries")
    }

    @Test
    fun `insertCategory with empty name still delegates`() = runTest {
        val category = Category(name = "")

        page.repository.insertCategory(category)

        page.verifyInsertCategoryCalled("")
    }

    // ── insertIncome ──

    @Test
    fun `insertIncome delegates to IncomeDao`() = runTest {
        val income = Income(type = "Income", amount = 100.0, categoryId = 1L, timestamp = "12345")

        page.repository.insertIncome(income)

        page.verifyInsertIncomeCalled("Income", 100.0, 1L)
    }

    @Test
    fun `insertIncome delegates Expense type`() = runTest {
        val income = Income(type = "Expense", amount = 50.0, categoryId = 2L, timestamp = "67890")

        page.repository.insertIncome(income)

        page.verifyInsertIncomeCalled("Expense", 50.0, 2L)
    }

    // ── deleteCategoryById ──

    @Test
    fun `deleteCategoryById deletes incomes first then category`() = runTest {
        page.repository.deleteCategoryById(42L)

        page.verifyDeleteCategoryByIdCalled(42L)
    }

    // ── getSumIncome / getSumExpense ──

    @Test
    fun `getSumIncome returns value from IncomeDao`() = runTest {
        page.stubGetSumIncome(1500.0)

        val result = page.repository.getSumIncome()

        assert(result == 1500.0)
        page.verifyGetSumIncomeCalled()
    }

    @Test
    fun `getSumExpense returns value from IncomeDao`() = runTest {
        page.stubGetSumExpense(500.0)

        val result = page.repository.getSumExpense()

        assert(result == 500.0)
        page.verifyGetSumExpenseCalled()
    }

    @Test
    fun `getSumIncome returns zero when no income`() = runTest {
        page.stubGetSumIncome(0.0)

        val result = page.repository.getSumIncome()

        assert(result == 0.0)
    }

    @Test
    fun `getSumExpense returns zero when no expense`() = runTest {
        page.stubGetSumExpense(0.0)

        val result = page.repository.getSumExpense()

        assert(result == 0.0)
    }
}

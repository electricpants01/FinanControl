package com.locotoDevTeam.financontrol.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.locotoDevTeam.financontrol.database.entity.Category
import com.locotoDevTeam.financontrol.database.entity.TransactionType
import com.locotoDevTeam.financontrol.page.CategoryViewModelPage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CategoryViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val page = CategoryViewModelPage()
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ── insertNewCategory ──

    @Test
    fun `insertNewCategory delegates to repository`() = runTest {
        page.stubInsertCategory()
        val vm = page.createViewModel()
        vm.setTestDispatcher(testDispatcher)

        vm.insertNewCategory("Salary")
        advanceUntilIdle()

        page.verifyInsertCategoryCalled("Salary")
    }

    @Test
    fun `insertNewCategory with empty name still delegates`() = runTest {
        page.stubInsertCategory()
        val vm = page.createViewModel()
        vm.setTestDispatcher(testDispatcher)

        vm.insertNewCategory("")
        advanceUntilIdle()

        page.verifyInsertCategoryCalled("")
    }

    // ── insertNewIncomeExpense ──

    @Test
    fun `insertNewIncomeExpense delegates Income type`() = runTest {
        page.stubInsertIncome()
        val vm = page.createViewModel()
        vm.setTestDispatcher(testDispatcher)

        vm.insertNewIncomeExpense(1L, 200.0, TransactionType.INCOME)
        advanceUntilIdle()

        page.verifyInsertIncomeCalled(TransactionType.INCOME, 200.0, 1L)
    }

    @Test
    fun `insertNewIncomeExpense delegates Expense type`() = runTest {
        page.stubInsertIncome()
        val vm = page.createViewModel()
        vm.setTestDispatcher(testDispatcher)

        vm.insertNewIncomeExpense(3L, 75.0, TransactionType.EXPENSE)
        advanceUntilIdle()

        page.verifyInsertIncomeCalled(TransactionType.EXPENSE, 75.0, 3L)
    }

    // ── deleteACategoryById ──

    @Test
    fun `deleteACategoryById delegates to repository`() = runTest {
        page.stubDeleteCategoryById()
        val vm = page.createViewModel()
        vm.setTestDispatcher(testDispatcher)

        vm.deleteACategoryById(10L)
        advanceUntilIdle()

        page.verifyDeleteCategoryByIdCalled(10L)
    }

    // ── categoriesUiState ──

    @Test
    fun `categoriesUiState updates when categories are emitted`() = runTest {
        page.stubGetSumIncome(1000.0)
        page.stubGetSumExpense(400.0)
        val vm = page.createViewModel()
        vm.setTestDispatcher(testDispatcher)

        // Let the init block's collect start (it will suspend waiting for emission)
        advanceUntilIdle()
        page.emitCategories(Category(1L, "Salary"), Category(2L, "Rent"))
        advanceUntilIdle()

        val state = vm.categoriesUiState.value
        assertFalse(state.isEmpty)
        assertEquals(1000.0, state.totalIncome, 0.0)
        assertEquals(400.0, state.totalExpense, 0.0)
        assertEquals(600.0, state.totalBalance, 0.0)
        assertEquals(2, state.categories.size)
        page.verifyGetSumIncomeCalled()
        page.verifyGetSumExpenseCalled()
    }

    @Test
    fun `categoriesUiState initial state is empty`() {
        val vm = page.createViewModel()

        val state = vm.categoriesUiState.value
        assertTrue(state.isEmpty)
        assertEquals(0.0, state.totalBalance, 0.0)
        assertTrue(state.categories.isEmpty())
    }

    @Test
    fun `categoriesUiState isEmpty when categories empty`() = runTest {
        page.stubGetSumIncome(0.0)
        page.stubGetSumExpense(0.0)
        val vm = page.createViewModel()
        vm.setTestDispatcher(testDispatcher)

        advanceUntilIdle()
        page.emitCategories()
        advanceUntilIdle()

        val state = vm.categoriesUiState.value
        assertTrue(state.isEmpty)
    }

    @Test
    fun `categoriesUiState returns StateFlow`() {
        val vm = page.createViewModel()
        assertNotNull(vm.categoriesUiState)
    }
}

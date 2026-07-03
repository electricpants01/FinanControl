package com.locotoDevTeam.financontrol.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.locotoDevTeam.financontrol.database.entity.Income
import com.locotoDevTeam.financontrol.database.entity.TransactionType
import com.locotoDevTeam.financontrol.page.InsightViewModelPage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class InsightViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val page = InsightViewModelPage()
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        page.setTestDispatcher(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `setCategoryId updates categoryId LiveData`() {
        page.viewModel.setCategoryId(42L)
        assertEquals(42L, page.viewModel.categoryId.value)
    }

    @Test
    fun `setCategoryId overwrites previous categoryId`() {
        page.viewModel.setCategoryId(1L)
        page.viewModel.setCategoryId(99L)
        assertEquals(99L, page.viewModel.categoryId.value)
    }

    @Test
    fun `deleteInsight delegates to repository`() = runTest {
        val income = Income(1L, "Test", TransactionType.INCOME, 100.0, 5L, 1000L)
        page.stubDeleteIncome(income)

        page.viewModel.deleteInsight(income)
        advanceUntilIdle()

        page.verifyDeleteIncomeCalled(income)
    }

    @Test
    fun `deleteInsight with Expense type delegates`() = runTest {
        val income = Income(2L, null, TransactionType.EXPENSE, 50.0, 3L, 2000L)
        page.stubDeleteIncome(income)

        page.viewModel.deleteInsight(income)
        advanceUntilIdle()

        page.verifyDeleteIncomeCalled(income)
    }

    @Test
    fun `getIncomesByCategoryId returns LiveData from repository`() {
        val incomes = listOf(Income(1L, null, TransactionType.INCOME, 100.0, 7L, 100L))
        page.stubGetAllByCategoryId(7L, *incomes.toTypedArray())

        val result = page.viewModel.getIncomesByCategoryId(7L)

        assertEquals(incomes, result.value)
        page.verifyGetAllByCategoryIdCalled(7L)
    }

    @Test
    fun `getIncomesByCategoryId returns empty list for empty category`() {
        page.stubGetAllByCategoryId(999L)

        val result = page.viewModel.getIncomesByCategoryId(999L)

        assertTrue(result.value!!.isEmpty())
    }

    @Test
    fun `splitIncomeAndExpenses separates income from expense`() {
        val list = listOf(
            Income(1L, null, TransactionType.INCOME, 100.0, 1L, 3L),
            Income(2L, null, TransactionType.EXPENSE, 50.0, 1L, 1L),
            Income(3L, null, TransactionType.INCOME, 200.0, 1L, 2L)
        )

        page.viewModel.splitIncomeAndExpenses(list)

        val result = page.viewModel.insightUiState.value.chartData
        assertEquals(3, result.size)
        assertTrue(result.all { it.type in listOf(TransactionType.INCOME, TransactionType.EXPENSE) })
    }

    @Test
    fun `splitIncomeAndExpenses limits to 15 per type`() {
        val incomes = (1..20).map { Income(it.toLong(), null, TransactionType.INCOME, it * 10.0, 1L, it.toLong()) }
        val expenses = (1..20).map { Income((it + 100).toLong(), null, TransactionType.EXPENSE, it * 5.0, 1L, it.toLong()) }
        val list = incomes + expenses

        page.viewModel.splitIncomeAndExpenses(list)

        val result = page.viewModel.insightUiState.value.chartData
        assertEquals(15, result.count { it.type == TransactionType.INCOME })
        assertEquals(15, result.count { it.type == TransactionType.EXPENSE })
        assertEquals(30, result.size)
    }

    @Test
    fun `splitIncomeAndExpenses sorts by timestamp ascending`() {
        val list = listOf(
            Income(1L, null, TransactionType.INCOME, 100.0, 1L, 300L),
            Income(2L, null, TransactionType.INCOME, 200.0, 1L, 100L),
            Income(3L, null, TransactionType.INCOME, 300.0, 1L, 200L)
        )

        page.viewModel.splitIncomeAndExpenses(list)

        val result = page.viewModel.insightUiState.value.chartData
        val timestamps = result.map { it.timestamp }
        assertEquals(listOf(100L, 200L, 300L), timestamps)
    }

    @Test
    fun `splitIncomeAndExpenses handles empty list`() {
        page.viewModel.splitIncomeAndExpenses(emptyList())
        val state = page.viewModel.insightUiState.value
        assertTrue(state.chartData.isEmpty())
        assertTrue(state.isEmpty)
    }
}

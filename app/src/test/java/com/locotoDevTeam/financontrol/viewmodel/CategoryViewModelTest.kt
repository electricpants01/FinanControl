package com.locotoDevTeam.financontrol.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.locotoDevTeam.financontrol.page.CategoryViewModelPage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runners.JUnit4

/**
 * Unit tests for CategoryViewModel.
 * Uses Page Object pattern: CategoryViewModelPage encapsulates mock setup and assertions.
 * The mock repository is injected via the ViewModel's constructor; setTestDispatcher() controls coroutines.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class CategoryViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val page = CategoryViewModelPage()
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

    // ── insertNewCategory ──

    @Test
    fun `insertNewCategory delegates to repository`() = runTest {
        page.stubInsertCategory()

        page.viewModel.insertNewCategory("Salary")
        testDispatcher.scheduler.advanceUntilIdle()

        page.verifyInsertCategoryCalled("Salary")
    }

    @Test
    fun `insertNewCategory with empty name still delegates`() = runTest {
        page.stubInsertCategory()

        page.viewModel.insertNewCategory("")
        testDispatcher.scheduler.advanceUntilIdle()

        page.verifyInsertCategoryCalled("")
    }

    // ── insertNewIncomeExpense ──

    @Test
    fun `insertNewIncomeExpense delegates Income type`() = runTest {
        page.stubInsertIncome()

        page.viewModel.insertNewIncomeExpense(1L, 200.0, "Income")
        testDispatcher.scheduler.advanceUntilIdle()

        page.verifyInsertIncomeCalled("Income", 200.0, 1L)
    }

    @Test
    fun `insertNewIncomeExpense delegates Expense type`() = runTest {
        page.stubInsertIncome()

        page.viewModel.insertNewIncomeExpense(3L, 75.0, "Expense")
        testDispatcher.scheduler.advanceUntilIdle()

        page.verifyInsertIncomeCalled("Expense", 75.0, 3L)
    }

    // ── deleteACategoryById ──

    @Test
    fun `deleteACategoryById delegates to repository`() = runTest {
        page.stubDeleteCategoryById()

        page.viewModel.deleteACategoryById(10L)
        testDispatcher.scheduler.advanceUntilIdle()

        page.verifyDeleteCategoryByIdCalled(10L)
    }

    // ── refreshOverview ──

    @Test
    fun `refreshOverview updates overview LiveData with income and expense`() = runTest {
        page.stubGetSumIncome(1000.0)
        page.stubGetSumExpense(400.0)

        page.viewModel.refreshOverview()
        testDispatcher.scheduler.advanceUntilIdle()

        page.verifyGetSumIncomeCalled()
        page.verifyGetSumExpenseCalled()
        val overview = page.viewModel.overview.getOrAwaitValue()
        assertEquals(1000.0, overview.first, 0.0)
        assertEquals(400.0, overview.second, 0.0)
        assertEquals(600.0, overview.third, 0.0)
    }

    @Test
    fun `refreshOverview with zero values works correctly`() = runTest {
        page.stubGetSumIncome(0.0)
        page.stubGetSumExpense(0.0)

        page.viewModel.refreshOverview()
        testDispatcher.scheduler.advanceUntilIdle()

        val overview = page.viewModel.overview.getOrAwaitValue()
        assertEquals(0.0, overview.first, 0.0)
        assertEquals(0.0, overview.second, 0.0)
        assertEquals(0.0, overview.third, 0.0)
    }

    // ── categories LiveData ──

    @Test
    fun `categories returns LiveData from repository when initialized`() {
        val categories = page.viewModel.categories
        assertNotNull(categories)
    }
}

fun <T> androidx.lifecycle.LiveData<T>.getOrAwaitValue(): T {
    var value: T? = null
    val latch = java.util.concurrent.CountDownLatch(1)
    val observer = object : androidx.lifecycle.Observer<T> {
        override fun onChanged(v: T) {
            value = v
            latch.countDown()
        }
    }
    observeForever(observer)
    latch.await(2, java.util.concurrent.TimeUnit.SECONDS)
    removeObserver(observer)
    return value!!
}

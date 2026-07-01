package com.locotoDevTeam.financontrol.repository

import com.locotoDevTeam.financontrol.database.entity.Income
import com.locotoDevTeam.financontrol.page.InsightRepositoryPage
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for InsightRepository.
 * Uses Page Object pattern: InsightRepositoryPage encapsulates mock setup and assertions.
 */
class InsightRepositoryTest {

    private val page = InsightRepositoryPage()

    @Before
    fun setUp() {
        // Page object already initializes mocks and repository
    }

    // ── getAllByCategoryId ──

    @Test
    fun `getAllByCategoryId returns LiveData from DAO for specific category`() {
        val incomes = listOf(
            Income(1L, null, "Income", 100.0, 5L, "1000"),
            Income(2L, "Desc", "Expense", 50.0, 5L, "2000")
        )
        page.stubGetAllByCategoryId(5L, *incomes.toTypedArray())

        val result = page.repository.getAllByCategoryId(5L)

        assertEquals(incomes, result.value)
        page.verifyGetAllByCategoryIdCalled(5L)
    }

    @Test
    fun `getAllByCategoryId returns empty list when no entries`() {
        page.stubGetAllByCategoryId(99L)

        val result = page.repository.getAllByCategoryId(99L)

        assertTrue(result.value!!.isEmpty())
    }

    @Test
    fun `getAllByCategoryId isolates results to category`() {
        val incomesCat1 = listOf(Income(1L, null, "Income", 100.0, 1L, "100"))
        val incomesCat2 = listOf(Income(2L, null, "Expense", 200.0, 2L, "200"))
        page.stubGetAllByCategoryId(1L, *incomesCat1.toTypedArray())
        page.stubGetAllByCategoryId(2L, *incomesCat2.toTypedArray())

        val result1 = page.repository.getAllByCategoryId(1L)
        val result2 = page.repository.getAllByCategoryId(2L)

        assertEquals(incomesCat1, result1.value)
        assertEquals(incomesCat2, result2.value)
    }

    // ── deleteIncome ──

    @Test
    fun `deleteIncome delegates to DAO`() = runTest {
        val income = Income(1L, null, "Income", 50.0, 1L, "123")

        page.repository.deleteIncome(income)

        page.verifyDeleteIncomeCalled(income)
    }

    @Test
    fun `deleteIncome with null uid still delegates`() = runTest {
        val income = Income(null, "desc", "Expense", 25.0, 3L, "456")

        page.repository.deleteIncome(income)

        page.verifyDeleteIncomeCalled(income)
    }
}

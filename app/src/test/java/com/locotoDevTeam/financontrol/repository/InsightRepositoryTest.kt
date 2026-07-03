package com.locotoDevTeam.financontrol.repository

import com.locotoDevTeam.financontrol.database.entity.Income
import com.locotoDevTeam.financontrol.database.entity.TransactionType
import com.locotoDevTeam.financontrol.page.InsightRepositoryPage
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class InsightRepositoryTest {

    private val page = InsightRepositoryPage()

    @Before
    fun setUp() {
        // Page object already initializes mocks and repository
    }

    // ── getAllByCategoryId ──

    @Test
    fun `getAllByCategoryId returns Flow from DAO for specific category`() = runTest {
        val incomes = listOf(
            Income(1L, null, TransactionType.INCOME, 100.0, 5L, 1000L),
            Income(2L, "Desc", TransactionType.EXPENSE, 50.0, 5L, 2000L)
        )
        page.stubGetAllByCategoryId(5L, *incomes.toTypedArray())

        val result = page.repository.getAllByCategoryId(5L).first()

        assertEquals(incomes, result)
        page.verifyGetAllByCategoryIdCalled(5L)
    }

    @Test
    fun `getAllByCategoryId returns empty list when no entries`() = runTest {
        page.stubGetAllByCategoryId(99L)

        val result = page.repository.getAllByCategoryId(99L).first()

        assertTrue(result.isEmpty())
    }

    @Test
    fun `getAllByCategoryId isolates results to category`() = runTest {
        val incomesCat1 = listOf(Income(1L, null, TransactionType.INCOME, 100.0, 1L, 100L))
        val incomesCat2 = listOf(Income(2L, null, TransactionType.EXPENSE, 200.0, 2L, 200L))
        page.stubGetAllByCategoryId(1L, *incomesCat1.toTypedArray())
        page.stubGetAllByCategoryId(2L, *incomesCat2.toTypedArray())

        val result1 = page.repository.getAllByCategoryId(1L).first()
        val result2 = page.repository.getAllByCategoryId(2L).first()

        assertEquals(incomesCat1, result1)
        assertEquals(incomesCat2, result2)
    }

    // ── deleteIncome ──

    @Test
    fun `deleteIncome delegates to DAO`() = runTest {
        val income = Income(1L, null, TransactionType.INCOME, 50.0, 1L, 123L)

        page.repository.deleteIncome(income)

        page.verifyDeleteIncomeCalled(income)
    }

    @Test
    fun `deleteIncome with null uid still delegates`() = runTest {
        val income = Income(null, "desc", TransactionType.EXPENSE, 25.0, 3L, 456L)

        page.repository.deleteIncome(income)

        page.verifyDeleteIncomeCalled(income)
    }
}

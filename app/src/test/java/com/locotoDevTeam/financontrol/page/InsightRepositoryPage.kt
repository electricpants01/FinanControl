package com.locotoDevTeam.financontrol.page

import com.locotoDevTeam.financontrol.database.dao.IncomeDao
import com.locotoDevTeam.financontrol.database.entity.Income
import com.locotoDevTeam.financontrol.ui.insight.InsightRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf

class InsightRepositoryPage {

    val incomeDao: IncomeDao = mockk(relaxed = true)
    val repository = InsightRepository(incomeDao)

    // ── Stub helpers ──

    fun stubGetAllByCategoryId(categoryId: Long, vararg incomes: Income) {
        every { incomeDao.getAllByCategoryId(categoryId) } returns flowOf(incomes.toList())
    }

    // ── Assertion helpers ──

    fun verifyGetAllByCategoryIdCalled(categoryId: Long) {
        verify { incomeDao.getAllByCategoryId(categoryId) }
    }

    fun verifyDeleteIncomeCalled(income: Income) {
        verify { incomeDao.delete(income) }
    }

    fun verifyNoDeleteIncomeCalled() {
        verify(exactly = 0) { incomeDao.delete(any()) }
    }
}

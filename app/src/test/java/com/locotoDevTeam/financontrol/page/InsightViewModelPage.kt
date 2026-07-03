package com.locotoDevTeam.financontrol.page

import com.locotoDevTeam.financontrol.database.dao.IncomeDao
import com.locotoDevTeam.financontrol.database.entity.Income
import com.locotoDevTeam.financontrol.ui.insight.InsightRepository
import com.locotoDevTeam.financontrol.ui.insight.InsightViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf

class InsightViewModelPage {

    val incomeDao: IncomeDao = mockk(relaxed = true)
    val repository: InsightRepository = mockk(relaxed = true)
    val viewModel = InsightViewModel(repository)

    // ── Stub helpers ──

    fun stubGetAllByCategoryId(categoryId: Long, vararg incomes: Income) {
        every { repository.getAllByCategoryId(categoryId) } returns flowOf(incomes.toList())
    }

    fun stubDeleteIncome(income: Income) {
        coEvery { repository.deleteIncome(income) } returns Unit
    }

    // ── Assertion helpers ──

    fun verifyDeleteIncomeCalled(income: Income) {
        coVerify { repository.deleteIncome(income) }
    }
}

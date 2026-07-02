package com.locotoDevTeam.financontrol.page

import com.locotoDevTeam.financontrol.database.FinancialDB
import com.locotoDevTeam.financontrol.database.dao.CategoryDao
import com.locotoDevTeam.financontrol.database.dao.IncomeDao
import com.locotoDevTeam.financontrol.di.DatabaseModule
import com.locotoDevTeam.financontrol.di.RepositoryModule
import com.locotoDevTeam.financontrol.ui.category.CategoryRepository
import com.locotoDevTeam.financontrol.ui.insight.InsightRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

/**
 * Page Object for the Hilt DI modules.
 *
 * The modules are plain `object`s whose `@Provides` functions are pure wiring, so they
 * can be exercised directly in a JVM unit test without the Hilt runtime. This encapsulates
 * the mock DAOs / database and the assertion helpers.
 */
class DiModulePage {

    val database: FinancialDB = mockk(relaxed = true)
    val categoryDao: CategoryDao = mockk(relaxed = true)
    val incomeDao: IncomeDao = mockk(relaxed = true)

    init {
        every { database.categoryDao() } returns categoryDao
        every { database.incomeDao() } returns incomeDao
    }

    // ── DatabaseModule ──

    fun provideCategoryDao(): CategoryDao = DatabaseModule.provideCategoryDao(database)

    fun provideIncomeDao(): IncomeDao = DatabaseModule.provideIncomeDao(database)

    fun verifyCategoryDaoSourcedFromDatabase() {
        verify { database.categoryDao() }
    }

    fun verifyIncomeDaoSourcedFromDatabase() {
        verify { database.incomeDao() }
    }

    // ── RepositoryModule ──

    fun provideCategoryRepository(): CategoryRepository =
        RepositoryModule.provideCategoryRepository(categoryDao, incomeDao)

    fun provideInsightRepository(): InsightRepository =
        RepositoryModule.provideInsightRepository(incomeDao)
}

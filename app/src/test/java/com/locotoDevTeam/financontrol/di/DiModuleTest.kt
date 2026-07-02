package com.locotoDevTeam.financontrol.di

import com.locotoDevTeam.financontrol.page.DiModulePage
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertSame
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for the Hilt DI modules ([DatabaseModule], [RepositoryModule]).
 *
 * Verifies that the `@Provides` functions correctly wire dependencies:
 * DAOs are sourced from the database, and repositories receive their DAOs.
 * Uses the Page Object pattern with MockK, per project testing conventions.
 */
class DiModuleTest {

    private lateinit var page: DiModulePage

    @Before
    fun setUp() {
        page = DiModulePage()
    }

    // ── DatabaseModule ──

    @Test
    fun `provideCategoryDao returns the dao from the database`() {
        val dao = page.provideCategoryDao()

        assertSame(page.categoryDao, dao)
        page.verifyCategoryDaoSourcedFromDatabase()
    }

    @Test
    fun `provideIncomeDao returns the dao from the database`() {
        val dao = page.provideIncomeDao()

        assertSame(page.incomeDao, dao)
        page.verifyIncomeDaoSourcedFromDatabase()
    }

    // ── RepositoryModule ──

    @Test
    fun `provideCategoryRepository builds a repository`() {
        val repository = page.provideCategoryRepository()

        assertNotNull(repository)
    }

    @Test
    fun `provideInsightRepository builds a repository`() {
        val repository = page.provideInsightRepository()

        assertNotNull(repository)
    }
}

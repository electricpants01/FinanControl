package com.locotoDevTeam.financontrol.di

import com.locotoDevTeam.financontrol.database.dao.CategoryDao
import com.locotoDevTeam.financontrol.database.dao.IncomeDao
import com.locotoDevTeam.financontrol.ui.category.CategoryRepository
import com.locotoDevTeam.financontrol.ui.insight.InsightRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module that provides repository instances.
 *
 * Repositories are the single entry point for data access (Fragment/ViewModel ->
 * Repository -> DAO -> Room). They are provided as `@Singleton` and receive their
 * DAOs from [DatabaseModule], enabling constructor injection and easy mocking in tests.
 */
@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideCategoryRepository(
        categoryDao: CategoryDao,
        incomeDao: IncomeDao
    ): CategoryRepository = CategoryRepository(categoryDao, incomeDao)

    @Provides
    @Singleton
    fun provideInsightRepository(
        incomeDao: IncomeDao
    ): InsightRepository = InsightRepository(incomeDao)
}

package com.locotoDevTeam.financontrol.di

import android.content.Context
import androidx.room.Room
import com.locotoDevTeam.financontrol.database.FinancialDB
import com.locotoDevTeam.financontrol.database.dao.CategoryDao
import com.locotoDevTeam.financontrol.database.dao.IncomeDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module that provides the Room database and its DAOs.
 *
 * Replaces the manual `FinancialDB.getAppDataBase(context)` singleton: the database
 * is now built once and shared application-wide via `@Singleton`, and DAOs are
 * exposed as injectable dependencies for repositories.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideFinancialDB(@ApplicationContext context: Context): FinancialDB =
        Room.databaseBuilder(context, FinancialDB::class.java, "financial-db").build()

    @Provides
    fun provideCategoryDao(database: FinancialDB): CategoryDao = database.categoryDao()

    @Provides
    fun provideIncomeDao(database: FinancialDB): IncomeDao = database.incomeDao()
}

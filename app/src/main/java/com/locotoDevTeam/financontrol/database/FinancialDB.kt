package com.locotoDevTeam.financontrol.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.locotoDevTeam.financontrol.database.dao.CategoryDao
import com.locotoDevTeam.financontrol.database.dao.IncomeDao
import com.locotoDevTeam.financontrol.database.entity.Category
import com.locotoDevTeam.financontrol.database.entity.Income

@Database(entities = arrayOf(Category::class, Income::class), version = 1)
abstract class FinancialDB: RoomDatabase() {

    abstract fun categoryDao(): CategoryDao
    abstract fun incomeDao(): IncomeDao


    companion object {
        var INSTANCE: FinancialDB? = null

        /**
         * Manual singleton accessor. Deprecated in favor of Hilt injection —
         * inject [FinancialDB] (or a DAO/Repository) instead of calling this.
         * Provided via `DatabaseModule.provideFinancialDB`.
         */
        @Deprecated(
            message = "Use Hilt injection (DatabaseModule) instead of the manual singleton.",
            replaceWith = ReplaceWith("@Inject FinancialDB")
        )
        fun getAppDataBase(context: Context): FinancialDB? {
            if (INSTANCE == null){
                synchronized(FinancialDB::class){
                    INSTANCE = Room.databaseBuilder(context.applicationContext, FinancialDB::class.java, "financial-db").build()
                }
            }
            return INSTANCE
        }

        fun destroyDataBase(){
            INSTANCE = null
        }
    }
}
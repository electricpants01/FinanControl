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
package com.locotoDevTeam.financontrol.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.locotoDevTeam.financontrol.database.converter.Converters
import com.locotoDevTeam.financontrol.database.dao.CategoryDao
import com.locotoDevTeam.financontrol.database.dao.IncomeDao
import com.locotoDevTeam.financontrol.database.entity.Category
import com.locotoDevTeam.financontrol.database.entity.Income

@Database(entities = arrayOf(Category::class, Income::class), version = 2)
@TypeConverters(Converters::class)
abstract class FinancialDB: RoomDatabase() {

    abstract fun categoryDao(): CategoryDao
    abstract fun incomeDao(): IncomeDao


    companion object {
        var INSTANCE: FinancialDB? = null

        /**
         * Migration from version 1 to 2: no data migration needed — TransactionType
         * enum uses the same stored String values ("Income"/"Expense") as the
         * previous raw String field, so existing data is compatible.
         */
        internal val MIGRATION_1_2 = object : androidx.room.migration.Migration(1, 2) {
            override fun migrate(database: androidx.sqlite.db.SupportSQLiteDatabase) {
                // No destructive changes; stored values remain "Income" / "Expense"
            }
        }

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
                    INSTANCE = Room.databaseBuilder(context.applicationContext, FinancialDB::class.java, "financial-db")
                        .addMigrations(MIGRATION_1_2)
                        .build()
                }
            }
            return INSTANCE
        }

        fun destroyDataBase(){
            INSTANCE = null
        }
    }
}
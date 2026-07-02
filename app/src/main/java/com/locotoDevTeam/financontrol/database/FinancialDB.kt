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

@Database(entities = arrayOf(Category::class, Income::class), version = 3)
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
         * Migration from version 2 to 3: converts the timestamp column from String
         * (stored as epoch millis text) to INTEGER (epoch millis as Long).
         * Uses the recreate strategy: create a new table with the correct schema,
         * copy data with CAST conversion, drop the old table, and rename.
         */
        internal val MIGRATION_2_3 = object : androidx.room.migration.Migration(2, 3) {
            override fun migrate(database: androidx.sqlite.db.SupportSQLiteDatabase) {
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS income_new (
                        uid INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        description TEXT,
                        type TEXT NOT NULL,
                        amount REAL NOT NULL,
                        categoryId INTEGER NOT NULL,
                        timestamp INTEGER NOT NULL DEFAULT 0
                    )
                """.trimIndent())
                database.execSQL("""
                    INSERT INTO income_new (uid, description, type, amount, categoryId, timestamp)
                    SELECT uid, description, type, amount, categoryId,
                           CASE WHEN timestamp IS NOT NULL AND timestamp != ''
                                THEN CAST(timestamp AS INTEGER)
                                ELSE 0
                           END
                    FROM income
                """.trimIndent())
                database.execSQL("DROP TABLE income")
                database.execSQL("ALTER TABLE income_new RENAME TO income")
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
                        .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
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
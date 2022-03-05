package com.locotoDevTeam.financontrol.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.locotoDevTeam.financontrol.database.entity.Income

@Dao
interface IncomeDao {

    @Insert
    fun insert(income: Income)

    @Query("select * from income")
    fun getAll(): LiveData<List<Income>>

    @Query("select * from income where categoryId = :categoryId")
    fun getAllByCategoryId(categoryId: Long): LiveData<List<Income>>

    @Delete
    fun delete(income: Income)

    @Query("select sum(amount) as incomeAmount from income where type = 'Income'")
    fun getSumIncome(): Double

    @Query("select sum(amount) as expenseAmount from income where type = 'Expense'")
    fun getSumExpense(): Double

}
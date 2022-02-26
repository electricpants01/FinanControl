package com.locotoDevTeam.financontrol.ui.category

import android.content.Context
import androidx.lifecycle.ViewModel
import com.locotoDevTeam.financontrol.database.FinancialDB
import com.locotoDevTeam.financontrol.database.entity.Category
import com.locotoDevTeam.financontrol.database.entity.Income
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class CategoryViewModel: ViewModel() {

    val coroutine = Dispatchers.IO

    fun insertNewCategory(categoryName: String, context: Context){
        CoroutineScope(coroutine).launch {
            FinancialDB.getAppDataBase(context)?.categoryDao()?.insert(Category(name = categoryName))
        }
    }

    fun insertNewIncomeExpense(categoryId: Long, amount: Double, type: String, context: Context){
        val date = Date()
        CoroutineScope(coroutine).launch {
            FinancialDB.getAppDataBase(context)?.incomeDao()?.insert(Income(type = type,amount = amount,categoryId = categoryId, timestamp = date.time.toString()))
        }
    }

}
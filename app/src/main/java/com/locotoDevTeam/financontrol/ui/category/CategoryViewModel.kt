package com.locotoDevTeam.financontrol.ui.category

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.locotoDevTeam.financontrol.database.FinancialDB
import com.locotoDevTeam.financontrol.database.entity.Category
import com.locotoDevTeam.financontrol.database.entity.Income
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class CategoryViewModel: ViewModel() {

    val dispatcher = Dispatchers.IO

    fun insertNewCategory(categoryName: String, context: Context){
        viewModelScope.launch(dispatcher) {
            FinancialDB.getAppDataBase(context)?.categoryDao()?.insert(Category(name = categoryName))
        }
    }

    fun deleteACategoryById(categoryId: Long, context: Context){
        viewModelScope.launch(dispatcher) {
            FinancialDB.getAppDataBase(context)?.categoryDao()?.deleteIncomesFromACategory(categoryId)
            FinancialDB.getAppDataBase(context)?.categoryDao()?.deleteCategoryById(categoryId)
        }
    }

}
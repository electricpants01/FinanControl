package com.locotoDevTeam.financontrol.ui.category

import android.content.Context
import androidx.lifecycle.ViewModel
import com.locotoDevTeam.financontrol.database.FinancialDB
import com.locotoDevTeam.financontrol.database.entity.Category
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CategoryViewModel: ViewModel() {


    fun insertNewCategory(categoryName: String, context: Context){
        CoroutineScope(Dispatchers.IO).launch {
            FinancialDB.getAppDataBase(context)?.categoryDao()?.insert(Category(name = categoryName))
        }
    }

}
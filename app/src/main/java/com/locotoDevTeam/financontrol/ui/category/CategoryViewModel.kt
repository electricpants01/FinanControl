package com.locotoDevTeam.financontrol.ui.category

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.locotoDevTeam.financontrol.database.FinancialDB
import com.locotoDevTeam.financontrol.database.entity.Category
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CategoryViewModel: ViewModel() {

    val dispatcher = Dispatchers.IO
    val categories = MutableLiveData<List<Category>>(emptyList())

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

    fun setCategories(myCategories: List<Category>){
        categories.value = myCategories
    }

    fun getCategory(index: Int): Category{
        return categories.value?.get(index)!!
    }

}
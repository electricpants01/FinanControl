package com.locotoDevTeam.financontrol.ui.insight

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.locotoDevTeam.financontrol.database.FinancialDB
import com.locotoDevTeam.financontrol.database.entity.Income
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class InsightViewModel: ViewModel() {

    val dispatcher = Dispatchers.IO
    var categoryId = MutableLiveData<Long>()

    // first, we need to set up the id the of category
    fun setCategoryId(newCategoryId: Long){
        this.categoryId.value = newCategoryId
    }

    // delete an insight
    fun deleteInsight(income: Income, context: Context){
        viewModelScope.launch(dispatcher) {
            FinancialDB.getAppDataBase(context)?.incomeDao()?.delete(income)
        }
    }

}
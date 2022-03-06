package com.locotoDevTeam.financontrol.ui.insight

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.locotoDevTeam.financontrol.database.FinancialDB
import com.locotoDevTeam.financontrol.database.entity.Income
import com.locotoDevTeam.financontrol.util.toDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class InsightViewModel: ViewModel() {

    val dispatcher = Dispatchers.IO
    var categoryId = MutableLiveData<Long>()
    val incomeExpenseGraphList = MutableLiveData<List<Income>>()

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

    // first we split into to arrays, oen of income and other of expenses
    // secondly we get the last 15 item from each ones
    // thirdly we sort by date
    fun splitIncomeAndExpenses(incomeList: List<Income>) {
        var incomes = incomeList.filter { it.type == "Income" }
        var expenses = incomeList.filter { it.type == "Expense" }
        incomes = incomes.sortedByDescending { it.timestamp }
        expenses = expenses.sortedByDescending { it.timestamp }
        var finalIncomes = mutableListOf<Income>()
        var finalExpenses = mutableListOf<Income>()
        for (i in 0..14) {
            if( i < incomes.size ){
                finalIncomes.add(incomes[i])
            }
            if( i < expenses.size) {
                finalExpenses.add(expenses[i])
            }
        }
        var finalIncomeExpenseList = finalIncomes + finalExpenses
        finalIncomeExpenseList = finalIncomeExpenseList.sortedBy { it.timestamp }
        incomeExpenseGraphList.value = finalIncomeExpenseList
    }
}
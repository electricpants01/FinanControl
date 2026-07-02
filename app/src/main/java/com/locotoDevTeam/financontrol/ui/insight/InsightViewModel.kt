package com.locotoDevTeam.financontrol.ui.insight

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.locotoDevTeam.financontrol.database.entity.Income
import com.locotoDevTeam.financontrol.database.entity.TransactionType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InsightViewModel @Inject constructor(
    private val insightRepository: InsightRepository
) : ViewModel() {

    private var dispatcher: CoroutineDispatcher = Dispatchers.IO
    var categoryId = MutableLiveData<Long>()
    val incomeExpenseGraphList = MutableLiveData<List<Income>>()

    /**
     * Set test dispatcher for unit testing to avoid flaky async behavior.
     */
    @VisibleForTesting
    internal fun setTestDispatcher(dispatcher: CoroutineDispatcher) {
        this.dispatcher = dispatcher
    }

    /**
     * Returns LiveData of income/expense entries for the given category,
     * sourced from the InsightRepository (not directly from DAO).
     */
    fun getIncomesByCategoryId(categoryId: Long): LiveData<List<Income>> {
        return insightRepository.getAllByCategoryId(categoryId)
    }

    // first, we need to set up the id the of category
    fun setCategoryId(newCategoryId: Long) {
        this.categoryId.value = newCategoryId
    }

    // delete an insight
    fun deleteInsight(income: Income) {
        viewModelScope.launch(dispatcher) {
            insightRepository.deleteIncome(income)
        }
    }

    // first we split into to arrays, oen of income and other of expenses
    // secondly we get the last 15 item from each ones
    // thirdly we sort by date
    fun splitIncomeAndExpenses(incomeList: List<Income>) {
        var incomes = incomeList.filter { it.type == TransactionType.INCOME }
        var expenses = incomeList.filter { it.type == TransactionType.EXPENSE }
        incomes = incomes.sortedByDescending { it.timestamp }
        expenses = expenses.sortedByDescending { it.timestamp }
        var finalIncomes = mutableListOf<Income>()
        var finalExpenses = mutableListOf<Income>()
        for (i in 0..14) {
            if (i < incomes.size) {
                finalIncomes.add(incomes[i])
            }
            if (i < expenses.size) {
                finalExpenses.add(expenses[i])
            }
        }
        var finalIncomeExpenseList = finalIncomes + finalExpenses
        finalIncomeExpenseList = finalIncomeExpenseList.sortedBy { it.timestamp }
        incomeExpenseGraphList.value = finalIncomeExpenseList
    }
}

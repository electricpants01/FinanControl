package com.locotoDevTeam.financontrol.ui.insight

import android.content.Context
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.locotoDevTeam.financontrol.database.FinancialDB
import com.locotoDevTeam.financontrol.database.entity.Income
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class InsightViewModel: ViewModel() {

    private var dispatcher: CoroutineDispatcher = Dispatchers.IO
    var categoryId = MutableLiveData<Long>()
    val incomeExpenseGraphList = MutableLiveData<List<Income>>()

    private var insightRepository: InsightRepository? = null

    private fun getRepository(context: Context): InsightRepository {
        if (insightRepository == null) {
            val db = FinancialDB.getAppDataBase(context)!!
            insightRepository = InsightRepository(db.incomeDao())
        }
        return insightRepository!!
    }

    /**
     * Set mock repository for unit testing. Bypasses FinancialDB initialization.
     */
    @VisibleForTesting
    internal fun setTestRepository(repository: InsightRepository) {
        this.insightRepository = repository
    }

    /**
     * Set test dispatcher for unit testing to avoid flaky async behavior.
     */
    @VisibleForTesting
    internal fun setTestDispatcher(dispatcher: CoroutineDispatcher) {
        this.dispatcher = dispatcher
    }

    /**
     * Initialize the repository from context. Call once from Fragment.
     * After this, LiveData methods become available for observation.
     */
    fun initRepository(context: Context) {
        getRepository(context)
    }

    /**
     * Returns LiveData of income/expense entries for the given category,
     * sourced from the InsightRepository (not directly from DAO).
     */
    fun getIncomesByCategoryId(categoryId: Long, context: Context): LiveData<List<Income>> {
        val repo = getRepository(context)
        return repo.getAllByCategoryId(categoryId)
    }

    // first, we need to set up the id the of category
    fun setCategoryId(newCategoryId: Long) {
        this.categoryId.value = newCategoryId
    }

    // delete an insight
    fun deleteInsight(income: Income, context: Context) {
        val repo = getRepository(context)
        viewModelScope.launch(dispatcher) {
            repo.deleteIncome(income)
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

package com.locotoDevTeam.financontrol.ui.insight

import androidx.lifecycle.LiveData
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.locotoDevTeam.financontrol.database.entity.Income
import com.locotoDevTeam.financontrol.database.entity.TransactionType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UiState for the Insight (per-category chart) screen.
 * Contains the processed chart-ready items, the raw income list,
 * and a flag for the empty state.
 */
data class InsightUiState(
    val items: List<Income> = emptyList(),
    val chartData: List<Income> = emptyList(),
    val isEmpty: Boolean = true
)

@HiltViewModel
class InsightViewModel @Inject constructor(
    private val insightRepository: InsightRepository
) : ViewModel() {

    var categoryId = MutableLiveData<Long>()

    private val _insightUiState = MutableStateFlow(InsightUiState())
    val insightUiState: StateFlow<InsightUiState> = _insightUiState.asStateFlow()

    /**
     * Returns a Flow of income/expense entries for the given category,
     * sourced from the InsightRepository (not directly from DAO).
     */
    fun getIncomesByCategoryId(categoryId: Long): Flow<List<Income>> {
        return insightRepository.getAllByCategoryId(categoryId)
    }

    fun setCategoryId(newCategoryId: Long) {
        this.categoryId.value = newCategoryId
    }

    fun deleteInsight(income: Income) {
        viewModelScope.launch {
            insightRepository.deleteIncome(income)
        }
    }

    /**
     * Splits the raw income list into income/expense groups, takes the last 15
     * of each, sorts by timestamp, and publishes the result via [insightUiState].
     */
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
        _insightUiState.value = InsightUiState(
            items = incomeList,
            chartData = finalIncomeExpenseList,
            isEmpty = incomeList.isEmpty()
        )
    }
}

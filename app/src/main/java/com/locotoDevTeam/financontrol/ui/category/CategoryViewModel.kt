package com.locotoDevTeam.financontrol.ui.category

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.locotoDevTeam.financontrol.database.entity.Category
import com.locotoDevTeam.financontrol.database.entity.Income
import com.locotoDevTeam.financontrol.database.entity.TransactionType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CategoriesUiState(
    val isEmpty: Boolean = true,
    val totalBalance: Double = 0.0,
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val categories: List<Category> = emptyList()
)

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private var dispatcher: CoroutineDispatcher = Dispatchers.IO

    private val _categoriesUiState = MutableStateFlow(CategoriesUiState())
    val categoriesUiState: StateFlow<CategoriesUiState> = _categoriesUiState.asStateFlow()

    init {
        // Collect categories from the repository Flow on Main (tests override
        // Dispatchers.Main). Heavy DB work is dispatched to [dispatcher] inside
        // refreshUiState. viewModelScope cancels the collection on clear.
        viewModelScope.launch(Dispatchers.Main) {
            categoryRepository.getAllCategories().collect { categories ->
                refreshUiState(categories)
            }
        }
    }

    @VisibleForTesting
    internal fun setTestDispatcher(dispatcher: CoroutineDispatcher) {
        this.dispatcher = dispatcher
    }

    fun insertNewCategory(categoryName: String) {
        viewModelScope.launch(dispatcher) {
            categoryRepository.insertCategory(Category(name = categoryName))
        }
    }

    fun insertNewIncomeExpense(categoryId: Long, amount: Double, type: TransactionType) {
        viewModelScope.launch(dispatcher) {
            categoryRepository.insertIncome(Income(type = type, amount = amount, categoryId = categoryId, timestamp = System.currentTimeMillis()))
        }
    }

    fun deleteACategoryById(categoryId: Long) {
        viewModelScope.launch(dispatcher) {
            categoryRepository.deleteCategoryById(categoryId)
        }
    }

    private fun refreshUiState(categories: List<Category>) {
        viewModelScope.launch(dispatcher) {
            val incomeSum = categoryRepository.getSumIncome()
            val expenseSum = categoryRepository.getSumExpense()
            _categoriesUiState.value = CategoriesUiState(
                isEmpty = categories.isEmpty(),
                totalBalance = incomeSum - expenseSum,
                totalIncome = incomeSum,
                totalExpense = expenseSum,
                categories = categories
            )
        }
    }
}

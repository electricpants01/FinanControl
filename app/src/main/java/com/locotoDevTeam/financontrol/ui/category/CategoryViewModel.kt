package com.locotoDevTeam.financontrol.ui.category

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
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
import java.util.*
import javax.inject.Inject

/**
 * UiState for the Category screen — a single source of truth that the Fragment
 * renders. Replaces the previous two-stream (categories + overview) observation
 * with one state object. The ViewModel owns all decision logic; the Fragment
 * only calls render(state).
 */
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

    /** Keeps a reference to the forever-observer so we can remove it in onCleared(). */
    private var categoriesObserver: Observer<List<Category>>? = null

    init {
        // Bridge the repository's LiveData into the StateFlow.  When categories
        // change we also fetch income/expense totals so the UiState is complete.
        val observer = Observer<List<Category>> { categories ->
            refreshUiState(categories)
        }
        categoriesObserver = observer
        categoryRepository.getAllCategories().observeForever(observer)
    }

    override fun onCleared() {
        super.onCleared()
        categoriesObserver?.let {
            categoryRepository.getAllCategories().removeObserver(it)
        }
    }

    /**
     * Set test dispatcher for unit testing to avoid flaky async behavior.
     */
    @VisibleForTesting
    internal fun setTestDispatcher(dispatcher: CoroutineDispatcher) {
        this.dispatcher = dispatcher
    }

    // ── public actions (called from Fragment) ──

    fun insertNewCategory(categoryName: String) {
        viewModelScope.launch(dispatcher) {
            categoryRepository.insertCategory(Category(name = categoryName))
        }
    }

    fun insertNewIncomeExpense(categoryId: Long, amount: Double, type: TransactionType) {
        val date = Date()
        viewModelScope.launch(dispatcher) {
            categoryRepository.insertIncome(Income(type = type, amount = amount, categoryId = categoryId, timestamp = date.time.toString()))
        }
    }

    fun deleteACategoryById(categoryId: Long) {
        viewModelScope.launch(dispatcher) {
            categoryRepository.deleteCategoryById(categoryId)
        }
    }

    // ── internal helpers ──

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

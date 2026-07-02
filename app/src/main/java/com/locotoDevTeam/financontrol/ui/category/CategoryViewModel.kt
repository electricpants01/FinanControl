package com.locotoDevTeam.financontrol.ui.category

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.locotoDevTeam.financontrol.database.entity.Category
import com.locotoDevTeam.financontrol.database.entity.Income
import com.locotoDevTeam.financontrol.database.entity.TransactionType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private var dispatcher: CoroutineDispatcher = Dispatchers.IO

    /**
     * LiveData of all categories, sourced from the repository.
     * Fragments observe this instead of calling DAOs directly.
     */
    val categories: LiveData<List<Category>>
        get() = categoryRepository.getAllCategories()

    /**
     * LiveData for the financial overview totals.
     * Exposes a Triple of (incomeSum, expenseSum, balance).
     */
    private val _overview = MediatorLiveData<Triple<Double, Double, Double>>()
    val overview: LiveData<Triple<Double, Double, Double>> get() = _overview

    /**
     * Set test dispatcher for unit testing to avoid flaky async behavior.
     */
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

    /**
     * Refreshes the financial overview (income sum, expense sum, balance).
     * Called from the Fragment to update total amounts reactively.
     */
    fun refreshOverview() {
        viewModelScope.launch(dispatcher) {
            val incomeSum = categoryRepository.getSumIncome()
            val expenseSum = categoryRepository.getSumExpense()
            _overview.postValue(Triple(incomeSum, expenseSum, incomeSum - expenseSum))
        }
    }
}

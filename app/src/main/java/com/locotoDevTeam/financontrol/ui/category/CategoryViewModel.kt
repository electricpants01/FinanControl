package com.locotoDevTeam.financontrol.ui.category

import android.content.Context
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.locotoDevTeam.financontrol.database.FinancialDB
import com.locotoDevTeam.financontrol.database.entity.Category
import com.locotoDevTeam.financontrol.database.entity.Income
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class CategoryViewModel: ViewModel() {

    private var dispatcher: CoroutineDispatcher = Dispatchers.IO

    private var categoryRepository: CategoryRepository? = null

    /**
     * LiveData of all categories, sourced from the repository.
     * Fragments observe this instead of calling DAOs directly.
     */
    val categories: LiveData<List<Category>>
        get() = categoryRepository?.getAllCategories() ?: MediatorLiveData()

    /**
     * LiveData for the financial overview totals.
     * Exposes a Triple of (incomeSum, expenseSum, balance).
     */
    private val _overview = MediatorLiveData<Triple<Double, Double, Double>>()
    val overview: LiveData<Triple<Double, Double, Double>> get() = _overview

    private fun getRepository(context: Context): CategoryRepository {
        if (categoryRepository == null) {
            val db = FinancialDB.getAppDataBase(context)!!
            categoryRepository = CategoryRepository(db.categoryDao(), db.incomeDao())
        }
        return categoryRepository!!
    }

    /**
     * Set mock repository for unit testing. Bypasses FinancialDB initialization.
     */
    @VisibleForTesting
    internal fun setTestRepository(repository: CategoryRepository) {
        this.categoryRepository = repository
    }

    /**
     * Set test dispatcher for unit testing to avoid flaky async behavior.
     */
    @VisibleForTesting
    internal fun setTestDispatcher(dispatcher: CoroutineDispatcher) {
        this.dispatcher = dispatcher
    }

    /**
     * Initialize the repository from context. Call once from Fragment/Activity.
     * After this, LiveData fields become available for observation.
     */
    fun initRepository(context: Context) {
        getRepository(context)
    }

    fun insertNewCategory(categoryName: String, context: Context) {
        val repo = getRepository(context)
        viewModelScope.launch(dispatcher) {
            repo.insertCategory(Category(name = categoryName))
        }
    }

    fun insertNewIncomeExpense(categoryId: Long, amount: Double, type: String, context: Context) {
        val date = Date()
        val repo = getRepository(context)
        viewModelScope.launch(dispatcher) {
            repo.insertIncome(Income(type = type, amount = amount, categoryId = categoryId, timestamp = date.time.toString()))
        }
    }

    fun deleteACategoryById(categoryId: Long, context: Context) {
        val repo = getRepository(context)
        viewModelScope.launch(dispatcher) {
            repo.deleteCategoryById(categoryId)
        }
    }

    /**
     * Refreshes the financial overview (income sum, expense sum, balance).
     * Called from the Fragment to update total amounts reactively.
     */
    fun refreshOverview(context: Context) {
        val repo = getRepository(context)
        viewModelScope.launch(dispatcher) {
            val incomeSum = repo.getSumIncome()
            val expenseSum = repo.getSumExpense()
            _overview.postValue(Triple(incomeSum, expenseSum, incomeSum - expenseSum))
        }
    }
}

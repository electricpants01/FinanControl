package com.locotoDevTeam.financontrol.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.navigation.findNavController
import com.locotoDevTeam.financontrol.data.dialog.AddCategoryDialog
import com.locotoDevTeam.financontrol.data.dialog.AddIncomeDialog
import com.locotoDevTeam.financontrol.database.entity.TransactionType
import com.locotoDevTeam.financontrol.databinding.ActivityMainBinding
import com.locotoDevTeam.financontrol.ui.category.CategoryViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity(), AddCategoryDialog.AddCategoryListener, AddIncomeDialog.AddIncomeListener {

    lateinit var binding: ActivityMainBinding
    private val categoryViewModel: CategoryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController(binding.fragmentContainerView.id).navigateUp() || super.onSupportNavigateUp()
    }

    // added a new category
    override fun onAddCategoryTapped(categoryName: String) {
        categoryViewModel.insertNewCategory(categoryName)
    }

    // added a new income from a category
    override fun onAddIncomeTapped(categoryId: Long, amount: Double, type: TransactionType) {
        categoryViewModel.insertNewIncomeExpense(categoryId,amount,type)
    }

}
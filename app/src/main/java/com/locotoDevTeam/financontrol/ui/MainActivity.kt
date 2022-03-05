package com.locotoDevTeam.financontrol.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.locotoDevTeam.financontrol.data.dialog.AddCategoryDialog
import com.locotoDevTeam.financontrol.data.dialog.AddIncomeDialog
import com.locotoDevTeam.financontrol.databinding.ActivityMainBinding
import com.locotoDevTeam.financontrol.ui.category.CategoryViewModel


class MainActivity : AppCompatActivity(), AddCategoryDialog.AddCategoryListener, AddIncomeDialog.AddIncomeListener {

    lateinit var binding: ActivityMainBinding
    private val categoryViewModel: CategoryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    // added a new category
    override fun onAddCategoryTapped(categoryName: String) {
        categoryViewModel.insertNewCategory(categoryName,this)
    }

    // added a new income from a category
    override fun onAddIncomeTapped(categoryId: Long, amount: Double, type: String) {
        categoryViewModel.insertNewIncomeExpense(categoryId,amount,type, this)
    }

}
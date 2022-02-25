package com.locotoDevTeam.financontrol.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.locotoDevTeam.financontrol.data.dialog.AddCategoryDialog
import com.locotoDevTeam.financontrol.data.dialog.AddIncomeDialog
import com.locotoDevTeam.financontrol.database.FinancialDB
import com.locotoDevTeam.financontrol.database.entity.Category
import com.locotoDevTeam.financontrol.databinding.ActivityMainBinding
import com.locotoDevTeam.financontrol.ui.category.CategoryViewModel

class MainActivity : AppCompatActivity(), AddCategoryDialog.AddCategoryListener, AddIncomeDialog.AddIncomeListener {

    lateinit var binding: ActivityMainBinding
    private val viewmodel: CategoryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onAddCategoryTapped(categoryName: String) {
        viewmodel.insertNewCategory(categoryName,this)
    }

    override fun onAddIncomeTapped(categoryName: String) {

    }

}
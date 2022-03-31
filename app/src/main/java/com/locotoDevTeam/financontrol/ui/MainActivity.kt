package com.locotoDevTeam.financontrol.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.navigation.findNavController
import com.locotoDevTeam.financontrol.R
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.menu_dolar -> {
                SharePreference(this).saveCurrency("$")
                rebootScreen()
            }
            R.id.menu_bob -> {
                SharePreference(this).saveCurrency("BOB")
                rebootScreen()
            }
            R.id.menu_yen -> {
                SharePreference(this).saveCurrency("¥")
                rebootScreen()
            }
            R.id.menu_euro -> {
                SharePreference(this).saveCurrency("€")
                rebootScreen()
            }
            R.id.menu_themes -> {
                Toast.makeText(this,getString(R.string.still_working), Toast.LENGTH_SHORT).show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        println("chris el homeasupenabled")
        return findNavController(binding.fragmentContainerView.id).navigateUp() || super.onSupportNavigateUp()
    }

    // added a new category
    override fun onAddCategoryTapped(categoryName: String) {
        categoryViewModel.insertNewCategory(categoryName,this)
    }

    // added a new income from a category
    override fun onAddIncomeTapped(categoryId: Long, amount: Double, type: String) {
        categoryViewModel.insertNewIncomeExpense(categoryId,amount,type, this)
    }

    fun rebootScreen(){
        startActivity(Intent(this, MainActivity::class.java))
        this.finish()
    }

}
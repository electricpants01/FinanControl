package com.locotoDevTeam.financontrol.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.locotoDevTeam.financontrol.R
import com.locotoDevTeam.financontrol.data.dialog.AddCategoryDialog
import com.locotoDevTeam.financontrol.data.dialog.AddIncomeDialog
import com.locotoDevTeam.financontrol.databinding.ActivityMainBinding
import com.locotoDevTeam.financontrol.ui.category.CategoryViewModel
import com.locotoDevTeam.financontrol.ui.insight.InsightViewModel
import com.locotoDevTeam.financontrol.util.Constants


class MainActivity : AppCompatActivity(), AddCategoryDialog.AddCategoryListener,
    AddIncomeDialog.AddIncomeListener {

    private lateinit var binding: ActivityMainBinding
    private val categoryViewModel: CategoryViewModel by viewModels()
    private val insightViewModel: InsightViewModel by viewModels()
    private lateinit var navController: NavController
    private lateinit var appBarConfig: AppBarConfiguration
    private lateinit var drawerToggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        checkUpdate()
        setupDrawer()
    }

    private fun setupDrawer() {
        // Set up the FragmentContainerView
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.myFragmentContainerView) as NavHostFragment
        navController = navHostFragment.navController

        appBarConfig = AppBarConfiguration(navController.graph, binding.myDrawerLayout)
        setupActionBarWithNavController(navController, appBarConfig)

        // Set up the app bar with the drawer toggle
        drawerToggle = ActionBarDrawerToggle(this, binding.myDrawerLayout, R.string.open_drawer, R.string.close_drawer)
        binding.myDrawerLayout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()

        // Handle navigation drawer item clicks
        val navHost: NavigationView = binding.drawerNavigationView

        navHost.setupWithNavController(navController)
        navHost.setNavigationItemSelectedListener { menuItem ->
            println("chris tapped menuItem ${menuItem.itemId}")
            when (menuItem.itemId) {
                R.id.drawerMenuLogin -> {
                    navController.navigate(R.id.loginFragment)
                    true
                }
                R.id.drawerMenuLogout -> {
                    println("chris tapped logout")
                    true
                }
                else -> false
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        println("chris your item $item")
        if(drawerToggle.onOptionsItemSelected(item)) {
            return true
        }
        when (item.itemId) {
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
                Toast.makeText(this, getString(R.string.still_working), Toast.LENGTH_SHORT).show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfig) || super.onSupportNavigateUp()
    }

    // added a new category
    override fun onAddCategoryTapped(categoryName: String) {
        categoryViewModel.insertNewCategory(categoryName, this)
    }

    // added a new income from a category
    override fun onAddIncomeTapped(categoryId: Long, amount: Double, type: String) {
        insightViewModel.insertNewIncomeExpense(categoryId, amount, type, this)
    }

    private fun rebootScreen() {
        startActivity(Intent(this, MainActivity::class.java))
        this.finish()
    }

    private fun checkUpdate() {
        val appUpdateManager: AppUpdateManager? = AppUpdateManagerFactory.create(this)
        val appUpdateInfoTask = appUpdateManager?.appUpdateInfo
        appUpdateInfoTask?.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)
            ) {
                appUpdateManager.startUpdateFlowForResult(
                    appUpdateInfo,
                    AppUpdateType.FLEXIBLE,
                    this,
                    Constants.UPDATE_IN_APP
                )
            }
        }
    }
}
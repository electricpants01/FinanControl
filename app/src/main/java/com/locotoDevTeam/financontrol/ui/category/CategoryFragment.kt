package com.locotoDevTeam.financontrol.ui.category

import android.opengl.Visibility
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.locotoDevTeam.financontrol.R
import com.locotoDevTeam.financontrol.data.adapter.CategoryAdapter
import com.locotoDevTeam.financontrol.data.dialog.AddCategoryDialog
import com.locotoDevTeam.financontrol.data.dialog.MaterialAlert
import com.locotoDevTeam.financontrol.database.FinancialDB
import com.locotoDevTeam.financontrol.database.entity.Category
import com.locotoDevTeam.financontrol.databinding.FragmentCategoryBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.exp

class CategoryFragment : Fragment(), CategoryAdapter.CategoryListener{

    private val categoryViewModel: CategoryViewModel by activityViewModels()
    lateinit var binding: FragmentCategoryBinding
    lateinit var recycler: RecyclerView
    lateinit var adapter: CategoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_category, container, false)
        binding = FragmentCategoryBinding.bind(view)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecycler()
        openAddNewCategory()
        initSubscriptions()
    }

    private fun initRecycler(){
        recycler = binding.rvCategory
        adapter = CategoryAdapter(emptyList(),this)
        recycler.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recycler.adapter = adapter
    }

    private fun initSubscriptions(){

        FinancialDB.getAppDataBase(requireContext())?.categoryDao()?.getAll()?.observe(viewLifecycleOwner,{
            initExpenseIncomeOverview()
            if(it.isNotEmpty()) {
                // all welcome components
                binding.viewContainer.visibility = View.GONE
                binding.txtWelcome.visibility = View.GONE
                binding.txtWelcomeDescription.visibility = View.GONE
                binding.ivWelcomeArrow.visibility = View.GONE
                // all overview components
                binding.ivOverviewExpense.visibility = View.VISIBLE
                binding.ivOverviewIncome.visibility = View.VISIBLE
                binding.txtOverview.visibility = View.VISIBLE
                binding.txtOverviewAmount.visibility = View.VISIBLE
                binding.txtExpenseAmount.visibility = View.VISIBLE
                binding.txtIncomeAmount.visibility = View.VISIBLE
                binding.txtIncome.visibility = View.VISIBLE
                binding.txtExpense.visibility = View.VISIBLE
                // make rv visible
                binding.rvCategory.visibility = View.VISIBLE
                adapter.setCategoryList(it)
            } else {
                // all welcome components
                binding.viewContainer.visibility = View.VISIBLE
                binding.txtWelcome.visibility = View.VISIBLE
                binding.txtWelcomeDescription.visibility = View.VISIBLE
                binding.ivWelcomeArrow.visibility = View.VISIBLE
                // all overview components
                binding.ivOverviewExpense.visibility = View.GONE
                binding.ivOverviewIncome.visibility = View.GONE
                binding.txtOverview.visibility = View.GONE
                binding.txtOverviewAmount.visibility = View.GONE
                binding.txtExpenseAmount.visibility = View.GONE
                binding.txtIncomeAmount.visibility = View.GONE
                binding.txtIncome.visibility = View.GONE
                binding.txtExpense.visibility = View.GONE
                // make rv visible
                binding.rvCategory.visibility = View.GONE
            }
        })
    }

    private fun initExpenseIncomeOverview(){
        CoroutineScope(Dispatchers.IO).launch {
            val incomeSum = FinancialDB.getAppDataBase(requireContext())?.incomeDao()?.getSumIncome()
            val expenseSum = FinancialDB.getAppDataBase(requireContext())?.incomeDao()?.getSumExpense()
            withContext(Dispatchers.Main){
                binding.txtIncomeAmount.text = incomeSum.toString()
                binding.txtExpenseAmount.text = expenseSum.toString()
                incomeSum?.let { incomeSum ->
                    expenseSum?.let { expenseSum ->
                        binding.txtOverviewAmount.text = (incomeSum - expenseSum).toString()
                    }
                }
            }
        }
    }

    private fun openAddNewCategory(){
        binding.floatingActionButton.setOnClickListener {
            val myDialog = AddCategoryDialog()
            myDialog.show(parentFragmentManager, "AddCategory")
        }
    }

    override fun onCategoryTapped(categoryId: Long) {
        val directions = CategoryFragmentDirections.actionCategoryFragmentToInsightFragment(categoryId)
        findNavController().navigate(directions)
    }

    override fun onDeleteCategoryTapped(categoryId: Long) {
        MaterialAlert.showDialog(resources.getString(R.string.category_deletion_title),
                resources.getString(R.string.category_deletion_description), requireContext()){
            categoryViewModel.deleteACategoryById(categoryId, requireContext())
        }
    }

}
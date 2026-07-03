package com.locotoDevTeam.financontrol.ui.category

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.locotoDevTeam.financontrol.R
import com.locotoDevTeam.financontrol.data.adapter.CategoryAdapter
import com.locotoDevTeam.financontrol.data.dialog.AddCategoryDialog
import com.locotoDevTeam.financontrol.data.dialog.MaterialAlert
import com.locotoDevTeam.financontrol.databinding.FragmentCategoryBinding
import com.locotoDevTeam.financontrol.ui.MainActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
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
        (activity as MainActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecycler()
        openAddNewCategory()
        initSubscriptions()
        listenForDialogResult()
    }

    /**
     * Listens for the result from AddCategoryDialog via FragmentResultListener,
     * replacing the previous pattern where MainActivity implemented the dialog's
     * listener interface.
     */
    private fun listenForDialogResult() {
        setFragmentResultListener(AddCategoryDialog.REQUEST_KEY) { _, bundle ->
            val categoryName = bundle.getString(AddCategoryDialog.KEY_CATEGORY_NAME) ?: return@setFragmentResultListener
            categoryViewModel.insertNewCategory(categoryName)
        }
    }

    private fun initRecycler(){
        recycler = binding.rvCategory
        adapter = CategoryAdapter(emptyList(),this)
        recycler.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recycler.adapter = adapter
    }

    

    private fun initSubscriptions() {
        // Observe categories from the ViewModel (sourced from CategoryRepository, not directly from DAO)
        categoryViewModel.categories.observe(viewLifecycleOwner) { categories ->
            categoryViewModel.refreshOverview()
            if (categories.isNotEmpty()) {
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
                adapter.setCategoryList(categories)
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
        }

        // Observe overview (income sum, expense sum, balance) from ViewModel
        categoryViewModel.overview.observe(viewLifecycleOwner) { (incomeSum, expenseSum, balance) ->
            binding.txtIncomeAmount.text = incomeSum.toString()
            binding.txtExpenseAmount.text = expenseSum.toString()
            binding.txtOverviewAmount.text = balance.toString()
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
            categoryViewModel.deleteACategoryById(categoryId)
        }
    }

}
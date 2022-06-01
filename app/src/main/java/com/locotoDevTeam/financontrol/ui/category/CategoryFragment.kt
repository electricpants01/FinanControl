package com.locotoDevTeam.financontrol.ui.category

import android.animation.ValueAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.locotoDevTeam.financontrol.R
import com.locotoDevTeam.financontrol.data.adapter.CategoryAdapter
import com.locotoDevTeam.financontrol.data.dialog.AddCategoryDialog
import com.locotoDevTeam.financontrol.data.dialog.MaterialAlert
import com.locotoDevTeam.financontrol.database.FinancialDB
import com.locotoDevTeam.financontrol.databinding.FragmentCategoryBinding
import com.locotoDevTeam.financontrol.ui.MainActivity
import com.locotoDevTeam.financontrol.ui.SharePreference
import kotlinx.coroutines.*

class CategoryFragment : Fragment(), CategoryAdapter.CategoryListener{

    private val categoryViewModel: CategoryViewModel by activityViewModels()
    lateinit var binding: FragmentCategoryBinding
    lateinit var recycler: RecyclerView
    lateinit var adapter: CategoryAdapter
    var shouldShowOverViewTotal: Boolean = false

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
        shouldShowOverViewTotal = SharePreference(requireContext()).getShowOverviewTotal()
        initRecycler()
        openAddNewCategory()
        initSubscriptions()
        shouldShowOverviewTotal()
    }

    private fun initRecycler(){
        recycler = binding.rvCategory
        adapter = CategoryAdapter(emptyList(),this)
        recycler.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recycler.adapter = adapter
    }

    private fun shouldShowOverviewTotal(){
        if(!shouldShowOverViewTotal) {
            binding.ivShowOverviewTotal.setImageResource(R.drawable.eyes_closed)
        }else{
            binding.ivShowOverviewTotal.setImageResource(R.drawable.eyes_open)
        }
    }

    private fun initSubscriptions(){

        FinancialDB.getAppDataBase(requireContext())?.categoryDao()?.getAll()?.observe(viewLifecycleOwner) {
            initExpenseIncomeOverview()
            if (it.isNotEmpty()) {
                // all welcome components
                binding.viewContainer.visibility = View.GONE
                binding.txtWelcome.visibility = View.GONE
                binding.txtWelcomeDescription.visibility = View.GONE
                binding.arrowAnimation.visibility = View.GONE
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
                binding.arrowAnimation.visibility = View.VISIBLE
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
    }

    private fun initExpenseIncomeOverview(){
        CoroutineScope(Dispatchers.IO).launch {
            val incomeSum = FinancialDB.getAppDataBase(requireContext())?.incomeDao()?.getSumIncome()
            val expenseSum = FinancialDB.getAppDataBase(requireContext())?.incomeDao()?.getSumExpense()
            val currency = SharePreference(requireContext()).getCurrencyPreference() ?: "$"
            withContext(Dispatchers.Main){
//                binding.txtExpenseAmount.text = "$expenseSum $currency"
//                binding.txtIncomeAmount.text = "$incomeSum $currency"
                startCountAnimation(incomeSum?.toFloat() ?: 0f, binding.txtIncomeAmount, currency)
                startCountAnimation(expenseSum?.toFloat() ?: 0f, binding.txtExpenseAmount, currency)
                incomeSum?.let { incomeSum ->
                    expenseSum?.let { expenseSum ->
                        val total = (incomeSum - expenseSum).toString()
//                        binding.txtOverviewAmount.text = "$total $currency"
                        startCountAnimation(total.toFloat() ?: 0f, binding.txtOverviewAmount, currency)
                    }
                }
            }
        }
    }

    private fun startCountAnimation(value: Float, textView: TextView, currency: String = "$"){
        if (shouldShowOverViewTotal){
            val animator = ValueAnimator.ofInt(0, value.toInt())
            animator.duration = 500
            animator.addUpdateListener { animation -> textView.text = "${animation.animatedValue} $currency" }
            animator.start()
        }else {
            textView.text = "*****"
        }
    }

    private fun openAddNewCategory(){
        binding.floatingActionButton.setOnClickListener {
            val myDialog = AddCategoryDialog()
            myDialog.show(parentFragmentManager, "AddCategory")
        }

        binding.ivShowOverviewTotal.setOnClickListener {
            // first, we get the current state of sharedPreference of showOverviewTotal
            shouldShowOverViewTotal = !shouldShowOverViewTotal
            SharePreference(requireContext()).saveShowOverviewTotal(shouldShowOverViewTotal)
            if(!shouldShowOverViewTotal) {
                binding.ivShowOverviewTotal.setImageResource(R.drawable.eyes_closed)
            }else{
                binding.ivShowOverviewTotal.setImageResource(R.drawable.eyes_open)
            }
            initExpenseIncomeOverview()
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
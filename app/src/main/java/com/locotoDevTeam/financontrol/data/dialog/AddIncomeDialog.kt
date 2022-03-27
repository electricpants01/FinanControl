package com.locotoDevTeam.financontrol.data.dialog

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.locotoDevTeam.financontrol.R
import com.locotoDevTeam.financontrol.databinding.DialogAddCategoryBinding
import com.locotoDevTeam.financontrol.databinding.DialogAddIncomeBinding
import com.locotoDevTeam.financontrol.ui.insight.InsightViewModel
import java.sql.SQLOutput

class AddIncomeDialog: BottomSheetDialogFragment() {

    private val insightViewModel: InsightViewModel by activityViewModels()
    private lateinit var binding: DialogAddIncomeBinding
    private lateinit var listener: AddIncomeListener
    private var chipSelected: String = Insight.Income.name

    enum class Insight{
        Income, Expense;
    }

    interface AddIncomeListener{
        // this listener is executed in the MainActivity.kt
        fun onAddIncomeTapped(categoryId: Long, amount: Double, type: String)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as AddIncomeListener
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.dialog_add_income,container,false)
        binding = DialogAddIncomeBinding.bind(view)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initButtonListener()
    }

    private fun initButtonListener(){
        // button for adding new income or expense
        binding.btnAddIncomeExpense.setOnClickListener {
            if( binding.editTextAddIncomeExpense.text.toString().isEmpty()){
                binding.editTextAddIncomeExpense.error = context?.getString(R.string.dialog_should_not_be_empty)
                return@setOnClickListener
            }
            if( binding.editTextAddIncomeExpense.text.toString().length >= 6){
                binding.editTextAddIncomeExpense.error = context?.getString(R.string.dialog_income_expense_max_length)
                return@setOnClickListener
            }
            val amount = binding.editTextAddIncomeExpense.text.toString().toDouble()
            insightViewModel.categoryId.value?.let { categoryId ->
                listener.onAddIncomeTapped(categoryId, amount, chipSelected)
            }
            dismiss()
        }

        binding.btnCancelIncomeExpense.setOnClickListener {
            dismiss()
        }

        binding.chipIncome.setOnClickListener {
            chipSelected = Insight.Income.name
        }

        binding.chipExpense.setOnClickListener {
            chipSelected = Insight.Expense.name
        }
    }

}
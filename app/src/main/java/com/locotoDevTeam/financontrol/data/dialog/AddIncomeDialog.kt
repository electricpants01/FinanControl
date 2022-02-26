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
import com.locotoDevTeam.financontrol.R
import com.locotoDevTeam.financontrol.databinding.DialogAddCategoryBinding
import com.locotoDevTeam.financontrol.databinding.DialogAddIncomeBinding
import com.locotoDevTeam.financontrol.ui.insight.InsightViewModel
import java.sql.SQLOutput

class AddIncomeDialog: DialogFragment() {

    private val insightViewModel: InsightViewModel by activityViewModels()
    private lateinit var binding: DialogAddIncomeBinding
    private lateinit var listener: AddIncomeListener
    private val spinnerItems = arrayListOf<String>("Income","Expense")
    private var spinnerItemSelected: String = ""

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
        initSpinner()
    }

    fun initButtonListener(){
        // button for adding new income or expense
        binding.btnAddIncomeExpense.setOnClickListener {
            val amount = binding.editTextAddIncomeExpense.text.toString().toDouble()
            insightViewModel.categoryId.value?.let { categoryId ->
                listener.onAddIncomeTapped(categoryId, amount, spinnerItemSelected)
            }
            dismiss()
        }
        binding.btnCancelIncomeExpense.setOnClickListener {
            dismiss()
        }
    }

    fun initSpinner(){
        val adapter = ArrayAdapter(requireContext(),android.R.layout.simple_spinner_dropdown_item, spinnerItems)
        binding.spinner.adapter = adapter

        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, id: Long) {
                spinnerItemSelected = when(position){
                    Insight.Income.ordinal -> Insight.Income.name
                    Insight.Expense.ordinal -> Insight.Expense.name
                    else -> Insight.Income.name
                }
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }
    }

}
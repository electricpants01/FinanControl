package com.locotoDevTeam.financontrol.data.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.locotoDevTeam.financontrol.R
import com.locotoDevTeam.financontrol.database.entity.TransactionType
import com.locotoDevTeam.financontrol.databinding.DialogAddIncomeBinding
import com.locotoDevTeam.financontrol.ui.insight.InsightViewModel

class AddIncomeDialog: BottomSheetDialogFragment() {

    companion object {
        /** Request key used by FragmentResultListener in InsightFragment. */
        const val REQUEST_KEY = "AddIncomeDialog_result"
        const val KEY_CATEGORY_ID = "categoryId"
        const val KEY_AMOUNT = "amount"
        const val KEY_TYPE = "type"
    }

    private val insightViewModel: InsightViewModel by activityViewModels()
    private lateinit var binding: DialogAddIncomeBinding
    private var spinnerItemSelected: TransactionType = TransactionType.INCOME

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

    private fun initButtonListener(){
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
                setFragmentResult(REQUEST_KEY, bundleOf(
                    KEY_CATEGORY_ID to categoryId,
                    KEY_AMOUNT to amount,
                    KEY_TYPE to spinnerItemSelected.name
                ))
            }
            dismiss()
        }
        binding.btnCancelIncomeExpense.setOnClickListener {
            dismiss()
        }
    }

    private fun initSpinner(){
        val spinnerItems = arrayListOf<String>(requireContext().getString(R.string.income),requireContext().getString(R.string.expense))
        val adapter = ArrayAdapter(requireContext(),android.R.layout.simple_spinner_dropdown_item, spinnerItems)
        binding.spinner.adapter = adapter

        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, id: Long) {
                spinnerItemSelected = TransactionType.entries[position]
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }
    }

}

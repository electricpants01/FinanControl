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
import com.locotoDevTeam.financontrol.R
import com.locotoDevTeam.financontrol.databinding.DialogAddCategoryBinding
import com.locotoDevTeam.financontrol.databinding.DialogAddIncomeBinding
import java.sql.SQLOutput

class AddIncomeDialog: DialogFragment() {

    lateinit var binding: DialogAddIncomeBinding
    lateinit var listener: AddIncomeListener
    val spinnerItems = arrayListOf<String>("Income","Expense")

    interface AddIncomeListener{
        fun onAddIncomeTapped(categoryName: String)
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
        binding.btnAddCategory.setOnClickListener {
            val text = binding.editTextTextPersonName.text.toString()
            listener.onAddIncomeTapped(text)
            dismiss()
        }
        binding.btnCancelCategory.setOnClickListener {
            dismiss()
        }
    }

    fun initSpinner(){
        val adapter = ArrayAdapter(requireContext(),R.layout.support_simple_spinner_dropdown_item, spinnerItems)
        binding.spinner.adapter = adapter

        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                println("chris presionaste ${p2}")
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }


        }
    }

}
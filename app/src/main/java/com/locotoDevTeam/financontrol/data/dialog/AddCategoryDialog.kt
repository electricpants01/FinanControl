package com.locotoDevTeam.financontrol.data.dialog

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.locotoDevTeam.financontrol.R
import com.locotoDevTeam.financontrol.databinding.DialogAddCategoryBinding

class AddCategoryDialog: DialogFragment() {

    lateinit var binding: DialogAddCategoryBinding
    lateinit var listener: AddCategoryListener

    interface AddCategoryListener{
        // this listener is executed in the MainActivity.kt
        fun onAddCategoryTapped(categoryName: String)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as AddCategoryListener
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.dialog_add_category,container,false)
        binding = DialogAddCategoryBinding.bind(view)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initButtonListener()
    }

    fun initButtonListener(){
        // this button creates a new category
        binding.btnAddCategory.setOnClickListener {
            if( binding.editTextTextPersonName.text.toString().isEmpty() ){
                binding.editTextTextPersonName.error = context?.getString(R.string.dialog_should_not_be_empty)
                return@setOnClickListener
            }
            if( binding.editTextTextPersonName.text.toString().length > 15 ){
                binding.editTextTextPersonName.error = context?.getString(R.string.dialog_category_max_length)
                return@setOnClickListener
            }
            val text = binding.editTextTextPersonName.text.toString()
            listener.onAddCategoryTapped(text)
            dismiss()
        }
        binding.btnCancelCategory.setOnClickListener {
            dismiss()
        }
    }

}
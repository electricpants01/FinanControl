package com.locotoDevTeam.financontrol.data.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.locotoDevTeam.financontrol.R
import com.locotoDevTeam.financontrol.databinding.DialogAddCategoryBinding

class AddCategoryDialog: BottomSheetDialogFragment() {

    companion object {
        /** Request key used by FragmentResultListener in CategoryFragment. */
        const val REQUEST_KEY = "AddCategoryDialog_result"
        const val KEY_CATEGORY_NAME = "categoryName"
    }

    lateinit var binding: DialogAddCategoryBinding

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
            setFragmentResult(REQUEST_KEY, bundleOf(KEY_CATEGORY_NAME to text))
            dismiss()
        }
        binding.btnCancelCategory.setOnClickListener {
            dismiss()
        }
        binding.ivClose.setOnClickListener {
            dismiss()
        }
    }

}

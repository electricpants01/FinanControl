package com.locotoDevTeam.financontrol.ui.category

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
            adapter.setCategoryList(it)
        })
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
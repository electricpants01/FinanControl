package com.locotoDevTeam.financontrol.ui.insight

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.locotoDevTeam.financontrol.R
import com.locotoDevTeam.financontrol.data.adapter.InsightAdapter
import com.locotoDevTeam.financontrol.data.dialog.AddIncomeDialog
import com.locotoDevTeam.financontrol.data.dialog.MaterialAlert
import com.locotoDevTeam.financontrol.database.FinancialDB
import com.locotoDevTeam.financontrol.database.entity.Income
import com.locotoDevTeam.financontrol.databinding.FragmentInsightBinding


class InsightFragment : Fragment(), InsightAdapter.InsightListener {

    private val args: InsightFragmentArgs by navArgs()
    private val insightViewModel: InsightViewModel by activityViewModels()
    lateinit var binding: FragmentInsightBinding
    lateinit var recycler: RecyclerView
    lateinit var adapter: InsightAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_insight, container, false)
        binding = FragmentInsightBinding.bind(view)
        insightViewModel.setCategoryId(args.categoryId)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecycler()
        initListeners()
        initSubscriptions()
    }

    fun initRecycler(){
        recycler = binding.rvInsight
        adapter = InsightAdapter(emptyList(), this)
        recycler.layoutManager = LinearLayoutManager(context,LinearLayoutManager.VERTICAL, false)
        recycler.adapter = adapter
    }

    fun initListeners(){ // FloatActionButton
        binding.floatAddInsight.setOnClickListener {
            val dialog = AddIncomeDialog()
            dialog.show(parentFragmentManager,"IncomeDialog")
        }
    }

    fun initSubscriptions(){
        val categoryId = insightViewModel.categoryId.value
        categoryId?.let {
            FinancialDB.getAppDataBase(requireContext())?.incomeDao()?.getAllByCategoryId(it)?.observe(viewLifecycleOwner,{
                adapter.setNewIncomeList(it)
            })
        }
    }

    override fun onInsightTapped(incomeId: Long) {
        // debes motrar la description en un toast de material design
    }

    override fun onDeleteInsightTapped(income: Income) {
        MaterialAlert.showDialog(resources.getString(R.string.insight_deletion_title),
            resources.getString(R.string.insight_delete_description), requireContext()){
            insightViewModel.deleteInsight(income, requireContext())
        }
    }
}
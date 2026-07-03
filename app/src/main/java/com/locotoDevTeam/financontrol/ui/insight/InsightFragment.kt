package com.locotoDevTeam.financontrol.ui.insight

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.locotoDevTeam.financontrol.R
import com.locotoDevTeam.financontrol.data.adapter.InsightAdapter
import com.locotoDevTeam.financontrol.data.dialog.AddIncomeDialog
import com.locotoDevTeam.financontrol.data.dialog.MaterialAlert
import com.locotoDevTeam.financontrol.database.entity.Income
import com.locotoDevTeam.financontrol.database.entity.TransactionType
import com.locotoDevTeam.financontrol.databinding.FragmentInsightBinding
import com.locotoDevTeam.financontrol.fancyChart.FancyChart
import com.locotoDevTeam.financontrol.fancyChart.MyFancyChartBuilder
import com.locotoDevTeam.financontrol.ui.MainActivity
import com.locotoDevTeam.financontrol.util.formatDateAndTimeString
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class InsightFragment : Fragment(), InsightAdapter.InsightListener {

    private val args: InsightFragmentArgs by navArgs()
    private val insightViewModel: InsightViewModel by activityViewModels()
    lateinit var binding: FragmentInsightBinding
    lateinit var recycler: RecyclerView
    lateinit var adapter: InsightAdapter
    lateinit var chart: FancyChart

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_insight, container, false)
        binding = FragmentInsightBinding.bind(view)
        (activity as MainActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        insightViewModel.setCategoryId(args.categoryId)
        chart = binding.insightFancyChart
        chart.setOnPointClickListener {
            Snackbar.make(binding.floatAddInsight, getString(R.string.insight_point_item_tapped, it.y.toString()), Snackbar.LENGTH_SHORT).show()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecycler()
        initListeners()
        observeIncomes()
        observeUiState()
    }

    fun initRecycler() {
        recycler = binding.rvInsight
        adapter = InsightAdapter(emptyList(), this)
        recycler.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recycler.adapter = adapter
    }

    fun initListeners() {
        binding.floatAddInsight.setOnClickListener {
            val dialog = AddIncomeDialog()
            dialog.show(parentFragmentManager, "IncomeDialog")
        }
    }

    /**
     * Collect raw incomes from the repository Flow (via ViewModel). When they
     * arrive, delegate processing to the ViewModel which updates [InsightUiState].
     */
    private fun observeIncomes() {
        val categoryId = insightViewModel.categoryId.value
        categoryId?.let { id ->
            viewLifecycleOwner.lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    insightViewModel.getIncomesByCategoryId(id).collect { incomes ->
                        insightViewModel.splitIncomeAndExpenses(incomes)
                        adapter.setNewIncomeList(incomes)
                    }
                }
            }
        }
    }

    /**
     * Observe the single UiState for chart data and render the chart.
     */
    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                insightViewModel.insightUiState.collect { state ->
                    MyFancyChartBuilder.createChart(state.chartData, chart)
                }
            }
        }
    }

    override fun onInsightTapped(income: Income) {
        val type = if (income.type == TransactionType.INCOME) getString(R.string.income) else getString(R.string.expense)
        val text = getString(R.string.insight_item_tapped, type, income.amount.toString(), income.timestamp.formatDateAndTimeString())
        Snackbar.make(binding.floatAddInsight, text, Snackbar.LENGTH_LONG)
            .show()
    }

    override fun onDeleteInsightTapped(income: Income) {
        MaterialAlert.showDialog(resources.getString(R.string.insight_deletion_title),
            resources.getString(R.string.insight_delete_description), requireContext()) {
            insightViewModel.deleteInsight(income)
        }
    }
}

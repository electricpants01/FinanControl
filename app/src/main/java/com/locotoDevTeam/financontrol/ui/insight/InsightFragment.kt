package com.locotoDevTeam.financontrol.ui.insight

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import com.locotoDevTeam.financontrol.R
import com.locotoDevTeam.financontrol.data.adapter.InsightAdapter
import com.locotoDevTeam.financontrol.data.adapter.SectionAdapter
import com.locotoDevTeam.financontrol.data.dialog.AddIncomeDialog
import com.locotoDevTeam.financontrol.data.dialog.MaterialAlert
import com.locotoDevTeam.financontrol.database.FinancialDB
import com.locotoDevTeam.financontrol.database.entity.Income
import com.locotoDevTeam.financontrol.databinding.FragmentInsightBinding
import com.locotoDevTeam.financontrol.fancyChart.FancyChart
import com.locotoDevTeam.financontrol.fancyChart.MyFancyChartBuilder
import com.locotoDevTeam.financontrol.fancyChart.data.ChartData
import com.locotoDevTeam.financontrol.ui.MainActivity
import com.locotoDevTeam.financontrol.util.Constants
import com.locotoDevTeam.financontrol.util.formatDateAndTimeString
import com.locotoDevTeam.financontrol.util.formatDateString
import com.locotoDevTeam.financontrol.util.toDate
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.math.exp


class InsightFragment : Fragment(), InsightAdapter.InsightListener {

    private val args: InsightFragmentArgs by navArgs()
    private val insightViewModel: InsightViewModel by activityViewModels()
    lateinit var binding: FragmentInsightBinding
    lateinit var recycler: RecyclerView
    lateinit var adapter: SectionAdapter
    lateinit var chart: FancyChart

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_insight, container, false)
        binding = FragmentInsightBinding.bind(view)
        (activity as MainActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setHasOptionsMenu(true)
        insightViewModel.setCategoryId(args.categoryId)
        chart = binding.insightFancyChart
        initSubscriptions()
        chart.setOnPointClickListener {
            Snackbar.make(binding.floatAddInsight,getString(R.string.insight_point_item_tapped, it.y.toString()),Snackbar.LENGTH_SHORT).show()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecycler()
        initListeners()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.setGroupVisible(R.id.menu_currency_group, false)
        inflater.inflate(R.menu.menu_insight, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.menu_calendar -> {
                showCalendar()
            }
            android.R.id.home -> {
                (activity as MainActivity).onSupportNavigateUp()
            }
        }
        return true
    }

    private fun initRecycler(){
        recycler = binding.rvSection
        adapter = SectionAdapter(emptyList(), emptyList(), requireContext(), this)
        recycler.layoutManager = LinearLayoutManager(context,LinearLayoutManager.VERTICAL, false)
        recycler.adapter = adapter
    }

    fun initListeners(){ // FloatActionButton
        binding.floatAddInsight.setOnClickListener {
            val dialog = AddIncomeDialog()
            dialog.show(parentFragmentManager,"IncomeDialog")
        }

        binding.chipAll.setOnClickListener {
            val incomes = insightViewModel.filteredList.value
            updateRecyclerViewIncomeList(incomes ?: emptyList())
        }

        binding.chipIncome.setOnClickListener {
            val incomes = insightViewModel.filteredList.value?.filter { it.type ==  AddIncomeDialog.Insight.Income.name}
            updateRecyclerViewIncomeList(incomes ?: emptyList())
        }

        binding.chipExpense.setOnClickListener {
            val incomes = insightViewModel.filteredList.value?.filter { it.type ==  AddIncomeDialog.Insight.Expense.name}
            updateRecyclerViewIncomeList(incomes ?: emptyList())
        }
    }

    private fun initSubscriptions(){
        val categoryId = insightViewModel.categoryId.value
        categoryId?.let {
            FinancialDB.getAppDataBase(requireContext())?.incomeDao()?.getAllByCategoryId(it)?.observe(viewLifecycleOwner) { incomes ->
                val isEmpty = incomes.isEmpty()
                binding.txtEmptyIncomes.visibility = if(isEmpty) View.VISIBLE else View.GONE
                binding.incomeAnimation.visibility = if(isEmpty) View.VISIBLE else View.GONE
                insightViewModel.setIncomeList(incomes)
                binding.myChipGroup.check(binding.chipAll.id)
                if(!isEmpty){
                    var sections = incomes.map { it -> it.timestamp.formatDateString() }
                    sections = sections.distinct()
                    sections = sections.reversed()
                    insightViewModel.splitIncomeAndExpenses(incomes)
                    adapter.setSectionIncomeList(sections, incomes)
                }else {
                    adapter.setSectionIncomeList(emptyList(), emptyList())
                }
            }
        }

//        insightViewModel.incomeExpenseGraphList.observe(viewLifecycleOwner,{
//           MyFancyChartBuilder.createChart(it,chart)
//        })
    }

    override fun onInsightTapped(income: Income) {
        val type = if(income.type == "Income") getString(R.string.income) else { getString(R.string.expense) }
        var text = getString(R.string.insight_item_tapped, type, income.amount.toString(), income.timestamp.formatDateAndTimeString())
        Snackbar.make(binding.floatAddInsight, text, Snackbar.LENGTH_LONG)
            .show()
    }

    override fun onDeleteInsightTapped(income: Income) {
        MaterialAlert.showDialog(resources.getString(R.string.insight_deletion_title),
            resources.getString(R.string.insight_delete_description), requireContext()){
            insightViewModel.deleteInsight(income, requireContext())
        }
    }

    private fun updateRecyclerViewIncomeList(incomes: List<Income>) {
        val originalList = insightViewModel.incomeList.value ?: emptyList()
        if(originalList.isNotEmpty()){
            var sections = incomes.map { it -> it.timestamp.formatDateString() }
            sections = sections.distinct()
            sections = sections.reversed()
            if (sections.isEmpty()) binding.emptyAnimation.visibility = View.VISIBLE
            else binding.emptyAnimation.visibility = View.GONE
            insightViewModel.splitIncomeAndExpenses(incomes)
            adapter.setSectionIncomeList(sections, incomes)
        }
    }

    private fun showCalendar(){
        // TODO: restart button on calendar and do this calendar a range
        val picker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select date")
                .build()
        picker.isCancelable = false
        picker.addOnPositiveButtonClickListener {
            val date = Date(it)
            val simple = SimpleDateFormat(Constants.monthDayYear)
            simple.timeZone = TimeZone.getTimeZone("UTC")
            val sectionName = listOf<String>(simple.format(date))
            val myFilteredList = insightViewModel.incomeList.value?.filter { it.timestamp.formatDateString() == sectionName[0] }
            insightViewModel.filteredList.postValue(myFilteredList)
            updateRecyclerViewIncomeList(myFilteredList ?: emptyList())
            binding.myChipGroup.check(binding.chipAll.id)
        }
        picker.addOnNegativeButtonClickListener {
            // Respond to negative button click.
        }
        picker.addOnCancelListener {
            // Respond to cancel button click.
        }
        picker.addOnDismissListener {
            // Respond to dismiss events.
        }
        picker.show(parentFragmentManager, "DatePicker")
    }
}
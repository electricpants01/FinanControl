package com.locotoDevTeam.financontrol.data.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.locotoDevTeam.financontrol.R
import com.locotoDevTeam.financontrol.database.entity.Income
import com.locotoDevTeam.financontrol.databinding.RvSectionsBinding
import com.locotoDevTeam.financontrol.util.formatDateString

class SectionAdapter(var sections: List<String>, var incomes: List<Income>, val context: Context, val listener: InsightAdapter.InsightListener): RecyclerView.Adapter<SectionAdapter.Holder>(){

    fun setSectionIncomeList(sections: List<String>, incomes: List<Income>) {
        this.sections = sections
        this.incomes = incomes
        notifyDataSetChanged()
    }

    class Holder(val view: View): RecyclerView.ViewHolder(view) {
        private val binding = RvSectionsBinding.bind(view)

        fun render(sectionName: String, incomes: List<Income>,context: Context, listener: InsightAdapter.InsightListener ) {
            binding.sectionName.text = sectionName
            initRecyclerView(listener, context, incomes)
        }

        private fun initRecyclerView(listener: InsightAdapter.InsightListener, context: Context, incomes: List<Income>){
            val recycler = binding.sectionRecyclerView
            val adapter = InsightAdapter(incomes, listener)
            recycler.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            recycler.adapter = adapter
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rv_sections,parent,false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val currentSection = sections[position]
        val filteredIncome = incomes.filter { it.timestamp.formatDateString() == currentSection }
        holder.render(currentSection,filteredIncome, context, listener)
    }

    override fun getItemCount(): Int  = sections.size

}
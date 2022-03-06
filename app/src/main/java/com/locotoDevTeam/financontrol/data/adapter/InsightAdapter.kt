package com.locotoDevTeam.financontrol.data.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.locotoDevTeam.financontrol.R
import com.locotoDevTeam.financontrol.database.entity.Income
import com.locotoDevTeam.financontrol.databinding.RvInsightBinding

class InsightAdapter(var incomeList: List<Income>,val listener: InsightListener): RecyclerView.Adapter<InsightAdapter.Holder>() {

    interface InsightListener{
        fun onInsightTapped(income: Income)
        fun onDeleteInsightTapped(income: Income)
    }

    fun setNewIncomeList(newIncomeList: List<Income>){
        this.incomeList = newIncomeList
        notifyDataSetChanged()
    }

    class Holder(val view: View): RecyclerView.ViewHolder(view) {

        val binding = RvInsightBinding.bind(view)

        fun render(income: Income, listener: InsightListener){
            binding.txtAmount.text = income.amount.toString()
            if( income.type == "Income"){
                binding.ivInsightType.setImageResource(R.drawable.ic_twotone_arrow_circle_up)
            } else {
                binding.ivInsightType.setImageResource(R.drawable.ic_twotone_arrow_circle_down)
            }
            binding.cvCategory.setOnClickListener { listener.onInsightTapped(income) }
            binding.ivDeleteInsight.setOnClickListener { listener.onDeleteInsightTapped(income) }
            binding.cvCategory.setOnLongClickListener {
                listener.onDeleteInsightTapped(income)
                return@setOnLongClickListener true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InsightAdapter.Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rv_insight,parent,false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: InsightAdapter.Holder, position: Int) {
        val current = incomeList[position]
        holder.render(current,listener)
    }

    override fun getItemCount(): Int = incomeList.size

}
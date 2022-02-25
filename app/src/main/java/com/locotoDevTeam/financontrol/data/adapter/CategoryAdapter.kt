package com.locotoDevTeam.financontrol.data.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.locotoDevTeam.financontrol.R
import com.locotoDevTeam.financontrol.database.entity.Category
import com.locotoDevTeam.financontrol.databinding.RvCategoryBinding

class CategoryAdapter(var categories: List<Category>,val listener: CategoryListener): RecyclerView.Adapter<CategoryAdapter.CategoryHolder>() {

    interface CategoryListener{
        fun onCategoryTapped(categoryId: Long)
    }

    fun setCategoryList(newCategoryList: List<Category>){
        this.categories = newCategoryList
        notifyDataSetChanged()
    }

    open class CategoryHolder(view: View,val listener: CategoryListener): RecyclerView.ViewHolder(view) {
        val rvCategoryBinding: RvCategoryBinding = RvCategoryBinding.bind(view)

        fun render(category: Category){
            rvCategoryBinding.cvCategory.setOnClickListener { listener.onCategoryTapped(category.uid!!) }
            rvCategoryBinding.txtName.text = category.name
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CategoryAdapter.CategoryHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rv_category, parent, false)
        return CategoryHolder(view, listener)
    }

    override fun onBindViewHolder(holder: CategoryHolder, position: Int) {
        val current = categories[position]
        holder.render(current)
    }

    override fun getItemCount(): Int = categories.size
}
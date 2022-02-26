package com.locotoDevTeam.financontrol.ui.insight

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class InsightViewModel: ViewModel() {

    var categoryId = MutableLiveData<Long>()


    fun setCategoryId(newCategoryId: Long){
        println("chris: se va a setear tu categoryId a ${newCategoryId}")
        this.categoryId.value = newCategoryId
    }

}
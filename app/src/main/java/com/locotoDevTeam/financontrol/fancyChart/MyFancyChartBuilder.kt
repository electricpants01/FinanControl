package com.locotoDevTeam.financontrol.fancyChart

import com.locotoDevTeam.financontrol.database.entity.Income
import com.locotoDevTeam.financontrol.fancyChart.data.ChartData




class MyFancyChartBuilder {

    companion object{
        fun createChart(incomeExpensesList: List<Income>, chart: FancyChart){
            // First data set
            // First data set
            val data = ChartData(ChartData.LINE_COLOR_GREEN)
            val data2 = ChartData(ChartData.LINE_COLOR_RED)
            data.addPoint(1,10)
            data.addXValue( 1.0 ,"primero")
//            incomeExpensesList.forEachIndexed{ index, income ->
//                if( income.type == "Income"){
//                    data.addPoint(index, income.amount.toInt())
//                    data.addXValue(index.toDouble(), income.timestamp)
//                }
//            }
            chart.addData(data)
//
//            incomeExpensesList.forEachIndexed{ index, income ->
//                if( income.type == "Expense"){
//                    data2.addPoint(index, income.amount.toInt())
//                    data2.addXValue(index.toDouble(), income.timestamp)
//                }
//            }

            chart.addData(data2)
//            val yValues2 = intArrayOf(0, 5, 9, 23, 15, 35, 45, 50, 41, 45, 32, 24)
//            for (i in 8..19) {
//                data2.addPoint(i, yValues2[i - 8])
//                data2.addXValue(i.toDouble(), "$i:00")
//            }
//            chart.addData(data2)
        }
    }
}
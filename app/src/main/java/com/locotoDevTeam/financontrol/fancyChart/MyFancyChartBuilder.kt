package com.locotoDevTeam.financontrol.fancyChart

import com.locotoDevTeam.financontrol.database.entity.Income
import com.locotoDevTeam.financontrol.fancyChart.data.ChartData
import com.locotoDevTeam.financontrol.util.formatDateString
import com.locotoDevTeam.financontrol.util.formatHourString


class MyFancyChartBuilder {

    companion object{
        fun createChart(incomeExpensesList: List<Income>, chart: FancyChart){
            // first we delete all the components on the canvas
            chart.clearValues()

            val data = ChartData(ChartData.LINE_COLOR_GREEN)
            val data2 = ChartData(ChartData.LINE_COLOR_RED)
//            val yValues = intArrayOf(0, 8, 9, 18, 35, 30, 33, 32, 46, 53, 50, 42)
//            for (i in 0..11){
//                if(i % 2 == 0){
//                    data.addPoint(i, yValues[i])
//                }else{
//                    data2.addPoint(i,yValues[i])
//                }
//            }
            incomeExpensesList.forEachIndexed{ index, income ->
                if( income.type == "Income"){
                    data.addPoint(index, income.amount.toInt())
                    data.addXValue(index.toDouble(),income.timestamp.formatHourString())
                }else {
                    data2.addPoint(index, income.amount.toInt())
                    data2.addXValue(index.toDouble(),income.timestamp.formatHourString())
                }
            }
            chart.addData(data)
            chart.addData(data2)
            // the chart.invalidate will redraw the canvas
            chart.invalidate()
        }
    }
}
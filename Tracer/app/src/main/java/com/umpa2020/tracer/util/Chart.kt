package com.umpa2020.tracer.util

import android.graphics.Color
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.umpa2020.tracer.App
import com.umpa2020.tracer.R

class Chart(val altsData: List<Double>, val speedsData: List<Double>, val chart: LineChart) {

  fun setChart() {    //클래스로 따로 빼야할듯
    val lineChart = chart
    val alts = ArrayList<Entry>()
    val speeds = ArrayList<Entry>()

    altsData.forEachIndexed { i, d ->
      alts.add(Entry(i.toFloat(), d.toFloat()))
    }
    speedsData.forEachIndexed { i, d ->
      speeds.add(Entry(i.toFloat(), d.toFloat()))
    }
    val xAxis = lineChart.xAxis
    xAxis.isEnabled = false
    val yLAxis = lineChart.axisLeft
    yLAxis.textColor = Color.RED
    yLAxis.axisMaximum = altsData.max()!!.toFloat() + 5
    yLAxis.axisMinimum = altsData.min()!!.toFloat() - 5

    val yRAxis = lineChart.axisRight
    yRAxis.textColor = Color.BLUE
    yRAxis.axisMaximum = speedsData.max()!!.toFloat() + 5
    yRAxis.axisMinimum = 0F

    // 속도 셋팅
    val lineDataSet = LineDataSet(speeds, App.instance.context().getString(R.string.speed))
    lineDataSet.lineWidth = 1.5f
    lineDataSet.color = Color.parseColor("#FF0000FF") // 파랑
    lineDataSet.setDrawHorizontalHighlightIndicator(false)
    lineDataSet.setDrawHighlightIndicators(false)
    lineDataSet.setDrawValues(false)
    lineDataSet.setDrawCircles(false)
    lineDataSet.setDrawCircleHole(false)
    lineDataSet.axisDependency = YAxis.AxisDependency.RIGHT
    lineDataSet.mode = LineDataSet.Mode.CUBIC_BEZIER

    // 고도 셋팅
    val lineDataSet2 = LineDataSet(alts, App.instance.context().getString(R.string.altitude))
    lineDataSet2.lineWidth = 1.5f
    lineDataSet2.color = Color.parseColor("#FFFF0000") // 빨강
    lineDataSet2.setDrawHorizontalHighlightIndicator(false)
    lineDataSet2.setDrawHighlightIndicators(false)
    lineDataSet2.setDrawValues(false)
    lineDataSet2.setDrawCircles(false)
    lineDataSet2.setDrawCircleHole(false)
    lineDataSet2.fillColor = Color.parseColor("#FFFF0000")
    lineDataSet2.setDrawFilled(true)
    lineDataSet2.axisDependency = YAxis.AxisDependency.LEFT


    val chartData = LineData()
    chartData.addDataSet(lineDataSet)
    chartData.addDataSet(lineDataSet2)

    /* lineChart.isDoubleTapToZoomEnabled = false // 더블 클릭 막기
     lineChart.setDrawGridBackground(false)
     lineChart.animateY(2000, Easing.EasingOption.EaseInCubic)*/

    lineChart.data = chartData
    lineChart.invalidate()

  }
}
package com.sutech.diary.view.statictics

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Color
import android.view.LayoutInflater
import android.widget.PopupWindow
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.sutech.diary.adapter.AdapterTrend
import com.sutech.diary.adapter.decoration.GridSpacingItemDecoration
import com.sutech.diary.base.BaseFragment
import com.sutech.diary.util.BarChartCustomRenderer
import com.sutech.diary.util.MoodFilter
import com.sutech.diary.util.MoodUtil
import com.sutech.diary.util.setOnClick
import com.sutech.journal.diary.diarywriting.lockdiary.R
import kotlinx.android.synthetic.main.fragment_statistics.btnBack
import kotlinx.android.synthetic.main.fragment_statistics.btnFilter
import kotlinx.android.synthetic.main.fragment_statistics.chart
import kotlinx.android.synthetic.main.fragment_statistics.layoutAdsStatics
import kotlinx.android.synthetic.main.fragment_statistics.statistic_filter_text
import kotlinx.android.synthetic.main.fragment_statistics.trendRV
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch


class StatisticsFrag : BaseFragment(R.layout.fragment_statistics) {
    private val filter: MutableStateFlow<MoodFilter> = MutableStateFlow(MoodFilter.ALL)
    private lateinit var myPopupWindow: PopupWindow

    override fun initView() {
        showAdsWithLayout("native_statics_detail", layoutAdsStatics)
        logEvent("StatisticsDiary_Show")
        setupView()
        handleEvent()
    }

    private fun handleEvent() {
        btnBack.setOnClick(500) {
            logEvent("StatisticsDiary_IconBack_Clicked")
            onBackToHome(R.id.statisticsFrag)
        }
        btnFilter.setOnClick(500) {
            myPopupWindow.showAsDropDown(it, 20, 0);
        }
    }

    private fun setupView() {
        setupRV()
        setupChart()
        setupPopup()
        lifecycleScope.launch {
            filter.collect { filter ->
                statistic_filter_text.text = resources.getString(filter.stringRes)
                setData()
            }
        }
    }

    private fun setupPopup() {
        val view = (requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(R.layout.item_popup, null)

        view.findViewById<TextView>(R.id.last_7_days).setOnClick(500) {
            lifecycleScope.launch {
                logEvent("StatisticsDiary_7day_Clicked")
                filter.emit(MoodFilter.SEVEN_DAY)
                myPopupWindow.dismiss()
            }
        }
        view.findViewById<TextView>(R.id.last_30_days).setOnClick(500) {
            lifecycleScope.launch {
                logEvent("StatisticsDiary_30day_Clicked")
                filter.emit(MoodFilter.THIRTY_DAY)
                myPopupWindow.dismiss()
            }
        }
        view.findViewById<TextView>(R.id.last_90_days).setOnClick(500) {
            lifecycleScope.launch {
                logEvent("StatisticsDiary_90day_Clicked")
                filter.emit(MoodFilter.NINETY_DAY)
                myPopupWindow.dismiss()
            }
        }
        view.findViewById<TextView>(R.id.all).setOnClick(500) {
            lifecycleScope.launch {
                logEvent("StatisticsDiary_All_Clicked")
                filter.emit(MoodFilter.ALL)
                myPopupWindow.dismiss()
            }
        }

        myPopupWindow = PopupWindow(view, 300, RelativeLayout.LayoutParams.WRAP_CONTENT, true)
    }

    private fun setupChart() {
        chart.setDrawBarShadow(false)
        chart.setDrawValueAboveBar(true)
        chart.description.isEnabled = false
        chart.setPinchZoom(false)
        chart.setDrawGridBackground(false)

        val xAxis: XAxis = chart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.granularity = 1f
        xAxis.labelCount = 10
        xAxis.setDrawLabels(false)

        val leftAxis: YAxis = chart.axisLeft
        leftAxis.axisLineColor = Color.WHITE
        leftAxis.setDrawGridLines(false)
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
        leftAxis.axisMinimum = 0f // this replaces setStartAtZero(true)

        val rightAxis: YAxis = chart.axisRight
        rightAxis.isEnabled = false
        val l: Legend = chart.legend
        l.isEnabled = false
    }

    private fun setData() {
        val bitmaps = MoodUtil.moods.map { mood ->
            BitmapFactory.decodeResource(resources, mood.imageResource)
        }
        lifecycleScope.launch {
            val yVals1 = MoodUtil.moods.mapIndexed { index, moodObj ->
                lifecycleScope.async {
                    BarEntry(index.toFloat(), MoodUtil.calTrendOnTenScale(requireContext(), moodObj.id, filter = filter.value))
                }.await()
            }

            var maxValue = 10f
            for (entry in yVals1) {
                if (entry.y > maxValue) {
                    maxValue = entry.y
                }
            }

            for (entry in yVals1) {
                if (entry.y == maxValue) {
                    maxValue += 5f
                    break
                }
            }

            val set = BarDataSet(yVals1, "")
            set.color = ResourcesCompat.getColor(resources, R.color.color_chart, null)
            val dataSets = listOf(set)

            val data = BarData(dataSets)
            data.setDrawValues(false)
            chart.data = data
            chart.setScaleEnabled(false)
            val leftAxis = chart.axisLeft
            leftAxis.axisMaximum = maxValue
            chart.renderer = BarChartCustomRenderer(chart, chart.animator, chart.viewPortHandler, ArrayList(bitmaps), requireContext())
            chart.setExtraOffsets(0f, 0f, 0f, 25f)
            chart.invalidate()
        }
    }

    private fun setupRV() {
        val spanCount = 2
        trendRV.adapter = AdapterTrend().also {
            it.submitList(listOf(MoodUtil.moods[1], MoodUtil.moods[8], MoodUtil.moods[0], MoodUtil.moods[9], MoodUtil.moods[2], MoodUtil.moods[6], MoodUtil.moods[4], MoodUtil.moods[5], MoodUtil.moods[3], MoodUtil.moods[7]))
        }
        trendRV.addItemDecoration(GridSpacingItemDecoration(spanCount, 50, false))
        trendRV.layoutManager = GridLayoutManager(requireContext(), spanCount)
    }
}
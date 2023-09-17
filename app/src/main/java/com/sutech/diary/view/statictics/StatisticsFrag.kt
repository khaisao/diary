package com.sutech.diary.view.statictics

import android.graphics.BitmapFactory
import android.graphics.Color
import android.view.MenuItem
import android.view.View
import androidx.annotation.MenuRes
import androidx.appcompat.widget.PopupMenu
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
import kotlinx.android.synthetic.main.fragment_statistics.ads
import kotlinx.android.synthetic.main.fragment_statistics.btnBack
import kotlinx.android.synthetic.main.fragment_statistics.btnFilter
import kotlinx.android.synthetic.main.fragment_statistics.chart
import kotlinx.android.synthetic.main.fragment_statistics.statistic_filter_text
import kotlinx.android.synthetic.main.fragment_statistics.trendRV
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch


class StatisticsFrag : BaseFragment(R.layout.fragment_statistics) {
    private val filter: MutableStateFlow<MoodFilter> = MutableStateFlow(MoodFilter.ALL)

    override fun initView() {
        showBanner("banner_statistic", ads)
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
            showMenu(it, R.menu.menu_item_mood_filter)
        }
    }

    private fun setupView() {
        setupRV()
        setupChart()
        lifecycleScope.launch {
            filter.collect { filter ->
                statistic_filter_text.text = resources.getString(filter.stringRes)
                setData()
            }
        }
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
            val set = BarDataSet(yVals1, "")
            set.color = ResourcesCompat.getColor(resources, R.color.color_chart, null)
            val dataSets = listOf(set)

            val data = BarData(dataSets)
            data.setDrawValues(false)
            chart.data = data
            chart.setScaleEnabled(false)
            chart.renderer = BarChartCustomRenderer(chart, chart.animator, chart.viewPortHandler, ArrayList(bitmaps), requireContext())
            chart.setExtraOffsets(0f, 0f, 0f, 25f)
            chart.invalidate()
        }
    }

    private fun setupRV() {
        val spanCount = 2
        trendRV.adapter = AdapterTrend().also {
            it.submitList(MoodUtil.moods)
        }
        trendRV.addItemDecoration(GridSpacingItemDecoration(spanCount, 50, false))
        trendRV.layoutManager = GridLayoutManager(requireContext(), spanCount)
    }

    private fun showMenu(v: View, @MenuRes menuRes: Int) {
        val popup = PopupMenu(requireContext(), v)
        popup.menuInflater.inflate(menuRes, popup.menu)
        popup.setOnMenuItemClickListener { menuItem: MenuItem ->
            lifecycleScope.launch {
                when (menuItem.itemId) {
                    R.id.all -> {
                        logEvent("StatisticsDiary_All_Clicked")
                        filter.emit(MoodFilter.ALL)
                    }
                    R.id.seven_day -> {
                        logEvent("StatisticsDiary_7day_Clicked")
                        filter.emit(MoodFilter.SEVEN_DAY)
                    }
                    R.id.thirty_day -> {
                        logEvent("StatisticsDiary_30day_Clicked")
                        filter.emit(MoodFilter.THIRTY_DAY)
                    }
                    R.id.ninety_day -> {
                        logEvent("StatisticsDiary_90day_Clicked")
                        filter.emit(MoodFilter.NINETY_DAY)
                    }
                }
            }
            popup.dismiss()
            true
        }
        popup.show()
    }
}
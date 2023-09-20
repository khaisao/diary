package com.sutech.diary.view.calendar

import android.content.Context
import android.graphics.Paint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.sutech.diary.adapter.AdapterDiaryItem
import com.sutech.diary.adapter.CalendarAdapter
import com.sutech.diary.base.BaseFragment
import com.sutech.diary.database.DiaryDatabase
import com.sutech.diary.model.DiaryModel
import com.sutech.diary.util.Common
import com.sutech.diary.util.Constant
import com.sutech.diary.util.DialogUtil
import com.sutech.diary.util.gone
import com.sutech.diary.util.setOnClick
import com.sutech.diary.util.show
import com.sutech.journal.diary.diarywriting.lockdiary.R
import kotlinx.android.synthetic.main.fragment_calendar.btn_back
import kotlinx.android.synthetic.main.fragment_calendar.btn_backMonth
import kotlinx.android.synthetic.main.fragment_calendar.btn_nextMonth
import kotlinx.android.synthetic.main.fragment_calendar.layoutAdsCalendar
import kotlinx.android.synthetic.main.fragment_calendar.rcv_calendar
import kotlinx.android.synthetic.main.fragment_calendar.rcv_diary
import kotlinx.android.synthetic.main.fragment_calendar.tvTimeDay
import kotlinx.android.synthetic.main.fragment_calendar.tv_curenTime
import kotlinx.android.synthetic.main.fragment_calendar.tv_today
import kotlinx.android.synthetic.main.fragment_home.icNotFoundDiarySad
import kotlinx.android.synthetic.main.fragment_home.ivNotFoundDiary
import kotlinx.android.synthetic.main.fragment_home.tvNotFoundDiary
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Calendar

class CalendarFragment : BaseFragment(R.layout.fragment_calendar), CalendarAdapter.OnDateItemListener {
    private var adapterDiary: AdapterDiaryItem? = null
    private var calendarAdapter: CalendarAdapter? = null
    private val arrDiary: MutableList<DiaryModel> = ArrayList()
    private var diaryDataBase: DiaryDatabase? = null
    private var mcontext: Context? = null

    companion object {
        var selectedDate: LocalDate = LocalDate.now()
        fun checkListDiary(date: LocalDate, context: Context): ArrayList<Int> {
            val diaryDataBase2 = DiaryDatabase.getInstance(context)
            val yearMonth = YearMonth.from(date)
            val daysInMonth = yearMonth.lengthOfMonth()
            val firstOfMonth: LocalDate = selectedDate.withDayOfMonth(1)!!
            val dayOfWeek = firstOfMonth.dayOfWeek.value
            val formatter = DateTimeFormatter.ofPattern(Constant.FormatdayMMYY)
            val listDiary = ArrayList<Int>()
            for (i in 1..42) {
                if (i > dayOfWeek && i <= daysInMonth + dayOfWeek) {
                    val dateTime: String = if (i - dayOfWeek <= 9) {
                        "0${(i - dayOfWeek)}-${selectedDate.format(formatter)}"
                    } else {
                        "${(i - dayOfWeek)}-${selectedDate.format(formatter)}"
                    }
                    CoroutineScope(Dispatchers.IO).launch {
                        diaryDataBase2.getDiaryDao().searchDiaryByDateTime(dateTime).let {
                            withContext(Dispatchers.Main) {
                                if (it.isNotEmpty()) {
                                    listDiary.add(i)
                                }
                            }
                        }
                    }
                }
            }
            return listDiary
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mcontext = context
    }
    override fun onDestroy() {
        super.onDestroy()
        mcontext = null
    }

    private fun initOnClick() {
        btn_back.setOnClick(500) {
            logEvent("CalendarScr_IconX_Clicked")
            onBackPress(R.id.calendarFlag)
        }
        btn_backMonth.setOnClick(500) {
            previousMonthAction()
        }
        btn_nextMonth.setOnClick(500) {
            nextMonthAction()
        }
        tv_today.setOnClick(500) {
            logEvent("CalendarScr_Today_Clicked")
            selectedDate = LocalDate.now()
            setMonthView()
            val calendar = Calendar.getInstance()
            getDataClick(calendar.get(Calendar.DAY_OF_MONTH).toString())
            calendarAdapter!!.notifyDataSetChanged()
        }
    }
    private fun setMonthView() {
        tv_curenTime.text = Common.monthYearFromDate(selectedDate)
        val daysInMonth = Common.daysInMonthArray(selectedDate)
        calendarAdapter = CalendarAdapter(false,daysInMonth, this, mcontext!!)
        rcv_calendar.layoutManager = GridLayoutManager(mcontext, 7)
        rcv_calendar.adapter = calendarAdapter
        Handler(Looper.getMainLooper()).postDelayed({
            calendarAdapter!!.notifyDataSetChanged()

        }, 1000)
    }
    private fun setRcvDiary() {
        adapterDiary = mcontext?.let {
            AdapterDiaryItem(true, it, { _, _ ->
            }, { positionDiary, positionContent ->
                logEvent("CalendarScr_Diary_Clicked")
                val bundle = Bundle()
                bundle.putString(Constant.EXTRA_DIARY, Gson().toJson(arrDiary[positionDiary]))
                bundle.putInt(Constant.EXTRA_POSITION_CONTENT, positionContent)
                gotoFrag(R.id.calendarFlag, R.id.action_calendarFlag_to_readDiaryFrag, bundle)
            }, { _, _ ->
            }, { _, _ ->
            })
        }
        rcv_diary.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        rcv_diary.adapter = adapterDiary
    }

    private fun previousMonthAction() {
        selectedDate = selectedDate.minusMonths(1)
        setMonthView()
        mcontext?.let { checkListDiary(selectedDate, it) }
    }
    private fun nextMonthAction() {
        selectedDate = selectedDate.plusMonths(1)
        setMonthView()
        mcontext?.let { checkListDiary(selectedDate, it) }
    }

    override fun initView() {
        logEvent("CalendarScr_Show")
        showAdsWithLayout("banner_calendar", layoutAdsCalendar)

        if (mcontext != null) {
            diaryDataBase = DiaryDatabase.getInstance(mcontext!!)
        }
        tv_today.paintFlags = tv_today.paintFlags or Paint.UNDERLINE_TEXT_FLAG

        selectedDate = LocalDate.now()

        setMonthView()

        setRcvDiary()

        initOnClick()

        val calendar = Calendar.getInstance()
        getDataClick(calendar.get(Calendar.DAY_OF_MONTH).toString())
    }
    private fun getDataDiary(keyword: String? = null, dateTime: String? = null) {
        Log.e("TAG", "getDataDiary: $keyword date $dateTime")
        CoroutineScope(Dispatchers.IO).launch {
            if (keyword.isNullOrBlank() && dateTime.isNullOrBlank()) {
                diaryDataBase?.getDiaryDao()?.getAllDiary()?.let {
                    withContext(Dispatchers.Main) {
                        Log.e("TAG", "getAllDiary: ${Gson().toJson(it)}")
                        Log.e("TAG", "getAllDiary: $keyword date $dateTime")
                        DialogUtil.cancelDialogLoading()
                        showNotFoundDiary(it.isEmpty())
                        updateDataDiary(it)
                        adapterDiary?.notifyDataSetChanged()
                    }
                }
            } else if (!dateTime.isNullOrBlank()) {
                diaryDataBase?.getDiaryDao()?.searchDiaryByDateTime(dateTime)?.let {
                    withContext(Dispatchers.Main) {
                        showNotFoundDiary(it.isEmpty())
                        updateDataDiary(it)
                        adapterDiary?.notifyDataSetChanged()
                        DialogUtil.cancelDialogLoading()
                    }
                }
            } else if (!keyword.isNullOrBlank()) {
                diaryDataBase?.getDiaryDao()?.searchDiaryByTitle("%$keyword%")?.let {
                    withContext(Dispatchers.Main) {
                        showNotFoundDiary(it.isEmpty())
                        updateDataDiary(it)
                        Log.e("TAG", "getDataDiary: $it")
                        adapterDiary?.notifyDataSetChanged()
                        DialogUtil.cancelDialogLoading()
                    }
                }
            }
        }
    }

    private fun showNotFoundDiary(isShow: Boolean) {
        if (isShow) {
            logEvent("DialogNoDiary_Show")
            ivNotFoundDiary?.show()
            tvNotFoundDiary?.show()
            icNotFoundDiarySad?.show()
        } else {
            ivNotFoundDiary?.gone()
            tvNotFoundDiary?.gone()
            icNotFoundDiarySad?.gone()
        }
    }

    private fun updateDataDiary(it: MutableList<DiaryModel>) {
        arrDiary.clear()
        arrDiary.addAll(it)
        adapterDiary?.submitList(arrDiary)
        adapterDiary?.notifyDataSetChanged()
    }

    override fun onDateItemClick(position: Int, dayText: String?) {
        getDataClick(dayText)
        calendarAdapter?.updatePosition(position)

    }
    private fun getDataClick(dayText: String?){
        if (dayText != "") {
            var dayTime = dayText
            if (dayText != null) {
                dayTime = if (dayText.toInt() in 1..9) {
                    "0$dayText"
                } else {
                    dayText
                }
            }
            val formatter = DateTimeFormatter.ofPattern(Constant.FormatdayMMYY)
            getDataDiary(dateTime = "$dayTime-${selectedDate.format(formatter)}")
            tvTimeDay.text = mcontext?.let {
                Common.getDay("$dayTime-${selectedDate.format(formatter)}",
                    it
                )
            } + " " + "$dayTime-${selectedDate.format(formatter)}"
        }
    }
}
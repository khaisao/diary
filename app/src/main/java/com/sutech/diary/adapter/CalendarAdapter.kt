package com.sutech.diary.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.sutech.diary.database.DiaryDatabase
import com.sutech.diary.view.calendar.CalendarFragment.Companion.checkListDiary
import com.sutech.diary.view.calendar.CalendarFragment.Companion.selectedDate
import com.sutech.diary.view.viet.VietFrag.Companion.addNewsSlectedDate
import com.sutech.journal.diary.diarywriting.lockdiary.R
import java.time.LocalDate


class CalendarAdapter(
    private val fromAddnew: Boolean,
    private val daysOfMonth: ArrayList<String>,
    private val onItemListener: OnDateItemListener,
    private val context: Context
) :
    RecyclerView.Adapter<CalendarViewHolder?>() {
    private var diaryDataBase: DiaryDatabase? = null
    private var selected = 0
    private var checkDiaryPosision = ArrayList<Int>()
    private var isStart = false

    init {
        checkDiaryPosision = if (fromAddnew) {
            ArrayList()
        } else {
            checkListDiary(LocalDate.now(), context)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updatePosition(position: Int) {
        isStart = true
        selected = position
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        diaryDataBase = DiaryDatabase.getInstance(context)
        val inflater = LayoutInflater.from(parent.context)
        val view: View = inflater.inflate(R.layout.calendar_cell, parent, false)
        val layoutParams = view.layoutParams
        layoutParams.height = (parent.height * 0.166666666).toInt()
        return CalendarViewHolder(view, onItemListener, fromAddnew, daysOfMonth)
    }

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        val currentDate = LocalDate.now()
        val currentDateStr = currentDate.dayOfMonth.toString()
        if (checkDiaryPosision.contains(position + 1)) {
            holder.dot.visibility = View.VISIBLE
        } else {
            holder.dot.visibility = View.INVISIBLE
        }

        if (!fromAddnew) {
            if (selected == position && daysOfMonth[position] != "" && isStart) {
                holder.lnCellDay.setBackgroundResource(R.drawable.bg_currenday_selected)
                holder.dayOfMonth.setTextColor(ContextCompat.getColor(context, R.color.secondary))
            } else {
                holder.lnCellDay.setBackgroundResource(R.drawable.bg_currenday_select)
                holder.dayOfMonth.setTextColor(ContextCompat.getColor(context, R.color.black))
            }
            if (daysOfMonth[position] == currentDateStr && selectedDate == LocalDate.now()) {
                holder.lnCellDay.setBackgroundResource(R.drawable.bg_currenday)
                holder.dayOfMonth.setTextColor(ContextCompat.getColor(context, R.color.white))
                holder.dot.setCardBackgroundColor(ContextCompat.getColor(context, R.color.white))
            }

            if (daysOfMonth[position] != "") {
                if (daysOfMonth[position].toInt() > currentDateStr.toInt() && selectedDate == LocalDate.now()) {
                    holder.lnCellDay.setBackgroundResource(R.drawable.bg_currenday_select)
                    holder.dayOfMonth.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.transparent_20
                        )
                    )
                }

                if (selectedDate >= LocalDate.now().plusMonths(1)) {
                    holder.lnCellDay.setBackgroundResource(R.drawable.bg_currenday_select)
                    holder.dayOfMonth.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.transparent_20
                        )
                    )
                }
            }


        } else {
            if (selected == position && daysOfMonth[position] != "") {
                holder.lnCellDay.setBackgroundResource(R.drawable.bg_currenday)
                holder.dayOfMonth.setTextColor(ContextCompat.getColor(context, R.color.white))
            } else {
                holder.lnCellDay.setBackgroundResource(R.drawable.bg_currenday_select)
                holder.dayOfMonth.setTextColor(ContextCompat.getColor(context, R.color.black))
                if (daysOfMonth[position] == currentDateStr && addNewsSlectedDate == LocalDate.now()) {
                    holder.lnCellDay.setBackgroundResource(R.drawable.bg_currenday_select)
                    holder.dayOfMonth.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.secondary
                        )
                    )
                }
                if (daysOfMonth[position] != "") {
                    if (daysOfMonth[position].toInt() > currentDateStr.toInt() && addNewsSlectedDate == LocalDate.now()) {
                        holder.lnCellDay.setBackgroundResource(R.drawable.bg_currenday_select)
                        holder.dayOfMonth.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.transparent_20
                            )
                        )
                    }
                    if (addNewsSlectedDate >= LocalDate.now().plusMonths(1)) {
                        holder.lnCellDay.setBackgroundResource(R.drawable.bg_currenday_select)
                        holder.dayOfMonth.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.transparent_20
                            )
                        )
                    }
                }
            }
        }

        holder.dayOfMonth.text = daysOfMonth[position]
    }

    override fun getItemCount(): Int {
        return daysOfMonth.size
    }

    interface OnDateItemListener {
        fun onDateItemClick(position: Int, dayText: String?)
    }
}
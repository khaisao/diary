package com.sutech.diary.adapter

import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.sutech.diary.adapter.CalendarAdapter.OnDateItemListener
import com.sutech.diary.view.calendar.CalendarFragment.Companion.selectedDate
import com.sutech.diary.view.viet.VietFrag
import com.sutech.journal.diary.diarywriting.lockdiary.R
import java.time.LocalDate


class CalendarViewHolder(itemView: View, private val onItemListener: OnDateItemListener, private val fromAddnew: Boolean, private val daysOfMonth: ArrayList<String>) :
    RecyclerView.ViewHolder(itemView), View.OnClickListener {
    val dayOfMonth: TextView
    val lnCellDay : LinearLayout
    val dot: CardView
    private val currentDateStr = LocalDate.now().dayOfMonth
    init {
        dayOfMonth = itemView.findViewById(R.id.cellDayText)
        dot = itemView.findViewById(R.id.dot)
        lnCellDay = itemView.findViewById(R.id.ln_cellDay)
        itemView.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        if(adapterPosition > -1){
            if (daysOfMonth[adapterPosition] != "") {
                if (!fromAddnew) {
                    if (daysOfMonth[adapterPosition].toInt() <= currentDateStr && selectedDate <= LocalDate.now() || selectedDate<LocalDate.now()) {
                        onItemListener.onDateItemClick(adapterPosition, dayOfMonth.text as String)
                    }
                } else {
                    if (daysOfMonth[adapterPosition].toInt() <= currentDateStr && VietFrag.addNewsSlectedDate <= LocalDate.now() || VietFrag.addNewsSlectedDate < LocalDate.now()) {
                        onItemListener.onDateItemClick(adapterPosition, dayOfMonth.text as String)
                    }
                }
            }
        }
    }
}
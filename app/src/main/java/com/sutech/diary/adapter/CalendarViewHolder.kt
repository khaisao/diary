package com.sutech.diary.adapter

import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.sutech.diary.adapter.CalendarAdapter.OnItemListener
import com.sutech.journal.diary.diarywriting.lockdiary.R


class CalendarViewHolder(itemView: View, private val onItemListener: OnItemListener) :
    RecyclerView.ViewHolder(itemView), View.OnClickListener {
    val dayOfMonth: TextView
    val lnCellDay : LinearLayout
    val dot: CardView

    init {
        dayOfMonth = itemView.findViewById(R.id.cellDayText)
        dot = itemView.findViewById(R.id.dot)
        lnCellDay = itemView.findViewById(R.id.ln_cellDay)
        itemView.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        onItemListener.onItemClick(adapterPosition, dayOfMonth.text as String)
    }
}
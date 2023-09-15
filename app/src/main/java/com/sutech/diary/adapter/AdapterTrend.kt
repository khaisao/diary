package com.sutech.diary.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sutech.diary.model.MoodObj
import com.sutech.diary.util.MoodUtil
import com.sutech.journal.diary.diarywriting.lockdiary.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AdapterTrend : RecyclerView.Adapter<AdapterTrend.TrendViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrendViewHolder {
        return TrendViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_trend, parent, false))
    }

    override fun getItemCount(): Int = MoodUtil.moods.size

    override fun onBindViewHolder(holder: TrendViewHolder, position: Int) {
        holder.binding(MoodUtil.moods[position])
    }

    inner class TrendViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @SuppressLint("SetTextI18n")
        fun binding(mood: MoodObj) {
            CoroutineScope(Dispatchers.Main).launch {
                itemView.findViewById<ImageView>(R.id.icon).setImageResource(mood.imageResource)
                itemView.findViewById<TextView>(R.id.percent).text = "${MoodUtil.calTrendPercentages(itemView.context, mood.id)} %"
            }
        }
    }
}
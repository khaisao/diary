package com.sutech.diary.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.sutech.diary.model.MoodObj
import com.sutech.journal.diary.diarywriting.lockdiary.R

class AdapterMood constructor(moods: List<MoodObj>, moodSelected: MoodSelected) : RecyclerView.Adapter<AdapterMood.MoodViewHolder>() {
    private val _moods = moods
    private val _moodSelected = moodSelected

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoodViewHolder {
        return MoodViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_mood, parent, false))
    }

    override fun getItemCount(): Int = _moods.size

    override fun onBindViewHolder(holder: MoodViewHolder, position: Int) {
        holder.binding(_moods[position])
        holder.handleEvents(_moods[position])
    }

    inner class MoodViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun binding(imageResource: MoodObj) {
            itemView.findViewById<ImageView>(R.id.mood).setImageResource(imageResource.imageResource)
        }

        fun handleEvents(imageResource: MoodObj) {
            itemView.findViewById<ImageView>(R.id.mood).setOnClickListener {
                _moodSelected.onMoodSelectedListener(imageResource)
            }
        }
    }

    interface MoodSelected {
        fun onMoodSelectedListener(mood: MoodObj)
    }
}
package com.sutech.diary.adapter.diffUtil

import androidx.recyclerview.widget.DiffUtil
import com.sutech.diary.model.MoodObj

class MoodDiffCallBack : DiffUtil.ItemCallback<MoodObj>() {
    override fun areItemsTheSame(oldItem: MoodObj, newItem: MoodObj): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: MoodObj, newItem: MoodObj): Boolean {
        return oldItem == newItem
    }
}
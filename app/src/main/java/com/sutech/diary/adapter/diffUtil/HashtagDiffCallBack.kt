package com.sutech.diary.adapter.diffUtil

import androidx.recyclerview.widget.DiffUtil
import com.sutech.diary.model.Hashtag

class HashtagDiffCallBack : DiffUtil.ItemCallback<Hashtag>() {
    override fun areItemsTheSame(oldItem: Hashtag, newItem: Hashtag): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Hashtag, newItem: Hashtag): Boolean {
        return oldItem == newItem
    }
}
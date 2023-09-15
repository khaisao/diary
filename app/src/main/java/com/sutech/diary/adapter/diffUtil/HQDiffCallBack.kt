package com.sutech.diary.adapter.diffUtil

import androidx.recyclerview.widget.DiffUtil
import com.sutech.diary.model.HashtagQuantity

class HQDiffCallBack : DiffUtil.ItemCallback<HashtagQuantity>() {
    override fun areItemsTheSame(oldItem: HashtagQuantity, newItem: HashtagQuantity): Boolean {
        return oldItem.hashtag == newItem.hashtag
    }

    override fun areContentsTheSame(oldItem: HashtagQuantity, newItem: HashtagQuantity): Boolean {
        return oldItem == newItem
    }
}
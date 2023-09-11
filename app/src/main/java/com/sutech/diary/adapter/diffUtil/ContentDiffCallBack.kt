package com.sutech.diary.adapter.diffUtil

import androidx.recyclerview.widget.DiffUtil
import com.sutech.diary.model.ContentModel

class ContentDiffCallBack : DiffUtil.ItemCallback<ContentModel>() {
    override fun areItemsTheSame(oldItem: ContentModel, newItem: ContentModel): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: ContentModel, newItem: ContentModel): Boolean {
        return oldItem == newItem
    }
}
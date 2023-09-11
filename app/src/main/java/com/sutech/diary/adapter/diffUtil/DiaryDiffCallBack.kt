package com.sutech.diary.adapter.diffUtil

import androidx.recyclerview.widget.DiffUtil
import com.sutech.diary.model.DiaryModel

class DiaryDiffCallBack : DiffUtil.ItemCallback<DiaryModel>() {
    override fun areItemsTheSame(oldItem: DiaryModel, newItem: DiaryModel): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: DiaryModel, newItem: DiaryModel): Boolean {
        return oldItem == newItem
    }
}
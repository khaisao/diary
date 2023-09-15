package com.sutech.diary.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sutech.diary.adapter.diffUtil.HashtagDiffCallBack
import com.sutech.diary.model.Hashtag
import com.sutech.journal.diary.diarywriting.lockdiary.databinding.ItemHashtagInputBinding

class AdapterInputHashtag: ListAdapter<Hashtag, RecyclerView.ViewHolder>(HashtagDiffCallBack()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemHashtagInputBinding.inflate(inflater, parent, false)
        return ViewHolderDiary(binding)
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolderDiary).binDataDiary(currentList[position])
    }

    override fun submitList(list: MutableList<Hashtag>?) {
        super.submitList(list?.toList())
    }


    inner class ViewHolderDiary(val binding:ItemHashtagInputBinding) : RecyclerView.ViewHolder(binding.root) {
        fun binDataDiary(item: Hashtag) {

        }
    }

}
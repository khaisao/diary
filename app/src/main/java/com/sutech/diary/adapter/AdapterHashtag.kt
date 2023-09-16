package com.sutech.diary.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.sutech.diary.adapter.diffUtil.HQDiffCallBack
import com.sutech.diary.model.HashtagQuantity
import com.sutech.diary.util.setOnClick
import com.sutech.journal.diary.diarywriting.lockdiary.R
import com.sutech.journal.diary.diarywriting.lockdiary.databinding.ItemHashtagBinding

class AdapterHashtag constructor(private val hashtagOnclickListener: HashtagOnclickListener) : ListAdapter<HashtagQuantity, AdapterHashtag.HashtagViewModel>(HQDiffCallBack()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HashtagViewModel {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemHashtagBinding.inflate(inflater, parent, false)
        return HashtagViewModel(binding)
    }

    override fun onBindViewHolder(holder: HashtagViewModel, position: Int) {
        holder.binding(currentList[position])
        holder.handleEvent(currentList[position])
    }

    inner class HashtagViewModel(private val binding: ItemHashtagBinding) : ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun binding(hashtagQuantity: HashtagQuantity) {
            binding.hashtag.text = hashtagQuantity.hashtag
            binding.quantity.text = "${hashtagQuantity.quantity} ${binding.root.resources.getString(R.string.diary)}"
        }

        fun handleEvent(hashtagQuantity: HashtagQuantity) {
            binding.root.setOnClick(500) {
                hashtagOnclickListener.onHashtagClick(hashtagQuantity.hashtag)
            }
        }
    }

    interface HashtagOnclickListener {
        fun onHashtagClick(hashtag: String)
    }
}
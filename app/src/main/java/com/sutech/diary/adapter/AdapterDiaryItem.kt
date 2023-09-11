package com.sutech.diary.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.sutech.diary.adapter.diffUtil.DiaryDiffCallBack
import com.sutech.diary.model.DiaryModel
import com.sutech.journal.diary.diarywriting.lockdiary.R
import kotlinx.android.synthetic.main.item_diary.view.*

private const val TAG = "AdapterDiaryItem"

class AdapterDiaryItem(
    private val onClickMore: (positionDiary: Int, position: Int) -> Unit,
    private val onClickItem: (positionDiary: Int, positionContent: Int) -> Unit,
    private val onClickEdit: (position: Int, positionContent: Int) -> Unit,
    private val onClickDelete: (position: Int, positionContent: Int) -> Unit
) : ListAdapter<DiaryModel, ViewHolder>(DiaryDiffCallBack()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolderDiary(inflater.inflate(R.layout.item_diary, parent, false))
    }

    private lateinit var adapterContent: AdapterContent
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        (holder as ViewHolderDiary).binDataDiary(position, currentList[position])
    }

    inner class ViewHolderDiary(view: View) : RecyclerView.ViewHolder(view) {
        fun binDataDiary(position: Int, diary: DiaryModel) {
            adapterContent = AdapterContent ({
                onClickItem(position,it)
            },{
                onClickMore(position,it)
            },{
                onClickEdit(position, it)
            },{
                onClickDelete(position, it)
            })
            adapterContent.submitList(diary.listContent)
            itemView.rcvContent.layoutManager =  LinearLayoutManager(itemView.context, RecyclerView.VERTICAL, false)
            itemView.rcvContent.adapter = adapterContent
            itemView.tvDay.text = diary.dateTime

        }
    }
}
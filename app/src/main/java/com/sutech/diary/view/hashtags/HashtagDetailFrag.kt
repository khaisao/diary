package com.sutech.diary.view.hashtags

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.sutech.diary.adapter.AdapterDiaryItem
import com.sutech.diary.base.BaseFragment
import com.sutech.diary.database.DiaryDatabase
import com.sutech.diary.model.DiaryModel
import com.sutech.diary.util.Constant
import com.sutech.diary.util.Constant.EXTRA_DIARY
import com.sutech.diary.util.Constant.EXTRA_POSITION_CONTENT
import com.sutech.diary.util.setOnClick
import com.sutech.journal.diary.diarywriting.lockdiary.R
import kotlinx.android.synthetic.main.fragment_hashtag_detail.btnBack
import kotlinx.android.synthetic.main.fragment_hashtag_detail.rcvData
import kotlinx.android.synthetic.main.fragment_hashtag_detail.top_app_bar_title
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HashtagDetailFrag : BaseFragment(R.layout.fragment_hashtag_detail) {
    private val arrDiary: MutableList<DiaryModel> = ArrayList()
    private var adapterDiary: AdapterDiaryItem? = null
    private var title: String? = null
    override fun initView() {
        getDataDiary()
        setupUi()
        handleEvent()
    }

    private fun getDataDiary() {
        lifecycleScope.launch(Dispatchers.IO) {
            DiaryDatabase.getInstance(requireContext()).getDiaryDao().getAllDiary().map {
                it.copy(listContent = it.listContent?.filter { contentModel -> contentModel.listHashtag.contains(title) }?.let { it1 -> ArrayList(it1) })
            }.filter { it.listContent?.isNotEmpty() == true }.let {
                withContext(Dispatchers.Main) {
                    updateDataDiary(it)
                }
            }
        }
    }

    private fun setupUi() {
        title = arguments?.getString(Constant.EXTRA_HASHTAG)
        top_app_bar_title.text = title
        setupRV()
    }

    private fun setupRV() {
        adapterDiary = AdapterDiaryItem(false, requireContext(), { _, _ ->
            logEvent("MainScr_IconDots_Clicked")
        }, { positionDiary, positionContent ->
            val bundle = Bundle()
            bundle.putString(EXTRA_DIARY, Gson().toJson(arrDiary[positionDiary]))
            bundle.putInt(EXTRA_POSITION_CONTENT, positionContent)
            gotoFrag(R.id.hashtagDetailFrag, R.id.action_hashtagDetailFrag_to_readDiaryFrag, bundle)
        }, { _, _ -> }, { _, _ -> })
        rcvData.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        rcvData.adapter = adapterDiary
    }

    private fun handleEvent() {
        btnBack.setOnClick(500) {
            onBackPress(R.id.hashtagDetailFrag)
        }
    }

    private fun updateDataDiary(it: List<DiaryModel>) {
        arrDiary.clear()
        arrDiary.addAll(it)
        adapterDiary?.submitList(arrDiary)
    }
}
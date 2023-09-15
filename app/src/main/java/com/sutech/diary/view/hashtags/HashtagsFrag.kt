package com.sutech.diary.view.hashtags

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.sutech.diary.adapter.AdapterHashtag
import com.sutech.diary.adapter.decoration.DividerItemDecoration
import com.sutech.diary.base.BaseFragment
import com.sutech.diary.util.Constant
import com.sutech.diary.util.HashtagUtil
import com.sutech.diary.util.setOnClick
import com.sutech.journal.diary.diarywriting.lockdiary.R
import kotlinx.android.synthetic.main.fragment_hashtags.btnBack
import kotlinx.android.synthetic.main.fragment_hashtags.hashtagsRV
import kotlinx.coroutines.launch

class HashtagsFrag : BaseFragment(R.layout.fragment_hashtags) {
    override fun initView() {
        setupView()
        handleEvent()
    }

    private fun handleEvent() {
        btnBack.setOnClick(500) {
            onBackToHome(R.id.hashtagsFrag)
        }
    }

    private fun setupView() {
        lifecycleScope.launch {
            val htQuantity = HashtagUtil.getHashtagQuantities(requireContext())
            val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            hashtagsRV.adapter = AdapterHashtag(object : AdapterHashtag.HashtagOnclickListener {
                override fun onHashtagClick(hashtag: String) {
                    val bundle = Bundle()
                    bundle.putString(Constant.EXTRA_HASHTAG, hashtag)
                    gotoFrag(R.id.hashtagsFrag, R.id.action_hashtagsFrag_to_hashtagDetailFrag, bundle)
                }
            }).also {
                it.submitList(htQuantity)
            }
            hashtagsRV.addItemDecoration(DividerItemDecoration(requireContext(), R.drawable.divider))
            hashtagsRV.layoutManager = layoutManager
        }
    }
}
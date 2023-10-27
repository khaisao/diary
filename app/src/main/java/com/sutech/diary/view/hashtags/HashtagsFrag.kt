package com.sutech.diary.view.hashtags

import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.sutech.diary.adapter.AdapterHashtag
import com.sutech.diary.adapter.decoration.DividerItemDecoration
import com.sutech.diary.base.BaseFragment
import com.sutech.diary.util.Constant
import com.sutech.diary.util.HashtagUtil
import com.sutech.diary.util.ImageUtil
import com.sutech.diary.util.setOnClick
import com.sutech.journal.diary.diarywriting.lockdiary.R
import kotlinx.android.synthetic.main.fragment_hashtags.btnBack
import kotlinx.android.synthetic.main.fragment_hashtags.empty_image
import kotlinx.android.synthetic.main.fragment_hashtags.empty_title
import kotlinx.android.synthetic.main.fragment_hashtags.hashtagsRV
import kotlinx.android.synthetic.main.fragment_hashtags.iv_theme
import kotlinx.coroutines.launch

class HashtagsFrag : BaseFragment(R.layout.fragment_hashtags) {
    override fun initView() {
        logEvent("HashtagsScr_Show")
        setupView()
        handleEvent()
    }

    private fun handleEvent() {
        btnBack.setOnClick(500) {
            logEvent("HashtagsScr_IconBack_Clicked")
            onBackToHome(R.id.hashtagsFrag)
        }
    }

    private fun setupView() {
        ImageUtil.setImage(empty_image, R.drawable.empty_box)
        ImageUtil.setThemeForImageView(iv_theme, requireContext())

        lifecycleScope.launch {
            val htQuantity = HashtagUtil.getHashtagQuantities(requireContext()).also {
                empty_image.visibility = if (it.isEmpty()) View.VISIBLE else View.GONE
                empty_title.visibility = if (it.isEmpty()) View.VISIBLE else View.GONE
            }
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
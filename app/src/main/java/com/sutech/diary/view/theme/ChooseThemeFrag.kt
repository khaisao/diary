package com.sutech.diary.view.theme

import com.sutech.journal.diary.diarywriting.lockdiary.R
import com.sutech.diary.base.BaseFragment
import com.sutech.diary.model.ThemeObj

class ChooseThemeFrag:BaseFragment(R.layout.fragment_choose_theme) {
    val listTheme = arrayListOf<ThemeObj>()
    val adapter =BannerThemeAdapter(listTheme) {

    }

    override fun initView() {
        initOnClick()
        initSlider()
    }
}
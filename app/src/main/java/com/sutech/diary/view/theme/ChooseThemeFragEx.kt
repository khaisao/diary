package com.sutech.diary.view.theme

import androidx.core.content.res.ResourcesCompat
import com.test.dialognew.setPreventDoubleClick
import com.sutech.journal.diary.diarywriting.lockdiary.R
import com.sutech.diary.base.AppController
import com.sutech.diary.database.DataStore
import com.sutech.diary.model.ThemeObj
import com.youth.banner.transformer.RotateDownPageTransformer
import kotlinx.android.synthetic.main.fragment_choose_theme.*


fun ChooseThemeFrag.initOnClick() {
    btnThemeBack?.setPreventDoubleClick(500){
        onBackPress(R.id.themeFrag)
    }
    btnSaveTheme?.setPreventDoubleClick(500){
        vpChooseTheme?.let {
            context?.let { ctx ->
                val currentPosition = it.currentItem
                AppController.getInstance().setThemeApp(currentPosition - 1, requireContext())
                DataStore.saveTheme(currentPosition - 1)
                val intent = this.activity?.intent
                this.activity?.finish()
                startActivity(intent)
            }
        }
    }
}

fun ChooseThemeFrag.initSlider() {
    listTheme.addAll(listOf(
        ThemeObj(textColor = ResourcesCompat.getColor(this.resources, R.color.darkTextColor, null), imageResource = R.drawable.default_background),
        ThemeObj(textColor = ResourcesCompat.getColor(this.resources, R.color.whiteTextColor, null), imageResource = R.drawable.theme_1_background),
        ThemeObj(textColor = ResourcesCompat.getColor(this.resources, R.color.whiteTextColor, null), imageResource = R.drawable.theme_2_background),
        ThemeObj(textColor = ResourcesCompat.getColor(this.resources, R.color.whiteTextColor, null), imageResource = R.drawable.theme_3_background),
        ThemeObj(textColor = ResourcesCompat.getColor(this.resources, R.color.darkTextColor, null), imageResource = R.drawable.theme_4_background),
        ThemeObj(textColor = ResourcesCompat.getColor(this.resources, R.color.darkTextColor, null), imageResource = R.drawable.theme_5_background),
        ThemeObj(textColor = ResourcesCompat.getColor(this.resources, R.color.darkTextColor, null), imageResource = R.drawable.theme_6_background),
        ThemeObj(textColor = ResourcesCompat.getColor(this.resources, R.color.darkTextColor, null), imageResource = R.drawable.theme_7_background),
    ))

    vpChooseTheme?.let {

        it.isAutoLoop(false)
        it.addBannerLifecycleObserver(this)
//        it.indicator = RectangleIndicator(context)
//        it.setIndicatorSelectedWidth(BannerUtils.dp2px(24f).toInt())
//        it.setIndicatorSpace(BannerUtils.dp2px(4f).toInt())
//            it.setBannerRound(20f)
//        it.setPageTransformer(ZoomOutPageTransformer())

        //添加画廊效果
        it.setBannerGalleryEffect(40, 16)
//        it.setBannerGalleryMZ(40)
        it.setPageTransformer(RotateDownPageTransformer())
//        it.setPageTransformer(ZoomOutPageTransformer())
        it.adapter = adapter
    }
}

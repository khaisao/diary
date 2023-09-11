package com.sutech.diary.view.theme

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
            context?.let {
                    ctx->
                val currentPosition = it.currentItem
                AppController.getInstance().setThemeApp(currentPosition-1)
                DataStore.saveTheme(currentPosition-1)
                onBackPress(R.id.themeFrag)
            }
        }
    }
}
fun ChooseThemeFrag.initSlider() {
    listTheme.add(
        ThemeObj(
            textColor = "#000000",
            colorSelected = "#000000",
            colorEditTex = "#252525",
            colorTextSecond = "#000000",
            backgroundColor = "#F2F2F2",
            iconColor = "#252525"
        )
    )

    listTheme.add(
        ThemeObj(
            textColor = "#F2F2F2",
            colorSelected = "#febd00",
            colorEditTex = "#F2F2F2",
            colorTextSecond = "#163DFF",
            backgroundColor = "#000000",
            iconColor = "#F2F2F2"
        )
    )


    listTheme.add(
        ThemeObj(
            textColor = "#F2F2F2",
            colorSelected = "#febd00",
            colorEditTex = "#F2F2F2",
            colorTextSecond = "#163DFF",
            backgroundColor = "#304FFE",
            iconColor = "#000000"
        )
    )

    listTheme.add(
        ThemeObj(
            textColor = "#F2F2F2",
            colorSelected = "#febd00",
            colorEditTex = "#F2F2F2",
            colorTextSecond = "#163DFF",
            backgroundColor = "#D50000",
            iconColor = "#febd00"
        )
    )

    listTheme.add(
        ThemeObj(
            textColor = "#F2F2F2",
            colorSelected = "#febd00",
            colorEditTex = "#F2F2F2",
            colorTextSecond = "#163DFF",
            backgroundColor = "#00C853",
            iconColor = "#febd00"
        )
    )

    listTheme.add(
        ThemeObj(
            textColor = "#F2F2F2",
            colorSelected = "#febd00",
            colorEditTex = "#F2F2F2",
            colorTextSecond = "#163DFF",
            backgroundColor = "#AA00FF",
            iconColor = "#000000"
        )
    )

    listTheme.add(
        ThemeObj(
            textColor = "#000000",
            colorSelected = "#febd00",
            colorEditTex = "#F2F2F2",
            colorTextSecond = "#163DFF",
            backgroundColor = "#FFAB00",
            iconColor = "#000000"
        )
    )

    listTheme.add(
        ThemeObj(
            textColor = "#000000",
            colorSelected = "#febd00",
            colorEditTex = "#F2F2F2",
            colorTextSecond = "#163DFF",
            backgroundColor = "#FF6D00",
            iconColor = "#000000"
        )
    )

    listTheme.add(
        ThemeObj(
            textColor = "#000000",
            colorSelected = "#febd00",
            colorEditTex = "#000000",
            colorTextSecond = "#163DFF",
            backgroundColor = "#F67FB5",
            iconColor = "#F2F2F2"
        )
    )

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

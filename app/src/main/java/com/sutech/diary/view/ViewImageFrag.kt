package com.sutech.diary.view

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sutech.journal.diary.diarywriting.lockdiary.R
import com.sutech.diary.adapter.ViewPagerAdapter
import com.sutech.diary.base.BaseFragment
import com.sutech.diary.model.ImageObj
import com.sutech.diary.util.Constant.EXTRA_ARRAY_IMAGE
import com.sutech.diary.util.Constant.EXTRA_POSITION_IMAGE
import com.sutech.diary.util.gone
import com.sutech.diary.util.setOnClickScaleView
import kotlinx.android.synthetic.main.fragment_view_image.*
import java.lang.reflect.Type

class ViewImageFrag : BaseFragment( R.layout.fragment_view_image) {

    private var arrImageObj: ArrayList<ImageObj> = ArrayList()
    private var positionImage = 0
    private val viewpagerAdapter = ViewPagerAdapter(arrImageObj)


    override fun initView() {
        tbViewImageBack.setOnClickScaleView {
            onBackPress(R.id.viewImageFrag)
        }
        setVpImage()
        getDataBundle()

    }

    private fun getDataBundle() {
        arguments?.let {
            val stringImage = arguments?.getString(EXTRA_ARRAY_IMAGE)
            positionImage = arguments?.getInt(EXTRA_POSITION_IMAGE, 0)!!

            if (!stringImage.isNullOrBlank()) {
                val listType: Type = object : TypeToken<ArrayList<ImageObj?>?>() {}.type
                arrImageObj.addAll(Gson().fromJson(stringImage, listType))
                vpImage?.postDelayed({
                    context?.let {
                        vpImage?.currentItem = positionImage
                    }

                },100)
                imgGoneView?.postDelayed({
                    context?.let {
                        imgGoneView?.gone()
                    }
                },500)
            }
        }
    }

    private fun setVpImage() {
        vpImage?.adapter = viewpagerAdapter
    }
}
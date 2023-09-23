package com.sutech.diary.view.chooseImage

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sutech.diary.base.BaseFragment
import com.sutech.diary.model.ImageObj
import com.sutech.diary.util.AppUtil
import com.sutech.diary.util.DeviceUtil
import com.sutech.diary.util.setOnClickScaleView
import com.sutech.diary.view.chooseImage.adapter.AdapterChooseImage
import com.sutech.journal.diary.diarywriting.lockdiary.R
import kotlinx.android.synthetic.main.activity_choose_image.rcvChooseImage
import kotlinx.android.synthetic.main.toolbar_choose_image.btnDone
import kotlinx.android.synthetic.main.toolbar_choose_image.tbBack

class ChooseImageAct :  BaseFragment(R.layout.activity_choose_image) {
    private var arrImageObj: ArrayList<ImageObj> = ArrayList()
    private var arrImageSelected: ArrayList<ImageObj> = ArrayList()
    private lateinit var adapterImage: AdapterChooseImage

    override fun initView() {
        logEvent("PhotoScr_Show")
        tbBack.setOnClickScaleView {
            logEvent("PhotoScr_Iconcancel_Clicked")
            DeviceUtil.arrImage.clear()
            onBackPress(R.id.chooseImageAct)
        }
        btnDone.setOnClickScaleView {
            logEvent("PhotoScr_IconOk_Clicked")
            if (arrImageSelected.isNotEmpty()) {
                DeviceUtil.arrImage.clear()
                DeviceUtil.arrImage.addAll(arrImageSelected)
                onBackPress(R.id.chooseImageAct)
            } else {
                AppUtil.showToast(context, R.string.please_choose_image)
            }
        }
        setRcvImage()
    }

    override fun onResume() {
        super.onResume()
        getAllImage()
    }

    private fun setRcvImage() {
        adapterImage = AdapterChooseImage(arrImageObj) {
            val video = arrImageObj[it]
            if (video.isSelected) {
                arrImageSelected.remove(video)

                arrImageObj[it].isSelected = !arrImageObj[it].isSelected
                adapterImage.notifyItemChanged(it)
            } else {
                if (arrImageSelected.size < 10) {
                    arrImageSelected.add(video)
                    arrImageObj[it].isSelected = !arrImageObj[it].isSelected
                    adapterImage.notifyItemChanged(it)
                } else {
                    AppUtil.showToast(context, R.string.max_image_selected)
                }

            }
        }
        rcvChooseImage.layoutManager = GridLayoutManager(context, 3, RecyclerView.VERTICAL, false)
        rcvChooseImage.adapter = adapterImage
    }

    private fun getAllImage() {
        arrImageObj.clear()
        if (DeviceUtil.getAllImage(requireContext()) != null) {
            arrImageObj.addAll(DeviceUtil.getAllImage(requireContext())!!)
        }
        adapterImage.notifyDataSetChanged()
    }
}
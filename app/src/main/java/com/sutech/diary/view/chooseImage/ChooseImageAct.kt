package com.sutech.diary.view.chooseImage

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sutech.diary.base.BaseFragment
import com.sutech.journal.diary.diarywriting.lockdiary.R
import com.sutech.diary.model.ImageObj
import com.sutech.diary.util.AppUtil
import com.sutech.diary.util.DeviceUtil
import com.sutech.diary.util.setOnClickScaleView
import com.sutech.diary.view.chooseImage.adapter.AdapterChooseImage
import kotlinx.android.synthetic.main.activity_choose_image.*
import kotlinx.android.synthetic.main.toolbar_choose_image.*

class ChooseImageAct :  BaseFragment(R.layout.activity_choose_image) {
    private val STORAGE_REQUEST = 100
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
        requestStoragePermission()

    }

    override fun onResume() {
        super.onResume()
    }

    private fun requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    STORAGE_REQUEST
                )
            }
        } else {

            getAllImage()

        }
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


    //
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {

        when (requestCode) {
            STORAGE_REQUEST -> {
                if (grantResults.isNotEmpty()
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    getAllImage()
                } else {
                    Toast.makeText(
                        context,
                        "You must grant a write storage permission to use this functionality",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

}
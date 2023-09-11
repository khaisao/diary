package com.sutech.diary.view

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.datatransport.cct.internal.LogEvent
import com.sutech.diary.base.BaseFragment
import com.sutech.journal.diary.diarywriting.lockdiary.R
import com.sutech.diary.model.ImageObj
import com.sutech.diary.util.DeviceUtil
import nv.module.brushdraw.ui.BrushUtils

class VeAct : BaseFragment(R.layout.activity_ve) {

    override fun initView() {
        logEvent("DrawScr_Show")
        initBrush()
        brushUtils?.setColorBrush( ContextCompat.getColor(requireContext(),R.color.color_black))

    }

    var brushUtils: BrushUtils? = null
    private fun initBrush() {
        brushUtils = BrushUtils(requireContext(), { bitmap ->
            //save
            logEvent("DrawScrScr_IconOk_Clicked")
            DeviceUtil.arrVe.clear()
            DeviceUtil.arrVe.add(ImageObj(id="bitmap${System.currentTimeMillis() / 1000}", path = null,bitmap = bitmap,isSelected = false ))
            onBackPress(R.id.veAct)
        }, {
            //back

            logEvent("DrawScrScr_Iconcancel_Clicked")
            DeviceUtil.arrVe.clear()
            onBackPress(R.id.veAct)
        })
        view?.let {
            v->
            brushUtils?.createBrush(
           v.findViewById(R.id.btnUndo),
           v.findViewById(R.id.btnRedo),
           v.findViewById(R.id.btnBack),
           v.findViewById(R.id.btnSave),
           v.findViewById(R.id.canvasView)
            )
        }
    }

}
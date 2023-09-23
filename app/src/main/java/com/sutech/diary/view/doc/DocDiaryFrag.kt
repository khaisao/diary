package com.sutech.diary.view.doc

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.addCallback
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.sutech.diary.adapter.AdapterImageContent
import com.sutech.diary.base.BaseFragment
import com.sutech.diary.database.DiaryDatabase
import com.sutech.diary.model.ContentModel
import com.sutech.diary.model.DiaryModel
import com.sutech.diary.util.*
import com.sutech.diary.view.viet.InputHashtagView
import com.sutech.journal.diary.diarywriting.lockdiary.R
import kotlinx.android.synthetic.main.fragment_doc_diary.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DocDiaryFrag : BaseFragment(R.layout.fragment_doc_diary) {
    private var diaryModel: DiaryModel? = null
    private var positionContent: Int = -1
    private var contentModel: ContentModel? = null
    private var adapterImage: AdapterImageContent? = null
    private lateinit var dirayDataBase: DiaryDatabase


    override fun initView() {
        AppUtil.readiary++

        dirayDataBase = DiaryDatabase.getInstance(requireContext())

        activity?.onBackPressedDispatcher?.addCallback(this, true) {
            backReader()
        }
        logEvent("ViewDiary_Show")
        showAdsWithLayout("banner_read_nhat_ky", layoutAdsDoc)
        setOnClickView()
        getDataBundle()
        setRcvDiary()
    }

    private fun setOnClickView() {
        btnWriteBack.setOnClickScaleView {
            backReader()
        }
        btnEditReaDia.setOnClickScaleView {
            logEvent("ViewDiary_IconPen_Clicked")
            val bundle = Bundle()
            bundle.putString(Constant.EXTRA_DIARY, Gson().toJson(diaryModel))
            bundle.putInt(Constant.EXTRA_POSITION_CONTENT, positionContent)
            gotoFrag(R.id.readDiaryFrag, R.id.action_readDiaryFrag_to_writeFrag, bundle)
        }
        btnDeleteReaDia.setOnClickScaleView {
            logEvent("ViewDiary_IconDelete_Clicked")
            onClickDeleteDiary()
        }

    }

    private fun backReader() {
        context?.let {
            onBackPress(R.id.readDiaryFrag)
        }
    }

    private fun onClickDeleteDiary() {
        context?.let { ctx ->
            DialogUtil.showDialogAlert(ctx, getString(R.string.confirm_delete_diary), {
                diaryModel?.listContent?.removeAt(positionContent)
                CoroutineScope(Dispatchers.IO).launch {
                    if (diaryModel?.listContent?.size != 0) {
                        dirayDataBase.getDiaryDao().updateDiary(diaryModel!!).let {
                            withContext(Dispatchers.Main) {
                                AppUtil.needUpdateDiary = true
                                AppUtil.showToast(ctx, R.string.delete_diary_success)
                                onBackPress(R.id.readDiaryFrag)
                            }
                        }
                    } else {
                        dirayDataBase.getDiaryDao().deleteDiary(diaryModel!!).let {
                            withContext(Dispatchers.Main) {
                                AppUtil.needUpdateDiary = true
                                AppUtil.showToast(ctx, R.string.delete_diary_success)
                                onBackPress(R.id.readDiaryFrag)
                            }
                        }
                    }
                }
            }, {})
        }

    }

    private fun getDataBundle() {
        if (arguments != null) {
            val stringArrDiary = arguments?.getString(Constant.EXTRA_DIARY)
            positionContent = arguments?.getInt(Constant.EXTRA_POSITION_CONTENT, 0)!!

            diaryModel = Gson().fromJson(stringArrDiary, DiaryModel::class.java)
            contentModel = diaryModel?.listContent?.get(positionContent)
        }
    }

    override fun onResume() {
        super.onResume()
        setDataDiary()
    }

    @SuppressLint("SetTextI18n")
    private fun setDataDiary() {
        tvDiaryTitle?.text = contentModel?.title
        tvDiaryCreateAt?.text = contentModel?.dateTimeCreate
        tvDiaryContent?.text = contentModel?.content
        if (contentModel != null) {
            for (item in contentModel?.listHashtag!!) {
                val view = InputHashtagView(requireContext())
                view_hashtag.addView(view)
                view.setRequestHashtagInput(false)
                view.setEnableHashtagInput(false)
                view.setTextForHashtag(item)
            }
        }
    }

    private fun setRcvDiary() {
        contentModel?.let {
            if (it.images.isEmpty()) {
                rcvDiaryImage?.gone()
            } else {
                rcvDiaryImage?.show()
            }
        }

        contentModel?.images?.let {
            adapterImage = AdapterImageContent(
                it,
                { position ->
                    val bundle = Bundle()
                    bundle.putString(
                        Constant.EXTRA_ARRAY_IMAGE,
                        Gson().toJson(contentModel?.images)
                    )
                    bundle.putInt(Constant.EXTRA_POSITION_IMAGE, position)
                    gotoFrag(R.id.readDiaryFrag, R.id.action_readDiaryFrag_to_viewImageFrag, bundle)
                },
                {
                    //                on delete
                })
            adapterImage?.isView = true
            rcvDiaryImage?.layoutManager =
                LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            rcvDiaryImage?.adapter = adapterImage
        }
    }

}
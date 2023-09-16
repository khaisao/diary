package com.sutech.diary.view.doc

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.activity.addCallback
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.sutech.diary.adapter.AdapterImageContent
import com.sutech.diary.base.BaseFragment
import com.sutech.diary.database.DataStore
import com.sutech.diary.database.DiaryDatabase
import com.sutech.diary.model.ContentModel
import com.sutech.diary.model.DiaryModel
import com.sutech.diary.util.*
import com.sutech.diary.util.AppUtil.openMarket
import com.sutech.diary.util.AppUtil.sendEmailMore
import com.sutech.diary.view.viet.InputHashtagView
import com.sutech.journal.diary.diarywriting.lockdiary.R
import kotlinx.android.synthetic.main.fragment_doc_diary.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DocDiaryFrag : BaseFragment(R.layout.fragment_doc_diary) {
    var diaryModel: DiaryModel? = null
    var positionContent: Int = -1
    var contentModel: ContentModel? = null
    var adapterImage: AdapterImageContent? = null
    private lateinit var dirayDataBase: DiaryDatabase


    override fun initView() {
        AppUtil.readiary ++

        dirayDataBase = DiaryDatabase.getInstance(requireContext())

        activity?.onBackPressedDispatcher?.addCallback(this, true) {
            backReader()
        }
        logEvent("ViewDiary_Show")
        showBanner("banner_viet_nhat_ky", layoutAdsDoc)
        setOnClickView()
        getDataBundle()
        setRcvDiary()

        btnRate?.setPreventDoubleClickScaleView(1000) {
            context?.let { ctx ->

                when (numRate) {
                    4, 3 -> {
                        logEvent("ViewDiary_Mailtous_Clicked")
                    }
                    5 -> {
                        logEvent("ViewDiary_Googleplay_Clicked")
                    }
                    2, 1 -> {
                        logEvent("ViewDiary_Mailtous_Clicked")
                    }
                    else -> {
                        logEvent("ViewDiary_Remindlater_Clicked")
                    }
                }
                if (numRate < 5) {
                    DataStore.setRated(true)
                    rlRateApp?.gone()
                    sendEmailMore(
                        ctx,
                        arrayOf("Sutechmobile@gmail.com"),
                        "Feedback to My Diary",
                        ""
                    )
                } else if (numRate == 5) {
                    DataStore.setRated(true)
                    openMarket(ctx, requireActivity().packageName)
                    rlRateApp.gone()
                } else {
                    rlRateApp.gone()
                }
            }
        }

        if (!DataStore.checkRated()) {
            rlRateApp?.show()
            showRate()
        } else {
            rlRateApp?.gone()
        }
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
            logEvent("ViewDiary_IconBack_Clicked")
            if (diaryModel?.listContent != null) {
                diaryModel?.listContent?.let { d ->
                    if (d.size > 0 && d[0].title != getString(R.string.app_name)&&AppUtil.readiary!=1) {
                        showAdsInter("back_diary", 12000, {
                            onBackPress(R.id.readDiaryFrag)
                        }, {
                            onBackPress(R.id.readDiaryFrag)
                        })
                    }else{
                        onBackPress(R.id.readDiaryFrag)
                    }
                }
            } else {
                onBackPress(R.id.readDiaryFrag)
            }
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
            if (it.images.isNullOrEmpty()) {
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


/*---------- rate -------*/

    var numRate = 0
    private fun showRate() {
        context?.let { ctx ->
            resetStar()
            isEnable(true)
            setOnClickStar(ctx)
        }
    }

    private fun setOnClickStar(context: Context) {
        ivStar1.setPreventDoubleClickScaleView(300) {
            btnRate.showIfInv()
            btnRate.text = context.getString(R.string.feedback_to_us)
            numRate = 1
            logEvent("ViewDiary_1star_Clicked")
            view?.let { ReadRateUtil.star1(it) }
            tvContents.text = context.getString(R.string.content_rate_1_star)
        }
        ivStar2.setPreventDoubleClickScaleView(300) {
            btnRate.showIfInv()
            numRate = 2
            logEvent("ViewDiary_2star_Clicked")
            btnRate.text = context.getString(R.string.feedback_to_us)
            view?.let { ReadRateUtil.star2(it) }
            tvContents.text = context.getString(R.string.content_rate_1_star)

        }
        ivStar3.setPreventDoubleClickScaleView(300) {
            btnRate.showIfInv()
            numRate = 3
            logEvent("ViewDiary_3star_Clicked")
            btnRate.text = context.getString(R.string.mail_to_us)
            view?.let { ReadRateUtil.star3(it) }
            tvContents.text = context.getString(R.string.content_rate_3_star)
        }
        ivStar4.setPreventDoubleClickScaleView(300) {
            btnRate.showIfInv()
            numRate = 4
            logEvent("ViewDiary_4star_Clicked")
            btnRate.text = context.getString(R.string.mail_to_us)
            view?.let { ReadRateUtil.star4(it) }
            tvContents.text = context.getString(R.string.content_rate_3_star)
        }
        ivStar5.setPreventDoubleClickScaleView(300) {
            btnRate.showIfInv()
            numRate = 5
            logEvent("ViewDiary_5star_Clicked")
            btnRate.text = context.getString(R.string.open_gp)
            view?.let { ReadRateUtil.star5(it) }
            tvContents.text = context.getString(R.string.content_rate_5_star)
        }


    }

    private fun resetStar() {
        context?.let {
            ivStar1?.setImageResource(R.drawable.ic_un_star_up)
            ivStar2?.setImageResource(R.drawable.ic_un_star_up)
            ivStar3?.setImageResource(R.drawable.ic_un_star_up)
            ivStar4?.setImageResource(R.drawable.ic_un_star_up)
            ivStar5?.setImageResource(R.drawable.ic_un_star_up)

        }
    }

    private fun isEnable(isEnable: Boolean) {

        context?.let {
            if (isEnable) {
                ivStar1?.isEnabled = true
                ivStar2?.isEnabled = true
                ivStar3?.isEnabled = true
                ivStar4?.isEnabled = true
                ivStar5?.isEnabled = true
            } else {
                ivStar1?.isEnabled = false
                ivStar2?.isEnabled = false
                ivStar3?.isEnabled = false
                ivStar4?.isEnabled = false
                ivStar5?.isEnabled = false
            }
        }


    }


}
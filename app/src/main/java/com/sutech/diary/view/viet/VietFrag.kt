package com.sutech.diary.view.viet

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.inputmethod.InputMethodManager
import androidx.activity.addCallback
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.sutech.diary.adapter.AdapterImageContent
import com.sutech.diary.base.BaseFragment
import com.sutech.diary.database.DiaryDatabase
import com.sutech.diary.model.ContentModel
import com.sutech.diary.model.DiaryModel
import com.sutech.diary.model.ImageObj
import com.sutech.diary.util.*
import com.sutech.diary.view.VeAct
import com.sutech.journal.diary.diarywriting.lockdiary.R
import kotlinx.android.synthetic.main.fragment_viet_diary.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

private const val TAG = "VietFrag"

class VietFrag : BaseFragment(R.layout.fragment_viet_diary) {

    private var IS_CHOOSE = 0
    private var isOnClick = false
    private var isOnClickUpdateDate = false
    private val arrImage: ArrayList<ImageObj> = ArrayList()
    private lateinit var adapterImage: AdapterImageContent


    private var currentDate = Calendar.getInstance()
    private var diaryModel: DiaryModel? = null
    private var positionContent: Int = -1
    private var contentModel: ContentModel? = null
    private var isUpdate = false


    private var titleDiary = ""
    private var contentDiary = ""
    private var moodDiary = MoodUtil.getMoodByName(MoodUtil.Mood.SMILING.name)
    var arrDelete: ArrayList<String> = ArrayList()
    private lateinit var dirayDataBase: DiaryDatabase


    override fun onResume() {
        super.onResume()
        if (isOnClick) {
            if (IS_CHOOSE == 1) {
                if (DeviceUtil.arrImage.isNotEmpty()) {
                    arrImage.addAll(DeviceUtil.arrImage)
                    DeviceUtil.arrImage.clear()
                    adapterImage.notifyDataSetChanged()
                }
            }
            if (IS_CHOOSE == 2) {
                if (DeviceUtil.arrVe.isNotEmpty()) {
                    arrImage.addAll(DeviceUtil.arrVe)
                    DeviceUtil.arrVe.clear()
                    adapterImage.notifyDataSetChanged()
                }
            }
            isOnClick = false
        }
    }

    override fun onStop() {
        super.onStop()
        AppUtil.hideKeyboard(requireActivity())
    }

    override fun initView() {
        context?.let { ctx ->
            dirayDataBase = DiaryDatabase.getInstance(ctx)
        }
        AppUtil.needUpdateDiary = false
        logEvent("WriteDiary_Show")
        showBanner("banner_read_nhat_ky", layoutAdsViet)
        initOnBackPress()
        getDataBundle()
        setDataBundle()
        initOnClick()
        initRcvImage()

    }

    private fun confirmBack() {
        logEvent("WriteDiary_IconBack_Clicked")
        context?.let { ctx ->

            logEvent("SaveDiary_Show")
            logEvent("DialogSave_Show")
            DialogUtil.showDialogConfirmSave(ctx, {
                logEvent("SaveDiary_IconSave_Clicked")
                logEvent("DialogSave_IconSave_Clicked")
                if (invalidateContent()) {
                    date = SimpleDateFormat("dd-MM-yyyy", Locale.US).format(currentDate.time)
                    dateTime = SimpleDateFormat(
                        "EEEE dd-MM-yyyy",
                        Locale.US
                    ).format(currentDate.time)
                    context?.let {
                        DialogUtil.showDialogLoading(
                            it,
                            getString(R.string.save_diary_load)
                        )
                    }
                    context?.let { ctx ->
//                    AdsUtil.loadInterstitialAndShow(
//                        ctx,
//                        getString(R.string.interstitial_save_diary),
//                        12000
//                    ) {
//                        Log.e(TAG, "loadInterstitialAndShow: $it")
                        CoroutineScope(Dispatchers.IO).launch {
                            moveImageToInternal()
                            removeFileInternal()
                            DialogUtil.cancelDialogLoading()
                            if (!isUpdate) {
                                insertNewDiary()
                            } else {
                                updateDiary()
                            }
                        }
//                    }

                    }
                }
            }, {
                logEvent("DialogSave_IconDiscard_Clicked")
                logEvent("SaveDiary_IconDiscard_Clicked")
                onBackWrite()
            })
        }

    }

    private fun onBackWrite() {
        context?.let {
            onBackPress(R.id.writeFrag)
        }
    }

    private fun initOnBackPress() {
        activity?.onBackPressedDispatcher?.addCallback(this, true) {
            confirmBack()
        }
    }

    private fun getDataBundle() {
        if (arguments != null) {
            val stringArrDiary = arguments?.getString(Constant.EXTRA_DIARY)
            positionContent = arguments?.getInt(Constant.EXTRA_POSITION_CONTENT, 0)!!

            diaryModel = Gson().fromJson(stringArrDiary, DiaryModel::class.java)
            contentModel = diaryModel?.listContent?.get(positionContent)
            isUpdate = true
        } else {
            isUpdate = false
        }
    }

    private fun setDataBundle() {
        if (contentModel != null) {
            edtTitle?.setText(contentModel?.title)
            edtContent?.setText(contentModel?.content)
            contentModel?.images?.let {
//                arrImage.clear()
                arrImage.addAll(it)
            }
        }
    }

    private fun initRcvImage() {
        adapterImage = AdapterImageContent(arrImage, {
            val bundle = Bundle()
            if (contentModel != null) {
                bundle.putString(Constant.EXTRA_ARRAY_IMAGE, Gson().toJson(contentModel?.images))
            } else {
                bundle.putString(Constant.EXTRA_ARRAY_IMAGE, Gson().toJson(arrImage))
            }
            bundle.putInt(Constant.EXTRA_POSITION_IMAGE, it)
            gotoFrag(R.id.writeFrag, R.id.action_writeFrag_to_viewImageFrag, bundle)
        }, {
            if (arrImage[it].path != null) {
                if (arrImage[it].path!!.contains("imageDir")) {
                    arrDelete.add(arrImage[it].path!!)
                }
            }
            arrImage.removeAt(it)
            adapterImage.notifyDataSetChanged()
        })
        adapterImage.isWrite = true
        rcvImageDiary.layoutManager = GridLayoutManager(context, 3, RecyclerView.VERTICAL, false)
        rcvImageDiary.adapter = adapterImage

    }

    var date = SimpleDateFormat("dd-MM-yyyy", Locale.US).format(currentDate.time)
    var dateTime = SimpleDateFormat("EEEE dd-MM-yyyy", Locale.US).format(currentDate.time)

    private fun initOnClick() {
        tvDateTime.text = SimpleDateFormat("dd MMMM yyyy", Locale.US).format(currentDate.time)

        btnWriteBack?.setOnClickScaleView(1000) {
            confirmBack()
        }
        rlCalendar?.setOnClickScaleView(1000) {
            logEvent("WriteDiary_IconCalen_Clicked")
            onClickChooseDate()
        }
        btnDate?.setOnClickScaleView(1000) {
            logEvent("WriteDiary_IconCalen_Clicked")
            onClickChooseDate()
        }

        edtTitle?.setOnFocusChangeListener { view, b ->
            logEvent("WriteDiary_Title_Clicked")
        }
        edtContent?.setOnFocusChangeListener { view, b ->
            logEvent("WriteDiary_Write_Clicked")
        }

        rlContent?.setOnClickListener {
            edtContent.requestFocus()
            context?.let { ctx ->
                val inputMethodManager =
                    ctx.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.showSoftInput(edtContent, 0)
            }

        }
        btnAddImage?.setOnClickScaleView(1000) {
            logEvent("WriteDiary_IconPhoto_Clicked")
            context?.let { ctx ->
                IS_CHOOSE = 1
                gotoFrag(R.id.writeFrag, R.id.action_writeFrag_to_chooseImageAct)
                isOnClick = true
            }


        }
        btnDraw?.setOnClickScaleView(1000) {
            logEvent("WriteDiary_IconPen_Clicked")
            context?.let { ctx ->
                IS_CHOOSE = 2
                gotoFrag(R.id.writeFrag, R.id.action_writeFrag_to_veAct)
                isOnClick = true
            }

        }
        btnSaveDiary?.setOnClickScaleView(2000) {
            logEvent("WriteDiary_IconOK_Clicked")
            if (invalidateContent()) {

                date = SimpleDateFormat("dd-MM-yyyy", Locale.US).format(currentDate.time)
                dateTime = SimpleDateFormat(
                    "EEEE dd-MM-yyyy",
                    Locale.US
                ).format(currentDate.time)
                context?.let {
                    DialogUtil.showDialogLoading(
                        it,
                        getString(R.string.save_diary_load)
                    )
                }
                context?.let { ctx ->
//                    AdsUtil.loadInterstitialAndShow(
//                        ctx,
//                        getString(R.string.interstitial_save_diary),
//                        12000
//                    ) {
//                        Log.e(TAG, "loadInterstitialAndShow: $it")
                    CoroutineScope(Dispatchers.IO).launch {
                        moveImageToInternal()
                        removeFileInternal()
                        DialogUtil.cancelDialogLoading()
                        if (!isUpdate) {
                            insertNewDiary()
                        } else {
                            updateDiary()
                        }
                    }
//                    }

                }
            }
        }
        btnAddMood?.setOnClickListener {
            context?.let { cxt ->
                val loc = IntArray(2) { value -> value }
                btnAddMood?.getLocationOnScreen(loc)
                val x = loc[0] - btnAddMood?.measuredWidth!! * 8
                val y = loc[1]
                DialogUtil.showDialogMood(cxt, x, y) { mood ->
                    moodDiary = mood
                    btnAddMood?.setImageResource(mood.imageResource)
                }
            }
        }
    }


    private fun onClickChooseDate() {
        context?.let { ctx -> Locale.setDefault(Locale.US);
            val dialogSearchDiary = DatePickerDialog(
                ctx, R.style.DialogDayTheme,
                { view, year, month, dayOfMonth ->
                    var keyMonth = if (month < 10) {
                        "0${month + 1}"
                    } else {
                        "${month + 1}"
                    }
                    val cal = Calendar.getInstance()
                    cal.set(Calendar.YEAR, year)
                    cal.set(Calendar.MONTH, month)
                    cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    setDateTime(cal)

                },
                currentDate.get(Calendar.YEAR),
                currentDate.get(Calendar.MONTH),
                currentDate.get(Calendar.DAY_OF_MONTH)
            )
            dialogSearchDiary.datePicker.maxDate = System.currentTimeMillis()

            if (!dialogSearchDiary.isShowing) {
                dialogSearchDiary.show()
            }
        }
    }

    private fun setDateTime(date: Calendar? = null) {
        isOnClickUpdateDate = true
        if (date != null) {
            currentDate = date
        }
        tvDateTime.text =
            SimpleDateFormat("dd MMMM yyyy", Locale.US).format(currentDate.time)
    }

    private suspend fun updateDiary() {
        contentModel?.title = titleDiary
        contentModel?.content = contentDiary
        contentModel?.images = arrImage
        contentModel?.dateTimeUpdate =
            SimpleDateFormat("dd-MM-yyyy", Locale.US).format(Date())
        contentModel?.mood = moodDiary

        if (isOnClickUpdateDate) {
            contentModel?.dateTimeCreate = dateTime
            var diarySearch = dirayDataBase.getDiaryDao().getDiaryByDateTime(date)
            var addToDay = true
            if (diarySearch == null) {
                diarySearch = DiaryModel(dateTime = date)
                addToDay = false
            }

            if (diarySearch.listContent == null) {
                diarySearch.listContent = ArrayList()
            }

            diarySearch.listContent?.add(0, contentModel!!)

            if (addToDay) {
                dirayDataBase.getDiaryDao().updateDiary(diarySearch).let {
                    withContext(Dispatchers.Main) {
                    }
                }
            } else {
                dirayDataBase.getDiaryDao().insertDiary(diarySearch).let {
                    withContext(Dispatchers.Main) {
                    }
                }
            }
            diaryModel?.listContent?.removeAt(positionContent)
        } else {
            diaryModel?.listContent?.set(positionContent, contentModel!!)
        }


        if (!diaryModel!!.listContent!!.isNullOrEmpty()) {
            dirayDataBase.getDiaryDao().updateDiary(diaryModel!!).let {
                withContext(Dispatchers.Main) {
                    context?.let {
                        AppUtil.showToast(it, R.string.update_diary_successful)
                        if (Constant.showRateToday == 0) {
                            Constant.showRateToday = 1
                            onBackToHome(R.id.writeFrag)
                        }else{
                            showAdsInter("save_diary",12000,{
                                onBackToHome(R.id.writeFrag)
                            },{
                                onBackToHome(R.id.writeFrag)
                            })
                        }
                    }
                }
            }
        } else {
            dirayDataBase.getDiaryDao().deleteDiary(diaryModel!!.id).let {
                withContext(Dispatchers.Main) {
                    context?.let {
                        AppUtil.showToast(it, R.string.update_diary_successful)
                        if (Constant.showRateToday == 0) {
                            Constant.showRateToday = 1
                            onBackToHome(R.id.writeFrag)
                        }else{
                            showAdsInter("save_diary",12000,{
                                onBackToHome(R.id.writeFrag)
                            },{
                                onBackToHome(R.id.writeFrag)
                            })
                        }
                    }
                }
            }
        }


    }

    private suspend fun insertNewDiary() {
        var diaryObj = dirayDataBase.getDiaryDao().getDiaryByDateTime(date)
        var addToDay = true
        if (diaryObj == null) {
            diaryObj = DiaryModel(dateTime = date)
            addToDay = false
        }

        if (diaryObj.listContent == null) {
            diaryObj.listContent = ArrayList()
        }
        diaryObj.listContent!!.add(
            0,
            ContentModel(
                title = titleDiary,
                content = contentDiary,
                images = arrImage,
                createDate = date,
                dateTimeCreate = dateTime,
                mood = moodDiary
            )
        )
        if (addToDay) {
            dirayDataBase.getDiaryDao().updateDiary(diaryObj).let {
                withContext(Dispatchers.Main) {
                    context?.let {
                        AppUtil.needUpdateDiary = true
                        AppUtil.showToast(it, R.string.create_diary_successful)
                        if (Constant.showRateToday == 0) {
                            Constant.showRateToday = 1
                            onBackToHome(R.id.writeFrag)
                        }else{
                            showAdsInter("save_diary",12000,{
                                onBackToHome(R.id.writeFrag)
                            },{
                                onBackToHome(R.id.writeFrag)
                            })
                        }
                    }
                }
            }
        } else {
            dirayDataBase.getDiaryDao().insertDiary(diaryObj).let {
                withContext(Dispatchers.Main) {
                    context?.let {
                        AppUtil.needUpdateDiary = true
                        AppUtil.showToast(it, R.string.create_diary_successful)
                        if (Constant.showRateToday == 0) {
                            Constant.showRateToday = 1
                            onBackToHome(R.id.writeFrag)
                        }else{
                            showAdsInter("save_diary",12000,{
                                onBackToHome(R.id.writeFrag)
                            },{
                                onBackToHome(R.id.writeFrag)
                            })
                        }
                    }
                }
            }
        }
    }

    private fun invalidateContent(): Boolean {
        titleDiary = edtTitle.text.toString().trim()
        contentDiary = edtContent.text.toString().trim()

        if (titleDiary.isBlank()) {
            titleDiary = if (contentDiary.isNotEmpty() && contentDiary.length > 150) {
                contentDiary.substring(0, 150)
            } else if (contentDiary.isNotEmpty() && contentDiary.length < 150) {
                contentDiary
            } else if (!arrImage.isNullOrEmpty()) {
                getString(R.string.diary_image)
            } else {
                AppUtil.showToast(context, R.string.please_input_content)
                return false
            }
        }
        return true

    }

    private fun moveImageToInternal() {
        if (!arrImage.isNullOrEmpty()) {
            Log.e(TAG, "moveImageToInternal: $arrImage")
            context?.let { ctx ->
                for (i in 0 until arrImage.size) {
                    if (arrImage[i].bitmap != null) {
                        arrImage[i].path = ImageUtil.saveToInternalStorage(
                            ctx,
                            arrImage[i]
                        )
                        arrImage[i].bitmap = null
                    } else if (arrImage[i].path != null && !arrImage[i].path!!.contains("imageDir")) {

                        arrImage[i].path = ImageUtil.copyFileToInternal(
                            ctx,
                            arrImage[i].path!!,
                            arrImage[i].id
                        )

                        Log.e(TAG, "moveImageToInternal: ${arrImage[i].path}")
                    }
                }
            }

        }
    }

    private fun removeFileInternal() {
        if (!arrDelete.isNullOrEmpty()) {
            for (path in arrDelete) {
                Log.e(TAG, "$path delete: ${AppUtil.deleteFileFromInternalStorage(path)}")
            }
        }
    }

}
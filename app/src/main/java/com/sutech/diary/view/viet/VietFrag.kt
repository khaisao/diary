package com.sutech.diary.view.viet

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Paint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.addCallback
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.sutech.diary.adapter.AdapterImageContent
import com.sutech.diary.adapter.AdapterInputHashtag
import com.sutech.diary.adapter.CalendarAdapter
import com.sutech.diary.base.BaseFragment
import com.sutech.diary.database.DiaryDatabase
import com.sutech.diary.model.ContentModel
import com.sutech.diary.model.DiaryModel
import com.sutech.diary.model.Hashtag
import com.sutech.diary.model.ImageObj
import com.sutech.diary.util.*
import com.sutech.journal.diary.diarywriting.lockdiary.R
import kotlinx.android.synthetic.main.fragment_viet_diary.btnAddImage
import kotlinx.android.synthetic.main.fragment_viet_diary.btnAddMood
import kotlinx.android.synthetic.main.fragment_viet_diary.btnDraw
import kotlinx.android.synthetic.main.fragment_viet_diary.btnHashtag
import kotlinx.android.synthetic.main.fragment_viet_diary.btnSaveDiary
import kotlinx.android.synthetic.main.fragment_viet_diary.btnWriteBack
import kotlinx.android.synthetic.main.fragment_viet_diary.edtContent
import kotlinx.android.synthetic.main.fragment_viet_diary.edtTitle
import kotlinx.android.synthetic.main.fragment_viet_diary.layoutAdsViet
import kotlinx.android.synthetic.main.fragment_viet_diary.rcvImageDiary
import kotlinx.android.synthetic.main.fragment_viet_diary.tvDateTime
import kotlinx.android.synthetic.main.fragment_viet_diary.view_input_hashtag
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale

private const val TAG = "VietFrag"

class VietFrag : BaseFragment(R.layout.fragment_viet_diary),CalendarAdapter.OnDateItemListener, InputHashtagView.HashtagInputViewListener {

    private var IS_CHOOSE = 0
    private var isOnClick = false
    private var isOnClickUpdateDate = false
    private val arrImage: ArrayList<ImageObj> = ArrayList()
    private lateinit var adapterImage: AdapterImageContent
    private lateinit var adapterHashtag: AdapterInputHashtag
    private var currentDate = Calendar.getInstance()
    private var diaryModel: DiaryModel? = null
    private var positionContent: Int = -1
    private var contentModel: ContentModel? = null
    private var isUpdate = false

    private var calendarAdapter: CalendarAdapter? = null
    private var titleDiary = ""
    private var contentDiary = ""
    private var moodDiary = MoodUtil.getMoodByName(MoodUtil.Mood.SMILING.name)
    var arrDelete: ArrayList<String> = ArrayList()
    private lateinit var dirayDataBase: DiaryDatabase

    companion object {
        var addNewsSlectedDate: LocalDate = LocalDate.now()
    }

    private val listInputHashtag: MutableList<Hashtag> = mutableListOf()

    override fun onResume() {
        super.onResume()
        if (this::adapterHashtag.isInitialized) {
            adapterHashtag.submitList(listInputHashtag)
        }
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
        addNewsSlectedDate = LocalDate.now()
        AppUtil.needUpdateDiary = false
        btnAddMood?.setImageResource(moodDiary.imageResource)
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
                    date = SimpleDateFormat(Constant.FormatdayDDMMYY, Locale.US).format(currentDate.time)
                    dateTime = SimpleDateFormat(
                        Constant.FormatdayEEDDMMYY,
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
                arrImage.clear()
                arrImage.addAll(it)
            }
            for (item in contentModel!!.listHashtag) {
                val view = InputHashtagView(requireContext())
                view_input_hashtag.addView(view)
                view.setRequestHashtagInput(false)
                view.setEnableHashtagInput(true)
                view.setTextForHashtag(item)
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

    var date = SimpleDateFormat(Constant.FormatdayDDMMYY, Locale.US).format(currentDate.time)
    var dateTime = SimpleDateFormat(Constant.FormatdayEEDDMMYY, Locale.US).format(currentDate.time)

    private fun initOnClick() {
        tvDateTime.text = SimpleDateFormat(Constant.FormatdayDDMMYY, Locale.US).format(currentDate.time)

        btnWriteBack?.setOnClickScaleView(1000) {
            confirmBack()
        }
        tvDateTime?.setOnClickScaleView(1000) {
            logEvent("WriteDiary_IconCalen_Clicked")
            //TODO Remove
            onClickChooseDate()

        }
        btnHashtag?.setOnClickScaleView(500) {
            edtTitle.clearFocus()
            edtContent.clearFocus()
            val view = InputHashtagView(requireContext())
            view.setCustomViewListener(this)
            view.setRequestHashtagInput(true)
            view.setEnableHashtagInput(true)
            view_input_hashtag.addView(view)
        }

        edtTitle?.setOnFocusChangeListener { view, isFocus ->
            logEvent("WriteDiary_Title_Clicked")
            if (isFocus) {
                edtTitle.isCursorVisible = true
                edtContent.clearFocus()
                edtContent.isCursorVisible = false
            }
        }
        edtContent?.setOnFocusChangeListener { view, isFocus ->
            logEvent("WriteDiary_Write_Clicked")
            if (isFocus) {
                edtContent.isCursorVisible = true
                edtTitle.clearFocus()
                edtTitle.isCursorVisible = false
            }
        }
        tvDateTime?.setOnClickListener {
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

                date = SimpleDateFormat(Constant.FormatdayDDMMYY, Locale.US).format(currentDate.time)
                dateTime = SimpleDateFormat(
                    Constant.FormatdayEEDDMMYY,
                    Locale.US
                ).format(currentDate.time)
                context?.let {
                    DialogUtil.showDialogLoading(
                        it,
                        getString(R.string.save_diary_load)
                    )
                }
                context?.let { ctx ->
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

                }
            }
        }
        btnAddMood?.setOnClickListener {
            context?.let { cxt ->
                val loc = IntArray(2) { value -> value }
                btnAddMood?.getLocationOnScreen(loc)
                val x = loc[0] - btnAddMood?.measuredWidth!! * 7
                val y = loc[1]
                DialogUtil.showDialogMood(cxt, x, y) { mood ->
                    moodDiary = mood
                    btnAddMood?.setImageResource(mood.imageResource)
                }
            }
        }
    }


    private fun onClickChooseDate() {
        context?.let { ctx -> Locale.setDefault(Locale.US)
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
    private fun showCalendar(context: Context){
        val customDialog = Dialog(context)
        val displayMetrics = DisplayMetrics()
        requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
        val width = displayMetrics.widthPixels
        val height = displayMetrics.heightPixels
        customDialog.setContentView(R.layout.calendar_layout)
        customDialog.setCanceledOnTouchOutside(true)
        customDialog.window!!.setLayout(width, height)
        customDialog.window?.setBackgroundDrawable(android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT))
        val btnNext = customDialog.findViewById<ImageView>(R.id.btn_nextMonth)
        val btnPrev = customDialog.findViewById<ImageView>(R.id.btn_backMonth)
        val rcvCalendar = customDialog.findViewById<RecyclerView>(R.id.rcv_calendar)
        val btnToday = customDialog.findViewById<TextView>(R.id.tv_today)
        val tvCurrentTime = customDialog.findViewById<TextView>(R.id.tv_curenTime)
        val btnOK = customDialog.findViewById<LinearLayout>(R.id.btn_ok)
        btnToday.paintFlags = btnToday.paintFlags or Paint.UNDERLINE_TEXT_FLAG

        btnNext.setOnClickListener {
            nextMonthAction(rcvCalendar,tvCurrentTime)
        }
        btnPrev.setOnClickListener {
            previousMonthAction(rcvCalendar,tvCurrentTime)
        }
        btnToday.setOnClickListener {
            addNewsSlectedDate = LocalDate.now()
            setMonthView(rcvCalendar,tvCurrentTime)
            calendarAdapter!!.notifyDataSetChanged()
        }
        setMonthView(rcvCalendar,tvCurrentTime)

        btnOK.setOnClickListener {
            customDialog.dismiss()
        }
        customDialog.show()
    }
    private fun setMonthView(rcvCalendar:RecyclerView,tvCurrentTime:TextView) {
        context?.let { ctx ->
            tvCurrentTime.text = monthYearFromDate(addNewsSlectedDate)
            val daysInMonth = daysInMonthArray(addNewsSlectedDate)
            calendarAdapter = CalendarAdapter(true,daysInMonth, this, ctx)
            rcvCalendar.layoutManager = GridLayoutManager(ctx, 7)
            rcvCalendar.adapter = calendarAdapter
        }

        Handler(Looper.getMainLooper()).postDelayed({
            calendarAdapter!!.notifyDataSetChanged()

        }, 1000)
    }
    private fun daysInMonthArray(date: LocalDate): ArrayList<String> {
        val daysInMonthArray = ArrayList<String>()
        val yearMonth = YearMonth.from(date)
        val daysInMonth = yearMonth.lengthOfMonth()
        val firstOfMonth: LocalDate = addNewsSlectedDate.withDayOfMonth(1)!!
        val dayOfWeek = firstOfMonth.dayOfWeek.value
        for (i in 1..42) {
            if (i <= dayOfWeek || i > daysInMonth + dayOfWeek) {
                daysInMonthArray.add("")
            } else {
                daysInMonthArray.add((i - dayOfWeek).toString())
            }
        }
        return daysInMonthArray
    }
    private fun monthYearFromDate(date: LocalDate): String {
        val formatter = DateTimeFormatter.ofPattern(Constant.FormatdayMMMMYY)
        return date.format(formatter)
    }
    private fun previousMonthAction(rcvCalendar:RecyclerView,tvCurrentTime:TextView) {
        addNewsSlectedDate = addNewsSlectedDate.minusMonths(1)
        setMonthView(rcvCalendar,tvCurrentTime)
    }

    private fun nextMonthAction(rcvCalendar:RecyclerView,tvCurrentTime:TextView) {
        addNewsSlectedDate = addNewsSlectedDate.plusMonths(1)
        setMonthView(rcvCalendar,tvCurrentTime)
    }


    private fun setDateTime(date: Calendar? = null) {
        isOnClickUpdateDate = true
        if (date != null) {
            currentDate = date
        }
        tvDateTime.text =
            SimpleDateFormat(Constant.FormatdayDDMMYY, Locale.US).format(currentDate.time)
    }

    private suspend fun updateDiary() {
        contentModel?.title = titleDiary
        contentModel?.content = contentDiary
        contentModel?.images = arrImage
        contentModel?.dateTimeUpdate =
            SimpleDateFormat(Constant.FormatdayDDMMYY, Locale.US).format(Date())

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


        if (diaryModel!!.listContent!!.isNotEmpty()) {
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

    private fun getAllHashtagInput(): MutableList<String> {
        val listHashtagInput = mutableListOf<String>()
        for (i in 0 until view_input_hashtag.childCount) {
            val childView: View = view_input_hashtag.getChildAt(i)
            if (childView is InputHashtagView) {
                val value = childView.getCurrentHashtagContent()
                if (value.length > 1) {
                    listHashtagInput.add(value)
                }
            }
        }
        return listHashtagInput
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
                mood = moodDiary,
                listHashtag = getAllHashtagInput()
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
            } else if (arrImage.isNotEmpty()) {
                getString(R.string.diary_image)
            } else {
                AppUtil.showToast(context, R.string.please_input_content)
                return false
            }
        }
        return true

    }

    private fun moveImageToInternal() {
        if (arrImage.isNotEmpty()) {
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
        if (arrDelete.isNotEmpty()) {
            for (path in arrDelete) {
                Log.e(TAG, "$path delete: ${AppUtil.deleteFileFromInternalStorage(path)}")
            }
        }
    }

    override fun onDateItemClick(position: Int, dayText: String?) {
        calendarAdapter?.updatePosition(position)
    }

    override fun onInputEmptyHashtag(view: InputHashtagView) {
        view_input_hashtag.removeView(view)
    }

}
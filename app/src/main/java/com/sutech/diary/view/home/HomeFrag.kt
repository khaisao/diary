package com.sutech.diary.view.home

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.activity.addCallback
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.sutech.diary.adapter.AdapterDiaryItem
import com.sutech.diary.base.BaseFragment
import com.sutech.diary.database.DataStore
import com.sutech.diary.database.DiaryDatabase
import com.sutech.diary.model.DiaryModel
import com.sutech.diary.model.ImageObj
import com.sutech.diary.util.*
import com.sutech.diary.util.Constant.EXTRA_DIARY
import com.sutech.diary.util.Constant.EXTRA_POSITION_CONTENT
import com.sutech.diary.util.Constant.showRateToday
import com.sutech.journal.diary.diarywriting.lockdiary.R
import com.test.dialognew.*
import kotlinx.android.synthetic.main.fragment_doc_diary.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("SimpleDateFormat")
class HomeFrag : BaseFragment(R.layout.fragment_home) {


    private var currentDate = Calendar.getInstance()
    private var adapterDiary: AdapterDiaryItem? = null
    private val arrDiary: MutableList<DiaryModel> = ArrayList()

    private var diaryDataBase: DiaryDatabase? = null
    override fun initView() {
        logEvent("MainScr_Show")
        activity?.onBackPressedDispatcher?.addCallback(this, true) {
            if (rlSearch?.isShow() == true) {
                rlSearch?.gone()
                rlSearch?.hideKeyboard()
                edtSearch?.setText("")
            } else {
                activity?.finish()

            }
        }
        context?.let { ctx ->
            diaryDataBase = DiaryDatabase.getInstance(ctx)
        }
        showBanner("banner_home", layoutAdsHome)
        initOnClick()
        setRcvDiary()
        setDateTime()
        showRate()
        Log.e("TAG", "onCreatedView: ???????")
        edtSearch?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                Log.e("TAG", "afterTextChanged: ???")
                edtSearch?.text?.let {
                    if (grFilter.isShown) {
                        val year = currentDate.get(Calendar.YEAR)
                        val month = currentDate.get(Calendar.MONTH)
                        val day = currentDate.get(Calendar.DAY_OF_MONTH)
                        getDataDiary(it.toString().trim(), "$day-$month-$year")
                    } else {
                        getDataDiary(it.toString().trim())
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
    }


    override fun onResume() {
        super.onResume()
        context?.let {
            Log.e("TAG", "onResume: home ${AppUtil.needUpdateDiary}")
            if (AppUtil.needUpdateDiary) {
                DialogUtil.showDialogLoading(it, getString(R.string.loading))
                getDataDiary()
                AppUtil.needUpdateDiary = false
                setDateTime(Calendar.getInstance())
            }
        }
    }

    private fun setDateTime(date: Calendar? = null) {
        if (date != null) {
            currentDate = date
        }
        tvFilter?.text = SimpleDateFormat("dd MMMM yyyy", Locale.US).format(currentDate.time)
    }

    private fun getDataDiary(keyword: String? = null, dateTime: String? = null) {
        Log.e("TAG", "getDataDiary: $keyword date $dateTime")
        CoroutineScope(Dispatchers.IO).launch {
            if (keyword.isNullOrBlank() && dateTime.isNullOrBlank()) {
                diaryDataBase?.getDiaryDao()?.getAllDiary()?.let {
                    withContext(Dispatchers.Main) {
                        Log.e("TAG", "getAllDiary: ${Gson().toJson(it)}")
                        Log.e("TAG", "getAllDiary: $keyword date $dateTime")
                        DialogUtil.cancelDialogLoading()
                        showNotFoundDiary(it.isNullOrEmpty())
                        updateDataDiary(it)
                        adapterDiary?.notifyDataSetChanged()
                    }
                }
            } else if (!dateTime.isNullOrBlank()) {
                diaryDataBase?.getDiaryDao()?.searchDiaryByDateTime(dateTime)?.let {
                    withContext(Dispatchers.Main) {
                        showNotFoundDiary(it.isNullOrEmpty())
                        updateDataDiary(it)
                        adapterDiary?.notifyDataSetChanged()
                        DialogUtil.cancelDialogLoading()
                    }
                }
            } else if (!keyword.isNullOrBlank()) {
                diaryDataBase?.getDiaryDao()?.searchDiaryByTitle("%$keyword%")?.let {
                    withContext(Dispatchers.Main) {
                        showNotFoundDiary(it.isNullOrEmpty())
                        updateDataDiary(it)
                        Log.e("TAG", "getDataDiaryssdasdasd: $it")
                        adapterDiary?.notifyDataSetChanged()
                        DialogUtil.cancelDialogLoading()
                    }
                }
            }
        }
    }

    private fun updateDataDiary(it: MutableList<DiaryModel>) {
        arrDiary.clear()
        arrDiary.addAll(it)
        adapterDiary?.submitList(arrDiary)
        adapterDiary?.notifyDataSetChanged()
    }

    private fun showNotFoundDiary(isShow: Boolean) {
        if (isShow) {
            logEvent("DialogNoDiary_Show")
            ivNotFoundDiary?.show()
            tvNotFoundDiary?.show()
            icNotFoundDiarySad?.show()
        } else {
            ivNotFoundDiary?.gone()
            tvNotFoundDiary?.gone()
            icNotFoundDiarySad?.gone()
        }
    }


    private fun initOnClick() {
        icCloseFilter.setOnClickScaleView {
            grFilter?.visibility = View.GONE
            setDateTime(Calendar.getInstance())
            context?.let {
                DialogUtil.showDialogLoading(it, getString(R.string.loading))
                getDataDiary(keyword = edtSearch?.text.toString(), dateTime = null)
            }
        }
        btnClearSearch?.setOnClickScaleView {
            logEvent("Search_IconX_Clicked")
            edtSearch?.setText("")
        }
        btnCloseSearch?.setOnClickScaleView {
            logEvent("Search_IconBack_Clicked")
            closeSearch()
        }

        btnShowSearch?.setOnClickScaleView {
            logEvent("MainScr_ButtonSearch_Clicked")
            rlSearch?.show()
            edtSearch?.requestFocus()
            edtSearch?.showKeyboard()
        }
        btnSearchDiary?.setOnClickScaleView {
            logEvent("MainScr_ButtonCalendar_Clicked")
            showDialogSearchDiary()
        }
        btnVietNhatKy?.setOnClickScaleView {
            logEvent("MainScr_ButtonPlus_Clicked")
            gotoFrag(R.id.mainFrag, R.id.action_mainFrag_to_writeFrag)
        }
        btnMenu?.setOnClickScaleView {
            logEvent("MainScr_IconSetting_Clicked")
            gotoFrag(R.id.mainFrag, R.id.action_mainFrag_to_settingFrag)
        }
        context?.let { ctx ->
        if (AppUtil.isIAP||!AppUtil.isNetworkAvailable(ctx)) {
            btnIap?.gone()
        }else{
            btnIap?.show()
        }
        }
        btnIap?.setOnClickScaleView {
            logEvent("MainScr_IconIAP_Clicked")
            gotoFrag(R.id.mainFrag, R.id.action_mainFrag_to_IAPFragment)
        }
    }

    private fun closeSearch() {
        rlSearch?.gone()
        rlSearch?.hideKeyboard()
        edtSearch?.setText("")
    }

    private fun showDialogSearchDiary() {

        context?.let { ctx ->
            Locale.setDefault(Locale.US);
            logEvent("CalendarScr_Show")
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

                    grFilter?.show()
                    setDateTime(cal)
                    context?.let {
                        DialogUtil.showDialogLoading(it, getString(R.string.loading))
                        getDataDiary(dateTime = "$dayOfMonth-$keyMonth-$year")
                    }
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

    private fun setRcvDiary() {
        adapterDiary = AdapterDiaryItem({ positionDiary, positionContent ->
            logEvent("MainScr_IconDots_Clicked")
        }, { positionDiary, positionContent ->
            val bundle = Bundle()
            bundle.putString(EXTRA_DIARY, Gson().toJson(arrDiary[positionDiary]))
            bundle.putInt(EXTRA_POSITION_CONTENT, positionContent)
            gotoFrag(R.id.mainFrag, R.id.action_mainFrag_to_readDiaryFrag, bundle)
            closeSearch()
        }, { positionDiary, positionContent ->
            logEvent("MainScr_IconEdit_Clicked")
            closeSearch()
            onClickUpdateDiary(positionDiary, positionContent)
        }, { positionDiary, positionContent ->
            logEvent("MainScr_IconDelete_Clicked")
            onClickDeleteDiary(positionDiary, positionContent)
        })
        rcvData.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        rcvData.adapter = adapterDiary
    }


    private fun onClickUpdateDiary(positionDiary: Int, positionContent: Int) {
        val bundle = Bundle()
        bundle.putString(EXTRA_DIARY, Gson().toJson(arrDiary[positionDiary]))
        bundle.putInt(EXTRA_POSITION_CONTENT, positionContent)
        gotoFrag(R.id.mainFrag, R.id.action_mainFrag_to_writeFrag, bundle)
    }

    private fun onClickDeleteDiary(positionDiary: Int, positionContent: Int) {
        logEvent("AlertScr_Show")
        logEvent("DialogAlert_Show")
        context?.let { ctx ->
            DialogUtil.showDialogAlert(ctx, getString(R.string.confirm_delete_diary), {
                logEvent("AlertScr_IconOK_Clicked")
                logEvent("DialogAlert_IconOK_Clicked")
                CoroutineScope(Dispatchers.IO).launch {
                    try {

                        arrDiary[positionDiary].listContent?.let {
                            removeFileInternal(it[positionContent].images)
                        }
                        arrDiary[positionDiary].listContent?.removeAt(positionContent)
                        if (arrDiary[positionDiary].listContent?.size != 0) {
                            diaryDataBase?.getDiaryDao()?.updateDiary(arrDiary[positionDiary])
                                ?.let {
                                    withContext(Dispatchers.Main) {
                                        AppUtil.showToast(ctx, R.string.delete_diary_success)

                                        adapterDiary?.notifyItemChanged(positionDiary)
                                    }
                                }
                        } else {
                            diaryDataBase?.getDiaryDao()?.deleteDiary(arrDiary[positionDiary])
                                ?.let {
                                    withContext(Dispatchers.Main) {
                                        AppUtil.showToast(ctx, R.string.delete_diary_success)
                                        arrDiary.removeAt(positionDiary)
                                        adapterDiary?.notifyDataSetChanged()
                                    }
                                }
                        }
                    } catch (e: Exception) {

                    }
                }
            }, {
                logEvent("AlertScr_IconCancel_Clicked")
                logEvent("DialogAlert_IconCancel_Clicked")
            })
        }

    }

    private fun removeFileInternal(arrImage: ArrayList<ImageObj>) {
        if (!arrImage.isNullOrEmpty()) {
            for (path in arrImage) {
                Log.e("TAG", "$path delete: ${AppUtil.deleteFileFromInternalStorage(path.path!!)}")
            }
        }
    }


    private fun showRate() {
        logEvent("DialogEnjoy_Show")
        if (Constant.showRate && !DataStore.checkRated() && showRateToday == 1) {
            showRateToday = 2
            context?.let {
                Constant.showRate = false
                DialogLib.getInstance().showDialogRate(
                    it,
                    lifecycle = lifecycle,
                    dialogNewInterface1 = object : DialogNewInterface {
                        override fun onClickStar(rate: Int) {
                            when (rate) {
                                1 -> {
                                    logEvent("DialogEnjoy_1star_Clicked")
                                }
                                2 -> {
                                    logEvent("DialogEnjoy_2star_Clicked")
                                }
                                3 -> {
                                    logEvent("DialogEnjoy_3star_Clicked")
                                }
                                4 -> {
                                    logEvent("DialogEnjoy_4star_Clicked")
                                }
                                5 -> {
                                    logEvent("DialogEnjoy_5star_Clicked")
                                }
                            }
                        }

                        override fun onRate(rate: Int) {
                            if (rate > 4) {
                                DataStore.setRated(true)
                                logEvent("DialogEnjoy_Googleplay_Clicked")
                                AppUtil.openMarket(it, it.packageName)
                            }  else if (rate in 2..4) {
                                DataStore.setRated(true)
                                logEvent("DialogEnjoy_Mailtous_Clicked")
                                AppUtil.sendEmailMore(
                                    it,
                                    arrayOf("Sutechmobile@gmail.com"),
                                    "Feedback to My Diary",
                                    ""
                                )
                            }else if (rate in 1..2) {
                                DataStore.setRated(true)
                                logEvent("DialogEnjoy_Feedback_Clicked")
                                AppUtil.sendEmailMore(
                                    it,
                                    arrayOf("Sutechmobile@gmail.com"),
                                    "Feedback to My Diary",
                                    ""
                                )
                            }else{
                                logEvent("DialogEnjoy_Remindlater_Clicked")

                            }
                        }

                        override fun onFB(choice: Int) {

                        }

                        override fun onCancel() {
                            logEvent("DialogEnjoy_IconX_Clicked")

                        }

                        override fun onCancelFb() {
                        }
                    },
                    rateCallback1 = object :
                        RateCallback {
                        override fun onFBShow() {
                            DataStore.setRated(true)
                        }
                    }
                )
            }
        }
    }
}
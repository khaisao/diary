package com.sutech.diary.view.home

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.activity.addCallback
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
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
import com.sutech.diary.util.Constant.FormatdayDDMMYY
import com.sutech.diary.util.Constant.showRateToday
import com.sutech.journal.diary.diarywriting.lockdiary.BuildConfig
import com.sutech.journal.diary.diarywriting.lockdiary.R
import com.test.dialognew.*
import kotlinx.android.synthetic.main.fragment_doc_diary.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.layout_item_drawer.cl_use_password
import kotlinx.android.synthetic.main.layout_item_drawer.ll_change_passcode
import kotlinx.android.synthetic.main.layout_item_drawer.ll_change_theme
import kotlinx.android.synthetic.main.layout_item_drawer.ll_feedback
import kotlinx.android.synthetic.main.layout_item_drawer.ll_hash_tag
import kotlinx.android.synthetic.main.layout_item_drawer.ll_our_other_app
import kotlinx.android.synthetic.main.layout_item_drawer.ll_policy
import kotlinx.android.synthetic.main.layout_item_drawer.ll_share
import kotlinx.android.synthetic.main.layout_item_drawer.ll_statistics
import kotlinx.android.synthetic.main.layout_item_drawer.ll_update_to_pro
import kotlinx.android.synthetic.main.layout_item_drawer.swPassword
import kotlinx.android.synthetic.main.pop_up_windown_sort.view.tv_latest_first
import kotlinx.android.synthetic.main.pop_up_windown_sort.view.tv_oldest_first
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
        swPassword?.isChecked = DataStore.getUsePassword()
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

    private fun showPopupSort() {
        val inflater = LayoutInflater.from(requireContext())
        val popupView = inflater.inflate(R.layout.pop_up_windown_sort, null)

        val popupWindow = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true)

        popupView.tv_latest_first.setOnClick(500) {
            logEvent("MainScr_IconArrange_Latest")
            sortListDiaryByDay(false)
            popupWindow.dismiss()
        }
        popupView.tv_oldest_first.setOnClick(500) {
            logEvent("MainScr_IconArrange_Oldest")
            sortListDiaryByDay(true)
            popupWindow.dismiss()
        }

        btnSort.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        val btnSortWidth = btnSort.measuredWidth

        val xOffset = (btnSortWidth - popupWindow.width) / 2

        popupWindow.showAsDropDown(btnSort, -xOffset + 17, 4)
    }

    private fun setDateTime(date: Calendar? = null) {
        if (date != null) {
            currentDate = date
        }
        tvFilter?.text = SimpleDateFormat("dd MMMM yyyy", Locale.US).format(currentDate.time)
    }

    private fun getDataDiary(keyword: String? = null, dateTime: String? = null) {
        CoroutineScope(Dispatchers.IO).launch {
            if (keyword.isNullOrBlank() && dateTime.isNullOrBlank()) {
                diaryDataBase?.getDiaryDao()?.getAllDiary()?.let {
                    withContext(Dispatchers.Main) {
                        DialogUtil.cancelDialogLoading()
                        showNotFoundDiary(it.isEmpty())
                        updateDataDiary(it)
                    }
                }
            } else if (!dateTime.isNullOrBlank()) {
                diaryDataBase?.getDiaryDao()?.searchDiaryByDateTime(dateTime)?.let {
                    withContext(Dispatchers.Main) {
                        showNotFoundDiary(it.isEmpty())
                        updateDataDiary(it)
                        DialogUtil.cancelDialogLoading()
                    }
                }
            } else if (!keyword.isNullOrBlank()) {
                diaryDataBase?.getDiaryDao()?.searchDiaryByTitle("%$keyword%")?.let {
                    withContext(Dispatchers.Main) {
                        showNotFoundDiary(it.isEmpty())
                        updateDataDiary(it)
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
    }

    private fun sortListDiaryByDay(isAscending: Boolean) {
        val dateFormat = FormatdayDDMMYY
        val unsortedList: List<DiaryModel> = adapterDiary?.currentList?.toList() ?: emptyList()
        val sortedList = if (isAscending) {
            unsortedList.sortedBy { parseDate(it.dateTime, dateFormat) }
        } else {
            unsortedList.sortedByDescending { parseDate(it.dateTime, dateFormat) }
        }
        adapterDiary?.submitList(sortedList)
    }

    private fun parseDate(dateString: String, dateFormat: String): Date {
        val sdf = SimpleDateFormat(dateFormat, Locale.getDefault())
        return sdf.parse(dateString) ?: Date()
    }

    private fun showNotFoundDiary(isShow: Boolean) {
        if (isShow) {
            logEvent("DialogNoDiary_Show")
            ivNotFoundDiary?.show()
            tvNotFoundDiary?.show()
            icNotFoundDiarySad?.show()
            rcvData.isVisible = false
        } else {
            ivNotFoundDiary?.gone()
            tvNotFoundDiary?.gone()
            icNotFoundDiarySad?.gone()
            rcvData.isVisible = true
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
            logEvent("SearchScr_IconX_Clicked")
            edtSearch?.setText("")
        }
        btnCloseSearch?.setOnClickScaleView {
            logEvent("SearchScr_IconBack_Clicked")
            edtSearch?.setText("")
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
            gotoFrag(R.id.mainFrag, R.id.action_mainFrag_to_calendarFrag)
        }
        btnVietNhatKy?.setOnClickScaleView {
            logEvent("MainScr_ButtonPlus_Clicked")
            gotoFrag(R.id.mainFrag, R.id.action_mainFrag_to_writeFrag)
        }
        btnMenu?.setOnClickScaleView {
            logEvent("MainScr_IconSetting_Clicked")
            drawer_layout.openDrawer(GravityCompat.START)
            logEvent("SettingScr_Show")
        }

        ll_change_passcode?.setOnClick(1500) {
            logEvent("SettingScr_Changepasscode_Clicked")
            val bundle = Bundle()
            bundle.putInt(Constant.TYPE_PASSWORD, 1)
            gotoFrag(R.id.mainFrag, R.id.action_mainFrag_to_passWordFrag, bundle)
            drawer_layout.closeDrawer(GravityCompat.START)
        }

        ll_policy?.setOnClick(1500) {
            logEvent("SettingScr_Policy_Clicked")
            AppUtil.openBrowser(
                requireContext(),
                "https://sutechmobile.blogspot.com/2022/12/my-diary-privacy-policy.html"
            )
            drawer_layout.closeDrawer(GravityCompat.START)
        }

        ll_share?.setOnClick(2000) {
            logEvent("SettingScr_Share_Clicked")
            try {
                val shareIntent = Intent(Intent.ACTION_SEND)
                shareIntent.type = "text/plain"
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "My diary")
                var shareMessage = "\nLet me recommend you this application\n\n"
                shareMessage =
                    "${shareMessage}https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}".trimIndent()
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
                startActivity(Intent.createChooser(shareIntent, "choose one"))
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
            drawer_layout.closeDrawer(GravityCompat.START)
        }

        ll_change_theme?.setOnClick(100) {
            logEvent("SettingScr_Changetheme_Clicked")
            gotoFrag(R.id.mainFrag,R.id.action_mainFrag_to_themeFrag)
            drawer_layout.closeDrawer(GravityCompat.START)
        }

        ll_feedback?.setOnClick(1500) {
            context?.let { ctx ->
                logEvent("SettingScr_Feedback_Clicked")
                AppUtil.sendEmailMore(
                    ctx,
                    arrayOf("Sutechmobile@gmail.com"),
                    "Feedback to My diary ${BuildConfig.VERSION_NAME}",
                    ""
                )
            }
            drawer_layout.closeDrawer(GravityCompat.START)
        }

        ll_update_to_pro?.setOnClick(1500) {
            logEvent("SettingScr_Upgrade_Clicked")
            gotoFrag(R.id.mainFrag, R.id.action_mainFrag_to_IAPFragment)
            drawer_layout.closeDrawer(GravityCompat.START)
        }

        cl_use_password.setOnClick(500) {
            if (!DataStore.getUsePassword()) {
                if (DataStore.getPassword().isNullOrBlank()) {
                    val bundle = Bundle()
                    bundle.putInt(Constant.TYPE_PASSWORD, 1)
                    gotoFrag(R.id.mainFrag, R.id.action_mainFrag_to_passWordFrag, bundle)
                } else {
                    DataStore.setUsePassword(true)
                }
            } else {
                DataStore.setUsePassword(false)
            }
            swPassword?.isChecked = DataStore.getUsePassword()
            if (swPassword.isChecked) {
                logEvent("SettingScr_OnPassword_Clicked")
            } else {
                logEvent("SettingScr_OffPassword_Clicked")
            }
        }

        ll_our_other_app.setOnClick(500) {
            logEvent("SettingScr_Ourotherapp_Clicked")
            AppUtil.openBrowser(
                requireContext(),
                "https://play.google.com/store/apps/developer?id=Sutech+Mobile"
            )
            drawer_layout.closeDrawer(GravityCompat.START)
        }

        ll_statistics.setOnClick(500) {
            logEvent("SettingScr_Statistics_Clicked")
            gotoFrag(R.id.mainFrag, R.id.action_mainFrag_to_statisticsFrag)
            drawer_layout.closeDrawer(GravityCompat.START)
        }

        ll_hash_tag.setOnClick(500) {
            logEvent("SettingScr_Hashtags_clicked")
            gotoFrag(R.id.mainFrag, R.id.action_mainFrag_to_hashtagsFrag)
            drawer_layout.closeDrawer(GravityCompat.START)
        }

        btnSort.setOnClick(500) {
            logEvent("MainScr_IconArrange_Clicked")
            showPopupSort()
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
        rcvData.isVisible = true
    }
    private fun setRcvDiary() {
        context?.let {
            adapterDiary =
                AdapterDiaryItem(false, it, { _, _ ->
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
                        e.printStackTrace()
                    }
                }
            }, {
                logEvent("AlertScr_IconCancel_Clicked")
                logEvent("DialogAlert_IconCancel_Clicked")
            })
        }

    }

    private fun removeFileInternal(arrImage: ArrayList<ImageObj>) {
        if (arrImage.isNotEmpty()) {
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
package com.sutech.diary.base

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.sutech.journal.diary.diarywriting.lockdiary.R
import com.sutech.diary.util.show
import kotlinx.android.synthetic.main.activity_base.*

abstract class BaseActivity(private val layout: Int) : AppCompatActivity() {
    protected var self: Context? = null
    private var dialogLoading: Dialog? = null
    protected abstract fun getToolbarType(): ToolbarType
    protected abstract fun onCreatedView()
    protected abstract fun setData()

    protected enum class ToolbarType {
        HAVE_TOOLBAR, NONE_TOOLBAR
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)
        self = this
        setView()
        onCreatedView()
        setData()

    }

    private fun setView() {
        dialogLoading = Dialog(self!!)
        dialogLoading!!.setContentView(R.layout.dialog_loading)
        dialogLoading!!.setCancelable(false)
        toolbarBack.setOnClickListener(View.OnClickListener { onBackPressed() })
        if (getToolbarType() == ToolbarType.HAVE_TOOLBAR) {
            baseToolbar.show()
        } else if (getToolbarType() == ToolbarType.NONE_TOOLBAR) {
            baseToolbar.visibility = View.GONE
        }
        layoutInflater.inflate(layout, content)
    }

    protected fun showProgress(isShow: Boolean) {
        if (isShow) {
            if (!dialogLoading!!.isShowing) {
                dialogLoading!!.show()
            }
        } else {
            if (dialogLoading!!.isShowing) {
                dialogLoading!!.dismiss()
            }
        }
    }

    protected fun setToolbarTitle(title: String?) {
        tvToolbarTitle!!.text = title
    }

    protected fun setToolbarTitle(title: Int) {
        tvToolbarTitle!!.text = getString(title)
    }
}
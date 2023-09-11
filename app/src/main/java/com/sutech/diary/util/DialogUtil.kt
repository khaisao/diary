package com.sutech.diary.util

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.util.Log
import android.view.View
import com.sutech.journal.diary.diarywriting.lockdiary.R
import kotlinx.android.synthetic.main.dialog_alert.*
import kotlinx.android.synthetic.main.dialog_confirm_save.*
import kotlinx.android.synthetic.main.dialog_loading.*

object DialogUtil {
    fun showDialogAlert(
        context: Context,
        content: String,
        onClickOk: () -> Unit,
        onClickCancel: () -> Unit
    ) {
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.dialog_alert)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.tvContentDialog.text = content
        dialog.btnConfirmDialog.setOnClickScaleView {
            onClickOk()
            dialog.cancel()
        }
        dialog.btnCancelDialog.setOnClickScaleView {
            onClickCancel()
            dialog.cancel()
        }

        dialog.show()

    }

    fun showDialogConfirmSave(
        context: Context,
        onClickOk: () -> Unit,
        onClickCancel: () -> Unit
    ) {
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.dialog_confirm_save)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.btnSaveDialog.setOnClickScaleView {
            onClickOk()
            dialog.cancel()
        }
        dialog.btnDiscard.setOnClickScaleView {
            onClickCancel()
            dialog.cancel()
        }

        dialog.show()

    }


    private var dialogLoading: Dialog? = null

    @SuppressLint("SetTextI18n")
    fun showDialogLoading(
        context: Context,
        content: String,
        sideContent: String? = null
    ) {
        if (dialogLoading == null) {
            dialogLoading = Dialog(context)
            dialogLoading?.setContentView(R.layout.dialog_loading)
            dialogLoading?.setCancelable(false)
            dialogLoading?.window?.setBackgroundDrawableResource(android.R.color.transparent)
            dialogLoading?.tvLoadingContent?.text = content
            if (!sideContent.isNullOrBlank()) {
                dialogLoading?.tvSideContent?.show()
                dialogLoading?.tvSideContent?.text = "($sideContent)"
            } else {
                dialogLoading?.tvSideContent?.visibility = View.GONE
            }
        }
        Log.e("TAG", "showDialogLoading: ????")
        dialogLoading?.show()
    }

    fun cancelDialogLoading() {
        Log.e("TAG", "cancelDialogLoading: ????")
        dialogLoading?.cancel()
        dialogLoading = null
    }
}
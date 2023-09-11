package com.test.dialognew

import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import kotlinx.android.synthetic.main.dialog_rate.view.*

class DialogLib {
    private var dialogNewInterface: DialogNewInterface? = null

    companion object {
        private var instance: DialogLib? = null

        fun getInstance(): DialogLib {
            if (instance == null)
                instance = DialogLib()
            return instance!!
        }
    }

    fun openMarket2(
        context: Context,
        packageName: String
    ) {
        val i =
            Intent(Intent.ACTION_VIEW)
        try {
            i.data = Uri.parse("market://details?id=$packageName")
            context.startActivity(i)
        } catch (ex: ActivityNotFoundException) {
//                openBrowser(
//                    context,
//                    com.volio.textonphoto.PhotorTool.BASE_GOOGLE_PLAY + packageName
//                )
        }
    }


    private fun openMarket(context: Context, packageName: String) {
        val uri = Uri.parse("market://details?id=" + packageName)
        val goToMarket = Intent(Intent.ACTION_VIEW, uri)
        goToMarket.addFlags(
            Intent.FLAG_ACTIVITY_NO_HISTORY or
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK
        )
        try {
            context.startActivity(goToMarket)
        } catch (e: ActivityNotFoundException) {
            context.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + packageName)
                )
            )
        }
    }

    private fun resetStar(view: View) {
        view.ivStar1.setImageResource(R.drawable.ic_un_star_up)
        view.ivStar2.setImageResource(R.drawable.ic_un_star_up)
        view.ivStar3.setImageResource(R.drawable.ic_un_star_up)
        view.ivStar4.setImageResource(R.drawable.ic_un_star_up)
        view.ivStar5.setImageResource(R.drawable.ic_un_star_up)
    }

    private fun isEnable(view: View, isEnable: Boolean) {
        if (isEnable) {
            view.ivStar1.isEnabled = true
            view.ivStar2.isEnabled = true
            view.ivStar3.isEnabled = true
            view.ivStar4.isEnabled = true
            view.ivStar5.isEnabled = true
        } else {
            view.ivStar1.isEnabled = false
            view.ivStar2.isEnabled = false
            view.ivStar3.isEnabled = false
            view.ivStar4.isEnabled = false
            view.ivStar5.isEnabled = false
        }

    }

    private fun openBrowser(context: Context, url: String) {
        var url = url
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "http://$url"
        }
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        try {
            context.startActivity(browserIntent)
        } catch (ex: java.lang.Exception) {
            ex.printStackTrace()
        }
    }


    private var rateCallback: RateCallback? = null

    fun showDialogRate(
        context: Context?,
        lifecycle: Lifecycle,
        dialogNewInterface1: DialogNewInterface,
        rateCallback1: RateCallback
    ) {
        if (context == null) return
        var isShow = false
        rateCallback = rateCallback1
        this.dialogNewInterface = dialogNewInterface1
        val animation = AnimationUtils.loadAnimation(context, R.anim.anim_rate)

        var numRate = 0
        val dialog: Dialog
        val view: View = LayoutInflater.from(context).inflate(
            R.layout.dialog_rate,
            null
        )
        val builder = android.app.AlertDialog.Builder(context)
        builder.setView(view)
        dialog = builder.create()
        dialog.setCanceledOnTouchOutside(false)
        dialog.setCancelable(false)
        dialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        if (!dialog.isShowing) {
            dialog.show()
            resetStar(view)
            isEnable(view, true)
            view.btnRate.visibility = View.VISIBLE
        }

        view.ivStar1.setPreventDoubleClickScaleView(300) {
            view.btnRate.showIfInv()
            view.btnRate.text = context.getString(R.string.feedback_to_us)
            numRate = 1
            dialogNewInterface?.onClickStar(numRate)
            RateUtil.star1(view)
            view.tvContents.text = context.getString(R.string.content_rate_1_star)
        }
        view.ivStar2.setPreventDoubleClickScaleView(300) {
            view.btnRate.showIfInv()
            numRate = 2
            dialogNewInterface?.onClickStar(numRate)
            view.btnRate.text = context.getString(R.string.feedback_to_us)
            RateUtil.star2(view)
            view.tvContents.text = context.getString(R.string.content_rate_1_star)
        }
        view.ivStar3.setPreventDoubleClickScaleView(300) {
            view.btnRate.showIfInv()
            numRate = 3
            dialogNewInterface?.onClickStar(numRate)
            view.btnRate.text = context.getString(R.string.mail_to_us)
            RateUtil.star3(view)
            view.tvContents.text = context.getString(R.string.content_rate_3_star)
        }
        view.ivStar4.setPreventDoubleClickScaleView(300) {
            view.btnRate.showIfInv()
            numRate = 4
            dialogNewInterface?.onClickStar(numRate)
            view.btnRate.text = context.getString(R.string.mail_to_us)
            RateUtil.star4(view)
            view.tvContents.text = context.getString(R.string.content_rate_3_star)
        }
        view.ivStar5.setPreventDoubleClickScaleView(300) {
            view.btnRate.showIfInv()
            numRate = 5
            dialogNewInterface?.onClickStar(numRate)
            view.btnRate.text = context.getString(R.string.open_gp)
            RateUtil.star5(view)
//            view.imThbest.visibility = View.INVISIBLE
//            view.img.visibility = View.INVISIBLE
            view.tvContents.text = context.getString(R.string.content_rate_5_star)
        }

        view.ivCancel.setPreventDoubleClickScaleView(300) {
            isShow = false
            dialog.dismiss()
            dialogNewInterface?.onCancel()
        }

        view.btnRate.setPreventDoubleClickScaleView(300) {
            dialogNewInterface?.onRate(numRate)
            dialog.dismiss()
            if (numRate > 4) {
                dialog.dismiss()
                isShow = false
            } else {
                isShow = false
                rateCallback?.onFBShow()
                dialog.dismiss()

            }

        }

        lifecycle.addObserver(LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> {
                    if (dialog.isShowing) {
                        isShow = true
                    }
                    dialog.dismiss()
                }

                Lifecycle.Event.ON_RESUME -> {
                    if (isShow) {
                        dialog.show()
                    }
                }
                else -> {

                }
            }
        })
    }
}
package com.test.dialognew

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.graphics.Rect
import android.os.SystemClock
import android.util.TypedValue
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.dialog_rate.view.*

fun convertDpToPx(resources: Resources, dp: Float): Float =
        TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                resources.displayMetrics
        )

fun View.setCompatColor(@ColorRes color: Int) {
    setBackgroundColor(ContextCompat.getColor(context, color))
}

fun CardView.setCardCompatColor(@ColorRes color: Int) {
    setCardBackgroundColor(ContextCompat.getColor(context, color))
}

fun Fragment.displayToast(@StringRes message: Int) {
    Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
}

fun Fragment.displayToast(message: String) {
    Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
}

fun Fragment.displayToastTop(message: String) {
    val toast = Toast.makeText(activity, message, Toast.LENGTH_SHORT)
    toast.setGravity(Gravity.TOP, 0, 250)
    toast.show()
}

fun View.inv() {
    visibility = View.INVISIBLE
}

fun View.show(isShow: Boolean) {
    if (isShow) {
        if (visibility == View.VISIBLE) return
        visibility = View.VISIBLE
    } else {
        if (visibility == View.GONE) return
        visibility = View.GONE
    }
}

fun View.showIfInv() {
    if (visibility == View.INVISIBLE)
        visibility = View.VISIBLE
}

fun View.isShow(): Boolean {
    return visibility == View.VISIBLE
}

fun View.setPreventDoubleClick(debounceTime: Long, action: () -> Unit) {
    this.setOnClickListener(object : View.OnClickListener {
        private var lastClickTime: Long = 0
        override fun onClick(v: View?) {
            if (SystemClock.elapsedRealtime() - lastClickTime < debounceTime) return
            action()
            lastClickTime = SystemClock.elapsedRealtime()
        }
    })
}

fun View.setPreventDoubleClickScaleView(debounceTime: Long, action: () -> Unit) {
    setOnTouchListener(object : View.OnTouchListener {
        private var lastClickTime: Long = 0
        private var rect: Rect? = null

        override fun onTouch(v: View, event: MotionEvent): Boolean {
            fun setScale(scale: Float) {
                v.scaleX = scale
                v.scaleY = scale
            }

            if (event.action == MotionEvent.ACTION_DOWN) {
                //action down: scale view down
                rect = Rect(v.left, v.top, v.right, v.bottom)
                setScale(0.9f)
            } else if (rect != null && !rect!!.contains(
                            v.left + event.x.toInt(),
                            v.top + event.y.toInt()
                    )
            ) {
                //action moved out
                setScale(1f)
                return false
            } else if (event.action == MotionEvent.ACTION_UP) {
                //action up
                setScale(1f)
                //handle click too fast
                if (SystemClock.elapsedRealtime() - lastClickTime < debounceTime) {
                } else {
                    lastClickTime = SystemClock.elapsedRealtime()
                    action()
                }
            } else {
                //other
            }

            return true
        }
    })
}

fun View.rotateImage() {
    val anim = RotateAnimation(
            0f, 360f,
            Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f
    )
    anim.interpolator = LinearInterpolator()
    anim.duration = 500
    anim.isFillEnabled = true
    anim.repeatCount = Animation.INFINITE
    anim.fillAfter = true
    startAnimation(anim)
}


fun View.setPreventDoubleClickScaleView(action: () -> Unit) {
    setOnTouchListener(object : View.OnTouchListener {
        private var lastClickTime: Long = 0
        private var rect: Rect? = null

        override fun onTouch(v: View, event: MotionEvent): Boolean {
            fun setScale(scale: Float) {
                v.scaleX = scale
                v.scaleY = scale
            }

            if (event.action == MotionEvent.ACTION_DOWN) {
                //action down: scale view down
                rect = Rect(v.left, v.top, v.right, v.bottom)
                setScale(0.9f)
            } else if (rect != null && !rect!!.contains(
                            v.left + event.x.toInt(),
                            v.top + event.y.toInt()
                    )
            ) {
                //action moved out
                setScale(1f)
                return false
            } else if (event.action == MotionEvent.ACTION_UP) {
                //action up
                setScale(1f)
                //handle click too fast
                if (SystemClock.elapsedRealtime() - lastClickTime < 500) {
                } else {
                    lastClickTime = SystemClock.elapsedRealtime()
                    action()
                }
            } else {
                //other
            }

            return true
        }
    })


}

// Author: https://github.com/sanogueralorenzo/Android-Kotlin-Clean-Architecture
/**
 * Use only from Activities, don't use from Fragment (with getActivity) or from Dialog/DialogFragment
 */
fun Activity.hideKeyboard() {
    val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    val view = currentFocus ?: View(this)
    imm.hideSoftInputFromWindow(view.windowToken, 0)
    window.decorView
}

fun View.showKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
}

/**
 * Use everywhere except from Activity (Custom View, Fragment, Dialogs, DialogFragments).
 */
fun View.hideKeyboard() {
    val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}





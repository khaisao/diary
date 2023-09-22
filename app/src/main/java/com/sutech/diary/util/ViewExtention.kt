package com.sutech.diary.util

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.Rect
import android.os.SystemClock
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import com.sutech.journal.diary.diarywriting.lockdiary.R

fun View.shake() {
    startAnimation(AnimationUtils.loadAnimation(context, R.anim.shake))
}
fun convertDpToPx(resources: Resources, dp: Float): Float =
    TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp,
        resources.displayMetrics
    )

fun View.setOnClick(debounceTime: Long, action: (v: View) -> Unit) {
    this.setOnClickListener(object : View.OnClickListener {
        private var lastClickTime: Long = 0
        override fun onClick(v: View) {
            if (SystemClock.elapsedRealtime() - lastClickTime < debounceTime) return
            action(v)
            lastClickTime = SystemClock.elapsedRealtime()
        }
    })
}

@SuppressLint("ClickableViewAccessibility")
fun View.setOnClickScaleView(debounceTime: Long =200, action: () -> Unit) {
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

fun View?.setPreventDoubleClickScaleView(debounceTime: Long ?=200, action: () -> Unit) {
    this?.setOnTouchListener(object : View.OnTouchListener {
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
                if (SystemClock.elapsedRealtime() - lastClickTime < debounceTime!!) {
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

private var lastClickTimeItem: Long = 0
fun View.setPreventDoubleClickItemScaleView(debounceTime: Long, action: () -> Unit) {
    setOnTouchListener(object : View.OnTouchListener {
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
                if (SystemClock.elapsedRealtime() - lastClickTimeItem < debounceTime) {
                } else {
                    lastClickTimeItem = SystemClock.elapsedRealtime()
                    action()
                }
            } else {
                //other
            }
            return true
        }
    })
}

fun View.setPreventDoubleClickItem(debounceTime : Long =300, action: () -> Unit) {
    this.setOnClickListener(object : View.OnClickListener {
        override fun onClick(v: View?) {
            if (SystemClock.elapsedRealtime() - lastClickTimeItem < debounceTime) return
            action()
            lastClickTimeItem = SystemClock.elapsedRealtime()
        }
    })
}

private var lastCheckTime: Long = 0
fun checkTime(debounceTime : Long =300, action: () -> Unit) {
            if (SystemClock.elapsedRealtime() - lastCheckTime < debounceTime) return
            action()
    lastCheckTime = SystemClock.elapsedRealtime()
}


fun View.showIfInv() {
    if (visibility == View.INVISIBLE)
        visibility = View.VISIBLE
}

fun View.isGone() = visibility == View.GONE
fun View.isInvisible() = visibility == View.INVISIBLE
fun View.show() {
    visibility = View.VISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

fun View.inVisible() {
    visibility = View.INVISIBLE
}

fun View.inv() {
    visibility = View.INVISIBLE
}

fun View.showSoftKeyboard() {
    this.requestFocus()
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
}

fun View.closeKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(this.windowToken, 0)
}
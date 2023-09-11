package com.sutech.common.edit_text

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.widget.EditText
import com.sutech.common.R
import com.sutech.common.ColorUtil
import com.sutech.common.TypeFaceUtil



@SuppressLint("AppCompatCustomView","CustomViewStyleable")
class EditTextMultilineMedium : EditText {
    private var mRect: Rect? = null
    private var mPaint: Paint? = null

    constructor(context: Context?) : super(context) {
        this.typeface = TypeFaceUtil.instant!!.medium
        this.setTextColor(ColorUtil.instant!!.colorEditText)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(
        context,
        attrs
    ) {


        mRect = Rect()
        mPaint = Paint()
        mPaint?.style = Paint.Style.FILL_AND_STROKE
//        mPaint?.color = ColorUtil.instant!!.colorEditText //SET YOUR OWN COLOR HERE
        mPaint?.color =    Color.parseColor("#000000") //SET YOUR OWN COLOR HERE

        this.typeface = TypeFaceUtil.instant!!.medium
        initAttr(attrs)
    }



    constructor(
        context: Context?, attrs: AttributeSet?,
        defStyle: Int
    ) : super(context, attrs, defStyle) {
        mRect = Rect()
        mPaint = Paint()
        mPaint?.style = Paint.Style.FILL_AND_STROKE
//        mPaint?.color = ColorUtil.instant!!.colorEditText //SET YOUR OWN COLOR HERE
        mPaint?.color =    Color.parseColor("#000000") //SET YOUR OWN COLOR HERE

        this.typeface = TypeFaceUtil.instant!!.medium
        initAttr(attrs)


    }

    private fun initAttr(attrs: AttributeSet?) {
        val ta = context.obtainStyledAttributes(
            attrs,
            R.styleable.text_style,
            0,
            0
        )
        val textColorDefault = ta.getBoolean(R.styleable.text_style_textColorDefault, true)
        if (textColorDefault) {
            this.setTextColor(ColorUtil.instant!!.colorEditText)
        }

        ta.recycle()
    }
    override fun onDraw(canvas: Canvas) {
        val height = height
        val lineHeight = lineHeight

        var count = height / lineHeight

        if (lineCount > count) count = lineCount //for long text with scrolling


        val r = mRect!!
        val paint = mPaint!!
        var baseline = getLineBounds(0, r) //first line


        for (i in 0 until count) {
            canvas.drawLine(
                r.left.toFloat(),
                (baseline + 1).toFloat(),
                r.right.toFloat(),
                (baseline + 1).toFloat(), paint
            )
            baseline += lineHeight //next line
        }
        super.onDraw(canvas)
    }
}
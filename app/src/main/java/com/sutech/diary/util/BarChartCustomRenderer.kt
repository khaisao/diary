package com.sutech.diary.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import com.github.mikephil.charting.animation.ChartAnimator
import com.github.mikephil.charting.interfaces.dataprovider.BarDataProvider
import com.github.mikephil.charting.renderer.BarChartRenderer
import com.github.mikephil.charting.utils.Utils
import com.github.mikephil.charting.utils.ViewPortHandler
import com.sutech.journal.diary.diarywriting.lockdiary.R

class BarChartCustomRenderer(aChart: BarDataProvider, aAnimator: ChartAnimator, aViewPortHandler: ViewPortHandler, aImageList: ArrayList<Bitmap>, aContext: Context) : BarChartRenderer(aChart, aAnimator, aViewPortHandler) {
    private val mContext = aContext
    private val mImageList = aImageList

    override fun drawData(c: Canvas?) {
        initBuffers()
        super.drawData(c)
    }

    override fun drawValues(aCanvas: Canvas) {
        if (!isDrawingValuesAllowed(mChart)) {
            return
        }

        val datasets = mChart.barData.dataSets
        val valueOffsetPlus = Utils.convertDpToPixel(22f)
        val valueTextHeight = Utils.calcTextHeight(mValuePaint, "8")

        for ((index, set) in datasets.withIndex()) {
            if (!shouldDrawValues(set)) {
                continue
            }

            applyValueTextStyle(set)

            var posOffset = if (mChart.isDrawValueAboveBarEnabled) {
                -valueOffsetPlus
            } else {
                valueTextHeight + valueOffsetPlus
            }

            var negOffset = if (mChart.isDrawValueAboveBarEnabled) {
                valueTextHeight + valueOffsetPlus
            } else {
                -valueOffsetPlus
            }

            val inverted = mChart.isInverted(set.axisDependency)
            if (inverted) {
                posOffset = -posOffset - valueTextHeight
                negOffset = -negOffset - valueTextHeight
            }

            val buffer = mBarBuffers[index]
            mAnimator.phaseY
            val formatter = set.valueFormatter
            var j = -4

            // iconsOffset

            while (true) {
                j += 4
                if (j >= buffer.buffer.size * mAnimator.phaseX) {
                    break
                }

                val left = buffer.buffer[j]
                val top = buffer.buffer[j + 1]
                val right = buffer.buffer[j + 2]
                val bottom = buffer.buffer[j + 3]

                val x = (left + right) / 2f

                if (!mViewPortHandler.isInBoundsRight(x)) {
                    break
                }

                if (!mViewPortHandler.isInBoundsY(top) or !mViewPortHandler.isInBoundsLeft(x)) {
                    continue
                }

                val entry = set.getEntryForIndex(j / 4)
                val y = if (entry.y >= 0) {
                    top + posOffset
                } else {
                    bottom + negOffset
                }

                if (set.isDrawValuesEnabled) {
                    drawValue(aCanvas, formatter.getBarLabel(entry), x, y, set.getValueTextColor(j / 4))
                }

                val bitmap = mImageList.getOrNull(j / 4)
                if (bitmap != null) {
                    val scaledBitmap = getScaledBitmap(bitmap)
                    aCanvas.drawBitmap(scaledBitmap, x - scaledBitmap.width / 2f, (bottom + 0.5f * negOffset) - scaledBitmap.width / 2f, null)
                }
            }
        }
    }

    private fun getScaledBitmap(aBitmap: Bitmap): Bitmap {
        val width = mContext.resources.getDimension(R.dimen.img_s_tiny).toInt()
        val height = mContext.resources.getDimension(R.dimen.img_s_tiny).toInt()
        return Bitmap.createScaledBitmap(aBitmap, width, height, true)
    }
}

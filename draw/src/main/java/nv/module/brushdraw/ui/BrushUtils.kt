package nv.module.brushdraw.ui

import android.content.Context
import android.graphics.Bitmap
import android.widget.ImageView
import android.widget.TextView
import nv.module.brushdraw.R
import nv.module.brushdraw.data.BaseRx
import nv.module.brushdraw.ui.customview.HandWriteCVCallback
import nv.module.brushdraw.ui.customview.HandWriteCanvasView

class BrushUtils(private val context: Context,private val saveView:(bitmap:Bitmap)->Unit,private val onBack:()->Unit) {
   private var canvasView: HandWriteCanvasView?=null
    fun createBrush(
        btnUndo: ImageView,
        btnRedo: ImageView,
        btnBack: ImageView,
        btnSave: TextView,
        canvasView: HandWriteCanvasView
    ) {
        listener(
            btnUndo,
            btnRedo,
            btnBack,
            btnSave, canvasView
        )

        this.canvasView = canvasView
    }

    fun setColorBrush(color:Int){
        canvasView?.setColor(color)
    }

    fun setBackground(color:Int){
        canvasView?.setBackgroundColor(color)
    }

    private fun listener(
        btnUndo: ImageView,
        btnRedo: ImageView,
        btnBack: ImageView,
        btnSave: TextView,
        canvasView: HandWriteCanvasView
    ) {
        btnSave.setOnClickListener {
            BaseRx().observableCreate<Bitmap>({
                canvasView.isDrawingCacheEnabled = true
                val bitmap = Bitmap.createBitmap(canvasView.drawingCache)
                canvasView.isDrawingCacheEnabled = false
                it.onNext(bitmap)
            }, {
                saveView(it)
            }, {

            })
        }

        btnBack.setOnClickListener {
            onBack()
        }

        btnUndo.setOnClickListener {
            canvasView.undo()
        }

        btnRedo.setOnClickListener {
            canvasView.reDo()
        }

        canvasView.setWriteCVCallback(object : HandWriteCVCallback {
            override fun onUndo(b: Boolean) {
                setColorFilter(btnUndo, b)
            }

            override fun onRedo(b: Boolean) {
                setColorFilter(btnRedo, b)
            }
        })
    }

    fun setColorFilter(view: ImageView, boolean: Boolean) {
        if (boolean) {
            view.setColorFilter(context.resources.getColor(R.color.color_select))
            return
        }

        view.setColorFilter(context.resources.getColor(R.color.color_black))
    }

}
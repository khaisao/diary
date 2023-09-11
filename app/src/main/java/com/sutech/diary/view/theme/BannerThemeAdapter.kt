package com.sutech.diary.view.theme

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sutech.journal.diary.diarywriting.lockdiary.R
import com.sutech.diary.model.ThemeObj
import com.sutech.diary.util.setPreventDoubleClickItem
import com.youth.banner.adapter.BannerAdapter
import kotlinx.android.synthetic.main.item_theme.view.*
import kotlinx.android.synthetic.main.toolbar_main.view.*

class BannerThemeAdapter(
    private val imageUrls: List<ThemeObj>,
    val onClickItem: (position: Int) -> Unit
) : BannerAdapter<ThemeObj, BannerThemeAdapter.ImageHolder>(imageUrls) {


    override fun onCreateHolder(parent: ViewGroup?, viewType: Int): ImageHolder {
//        val params = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
//        imageView.layoutParams = params
//        imageView.scaleType = ImageView.ScaleType.CENTER_CROP
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            BannerUtils.setBannerRound(imageView, 20f)
//        }
        val imageView =   LayoutInflater.from(parent!!.context).inflate(R.layout.item_theme, parent, false)
        return ImageHolder(imageView)
    }



    inner class ImageHolder(view: View) : RecyclerView.ViewHolder(view) {


        fun binData(data: ThemeObj, position: Int) {
            itemView.bgItemTheme.setBackgroundColor(Color.parseColor(data.backgroundColor))


            itemView.tvDay.setTextColor(Color.parseColor(data.textColor))
            itemView.tvTitleTheme.setTextColor(Color.parseColor(data.textColor))
            itemView.tvContentTheme.setTextColor(Color.parseColor(data.textColor))
            itemView.tvDay2.setTextColor(Color.parseColor(data.textColor))
            itemView.tvTitleTheme2.setTextColor(Color.parseColor(data.textColor))
            itemView.tvContentTheme2.setTextColor(Color.parseColor(data.textColor))
            itemView.setPreventDoubleClickItem {
              onClickItem(position)
            }
        }

    }

    override fun onBindView(holder: ImageHolder?, data: ThemeObj, position: Int, size: Int) {
        holder?.binData(data,position)
    }

}


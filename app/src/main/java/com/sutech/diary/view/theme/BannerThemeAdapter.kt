package com.sutech.diary.view.theme

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sutech.diary.model.ThemeObj
import com.sutech.diary.util.ImageUtil
import com.sutech.diary.util.setPreventDoubleClickItem
import com.sutech.journal.diary.diarywriting.lockdiary.R
import com.youth.banner.adapter.BannerAdapter
import kotlinx.android.synthetic.main.item_theme.view.iv_theme
import kotlinx.android.synthetic.main.item_theme.view.tvContentTheme
import kotlinx.android.synthetic.main.item_theme.view.tvContentTheme2
import kotlinx.android.synthetic.main.item_theme.view.tvDay
import kotlinx.android.synthetic.main.item_theme.view.tvDay2
import kotlinx.android.synthetic.main.item_theme.view.tvTitleTheme
import kotlinx.android.synthetic.main.item_theme.view.tvTitleTheme2

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
//            itemView.setBackgroundResource(data.imageResource)
            ImageUtil.setImage(itemView.iv_theme, data.imageResource)

            itemView.tvDay.setTextColor(data.textColor)
            itemView.tvTitleTheme.setTextColor(data.textColor)
            itemView.tvContentTheme.setTextColor(data.textColor)
            itemView.tvDay2.setTextColor(data.textColor)
            itemView.tvTitleTheme2.setTextColor(data.textColor)
            itemView.tvContentTheme2.setTextColor(data.textColor)
            itemView.setPreventDoubleClickItem {
              onClickItem(position)
            }
        }

    }

    override fun onBindView(holder: ImageHolder?, data: ThemeObj, position: Int, size: Int) {
        holder?.binData(data,position)
    }

}


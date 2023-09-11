package com.sutech.diary.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sutech.journal.diary.diarywriting.lockdiary.R
import com.sutech.diary.model.ImageObj
import com.sutech.diary.util.ImageUtil
import kotlinx.android.synthetic.main.item_view_image.view.*

class ViewPagerAdapter(private var arrImage: ArrayList<ImageObj>) :
    RecyclerView.Adapter<PagerVH>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagerVH =
        PagerVH(
            LayoutInflater.from(parent.context).inflate(R.layout.item_view_image, parent, false)
        )

    override fun getItemCount(): Int = arrImage.size

    override fun onBindViewHolder(holder: PagerVH, position: Int): Unit = holder.itemView.run {
        val image = arrImage[position]
        if (image.bitmap != null) {
            ImageUtil.setImage(imgView, image.bitmap)
        } else {
            Log.e("TAG", "onBindViewHolder: ${image.path}")
//            Log.e("TAG", "onBindViewHolder: ${File(image.path).exists()}")
            ImageUtil.setImage(imgView, image.path)
        }
    }
}

class PagerVH(itemView: View) : RecyclerView.ViewHolder(itemView)
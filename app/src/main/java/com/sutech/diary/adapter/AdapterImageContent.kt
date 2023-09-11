package  com.sutech.diary.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sutech.journal.diary.diarywriting.lockdiary.R
import com.sutech.diary.model.ImageObj
import com.sutech.diary.util.ImageUtil
import com.sutech.diary.util.show
import kotlinx.android.synthetic.main.item_image_content_edit.view.*
import kotlinx.android.synthetic.main.item_image_content_view.view.*

class AdapterImageContent(
    private var arrImageObj: ArrayList<ImageObj>,
    private val onClickItemImage: (position: Int) -> Unit,
    private val onDeleteItemImage: (position: Int) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var isWrite = false
    var isView = false
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (!isView) {
            val view: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_image_content_edit, parent, false)
            ItemImage(view)

        } else {
            val view: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_image_content_view, parent, false)
            ItemImageContentView(view)
        }
    }


    override fun getItemCount(): Int {
        return if (isWrite) {
            arrImageObj.size
        } else {
            if (!isView) {
                if (arrImageObj.size > 3) {
                    3
                } else {
                    arrImageObj.size
                }
            } else {
                return arrImageObj.size
            }
        }

    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ItemImage) {
            holder.binDataImage(position)
        } else if (holder is ItemImageContentView) {
           holder.binDataImage(position)
        }
    }

    @SuppressLint("SetTextI18n")
    private inner class ItemImage(view: View) : RecyclerView.ViewHolder(view) {

        fun binDataImage(position: Int) {
            val image = arrImageObj[position]
            if (!image.path.isNullOrBlank()) {
                ImageUtil.setImage(itemView.imgThumbImageEdit, image.path!!)
            } else if (image.bitmap != null) {
                ImageUtil.setImage(itemView.imgThumbImageEdit, image.bitmap)
            }


            if (isWrite) {
                itemView.btnDeleteImage.show()
                itemView.transparentView.show()
            } else {
                if (arrImageObj.size > 3&&position == 2) {
                    itemView.transparentView.show()
                    itemView.tvImageLeft.show()
                    itemView.tvImageLeft.text = "+${arrImageObj.size - 3}"
                } else {
                    itemView.tvImageLeft.visibility = View.GONE
                }
            }
            itemView.setOnClickListener {
                onClickItemImage(position)
            }
            itemView.btnDeleteImage.setOnClickListener {
                onDeleteItemImage(position)
            }

        }
    }

    @SuppressLint("SetTextI18n")
    private inner class ItemImageContentView(view: View) : RecyclerView.ViewHolder(view) {
        fun binDataImage(position: Int) {
            val image = arrImageObj[position]
            if (!image.path.isNullOrBlank()) {
                ImageUtil.setImage(itemView.imgThumb, image.path!!)
            } else if (image.bitmap != null) {
                ImageUtil.setImage(itemView.imgThumb, image.bitmap)
            }
            itemView.setOnClickListener {
                onClickItemImage(position)
            }

        }
    }

}
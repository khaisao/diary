package com.sutech.diary.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.sutech.diary.adapter.diffUtil.ContentDiffCallBack
import com.sutech.diary.model.ContentModel
import com.sutech.diary.util.ImageUtil
import com.sutech.diary.util.checkTime
import com.sutech.diary.util.setPreventDoubleClickItem
import com.sutech.diary.util.show
import com.sutech.journal.diary.diarywriting.lockdiary.R
import kotlinx.android.synthetic.main.item_content.view.*

private const val TAG = "AdapterDiaryItem"

class AdapterContent(
    private val onClickItem: (position: Int) -> Unit,
    private val onClickMore: (position: Int) -> Unit,
    private val onClickEdit: (position: Int) -> Unit,
    private val onClickDelete: (position: Int) -> Unit
) : ListAdapter<ContentModel, ViewHolder>(ContentDiffCallBack()) {
    lateinit var adapterImage: AdapterImageContent

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolderContent(inflater.inflate(R.layout.item_content, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        (holder as ViewHolderContent).binDataContent(position, currentList[position])
    }

    inner class ViewHolderContent(view: View) : RecyclerView.ViewHolder(view) {
        @SuppressLint("SetTextI18n")
        fun binDataContent(position: Int, content: ContentModel) {
            itemView.tvTitle.text = content.title
            itemView.tvContent.text = content.content
            if (content.content.isEmpty()) {
                itemView.rlDiaryImage.visibility = View.VISIBLE
                itemView.tvContent.visibility = View.GONE
                itemView.imgPreview.visibility = View.GONE
                itemView.imgMoreImage.visibility = View.GONE
                itemView.tvMoreImage.visibility = View.GONE
            } else {
                   itemView.tvContent.visibility =  View.VISIBLE
                   itemView.imgPreview.visibility = View.VISIBLE
                itemView.rlDiaryImage.visibility = View.GONE
                itemView.imgPreview.visibility = View.VISIBLE
                itemView.imgMoreImage.visibility = View.VISIBLE
                itemView.tvMoreImage.visibility = View.VISIBLE
            }
            if (content.images.size > 0) {
                if (content.images.size > 1) {
                    itemView.tvMoreImage.text = "+${content.images.size - 1}"
                    itemView.tvMoreDiaryImage.text = "+${content.images.size - 1}"
                } else {
                    itemView.tvMoreImage.visibility = View.GONE
                    itemView.imgMoreImage.visibility = View.GONE

                    itemView.imgMoreDiaryImage.visibility = View.GONE
                    itemView.imgMoreDiaryImage.visibility = View.GONE
                }
                if (!content.images[0].path.isNullOrBlank()) {
                    ImageUtil.setImage(itemView.imgPreview, content.images[0].path!!)
                    ImageUtil.setImage(itemView.imgDiaryImagePreview, content.images[0].path!!)
                } else if (content.images[0].bitmap != null) {
                    ImageUtil.setImage(itemView.imgPreview, content.images[0].bitmap)
                    ImageUtil.setImage(itemView.imgDiaryImagePreview, content.images[0].bitmap)
                } else {
                    itemView.imgPreview.visibility = View.GONE
                    itemView.imgDiaryImagePreview.visibility = View.GONE
                }

            } else {
                itemView.imgMoreImage.visibility = View.GONE
                itemView.imgPreview.visibility = View.GONE
                itemView.tvMoreImage.visibility = View.GONE

                itemView.rlDiaryImage.visibility = View.GONE
            }

            itemView.btnMore.setPreventDoubleClickItem {
                onClickMore(position)
                val popup = PopupMenu(itemView.context, itemView.btnMore)
                popup.inflate(R.menu.menu_item_content)
                //adding click listener
                //adding click listener
                popup.setOnMenuItemClickListener { item ->
                    when (item.getItemId()) {
                        R.id.menuEdit -> {
                            checkTime {
                                onClickEdit(position)//handle menu1 click
                            }
                            true
                        }
                        R.id.menuDelete -> {
                            checkTime {
                            onClickDelete(position)//handle menu2 click

                        }
                            true
                        }
                        else -> false
                    }
                }
                //displaying the popup
                //displaying the popup
                popup.show()


            }
            itemView.setPreventDoubleClickItem {
                onClickItem(position)
            }
        }
    }
}
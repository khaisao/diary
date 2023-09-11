package  com.sutech.diary.view.chooseImage.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sutech.journal.diary.diarywriting.lockdiary.R
import com.sutech.diary.model.ImageObj
import com.sutech.diary.util.ImageUtil
import com.sutech.diary.util.show
import kotlinx.android.synthetic.main.item_image.view.*

class AdapterChooseImage(
    private var arrImageObj: ArrayList<ImageObj>,
    private val onClickItemVideo: (position: Int) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_image, parent, false)
        return ItemImage(view)
    }


    override fun getItemCount(): Int {
        return arrImageObj.size
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) =
        (holder as ItemImage).binDataImage(position)

    private inner class ItemImage(view: View) : RecyclerView.ViewHolder(view) {

        fun binDataImage(position: Int) {
            val image = arrImageObj[position]
            ImageUtil.setImage(itemView.imgThumb,  Uri.parse(image.path),200,200)
            if (image.isSelected){
                itemView.rlSelectImg.show()
            }else{
                itemView.rlSelectImg.visibility = View.GONE
            }
            itemView.setOnClickListener {
                onClickItemVideo(position)
            }

        }
    }

}
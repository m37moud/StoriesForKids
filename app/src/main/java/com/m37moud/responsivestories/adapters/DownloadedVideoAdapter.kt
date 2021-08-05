package com.m37moud.responsivestories.adapters

import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.m37moud.responsivestories.ui.fragments.story.OfflinePlayerActivity
import com.m37moud.responsivestories.R
import com.m37moud.responsivestories.data.database.entity.VideoEntity2
import com.m37moud.responsivestories.util.VideosDiffUtil
import java.util.*
import kotlin.collections.ArrayList

class DownloadedVideoAdapter(
    var context: Context
) : RecyclerView.Adapter<DownloadedVideoAdapter.HolderVideo>() {
//room changed3/8
    var vidList = emptyList<VideoEntity2>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderVideo {
        val view = LayoutInflater.from(context).inflate(R.layout.row_video, parent, false)
        return HolderVideo(
            view
        )
    }

    override fun getItemCount(): Int {
        return vidList.size
    }

    override fun onBindViewHolder(holder: HolderVideo, position: Int) {
        val videoModel = vidList[position]

        val title: String? = videoModel.title
        val date: String? = videoModel.timestamp
        val url: String? = videoModel.videoUri
        val thumb: String? = videoModel.videoThumb

        val cal = Calendar.getInstance()
        cal.timeInMillis = date!!.toLong()
        val formated = DateFormat.format("dd/MM/yyy K:mm a", cal).toString()


//        holder.vidImg.load(url)
        val requestOptions = RequestOptions()
            .placeholder(R.drawable.ic_error_placeholder)
        if(TextUtils.isEmpty(thumb)){
            Glide.with(context)
                .applyDefaultRequestOptions(requestOptions)
                .asDrawable()
                .load(url).into(holder.vidImg)
        }else{
            Glide.with(context)
                .applyDefaultRequestOptions(requestOptions)
                .asDrawable()
                .load(thumb).into(holder.vidImg)
        }


        holder.vidtitle.text = title
        holder.viddate.text = formated
//        setVideoUrl(videoModel,holder)
        holder.itemView.setOnClickListener {
            val intent = Intent(it.context, OfflinePlayerActivity::class.java)
            intent.putExtra("videoUri", url)
            context.startActivity(intent)

        }


    }


    class HolderVideo(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var vidImg: ImageView = itemView.findViewById(R.id.vid_img)

        //        var vidView: VideoView = itemView.findViewById(R.id.vid_show)
        var vidtitle: TextView = itemView.findViewById(R.id.title_vid)
        var viddate: TextView = itemView.findViewById(R.id.date_vid)
//        var progress : ProgressBar = itemView.findViewById(R.id.progress_bar    )

    }
//room change
    fun setData(newData: ArrayList<VideoEntity2>) {
        val recipesDiffUtil =
            VideosDiffUtil(vidList, newData)
        val diffUtilResult = DiffUtil.calculateDiff(recipesDiffUtil)
        vidList = newData
        diffUtilResult.dispatchUpdatesTo(this)
    }
}
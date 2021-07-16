package com.m37moud.responsivestories.adapters

import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.m37moud.responsivestories.R
import com.m37moud.responsivestories.models.VideoModel
import com.m37moud.responsivestories.ui.fragments.story.OnlinePlayerActivity
import com.m37moud.responsivestories.util.VideosDiffUtil
import java.util.*


class VideoAdapter(
    var context: Context
//    var vidList: ArrayList<VideoModel>
) : RecyclerView.Adapter<VideoAdapter.HolderVideo>() {

    var vidList = emptyList<VideoModel>()
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

//        val id: String? = videoModel.id
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
            val intent = Intent(it.context, OnlinePlayerActivity::class.java)
            intent.putExtra("videoUri", url)
            context.startActivity(intent)

        }


    }


//    private fun setVideoUrl(
//        videoModel: VideoModel,
//        holder: HolderVideo
//    ) {
//
////        holder.progress.visibility = View.VISIBLE
//
//        //get video url
//        val videoUrl = videoModel.videoUri
//
//        //media controller
//        val mediaController = MediaController(context)
//        mediaController.setAnchorView(holder.vidView)
//
//        val vidUri = Uri.parse(videoUrl)
//        holder.vidView.setMediaController(mediaController)
//        holder.vidView.setVideoURI(vidUri)
//        holder.vidView.requestFocus()
//
//        holder.vidView.setOnPreparedListener {mediaPlayer ->
//            //video is prepared to play
//            mediaPlayer.start()
//
//        }
//        holder.vidView.setOnInfoListener(MediaPlayer.OnInfoListener { mediaPlayer, what, extra ->
//
//            //check if buffering and rendering
//            when(what){
//                MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START ->
//                {
//                    //rendering started
////                    holder.progress.visibility = View.VISIBLE
//                    return@OnInfoListener true
//                }
//                MediaPlayer.MEDIA_INFO_BUFFERING_START ->{
//                    //buffering started
////                    holder.progress.visibility = View.VISIBLE
//                    return@OnInfoListener true
//                }
//                MediaPlayer.MEDIA_INFO_BUFFERING_END ->{
//                    //buffering end
////                    holder.progress.visibility = View.GONE
//                    return@OnInfoListener true
//                }
//            }
//            false
//        })
//
//        holder.vidView.setOnCompletionListener {mediaPlayer ->
//            //restart video when complete | loop video
//            mediaPlayer.start()
//
//        }
//
//    }

    class HolderVideo(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var vidImg: ImageView = itemView.findViewById(R.id.vid_img)

        //        var vidView: VideoView = itemView.findViewById(R.id.vid_show)
        var vidtitle: TextView = itemView.findViewById(R.id.title_vid)
        var viddate: TextView = itemView.findViewById(R.id.date_vid)
//        var progress : ProgressBar = itemView.findViewById(R.id.progress_bar    )

    }

    fun setData(newData: ArrayList<VideoModel>) {
        val recipesDiffUtil =
            VideosDiffUtil(vidList, newData)
        val diffUtilResult = DiffUtil.calculateDiff(recipesDiffUtil)
        vidList = newData
        diffUtilResult.dispatchUpdatesTo(this)
    }
}
package com.m37moud.responsivestories.adapters

import android.content.Context
import android.net.Uri
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.m37moud.responsivestories.R
import com.m37moud.responsivestories.models.LearnModel
import com.m37moud.responsivestories.util.Constants
import com.m37moud.responsivestories.util.Constants.Companion.RESOURCE
import kotlinx.android.synthetic.main.category_learn_items.view.*
import java.util.*
import java.util.Collections.emptyList

class LearnAdapter constructor(var context: Context, val mItemClickListener: ItemClickListener) :
    RecyclerView.Adapter<LearnAdapter.LearnViewHolder>() {
    init {
        displayTitles()
    }

    private var learnTitle = emptyList<LearnModel>()

    interface ItemClickListener {
        fun onItemClick(position: Int)
    }

    inner class LearnViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var learnContainer: LinearLayout = itemView.findViewById(R.id.category_learn_container)

        var learnImg: ImageView = itemView.findViewById(R.id.img_title)

        var learnTitle: TextView = itemView.findViewById(R.id.txt_title)

        init {
            itemView.setOnClickListener {
                mItemClickListener.onItemClick(adapterPosition)
            }
        }

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LearnViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.category_learn_items, parent, false)
        return LearnViewHolder(view)
    }

    override fun getItemCount(): Int {
        return learnTitle.size
    }

    override fun onBindViewHolder(holder: LearnViewHolder, position: Int) {

        val learnCategory = learnTitle[position]
        val uri =
            Uri.parse(RESOURCE + learnCategory.img)


        val requestOptions = RequestOptions()
            .diskCacheStrategy(DiskCacheStrategy.DATA)
            .placeholder(R.drawable.ic_error_placeholder)
        Glide.with(context)
            .applyDefaultRequestOptions(requestOptions)
            .asDrawable()
            .load(uri.toString()).into(holder.itemView.img_title)

//        holder.itemView.img_title.load(uri.toString()) {
////            crossfade(600)
//            error(R.drawable.ic_error_placeholder)
//        }
        holder.itemView.txt_title.text = learnCategory.title
    }

    //    val img = listOf<String>("animals", "colors", "shapes", "numbers", "alphabet")

    fun displayTitles() {
        val list: MutableList<LearnModel> = ArrayList()

        val img = ArrayList<String>()
        img.add(context.getString(R.string.animals))
        img.add(context.getString(R.string.colors))
        img.add(context.getString(R.string.shapes))
        img.add(context.getString(R.string.numbers))
        img.add(context.getString(R.string.alphabets))
//
        for (i in 0 until img.size) {

            val item: LearnModel = LearnModel(Constants.img[i], img[i], "")
            list.add(item)
            learnTitle = list
            notifyDataSetChanged()
        }


    }


    fun getCategoryName(position: Int): LearnModel? {
        return learnTitle[position]
    }


    fun initEnimTouchView(holder: LearnViewHolder) {




    }


}
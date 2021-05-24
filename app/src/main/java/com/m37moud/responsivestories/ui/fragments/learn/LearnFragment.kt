package com.m37moud.responsivestories.ui.fragments.learn

import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.m37moud.responsivestories.R
import com.m37moud.responsivestories.adapters.LearnAdapter
import com.m37moud.responsivestories.models.LearnModel
import com.m37moud.responsivestories.util.Constants
import com.m37moud.responsivestories.util.Constants.Companion.RESOURCE
import com.m37moud.responsivestories.util.MediaService
import kotlinx.android.synthetic.main.fragment_learn.*
import kotlinx.android.synthetic.main.fragment_learn.view.*
import kotlinx.android.synthetic.main.fragment_third_screen.*

class LearnFragment : Fragment(), LearnAdapter.ItemClickListener {
    private val mAdapter: LearnAdapter by lazy { LearnAdapter(requireContext(), this) }
    private var category: LearnModel? = null
    private var textCategory: TextView? = null
    private var textImgCategory: ImageView? = null

    private var shouldPlay = false
    private var categoryPosition = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_learn, container, false)
        textCategory = view.findViewById(R.id.cat_txt_title)
        textImgCategory = view.findViewById(R.id.category_img)

        //start service and play music

        startService()
        //start service and play music
        display()

        setupRecyclerView(view)


        view.img_click.setOnClickListener {
            val intent = Intent(requireContext(), EnteredLearenActivity::class.java)

//            val url = cat_txt_title.text
            //get image name from Constans list
            val url = Constants.img[categoryPosition]
            intent.putExtra("selectedCategory", url)
            shouldPlay = true
            startActivity(intent)
            finish
        }


        return view
    }


    private fun setupRecyclerView(view: View) {
        view.rv_title.adapter = mAdapter
        view.rv_title.setHasFixedSize(true)
        view.rv_title.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
    }

    private fun display() {
        mAdapter.displayTitles()
        Log.d("LearnFragment", "clicked: $category")
        if (category == null) {
            category = LearnModel("animals", "الحيوانات", "")
            initToNextPage(this.category!!)
        }
        initToNextPage(this.category!!)


    }

    private fun initToNextPage(cat: LearnModel) {


        val uri =
            Uri.parse(RESOURCE + cat.img)
        textImgCategory?.load(uri) {
            crossfade(300)
        }
        textCategory?.text = cat.title


    }

    override fun onItemClick(position: Int) {
        categoryPosition = position
        val category = mAdapter.getCategoryName(position)
        initToNextPage(category!!)
        val categoryName = category.img
        if (categoryName != null) playImgSound(categoryName)

        Log.d("LearnFragment", "clicked: $position")
    }

    private fun playImgSound(name: String) {
        var path: String? = null

        try {
            val newName = removeLastChar(name)

            path = "sound/" + newName + "ar.mp3"

            Log.d("soundmd", "play: " + path)
            val mediaPlayer = MediaPlayer()

            val descriptor = context?.assets?.openFd(path)
            if (descriptor != null) {
                mediaPlayer.setDataSource(
                    descriptor.fileDescriptor,
                    descriptor.startOffset,
                    descriptor.length
                )
                descriptor.close()
            }

            mediaPlayer.prepare()
            mediaPlayer.setVolume(1f, 1f)
            mediaPlayer.isLooping = false
            mediaPlayer.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    private fun removeLastChar(str: String?): String? {
        var str = str
        if (str != null && str.isNotEmpty() && str[str.length - 1] == 's') {
            str = str.substring(0, str.length - 1)
        }
        return str
    }


    override fun onDestroy() {
        stopService()
        super.onDestroy()
    }


    override fun onStop() {
        super.onStop()
        if (!shouldPlay) {
            stopService()
        }

    }

    override fun onStart() {
        startService()
        shouldPlay = false
        super.onStart()
    }

    override fun onResume() {
        startService()
        shouldPlay = false
        super.onResume()
    }

    private fun startService() {
        val intent = Intent(context, MediaService::class.java)
        if (context != null) {
            context?.startService(intent)
        }
    }


    private fun stopService() {
        val intent = Intent(context, MediaService::class.java)
        if (context != null) {
            context?.stopService(intent)
        }
    }
}
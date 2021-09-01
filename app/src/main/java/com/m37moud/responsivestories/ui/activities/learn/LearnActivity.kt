package com.m37moud.responsivestories.ui.activities.learn

import android.content.Intent
import android.content.res.Configuration
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.m37moud.responsivestories.R
import com.m37moud.responsivestories.adapters.LearnAdapter
import com.m37moud.responsivestories.databinding.ActivityLearnBinding
import com.m37moud.responsivestories.models.LearnModel
import com.m37moud.responsivestories.util.Constants
import com.m37moud.responsivestories.util.Constants.Companion.RESOURCE
import com.m37moud.responsivestories.util.MediaService
import kotlinx.android.synthetic.main.activity_learn.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_third_screen.*

class LearnActivity : AppCompatActivity(), LearnAdapter.ItemClickListener {

    private var _binding: ActivityLearnBinding ? = null
    private val binding get() = _binding!!
    private val mAdapter: LearnAdapter by lazy { LearnAdapter(this@LearnActivity, this) }
    private var category: LearnModel? = null


    private var shouldPlay = false
    private var categoryPosition = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityLearnBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        //start service and play music
        if (!shouldPlay) {
            shouldPlay = true
            startService()
        }



        display()

        setupRecyclerView()


        binding.imgClick.setOnClickListener {
            val intent = Intent(this@LearnActivity, EnteredLearenActivity::class.java)

            //get image name from Constans list
            val url = Constants.img[categoryPosition]
            intent.putExtra("selectedCategory", url)
            shouldPlay = true
            startActivity(intent)
            finish
        }

        Constants.initBackgroundColor(learn_FrameLayout, this@LearnActivity)
        learn_scroll.visibility = View.VISIBLE

    }



    private fun setupRecyclerView() {
        binding.rvTitle.adapter = mAdapter
        binding.rvTitle.setHasFixedSize(true)
        binding.rvTitle.layoutManager =
            LinearLayoutManager(this@LearnActivity, LinearLayoutManager.HORIZONTAL, false)
    }

    private fun display() {
        mAdapter.displayTitles()
        Log.d("LearnFragment", "clicked: $category")
        if (category == null) {
            category = LearnModel("animals", getString(R.string.animals), "")
            initToNextPage(this.category!!)
        }
        initToNextPage(this.category!!)


    }

    private fun initToNextPage(cat: LearnModel) {


        val uri =
            Uri.parse(RESOURCE + cat.img)
        binding.categoryImg.load(uri) {
//            crossfade(300)
        }
        binding.catTxtTitle.text = cat.title


    }

    override fun onItemClick(position: Int) {
        categoryPosition = position
        val category = mAdapter.getCategoryName(position)
        initToNextPage(category!!)
        val categoryName = category.img
        if (categoryName != null) playImgSound(categoryName)

        Log.d("LearnFragment", "clicked: $position")
    }

//    from work date 13/7/2021 *****************


//    private fun changeOrientation() {
//        Log.d("loadAd", "changeOrientation: called")
//
//        val display = (getSystemService(AppCompatActivity.WINDOW_SERVICE) as WindowManager).defaultDisplay
//        val orientation = display.orientation
//
//
//
//        if (orientation == Surface.ROTATION_90 || orientation == Surface.ROTATION_270) {
//            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
//
//        } else {
//            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
//
//        }
//    }

    private fun playImgSound(name: String) {
        var path: String?

        try {
            val newName = removeLastChar(name)

            path = "sound/" + newName + "ar.mp3"

            Log.d("soundmd", "play: " + path)
            val mediaPlayer = MediaPlayer()

            val descriptor = this@LearnActivity?.assets?.openFd(path)
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
            Log.d("soundmd", "play: " + e)
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




    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
//            setFullScreen()
            Toast.makeText(this@LearnActivity, "landscape", Toast.LENGTH_SHORT).show()
            category_img.visibility = View.GONE
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            category_img.visibility = View.VISIBLE
            Toast.makeText(this@LearnActivity, "portrait", Toast.LENGTH_SHORT).show()

        }
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
        val intent = Intent(this@LearnActivity, MediaService::class.java)
        if (this@LearnActivity != null) {
            this@LearnActivity?.startService(intent)
        }
    }


    private fun stopService() {
        val intent = Intent(this@LearnActivity, MediaService::class.java)
        if (this@LearnActivity != null) {
            this@LearnActivity?.stopService(intent)
        }
    }


//    override fun onDestroy() {
//        super.onDestroy()
//        _binding = null
//    }
}
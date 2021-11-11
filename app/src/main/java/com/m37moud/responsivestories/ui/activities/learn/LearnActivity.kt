package com.m37moud.responsivestories.ui.activities.learn

import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.m37moud.responsivestories.MainActivity
import com.m37moud.responsivestories.R
import com.m37moud.responsivestories.adapters.LearnAdapter
import com.m37moud.responsivestories.databinding.ActivityLearnBinding
import com.m37moud.responsivestories.models.LearnModel
import com.m37moud.responsivestories.util.Constants
import com.m37moud.responsivestories.util.Constants.Companion.RESOURCE
import com.m37moud.responsivestories.util.RemoteConfigUtils
import com.m37moud.responsivestories.util.media.AudioManager
import com.skydoves.elasticviews.ElasticAnimation
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_learn.*
import kotlinx.android.synthetic.main.fragment_third_screen.*
import javax.inject.Inject

@AndroidEntryPoint
class LearnActivity : AppCompatActivity(), LearnAdapter.ItemClickListener {

    private var _binding: ActivityLearnBinding? = null
    private val binding get() = _binding!!
    private val mAdapter: LearnAdapter by lazy { LearnAdapter(this@LearnActivity, this) }
    private var category: LearnModel? = null

    @Inject
    lateinit var audioManager: AudioManager

    private var shouldPlay = false
    private var shouldAllowBack = false

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

//        RemoteConfigUtils.init()

        if (!Constants.activateSetting)
            this.audioManager.getAudioService()?.playMusic()

//        shouldPlay = true
//        if (shouldPlay) {
//            startService()
//        }

        Constants.initBackgroundColor(parent_learn_frame, this@LearnActivity)



        binding.learnLoading.visibility = View.VISIBLE
        binding.learnContainerFrame.visibility = View.INVISIBLE


        Handler().postDelayed(
            {
                binding.learnLoading.visibility = View.GONE
                shouldAllowBack = true
                binding.learnContainerFrame.visibility = View.VISIBLE
            }, 2500
        )

        display()

        setupRecyclerView()


        binding.imgClick.setOnClickListener {
            Constants.clickSound(this)
            ElasticAnimation(it).setScaleX(0.85f).setScaleY(0.85f).setDuration(200)
                .setOnFinishListener {

                    val intent = Intent(this@LearnActivity, EnteredLearnActivity::class.java)

                    //get image name from Constans list
                    val url = Constants.img[categoryPosition]
                    intent.putExtra("selectedCategory", url)
                    shouldPlay = true
                    startActivity(intent)
                    finish
                }.doAction()

        }

        val backgroundColor = parent_learn_frame.background
        binding.learnFrameLayout.background = backgroundColor
        binding.learnScroll.visibility = View.VISIBLE

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

//
//    override fun onConfigurationChanged(newConfig: Configuration) {
//        super.onConfigurationChanged(newConfig)
//        // Checks the orientation of the screen
//        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
////            setFullScreen()
//            Toast.makeText(this@LearnActivity, "landscape", Toast.LENGTH_SHORT).show()
//            category_img.visibility = View.GONE
//        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
//            category_img.visibility = View.VISIBLE
//            Toast.makeText(this@LearnActivity, "portrait", Toast.LENGTH_SHORT).show()
//
//        }
//    }


    override fun onDestroy() {
//        stopService()
        super.onDestroy()
    }


    override fun onStop() {
        super.onStop()
        if (!this.shouldPlay) {
            this.audioManager.getAudioService()?.pauseMusic()

        }

    }

    override fun onStart() {
//        startService()
//        shouldPlay = false
        super.onStart()
    }

    override fun onResume() {
//        startService()
        if (!Constants.activateSetting)
            this.audioManager.getAudioService()?.resumeMusic()

        shouldPlay = false
        super.onResume()
    }

    override fun onBackPressed() {

        this.shouldPlay = true
//
//        if (!shouldPlay) {
//            stopService()
//        }
        if (shouldAllowBack) {
            Constants.fabCloseSound(this)

            startActivity(
                Intent(
                    this@LearnActivity,
                    MainActivity::class.java
                )
            )
            finish()

            super.onBackPressed()
        }


    }


}
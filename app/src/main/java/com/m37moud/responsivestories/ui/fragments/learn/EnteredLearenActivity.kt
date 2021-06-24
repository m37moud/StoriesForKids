package com.m37moud.responsivestories.ui.fragments.learn

import android.content.Intent
import android.graphics.Path
import android.graphics.drawable.Drawable
import android.media.MediaPlayer
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.m37moud.responsivestories.R
import com.m37moud.responsivestories.models.AnimalsModel
import com.m37moud.responsivestories.util.MediaService
import kotlinx.android.synthetic.main.activity_entered_learen.*
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.util.*
import kotlin.collections.ArrayList
import kotlin.properties.Delegates
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

const val GAME_LENGTH_MILLISECONDS = 3000L
const val AD_UNIT_ID = "ca-app-pub-3940256099942544/1033173712"


class EnteredLearenActivity : AppCompatActivity() {

    private var shouldPlay = false
    private var category: String? = null
    private var counter: Int = 0
    private lateinit var list: ArrayList<String>
    private lateinit var listModel: ArrayList<AnimalsModel>


    //ads

    private var mInterstitialAd: InterstitialAd? = null

    private var mAdIsLoading: Boolean = false
    private var TAG = "EnteredLearenActivity"


    private val requestOptions = RequestOptions()
        .placeholder(R.drawable.ic_error_placeholder)

    private var showEng by Delegates.notNull<Boolean>()
    private var clicked = false
    private var folder = ""

    //most of problem is fixed
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setFullScreen()
        setContentView(R.layout.activity_entered_learen)


//    startInterstitialAd()
        loadAd()
        img_sound.setOnClickListener {

            img_sound.isEnabled = false
//            val name = txt_name.text.toString()
            val name = initName(list[counter], true)
            img_sound.animate().apply {


//                for fade
//                alpha(.5f)
//                for zoom
//                scaleXBy(.5f)
//                scaleYBy(.5f)
//                rotationYBy(200f)
                // move up and down animate

                translationYBy(-50f)
            }.withEndAction {
                img_sound.animate().apply {
                    translationYBy(50f)

                }.withEndAction {
                    img_sound.animate().apply {
                        translationYBy(50f)
                    }
                }.withEndAction {
                    img_sound.animate().apply {
                        translationYBy(-50f)
                    }.withEndAction {
                        img_sound.animate().apply {
                            translationYBy(50f)
                        }.withEndAction {
                            img_sound.isEnabled = true
                        }
                    }
                }

                playImgSound("animals/".plus(name))


            }


        }

        txt_name.setOnClickListener {
            clicked = false
            if (!isFolder(category!!) && !clicked) {
                Log.d("txt_name", "showEng: " + clicked.toString())
                changeLang()
                showEng = !showEng
            }



            Log.d("txt_name", "showEng: " + showEng.toString())
            Log.d("if", "showEng: " + showEng.toString())

            val name = initName(list[counter], showEng)
//            val name = initName(list[counter])
//            val name = txt_name.text.toString()
            //set text
            txt_name.text = name
            //play sound
            val path = category.plus("Name") + folder + File.separator + name
            playImgSound(path)


        }



        getAssetsFolder()



        right_img_btn.setOnClickListener {

            if (!isFolder(category!!) && !clicked) {
                detectLanguage()
            }


//            showEng = false
//            txt_name.animation = AnimationUtils.loadAnimation(this , R.anim.zoom_out)
//            txt_name.startAnimation(AnimationUtils.loadAnimation(this , R.anim.zoom_out))
//           initViews(500)

            if (counter < list.size) {
                counter++
                left_img_btn.visibility = View.VISIBLE
                right_img_btn.visibility = View.VISIBLE

                txt_name.animate().apply {
                    initViews()
                    //zoom and fade out for txtview
//                    duration = 100
                    alphaBy(.5f)
                    //zoom out
                    scaleXBy(-1f)
                    scaleYBy(-1f)


                    img_sound.animate().apply {
                        //zoom and fade out for img
                        startDelay = 100
//                        duration = 100
                        alphaBy(.5f)
                        scaleXBy(-1f)
                        scaleYBy(-1f)

                    }.start()

                }.withEndAction {

                    txt_name.animate().apply {

                        startDelay = 100
//                        duration = 100
                        setImage(list)
                        //zoom in
                        scaleXBy(1f)
                        scaleYBy(1f)
                        alphaBy(-.5f)


                    }.withEndAction {
                        img_sound.animate().apply {
                            alphaBy(-.5f)
                            //zoom out
                            scaleXBy(1f)
                            scaleYBy(1f)

                        }.withEndAction {
                            endViews()
                        }
                    }
                }

                right_img_btn.visibility = View.VISIBLE
            }
            if (counter == list.size - 1) {

                right_img_btn.visibility = View.GONE
            }

        }

        left_img_btn.setOnClickListener {
//            showEng = false


//            if (folder == "EN") {
//                showEng = true
//            } else {
//                showEng = false
//            }
            if (!isFolder(category!!) && !clicked) {
                detectLanguage()
            }

            if (counter > 0) {
                counter--

                right_img_btn.visibility = View.VISIBLE

                txt_name.animate().apply {
                    initViews()
                    //zoom and fade out for txtview
//                    duration = 100
                    alphaBy(.5f)
                    //zoom out
                    scaleXBy(-1f)
                    scaleYBy(-1f)


                    img_sound.animate().apply {
                        //zoom and fade out for img
                        startDelay = 100
//                        duration = 100
                        alphaBy(.5f)
                        scaleXBy(-1f)
                        scaleYBy(-1f)

                    }.start()

                }.withEndAction {

                    txt_name.animate().apply {

                        startDelay = 100
//                        duration = 100
                        setImage(list)
                        //zoom in
                        scaleXBy(1f)
                        scaleYBy(1f)
                        alphaBy(-.5f)


                    }.withEndAction {
                        img_sound.animate().apply {
                            alphaBy(-.5f)
                            //zoom out
                            scaleXBy(1f)
                            scaleYBy(1f)

                        }.withEndAction {
                            endViews()
                        }
                    }
                }
                left_img_btn.visibility = View.VISIBLE
            }
            if (counter == 0) {
                left_img_btn.visibility = View.GONE
            }

        }


    }


    private fun getAssetsFolder() {
        initBtn()

        val intent = intent
        category = intent.getStringExtra("selectedCategory")
        Log.d("asset", "getAssetsFolder: " + category!!.toString())

        val images = assets.list(category!!)
        val imgList: ArrayList<String> = ArrayList(images!!.toList())
        Log.d("asset", "getAssetsFolder: " + imgList.toString())
        // show which folder to view content
        //check if it path of actegory containe folders or not
        if (isFolder(category!!)) {
            //set images to arabic button in  dialog
            initChooseDialog()
            Log.d(
                "asset",
                "getAssetsFolder: if statement is folder is = True " + category!!.toString()
            )
            folderContainer.visibility = View.VISIBLE
            containerCardContainer.visibility = View.GONE

            //init on click listener to arabic container
            arabic_container.setOnClickListener {
                val arabicFiles = assets.list(category!!)
                val imgList: ArrayList<String> = ArrayList(arabicFiles!!.toList())
                Log.d("getAssetsFolder", "arabic_container: " + imgList.toString())
                if (imgList.isNotEmpty()) {
                    imgList.forEach { folders ->
                        if (folders.toUpperCase(Locale.ROOT) == "AR") {
                            try {
                                Log.d(
                                    "getAssetsFolder", "folders : " +
                                            folders
                                )
                                val path = category!! + File.separator + folders
                                Log.d(
                                    "getAssetsFolder", "path: " +
                                            path
                                )
                                val files = assets.list(path)
                                val nFiles: ArrayList<String> = ArrayList(files!!.toList())
                                Log.d(
                                    "getAssetsFolder", "nFiles: " +
                                            nFiles
                                )
                                folder = folders
                                showEng = false
//                                initBtn()

                                setImage(nFiles)

                            } catch (e: IOException) {
                                Toast.makeText(
                                    this@EnteredLearenActivity,
                                    " $e",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                                Log.d("getAssetsFolder", "setImage: ${e.message}")
                            }
                        }
                    }

                }

            }
            //init on click listener to english container
            english_container.setOnClickListener {

                if (imgList.isNotEmpty()) {
                    imgList.forEach {
                        if (it == "EN") {
                            try {
                                val files = assets.list(category!! + File.separator + it)
                                val nFiles: ArrayList<String> = ArrayList(files!!.toList())
                                Log.d("getAssetsFolder", "nFiles: ${nFiles.toString()}")
                                folder = it
                                showEng = true
//                                initBtn()
                                setImage(nFiles)

                            } catch (e: IOException) {
                                Toast.makeText(
                                    this@EnteredLearenActivity,
                                    " $e",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                                Log.d("getAssetsFolder", "setImage: $e")
                            }
                        }
                    }


                }


            }


        } else {
            Log.d("asset", "getAssetsFolder:  if state false" + category!!.toString())
            detectLanguage()
            setImage(imgList)
        }

    }

    private fun initBtn() {
        right_img_btn.visibility = View.VISIBLE
        left_img_btn.visibility = View.GONE
    }


    private fun initImageTxt(name: String): String {

//        txt_name.startAnimation(AnimationUtils.loadAnimation(this , R.anim.zoom_in))

        val n = name.split(".")

        var nameOnly: String? = null

        nameOnly = n[0]

        return nameOnly

    }


    private fun initName(name: String, showEng: Boolean): String {
        Log.d("initName", "invoke: ")
//        txt_name.startAnimation(AnimationUtils.loadAnimation(this , R.anim.zoom_in))

        val n = name.split(".")

        var nameOnly: String? = null

        nameOnly = n[0]

        if (!isFolder(category!!)) {
            if (nameOnly.contains("-")) {
                val eng = nameOnly.split("-")
                val engName: String?
                return if (!showEng) {
                    //eng text
                    engName = eng[1]

                    engName
                } else {
                    //arabic text
                    engName = eng[0]
                    engName
                }
            }

        } else if (this.category == "numbers") {
            Log.d("initName", "category: ")
            val nameToNum = initNumberTo(nameOnly, shouldPlay)
            Log.d("initName", "category:  = numbers " + nameToNum.toString())
            return nameToNum!!
        }
        return nameOnly
    }

    private fun isFolder(path: String): Boolean {

        val images = assets.list(path)
        val images1 = assets.list("sound")

        val new = images!!.toList()
        val new2 = images1!!.toList()
        Log.d("ifFolder", "list is: " + new2.toString())
//
        if (!new[0].contains(".")) {
            clicked = true
            Log.d("ifFolder", "path cant open it is folder back true ")
            return true
        } else if (new[0].contains(".")) {
            Log.d("ifFolder", "path open it is file back false ")
            return false
        }
        Log.d("ifFolder", "path cant recognize not true or false ")
        //If list returns any entries, than the path is a directory

        return false
    }

    //new work from my jop 10/6
    private fun setImage(imgList: ArrayList<String>) {
        folderContainer.visibility = View.GONE
        containerCardContainer.visibility = View.VISIBLE

        Log.d("setImage", "setImage: " + imgList.toString())

        Log.d("setImage", "setImage:  localLang " + showEng + imgList.toString())
        list = imgList

        val name: String?

        name = "" + initImageTxt(list[counter])

        txt_name.text = initName(list[counter], showEng)
        try {
            val input: InputStream = if (isFolder(category!!) && !TextUtils.isEmpty(folder)) {

                assets.open(category!! + File.separator + folder + File.separator + name.plus(".png"))
            } else {
                assets.open(category!! + File.separator + name.plus(".png"))
            }


            val drawable = Drawable.createFromStream(input, null)

            Glide.with(this@EnteredLearenActivity)
                .applyDefaultRequestOptions(requestOptions)
                .asDrawable()
                .load(drawable)
                .into(img_sound)


            val imgName = if (this.category == "numbers") {
                initNumberTo(list[counter], true)
            } else {
                initName(list[counter], showEng)
            }
            Log.d("soundmd", "play: " + imgName)

            if (!TextUtils.isEmpty(imgName)) {
                val path =
                    category.plus("Name") + folder.toUpperCase(Locale.getDefault()) + File.separator + imgName
                Log.d("soundmd", "play: " + path)
                playImgSound(path)
            }

        } catch (e: IOException) {
            Toast.makeText(this@EnteredLearenActivity, " $e", Toast.LENGTH_SHORT).show()
            Log.d("TAG", "setImage: ${e.message}")
        }

    }


    private fun playImgSound(name: String) {
        Log.d("playImgSound", "name =  " + name)
        val path: String?
        try {
            Log.d("playImgSound", "play:" + category)
//
            path = "sound" + File.separator + "$name.mp3".trim()

            Log.d("playImgSound", "path: false " + path.trim())

            val mediaPlayer = MediaPlayer()
            val descriptor = assets.openFd(path)
//            val descriptor = assets.openFd("sound/alphabetsNameAR/أ.mp3")
            mediaPlayer.setDataSource(
                descriptor.fileDescriptor,
                descriptor.startOffset,
                descriptor.length
            )
            descriptor.close()
            mediaPlayer.prepare()
            mediaPlayer.setVolume(1f, 1f)
            mediaPlayer.isLooping = false
            mediaPlayer.start()
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("playImgSound", "play : catch " + e)
        }

    }

    private fun removeLastChar(str: String?): String? {
        var strName = str
        if (strName != null && strName.isNotEmpty() && (strName[strName.length - 1] == '1' || strName[strName.length - 1] == '2')) {
            strName = strName.substring(0, strName.length - 1)
        }
        return strName
    }

    private fun initNumberTo(str: String?, shouldPlay: Boolean): String? {
        Log.d("initNumberTo", "initNumberTo = " + str)
        var strName = ""
        if (str!!.isNotEmpty()) {
            if (shouldPlay) {
                //get the number digit from the name
                strName = str[0].toString()
                Log.d("initNumberTo", "initNumberTo : true " + strName)
            } else {
                //get the name without the number digit from the name
                val len = str.length

                strName = str.substring(1, str.length)
                Log.d("initNumberTo", "initNumberTo : false " + len.toString())
                Log.d("initNumberTo", "initNumberTo :  " + strName)
            }
        }

        return strName
    }


    private fun setFullScreen() {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
    }

    private fun initViews() {
//        Thread.sleep(millSec)
        right_img_btn.isEnabled = false
        left_img_btn.isEnabled = false


        right_img_btn.setColorFilter(ContextCompat.getColor(this, R.color.lightMediumGray))
        left_img_btn.setColorFilter(ContextCompat.getColor(this, R.color.lightMediumGray))
    }

    //detect mobile language to select what the name will start
    private fun detectLanguage() {
        val lang = Locale.getDefault().displayLanguage

        if (lang == "العربية") {
            folder = "AR"
            showEng = false
        } else {
            folder = "EN"
            showEng = true
        }
        Log.d("detectLanguage", "language: " + folder + lang.toString())
    }

    private fun changeLang() {

        if (folder == "AR")
            folder = "EN"
        else if (folder == "EN") {
            folder = "AR"
        }

    }

    private fun initChooseDialog() {

        try {
            val srcAr = assets.open("categoryImg" + File.separator + category.plus("AR.jpg"))
            val drawableAr = Drawable.createFromStream(srcAr, null)


            Glide.with(this@EnteredLearenActivity)
                .applyDefaultRequestOptions(requestOptions)
                .asDrawable()
                .load(drawableAr).into(arabic_img)

        } catch (e: IOException) {
            Toast.makeText(
                this@EnteredLearenActivity,
                " $e",
                Toast.LENGTH_SHORT
            )
                .show()
            Log.d("initChooseDialog", "arabic_img: $e")
        }
        //set images to english button in  dialog
        try {

            val srcEn = assets.open("categoryImg" + File.separator + category.plus("EN.jpg"))
            val drawableEn = Drawable.createFromStream(srcEn, null)


            Glide.with(this@EnteredLearenActivity)
                .applyDefaultRequestOptions(requestOptions)
                .asDrawable()
                .load(drawableEn).into(english_img)

        } catch (e: IOException) {
            Toast.makeText(
                this@EnteredLearenActivity,
                " $e",
                Toast.LENGTH_SHORT
            )
                .show()
            Log.d("initChooseDialog", "english_img: $e")
        }
    }

    private fun endViews() {
//        Thread.sleep(millSec)
        right_img_btn.isEnabled = true
        left_img_btn.isEnabled = true


        right_img_btn.setColorFilter(ContextCompat.getColor(this, R.color.blue))
        left_img_btn.setColorFilter(ContextCompat.getColor(this, R.color.blue))
    }


    private fun loadAd() {
        var adRequest = AdRequest.Builder().build()

        InterstitialAd.load(
            this, AD_UNIT_ID, adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.d(TAG, adError?.message)
                    mInterstitialAd = null
                    mAdIsLoading = false
                    val error = "domain: ${adError.domain}, code: ${adError.code}, " +
                            "message: ${adError.message}"
                    Toast.makeText(
                        this@EnteredLearenActivity,
                        "onAdFailedToLoad() with error $error",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    Log.d(TAG, "Ad was loaded.")
                    mInterstitialAd = interstitialAd
                    mAdIsLoading = false
                    mInterstitialAd?.fullScreenContentCallback =
                        object : FullScreenContentCallback() {
                            override fun onAdDismissedFullScreenContent() {
                                Log.d(TAG, "showInterstitial Ad was dismissed.")
                                // Don't forget to set the ad reference to null so you
                                // don't show the ad a second time.
                                mInterstitialAd = null
//                                loadAd()
                            }

                            override fun onAdFailedToShowFullScreenContent(adError: AdError?) {
                                Log.d(TAG, "showInterstitial Ad failed to show.")
                                // Don't forget to set the ad reference to null so you
                                // don't show the ad a second time.
                                mInterstitialAd = null
                            }

                            override fun onAdShowedFullScreenContent() {
                                Log.d(TAG, "showInterstitial Ad showed fullscreen content.")
                                // Called when ad is dismissed.
                                mInterstitialAd = null

                            }
                        }

                }
            }
        )
    }

    // Show the ad if it's ready. Otherwise toast and restart the game.
    private fun showInterstitial() {
//        loadAd()
        Log.d("showInterstitial", "showInterstitial Ad was dismissed." + mInterstitialAd)
        if (mInterstitialAd != null) {

            mInterstitialAd?.show(this)
        } else {
            Toast.makeText(this, "Ad wasn't loaded.", Toast.LENGTH_SHORT).show()

        }
    }

    private fun startInterstitialAd() {
        if (!mAdIsLoading && mInterstitialAd == null) {
            mAdIsLoading = true
            loadAd()
        }
    }


    override fun onResume() {
        super.onResume()
        startService()
    }


    override fun onBackPressed() {
        super.onBackPressed()

        shouldPlay = true
        startService()

    }

    override fun finish() {
        //show ads
//        showInterstitial()
        if (mInterstitialAd != null) {
            mInterstitialAd?.show(this)
            super.finish()
        } else {
            Toast.makeText(this, "Ad wasn't loaded.", Toast.LENGTH_SHORT).show()
            super.finish()
        }


    }

    override fun onStop() {
        super.onStop()
        if (!shouldPlay) {
            stopService()
        }
    }

    private fun startService() {
        val intent = Intent(this, MediaService::class.java)

        startService(intent)

    }

    private fun stopService() {
        val intent = Intent(this, MediaService::class.java)

        stopService(intent)
    }
}

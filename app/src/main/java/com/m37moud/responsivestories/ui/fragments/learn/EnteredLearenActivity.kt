package com.m37moud.responsivestories.ui.fragments.learn

import android.content.Intent
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
import com.m37moud.responsivestories.R
import com.m37moud.responsivestories.models.AnimalsModel
import com.m37moud.responsivestories.util.MediaService
import kotlinx.android.synthetic.main.activity_entered_learen.*
import java.io.File
import java.io.IOException


class EnteredLearenActivity : AppCompatActivity() {
    private var shouldPlay = false
    private var category: String? = null
    private var counter: Int = 0
    private lateinit var list: ArrayList<String>
    private lateinit var listModel: ArrayList<AnimalsModel>

    private var showEng = false
    private var clicked = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setFullScreen()
        setContentView(R.layout.activity_entered_learen)



        img_sound.setOnClickListener {

            img_sound.isEnabled = false
            val name = txt_name.text.toString()
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
            Log.d("txt_name", "showEng: " + showEng.toString())

            showEng = !showEng

            Log.d("if", "showEng: " + showEng.toString())

            val name = initName(list[counter], showEng)
//            val name = txt_name.text.toString()
            //set text
            txt_name.text = name
            //play sound in english
            playImgSound(name)


        }

        getAssetsFolder()

        right_img_btn.setOnClickListener {

            showEng = false
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
            showEng = false

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
//new work from work 25/5
        arabic_container.setOnClickListener {
            val images = assets.list(category!!)
            val imgList: ArrayList<String> = ArrayList(images!!.toList())
            if (imgList.isNotEmpty()) {
                imgList.forEach {
                    if (imgList.contains("AR")) {
                        try {
                            val name = initImageTxt(list[counter])
                            val input = assets.open(it + File.separator + name.plus(".png"))
                            val drawable = Drawable.createFromStream(input, null)
                            img_sound.setImageDrawable(drawable)

                            val imgName = initName(list[counter], false)
                            Log.d("arabic_container", "play: " + imgName)
                            if (!TextUtils.isEmpty(imgName)) {
                                playImgSound(imgName)
                            }

                        } catch (e: IOException) {
                            Toast.makeText(this@EnteredLearenActivity, " $e", Toast.LENGTH_SHORT)
                                .show()
                            Log.d("arabic_container", "setImage: $e")
                        }
                    }
                }


            }


        }
//new work from work 25/5
        english_container.setOnClickListener {
            val images = assets.list(category!!)
            val imgList: ArrayList<String> = ArrayList(images!!.toList())
            if (imgList.isNotEmpty()) {
                imgList.forEach {
                    if (imgList.contains("EN")) {
                        try {
                            val files = assets.list(category!!+ File.separator+"EN")
                            val nFiles: ArrayList<String> = ArrayList(files!!.toList())
                            val name = initName(nFiles[counter], true)
                            val input =
                                assets.open(category + "/" + it + File.separator + name.plus(".png"))
                            val drawable = Drawable.createFromStream(input, null)
                            img_sound.setImageDrawable(drawable)

                            val imgName = initName(nFiles[counter], false)
                            Log.d("soundmd", "play: " + imgName)
                            if (!TextUtils.isEmpty(imgName)) {
                                playImgSound(imgName)
                            }

                        } catch (e: IOException) {
                            Toast.makeText(this@EnteredLearenActivity, " $e", Toast.LENGTH_SHORT)
                                .show()
                            Log.d("TAG", "setImage: $e")
                        }
                    }
                }


            }


        }

    }

    //new work from work 25/5


    private fun getAssetsFolder() {
        val intent = intent
        category = intent.getStringExtra("selectedCategory")
        Log.d("asset", "getAssetsFolder: " + category!!.toString())

        val images = assets.list(category!!)
        val imgList: ArrayList<String> = ArrayList(images!!.toList())
        Log.d("asset", "getAssetsFolder: " + imgList!!.toString())
        // show which folder to view content
        if (isFolder(category!!)) {
            Log.d("asset", "getAssetsFolder: if statement True " + category!!.toString())
            folderContainer.visibility = View.VISIBLE
            containerCardContainer.visibility = View.GONE

            //new work from work 25/5
            arabic_container.setOnClickListener {
                val images = assets.list(category!!)
                val imgList: ArrayList<String> = ArrayList(images!!.toList())
                if (imgList.isNotEmpty()) {
                    imgList.forEach {
                        if (imgList.contains("AR")) {
                            try {
                                val files = assets.list(category!!+ File.separator+"AR")
                                val nFiles: ArrayList<String> = ArrayList(files!!.toList())
                                Log.d("arabic_container", "nFiles: ${nFiles.toString()}")
                                initBtn()
                                setImage(nFiles)

                            } catch (e: IOException) {
                                Toast.makeText(this@EnteredLearenActivity, " $e", Toast.LENGTH_SHORT)
                                    .show()
                                Log.d("arabic_container", "setImage: $e")
                            }
                        }
                    }


                }


            }
//new work from work 25/5
            english_container.setOnClickListener {

                if (imgList.isNotEmpty()) {
                    imgList.forEach {
                        if (imgList.contains("EN")) {
                            try {
                                val files = assets.list(category!!+ File.separator+"EN")
                                val nFiles: ArrayList<String> = ArrayList(files!!.toList())
                                Log.d("english_container", "nFiles: ${nFiles.toString()}")
                                initBtn()
                                setImage(nFiles)

//                                val name = initImageTxt(nFiles[counter])
//                                val input =
//                                    assets.open(category + "/" + it + File.separator + name.plus(".png"))
//                                val drawable = Drawable.createFromStream(input, null)
//                                img_sound.setImageDrawable(drawable)
//
//                                val imgName = initName(list[counter], false)
//                                Log.d("soundmd", "play: " + imgName)
//                                if (!TextUtils.isEmpty(imgName)) {
//                                    playImgSound(imgName)
//                                }

                            } catch (e: IOException) {
                                Toast.makeText(this@EnteredLearenActivity, " $e", Toast.LENGTH_SHORT)
                                    .show()
                                Log.d("TAG", "setImage: $e")
                            }
                        }
                    }


                }


            }


        } else {
//            containerCardContainer.visibility = View.VISIBLE
//            folderContainer.visibility = View.GONE
//            val pa = assets.openFd(category!!).extras
//        Log.d("asset", "getAssetsFolder: " + images!!.size.toString())


            Log.d("asset", "getAssetsFolder:  if state false" + category!!.toString())
            initBtn()
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
        }
        return nameOnly
    }

    private fun isFolder(path: String): Boolean {

        val images = assets.list(path)
        val new = images!!.toList()
        Log.d("ifFolder", "list is: " + new.toString())
//
        if (!new[0].contains(".")) {
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

    @Throws(IOException::class)
    fun isDirectory(path: String): Boolean {
        //If list returns any entries, than the path is a directory
        val files = assets.list(path)!!.toList()
        Log.d("ifFolder", "list is: " + files.toString())
        return if (files.isNotEmpty()) {
            Log.d("ifFolder", "list is: null")
            true
        } else {
            try {
                //If we can open a stream then the path leads to a file
                assets.open(path)
                Log.d("ifFolder", "path open it is file back false ")
                false
            } catch (ex: java.lang.Exception) {
                //.open() throws exception if it's a directory that you're passing as a parameter
                Log.d("ifFolder", "path cant open it is folder back true ")
                true
            }
        }
    }


    private fun setImage(imgList: ArrayList<String>) {

        containerCardContainer.visibility = View.VISIBLE
        folderContainer.visibility = View.GONE

        Log.d("TAG", "setImage: " + imgList.toString())

        list = imgList

        var name: String? = null

        name = if (this.category == "colors" || this.category == "الالوان") {
//                ""+ removeLastChar(initTxt(list[counter]))
//            Log.d("soundmd", "play: " + list.toString())
            "" + initImageTxt(list[counter])
        } else {
            "" + initImageTxt(list[counter])
        }

        txt_name.text = initName(list[counter], false)
        try {
            val input = assets.open(category!! + File.separator + name.plus(".png"))
            val drawable = Drawable.createFromStream(input, null)
            img_sound.setImageDrawable(drawable)

            val imgName = initName(list[counter], false)
            Log.d("soundmd", "play: " + imgName)
            if (!TextUtils.isEmpty(imgName)) {
                playImgSound(imgName)
            }

        } catch (e: IOException) {
            Toast.makeText(this@EnteredLearenActivity, " $e", Toast.LENGTH_SHORT).show()
            Log.d("TAG", "setImage: $e")
        }


    }


    private fun playImgSound(name: String) {


        var path: String? = null
        try {
            Log.d("playImgSound", "play:   " + category)
            if (category == "colors") {
                val newName = removeLastChar(name)
                path = "sound/$newName.mp3"
                Log.d("colors", "play: true  " + path)
            } else {
                path = "sound/$category" + "NameAr/$name.mp3"
                Log.d("playImgSound", "play: false " + path)
            }

            val mediaPlayer = MediaPlayer()
            val descriptor = assets.openFd(path)
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
        }

    }

    private fun removeLastChar(str: String?): String? {
        var str = str
        if (str != null && str.length > 0 && (str[str.length - 1] == '1' || str[str.length - 1] == '2')) {
            str = str.substring(0, str.length - 1)
        }
        return str
    }


    override fun onDestroy() {
//        playBackgroundSound(false)
        super.onDestroy()
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
//        left_img_btn.visibility = View.GONE
    }

    private fun endViews() {
//        Thread.sleep(millSec)
        right_img_btn.isEnabled = true
        left_img_btn.isEnabled = true


        right_img_btn.setColorFilter(ContextCompat.getColor(this, R.color.blue))
        left_img_btn.setColorFilter(ContextCompat.getColor(this, R.color.blue))
//        left_img_btn.visibility = View.GONE
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

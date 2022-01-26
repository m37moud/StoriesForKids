package com.m37moud.responsivestories.ui.activities.story

import android.app.NotificationManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.*
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.exoplayer2.offline.Download
import com.google.android.exoplayer2.offline.Download.*
import com.google.android.exoplayer2.offline.DownloadManager
import com.google.android.exoplayer2.offline.DownloadRequest
import com.google.android.exoplayer2.offline.DownloadService
import com.google.android.exoplayer2.upstream.DataSource
import com.m37moud.responsivestories.R
import com.m37moud.responsivestories.adapters.DownloadedVideoAdapter
import com.m37moud.responsivestories.adapters.VideoAdapter
import com.m37moud.responsivestories.data.database.entity.CategoriesEntity
import com.m37moud.responsivestories.data.database.entity.VideoEntity
import com.m37moud.responsivestories.databinding.ActivityStoryBinding
import com.m37moud.responsivestories.models.CategoriesModel
import com.m37moud.responsivestories.models.VideoModel
import com.m37moud.responsivestories.ui.activities.started.MainActivity
import com.m37moud.responsivestories.ui.activities.story.bottomsheet.CategoriesBottomSheet
import com.m37moud.responsivestories.util.*
import com.m37moud.responsivestories.util.media.AudioManager
import com.m37moud.responsivestories.viewmodel.MainViewModel
import com.m37moud.responsivestories.viewmodel.VideosViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_story.*
import kotlinx.android.synthetic.main.categories_bottom_sheet.*
import kotlinx.android.synthetic.main.categories_bottom_sheet.view.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList


@ExperimentalCoroutinesApi
@AndroidEntryPoint
class StoryActivity : AppCompatActivity(), DownloadTracker.Listener {
    // we need to use applicationContext
    private var _binding: ActivityStoryBinding? = null
    private val binding get() = _binding!!

    private val mainViewModel: MainViewModel by viewModels()
    private val videosViewModel: VideosViewModel by viewModels()

    //network
    private lateinit var networkListener: NetworkListener
    private lateinit var downloadTracker: DownloadTracker
    private lateinit var downloadManager: DownloadManager
    private lateinit var dataSourceFactory: DataSource.Factory

    private lateinit var listVid: ArrayList<VideoModel>
    private lateinit var listCategory: ArrayList<CategoriesModel>
    private lateinit var chickedListCategory: ArrayList<CategoriesModel>
    private lateinit var roomList: ArrayList<VideoModel>

    //    private lateinit var roomList: ArrayList<VideoEntity2>
    private lateinit var roomEntityList: ArrayList<VideoEntity>
    private lateinit var downloadedList: ArrayList<Download>
    private lateinit var listReadDatabase: ArrayList<VideoEntity>
    private lateinit var listCategoriesReadDatabase: ArrayList<CategoriesEntity>

    private var shouldPlay = false
    private var shouldAllowBack = false

    @Inject
    lateinit var audioManager: AudioManager


    //    private var savedRecipeId = 0

    private var savedRecipeId = ""
    private var counter = 0
    private var firstSavedVideos = 0

    private val mAdapter by lazy {
        VideoAdapter(
            this@StoryActivity
        )
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (!Constants.activateSetting)
            this.audioManager.getAudioService()?.playMusic()

//        mainViewModel = ViewModelProvider(this@StoryActivity).get(MainViewModel::class.java)
//        videosViewModel = ViewModelProvider(this@StoryActivity).get(VideosViewModel::class.java)

        listVid = ArrayList()
        roomList = ArrayList()
//        listCategory = ArrayList()
        listCategory = ArrayList()
        chickedListCategory = ArrayList()

//        videosViewModel.saveExitStatus(false)

        binding.lifecycleOwner = this

        Constants.initBackgroundColor(parent_story_frame, this@StoryActivity)



        binding.storyLoading.visibility = View.VISIBLE
        binding.cardView.visibility = View.INVISIBLE


        Handler(Looper.getMainLooper()).postDelayed(
            {
                shouldAllowBack = true
                binding.storyLoading.visibility = View.GONE
                binding.cardView.visibility = View.VISIBLE
            }, 2500
        )
        ///
        initRecyclerView()

        videosViewModel.readBackOnline.observe(this@StoryActivity, Observer {
            videosViewModel.backOnline = it
        })

        videosViewModel.readCategoriesType.asLiveData().observe(this@StoryActivity) { category ->
            val c = category.selectedCategoryType
            Log.d("readCategoriesType", "category: $c")
            if (TextUtils.isEmpty(c)) {
                Log.d("readCategoriesType", "if true: ")
                readDatabase()

            } else {
                Log.d("readCategoriesType", "if false: $category")
                readVideosWithCategories2()


            }

        }


//        lifecycleScope.launchWhenStarted {
//            networkListener = NetworkListener()
//            networkListener.checkNetworkAvailability(this@StoryActivity)
//                .collect { status ->
//                    Log.d("NetworkListener", status.toString())
//                    videosViewModel.networkStatus = status
//                    videosViewModel.showNetworkStatus()
//                    //read database
//                    videosViewModel.readBottomSheetExitStatus.observe(
//                        this@StoryActivity,
//                        Observer { exitStatus ->
//                            Log.d("bottomSheetExit", exitStatus.toString())
//                            if (exitStatus) {
//                                readVideosWithCategories2()
//
////                                readVideosWithCategories(videosViewModel.applyQuery())
//                                Log.d("readVideosCategories", "readVideosWithCategories called! applyQuery= ${videosViewModel.applyQuery()}")
//
//                            } else {
//                                readDatabase()
//                                Log.d("readVideosCategories", "readVideosWithCategories called! applyQuery= ${videosViewModel.applyQuery()}")
//
//                            }
//                        })
//                }
//        }
        ///

        initManagers()
        loadVideos()

        //check if is there new videos **
        videosViewModel.readShouldDownload.observe(this@StoryActivity, Observer {
            Log.d("mah firstCheck", " called! + it = $it ")
            if (!it) {

                firstCheck()
//                getCategoriesFromFirebase()

            }

        })

//        dataSourceFactory = buildDataSourceFactory()!!
        //.........................................................

        //from database
        readCategoriesFromDatabase()

        //.........................................................

        //bring categories from videos were downloaded

//        videosViewModel.readShouldLoad.observe(this@StoryActivity, Observer {
//            Log.d("mah firstCheck", " called! + it = $it ")
//            if (it) {
//                readCategoriesFromVideos()
//
//            }
//
//        })

        //.........................................................


//

        binding.selectCategoryFab.setOnClickListener {
            Constants.clickSound(this)
            videosViewModel.readShouldLoad.observe(this@StoryActivity, Observer {
                Log.d("mah readShouldLoad", " called! + it = $it ")
                if (it) {
                    if (listCategory.isNotEmpty()) {
                        Log.d("selectCategoryFab", "selectCategoryFab: $listCategory")
                        binding.selectCategoryFab.isClickable = true
                        val bottomSheetFragment = CategoriesBottomSheet()
                        bottomSheetFragment.show(supportFragmentManager, bottomSheetFragment.tag)
                        val bundle = Bundle()
                        bundle.putParcelableArrayList("myListCategory", listCategory)
                        bottomSheetFragment.arguments = bundle
                        videosViewModel.readShouldLoad.removeObservers(this@StoryActivity)

//                        val category = bottomSheetFragment.arguments?.getString("chipCategory")
//                        Log.d("selectCategoryFab", "selectCategoryFab: $category")

//                category?.let { it1 -> readVideosWithCategories(it1) }

//                bottomSheetFragment.apply_btn.setOnClickListener {
//                    val category = bottomSheetFragment.arguments?.getString("chipCategory")
//                    category?.let { it1 -> readVideosWithCategories(it1) }
//
//                     bottomSheetFragment.dismiss()
//                }
                    }
                } else {
                    Toast.makeText(
                        this@StoryActivity,
                        "try to fetch Categories",
                        Toast.LENGTH_SHORT
                    )
                        .show()
//            binding.selectCategoryFab.isClickable = false
                }

            })


        }
//        Constants.initBackgroundColor(story_FrameLayout, this@StoryActivity)
        val backgroundColor = parent_story_frame.background as ColorDrawable
        binding.storyFrameLayout.background = backgroundColor
        binding.storyScroll.visibility = View.VISIBLE

//        binding.selectCategoryFab.background = backgroundColor
//        binding.selectCategoryFab.backgroundTintList = ColorStateList.valueOf(backgroundColor.color)


    }


    override fun onStart() {
        super.onStart()
        //check if is there new videos **
        downloadTracker.addListener(this)


//        firstCheck()
    }

    private fun offline() {
        binding.cowLoading.visibility = View.GONE
        binding.rcStory.visibility = View.GONE
        binding.noNetConnectionImg.visibility = View.VISIBLE
        binding.noNetConnectionTxt.visibility = View.VISIBLE
        binding.noNetConnectionTxt.text = getString(R.string.NoInternet)
    }

    private fun showLoading() {
        binding.cowLoading.visibility = View.VISIBLE
        binding.noNetConnectionTxt.visibility = View.VISIBLE
        binding.noNetConnectionTxt.text = getString(R.string.loading)
    }

    private fun hideLoading() {

        binding.rcStory.visibility = View.VISIBLE
        binding.noNetConnectionImg.visibility = View.GONE
        binding.noNetConnectionTxt.visibility = View.GONE
        binding.cowLoading.visibility = View.GONE
    }

    private fun initRecyclerView() {

        val layout = LinearLayoutManager(this@StoryActivity, LinearLayoutManager.HORIZONTAL, false)
        binding.rcStory.layoutManager = layout
        binding.rcStory.setHasFixedSize(true)

//        binding.rcStory.layoutManager = GridLayoutManager(this@StoryActivity, 2)
//        binding.rcStory.setHasFixedSize(true)

    }


    private fun startService() {
        try {
            DownloadService.start(
                this@StoryActivity,
                DemoDownloadService::class.java
            )
        } catch (e: IllegalStateException) {
            DownloadService.startForeground(
                this@StoryActivity,
                DemoDownloadService::class.java
            )
        }

    }


    private fun startDownload(downloadList: ArrayList<VideoModel>) {
        //if video is download we get online categories list *****************
        getCategoriesFromFirebase()
        fabProgressCircle.show()
        videosViewModel.saveLoadingStatus(false)
        //*********************************************************
        Log.d("mah startDownload", " called! + list = $downloadList.toString()")
        startService()
//        downloadTracker!!.addListener(this)
        if (!downloadList.isNullOrEmpty()) {
            downloadList.forEach { model ->
                Log.d("EXO  DOWNLOADING ", "finish" + downloadList.toString())
                val myDownloadRequest = DownloadRequest(
                    model.id!!,
                    DownloadRequest.TYPE_PROGRESSIVE,
                    Uri.parse(model.videoUri),
                    /* streamKeys= */ Collections.emptyList(),
                    /* customCacheKey= */ null,
                    null
                )

                downloadManager.addDownload(myDownloadRequest)

            }
            //after download all videos in the list but download status = true
        } else {
            Log.d("EXO  DOWNLOADING ", "finishlist" + listVid.toString())
        }

    }

    private fun removeDownload(removeList: ArrayList<VideoModel>) {

        Log.d("mah removeDownload", " called! + list = " + removeList.toString())
        startService()
//        downloadTracker!!.addListener(this)
        if (!removeList.isNullOrEmpty()) {
            removeList.forEach { model ->
                Log.d("EXO removeDownload ", "finish" + removeList.toString())

                //  downloadManager.addDownload(myDownloadRequest)

                downloadManager.removeDownload(model.id!!)

            }
            //after download all videos in the list but download status = true
//            videosViewModel.saveDownloadStatus(true)
        } else {
            Log.d("EXO  removeDownload ", "finishlist" + listVid.toString())
        }

    }

    override fun onDownloadsChanged(download: Download) {
        when (download.state) {
            STATE_QUEUED -> {
            }
            STATE_STOPPED -> {
            }
            STATE_DOWNLOADING -> {


                Toast.makeText(this@StoryActivity, "Downloading started .", Toast.LENGTH_SHORT)
                    .show()
//                Log.d(
//                    "EXO DOWNLOADING ",
//                    " " + download.contentLength
//                )
//                Log.d("EXO  DOWNLOADING ", "" + download.percentDownloaded)
            }

            STATE_REMOVING -> {

                Toast.makeText(this@StoryActivity, "DownloadREMOVING .", Toast.LENGTH_SHORT)
                    .show()
                //download id
                val id = download.request.id
                val size = roomList.size
                if (counter == size) {
                    readDatabase()
                }
                repeat(roomList.size) {


                    val vidId = roomList[it].id.toString()
                    Log.d(
                        "mah DownloadREMOVING",
                        "DownloadREMOVING sucsess!download = " + id + "\n " + vidId
                    )
                    if (vidId == id) {
                        savedRecipeId = roomEntityList[it].id
                        deleteVideo(roomList[it].id.toString())

                    }

                }
            }
            STATE_RESTARTING -> {


            }
            //job work 4/8
            STATE_COMPLETED -> {
                Toast.makeText(this@StoryActivity, "Downloading finished .", Toast.LENGTH_SHORT)
                    .show()
//                Log.d("EXO  DOWNLOADING ", "finish" + download.toString())
                //download id
                val id = download.request.id
                repeat(listVid.size) {

                    val vidId = listVid[it].id



                    Logger.d(
                        "mah onDownloadsChanged",
                        "STATE_COMPLETED sucsess!download = " + id + "\n " + vidId
                    )
                    if (vidId!! == id) {
//                        savedRecipeId = vidId.toInt()
                        saveVideoData(listVid[it])


                        Logger.d(
                            "mah onDownloadsChanged",
                            "counter " + "\n " + counter
                        )
                        //refresh the list again
//                        if (counter == size) {
//                            readDatabase()
//                        }


                    }


                }


            }
            STATE_FAILED -> {
                Toast.makeText(this@StoryActivity, "video Download faild .", Toast.LENGTH_SHORT)
                    .show()
                Logger.d(
                    "mah onDownloadsChanged",
                    "STATE_FAILED faild  "
                )

            }
        }
    }

//    private fun buildDataSourceFactory(): DataSource.Factory? {
//        return AdaptiveExoplayer.getInstance(applicationContext).buildDataSourceFactory()
//    }

    private fun initManagers() {
        val application: AdaptiveExoplayer = AdaptiveExoplayer.getInstance(applicationContext)
        downloadTracker = application.downloadTracker!!
        downloadManager = application.downloadManager!!
    }


    private fun loadVideos() {
        downloadedList = ArrayList()

        AdaptiveExoplayer.getInstance(applicationContext)
            .downloadTracker.downloads.entries.forEach { (keyUri, download) ->
                Log.d("TAG", "loadVideos: " + download.toString())
                downloadedList.add(download)
            }


    }

    private fun readVideosWithCategories2() {
        Log.d("readVideosCategories", "readVideosWithCategories called!")

        hideLoading()

        mainViewModel.readVideosWithCategory(videosViewModel.applyQuery())
            .observe(this@StoryActivity, { database ->
                if (database.isNotEmpty()) {
                    Log.d(
                        "readVideosCategories",
                        "readVideosWithCategories called! applyQuery= ${videosViewModel.applyQuery()}"
                    )
                    database.let {
                        val adapterReadDatabase = DownloadedVideoAdapter(
                            this@StoryActivity
                        )
                        adapterReadDatabase.setData(it as ArrayList<VideoEntity>)
                        binding.rcStory.adapter = adapterReadDatabase
                    }
                } else {
                    readAllVideosWithCategories()
                }


            })


    }

    private fun readAllVideosWithCategories() {
        Log.d("readVideosCategories", "readVideosWithCategories called!")

        hideLoading()
        lifecycleScope.launch {

            mainViewModel.readVideos
                .observe(this@StoryActivity, { database ->

                    if (database.isNotEmpty()) {

                        Log.d("readVideosCategories", "if statement true")

                        listReadDatabase = database as ArrayList
                        //room change
                        val adapterReadDatabase = DownloadedVideoAdapter(
                            this@StoryActivity
                        )
                        adapterReadDatabase.setData(listReadDatabase)
                        binding.rcStory.adapter = adapterReadDatabase
                        Log.d(
                            "readVideosCategories",
                            "list is " + listReadDatabase.toString()
                        )

                    } else {
                        Log.d("readVideosCategories", "if statement is false ...")
//                    Log.d("mah readDatabase", "if statement is false ...listVid = " + listVid.toString())
//                        mainViewModel.readVideos.removeObservers(this@StoryActivity)
                    }
                })
        }


    }


    private fun readDatabase() {
        Logger.d("mah readDatabase", "readDatabase called!")
        hideLoading()
        lifecycleScope.launch {
            mainViewModel.readVideos.observe(this@StoryActivity, Observer { database ->
                if (database.isNotEmpty()) {

                    Logger.d("mah readDatabase", "if statement true")

                    listReadDatabase = database as ArrayList
                    //room change
                    val adapterReadDatabase = DownloadedVideoAdapter(
                        this@StoryActivity
                    )
                    adapterReadDatabase.setData(listReadDatabase)
                    binding.rcStory.adapter = adapterReadDatabase
                    Log.d("mah readDatabase", "list is " + listReadDatabase.toString())
                    mainViewModel.readVideos.removeObservers(this@StoryActivity)

                } else {
                    Logger.d("mah readDatabase", "if statement is false ...")
//                    Log.d("mah readDatabase", "if statement is false ...listVid = " + listVid.toString())
                    mainViewModel.readVideos.removeObservers(this@StoryActivity)
                    firstRequestApiData()
                }
            })
        }
    }


    //take online requested list and compare it with room list
    private fun onlineListToCheck(newData: ArrayList<VideoModel>) {

        Log.d("mah onlineListToCheck", " called!")
        //compare method to define the deferance between two lists then download it

        //read database to get details
        //if the online list is bigger than offline list
        when {
            /**newData online list*/
            newData.size >
                    /**room (offline)) list*/
                    roomList.size -> {
                Log.d("mah onlineListToCheck", " if statement true!  will add the new video")

                // to do will make compare method to define the deference between two lists then download it
                Log.d(
                    "mah onlineListToCheck",
                    "  online list : " + newData.toString() + newData.size
                )
                Log.d(
                    "mah onlineListToCheck",
                    "  offline list : " + roomList.toString() + roomList.size
                )

                //second list should be the bigger
                val difference = backStrangeItem(roomList, newData)

                val listV = difference as ArrayList<VideoModel>

                //last check to download the new list
                shouldDownloadNewList(listV)


            }

            //check if videos deleted from online database
            /**newData online list*/
            newData.size <
                    /**room (offline)) list*/
                    /**room (offline)) list*/
                    roomList.size -> {

                // to do will make compare method to define the dafrence between two lists then download it
                Log.d(
                    "mah onlineListToCheck",
                    "  will delete the video "
                )
                Log.d(
                    "mah onlineListToCheck",
                    "  online list : " + newData.toString() + newData.size
                )
                Log.d(
                    "mah onlineListToCheck",
                    "  offline list : " + roomList.toString() + roomList.size
                )
                //second list should be the bigger
                val difference = backStrangeItem(newData, roomList)

                Log.d(
                    "mah onlineListToCheck",
                    " difference " + difference.toString() + difference.size
                )

                val listV = difference as ArrayList<VideoModel>

                Log.d(
                    "mah onlineListToCheck",
                    "  list after delete :- " + listV[0].title.toString() + " :  " + listV.toString() + "  : " + listV.size
                )
                //last check to download the new list
                shouldRemoveDownloaded(listV)

            }
            else -> {
                return
            }
        }

    }

    private fun shouldDownloadNewList(list: ArrayList<VideoModel>) {

        if (!list.isNullOrEmpty()) {

            Log.d("mah Videos", "shouldDownloadNewList sucsess! start download")
            startDownload(listVid)

        }
    }

    private fun shouldRemoveDownloaded(list: ArrayList<VideoModel>) {

        if (!list.isNullOrEmpty()) {

            Log.d("mah Videos", "shouldRemoveDownloaded sucsess! start download")
            removeDownload(list)

        }
    }


    private fun requestApiData() {
        listVid = ArrayList()
        listVid.clear()
        Log.d("requestApiData", "method calle is ")

        mainViewModel.getVideos()
        mainViewModel.videosResponse.observe(this@StoryActivity, Observer { response ->
            when (response) {
                is NetworkResult.Success -> {
                    Log.d("mah requestApiData", "requestApiData sucsess!")

                    response.data?.let {
                        listVid = it

                        if (!listVid.isNullOrEmpty()) { // check online list
                            Log.d("mah requestApiData", "online list is " + listVid)
//                            check for updates
                            updateCheck(listVid)

                            //get offline list
                            getDatabaseList()
                            if (!roomList.isNullOrEmpty()) {// check offline list
                                Log.d(
                                    "mah requestApiData",
                                    "offline list is " + roomList.toString()
                                )
//                                onlineListToCheck(listVid)
                            }

                        }
                        Log.d(
                            "mah requestApiData",
                            "requestApiData sucsess! + list is " + listVid.toString()
                        )
                    }
                }
                is NetworkResult.Error -> {
                    Log.d(
                        "Videos",
                        "mah requestApiData error! \n" + response.toString()
                    )
                    Toast.makeText(
                        this@StoryActivity,
                        response.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                is NetworkResult.Loading -> {
                    Log.d("Videos", "requestApiData Loading!")
                }
            }
        })
    }


    private fun updateCheck(listVid: ArrayList<VideoModel>) {
        Log.d("mah updateCheck", "method called")
        listVid.forEach {

            if (it.update) {
                Log.d("mah updateCheck", "method called" + it.update)
                updateVideo(it)
            }
        }

    }

    private fun getDatabaseList() {
        Log.d("mah getDatabaseList", "method called")
        roomList = ArrayList()
        roomList.clear()

        lifecycleScope.launch {
            mainViewModel.readVideos.observeOnce(this@StoryActivity, Observer { database ->
                if (database.isNotEmpty()) {

                    val list = database as ArrayList
                    roomEntityList = database

                    list.forEach {
//                        val ls = it.videos
                        //room change

//                        val ls = it as VideoModel

                        val lsToModel = VideoModel(
                            it.id,
                            it.title,
                            it.timestamp,
                            it.videoUri,
                            it.videoSlide,
                            it.videoThumb,
                            it.videoCategory,
                            it.videoDescription,
                            it.videoType,
                            it.update
                        )
                        roomList.add(lsToModel)
                    }

                    onlineListToCheck(listVid)


                } else {
                    //no video is found
                    startDownload(listVid)


                }
            })
        }
    }


    private fun firstRequestApiData() {

        Logger.d("firstRequestApiData", "requestApiData called!")
        mainViewModel.getVideos()

        mainViewModel.videosResponse.observe(this@StoryActivity, Observer { response ->
            when (response) {
                is NetworkResult.Success -> {
                    Logger.d("firstRequestApiData", "requestApiData sucsess!")
                    hideLoading()
                    binding.rcStory.adapter = mAdapter
                    response.data?.let {
                        mAdapter.setData(it)
                        mainViewModel.videosResponse.removeObservers(this@StoryActivity)
                    }
                }

                is NetworkResult.Error -> {
                    Logger.d(
                        "firstRequestApiData",
                        "mah firstRequestApiData error! \n" + response.toString()
                    )
                    offline()

                    Toast.makeText(
                        this@StoryActivity,
                        response.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                is NetworkResult.Loading -> {
                    showLoading()
                    Logger.d("firstRequestApiData", "requestApiData Loading!")
                }
            }
        })
    }


    private fun saveVideoData(model: VideoModel) {
        Log.d("saveVideoData", "videoData!" + model)
        val id = model.id!!

        val videoData: VideoEntity = VideoEntity(
            id,
            model.title,
            model.timestamp,
            model.videoUri,
            model.videoSlide,
            model.videoThumb,
            model.videoCategory,
            model.videoDescription,
            model.videoType,
            model.update
        )
        mainViewModel.insertVideos(videoData).invokeOnCompletion {
            counter++
            val size = listVid.size
            Log.d("saveVideoData", "isert all videos !" + size)
            Log.d("saveVideoData", " numbre iserted videos !" + counter)

//            firstSavedVideos++
//            Logger.d("downloaded firstSavedVideos = $firstSavedVideos")
//            //finish all download videos
//            listVid.let {
//                if (firstSavedVideos == listVid.size) {
//
//
//                }
//            }

            val vidCategory = model.videoCategory

            chickedListCategory.let {
                it.forEach { category ->
                    if (category.categoryName == vidCategory) saveCategoriesData(category)
                    return@forEach
                }
            }

            //refresh the list again
            if (counter == size-1) {
                readDatabase()
                videosViewModel.saveLoadingStatus(true)
                fabProgressCircle.beginFinalAnimation()
            }
            createNotification(model)
        }

    }

    private fun createNotification(model: VideoModel) {
        Log.d("createNotification", "notify called")
        val notificationManager =
            this@StoryActivity.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = AppUtil.createExoDownloadNotificationChannel(this@StoryActivity)

        val notificationCompleted =
            NotificationCompat.Builder(this@StoryActivity, channelId)
                .setColor(ContextCompat.getColor(this@StoryActivity, R.color.colorAccent))
                .setContentTitle(model.title)
                .setContentText("new story is added")
                .setAutoCancel(false)
                .setWhen(System.currentTimeMillis())
                .setOnlyAlertOnce(true)
                .setSmallIcon(R.mipmap.ic_launcher)
                .build()
        notificationManager.notify(1003, notificationCompleted)

    }

    //    private fun deleteVideo(model: VideoModel) {
//
//        val videoData = VideoEntity(savedRecipeId, model)
//        Log.d("deleteVideo", "videoData!" + videoData.toString())
//        mainViewModel.deleteVideo(videoData)
//
//    }
    private fun deleteVideo(id: String) {

        mainViewModel.deleteVideo(id).invokeOnCompletion {
            counter++
        }

    }

    // update video model in room database then back the boolean value in firebase to false then the same in room database
    private fun updateVideo(model: VideoModel) {

        val id = model.id!!
        val videoData = VideoEntity(
            id,
            model.title,
            model.timestamp,
            model.videoUri,
            model.videoSlide,
            model.videoThumb,
            model.videoCategory,
            model.videoDescription,
            model.videoType,
            model.update
        )

        Log.d("updateVideo", "entity!" + videoData.toString())
        Log.d("updateVideo", "model!" + model.title.toString())

        //update all fields in room database
        mainViewModel.updateVideo(videoData).invokeOnCompletion {

            //when update is complete and properties (videoUpdate) back to false in fire base should update either in database
            mainViewModel.updateVideoComplete(model)
            readDatabase()

        }

    }

    private fun firstCheck() {
        videosViewModel.readShouldDownload.observe(this@StoryActivity, Observer {
            Logger.d("mah firstCheck", "method calle is " + it.toString())
            //check for daily check
            if (!it) {
                Toast.makeText(
                    this@StoryActivity,
                    "check for new videos",
                    Toast.LENGTH_SHORT
                ).show()
                //get online list
                requestApiData()
            } else {
                Logger.d("mah firstCheck", "if state  is false ")
            }

        })

        videosViewModel.saveDownloadStatus(true)
    }

    // function to calc the difrence between two list
    private fun backStrangeItem(
        listOne: ArrayList<VideoModel>,
        listTwo: ArrayList<VideoModel>
    ): List<VideoModel> {

        return listTwo.filterNot { lis ->
            listOne.any {
                lis.id == it.id
            }
        }
    }

    private fun getCategoriesFromFirebase() {

        Logger.d("getCategoriesFirebase", "getCategories called!")
        mainViewModel.getCategories()

        mainViewModel.categoriesResponse.observe(this@StoryActivity, Observer { response ->
            when (response) {
                is NetworkResult.Success -> {
                    Logger.d("getCategories", "getCategories sucsess!")
                    hideLoading()
                    response.data?.let {
                        chickedListCategory = it
//                        prepareSaveCategoriesData(it)
                    }
                }

                is NetworkResult.Error -> {
                    Logger.d(
                        "getCategories",
                        "mah getCategories error! \n" + response.toString()
                    )

                    Toast.makeText(
                        this@StoryActivity,
                        response.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                is NetworkResult.Loading -> {

                    Logger.d("getCategories", "getCategories Loading!")
                }
            }
        })
    }

    private fun prepareSaveCategoriesData(list: ArrayList<CategoriesModel>) {

        Logger.d("prepareSaveCategories", "CategoriesModel! called")

        if (list.isNotEmpty()) {
            if (list.size != listCategory.size) {
                Logger.d("prepareSaveCategories", "if ! true")

                mainViewModel.deleteCategories()
            }

            var countSaver = 0
            list.forEach {
                saveCategoriesData(it)
                countSaver++
            }
            // read offline categories again
            Logger.d("prepareSaveCategories", "countSaver = $countSaver")

            if (countSaver == list.size) readCategoriesFromVideos()
        }

    }

    private fun saveCategoriesData(model: CategoriesModel) {
        Log.d("saveCategoriesData", "videoData!" + model)
        val id = model.categoryId!!

        val categoryData: CategoriesEntity = CategoriesEntity(
            id,
            model.categoryName,
            model.categoryImage
        )
        mainViewModel.insertCategories(categoryData).invokeOnCompletion {
            val size = listCategory.size
            Log.d("saveVideoData", "isert all videos !" + size)

        }

    }

    //bring categories from videos were downloaded
    private fun readCategoriesFromVideos() {

        Logger.d("readCategoriesVideos", " called!")
//        lifecycleScope.launch {
//        if (roomList.size > 0)
        mainViewModel.readCategoriesFromVideos.observe(
            this@StoryActivity,
            Observer { database ->
                if (database.isNotEmpty()) {

                    Logger.d("readCategoriesVideos", "if statement true")

//                    listCategory = database as ArrayList<CategoriesModel>
                    listCategoriesReadDatabase = database as java.util.ArrayList
                    listCategoriesReadDatabase.forEach {
                        val categoryModel =
                            CategoriesModel(it.categoryId, it.categoryName, it.categoryImage)
                        listCategory.add(categoryModel)

                    }


                    Logger.d("readCategoriesVideos", "list is " + listCategory)

                    mainViewModel.readCategoriesFromVideos.removeObservers(this@StoryActivity)

                } else {
                    mainViewModel.readCategoriesFromVideos.removeObservers(this@StoryActivity)
//                        getCategoriesFromFirebase()

                    Logger.d("readCategoriesVideos", "if statement is false ...")
//                    Log.d("mah readDatabase", "if statement is false ...listVid = " + listVid.toString())
                }
            })
//        }
    }

    //read all inserted category
    private fun readCategoriesFromDatabase() {
        Logger.d("readCategoriesDatabase", "readCategoriesFromDatabase called!")
        lifecycleScope.launch {
            mainViewModel.readCategories.observe(this@StoryActivity, Observer { database ->
                if (database.isNotEmpty()) {

                    Log.d("readCategoriesDatabase", "if statement true")

//                    listCategory = database as ArrayList<CategoriesModel>
                    listCategoriesReadDatabase = database as ArrayList
                    listCategoriesReadDatabase.forEach {
                        val categoryModel =
                            CategoriesModel(it.categoryId, it.categoryName, it.categoryImage)
                        listCategory.add(categoryModel)

                    }


//                    Log.d("readCategoriesDatabase", "list is " + listCategory)

                    mainViewModel.readCategories.removeObservers(this@StoryActivity)

                } else {
                    Log.d("readCategoriesDatabase", "if statement is false ...")
//                    Log.d("mah readDatabase", "if statement is false ...listVid = " + listVid.toString())
                    mainViewModel.readCategories.removeObservers(this@StoryActivity)
                }
            })
        }
    }


    override fun onBackPressed() {
        shouldPlay = true
        if (shouldAllowBack) {
            Constants.fabCloseSound(this)

            startActivity(
                Intent(
                    this@StoryActivity,
                    MainActivity::class.java
                )
            )
            finish()
            super.onBackPressed()

        }

    }

    override fun onStop() {
        Log.d("StoryActivity", "onStop: ")


        if (!shouldPlay) {
            this.audioManager.getAudioService()?.pauseMusic()

        } else {
            videosViewModel.saveDownloadStatus(false)

            videosViewModel.saveCategoryType(
                "",
                0

            )
            videosViewModel.saveExitStatus(false)
        }

        super.onStop()
    }

    override fun onResume() {
        shouldPlay = false

        if (!Constants.activateSetting)
            this.audioManager.getAudioService()?.resumeMusic()

        super.onResume()
    }

    override fun onDestroy() {
        Log.d("StoryActivity", "onDestroy: ")
        //prepare for check downloads to story


        downloadTracker.removeListener(this)

        //whe app end download status = false
        _binding = null
        super.onDestroy()

    }


}
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

const val TAG = "StoryActivity"

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class StoryActivity : AppCompatActivity(), DownloadTracker.Listener {
    // we need to use applicationContext
    private var _binding: ActivityStoryBinding? = null
    private val binding get() = _binding!!

    private val mainViewModel: MainViewModel by viewModels()
    private val videosViewModel: VideosViewModel by viewModels()

    //network

    //    private lateinit var networkListener: NetworkListener
    private lateinit var downloadTracker: DownloadTracker
    private lateinit var downloadManager: DownloadManager
//    private lateinit var dataSourceFactory: DataSource.Factory

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

        videosViewModel.readCategoriesType.asLiveData().observe(this) { category ->
            val c = category.selectedCategoryType
            Logger.d("readCategoriesType", "category: $c")
            if (TextUtils.isEmpty(c)) {
                Logger.d("readCategoriesType", "if true: ")
                readDatabase()

            } else {
                Logger.d("readCategoriesType", "if false: $category")
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
            Logger.d(TAG, "readShouldDownload called! + it = $it ")
            if (!it) {

                firstCheck()
//                getCategoriesFromFirebase()

            }

        })

//        dataSourceFactory = buildDataSourceFactory()!!
        //.........................................................

        //readCategories from database
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
            Logger.d("mah btn", " clicked! ")

            Constants.clickSound(this)
            videosViewModel.readShouldLoad.observe(this@StoryActivity, Observer {
                Logger.d("mah btn", " called! + it = $it ")

                if (it) {
                    Logger.d("mah btn", " check from category list ")

                    if (listCategory.isNotEmpty()) {
                        Logger.d("mah btn", " check from category list result is true")

                        Logger.d("selectCategoryFab", "selectCategoryFab: $listCategory")
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
                    } else {
                        Logger.d("mah btn", " check from category list result is false")

                    }
                } else {
                    videosViewModel.readShouldLoad.removeObservers(this@StoryActivity)
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
        Logger.d("mah startDownload", " called! + list = ${downloadList.toString()}")
        startService()
//        downloadTracker!!.addListener(this)
        if (!downloadList.isNullOrEmpty()) {
            downloadList.forEach { model ->
                Logger.d("EXO  DOWNLOADING ", "finish" + downloadList.toString())
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
            Logger.d("EXO  DOWNLOADING ", "finishlist" + listVid.toString())
        }

    }

    private fun removeDownload(removeList: ArrayList<VideoModel>) {

        Logger.d("mah removeDownload", " called! + list = " + removeList.toString())
        startService()
//        downloadTracker!!.addListener(this)
        if (!removeList.isNullOrEmpty()) {
            removeList.forEach { model ->
                Logger.d("EXO removeDownload ", "finish" + removeList.toString())

                //  downloadManager.addDownload(myDownloadRequest)

                downloadManager.removeDownload(model.id!!)

            }
            //after download all videos in the list but download status = true
//            videosViewModel.saveDownloadStatus(true)
        } else {
            Logger.d("EXO  removeDownload ", "finishlist" + listVid.toString())
        }

    }

    override fun onDownloadsChanged(download: Download) {
        when (download.state) {
            STATE_QUEUED -> {
            }
            STATE_STOPPED -> {
            }
            STATE_DOWNLOADING -> {

                val precent = download.percentDownloaded

                Logger.d("Downloading ", "in progress : $precent")

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
                    Logger.d("Download remove", "check is true : ")

                    loadDBOnce()
                }
                repeat(roomList.size) {


                    val vidId = roomList[it].id.toString()
                    Logger.d(
                        "DownloadREMOVING",
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
                Logger.d("Downloading ", "finish list ${download.request.id.toString()}")

                Toast.makeText(this@StoryActivity, "Downloading finished .", Toast.LENGTH_SHORT)
                    .show()
//                Log.d("EXO  DOWNLOADING ", "finish" + download.toString())
                //download id
                val id = download.request.id
                run chickVidIfDownload@{
                    repeat(listVid.size) {

                        val vidId = listVid[it].id



                        Logger.d(
                            "Downloading",
                            "video ( ${listVid[it].title} ) STATE_COMPLETED succsess!download = $it \n $id  \n  $vidId "
                        )
                        Logger.d(
                            "Downloading",
                            "wil check if this ( ${listVid[it].title} ) is already download to save its data in database "
                        )
                        if (vidId!! == id) {
                            Logger.d(
                                "Downloading",
                                "wil check  ${listVid[it].title} result is true"
                            )
//                        savedRecipeId = vidId.toInt()
                            saveVideoData(listVid[it])
                            return@chickVidIfDownload

                        } else {
                            Logger.d(
                                "Downloading",
                                "wil check  ${listVid[it].title} result is false"
                            )
                        }


                    }

                }


            }
            STATE_FAILED -> {
                Toast.makeText(this@StoryActivity, "video Download faild .", Toast.LENGTH_SHORT)
                    .show()
                Logger.d(
                    "Downloading",
                    "STATE_FAILED faild  "
                )

            }
        }
    }

//    private fun buildDataSourceFactory(): DataSource.Factory? {
//        return AdaptiveExoplayer.getInstance(applicationContext).buildDataSourceFactory()
//    }

    private fun initManagers() {
        Logger.d(TAG, "initManagers called!")

        val application: AdaptiveExoplayer = AdaptiveExoplayer.getInstance(applicationContext)
        downloadTracker = application.downloadTracker!!
        downloadManager = application.downloadManager!!
    }


    private fun loadVideos() {
        Logger.d(TAG, "loadVideos: called")

        downloadedList = ArrayList()

        AdaptiveExoplayer.getInstance(applicationContext)
            .downloadTracker.downloads.entries.forEach { (keyUri, download) ->
                Logger.d(TAG, "loadVideos: ${download.toString()}")
                downloadedList.add(download)
            }


    }

    private fun readVideosWithCategories2() {
        Logger.d("readVideosCategories2", "readVideosWithCategories called!")

        hideLoading()

        mainViewModel.readVideosWithCategory(videosViewModel.applyQuery())
            .observe(this@StoryActivity, { database ->
                if (database.isNotEmpty()) {
                    Logger.d(
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
        Logger.d("readVideosCategories", "readVideosWithCategories called!")

        hideLoading()
        lifecycleScope.launch {

            mainViewModel.readVideos
                .observe(this@StoryActivity, { database ->

                    if (database.isNotEmpty()) {

                        Logger.d("readVideosCategories", "if statement true")

                        listReadDatabase = database as ArrayList
                        //room change
                        val adapterReadDatabase = DownloadedVideoAdapter(
                            this@StoryActivity
                        )
                        adapterReadDatabase.setData(listReadDatabase)
                        binding.rcStory.adapter = adapterReadDatabase
                        Logger.d(
                            "readVideosCategories",
                            "list is " + listReadDatabase.toString()
                        )

                    } else {
                        Logger.d("readVideosCategories", "if statement is false ...")
//                    Log.d("mah readDatabase", "if statement is false ...listVid = " + listVid.toString())
//                        mainViewModel.readVideos.removeObservers(this@StoryActivity)
                    }
                })
        }


    }


    private fun readDatabase() {
        Logger.d("readDatabase", "readDatabase called!")
        hideLoading()
        lifecycleScope.launch {
            mainViewModel.readVideos.observeOnce(this@StoryActivity, Observer { database ->
                if (database.isNotEmpty()) {

                    Logger.d("mah readDatabase", "if statement true")

                    listReadDatabase = database as ArrayList
                    //room change
                    val adapterReadDatabase = DownloadedVideoAdapter(
                        this@StoryActivity
                    )
                    adapterReadDatabase.setData(listReadDatabase)
                    binding.rcStory.adapter = adapterReadDatabase
                    Logger.d("mah readDatabase", "list is " + listReadDatabase.toString())
//                    mainViewModel.readVideos.removeObservers(this@StoryActivity)

                } else {
                    Logger.d("mah readDatabase", "if statement is false ...")
//                    Log.d("mah readDatabase", "if statement is false ...listVid = " + listVid.toString())
//                    mainViewModel.readVideos.removeObservers(this@StoryActivity)
                    firstRequestApiData()
                }
            })
        } // life scope end
    }

    private fun loadDBOnce() {
        Logger.d("loadDBOnce", "loadDBOnce called!")
        hideLoading()
//        lifecycleScope.launch {// strt life cycle scope
            mainViewModel.readVideos.observe(this@StoryActivity, Observer { database ->
                if (database.isNotEmpty()) {

                    Logger.d("loadDBOnce", "if statement true")

                    (database as ArrayList).let {
                        val adapterReadDatabase = DownloadedVideoAdapter(
                            this@StoryActivity
                        )
                        adapterReadDatabase.setData(it)
                        binding.rcStory.adapter = adapterReadDatabase
                        it.joinToString { vid ->
                            Logger.d("loadDBOnce", "list is ${vid.title}").toString()
                        }
                    }

                } else {
                    Logger.d("loadDBOnce", "if statement is false ...")
//
                }
            })
//        } // life scope end
    }


    //take online requested list and compare it with room list
    private fun onlineListToCheck(newData: ArrayList<VideoModel>) {

        Logger.d("mah onlineListToCheck", " called!")
        //compare method to define the deferance between two lists then download it

        //read database to get details
        //if the online list is bigger than offline list
        when {
            /**newData online list*/
            newData.size >
                    /**room (offline)) list*/
                    roomList.size -> {
                Logger.d("mah onlineListToCheck", " if statement true!  will add the new video")

                // to do will make compare method to define the deference between two lists then download it
                Logger.d(
                    "mah onlineListToCheck",
                    "  online list : " + newData.toString() + newData.size
                )
                Logger.d(
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
                Logger.d(
                    "mah onlineListToCheck",
                    "  will delete the video "
                )
                Logger.d(
                    "mah onlineListToCheck",
                    "  online list : " + newData.toString() + newData.size
                )
                Logger.d(
                    "mah onlineListToCheck",
                    "  offline list : " + roomList.toString() + roomList.size
                )
                //second list should be the bigger
                val difference = backStrangeItem(newData, roomList)

                Logger.d(
                    "mah onlineListToCheck",
                    " difference " + difference.toString() + difference.size
                )

                val listV = difference as ArrayList<VideoModel>

                Logger.d(
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

            Logger.d("mah Videos", "shouldDownloadNewList sucsess! start download")
            startDownload(listVid)

        }
    }

    private fun shouldRemoveDownloaded(list: ArrayList<VideoModel>) {

        if (!list.isNullOrEmpty()) {

            Logger.d("mah Videos", "shouldRemoveDownloaded sucsess! start download")
            removeDownload(list)

        }
    }


    private fun requestApiData() {
        listVid = ArrayList()
        listVid.clear()
        Logger.d("requestApiData", "method called is ")

        mainViewModel.getVideos()
        mainViewModel.videosResponse.observe(this@StoryActivity, Observer { response ->
            when (response) {
                is NetworkResult.Success -> {
                    Logger.d("mah requestApiData", "requestApiData sucsess!")

                    response.data?.let {
                        listVid = it

                        if (!listVid.isNullOrEmpty()) { // check online list
                            Logger.d("mah requestApiData", "online list is " + listVid)
//                            check for updates
                            updateCheck(listVid)

                            //get offline list
                            getDatabaseList()
                            if (!roomList.isNullOrEmpty()) {// check offline list
                                Logger.d(
                                    "mah requestApiData",
                                    "offline list is " + roomList.toString()
                                )
//                                onlineListToCheck(listVid)
                            }

                        }
                        Logger.d(
                            "mah requestApiData",
                            "requestApiData sucsess! + list is " + listVid.toString()
                        )
                    }
                }
                is NetworkResult.Error -> {
                    Logger.d(
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
                    Logger.d("Videos", "requestApiData Loading!")
                }
            }
        })
    }


    private fun updateCheck(listVid: ArrayList<VideoModel>) {
        Logger.d("mah updateCheck", "method called")
        listVid.forEach {

            if (it.update) {
                Logger.d("mah updateCheck", "method called" + it.update)
                updateVideo(it)
            }
        }

    }

    private fun getDatabaseList() {
        Logger.d("mah getDatabaseList", "method called")
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
        Logger.d("saveVideoData", "prepare to save video data to ( ${model.title} )")
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
            Logger.d("saveVideoData", "video name is  ( ${model.title} ) is inserted !")

            val vidCategory = model.videoCategory
            Logger.d(
                "saveVideoData",
                "start to check if video category is one in the chickedListCategory $vidCategory will check now!"
            )

            run chickedListCat@{
                chickedListCategory.let {
                    Logger.d("saveVideoData", "chickedListCategory size is ${it.size}")

                    it.forEach { category: CategoriesModel ->
                        Logger.d("saveVideoData", "chickedListCategory is ${category.categoryName}")

                        if (category.categoryName == vidCategory) {
                            Logger.d(
                                "saveVideoData",
                                "chickedListCategory result for ${category.categoryName} is one of main categories = true"
                            )

                            saveCategoriesData(category)
                            return@chickedListCat
                        }
                    }
                }
            }


        }

        createNotification(model)


    }

    private fun createNotification(model: VideoModel) {
        Logger.d("createNotification", "notify called")
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

        Logger.d("updateVideo", "entity!" + videoData.toString())
        Logger.d("updateVideo", "model!" + model.title.toString())

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

//    private fun prepareSaveCategoriesData(list: ArrayList<CategoriesModel>) {
//
//        Logger.d("prepareSaveCategories", "CategoriesModel! called")
//
//        if (list.isNotEmpty()) {
//            if (list.size != listCategory.size) {
//                Logger.d("prepareSaveCategories", "if ! true")
//
//                mainViewModel.deleteCategories()
//            }
//
//            var countSaver = 0
//            list.forEach {
//                saveCategoriesData(it)
//                countSaver++
//            }
//            // read offline categories again
//            Logger.d("prepareSaveCategories", "countSaver = $countSaver")
//
//            if (countSaver == list.size) readCategoriesFromVideos()
//        }
//
//    }

    private fun saveCategoriesData(model: CategoriesModel) {
        Logger.d("saveCategoriesData", "category! = ${model.categoryName} prepare to save")
        val id = model.categoryId!!

        val categoryData: CategoriesEntity = CategoriesEntity(
            id,
            model.categoryName,
            model.categoryImage
        )
        mainViewModel.insertCategories(categoryData).invokeOnCompletion {
//            val size = listCategory.size


            Logger.d("saveCategoriesData", "category ( ${model.categoryName} ) is inserted !")
            counter++
            Logger.d("saveCategoriesData", "counter is increased by one ")

            val size = listVid.size
            Logger.d("saveCategoriesData", " number of videos should download is = $size !")
            Logger.d("saveCategoriesData", " number of  videos ! counter =  $counter")

            Logger.d("saveCategoriesData", "check if all video is completed")

            //refresh the list again
            if (counter == size) {
                Logger.d("saveCategoriesData", "check if all video is completed result is true")

                loadDBOnce()
                readCategoriesDBOnceAgain()
                videosViewModel.saveLoadingStatus(true)
                fabProgressCircle.beginFinalAnimation()
            } else {
                Logger.d("saveCategoriesData", "check if all video is completed result is false")

            }



        }

    }

    //bring categories from videos were downloaded
//    private fun readCategoriesFromVideos() {
//
//        Logger.d("readCategoriesVideos", " called!")
////        lifecycleScope.launch {
////        if (roomList.size > 0)
//        mainViewModel.readCategoriesFromVideos.observe(
//            this@StoryActivity,
//            Observer { database ->
//                if (database.isNotEmpty()) {
//
//                    Logger.d("readCategoriesVideos", "if statement true")
//
////                    listCategory = database as ArrayList<CategoriesModel>
//                    listCategoriesReadDatabase = database as java.util.ArrayList
//                    listCategoriesReadDatabase.forEach {
//                        val categoryModel =
//                            CategoriesModel(it.categoryId, it.categoryName, it.categoryImage)
//                        listCategory.add(categoryModel)
//
//                    }
//
//
//                    Logger.d("readCategoriesVideos", "list is " + listCategory)
//
//                    mainViewModel.readCategoriesFromVideos.removeObservers(this@StoryActivity)
//
//                } else {
//                    mainViewModel.readCategoriesFromVideos.removeObservers(this@StoryActivity)
////                        getCategoriesFromFirebase()
//
//                    Logger.d("readCategoriesVideos", "if statement is false ...")
////                    Log.d("mah readDatabase", "if statement is false ...listVid = " + listVid.toString())
//                }
//            })
////        }
//    }

    //read all inserted category
    private fun readCategoriesFromDatabase() {
        Logger.d("readCategoriesDatabase", "readCategoriesFromDatabase called!")
        lifecycleScope.launch {
            mainViewModel.readCategories.observeOnce(this@StoryActivity, Observer { database ->
                if (database.isNotEmpty()) {

                    Logger.d("readCategoriesDatabase", "if statement true")

//                    listCategory = database as ArrayList<CategoriesModel>
                    listCategoriesReadDatabase = database as ArrayList
                    listCategoriesReadDatabase.forEach {
                        val categoryModel =
                            CategoriesModel(it.categoryId, it.categoryName, it.categoryImage)
                        listCategory.add(categoryModel)

                    }


//                    Log.d("readCategoriesDatabase", "list is " + listCategory)

//                    mainViewModel.readCategories.removeObservers(this@StoryActivity)

                } else {
                    Logger.d("readCategoriesDatabase", "if statement is false ...")
//                    Log.d("mah readDatabase", "if statement is false ...listVid = " + listVid.toString())

//                    mainViewModel.readCategories.removeObservers(this@StoryActivity)
                }
            })
        }
    }

    //read all inserted category
    private fun readCategoriesDBOnceAgain() {
        Logger.d("readCategoriesDBOnceAgain", "readCategoriesDBOnceAgain called!")
//        lifecycleScope.launch {// life cycle scope
            mainViewModel.readCategories.observe(this@StoryActivity, Observer { database ->
                if (database.isNotEmpty()) {

                    Logger.d("readCategoriesDBOnceAgain", "if statement true")

//                    listCategory = database as ArrayList<CategoriesModel>
                    (database as ArrayList).let {
                        it.forEach {
                            val categoryModel =
                                CategoriesModel(it.categoryId, it.categoryName, it.categoryImage)
                            listCategory.add(categoryModel)

                        }
                    }


                }
            })
//        }//end of scope
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
        Logger.d("StoryActivity", "onStop: ")


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
        Logger.d("StoryActivity", "onDestroy: ")
        //prepare for check downloads to story


        downloadTracker.removeListener(this)

        //whe app end download status = false
        _binding = null
        super.onDestroy()

    }


}
package com.m37moud.responsivestories.ui.fragments.story

import android.app.NotificationManager
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.exoplayer2.offline.Download
import com.google.android.exoplayer2.offline.Download.*
import com.google.android.exoplayer2.offline.DownloadManager
import com.google.android.exoplayer2.offline.DownloadRequest
import com.google.android.exoplayer2.offline.DownloadService
import com.google.android.exoplayer2.upstream.DataSource
import com.m37moud.responsivestories.R
import com.m37moud.responsivestories.adapters.DownloadedVideoAdapter
import com.m37moud.responsivestories.adapters.VideoAdapter
import com.m37moud.responsivestories.data.database.entity.VideoEntity2
import com.m37moud.responsivestories.databinding.FragmentStoryBinding
import com.m37moud.responsivestories.models.VideoModel
import com.m37moud.responsivestories.util.*
import com.m37moud.responsivestories.viewmodel.MainViewModel
import com.m37moud.responsivestories.viewmodel.VideosViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import java.util.*
import kotlin.collections.ArrayList


@ExperimentalCoroutinesApi
@AndroidEntryPoint
class StoryFragment : Fragment(), DownloadTracker.Listener {
    // we need to use applicationContext
    private var _binding: FragmentStoryBinding? = null
    private val binding get() = _binding!!

    private lateinit var mainViewModel: MainViewModel
    private lateinit var videosViewModel: VideosViewModel

    //network
    private lateinit var networkListener: NetworkListener
    private lateinit var downloadTracker: DownloadTracker
    private lateinit var downloadManager: DownloadManager
    private lateinit var dataSourceFactory: DataSource.Factory

    private lateinit var listVid: ArrayList<VideoModel>
    private lateinit var roomList: ArrayList<VideoModel>

    //    private lateinit var roomList: ArrayList<VideoEntity2>
    private lateinit var roomEntityList: ArrayList<VideoEntity2>
    private lateinit var downloadedList: ArrayList<Download>
    private lateinit var listReadDatabase: ArrayList<VideoEntity2>


    //    private var savedRecipeId = 0
    private var savedRecipeId = ""

    private val mAdapter by lazy {
        VideoAdapter(
            requireContext()
        )
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        videosViewModel = ViewModelProvider(requireActivity()).get(VideosViewModel::class.java)
        listVid = ArrayList()
        roomList = ArrayList()

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentStoryBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        ///
        initRecyclerView()
        videosViewModel.readBackOnline.observe(viewLifecycleOwner, Observer {
            videosViewModel.backOnline = it
        })


        lifecycleScope.launchWhenStarted {
            networkListener = NetworkListener()
            networkListener.checkNetworkAvailability(requireContext())
                .collect { status ->
                    Log.d("NetworkListener", status.toString())
                    videosViewModel.networkStatus = status
                    videosViewModel.showNetworkStatus()
                    //read database
                    readDatabase()
                }
        }
        ///

        initManagers()
        loadVideos()

        //check if is there new videos **
        videosViewModel.readShouldDownload.observe(viewLifecycleOwner, Observer {
            Log.d("mah firstCheck", " called! + it = $it ")
            if (!it) {
                firstCheck()

            }

        })

        dataSourceFactory = buildDataSourceFactory()!!


        binding.addVideoFab.setOnClickListener {
//            startActivity(Intent(requireContext(), AddVideoActivity::class.java))
        }

        return binding.root
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

        binding.rcStory.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rcStory.setHasFixedSize(true)

    }


    private fun startService() {
        try {
            DownloadService.start(
                requireContext(),
                DemoDownloadService::class.java
            )
        } catch (e: IllegalStateException) {
            DownloadService.startForeground(
                requireContext(),
                DemoDownloadService::class.java
            )
        }

    }


    private fun startDownload(downloadList: ArrayList<VideoModel>) {

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
                Toast.makeText(requireContext(), "Downloading started .", Toast.LENGTH_SHORT).show()
//                Log.d(
//                    "EXO DOWNLOADING ",
//                    " " + download.contentLength
//                )
//                Log.d("EXO  DOWNLOADING ", "" + download.percentDownloaded)
            }

            STATE_REMOVING -> {

                Toast.makeText(requireContext(), "DownloadREMOVING .", Toast.LENGTH_SHORT)
                    .show()
                //download id
                val id = download.request.id
                val size = roomList.size - 1
                repeat(roomList.size) {


                    val vidId = roomList[it].id.toString()
                    Log.d(
                        "mah DownloadREMOVING",
                        "DownloadREMOVING sucsess!download = " + id + "\n " + vidId
                    )
                    if (vidId!! == id)
                        savedRecipeId = roomEntityList[it].id
                    deleteVideo(roomList[it].id.toString())

                    Log.d(
                        "mah onDownloadsChanged",
                        "counter " + "\n " + it
                    )

                    if (it == size) readDatabase()
                }
            }
            STATE_RESTARTING -> {

            }
            //job work 4/8
            STATE_COMPLETED -> {
                Toast.makeText(requireContext(), "Downloading finished .", Toast.LENGTH_SHORT)
                    .show()
//                Log.d("EXO  DOWNLOADING ", "finish" + download.toString())
                //download id
                val id = download.request.id
                val size = listVid.size - 1
                repeat(listVid.size) {


                    val vidId = listVid[it].id
                    Log.d(
                        "mah onDownloadsChanged",
                        "STATE_COMPLETED sucsess!download = " + id + "\n " + vidId
                    )
                    if (vidId!! == id) {
//                        savedRecipeId = vidId.toInt()
                        saveVideoData(listVid[it])
                        Log.d(
                            "mah onDownloadsChanged",
                            "counter " + "\n " + it
                        )

                        if (it == size) readDatabase()
                    }


                }
                //refresh the list again


            }
            STATE_FAILED -> {

            }
        }
    }

    private fun buildDataSourceFactory(): DataSource.Factory? {
        return AdaptiveExoplayer.getInstance(context).buildDataSourceFactory()
    }

    private fun initManagers() {
        val application: AdaptiveExoplayer = AdaptiveExoplayer.getInstance(context)
        downloadTracker = application.downloadTracker!!
        downloadManager = application.downloadManager!!
    }


    private fun loadVideos() {
        downloadedList = ArrayList()

        AdaptiveExoplayer.getInstance(context)
            .downloadTracker.downloads.entries.forEach { (keyUri, download) ->
                Log.d("TAG", "loadVideos: " + download.toString())
                downloadedList.add(download)
            }


    }

    private fun readDatabase() {
        Log.d("mah readDatabase", "readDatabase called!")
        hideLoading()
        lifecycleScope.launch {
            mainViewModel.readVideos.observeOnce(viewLifecycleOwner, Observer { database ->
                if (database.isNotEmpty()) {

                    Log.d("mah readDatabase", "if statement true")

                    listReadDatabase = database as ArrayList
//room change
                    val adapterReadDatabase = DownloadedVideoAdapter(
                        requireActivity()
                    )
                    adapterReadDatabase.setData(listReadDatabase)
                    binding.rcStory.adapter = adapterReadDatabase
                    Log.d("mah readDatabase", "list is " + listReadDatabase.toString())

                } else {
                    Log.d("mah readDatabase", "if statement is false ...")
//                    Log.d("mah readDatabase", "if statement is false ...listVid = " + listVid.toString())

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
        Log.d("mah firstCheck requestApiData", "method calle is ")

        mainViewModel.loadVideosFromFirebase()
        mainViewModel.videosResponse.observe(viewLifecycleOwner, Observer { response ->
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
                        requireContext(),
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

//    private fun updateCheck(listVid: ArrayList<VideoModel>) =
//        CoroutineScope(Dispatchers.IO).launch {
//            listVid.forEach {
//                if (it.videoUpdate!!) {
//
//                    updateVideo(it)
//                }
//            }
//
////        withContext(Dispatchers.Main){
////
////        }
//
//
//        }


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
            mainViewModel.readVideos.observeOnce(viewLifecycleOwner, Observer { database ->
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
                    startDownload(listVid)
                }
            })
        }
    }


    private fun firstRequestApiData() {

        Log.d("mah firstRequestApiData", "requestApiData called!")
        mainViewModel.loadVideosFromFirebase()

        mainViewModel.videosResponse.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is NetworkResult.Success -> {
                    Log.d("mah firstRequestApiData", "requestApiData sucsess!")
                    hideLoading()
                    binding.rcStory.adapter = mAdapter
                    response.data?.let {
                        mAdapter.setData(it)
                        mainViewModel.videosResponse.removeObservers(viewLifecycleOwner)
                    }
                }

                is NetworkResult.Error -> {
                    Log.d(
                        "firstRequestApiData",
                        "mah firstRequestApiData error! \n" + response.toString()
                    )
                    offline()
                    Toast.makeText(
                        requireContext(),
                        response.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                is NetworkResult.Loading -> {
                    showLoading()
                    Log.d("firstRequestApiData", "requestApiData Loading!")
                }
            }
        })
    }


    private fun saveVideoData(model: VideoModel) {
        Log.d("saveVideoData", "videoData!" + model)
        val id = model.id!!

        val videoData: VideoEntity2 = VideoEntity2(
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
        Log.d("saveVideoData", "videoData!" + videoData.toString())
        mainViewModel.insertVideos(videoData)
        createNotification(model)
//        readDatabase()

    }

    private fun createNotification(model: VideoModel) {
        Log.d("createNotification", "notify called")
        val notificationManager =
            requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = AppUtil.createExoDownloadNotificationChannel(requireContext())

        val notificationCompleted =
            NotificationCompat.Builder(requireContext(), channelId)
                .setColor(ContextCompat.getColor(requireContext(), R.color.colorAccent))
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

//        val videoData = VideoEntity2(savedRecipeId)
        mainViewModel.deleteVideo(id)

    }

    //from work 1/8
    // update video model in room database then back the boolean value in firebase to false then the same in room database
    private fun updateVideo(model: VideoModel) {

//        val videoData = VideoEntity(savedRecipeId, model)
        val id = model.id!!

        val videoData = VideoEntity2(
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
        val result = mainViewModel.updateVideo(videoData)
        Log.d("updateVideo", "update ? " + result.toString())

        if (result.isCompleted) {
            //when update is complete and propertiey (videoUpdate) back to false in fire base should update either in database

            mainViewModel.updateVideoComplete(model)
            //when update is complete and propertiey (videoUpdate) back to false in fire base should update either in database
//room change
//            mainViewModel.updateVideoRoomComplete(savedRecipeId.toInt(), false)

        } else {
            Log.d("updateVideo", "videoData! failed")
            Toast.makeText(requireContext(), "update failed", Toast.LENGTH_SHORT).show()
        }


    }


    private fun firstCheck() {
        videosViewModel.readShouldDownload.observe(viewLifecycleOwner, Observer {
            Log.d("mah firstCheck", "method calle is " + it.toString())
            //check for daily check
            if (!it) {
                Toast.makeText(
                    requireContext(),
                    "check for new videos",
                    Toast.LENGTH_SHORT
                ).show()
                //get online list
                requestApiData()
            } else {
                Log.d("mah firstCheck", "if state  is false ")
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

//    private fun backStrangeItemFromOnlineList(
//        listOne: ArrayList<VideoEntity2>,
//        listTwo: ArrayList<VideoModel>
//    ): List<VideoModel> {
//
//        return listTwo.filterNot { lis ->
//            listOne.any {
//                lis.id == it.id
//            }
//        }
//    }

//    private fun backStrangeItemFromOfflineList(
//        listOne: ArrayList<VideoModel>,
//        listTwo: ArrayList<VideoEntity2>
//    ): List<VideoModel> {
//
//        return listTwo.filterNot { lis ->
//            listOne.any {
//                lis.id == it.id
//            }
//        }
//    }


    override fun onDestroyView() {
        super.onDestroyView()

        downloadTracker.removeListener(this)
        //whe app end download status = false
        _binding = null
    }


}
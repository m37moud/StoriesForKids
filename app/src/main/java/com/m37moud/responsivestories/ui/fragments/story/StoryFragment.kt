package com.m37moud.responsivestories.ui.fragments.story

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.exoplayer2.offline.*
import com.google.android.exoplayer2.offline.Download.*
import com.google.android.exoplayer2.upstream.DataSource
import com.m37moud.responsivestories.adapters.DownloadedVideoAdapter
import com.m37moud.responsivestories.adapters.VideoAdapter
import com.m37moud.responsivestories.data.database.entity.VideoEntity
import com.m37moud.responsivestories.databinding.FragmentStoryBinding
import com.m37moud.responsivestories.models.VideoModel
import com.m37moud.responsivestories.util.*
import androidx.lifecycle.Observer
import com.m37moud.responsivestories.AddVideoActivity
import com.m37moud.responsivestories.R
import com.m37moud.responsivestories.viewmodel.MainViewModel
import com.m37moud.responsivestories.viewmodel.VideosViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList


@ExperimentalCoroutinesApi
@AndroidEntryPoint
class StoryFragment : Fragment(), DownloadTracker.Listener {

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
    private lateinit var roomEntityList: ArrayList<VideoEntity>
    private lateinit var downloadedList: ArrayList<Download>

    private var savedRecipeId = 0

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
//                    firstRequestApiData()
//                    requestApiData()
//
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

//        lifecycleScope.launchWhenStarted {
//
//        }
        //   ** //


        dataSourceFactory = buildDataSourceFactory()!!


        binding.addVideoFab.setOnClickListener {
            startActivity(Intent(requireContext(), AddVideoActivity::class.java))
//            startDownload()
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

    private fun showLoading() {
        binding.cowLoading.visibility = View.VISIBLE
        binding.noNetConnectionTxt.visibility = View.VISIBLE
        binding.noNetConnectionTxt.text = getString(R.string.loading)
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


//        Log.d("mah startDownload", " called! + list = $downloadList.toString()" )
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

                repeat(roomList.size) {

                    val vidId = roomList[it].id
                    Log.d(
                        "mah DownloadREMOVING",
                        "DownloadREMOVING sucsess!download = " + id + "\n " + vidId
                    )
                    if (vidId!! == id)
                        savedRecipeId = roomEntityList[it].id
                        deleteVideo(roomList[it])
                }
            }
            STATE_RESTARTING -> {

            }
            STATE_COMPLETED -> {
                Toast.makeText(requireContext(), "Downloading finished .", Toast.LENGTH_SHORT)
                    .show()
//                Log.d("EXO  DOWNLOADING ", "finish" + download.toString())
                //download id
                val id = download.request.id

                repeat(listVid.size) {

                    val vidId = listVid[it].id
                    Log.d(
                        "mah onDownloadsChanged",
                        "STATE_COMPLETED sucsess!download = " + id + "\n " + vidId
                    )
                    if (vidId!! == id) {
//                        savedRecipeId = vidId.toInt()
                        saveVideoData(listVid[it])
                    }
                }

            }
            STATE_FAILED -> {

            }
        }
    }

    private fun buildDataSourceFactory(): DataSource.Factory? {
        return AdaptiveExoplayer.getInstance(requireContext()).buildDataSourceFactory()
    }

    private fun initManagers() {
        val application: AdaptiveExoplayer = AdaptiveExoplayer.getInstance(requireContext())
        downloadTracker = application.downloadTracker!!
        downloadManager = application.downloadManager!!
    }


    private fun loadVideos() {
        downloadedList = ArrayList()

        AdaptiveExoplayer.getInstance(requireContext())
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

                    val list = database as ArrayList

                    val adapter = DownloadedVideoAdapter(
                        requireActivity()
                    )
                    adapter.setData(list)
                    binding.rcStory.adapter = adapter
                    Log.d("mah readDatabase", "list is " + list.toString())

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
                    /**room (offline)) list*/
                    roomList.size -> {
                Log.d("mah onlineListToCheck", " if statement true!  will add the new video")

                // to do will make compare method to define the dafrence between two lists then download it
                Log.d(
                    "mah onlineListToCheck",
                    "  online list : " + newData.toString() + newData.size
                )
                Log.d(
                    "mah onlineListToCheck",
                    "  offline list : " + roomList.toString() + roomList.size
                )

                //second list should be the bigger
                val difference  = backStrangeItem(roomList , newData)

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
                val difference  = backStrangeItem(newData , roomList)

                Log.d(
                    "mah onlineListToCheck",
                    " difference " + difference.toString() + difference.size
                )

                val listV = difference as ArrayList<VideoModel>

                Log.d("mah onlineListToCheck", "  list after delete :- " + listV[0].title.toString() + " :  "+listV.toString() +"  : " + listV.size)
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

        mainViewModel.loadVideosFromFirebase()
        mainViewModel.videosResponse.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is NetworkResult.Success -> {
                    Log.d("mah requestApiData", "requestApiData sucsess!")

                    response.data?.let {
                        listVid = it
                        if (!listVid.isNullOrEmpty()) { // check online list
                            Log.d("mah requestApiData", "online list is " + listVid.toString())
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
                        val ls = it.videos
                        roomList.add(ls)
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

        val videoData = VideoEntity(savedRecipeId, model)
        Log.d("saveVideoData", "videoData!" + videoData.toString())
        mainViewModel.insertVideos(videoData)

    }

    private fun deleteVideo(model: VideoModel) {

        val videoData = VideoEntity(savedRecipeId, model)
        Log.d("deleteVideo", "videoData!" + videoData.toString())
        mainViewModel.deleteVideo(videoData)

    }

    private fun firstCheck() {
        videosViewModel.readShouldDownload.observe(viewLifecycleOwner, Observer {
            Log.d("mah firstCheck", "method calle is ")
            //check for daily check
            if (!it) {
                Toast.makeText(
                    requireContext(),
                    "check for new videos",
                    Toast.LENGTH_SHORT
                ).show()
                //get online list
                requestApiData()
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


    override fun onDestroy() {
        super.onDestroy()
        downloadTracker.removeListener(this)
        //whe app end download status = false
        _binding = null
    }


}
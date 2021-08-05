package com.m37moud.responsivestories.viewmodel

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import android.widget.Toast
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.google.firebase.database.*
import com.m37moud.responsivestories.data.Repository
import com.m37moud.responsivestories.data.database.entity.VideoEntity2
import com.m37moud.responsivestories.models.VideoModel
import com.m37moud.responsivestories.util.NetworkResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

class MainViewModel @ViewModelInject constructor(
    private val repository: Repository,
    application: Application
) : AndroidViewModel(application) {

    //local database
    val readVideos: LiveData<List<VideoEntity2>> =
        repository.local.readVideos().asLiveData()


    fun insertVideos(videoEntity: VideoEntity2) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.local.insertVideos(videoEntity)
        }


//    fun deleteVideo(videoEntity: VideoEntity2) =
//        viewModelScope.launch(Dispatchers.IO) {
//            repository.local.deleteVideo(videoEntity)
//        }
    fun deleteVideo(id: String) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.local.deleteVideo(id)
        }

    //update all fields in room database
    fun updateVideo(videoEntity: VideoEntity2) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.local.updateVideo(videoEntity)
        }

    //when update is complete and property (videoUpdate) back to false in fire base should update either in database
    //room change
    fun updateVideoRoomComplete(tid: String, video: Boolean) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.local.updateVideoComplete(tid, video)
        }

    // firebase response
    var videosResponse: MutableLiveData<NetworkResult<ArrayList<VideoModel>>> = MutableLiveData()

    //after update room database update firebase property back it to false
    fun updateVideoComplete(model: VideoModel) = viewModelScope.launch {
        updateComplete(model)
    }

    @ExperimentalCoroutinesApi
    fun loadVideosFromFirebase() {
        videosResponse.value = NetworkResult.Loading()
        if (hasInternetConnection()) {
            try {
                //init array list before adding data

                val list: ArrayList<VideoModel> = ArrayList()

                val dbRef = FirebaseDatabase.getInstance().getReference("Videos")


                dbRef.addValueEventListener(object : ValueEventListener {

                    override fun onCancelled(error: DatabaseError) {
                        Log.d("errore", "Value is: " + error.message)

                    }


                    override fun onDataChange(snapshot: DataSnapshot) {
                        //clear the list before adding data

                        for (ds in snapshot.children) {
                            ds.let {

                                //get data as model
                                val modelVideo: VideoModel? = ds.getValue(VideoModel::class.java)
                                //add to array list
                                if (modelVideo != null) {

                                    list.add(modelVideo)
//                                    saveVideoData(modelVideo)
                                } else {
                                    videosResponse.value =
                                        NetworkResult.Error("sorry we will add new videos.")
                                }

                            }

                        }
//                        sendVideoListToCheck(list)
                        videosResponse.value = NetworkResult.Success(list)
//                        fragment.successDashboardItemsList(list)

                    }

                })

            } catch (e: Exception) {

                videosResponse.value = NetworkResult.Error("Videos not found.")
//                fragment.hideLoading()
//                fragment.offline()

            }
        } else {
            videosResponse.value = NetworkResult.Error("No Internet Connection.")
        }

    }

    private suspend fun updateComplete(model: VideoModel) {
        if (hasInternetConnection()) {

            val ref = FirebaseDatabase.getInstance().reference
            val applesQuery: Query =
                ref.child("Videos").orderByChild("id").equalTo(model.id)
            applesQuery.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (appleSnapshot in dataSnapshot.children) {
                        appleSnapshot.ref.child("update").setValue(false) .addOnSuccessListener {
                            Toast.makeText(getApplication(), "update is complete", Toast.LENGTH_SHORT)
                                .show()
                            //when update is complete and properties (videoUpdate) back to false in firebase should update either in database
                            updateVideoRoomComplete(model.id!!,false)
                        }
                            .addOnFailureListener {
                                Toast.makeText(getApplication(), "update is failed", Toast.LENGTH_SHORT).show()

                            }

                    }
                    Log.d("updateComplete", "${model.title} video is update : successfully ")
//                Toast.makeText(this@MainActivity, "", Toast.LENGTH_SHORT).show()
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.e("updateComplete", "onCancelled", databaseError.toException())
                }
            })

//            val dbRef = FirebaseDatabase.getInstance().getReference("Videos")
//            val l = dbRef.child(model.id!!)
//            l.child("update").setValue(false)
//                .addOnSuccessListener {
//                    Toast.makeText(getApplication(), "update is complete", Toast.LENGTH_SHORT)
//                        .show()
//                    //fun updateVideoRoomComplete(tid: Int, video: Boolean)
//                    updateVideoRoomComplete(model.id!!,false)
//                }
//                .addOnFailureListener {
//                    Toast.makeText(getApplication(), "update is failed", Toast.LENGTH_SHORT).show()
//
//                }
        }


    }


    private fun hasInternetConnection(): Boolean {
        val connectivityManager = getApplication<Application>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }
}
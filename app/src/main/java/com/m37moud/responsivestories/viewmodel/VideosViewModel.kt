package com.m37moud.responsivestories.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.m37moud.responsivestories.data.DataStoreRepository
import com.m37moud.responsivestories.util.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class VideosViewModel @ViewModelInject constructor(
    application: Application,
    private val dataStoreRepository: DataStoreRepository
) : AndroidViewModel(application){
    var networkStatus = false
    var backOnline = false


    private var categoryType = Constants.DEFAULT_CATEGORY_TYPE

    val readBackOnline = dataStoreRepository.readBackOnline.asLiveData()
    val readShouldDownload = dataStoreRepository.readDownloadStatus.asLiveData()
    val readShouldLoad = dataStoreRepository.readLoadingStatus.asLiveData()
    val readCategoriesType = dataStoreRepository.readCategoryType.asLiveData()



    private fun saveBackOnline(backOnline: Boolean) =
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepository.saveBackOnline(backOnline)
        }





    fun showNetworkStatus() {
        if (!networkStatus) {
            Toast.makeText(getApplication(), "No Internet Connection.", Toast.LENGTH_SHORT).show()
            saveBackOnline(true)
        } else if (networkStatus) {
            if (backOnline) {
                Toast.makeText(getApplication(), "We're back online.", Toast.LENGTH_SHORT).show()
                saveBackOnline(false)
            }
        }
    }


    //download status

     fun saveDownloadStatus(downloadStatus: Boolean) =
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepository.saveDownloadStatus(downloadStatus)
        }

    fun saveLoadingStatus(loadingStatus: Boolean) =
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepository.saveDownloadStatus(loadingStatus)
        }


    fun saveCategoryType(mealType: String, mealTypeId: Int) =
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepository.saveCategoryType(mealType, mealTypeId)
        }

}
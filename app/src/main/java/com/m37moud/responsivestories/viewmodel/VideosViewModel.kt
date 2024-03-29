package com.m37moud.responsivestories.viewmodel

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.m37moud.responsivestories.data.DataStoreRepository
import com.m37moud.responsivestories.util.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class VideosViewModel @ViewModelInject constructor(
    application: Application,
    private val dataStoreRepository: DataStoreRepository
) : AndroidViewModel(application) {
    var networkStatus = false
    var backOnline = false
    var exitStatus = false


    private var categoryType = Constants.DEFAULT_CATEGORY_TYPE

    val readBackOnline = dataStoreRepository.readBackOnline.asLiveData()
    val readShouldDownload = dataStoreRepository.readDownloadStatus.asLiveData()
    val readShouldOpenCategoryFab = dataStoreRepository.readLoadingStatus.asLiveData()//fab category btn status
    val readBottomSheetExitStatus = dataStoreRepository.readExitStatus.asLiveData()


    val readCategoriesType = dataStoreRepository.readCategoryType


    private fun saveBackOnline(backOnline: Boolean) =
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepository.saveBackOnline(backOnline)
        }


    fun showNetworkStatus() {
        if (!networkStatus) {
            Toast.makeText(getApplication(), "No Internet Connection", Toast.LENGTH_SHORT).show()
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
// fab category button
    fun saveFabCategoryStatus(loadingStatus: Boolean) =
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepository.saveLoadingStatus(loadingStatus)
        }


    fun saveCategoryType(mealType: String, mealTypeId: Int) =
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepository.saveCategoryType(mealType, mealTypeId)
        }

    fun saveExitStatus(exit: Boolean) =
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepository.saveExitStatus(exit)
        }


    fun applyQuery(): String {
        Log.d("videosViewModel", "applyQuery called")

        viewModelScope.launch {
            readCategoriesType.collect { value ->

                categoryType = if (value.selectedCategoryType == "all" || value.selectedCategoryType =="الكل") {
                    Constants.DEFAULT_CATEGORY_TYPE
                } else
                    value.selectedCategoryType

            }
        }
        Log.d("videosViewModel", "applyQuery categoryType : $categoryType")


        return categoryType
    }

}
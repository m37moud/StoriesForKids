package com.m37moud.responsivestories

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.m37moud.responsivestories.viewmodel.VideosViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var videosViewModel: VideosViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.hide()

        videosViewModel = ViewModelProvider(this@MainActivity).get(VideosViewModel::class.java)

    }

    override fun onStart() {
        videosViewModel.saveDownloadStatus(false)
        super.onStart()
    }

    override fun onDestroy() {
//        when app end download status = false
        Toast.makeText(
            this,
            "saveDownloadStatus = false",
            Toast.LENGTH_SHORT
        ).show()
        Log.d("mainAcc", "onDestroy! -> saveDownloadStatus = false" )
        videosViewModel.saveDownloadStatus(false)
        super.onDestroy()
    }
}
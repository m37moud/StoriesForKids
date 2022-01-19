package com.m37moud.responsivestories.ui.activities.started

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.m37moud.responsivestories.R
import com.m37moud.responsivestories.firebase.RemoteConfigUtils.getDonateLink
import kotlinx.android.synthetic.main.activity_webview.*


class WebViewActivity : AppCompatActivity() {

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webview)

        webView1!!.settings.javaScriptEnabled = true

        webView1.settings.builtInZoomControls = true
        webView1.settings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
        val activity: Activity = this
        webView1.webViewClient = object : WebViewClient() {
            override fun onReceivedError(
                view: WebView, 
                errorCode: Int,
                description: String,
                failingUrl: String
            ) {
                Toast.makeText(activity, description, Toast.LENGTH_SHORT).show()
            }
        }
        val donateLink: String? = intent.getStringExtra("donateLink")

        donateLink?.let { webView1.loadUrl(it) }


        backbutn.setOnClickListener {
            finish()
        }
    }
}
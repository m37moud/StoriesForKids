package com.m37moud.responsivestories.ui.activities.story

import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.offline.DownloadHelper
import com.google.android.exoplayer2.offline.DownloadRequest
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.*
import com.google.android.exoplayer2.upstream.BandwidthMeter
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.m37moud.responsivestories.R
import com.m37moud.responsivestories.nativetemplates.NativeTemplateStyle
import com.m37moud.responsivestories.nativetemplates.TemplateView
import com.m37moud.responsivestories.ui.activities.learn.AD_REWARDEDAD_ID
import com.m37moud.responsivestories.util.AdaptiveExoplayer
import com.m37moud.responsivestories.util.Constants
import com.m37moud.responsivestories.util.Constants.Companion.interstitialAds
import com.m37moud.responsivestories.util.Constants.Companion.showAdsFromRemoteConfig
import com.m37moud.responsivestories.util.media.AudioManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_offline_player.*
import javax.inject.Inject

const val AD_InterstitialAd_ID = "ca-app-pub-3940256099942544/1033173712"
@AndroidEntryPoint
class OfflinePlayerActivity : AppCompatActivity() {
    private lateinit var player: SimpleExoPlayer
    private lateinit var videoUri: Uri
    private lateinit var dataSourceFactory: DataSource.Factory
    lateinit var adLoader: AdLoader

    private var shouldPlay = false



    @Inject
    lateinit var audioManager: AudioManager


    private val KEY_POSITION = "position"
    private var position = 0L

    //ads refrence
    private var mInterstitialAd: InterstitialAd ? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setFullScreen()
        setContentView(R.layout.activity_offline_player)

        dataSourceFactory = buildDataSourceFactory()!!
        hideActionBar()
        initializePlayer()
//        showAdsFromRemoteConfig = RemoteConfigUtils.getAdsState()
        Log.d("OfflinePlayerActivity", "showAdsFromRemoteConfig: $showAdsFromRemoteConfig ")

        if (showAdsFromRemoteConfig)
                 loadAd()

    }

    private fun initializePlayer() {

        playVideo()
    }

    private fun hideActionBar() {
        supportActionBar!!.hide()
    }

    private fun setFullScreen() {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
    }

    private fun playVideo() {
        try {
//
            player = SimpleExoPlayer.Builder(this)
                .build()
            val intent = intent
            val url = intent.getStringExtra("videoUri")
            videoUri = Uri.parse(url)

            val renderersFactory = DefaultRenderersFactory(
                this, null,
                DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER
            )

            val bandwidthMeter: BandwidthMeter = DefaultBandwidthMeter()
            val videoTrackSelectionFactory: TrackSelection.Factory =
                AdaptiveTrackSelection.Factory(bandwidthMeter)
            val trackSelector: TrackSelector = DefaultTrackSelector(videoTrackSelectionFactory)

            player = ExoPlayerFactory.newSimpleInstance(this, renderersFactory, trackSelector)

            val downloadRequest: DownloadRequest =
                AdaptiveExoplayer.getInstance(this).downloadTracker
                    .getDownloadRequest(videoUri)
            val mediaSource =
                DownloadHelper.createMediaSource(downloadRequest, dataSourceFactory)

            player.prepare(mediaSource, false, true)

            player.addListener(object : Player.EventListener {

                override fun onTimelineChanged(timeline: Timeline, reason: Int) {
                }

                //
                override fun onTracksChanged(
                    trackGroups: TrackGroupArray,
                    trackSelections: TrackSelectionArray
                ) {
                    Log.v(" ", "Listener-onTracksChanged...")
                }

                override fun onLoadingChanged(isLoading: Boolean) {
                    Log.v(
                        "FragmentActivity.TAG",
                        "Listener-onLoadingChanged...isLoading:$isLoading"
                    )
                }

                override fun onPlayerStateChanged(
                    playWhenReady: Boolean,
                    playbackState: Int
                ) {
                    Log.v("FragmentActivity.TAG", "Listener-onPlayerStateChanged...$playbackState")
                    when (playbackState) {
                        ExoPlayer.STATE_IDLE -> {
                            OfflinePlayerView.keepScreenOn = false
//                            hideAds()
                            Log.d(
                                "FragmentActivity.TAG",
                                "playbackState : " + "STATE_IDLE"
                            )

                        }
                        ExoPlayer.STATE_BUFFERING -> {
                            Log.d("FragmentActivity.TAG", "playbackState : " + "STATE_BUFFERING")
                            loading_exoplayer_offline.visibility = View.VISIBLE
                        }

                        ExoPlayer.STATE_READY -> {
                            OfflinePlayerView.keepScreenOn = true
                            Log.d("FragmentActivity.TAG", "playbackState : " + "STATE_READY")
                            loading_exoplayer_offline.visibility = View.GONE
                            if (player.isPlaying) {

//                                showAds()
                                my_template.visibility = View.GONE

                            } else {
                                showNativeAds()
//                                hideAds()

                            }
                        }
                        ExoPlayer.STATE_ENDED -> {
                            showNativeAds()
                            OfflinePlayerView.keepScreenOn = false
//                            hideAds()
                            Log.d(
                                "FragmentActivity.TAG",
                                "playbackState : " + "STATE_ENDED"
                            )
                        }
                        else -> {
                        }
                    }
                }

                override fun onRepeatModeChanged(repeatMode: Int) {
                    Log.v("FragmentActivity.TAG", "Listener-onRepeatModeChanged...")
                }

                override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {}
                override fun onPlayerError(error: ExoPlaybackException) {
                    Log.v("FragmentActivity.TAG", "Listener-onPlayerError...")
                    player.stop()
                    player.prepare(mediaSource)
                    player.playWhenReady = true
                }

                override fun onPositionDiscontinuity(reason: Int) {}
                override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters) {
                    Log.v("FragmentActivity.TAG", "Listener-onPlaybackParametersChanged...")
                }

                /**
                 * Called when all pending seek requests have been processed by the simpleExoPlayer. This is guaranteed
                 * to happen after any necessary changes to the simpleExoPlayer state were reported to
                 * [.onPlayerStateChanged].
                 */
                override fun onSeekProcessed() {}

            })

            OfflinePlayerView.keepScreenOn = true
            OfflinePlayerView.player = player
            player.playWhenReady = true

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    override fun onBackPressed() {
        super.onBackPressed()

        player.playWhenReady = false
        player.release()
    }

    private fun buildDataSourceFactory(): DataSource.Factory? {
        return AdaptiveExoplayer.getInstance(this).buildDataSourceFactory()
    }

//    private fun showAds() {
//
//        val adRequest = AdRequest.Builder()
//
//            .build()
//        ad_viewOffline.visibility = View.VISIBLE
//        ad_viewOffline.loadAd(adRequest)
//
//
//    }
//    private fun hideAds(){
//        ad_viewOffline.pause()
//        ad_viewOffline.visibility = View.GONE
//    }

    private fun showNativeAds() {

        adLoader = AdLoader.Builder(this, "ca-app-pub-3940256099942544/2247696110")
            .forNativeAd { ad: NativeAd ->
                // Show the ad.

                if (isDestroyed) {
                    ad.destroy()
                    my_template.visibility = View.GONE
                    return@forNativeAd
                }


                if (adLoader.isLoading) {
                    // The AdLoader is still loading ads.
                    // Expect more adLoaded or onAdFailedToLoad callbacks.
                    val styles: NativeTemplateStyle =
                        NativeTemplateStyle.Builder().build()
                    val template: TemplateView = findViewById(R.id.my_template)
                    template.setStyles(styles)
                    template.setNativeAd(ad)
                    my_template.visibility = View.VISIBLE
                } else {
                    Log.d("showNativeAds", "showNativeAds: error if false")
                    // The AdLoader has finished loading ads.
                }


            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    // Handle the failure by logging, altering the UI, and so on.
                }


            })

            .withNativeAdOptions(
                NativeAdOptions.Builder()
                    // Methods in the NativeAdOptions.Builder class can be
                    // used here to specify individual options settings.
                    .build()
            )
            .build()

        adLoader.loadAd(AdRequest.Builder().build())


    }


    private fun loadAd() {
        val mInterstitialAdsID = if(TextUtils.isEmpty(interstitialAds))
            AD_InterstitialAd_ID
        else
            interstitialAds.toString()

        var adRequest = AdRequest.Builder().build()


        InterstitialAd.load(
            this, mInterstitialAdsID, adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.d("loadAd", adError?.message)
                    mInterstitialAd = null
//                    mAdIsLoading = false
                    val error = "domain: ${adError.domain}, code: ${adError.code}, " +
                            "message: ${adError.message}"
                    Toast.makeText(
                        this@OfflinePlayerActivity,
                        "onAdFailedToLoad() with error $error",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    Log.d("loadAd", "Ad was loaded.")
                    mInterstitialAd = interstitialAd
//                    mAdIsLoading = false
                    mInterstitialAd?.fullScreenContentCallback =
                        object : FullScreenContentCallback() {
                            override fun onAdDismissedFullScreenContent() {
                                Log.d("loadAd", "showInterstitial Ad was dismissed.")
                                // Don't forget to set the ad reference to null so you
                                // don't show the ad a second time.
                                mInterstitialAd = null
//                                shouldPlay = true

//                                loadAd()
                            }

                            override fun onAdFailedToShowFullScreenContent(adError: AdError?) {
                                Log.d("loadAd", "showInterstitial Ad failed to show.")
                                // Don't forget to set the ad reference to null so you
                                // don't show the ad a second time.
                                mInterstitialAd = null
                                shouldPlay = true

                            }

                            override fun onAdShowedFullScreenContent() {
                                Log.d("loadAd", "showInterstitial Ad showed fullscreen content.")
                                // Called when ad is dismissed.
                                mInterstitialAd = null

                            }
                        }

                }
            }
        )
    }

    override fun finish() {
        //show ads
        //stop playing sound
        if (!shouldPlay) {
            this.audioManager.getAudioService()?.pauseMusic()

        }
        if (mInterstitialAd != null) {
            mInterstitialAd?.show(this)
            super.finish()
        } else {
            shouldPlay = true
            Toast.makeText(this, "Ad wasn't loaded.", Toast.LENGTH_SHORT).show()
            super.finish()
        }


    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        position = player.currentPosition
        outState.putLong(KEY_POSITION, position)
//        outState.putBoolean(KEY_PLAYER_PLAY_WHEN_READY, player.playWhenReady)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        savedInstanceState.let {
            player.seekTo(it.getLong(KEY_POSITION))
//            player.playWhenReady = it.getBoolean(KEY_PLAYER_PLAY_WHEN_READY)
        }
    }


    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
//            hideAds()
            Toast.makeText(this, "landscape", Toast.LENGTH_SHORT).show()
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
//            showAds()
            Toast.makeText(this, "portrait", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onPause() {
//        hideAds()
        player.playWhenReady = false
        player.release()
        super.onPause()

    }


    override fun onStop() {
//        hideAds()
        if (!shouldPlay) {
//            stopService()
            this.audioManager.getAudioService()?.pauseMusic()

        }
        player.playWhenReady = false

        player.release()
        super.onStop()
    }
//

    // Called when returning to the activity
    public override fun onResume() {
        super.onResume()
//        initializePlayer()
        ad_viewOffline.resume()
    }


    // Called before the activity is destroyed
    public override fun onDestroy() {
        if (mInterstitialAd != null) {
            mInterstitialAd = null
        }
        ad_viewOffline.destroy()
        player.release()
        super.onDestroy()
    }


}
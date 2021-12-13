package com.m37moud.responsivestories.ui.activities.story

import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.extractor.ExtractorsFactory
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.trackselection.TrackSelector
import com.google.android.exoplayer2.ui.PlayerControlView
import com.google.android.exoplayer2.upstream.BandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.google.android.exoplayer2.video.VideoRendererEventListener
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.m37moud.responsivestories.R
import com.m37moud.responsivestories.nativetemplates.NativeTemplateStyle
import com.m37moud.responsivestories.nativetemplates.TemplateView
import com.m37moud.responsivestories.util.Constants
import com.m37moud.responsivestories.util.Constants.Companion.USER_AGENT
import com.m37moud.responsivestories.util.media.AudioManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_offline_player.*
import kotlinx.android.synthetic.main.activity_player.*
import kotlinx.android.synthetic.main.offline_player_custom_control.*
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class OnlinePlayerActivity : AppCompatActivity(), View.OnClickListener, VideoRendererEventListener,
    PlaybackPreparer, PlayerControlView.VisibilityListener {

    private lateinit var simpleExoPlayer : SimpleExoPlayer
    private lateinit var videoUri : Uri
    private lateinit var handler: Handler
    private lateinit var mFormatBuilder: StringBuilder
    private lateinit var mFormatter: Formatter
    lateinit var adLoader: AdLoader

    private var shouldPlay = false


    @Inject
    lateinit var audioManager: AudioManager
    private val KEY_POSITION = "position"
    private var position = 0L
    private var mInterstitialAd: InterstitialAd? = null

    private lateinit var extractorsFactory : ExtractorsFactory
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setFullScreen()
        setContentView(R.layout.activity_player)
//        initializePlayer()
        prepareView()
        if (Constants.showAdsFromRemoteConfig)
            loadAd()
    }

    private fun initExoplayer(){
        simpleExoPlayer = SimpleExoPlayer.Builder(this)

            .build()
        val intent = intent
        val url = intent.getStringExtra("videoUri")
        videoUri = Uri.parse(url)
        extractorsFactory = DefaultExtractorsFactory()
        val bandwidthMeter: BandwidthMeter = DefaultBandwidthMeter()
        val trackSelector: TrackSelector =
            DefaultTrackSelector(AdaptiveTrackSelection.Factory(bandwidthMeter))
        simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(this, trackSelector)
        extractorsFactory = DefaultExtractorsFactory()

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
            val playerInfo: String = Util.getUserAgent(this, USER_AGENT)
            val dataSourceFactory =
                DefaultDataSourceFactory(this, playerInfo)
            val mediaSource: MediaSource = ExtractorMediaSource(
                videoUri, dataSourceFactory, extractorsFactory, null, null
            )
            OnlinePlayerView.keepScreenOn = true
            OnlinePlayerView.player = simpleExoPlayer

            simpleExoPlayer.prepare(mediaSource)
            simpleExoPlayer.addListener(object : Player.EventListener {

                override fun onTimelineChanged(timeline: Timeline, reason: Int) {
                }
//
                override fun onTracksChanged(
                    trackGroups: TrackGroupArray,
                    trackSelections: TrackSelectionArray
                ) {
                    Log.v( " ", "Listener-onTracksChanged...")
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
                        ExoPlayer.STATE_IDLE ->{OnlinePlayerView.keepScreenOn = false
                            Log.d(
                                "FragmentActivity.TAG",
                                "playbackState : " + "STATE_IDLE"
                            )
                        }
                        ExoPlayer.STATE_BUFFERING -> {
                            Log.d("FragmentActivity.TAG", "playbackState : " + "STATE_BUFFERING")
                            loading_exoplayer_online.visibility = View.VISIBLE
                        }
                        ExoPlayer.STATE_READY -> {
                            Log.d("FragmentActivity.TAG", "playbackState : " + "STATE_READY")
                            loading_exoplayer_online.visibility =View.GONE
                        }
                        ExoPlayer.STATE_ENDED ->{OnlinePlayerView.keepScreenOn = false
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
                    simpleExoPlayer.stop()
                    simpleExoPlayer.prepare(mediaSource)
                    simpleExoPlayer.setPlayWhenReady(true)
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
            simpleExoPlayer.playWhenReady = true

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

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
        val mInterstitialAdsID = if (TextUtils.isEmpty(Constants.interstitialAds))
            AD_InterstitialAd_ID
        else
            Constants.interstitialAds.toString()

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
                        this@OnlinePlayerActivity,
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
        position = simpleExoPlayer.currentPosition
        outState.putLong(KEY_POSITION, position)
//        outState.putBoolean(KEY_PLAYER_PLAY_WHEN_READY, player.playWhenReady)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        savedInstanceState.let {
            simpleExoPlayer.seekTo(it.getLong(KEY_POSITION))
//            player.playWhenReady = it.getBoolean(KEY_PLAYER_PLAY_WHEN_READY)
        }
    }
    override fun onResume() {
        super.onResume()
        ad_viewOffline.resume()
//        if (Util.SDK_INT <= 23 || playerView == null) {
//        initExoplayer()
////            if (playerView != null) {
////                playerView.onResume();
////            }
////        }
//
//        FullScreencall()
    }

    override fun onPause() {
        super.onPause()
        simpleExoPlayer.release()

        simpleExoPlayer.playWhenReady =false
    }
    override fun onStop() {
//        hideAds()
        if (!shouldPlay) {
//            stopService()
            this.audioManager.getAudioService()?.pauseMusic()

        }
        simpleExoPlayer.playWhenReady = false

        simpleExoPlayer.release()
        super.onStop()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        simpleExoPlayer.playWhenReady =false
        shouldPlay = true
        simpleExoPlayer.release()
    }
    override fun onClick(view: View?) {
        if (view!!.id == R.id.img_back_player) {
            onBackPressed()
        }
    }

    override fun preparePlayback() {
        initExoplayer()
    }

    override fun onVisibilityChange(visibility: Int) {
    }

    private fun finishActivity() {
        this.finish()
    }
    private fun prepareView() {
        setProgress()
    }

    private fun setProgress() {
        handler = Handler(Looper.getMainLooper())
        //Make sure you update Seekbar on UI thread
        handler.post(object : Runnable {
            override fun run() {
                if (simpleExoPlayer != null) {
                    tv_player_current_time.text =
                        stringForTime(simpleExoPlayer.currentPosition.toInt())
                    tv_player_end_time.text = stringForTime(simpleExoPlayer.duration.toInt())
                    handler.postDelayed(this, 1000)
                }
            }
        })
    }

    private fun stringForTime(timeMs: Int): String? {
        mFormatBuilder = StringBuilder()
        mFormatter = Formatter(mFormatBuilder, Locale.getDefault())
        val totalSeconds = timeMs / 1000
        val seconds = totalSeconds % 60
        val minutes = totalSeconds / 60 % 60
        val hours = totalSeconds / 3600
        mFormatBuilder.setLength(0)
        return if (hours > 0) {
            mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString()
        } else {
            mFormatter.format("%02d:%02d", minutes, seconds).toString()
        }
    }
}
package com.m37moud.responsivestories.ui.fragments.story

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.offline.DownloadHelper
import com.google.android.exoplayer2.offline.DownloadRequest
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.*
import com.google.android.exoplayer2.upstream.BandwidthMeter
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView
import com.m37moud.responsivestories.R
import com.m37moud.responsivestories.util.AdaptiveExoplayer
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_offline_player.*
import kotlinx.android.synthetic.main.activity_player.*


class OfflinePlayerActivity : AppCompatActivity() {
    private lateinit var player: SimpleExoPlayer
    private lateinit var videoUri: Uri
    private lateinit var dataSourceFactory: DataSource.Factory
    lateinit var adLoader: AdLoader

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setFullScreen()
        setContentView(R.layout.activity_offline_player)

        dataSourceFactory = buildDataSourceFactory()!!
        hideActionBar()
        initializePlayer()

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
                            Log.d("FragmentActivity.TAG", "playbackState : " + "STATE_READY")
                            loading_exoplayer_offline.visibility = View.GONE
                            if (player.isPlaying) {
                                ad_viewOffline.visibility = View.VISIBLE
                                showAds()

                            } else {
                                showNativeAds()
//                                ad_viewOffline.pause()
//                                ad_viewOffline.visibility = View.GONE
                            }
                        }
                        ExoPlayer.STATE_ENDED -> {
                            OfflinePlayerView.keepScreenOn = false
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
                    player.setPlayWhenReady(true)
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

    private fun showAds() {

        val adRequest = AdRequest.Builder()

            .build()
        ad_viewOffline.loadAd(adRequest)


    }

    private fun showNativeAds() {

         adLoader = AdLoader.Builder(this, "ca-app-pub-3940256099942544/2247696110")
            .forNativeAd { ad: NativeAd ->
                // Show the ad.
                adLoader.loadAds(AdRequest.Builder().build(), 5)

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
                .build())
            .build()



    }

    override fun onPause() {
        ad_viewOffline.pause()
        player.playWhenReady = false
        super.onPause()

    }
//

    // Called when returning to the activity
    public override fun onResume() {
        super.onResume()
        ad_viewOffline.resume()
    }

    // Called before the activity is destroyed
    public override fun onDestroy() {
        ad_viewOffline.destroy()
        super.onDestroy()
    }


}
package com.m37moud.responsivestories.ui.activities.story

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
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
import com.google.android.exoplayer2.upstream.BandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.m37moud.responsivestories.R
import com.m37moud.responsivestories.util.Constants.Companion.USER_AGENT
import kotlinx.android.synthetic.main.activity_player.*


class OnlinePlayerActivity : AppCompatActivity() {

    private lateinit var player : SimpleExoPlayer
    private lateinit var videoUri : Uri

    private lateinit var extractorsFactory : ExtractorsFactory
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setFullScreen()
        setContentView(R.layout.activity_player)
        hideActionBar()
        initializePlayer()
    }

    private fun initializePlayer(){
        player = SimpleExoPlayer.Builder(this)

            .build()
        val intent = intent
        val url = intent.getStringExtra("videoUri")
        videoUri = Uri.parse(url)
        extractorsFactory = DefaultExtractorsFactory()
        val bandwidthMeter: BandwidthMeter = DefaultBandwidthMeter()
        val trackSelector: TrackSelector =
            DefaultTrackSelector(AdaptiveTrackSelection.Factory(bandwidthMeter))
        player = ExoPlayerFactory.newSimpleInstance(this, trackSelector)
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
            OnlinePlayerView.player = player

            player.prepare(mediaSource)
            player.addListener(object : Player.EventListener {

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
            player.playWhenReady = true

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onPause() {
        super.onPause()
        player.playWhenReady =false
    }

    override fun onBackPressed() {
        super.onBackPressed()
        player.playWhenReady =false
        player.release()
    }
}
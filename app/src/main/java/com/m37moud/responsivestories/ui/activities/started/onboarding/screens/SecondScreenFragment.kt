package com.m37moud.responsivestories.ui.activities.started.onboarding.screens

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.m37moud.responsivestories.R
import com.m37moud.responsivestories.util.media.AudioManager
import com.m37moud.responsivestories.util.media.PodcastEntryPoint
import dagger.hilt.android.EntryPointAccessors
import kotlinx.android.synthetic.main.fragment_second_screen.view.*

class SecondScreenFragment : Fragment() {
    private val audioManager: AudioManager by lazy {
        EntryPointAccessors.fromApplication (context,
            PodcastEntryPoint::class.java).audioManager()
    }
    private var shouldPlay = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_second_screen, container, false)

        val viewPager = activity?.findViewById<ViewPager2>(R.id.viewPager)
        this.audioManager.getAudioService()?.playMusic()

        view.next2.setOnClickListener {
            viewPager?.currentItem = 2
            requireActivity().finish()

        }

        return view
    }
    override fun onStop() {

        if (!shouldPlay) {
            this.audioManager.getAudioService()?.pauseMusic()

        }

        super.onStop()
    }

    override fun onResume() {
        this.audioManager.getAudioService()?.resumeMusic()


        super.onResume()
    }

}
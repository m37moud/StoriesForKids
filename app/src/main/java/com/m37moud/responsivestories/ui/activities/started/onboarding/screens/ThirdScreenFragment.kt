package com.m37moud.responsivestories.ui.activities.started.onboarding.screens

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.m37moud.responsivestories.R
import com.m37moud.responsivestories.ui.activities.started.onboarding.StartActivity
import kotlinx.android.synthetic.main.fragment_third_screen.*
import kotlinx.android.synthetic.main.fragment_third_screen.view.*

class ThirdScreenFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_third_screen, container, false)


        val viewPager = activity?.findViewById<ViewPager2>(R.id.viewPager)
        view.finish.setOnClickListener {
//            findNavController().navigate(R.id.action_viewPagerFragment_to_storyFragment2)
            startActivity(Intent(requireContext(), StartActivity::class.java))

            requireActivity().finish()

            onBoardingFinished()

        }



        return view
    }

    private fun onBoardingFinished(){
        val sharedPref = requireActivity().getSharedPreferences("onBoarding", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putBoolean("Finished", true)
        editor.apply()
    }

}
package com.m37moud.responsivestories.ui.fragments.started.onboarding

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.m37moud.responsivestories.R
import com.m37moud.responsivestories.ui.fragments.started.onboarding.screens.FirstScreenFragment
import com.m37moud.responsivestories.ui.fragments.started.onboarding.screens.SecondScreenFragment
import com.m37moud.responsivestories.ui.fragments.started.onboarding.screens.ThirdScreenFragment
import kotlinx.android.synthetic.main.fragment_view_pager.view.*

class ViewPagerFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_view_pager, container, false)
        val fragmentList = arrayListOf<Fragment>(
            FirstScreenFragment(),
            SecondScreenFragment(),
            ThirdScreenFragment()
        )

        val adapter : ViewPagerAdapter = ViewPagerAdapter(
            fragmentList,
            requireActivity().supportFragmentManager,
            lifecycle
        )

        view.viewPager.adapter = adapter

        return view
    }


}
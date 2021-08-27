package com.m37moud.responsivestories.ui.fragments.started.onboarding

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import com.m37moud.responsivestories.R
import com.m37moud.responsivestories.ui.fragments.started.onboarding.screens.FirstScreenFragment
import com.m37moud.responsivestories.ui.fragments.started.onboarding.screens.SecondScreenFragment
import com.m37moud.responsivestories.ui.fragments.started.onboarding.screens.ThirdScreenFragment
import kotlinx.android.synthetic.main.fragment_view_pager.*
import kotlinx.android.synthetic.main.fragment_view_pager.view.*

class ViewPagerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_view_pager)
        val fragmentList = arrayListOf<Fragment>(
            FirstScreenFragment(),
            SecondScreenFragment(),
            ThirdScreenFragment()
        )

        val adapter : ViewPagerAdapter = ViewPagerAdapter(
            fragmentList,
            this@ViewPagerActivity.supportFragmentManager,
            lifecycle
        )

        viewPager.adapter = adapter

    }





}
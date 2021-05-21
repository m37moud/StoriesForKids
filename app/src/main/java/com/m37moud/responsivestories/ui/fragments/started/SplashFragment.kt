package com.m37moud.responsivestories.ui.fragments.started

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Parcel
import android.os.Parcelable
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.m37moud.responsivestories.R

class SplashFragment() : Fragment(), Parcelable {


    constructor(parcel: Parcel) : this() {
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        Handler().postDelayed({
            if(onBoardingFinished()){
                findNavController().navigate(R.id.action_splashFragment_to_homeActivity)
            }else{
                findNavController().navigate(R.id.action_splashFragment_to_viewPagerFragment)
            }
        } , 2500)
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

    private fun onBoardingFinished(): Boolean{
        val sharedPref = requireActivity().getSharedPreferences("onBoarding", Context.MODE_PRIVATE)
        return sharedPref.getBoolean("Finished", false)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SplashFragment> {
        override fun createFromParcel(parcel: Parcel): SplashFragment {
            return SplashFragment(parcel)
        }

        override fun newArray(size: Int): Array<SplashFragment?> {
            return arrayOfNulls(size)
        }
    }


}
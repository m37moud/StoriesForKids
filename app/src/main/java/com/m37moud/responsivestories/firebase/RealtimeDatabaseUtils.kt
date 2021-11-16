package com.m37moud.responsivestories.firebase

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.m37moud.responsivestories.models.AdsModel
import com.m37moud.responsivestories.util.NetworkResult

object RealtimeDatabaseUtils {
    private lateinit var database: FirebaseDatabase

    fun init() {
        database = getFirebaseDatabase()
    }

    private fun getFirebaseDatabase(): FirebaseDatabase {

        return Firebase.database

    }

    fun getAdsStatus(): Boolean {
        var status: Boolean= false

        database.getReference("AdsFolder").child("activateAds")
            .addValueEventListener(object : ValueEventListener {

                override fun onCancelled(error: DatabaseError) {
                    Log.d("getAdsStatus", "Value is: " + error.message)

                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    //clear the list before adding data

                     status = snapshot.getValue(Boolean::class.java)!!
                    Log.d("getAdsStatus", "Value is: " + status.toString())


                }

            }

            )

        return if (status) {
            true
        } else {
            false
        }

    }

}
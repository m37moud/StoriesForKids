package com.m37moud.responsivestories

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.widget.MediaController
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_add_video.*

class AddVideoActivity : AppCompatActivity() {

    // pick video constant

    private val VIDEO_PICK_GALLERY_CODE = 100
    private val VIDEO_PICK_CAMERA_CODE = 101

    // permission request code
    private val CAMERA_REQUEST_CODE = 102

    private lateinit var cameraPermissions: Array<String>
    //progress bar
    private lateinit var progressDialog : ProgressDialog


    private var videoUri: Uri? = null

    private lateinit var actionBar: ActionBar

    private var title = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_video)

        actionBar = supportActionBar!!
        actionBar.title = "add new video"

        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.setDisplayShowHomeEnabled(true)


        cameraPermissions =
            arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)

        // init progress bar
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("please wait")
        progressDialog.setMessage("Uploading Video...")
        progressDialog.setCanceledOnTouchOutside(false)

        //handle upload button
        upload_btn.setOnClickListener {
            title = editText.text.toString().trim()

            if(TextUtils.isEmpty(title)){
                Toast.makeText(this, "Title Required", Toast.LENGTH_SHORT).show()
            }else if(videoUri == null){
                Toast.makeText(this, "Select Video First", Toast.LENGTH_SHORT).show()
            }
            else{
                //title and video is selected
                uploadVideoFirebase()
            }

        }

        pick_video_fab.setOnClickListener {

            videoPickDialog()
        }
    }

    private fun uploadVideoFirebase() {
       // show progress dialog
        progressDialog.show()
        //time stamp
        val timestamp = "" + System.currentTimeMillis()

        //file path and name for firebase storage

        val filePathAndName = "Videos/video_$timestamp"

        //storage reference
        val storageReference = FirebaseStorage.getInstance().getReference(filePathAndName)

        //upload video using uri of video to storage
        storageReference.putFile(videoUri!!)
            .addOnSuccessListener { taskSnapshot ->
                Log.d("uploadVideoFirebase", "succ :  storage ")
                //upload video , and get url to this video
                val uriTask = taskSnapshot.storage.downloadUrl
                while (!uriTask.isSuccessful);
                val downloadUri = uriTask.result
                if(uriTask.isSuccessful){
                    //video url is received successful

                    //add details to HashMap then firebase db
                    val hashMap = HashMap<String,Any>()
                    hashMap["id"] = "$timestamp"
                    hashMap["title"] = "$title"
                    hashMap["timestamp"] = "$timestamp"
                    hashMap["videoUri"] = "$downloadUri"

                    //add HashMap info to firebase db
                    val dbReference = FirebaseDatabase.getInstance().getReference("Videos")
                    dbReference.child(timestamp)
                        .setValue(hashMap)
                        .addOnSuccessListener {taskSnapshot ->

                            Log.d("uploadVideoFirebase", "succ :  database ")
                            //video info add successfully

                            progressDialog.dismiss()
                            Toast.makeText(this, "Video Uploaded successfully", Toast.LENGTH_SHORT).show()
                        }

                        .addOnFailureListener {e ->
                            Log.d("uploadVideoFirebase", "err :  database ")
                            progressDialog.dismiss()

                            //failed to add info to database
                            Toast.makeText(this, "${e.message}", Toast.LENGTH_SHORT).show()
                        }


                }
                
            }
            .addOnProgressListener {taskSnapshot ->
                val progess: Double =
                    100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount()
                progressDialog.setMessage("Uploaded " + progess.toInt() + "%...")
            }
            .addOnFailureListener {e ->
                //upload Failed
                Log.d("uploadVideoFirebase", "err :  storage ")
                progressDialog.dismiss()
                Toast.makeText(this, "${e.message}", Toast.LENGTH_SHORT).show()
            }

    }

    private fun videoPickDialog() {
        // option to display in dialog
        val option = arrayOf("Camera" , "Gallery")

        //alert dialog
        val builder = AlertDialog.Builder(this)
        builder.setTitle("pick video from")
            .setItems(option){dialogInterface , i->
                if(i == 0){
                    //camera selected
                    if(!checkCameraPermissions()){
                        // if camera permission dosent granted
                        //ask user to give permission
                        requestCameraPermissions()
                    }
                    else{
                        //camera permission allowed
                        videoPickCamera()
                    }

                }else{
                    // gallery selected
                    videoPickGallery()

                }
            }
            .show()

    }

    private fun requestCameraPermissions() {
        //request camera permission
        ActivityCompat.requestPermissions(
            this,
            cameraPermissions,
            CAMERA_REQUEST_CODE
        )
    }

    private fun checkCameraPermissions(): Boolean {
        //chick permissions camera and storage granted or not
        val cameraPermissions = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        val storageWritePermissions = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED

        return cameraPermissions && storageWritePermissions
    }

    private fun videoPickGallery(){
        // pick video from gallery or create intent for selected video from gallery
        val intent = Intent()
        intent.type = "video/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent,"Choose Video"),
        VIDEO_PICK_GALLERY_CODE)
    }

    private fun videoPickCamera(){
        val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        startActivityForResult(intent , VIDEO_PICK_CAMERA_CODE)
    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed() // go to previous activity
        return super.onSupportNavigateUp()
    }

    //handle permissions result
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode){
            CAMERA_REQUEST_CODE ->
            {
                if(grantResults.size > 0){
                    //check if permissions allowed or not
                    val cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
                    val storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED
                    if(cameraAccepted && storageAccepted){
                        videoPickCamera()
                    }else{
                        //permissions denied
                        Toast.makeText(this, "permissions denied", Toast.LENGTH_SHORT).show()
                    }
                }
            }

        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


    //handle video pick result
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if(resultCode == Activity.RESULT_OK){
            if(requestCode == VIDEO_PICK_CAMERA_CODE){
                videoUri = data!!.data
                previewSelectedVideo()
            }else if(requestCode == VIDEO_PICK_GALLERY_CODE){
                videoUri =data!!.data
                previewSelectedVideo()
            }

        }else{
            Toast.makeText(this, "Cancelled...", Toast.LENGTH_SHORT).show()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun previewSelectedVideo() {

        val mediaController = MediaController(this)
        mediaController.setAnchorView(video_view)

        video_view.setMediaController(mediaController)

        video_view.setVideoURI(videoUri)
        video_view.requestFocus()
        video_view.setOnPreparedListener {
            //make video not palyed when selected
            video_view.pause()
        }
    }
}
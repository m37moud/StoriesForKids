package com.m37moud.responsivestories


import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_drawer.*
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.app_bar_main.*

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var appBarConfiguration: AppBarConfiguration

//    private val aboutViewModel: AboutViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drawer)
//        setContentView(R.layout.activity_home)
        setSupportActionBar(toolBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolBar.setTitleTextColor(ContextCompat.getColor(this, R.color.white))

        drawerLayout = findViewById(R.id.drawer_layout)

//        navOptions.findNavController(R.id.homeHostFragment).navigate(R.id.action_splashFragment_to_homeActivity,null,navOptions)
//        navController.popBackStack(R.id.homeHostFragment, true)
        navController = findNavController(R.id.homeHostFragment)


//
//        appBarConfiguration = AppBarConfiguration(
//            navController.graph
//            , drawerLayout
//        )


//         appBarConfiguration = AppBarConfiguration(
//            setOf(
//                R.id.storyFragment,
//                R.id.learnFragment
//            )
//        )
//
         appBarConfiguration = AppBarConfiguration.Builder(R.id.storyFragment, R.id.learnFragment)
             .setOpenableLayout(drawerLayout)
            .build()

        //to show fragment activity in nav host fragment
        bottomNavigationView.setupWithNavController(navController)

        nav_view.setupWithNavController(navController)
        nav_view.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_youtube -> {

                    // youtube activity
                   getOpenYoutubeIntent()
                    //write your implementation here

//                    aboutViewModel.contact(applicationContext)
                    if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
                        drawer_layout.closeDrawer(GravityCompat.START)
                    }
                    true
                }
                R.id.nav_fb -> {
                    //write your implementation here
                    //facebook activity start
                    getOpenFacebookIntent()

                    if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
                        drawer_layout.closeDrawer(GravityCompat.START)
                    }
                    true
                }
                R.id.nav_email -> {

                    getOpenMailIntent()

                    if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
                        drawer_layout.closeDrawer(GravityCompat.START)
                    }
                    true
                }
                else -> {
                    if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
                        drawer_layout.closeDrawer(GravityCompat.START)
                    }
                    false
                }
            }

        }
//

//to show details for bottom nav view  in app bar
        setupActionBarWithNavController(navController, appBarConfiguration)


    }

    override fun onStart() {
        super.onStart()
        showAds()
    }

//
//    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        menuInflater.inflate(R.menu.drawer, menu)
//        return true
//    }

    override fun onSupportNavigateUp(): Boolean {

//        val navDrawerController = findNavController(R.id.homeHostFragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }


   private fun getOpenFacebookIntent(): Intent? = try {
//        context.getPackageManager().getPackageInfo("com.facebook.katana", 0)
        Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/536320790653318")).apply {
            setPackage("com.facebook.katana")
        }.also { readyIntent ->
            startActivity(readyIntent)
        }
    } catch (e: Exception) {
        Intent(
            Intent.ACTION_VIEW,
            Uri.parse("https://www.facebook.com/536320790653318")
        )
    }

    private fun getOpenYoutubeIntent(): Intent? = try {


        Intent(
            Intent.ACTION_VIEW,
            Uri.parse("https://www.youtube.com/channel/UC7pejtgsjgdPODeWGgXLFuQ?sub_confirmation=1")
        ).apply {
            setPackage("com.google.android.youtube")
        }.also { readyIntent ->
            startActivity(readyIntent)
        }
    } catch (e: Exception) {
        Intent(
            Intent.ACTION_VIEW,
            Uri.parse("https://www.youtube.com/channel/UC7pejtgsjgdPODeWGgXLFuQ")
        )
    }

    private fun getOpenMailIntent(): Intent? = try {

        Intent(Intent.ACTION_SENDTO ).apply {
            type = "text/plain"
//            type = "message/rfc822"
            data = Uri.parse("mailto:m37moud00@gmail.com")
            putExtra(Intent.EXTRA_TEXT, "that is a great app ")
            putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name).plus(" App "))
        }.also { readyIntent ->
            startActivity(Intent.createChooser(readyIntent,"Send feedback"))
        }

    } catch (e: Exception) {
        Log.e("TAG", "getOpenGMailIntent: ", e)
        Intent(
            Intent.ACTION_SEND,
            Uri.parse("https://www.youtube.com/channel/UC7pejtgsjgdPODeWGgXLFuQ?sub_confirmation=1")
        )
    }

    private fun showAds(){

        val adRequest = AdRequest.Builder()
            .build()
        ad_view.loadAd(adRequest)
        ad_view.adListener = object: AdListener() {
            override fun onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                ad_view.visibility = View.VISIBLE
            }

            override fun onAdFailedToLoad(adError : LoadAdError) {
                // Code to be executed when an ad request fails.
                ad_view.visibility = View.GONE

            }

        }

    }


    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
//            setFullScreen()
            supportActionBar?.hide()

        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            supportActionBar?.show()

        }
    }
    public override fun onPause() {
        ad_view.pause()
        ad_view.visibility = View.GONE
        super.onPause()
    }

    // Called when returning to the activity
    public override fun onResume() {
        ad_view.resume()
        ad_view.visibility = View.VISIBLE
        super.onResume()

    }

    // Called before the activity is destroyed
    public override fun onDestroy() {
        ad_view.destroy()
        ad_view.visibility = View.GONE
        super.onDestroy()
    }

//    override fun onBackPressed() {
//
//        val navigationController = navHostFragment.findNavController()
//        if (navigationController.currentDestination?.id == R.id.homeActivity) {
//            finish()
//
//        } else {
//            super.onBackPressed()
//        }
//
//    }

}
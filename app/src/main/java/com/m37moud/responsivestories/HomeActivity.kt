package com.m37moud.responsivestories


import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.m37moud.responsivestories.viewmodel.AboutViewModel
import com.m37moud.responsivestories.viewmodel.VideosViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_drawer.*
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.app_bar_main.*

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var appBarConfiguration: AppBarConfiguration


    private val aboutViewModel: AboutViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drawer)
        setSupportActionBar(toolBar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolBar.setTitleTextColor(ContextCompat.getColor(this, R.color.white))

        drawerLayout = findViewById(R.id.drawer_layout)

        navController = findNavController(R.id.homeHostFragment)


        appBarConfiguration = AppBarConfiguration(
            navController.graph
            , drawerLayout
        )


//        val appBarConfigurationToBottom = AppBarConfiguration(
//            setOf(
//                R.id.storyFragment,
//                R.id.learnFragment
//            )
//        )

        //to show fragment activity in nav host fragment
        bottomNavigationView.setupWithNavController(navController)

        nav_view.setupWithNavController(navController)
        nav_view.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_youtube -> {


                    // youtube activity
                    startActivity(getOpenYoutubeIntent(applicationContext))
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
                    startActivity(getOpenFacebookIntent(applicationContext))

                    if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
                        drawer_layout.closeDrawer(GravityCompat.START)
                    }
                    true
                }
                R.id.nav_email -> {

                    getOpenGmailIntent(applicationContext)

//                    startActivity(getOpenGmailIntent(applicationContext))

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


//        setupActionBarWithNavController(navController, appBarConfigurationToBottom)
//to show details for bottom nav view  in app bar
        setupActionBarWithNavController(navController, appBarConfiguration)


    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.drawer, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {

        val navDrawerController = findNavController(R.id.homeHostFragment)
        return navDrawerController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }


    fun getOpenFacebookIntent(context: Context): Intent? = try {
        context.getPackageManager().getPackageInfo("com.facebook.katana", 0)
        Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/536320790653318"))
    } catch (e: Exception) {
        Intent(
            Intent.ACTION_VIEW,
            Uri.parse("https://www.facebook.com/536320790653318")
        )
    }

    fun getOpenYoutubeIntent(context: Context): Intent? = try {

        context.getPackageManager().getPackageInfo("com.google.android.youtube", 0)
        Intent(
            Intent.ACTION_VIEW,
            Uri.parse("https://www.youtube.com/channel/UC7pejtgsjgdPODeWGgXLFuQ?sub_confirmation=1")
        )
    } catch (e: Exception) {
        Intent(
            Intent.ACTION_VIEW,
            Uri.parse("https://www.youtube.com/channel/UC7pejtgsjgdPODeWGgXLFuQ?sub_confirmation=1")
        )
    }

    fun getOpenGmailIntent(context: Context): Intent? = try {

        Intent(Intent.ACTION_SEND).apply {
            setPackage("com.google.android.gm")
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, "Go, go share text!")
        }.also { readyIntent ->
            startActivity(readyIntent)
        }

    } catch (e: Exception) {
        Intent(
            Intent.ACTION_VIEW,
            Uri.parse("https://www.youtube.com/channel/UC7pejtgsjgdPODeWGgXLFuQ?sub_confirmation=1")
        )
    }
}
/**
 * recipe app (ResponsiveStory قصص اطفال)
 * @author  Mahmoud Aly
 * @version 1.0
 * @since   2021-1-25
 */

package com.m37moud.responsivestories.viewmodel

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.view.View
import androidx.lifecycle.ViewModel
import com.m37moud.responsivestories.R


class AboutViewModel() : ViewModel() {
    var version = "1.0"
    //get the app version
    fun appVersion(a: Activity) {



        val pm = a.packageManager
        var pi: PackageInfo? = null
        try {
            pi = pm.getPackageInfo(a.packageName, 0)
            version = pi.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
    }

    //share the app
    fun share(view: View) {
        val context = view.context
        val play_url = "http://play.google.com/store/apps/details?id=" + context.packageName
        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, context.resources.getString(R.string.app_name))
        sendIntent.putExtra(Intent.EXTRA_TEXT, play_url)
        sendIntent.type = "text/plain"
        context.startActivity(
            Intent.createChooser(
                sendIntent,
                context.resources.getString(R.string.app_name)
            )
        )
    }

    //opens google play to rate the app
    fun rate(view: View) {
        val context = view.context
        val uri = Uri.parse("FBD://details?id=" + context.packageName)
        val goToMarket: Intent = Intent(Intent.ACTION_VIEW, uri)
        goToMarket.addFlags(
            Intent.FLAG_ACTIVITY_NO_HISTORY or
                    Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK
        )
        try {
            context.startActivity(goToMarket)
        } catch (e: ActivityNotFoundException) {
            context.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + context.packageName)
                )
            )
        }
    }

    //open contact link in the browser
    fun contact(view: View) {
        val i = Intent(Intent.ACTION_VIEW)
        val d = Uri.parse("http://www.facebook.com/engma7moud3ly")
        i.data = d
        view.context.startActivity(i)
    }
}
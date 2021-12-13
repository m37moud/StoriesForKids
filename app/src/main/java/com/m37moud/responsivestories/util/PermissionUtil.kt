package com.m37moud.responsivestories.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.core.app.ActivityCompat
import androidx.core.content.PermissionChecker
import com.m37moud.responsivestories.R

object  PermissionUtil {

    fun request(
        activity: Activity,
        message: String?,
        permissions: List<String>,
        requestCode: Int = 1
    ) {
        if (!checkPermissionsAllGrant(activity, permissions)) {
            if (message.isNullOrBlank()) {
                ActivityCompat.requestPermissions(activity, permissions.toTypedArray(), requestCode)
            } else {
                DialogUtil.show(
                    activity,
                    activity.getString(R.string.need_permission),
                    message,
                    { _, _ ->
                        ActivityCompat.requestPermissions(
                            activity,
                            permissions.toTypedArray(),
                            requestCode
                        )
                    }
                )
            }
        }
    }

    fun requestReadNotification(activity: Activity) {
        val intent = Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")
        activity.startActivity(intent)
    }

    fun checkPermissionsAllGrant(context: Context, permissions: List<String>): Boolean {
        var isAllGrant = true
        permissions.forEach {
            if (PermissionChecker.checkSelfPermission(
                    context,
                    it
                ) == PermissionChecker.PERMISSION_DENIED
            ) {
                isAllGrant = false
                return@forEach
            }
        }
        return isAllGrant
    }
}
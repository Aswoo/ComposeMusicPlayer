package com.sdu.composemusicplayer.utils

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.Context

object AppStateUtil {
    fun isAppInForeground(context: Context): Boolean {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val appProcesses = activityManager.runningAppProcesses ?: return false
        val packageName = context.packageName

        return appProcesses.any { appProcess -> 
            appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND 
            && appProcess.processName == packageName 
        }
    }
}

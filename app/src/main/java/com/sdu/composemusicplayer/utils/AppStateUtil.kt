package com.sdu.composemusicplayer.utils

import android.app.ActivityManager
import android.content.Context
import android.content.res.Resources

object AppStateUtil {
    fun isAppInForeground(context: Context): Boolean {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val appProcesses = activityManager.runningAppProcesses ?: return false
        val packageName = context.packageName

        return appProcesses.any { appProcess ->
            appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND &&
                appProcess.processName == packageName
        }
    }

    /** 시스템 네비게이션 바 높이를 dp로 반환 */
    fun getNavigationBarHeightDp(resources: Resources): Float {
        val resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
        return if (resourceId > 0) {
            val px = resources.getDimensionPixelSize(resourceId)
            px / resources.displayMetrics.density
        } else {
            0f
        }
    }
}

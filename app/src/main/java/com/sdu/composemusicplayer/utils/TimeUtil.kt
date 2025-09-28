@file:Suppress("ImplicitDefaultLocale")

package com.sdu.composemusicplayer.utils

private const val MILLIS_IN_SECOND = 1000
private const val SECONDS_IN_MINUTE = 60
private const val SECONDS_IN_HOUR = 3600

fun Long.millisToTime(): String {
    val seconds = this / MILLIS_IN_SECOND
    val hours = seconds / SECONDS_IN_HOUR
    val minutes = (seconds % SECONDS_IN_HOUR) / SECONDS_IN_MINUTE
    val remainingSeconds = seconds % SECONDS_IN_MINUTE

    var result = ""
    if (hours.toInt() != 0) result += String.format("%02d:", hours)
    result += String.format("%02d:", minutes)
    result += String.format("%02d", remainingSeconds)

    return result
}

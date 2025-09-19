package com.sdu.composemusicplayer.core.constants

object AppConstants {
    // Media related constants
    const val MIN_TRACK_SIZE_KB = 100
    const val MIN_TRACK_DURATION_SECONDS = 60
    
    // UI related constants
    const val DEFAULT_ALBUM_ART_SIZE = 200
    const val DEFAULT_ICON_SIZE = 24
    const val DEFAULT_PADDING = 16
    const val DEFAULT_ELEVATION = 4
    const val DEFAULT_CORNER_RADIUS = 8
    
    // Animation related constants
    const val DEFAULT_ANIMATION_DURATION = 300
    const val FAST_ANIMATION_DURATION = 150
    const val SLOW_ANIMATION_DURATION = 500
    
    // Lyrics related constants
    const val LYRICS_UPDATE_INTERVAL_MS = 1000L
    const val LYRICS_SYNC_THRESHOLD_MS = 500L
    const val LYRICS_CACHE_DURATION_MS = 300000L // 5 minutes
    
    // Playlist related constants
    const val DEFAULT_PLAYLIST_ID = 0
    const val MAX_PLAYLIST_NAME_LENGTH = 50
    
    // Music player related constants
    const val DEFAULT_VOLUME = 1.0f
    const val DEFAULT_PLAYBACK_SPEED = 1.0f
    const val SEEK_THRESHOLD_MS = 1000L
    
    // UI size constants
    const val DEFAULT_ICON_SIZE_DP = 24
    const val DEFAULT_PADDING_DP = 16
    const val DEFAULT_ELEVATION_DP = 4
    const val DEFAULT_CORNER_RADIUS_DP = 8
    const val DEFAULT_ALBUM_ART_SIZE_DP = 200
    const val DEFAULT_BUTTON_SIZE_DP = 48
    const val DEFAULT_SLIDER_THUMB_SIZE_DP = 20
    const val DEFAULT_SLIDER_TRACK_HEIGHT_DP = 4
    
    // Animation and timing constants
    const val DEFAULT_ANIMATION_DURATION_MS = 300L
    const val FAST_ANIMATION_DURATION_MS = 150L
    const val SLOW_ANIMATION_DURATION_MS = 500L
    
    // Scroll and interaction constants
    const val SCROLL_THRESHOLD_RATIO = 0.8f
    const val DEFAULT_ALPHA = 0.8f
    const val FADE_ALPHA = 0.3f
    
    // API level constants
    const val API_LEVEL_29 = 29
    const val API_LEVEL_30 = 30
    
    // Color constants
    const val SELECTED_BACKGROUND_COLOR = 0xFF2A2A2A
}

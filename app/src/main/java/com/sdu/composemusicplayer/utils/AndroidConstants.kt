package com.sdu.composemusicplayer.utils

/**
 * Android 프로젝트에서 자주 사용되는 상수들을 정의하는 객체
 * MagicNumber 규칙을 피하기 위해 의미있는 상수로 선언
 */
object AndroidConstants {
    
    // ===== DP (Density-independent Pixels) 값들 =====
    object Dp {
        const val ZERO = 0
        const val TINY = 2
        const val SMALL = 4
        const val MEDIUM = 8
        const val LARGE = 16
        const val XLARGE = 24
        const val XXLARGE = 32
        const val HUGE = 48
        const val MASSIVE = 64
    }
    
    // ===== 시간 관련 상수들 =====
    object Time {
        const val MILLIS_IN_SECOND = 1000
        const val SECONDS_IN_MINUTE = 60
        const val MINUTES_IN_HOUR = 60
        const val HOURS_IN_DAY = 24
        
        // 타임아웃 값들
        const val CONNECT_TIMEOUT_SECONDS = 30
        const val READ_TIMEOUT_SECONDS = 30
        const val WRITE_TIMEOUT_SECONDS = 30
        
        // 업데이트 간격
        const val DURATION_UPDATE_INTERVAL_MS = 1000L
    }
    
    // ===== 색상 관련 상수들 =====
    object Color {
        // 색상 최대값
        const val MAX_ALPHA = 255
        const val MAX_RGB = 255
        
        // 투명도 값들
        const val ALPHA_TRANSPARENT = 0
        const val ALPHA_SEMI_TRANSPARENT = 128
        const val ALPHA_OPAQUE = 255
        
        // 색상 값들 (16진수)
        const val SPOTIFY_GREEN = 0xFF1DB954
        const val SPOTIFY_BLACK = 0xFF121212
        const val SPOTIFY_WHITE = 0xFFF5F5F5
        const val SPOTIFY_DARK_GRAY = 0xFF282828
        const val SPOTIFY_GRAY = 0xFFB3B3B3
        const val SPOTIFY_DIVIDER = 0xFF404040
        const val SPOTIFY_LIGHT_GRAY = 0xFFB3B3B3
        const val LIGHT_RED = 0xFFED4956
        const val TEXT_DEFAULT_LIGHT = 0xFF4B4B4B
        const val TEXT_DEFAULT_DARK = 0xFFEEEEEE
    }
    
    // ===== 데이터베이스 관련 상수들 =====
    object Database {
        const val VERSION = 4
        const val NAME = "music_db_0903"
    }
    
    // ===== 미디어 관련 상수들 =====
    object Media {
        // 최소 트랙 크기 (KB)
        const val MIN_TRACK_SIZE_KB = 100
        // 최소 트랙 길이 (초)
        const val MIN_TRACK_DURATION_SECONDS = 60
        // 바이트 단위 변환
        const val BYTES_IN_KB = 1024
    }
    
    // ===== UI 관련 상수들 =====
    object UI {
        // 기본 패딩/마진 값들
        const val DEFAULT_PADDING = 16
        const val SMALL_PADDING = 8
        const val LARGE_PADDING = 24
        
        // 기본 크기 값들
        const val DEFAULT_ICON_SIZE = 24
        const val LARGE_ICON_SIZE = 48
        const val SMALL_ICON_SIZE = 16
        
        // 기본 높이 값들
        const val DEFAULT_BUTTON_HEIGHT = 48
        const val DEFAULT_LIST_ITEM_HEIGHT = 56
        const val DEFAULT_APP_BAR_HEIGHT = 56
    }
    
    // ===== 네트워크 관련 상수들 =====
    object Network {
        const val CONNECT_TIMEOUT_SECONDS = 30
        const val READ_TIMEOUT_SECONDS = 30
        const val WRITE_TIMEOUT_SECONDS = 30
    }
    
    // ===== 기타 상수들 =====
    object Misc {
        // 기본값들
        const val DEFAULT_ID = -1L
        const val DEFAULT_DURATION = 0L
        
        // 퍼센트
        const val HUNDRED_PERCENT = 100
        
        // 기본 인덱스
        const val DEFAULT_INDEX = 0
        const val INVALID_INDEX = -1
        
        // Flow 구독 유지 시간 (초)
        const val SUBSCRIPTION_TIMEOUT_SECONDS = 5
        
        // Shape 관련 상수
        const val SHAPE_THICKNESS_DIVISOR = 2.1f
        const val SHAPE_OFFSET_Y = 3f
    }
}

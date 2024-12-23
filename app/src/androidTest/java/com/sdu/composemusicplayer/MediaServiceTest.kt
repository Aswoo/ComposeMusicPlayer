import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import com.sdu.composemusicplayer.mediaPlayer.service.MediaService
import io.mockk.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class MediaServiceTest {
    private lateinit var exoPlayer: ExoPlayer
    private lateinit var mediaSession: MediaSession
    private lateinit var mediaService: MediaService

    @Before
    fun setUp() {
        // Mocking ExoPlayer 및 MediaSession
        exoPlayer = mockk(relaxed = true) // relaxed=true로 설정하여 필요없는 부분은 자동으로 처리
        mediaSession = mockk(relaxed = true)

        // MediaService 인스턴스 생성 시 의존성 주입
        mediaService =
            MediaService().apply {
                this.exoPlayer = exoPlayer // Hilt 없이 직접 주입
                this.mediaSession = mediaSession
            }
    }

    @Test
    fun testOnCreate() {
        // onCreate 동작 테스트
        mediaService.onCreate()

        // mediaSession이 null이 아닌지 확인
        assertNotNull(mediaService.mediaSession)

        // ExoPlayer와 MediaSession이 초기화되었는지 추가 검증
        verify { mediaSession.release() wasNot Called }
    }

    @Test
    fun testOnDestroy() {
        // onDestroy 동작 테스트
        mediaService.onDestroy()

        // ExoPlayer와 MediaSession이 해제되었는지 확인
        verify { exoPlayer.release() }
        verify { mediaSession.release() }
    }
}

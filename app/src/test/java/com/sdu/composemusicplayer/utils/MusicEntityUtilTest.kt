import android.net.Uri
import io.mockk.*
import org.junit.After
import org.junit.Before

class MusicEntityUtilTest {
    @Before
    fun setUp() {
        // Uri.parse 메소드를 정적으로 모킹
        mockkStatic(Uri::class)
    }

    @After
    fun tearDown() {
        // 정적 메소드 모킹 해제
        unmockkStatic(Uri::class)
    }
}

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import com.sdu.composemusicplayer.utils.MusicUtil
import io.mockk.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class MusicEntityUtilTest {
    @Before
    fun setUp() {
        // Uri.parse 메소드를 정적으로 모킹
        mockkStatic(Uri::class)
    }

    @Test
    fun testFetchMusicFromDevice_withSkipConditions() {
        // Mock context and content resolver
        val context = mockk<Context>()
        val contentResolver = mockk<ContentResolver>()
        val cursor = mockk<Cursor>()

        // Setup the context to return a mock content resolver
        every { context.contentResolver } returns contentResolver

        // Setup query to return a mock cursor
        every { contentResolver.query(any(), any(), any(), any(), any()) } returns cursor

        // Mock cursor behavior
        every { cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID) } returns 0
        every { cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE) } returns 1
        every { cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST) } returns 2
        every { cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION) } returns 3
        every { cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID) } returns 4
        every { cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE) } returns 5

        every { cursor.moveToNext() } returns true andThen false // Move once and then stop
        every { cursor.getLong(0) } returns 1L
        every { cursor.getString(1) } returns "Song Title"
        every { cursor.getString(2) } returns "Artist Name"
        every { cursor.getLong(3) } returns 120_000L // 120 seconds
        every { cursor.getString(4) } returns "albumId"
        every { cursor.getInt(5) } returns 150_000 // 150 KB

        // Mock Uri.parse to return a fake URI
        val fakeUri = Uri.parse("content://media/external/audio/media/1")
        every { Uri.parse(any()) } returns fakeUri

        // Execute the function to test
        val result = MusicUtil.fetchMusicFromDevice(context)

        // Verify the result
        assertEquals(1, result.size) // There should be 1 music entity
        assertEquals("Song Title", result[0].title) // Check the song title
        assertEquals("Artist Name", result[0].artist) // Check the artist

        // Verify Uri.parse was called
        verify { Uri.parse("content://media/external/audio/media/1") }

        // Verify that the cursor's query method was invoked
        verify { contentResolver.query(any(), any(), any(), any(), any()) }

        // Confirm that all mocks were verified
        confirmVerified(context, contentResolver, cursor)
    }

    @After
    fun tearDown() {
        // 정적 메소드 모킹 해제
        unmockkStatic(Uri::class)
    }
}

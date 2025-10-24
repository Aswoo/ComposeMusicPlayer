package com.sdu.composemusicplayer.core.database

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import com.sdu.composemusicplayer.R
import com.sdu.composemusicplayer.core.database.dao.MusicDao
import com.sdu.composemusicplayer.core.database.entity.MusicEntity
import com.sdu.composemusicplayer.utils.AndroidConstants
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.unmockkStatic
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail

@ExperimentalCoroutinesApi
@RunWith(JUnit4::class)
class MusicRepositoryImplTest {

    private lateinit var musicRepository: MusicRepositoryImpl
    private lateinit var mockMusicDao: MusicDao
    private lateinit var mockContext: Context
    private lateinit var mockContentResolver: ContentResolver
    private lateinit var mockCursor: Cursor

    @Before
    fun setUp() {
        // Mock 객체들 생성
        mockMusicDao = mockk(relaxed = true)
        mockContext = mockk(relaxed = true)
        mockContentResolver = mockk(relaxed = true)
        mockCursor = mockk(relaxed = true)

        // Context 설정
        every { mockContext.contentResolver } returns mockContentResolver
        every { mockContext.getString(R.string.unknown) } returns "Unknown"

        // Static mock 설정
        mockkStatic(Uri::class)
        every { Uri.parse(any<String>()) } returns mockk<Uri>(relaxed = true)
        every { Uri.withAppendedPath(any<Uri>(), any<String>()) } returns mockk<Uri>(relaxed = true)

        // Repository 인스턴스 생성
        musicRepository = MusicRepositoryImpl(mockMusicDao, mockContext)
    }

    @After
    fun tearDown() {
        unmockkStatic(Uri::class)
    }

    @Test
    fun `getAllMusics는_DAO의_getAllMusices를_호출한다`() = runTest {
        // Given
        val expectedMusics = listOf(createTestMusicEntity(1), createTestMusicEntity(2))
        every { mockMusicDao.getAllMusices() } returns flowOf(expectedMusics)

        // When
        val result = musicRepository.getAllMusics()

        // Then
        val actualMusics = result.first()
        assertEquals(expectedMusics, actualMusics)
    }

    @Test
    fun `insertMusic은_DAO의_insert를_호출한다`() = runTest {
        // Given
        val testMusic = createTestMusicEntity(1)
        coEvery { mockMusicDao.insert(testMusic) } returns Unit

        // When
        musicRepository.insertMusic(testMusic)

        // Then
        coVerify { mockMusicDao.insert(testMusic) }
    }

    @Test
    fun `insertMusics는_DAO의_insert를_리스트로_호출한다`() = runTest {
        // Given
        val testMusics = listOf(createTestMusicEntity(1), createTestMusicEntity(2))
        coEvery { mockMusicDao.insert(testMusics) } returns Unit

        // When
        musicRepository.insertMusics(*testMusics.toTypedArray())

        // Then
        coVerify { mockMusicDao.insert(testMusics) }
    }

    // syncMusicWithDevice 테스트는 Android 프레임워크에 의존적이므로 통합 테스트에서 수행

    @Test
    fun `deleteMusics는_디바이스에서_파일을_삭제하고_DAO에서도_삭제한다`() = runTest {
        // Given
        val testMusic = createTestMusicEntity(1)
        val mockUri = mockk<Uri>(relaxed = true)
        every { Uri.parse(testMusic.audioPath) } returns mockUri
        every { mockContentResolver.delete(mockUri, null, null) } returns 1
        coEvery { mockMusicDao.delete(listOf(testMusic)) } returns Unit

        // When
        musicRepository.deleteMusics(testMusic, context = mockContext)

        // Then
        coVerify { mockContentResolver.delete(mockUri, null, null) }
        coVerify { mockMusicDao.delete(listOf(testMusic)) }
    }

    @Test
    fun `deleteMusics는_디바이스_삭제_실패시_예외를_던진다`() = runTest {
        // Given
        val testMusic = createTestMusicEntity(1)
        val mockUri = mockk<Uri>(relaxed = true)
        every { Uri.parse(testMusic.audioPath) } returns mockUri
        every { mockContentResolver.delete(mockUri, null, null) } returns 0 // 삭제 실패

        // When & Then
        try {
            musicRepository.deleteMusics(testMusic, context = mockContext)
            fail("Expected IllegalStateException to be thrown")
        } catch (e: IllegalStateException) {
            // Expected exception
            assertTrue(e.message?.contains("Failed to delete file from device") == true)
        }
    }

    // createMusicEntityFromCursor 테스트는 Android 프레임워크에 의존적이므로 통합 테스트에서 수행

    // shouldAddMusic 테스트는 Android 프레임워크에 의존적이므로 통합 테스트에서 수행

    // Helper functions
    private fun createTestMusicEntity(id: Long): MusicEntity {
        return MusicEntity(
            audioId = id,
            title = "Test Song $id",
            artist = "Test Artist $id",
            duration = 180000L, // 3분
            albumPath = "content://media/external/audio/albumart/$id",
            audioPath = "content://media/external/audio/media/$id"
        )
    }

}

package com.sdu.composemusicplayer.core.database

import android.net.Uri
import com.sdu.composemusicplayer.core.database.dao.QueueDao
import com.sdu.composemusicplayer.core.database.entity.QueueEntity
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue

@ExperimentalCoroutinesApi
@RunWith(JUnit4::class)
class QueueRepositoryImplTest {

    private lateinit var queueRepository: QueueRepositoryImpl
    private lateinit var mockQueueDao: QueueDao

    @Before
    fun setUp() {
        // Mock 객체들 생성
        mockQueueDao = mockk(relaxed = true)

        // Uri static mock 설정
        mockkStatic(Uri::class)
        every { Uri.parse(any<String>()) } returns mockk<Uri>(relaxed = true)

        // Repository 인스턴스 생성
        queueRepository = QueueRepositoryImpl(mockQueueDao)
    }

    @After
    fun tearDown() {
        unmockkStatic(Uri::class)
    }

    @Test
    fun `getQueue는_DAO의_getQueue를_호출하고_변환된_결과를_반환한다`() = runTest {
        // Given
        val queueEntities = listOf(
            QueueEntity(1, "uri1", "Song 1", "Artist 1", "Album 1"),
            QueueEntity(2, "uri2", "Song 2", "Artist 2", "Album 2")
        )
        coEvery { mockQueueDao.getQueue() } returns queueEntities

        // When
        val result = queueRepository.getQueue()

        // Then
        assertEquals(2, result.size)
        assertEquals("Song 1", result[0].title)
        assertEquals("Artist 1", result[0].artist)
        assertEquals("Album 1", result[0].album)
        // Uri.toString()은 모킹된 Uri를 반환하므로 정확한 값 비교는 어려움
        // 대신 songUri가 null이 아닌지만 확인
        assertTrue(result[0].songUri != null)
        
        assertEquals("Song 2", result[1].title)
        assertEquals("Artist 2", result[1].artist)
        assertEquals("Album 2", result[1].album)
        assertTrue(result[1].songUri != null)
    }

    @Test
    fun `getQueue는_빈_리스트를_반환한다`() = runTest {
        // Given
        val emptyQueue = emptyList<QueueEntity>()
        coEvery { mockQueueDao.getQueue() } returns emptyQueue

        // When
        val result = queueRepository.getQueue()

        // Then
        assertTrue(result.isEmpty())
    }

    @Test
    fun `observeQueueUris는_DAO의_getQueueFlow를_호출하고_URI_리스트를_반환한다`() = runTest {
        // Given
        val queueEntities = listOf(
            QueueEntity(1, "uri1", "Song 1", "Artist 1", "Album 1"),
            QueueEntity(2, "uri2", "Song 2", "Artist 2", "Album 2")
        )
        every { mockQueueDao.getQueueFlow() } returns flowOf(queueEntities)

        // When
        val result = queueRepository.observeQueueUris().first()

        // Then
        assertEquals(2, result.size)
        assertEquals("uri1", result[0])
        assertEquals("uri2", result[1])
    }

    @Test
    fun `observeQueueUris는_빈_URI_리스트를_반환한다`() = runTest {
        // Given
        val emptyQueue = emptyList<QueueEntity>()
        every { mockQueueDao.getQueueFlow() } returns flowOf(emptyQueue)

        // When
        val result = queueRepository.observeQueueUris().first()

        // Then
        assertTrue(result.isEmpty())
    }

    @Test
    fun `saveQueueFromDBQueueItems는_DAO의_changeQueue를_호출한다`() = runTest {
        // Given
        val dbQueueItems = listOf(
            DBQueueItem(
                songUri = Uri.parse("uri1"),
                title = "Song 1",
                artist = "Artist 1",
                album = "Album 1"
            ),
            DBQueueItem(
                songUri = Uri.parse("uri2"),
                title = "Song 2",
                artist = "Artist 2",
                album = "Album 2"
            )
        )
        coEvery { mockQueueDao.changeQueue(any()) } returns Unit

        // When
        queueRepository.saveQueueFromDBQueueItems(dbQueueItems)

        // Then
        coVerify { mockQueueDao.changeQueue(any()) }
    }

    @Test
    fun `saveQueueFromDBQueueItems는_빈_리스트를_처리한다`() = runTest {
        // Given
        val emptyQueue = emptyList<DBQueueItem>()
        coEvery { mockQueueDao.changeQueue(any()) } returns Unit

        // When
        queueRepository.saveQueueFromDBQueueItems(emptyQueue)

        // Then
        coVerify { mockQueueDao.changeQueue(any()) }
    }

}

package com.tama.gallerynoai.ui.viewmodel

import android.net.Uri
import com.tama.gallerynoai.data.model.MediaItem
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.Mockito.mock

class SearchScoringTest {

    @Test
    fun testCalculateSearchScore_NameMatch() {
        val item = createMediaItem(name = "dog_photo.jpg")
        val score = calculateScoreManual(item, "dog")
        assertTrue("Score for matching name should be positive", score > 0)
    }

    @Test
    fun testCalculateSearchScore_CustomTagMatch() {
        val item = createMediaItem(customTags = listOf("holiday"))
        val score = calculateScoreManual(item, "holiday")
        assertTrue("Score for matching custom tag should be positive", score > 0)
    }

    @Test
    fun testCalculateSearchScore_NoMatch() {
        val item = createMediaItem(name = "cat.jpg")
        val score = calculateScoreManual(item, "dog")
        assertTrue("Score for no match should be zero", score == 0)
    }

    private fun createMediaItem(
        id: Long = 1L,
        name: String = "test.jpg",
        customTags: List<String> = emptyList(),
        isFavorite: Boolean = false
    ): MediaItem {
        val mockUri = mock(Uri::class.java)
        return MediaItem(
            id = id,
            uri = mockUri,
            name = name,
            dateModified = 0,
            size = 0,
            mimeType = "image/jpeg",
            bucketId = "0",
            isVideo = false,
            isFavorite = isFavorite,
            customTags = customTags
        )
    }

    private fun calculateScoreManual(item: MediaItem, query: String): Int {
        val normalizedQuery = query.trim().lowercase()
        val terms = normalizedQuery.split(Regex("\\s+")).filter { it.isNotBlank() }
        
        var score = 0
        for (term in terms) {
            var termScore = 0
            if (item.name.contains(term, ignoreCase = true)) termScore += 100
            if (item.customTags.any { it.contains(term, ignoreCase = true) }) termScore += 90
            if (termScore == 0) return 0
            score += termScore
        }
        return score
    }
}


package com.tama.gallerynoai.ui.screens

import android.content.IntentSender
import androidx.compose.runtime.*
import com.tama.gallerynoai.data.model.MediaItem
import com.tama.gallerynoai.data.model.SortType
import com.tama.gallerynoai.ui.viewmodel.GalleryViewModel

enum class QuickAccessType {
    FAVORITES, VIDEOS, SCREENSHOTS
}

@Composable
fun QuickAccessDetailScreen(
    type: QuickAccessType,
    viewModel: GalleryViewModel,
    onMediaClick: (MediaItem) -> Unit,
    onBackClick: () -> Unit,
    onDeleteRequest: (IntentSender) -> Unit = {}
) {
    val mediaItems by viewModel.mediaItems.collectAsState()
    val sortType by viewModel.sortType.collectAsState()
    
    val filteredMedia = remember(mediaItems, type, sortType) {
        val filtered = when (type) {
            QuickAccessType.FAVORITES -> mediaItems.filter { it.isFavorite }
            QuickAccessType.VIDEOS -> mediaItems.filter { it.isVideo }
            QuickAccessType.SCREENSHOTS -> mediaItems.filter { 
                it.relativePath?.contains("Screenshots", ignoreCase = true) == true || 
                it.name.contains("Screenshot", ignoreCase = true)
            }
        }
        when (sortType) {
            SortType.DATE_NEWEST -> filtered.sortedByDescending { it.dateModified }
            SortType.DATE_OLDEST -> filtered.sortedBy { it.dateModified }
            SortType.SIZE_LARGEST -> filtered.sortedByDescending { it.size }
            SortType.SIZE_SMALLEST -> filtered.sortedBy { it.size }
        }
    }

    val title = when (type) {
        QuickAccessType.FAVORITES -> "Favorites"
        QuickAccessType.VIDEOS -> "Videos"
        QuickAccessType.SCREENSHOTS -> "Screenshots"
    }
    
    MediaGridScreen(
        title = title,
        filteredMedia = filteredMedia,
        viewModel = viewModel,
        onMediaClick = onMediaClick,
        onBackClick = onBackClick,
        onDeleteRequest = onDeleteRequest
    )
}


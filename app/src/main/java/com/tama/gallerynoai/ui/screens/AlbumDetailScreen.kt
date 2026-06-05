package com.tama.gallerynoai.ui.screens

import android.content.IntentSender
import androidx.compose.runtime.*
import com.tama.gallerynoai.data.model.MediaItem
import com.tama.gallerynoai.data.model.SortType
import com.tama.gallerynoai.ui.viewmodel.GalleryViewModel

@Composable
fun AlbumDetailScreen(
    albumId: String,
    albumName: String,
    viewModel: GalleryViewModel,
    onMediaClick: (MediaItem) -> Unit,
    onBackClick: () -> Unit,
    onSearchClick: () -> Unit,
    onDeleteRequest: (IntentSender) -> Unit = {}
) {
    val mediaItems by viewModel.mediaItems.collectAsState()
    val sortType by viewModel.sortType.collectAsState()
    
    val albumMedia = remember(mediaItems, albumId, sortType) {
        val filtered = mediaItems.filter { it.bucketId == albumId }
        when (sortType) {
            SortType.DATE_NEWEST -> filtered.sortedByDescending { it.dateModified }
            SortType.DATE_OLDEST -> filtered.sortedBy { it.dateModified }
            SortType.SIZE_LARGEST -> filtered.sortedByDescending { it.size }
            SortType.SIZE_SMALLEST -> filtered.sortedBy { it.size }
        }
    }
    
    MediaGridScreen(
        title = albumName,
        filteredMedia = albumMedia,
        viewModel = viewModel,
        onMediaClick = onMediaClick,
        onBackClick = onBackClick,
        onSearchClick = onSearchClick,
        onDeleteRequest = onDeleteRequest
    )
}


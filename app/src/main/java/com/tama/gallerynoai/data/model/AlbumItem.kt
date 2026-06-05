package com.tama.gallerynoai.data.model

import android.net.Uri

data class AlbumItem(
    val id: String,
    val name: String,
    val coverUri: Uri,
    val itemCount: Int,
    val totalSize: Long = 0L,
    val relativePath: String? = null
)


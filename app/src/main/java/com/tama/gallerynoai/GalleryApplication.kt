package com.tama.gallerynoai

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.decode.VideoFrameDecoder
import coil.disk.DiskCache
import coil.memory.MemoryCache

class GalleryApplication : Application(), ImageLoaderFactory {

    override fun newImageLoader(): ImageLoader {
        val diskCacheMb = getSharedPreferences("gallery_settings", MODE_PRIVATE)
            .getInt("disk_cache_mb", 512)

        return ImageLoader.Builder(this)
            .components {
                add(VideoFrameDecoder.Factory())
            }
            .memoryCache {
                MemoryCache.Builder(this)
                    .maxSizePercent(0.25) // Use 25% of available memory
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(this.cacheDir.resolve("image_cache"))
                    .maxSizeBytes(diskCacheMb.toLong() * 1024 * 1024)
                    .build()
            }
            .crossfade(100) // Shorter crossfade for snappier feel
            .respectCacheHeaders(false) // Local files don't change often
            .build()
    }
}


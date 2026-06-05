package com.tama.gallerynoai.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tama.gallerynoai.data.repository.MediaRepository
import com.tama.gallerynoai.data.settings.SettingsManager

class GalleryViewModelFactory(
    private val repository: MediaRepository,
    private val settingsManager: SettingsManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(GalleryViewModel::class.java) -> {
                GalleryViewModel(repository, settingsManager) as T
            }
            modelClass.isAssignableFrom(SettingsViewModel::class.java) -> {
                SettingsViewModel(settingsManager) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}


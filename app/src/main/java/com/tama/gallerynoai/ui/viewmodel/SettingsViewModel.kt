package com.tama.gallerynoai.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tama.gallerynoai.data.settings.AppThemeColor
import com.tama.gallerynoai.data.settings.FullscreenRotationMode
import com.tama.gallerynoai.data.settings.SettingsManager
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsManager: SettingsManager
) : ViewModel() {

    val useDefaultEditor: StateFlow<Boolean> = settingsManager.useDefaultEditor
    val defaultEditorPackage: StateFlow<String?> = settingsManager.defaultEditorPackage
    val useDefaultVideoEditor: StateFlow<Boolean> = settingsManager.useDefaultVideoEditor
    val autoPlayVideo: StateFlow<Boolean> = settingsManager.autoPlayVideo
    val defaultMuteVideo: StateFlow<Boolean> = settingsManager.defaultMuteVideo
    val defaultVideoEditorPackage: StateFlow<String?> = settingsManager.defaultVideoEditorPackage
    val themeMode: StateFlow<String> = settingsManager.themeMode
    val accentColor: StateFlow<Int> = settingsManager.accentColor
    val secondaryColor: StateFlow<Int> = settingsManager.secondaryColor
    val tertiaryColor: StateFlow<Int> = settingsManager.tertiaryColor
    val dateFormat: StateFlow<String> = settingsManager.dateFormat
    val searchAllFilesByDefault: StateFlow<Boolean> = settingsManager.searchAllFilesByDefault
    val fontFamily: StateFlow<String> = settingsManager.fontFamily
    val amoledMode: StateFlow<Boolean> = settingsManager.amoledMode
    val themeColor: StateFlow<AppThemeColor> = settingsManager.themeColor
    val gridColumns: StateFlow<Int> = settingsManager.gridColumns
    val fullscreenRotationMode: StateFlow<FullscreenRotationMode> = settingsManager.fullscreenRotationMode
    val diskCacheMb: StateFlow<Int> = settingsManager.diskCacheMb
    val showNavLabel: StateFlow<Boolean> = settingsManager.showNavLabel
    val defaultSort: StateFlow<String> = settingsManager.defaultSort
    val trashWarningEnabled: StateFlow<Boolean> = settingsManager.trashWarningEnabled

    private val _scrollToTopTrigger = MutableSharedFlow<String>(replay = 0)
    val scrollToTopTrigger: SharedFlow<String> = _scrollToTopTrigger.asSharedFlow()

    fun toggleUseDefaultEditor(enabled: Boolean) {
        viewModelScope.launch { settingsManager.setUseDefaultEditor(enabled) }
    }

    fun toggleUseDefaultVideoEditor(enabled: Boolean) {
        viewModelScope.launch { settingsManager.setUseDefaultVideoEditor(enabled) }
    }

    fun setAutoPlayVideo(enabled: Boolean) {
        viewModelScope.launch { settingsManager.setAutoPlayVideo(enabled) }
    }

    fun setDefaultMuteVideo(enabled: Boolean) {
        viewModelScope.launch { settingsManager.setDefaultMuteVideo(enabled) }
    }

    fun setThemeMode(mode: String) {
        viewModelScope.launch { settingsManager.setThemeMode(mode) }
    }

    fun setThemeColor(color: AppThemeColor) {
        viewModelScope.launch { settingsManager.setThemeColor(color) }
    }

    fun setAccentColor(color: Int) {
        viewModelScope.launch { settingsManager.setAccentColor(color) }
    }

    fun setSecondaryColor(color: Int) {
        viewModelScope.launch { settingsManager.setSecondaryColor(color) }
    }

    fun setTertiaryColor(color: Int) {
        viewModelScope.launch { settingsManager.setTertiaryColor(color) }
    }

    fun setDateFormat(format: String) {
        viewModelScope.launch { settingsManager.setDateFormat(format) }
    }

    fun setDefaultEditorPackage(pkg: String?) {
        viewModelScope.launch { settingsManager.setDefaultEditorPackage(pkg) }
    }

    fun setDefaultVideoEditorPackage(pkg: String?) {
        viewModelScope.launch { settingsManager.setDefaultVideoEditorPackage(pkg) }
    }

    fun setSearchAllFilesByDefault(enabled: Boolean) {
        viewModelScope.launch { settingsManager.setSearchAllFilesByDefault(enabled) }
    }

    fun setFontFamily(fontFamily: String) {
        viewModelScope.launch { settingsManager.setFontFamily(fontFamily) }
    }

    fun setAmoledMode(enabled: Boolean) {
        viewModelScope.launch { settingsManager.setAmoledMode(enabled) }
    }

    fun setGridColumns(columns: Int) {
        viewModelScope.launch { settingsManager.setGridColumns(columns) }
    }

    fun setFullscreenRotationMode(mode: FullscreenRotationMode) {
        viewModelScope.launch { settingsManager.setFullscreenRotationMode(mode) }
    }

    fun setDiskCacheMb(mb: Int) {
        viewModelScope.launch { settingsManager.setDiskCacheMb(mb) }
    }

    fun setShowNavLabel(show: Boolean) {
        viewModelScope.launch { settingsManager.setShowNavLabel(show) }
    }

    fun setDefaultSort(sort: String) {
        viewModelScope.launch { settingsManager.setDefaultSort(sort) }
    }

    fun setTrashWarningEnabled(enabled: Boolean) {
        viewModelScope.launch { settingsManager.setTrashWarningEnabled(enabled) }
    }

    fun triggerScrollToTop(route: String = "") {
        viewModelScope.launch { _scrollToTopTrigger.emit(route) }
    }
}

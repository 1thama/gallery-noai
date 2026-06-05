package com.tama.gallerynoai.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tama.gallerynoai.data.settings.SettingsManager
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsManager: SettingsManager
) : ViewModel() {

    val useDefaultEditor: StateFlow<Boolean> = settingsManager.useDefaultEditor
    val defaultEditorPackage: StateFlow<String?> = settingsManager.defaultEditorPackage
    val useDefaultVideoEditor: StateFlow<Boolean> = settingsManager.useDefaultVideoEditor
    val defaultVideoEditorPackage: StateFlow<String?> = settingsManager.defaultVideoEditorPackage
    val themeMode: StateFlow<String> = settingsManager.themeMode
    val accentColor: StateFlow<Int> = settingsManager.accentColor
    val secondaryColor: StateFlow<Int> = settingsManager.secondaryColor
    val tertiaryColor: StateFlow<Int> = settingsManager.tertiaryColor
    val dateFormat: StateFlow<String> = settingsManager.dateFormat
    val searchAllFilesByDefault: StateFlow<Boolean> = settingsManager.searchAllFilesByDefault
    val fontFamily: StateFlow<String> = settingsManager.fontFamily

    private val _scrollToTopTrigger = MutableSharedFlow<String>(replay = 0)
    val scrollToTopTrigger: SharedFlow<String> = _scrollToTopTrigger.asSharedFlow()

    fun setUseDefaultEditor(enabled: Boolean) {
        viewModelScope.launch { settingsManager.setUseDefaultEditor(enabled) }
    }

    fun toggleUseDefaultEditor(enabled: Boolean) {
        viewModelScope.launch { settingsManager.setUseDefaultEditor(enabled) }
    }

    fun setUseDefaultVideoEditor(enabled: Boolean) {
        viewModelScope.launch { settingsManager.setUseDefaultVideoEditor(enabled) }
    }

    fun toggleUseDefaultVideoEditor(enabled: Boolean) {
        viewModelScope.launch { settingsManager.setUseDefaultVideoEditor(enabled) }
    }

    fun setThemeMode(mode: String) {
        viewModelScope.launch { settingsManager.setThemeMode(mode) }
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

    fun triggerScrollToTop(route: String = "") {
        viewModelScope.launch { _scrollToTopTrigger.emit(route) }
    }
}

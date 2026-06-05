package com.tama.gallerynoai.data.settings

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("gallery_settings", Context.MODE_PRIVATE)

    private val _themeMode = MutableStateFlow(prefs.getString(KEY_THEME_MODE, "System") ?: "System")
    val themeMode: StateFlow<String> = _themeMode.asStateFlow()

    private val _accentColor = MutableStateFlow(prefs.getInt(KEY_ACCENT_COLOR, 0xFF6650a4.toInt()))
    val accentColor: StateFlow<Int> = _accentColor.asStateFlow()

    private val _secondaryColor = MutableStateFlow(prefs.getInt(KEY_SECONDARY_COLOR, 0xFF625b71.toInt()))
    val secondaryColor: StateFlow<Int> = _secondaryColor.asStateFlow()

    private val _tertiaryColor = MutableStateFlow(prefs.getInt(KEY_TERTIARY_COLOR, 0xFF7D5260.toInt()))
    val tertiaryColor: StateFlow<Int> = _tertiaryColor.asStateFlow()

    private val _dateFormat = MutableStateFlow(prefs.getString(KEY_DATE_FORMAT, "dd/MM/yyyy") ?: "dd/MM/yyyy")
    val dateFormat: StateFlow<String> = _dateFormat.asStateFlow()

    private val _useDefaultEditor = MutableStateFlow(prefs.getBoolean(KEY_USE_DEFAULT_EDITOR, false))
    val useDefaultEditor: StateFlow<Boolean> = _useDefaultEditor.asStateFlow()

    private val _defaultEditorPackage = MutableStateFlow(prefs.getString(KEY_DEFAULT_EDITOR_PACKAGE, null))
    val defaultEditorPackage: StateFlow<String?> = _defaultEditorPackage.asStateFlow()

    private val _useDefaultVideoEditor = MutableStateFlow(prefs.getBoolean(KEY_USE_DEFAULT_VIDEO_EDITOR, false))
    val useDefaultVideoEditor: StateFlow<Boolean> = _useDefaultVideoEditor.asStateFlow()

    private val _defaultVideoEditorPackage = MutableStateFlow(prefs.getString(KEY_DEFAULT_VIDEO_EDITOR_PACKAGE, null))
    val defaultVideoEditorPackage: StateFlow<String?> = _defaultVideoEditorPackage.asStateFlow()

    private val _searchAllFilesByDefault = MutableStateFlow(prefs.getBoolean(KEY_SEARCH_ALL_FILES_BY_DEFAULT, true))
    val searchAllFilesByDefault: StateFlow<Boolean> = _searchAllFilesByDefault.asStateFlow()

    private val _fontFamily = MutableStateFlow(prefs.getString(KEY_FONT_FAMILY, "Default") ?: "Default")
    val fontFamily: StateFlow<String> = _fontFamily.asStateFlow()

    fun setThemeMode(mode: String) {
        prefs.edit { putString(KEY_THEME_MODE, mode) }
        _themeMode.value = mode
    }

    fun setAccentColor(color: Int) {
        prefs.edit { putInt(KEY_ACCENT_COLOR, color) }
        _accentColor.value = color
    }

    fun setSecondaryColor(color: Int) {
        prefs.edit { putInt(KEY_SECONDARY_COLOR, color) }
        _secondaryColor.value = color
    }

    fun setTertiaryColor(color: Int) {
        prefs.edit { putInt(KEY_TERTIARY_COLOR, color) }
        _tertiaryColor.value = color
    }

    fun setDateFormat(format: String) {
        prefs.edit { putString(KEY_DATE_FORMAT, format) }
        _dateFormat.value = format
    }

    fun setUseDefaultEditor(enabled: Boolean) {
        prefs.edit { putBoolean(KEY_USE_DEFAULT_EDITOR, enabled) }
        _useDefaultEditor.value = enabled
    }

    fun setDefaultEditorPackage(packageName: String?) {
        prefs.edit { putString(KEY_DEFAULT_EDITOR_PACKAGE, packageName) }
        _defaultEditorPackage.value = packageName
    }

    fun setUseDefaultVideoEditor(enabled: Boolean) {
        prefs.edit { putBoolean(KEY_USE_DEFAULT_VIDEO_EDITOR, enabled) }
        _useDefaultVideoEditor.value = enabled
    }

    fun setDefaultVideoEditorPackage(packageName: String?) {
        prefs.edit { putString(KEY_DEFAULT_VIDEO_EDITOR_PACKAGE, packageName) }
        _defaultVideoEditorPackage.value = packageName
    }

    fun setSearchAllFilesByDefault(enabled: Boolean) {
        prefs.edit { putBoolean(KEY_SEARCH_ALL_FILES_BY_DEFAULT, enabled) }
        _searchAllFilesByDefault.value = enabled
    }

    fun setFontFamily(fontFamily: String) {
        prefs.edit { putString(KEY_FONT_FAMILY, fontFamily) }
        _fontFamily.value = fontFamily
    }

    companion object {
        private const val KEY_THEME_MODE = "theme_mode"
        private const val KEY_ACCENT_COLOR = "accent_color"
        private const val KEY_SECONDARY_COLOR = "secondary_color"
        private const val KEY_TERTIARY_COLOR = "tertiary_color"
        private const val KEY_DATE_FORMAT = "date_format"
        private const val KEY_USE_DEFAULT_EDITOR = "use_default_editor"
        private const val KEY_DEFAULT_EDITOR_PACKAGE = "default_editor_package"
        private const val KEY_USE_DEFAULT_VIDEO_EDITOR = "use_default_video_editor"
        private const val KEY_DEFAULT_VIDEO_EDITOR_PACKAGE = "default_video_editor_package"
        private const val KEY_SEARCH_ALL_FILES_BY_DEFAULT = "search_all_files_by_default"
        private const val KEY_FONT_FAMILY = "font_family"
    }
}


package com.tama.gallerynoai.data.settings

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

enum class AppThemeColor {
    DEFAULT,
    DYNAMIC_COLOR,
    OCEAN_BLUE,
    FOREST_GREEN,
    SUNSET_ORANGE
}

enum class FullscreenRotationMode {
    SYSTEM_SETTING,
    DEVICE_ROTATION,
    ASPECT_RATIO
}

class SettingsManager(private val context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("gallery_settings", Context.MODE_PRIVATE)
    private val scope = CoroutineScope(Dispatchers.IO)

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

    private val _autoPlayVideo = MutableStateFlow(prefs.getBoolean(KEY_AUTO_PLAY_VIDEO, true))
    val autoPlayVideo: StateFlow<Boolean> = _autoPlayVideo.asStateFlow()

    private val _defaultMuteVideo = MutableStateFlow(prefs.getBoolean(KEY_DEFAULT_MUTE_VIDEO, false))
    val defaultMuteVideo: StateFlow<Boolean> = _defaultMuteVideo.asStateFlow()

    private val _defaultVideoEditorPackage = MutableStateFlow(prefs.getString(KEY_DEFAULT_VIDEO_EDITOR_PACKAGE, null))
    val defaultVideoEditorPackage: StateFlow<String?> = _defaultVideoEditorPackage.asStateFlow()

    private val _searchAllFilesByDefault = MutableStateFlow(prefs.getBoolean(KEY_SEARCH_ALL_FILES_BY_DEFAULT, true))
    val searchAllFilesByDefault: StateFlow<Boolean> = _searchAllFilesByDefault.asStateFlow()

    private val _fontFamily = MutableStateFlow(prefs.getString(KEY_FONT_FAMILY, "Default") ?: "Default")
    val fontFamily: StateFlow<String> = _fontFamily.asStateFlow()

    private val _amoledMode = MutableStateFlow(prefs.getBoolean(KEY_AMOLED_MODE, false))
    val amoledMode: StateFlow<Boolean> = _amoledMode.asStateFlow()

    private val _gridColumns = MutableStateFlow(prefs.getInt(KEY_GRID_COLUMNS, 3))
    val gridColumns: StateFlow<Int> = _gridColumns.asStateFlow()

    private val _fullscreenRotationMode = MutableStateFlow(
        try {
            FullscreenRotationMode.valueOf(prefs.getString(KEY_FULLSCREEN_ROTATION_MODE, FullscreenRotationMode.SYSTEM_SETTING.name) ?: FullscreenRotationMode.SYSTEM_SETTING.name)
        } catch (e: Exception) {
            FullscreenRotationMode.SYSTEM_SETTING
        }
    )
    val fullscreenRotationMode: StateFlow<FullscreenRotationMode> = _fullscreenRotationMode.asStateFlow()

    private val _diskCacheMb = MutableStateFlow(prefs.getInt(KEY_DISK_CACHE_MB, 512))
    val diskCacheMb: StateFlow<Int> = _diskCacheMb.asStateFlow()

    private val _showNavLabel = MutableStateFlow(prefs.getBoolean(KEY_SHOW_NAV_LABEL, true))
    val showNavLabel: StateFlow<Boolean> = _showNavLabel.asStateFlow()

    private val _defaultSort = MutableStateFlow(prefs.getString(KEY_DEFAULT_SORT, "DATE_NEWEST") ?: "DATE_NEWEST")
    val defaultSort: StateFlow<String> = _defaultSort.asStateFlow()

    private val _trashWarningEnabled = MutableStateFlow(prefs.getBoolean(KEY_TRASH_WARNING, true))
    val trashWarningEnabled: StateFlow<Boolean> = _trashWarningEnabled.asStateFlow()

    // Color Theme using DataStore
    private val themeColorKey = stringPreferencesKey("theme_color")
    val themeColor: StateFlow<AppThemeColor> = context.dataStore.data
        .map { preferences ->
            val colorName = preferences[themeColorKey] ?: AppThemeColor.DEFAULT.name
            try {
                AppThemeColor.valueOf(colorName)
            } catch (e: Exception) {
                AppThemeColor.DEFAULT
            }
        }.stateIn(scope, SharingStarted.Eagerly, AppThemeColor.DEFAULT)

    fun setThemeColor(color: AppThemeColor) {
        scope.launch {
            context.dataStore.edit { preferences ->
                preferences[themeColorKey] = color.name
            }
        }
    }

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

    fun setAutoPlayVideo(enabled: Boolean) {
        prefs.edit { putBoolean(KEY_AUTO_PLAY_VIDEO, enabled) }
        _autoPlayVideo.value = enabled
    }

    fun setDefaultMuteVideo(enabled: Boolean) {
        prefs.edit { putBoolean(KEY_DEFAULT_MUTE_VIDEO, enabled) }
        _defaultMuteVideo.value = enabled
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

    fun setAmoledMode(enabled: Boolean) {
        prefs.edit { putBoolean(KEY_AMOLED_MODE, enabled) }
        _amoledMode.value = enabled
    }

    fun setGridColumns(columns: Int) {
        prefs.edit { putInt(KEY_GRID_COLUMNS, columns) }
        _gridColumns.value = columns
    }

    fun setFullscreenRotationMode(mode: FullscreenRotationMode) {
        prefs.edit { putString(KEY_FULLSCREEN_ROTATION_MODE, mode.name) }
        _fullscreenRotationMode.value = mode
    }

    fun setDiskCacheMb(mb: Int) {
        prefs.edit { putInt(KEY_DISK_CACHE_MB, mb) }
        _diskCacheMb.value = mb
    }

    fun setShowNavLabel(show: Boolean) {
        prefs.edit { putBoolean(KEY_SHOW_NAV_LABEL, show) }
        _showNavLabel.value = show
    }

    fun setDefaultSort(sort: String) {
        prefs.edit { putString(KEY_DEFAULT_SORT, sort) }
        _defaultSort.value = sort
    }

    fun setTrashWarningEnabled(enabled: Boolean) {
        prefs.edit { putBoolean(KEY_TRASH_WARNING, enabled) }
        _trashWarningEnabled.value = enabled
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
        private const val KEY_AUTO_PLAY_VIDEO = "auto_play_video"
        private const val KEY_DEFAULT_MUTE_VIDEO = "default_mute_video"
        private const val KEY_DEFAULT_VIDEO_EDITOR_PACKAGE = "default_video_editor_package"
        private const val KEY_SEARCH_ALL_FILES_BY_DEFAULT = "search_all_files_by_default"
        private const val KEY_FONT_FAMILY = "font_family"
        private const val KEY_AMOLED_MODE = "amoled_mode"
        private const val KEY_GRID_COLUMNS = "grid_columns"
        private const val KEY_FULLSCREEN_ROTATION_MODE = "fullscreen_rotation_mode"
        private const val KEY_DISK_CACHE_MB = "disk_cache_mb"
        private const val KEY_SHOW_NAV_LABEL = "show_nav_label"
        private const val KEY_DEFAULT_SORT = "default_sort"
        private const val KEY_TRASH_WARNING = "trash_warning_enabled"
    }
}

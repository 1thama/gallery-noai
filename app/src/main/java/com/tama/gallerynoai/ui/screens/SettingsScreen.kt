package com.tama.gallerynoai.ui.screens

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Label
import androidx.compose.material.icons.automirrored.filled.ManageSearch
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.filled.*
import kotlinx.coroutines.launch
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tama.gallerynoai.data.settings.AppThemeColor
import com.tama.gallerynoai.data.settings.FullscreenRotationMode
import com.tama.gallerynoai.ui.theme.SelectableColors
import com.tama.gallerynoai.ui.viewmodel.SettingsViewModel

data class EditorApp(
    val name: String,
    val packageName: String,
    val icon: Drawable
)

@Composable
fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .padding(start = 16.dp, top = 24.dp, end = 16.dp, bottom = 8.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: SettingsViewModel) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val locale = configuration.locales[0]
    val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
    val dateFormat by viewModel.dateFormat.collectAsStateWithLifecycle()
    val useDefaultEditor by viewModel.useDefaultEditor.collectAsStateWithLifecycle()
    val defaultEditorPackage by viewModel.defaultEditorPackage.collectAsStateWithLifecycle()
    val useDefaultVideoEditor by viewModel.useDefaultVideoEditor.collectAsStateWithLifecycle()
    val autoPlayVideo by viewModel.autoPlayVideo.collectAsStateWithLifecycle()
    val defaultMuteVideo by viewModel.defaultMuteVideo.collectAsStateWithLifecycle()
    val defaultVideoEditorPackage by viewModel.defaultVideoEditorPackage.collectAsStateWithLifecycle()
    val searchAllFilesByDefault by viewModel.searchAllFilesByDefault.collectAsStateWithLifecycle()
    val fontFamily by viewModel.fontFamily.collectAsStateWithLifecycle()
    val amoledMode by viewModel.amoledMode.collectAsStateWithLifecycle()
    val gridColumns by viewModel.gridColumns.collectAsStateWithLifecycle()
    val fullscreenRotationMode by viewModel.fullscreenRotationMode.collectAsStateWithLifecycle()
    val diskCacheMb by viewModel.diskCacheMb.collectAsStateWithLifecycle()
    val showNavLabel by viewModel.showNavLabel.collectAsStateWithLifecycle()
    val defaultSort by viewModel.defaultSort.collectAsStateWithLifecycle()
    val trashWarningEnabled by viewModel.trashWarningEnabled.collectAsStateWithLifecycle()
    val themeColor by viewModel.themeColor.collectAsStateWithLifecycle()
    val accentColorInt by viewModel.accentColor.collectAsStateWithLifecycle()
    val secondaryColorInt by viewModel.secondaryColor.collectAsStateWithLifecycle()
    val tertiaryColorInt by viewModel.tertiaryColor.collectAsStateWithLifecycle()

    val scrollState = rememberScrollState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var showThemeDialog by remember { mutableStateOf(false) }
    var showThemeColorDialog by remember { mutableStateOf(false) }
    var showAccentColorDialog by remember { mutableStateOf(false) }
    var showSecondaryColorDialog by remember { mutableStateOf(false) }
    var showTertiaryColorDialog by remember { mutableStateOf(false) }
    var showDateFormatDialog by remember { mutableStateOf(false) }
    var showFontDialog by remember { mutableStateOf(false) }
    var showGridColumnsDialog by remember { mutableStateOf(false) }
    var showRotationModeDialog by remember { mutableStateOf(false) }
    var showDiskCacheDialog by remember { mutableStateOf(false) }
    var showDefaultSortDialog by remember { mutableStateOf(false) }
    var showEditorDialog by remember { mutableStateOf(false) }
    var showVideoEditorDialog by remember { mutableStateOf(false) }
    var availableEditors by remember { mutableStateOf<List<EditorApp>>(emptyList()) }
    var availableVideoEditors by remember { mutableStateOf<List<EditorApp>>(emptyList()) }

    fun loadEditors(isVideos: Boolean = false) {
        val intent = Intent(Intent.ACTION_EDIT).apply {
            type = if (isVideos) "video/*" else "image/*"
        }
        val packageManager = context.packageManager
        val resolveInfos = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
            .ifEmpty { packageManager.queryIntentActivities(intent, 0) }

        val editors = resolveInfos.map {
            EditorApp(
                name = it.loadLabel(packageManager).toString(),
                packageName = it.activityInfo.packageName,
                icon = it.loadIcon(packageManager)
            )
        }

        if (isVideos) {
            availableVideoEditors = editors
        } else {
            availableEditors = editors
        }
    }

    LaunchedEffect(Unit) {
        loadEditors(false)
        loadEditors(true)
        viewModel.scrollToTopTrigger.collect {
            scrollState.animateScrollTo(0)
        }
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Settings",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.headlineMedium
                    )
                },
                scrollBehavior = scrollBehavior,
                windowInsets = WindowInsets(0, 0, 0, 0),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    scrolledContainerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
        ) {
            SettingsSectionHeader("General")
            ListItem(
                modifier = Modifier.clickable { showDefaultSortDialog = true },
                headlineContent = { Text("Default Sort Order") },
                supportingContent = {
                    val label = when (defaultSort) {
                        "DATE_NEWEST" -> "Date: Newest First"
                        "DATE_OLDEST" -> "Date: Oldest First"
                        "SIZE_LARGEST" -> "Size: Largest First"
                        "SIZE_SMALLEST" -> "Size: Smallest First"
                        else -> defaultSort
                    }
                    Text(label)
                },
                leadingContent = { Icon(Icons.AutoMirrored.Filled.Sort, contentDescription = null) }
            )
            ListItem(
                headlineContent = { Text("Search All Files") },
                supportingContent = { Text("Search recursively across all device directories") },
                leadingContent = { Icon(Icons.AutoMirrored.Filled.ManageSearch, contentDescription = null) },
                trailingContent = {
                    Switch(
                        checked = searchAllFilesByDefault,
                        onCheckedChange = { viewModel.setSearchAllFilesByDefault(it) }
                    )
                }
            )

            ListItem(
                modifier = Modifier.clickable { showDateFormatDialog = true },
                headlineContent = { Text("Date Format") },
                supportingContent = { Text(dateFormat) },
                leadingContent = { Icon(Icons.Default.CalendarToday, contentDescription = null) }
            )
            ListItem(
                modifier = Modifier.clickable { showRotationModeDialog = true },
                headlineContent = { Text("Fullscreen Rotation Mode") },
                supportingContent = {
                    val label = when (fullscreenRotationMode) {
                        FullscreenRotationMode.SYSTEM_SETTING -> "Follow System Setting"
                        FullscreenRotationMode.DEVICE_ROTATION -> "Force Device Rotation (Sensor)"
                        FullscreenRotationMode.ASPECT_RATIO -> "Auto Aspect Ratio"
                    }
                    Text(label)
                },
                leadingContent = { Icon(Icons.Default.ScreenRotation, contentDescription = null) }
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            SettingsSectionHeader("Appearance")
            ListItem(
                modifier = Modifier.clickable { showGridColumnsDialog = true },
                headlineContent = { Text("Grid Columns") },
                supportingContent = { Text("$gridColumns columns") },
                leadingContent = { Icon(Icons.Default.GridView, contentDescription = null) }
            )
            ListItem(
                headlineContent = { Text("Show Navigation Labels") },
                supportingContent = { Text("Show text labels in the bottom navigation bar") },
                leadingContent = { Icon(Icons.AutoMirrored.Filled.Label, contentDescription = null) },
                trailingContent = {
                    Switch(
                        checked = showNavLabel,
                        onCheckedChange = { viewModel.setShowNavLabel(it) }
                    )
                }
            )
            ListItem(
                modifier = Modifier.clickable { showThemeDialog = true },
                headlineContent = { Text("App Theme") },
                supportingContent = { Text(themeMode) },
                leadingContent = { Icon(Icons.Default.Brightness4, contentDescription = null) }
            )
            ListItem(
                modifier = Modifier.clickable { showThemeColorDialog = true },
                headlineContent = { Text("Theme Color") },
                supportingContent = { Text(themeColor.name.replace("_", " ").lowercase().replaceFirstChar { if (it.isLowerCase()) it.titlecase(locale) else it.toString() }) },
                leadingContent = { Icon(Icons.Default.Palette, contentDescription = null) }
            )
            ListItem(
                headlineContent = { Text("AMOLED Black Mode") },
                supportingContent = { Text("Pure black background, saves battery on OLED screens") },
                leadingContent = { Icon(Icons.Default.PhoneAndroid, contentDescription = null) },
                trailingContent = {
                    Switch(
                        checked = amoledMode,
                        onCheckedChange = { viewModel.setAmoledMode(it) },
                        enabled = themeMode == "Dark" || themeMode == "System"
                    )
                }
            )
            ListItem(
                modifier = Modifier.clickable { showAccentColorDialog = true },
                headlineContent = { Text("Primary Color") },
                supportingContent = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .background(Color(accentColorInt), CircleShape)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Change app primary color")
                    }
                },
                leadingContent = { Icon(Icons.Default.ColorLens, contentDescription = null) }
            )
            ListItem(
                modifier = Modifier.clickable { showSecondaryColorDialog = true },
                headlineContent = { Text("Secondary Color") },
                supportingContent = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .background(Color(secondaryColorInt), CircleShape)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Change app secondary color")
                    }
                },
                leadingContent = { Icon(Icons.Default.FormatColorFill, contentDescription = null) }
            )
            ListItem(
                modifier = Modifier.clickable { showTertiaryColorDialog = true },
                headlineContent = { Text("Tertiary Color") },
                supportingContent = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .background(Color(tertiaryColorInt), CircleShape)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Change app tertiary color")
                    }
                },
                leadingContent = { Icon(Icons.Default.Colorize, contentDescription = null) }
            )
            ListItem(
                modifier = Modifier.clickable { showFontDialog = true },
                headlineContent = { Text("Font Style") },
                supportingContent = { Text(fontFamily) },
                leadingContent = { Icon(Icons.Default.TextFields, contentDescription = null) }
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            SettingsSectionHeader("Privacy & Storage")
            ListItem(
                modifier = Modifier.clickable { showDiskCacheDialog = true },
                headlineContent = { Text("Thumbnail Cache Size") },
                supportingContent = { Text("$diskCacheMb MB") },
                leadingContent = { Icon(Icons.Default.Storage, contentDescription = null) }
            )
            ListItem(
                headlineContent = { Text("Trash Deletion Warning") },
                supportingContent = { Text("Show reminder that trash is auto-deleted after 30 days") },
                leadingContent = { Icon(Icons.Default.RestoreFromTrash, contentDescription = null) },
                trailingContent = {
                    Switch(
                        checked = trashWarningEnabled,
                        onCheckedChange = { viewModel.setTrashWarningEnabled(it) }
                    )
                }
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            SettingsSectionHeader("Editor Settings")
            ListItem(
                modifier = Modifier.clickable {
                    loadEditors(false)
                    showEditorDialog = true
                },
                headlineContent = { Text("Use Default Photo Editor") },
                supportingContent = {
                    val currentEditor = availableEditors.find { it.packageName == defaultEditorPackage }?.name ?: "None selected"
                    Text("Selected: $currentEditor")
                },
                leadingContent = { Icon(Icons.Default.Image, contentDescription = null) },
                trailingContent = {
                    Switch(
                        checked = useDefaultEditor,
                        onCheckedChange = { viewModel.toggleUseDefaultEditor(it) }
                    )
                }
            )
            ListItem(
                modifier = Modifier.clickable {
                    loadEditors(true)
                    showVideoEditorDialog = true
                },
                headlineContent = { Text("Use Default Video Editor") },
                supportingContent = {
                    val currentEditor = availableVideoEditors.find { it.packageName == defaultVideoEditorPackage }?.name ?: "None selected"
                    Text("Selected: $currentEditor")
                },
                leadingContent = { Icon(Icons.Default.Movie, contentDescription = null) },
                trailingContent = {
                    Switch(
                        checked = useDefaultVideoEditor,
                        onCheckedChange = { viewModel.toggleUseDefaultVideoEditor(it) }
                    )
                }
            )
            ListItem(
                headlineContent = { Text("Auto-Play Video") },
                supportingContent = { Text("Automatically play video when opened") },
                leadingContent = { Icon(Icons.Default.PlayCircle, contentDescription = null) },
                trailingContent = {
                    Switch(
                        checked = autoPlayVideo,
                        onCheckedChange = { viewModel.setAutoPlayVideo(it) }
                    )
                }
            )
            ListItem(
                headlineContent = { Text("Default Mute Video") },
                supportingContent = { Text("Start videos muted by default") },
                leadingContent = { Icon(Icons.AutoMirrored.Filled.VolumeOff, contentDescription = null) },
                trailingContent = {
                    Switch(
                        checked = defaultMuteVideo,
                        onCheckedChange = { viewModel.setDefaultMuteVideo(it) }
                    )
                }
            )

            Spacer(modifier = Modifier.height(32.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Version 1.0 - By Tama",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }

    if (showGridColumnsDialog) {
        AlertDialog(
            onDismissRequest = { showGridColumnsDialog = false },
            title = { Text("Grid Columns") },
            text = {
                Column {
                    listOf(2, 3, 4).forEach { columns ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.setGridColumns(columns)
                                    showGridColumnsDialog = false
                                }
                                .padding(vertical = 12.dp)
                        ) {
                            RadioButton(selected = gridColumns == columns, onClick = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("$columns columns")
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showGridColumnsDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showRotationModeDialog) {
        AlertDialog(
            onDismissRequest = { showRotationModeDialog = false },
            title = { Text("Fullscreen Rotation Mode") },
            text = {
                Column {
                    listOf(
                        FullscreenRotationMode.SYSTEM_SETTING to "Follow System Setting",
                        FullscreenRotationMode.DEVICE_ROTATION to "Force Device Rotation (Sensor)",
                        FullscreenRotationMode.ASPECT_RATIO to "Auto Aspect Ratio"
                    ).forEach { (mode, label) ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.setFullscreenRotationMode(mode)
                                    showRotationModeDialog = false
                                }
                                .padding(vertical = 12.dp)
                        ) {
                            RadioButton(selected = fullscreenRotationMode == mode, onClick = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(label)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showRotationModeDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showDiskCacheDialog) {
        AlertDialog(
            onDismissRequest = { showDiskCacheDialog = false },
            title = { Text("Thumbnail Cache Size") },
            text = {
                Column {
                    listOf(256, 512, 1024).forEach { size ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.setDiskCacheMb(size)
                                    showDiskCacheDialog = false
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Restart app to apply cache changes")
                                    }
                                }
                                .padding(vertical = 12.dp)
                        ) {
                            RadioButton(selected = diskCacheMb == size, onClick = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("$size MB")
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showDiskCacheDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showDefaultSortDialog) {
        AlertDialog(
            onDismissRequest = { showDefaultSortDialog = false },
            title = { Text("Default Sort Order") },
            text = {
                Column {
                    listOf(
                        "DATE_NEWEST" to "Date: Newest First",
                        "DATE_OLDEST" to "Date: Oldest First",
                        "SIZE_LARGEST" to "Size: Largest First",
                        "SIZE_SMALLEST" to "Size: Smallest First"
                    ).forEach { (value, label) ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.setDefaultSort(value)
                                    showDefaultSortDialog = false
                                }
                                .padding(vertical = 12.dp)
                        ) {
                            RadioButton(selected = defaultSort == value, onClick = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(label)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showDefaultSortDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showThemeColorDialog) {
        AlertDialog(
            onDismissRequest = { showThemeColorDialog = false },
            title = { Text("Choose Theme Color") },
            text = {
                Column {
                    AppThemeColor.entries.forEach { color ->
                        val isAvailable = if (color == AppThemeColor.DYNAMIC_COLOR) {
                            android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S
                        } else true

                        if (isAvailable) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        viewModel.setThemeColor(color)
                                        showThemeColorDialog = false
                                    }
                                    .padding(vertical = 12.dp)
                            ) {
                                RadioButton(selected = themeColor == color, onClick = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(color.name.replace("_", " ").lowercase().replaceFirstChar { if (it.isLowerCase()) it.titlecase(locale) else it.toString() })
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showThemeColorDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showThemeDialog) {
        AlertDialog(
            onDismissRequest = { showThemeDialog = false },
            title = { Text("Choose Theme") },
            text = {
                Column {
                    listOf("System", "Light", "Dark").forEach { mode ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.setThemeMode(mode)
                                    showThemeDialog = false
                                }
                                .padding(vertical = 12.dp)
                        ) {
                            RadioButton(selected = themeMode == mode, onClick = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(mode)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showThemeDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showAccentColorDialog) {
        AlertDialog(
            onDismissRequest = { showAccentColorDialog = false },
            title = { Text("Choose Primary Color") },
            text = {
                Column {
                    androidx.compose.foundation.lazy.grid.LazyVerticalGrid(
                        columns = androidx.compose.foundation.lazy.grid.GridCells.Adaptive(minSize = 48.dp),
                        modifier = Modifier.height(200.dp)
                    ) {
                        items(SelectableColors.size) { index ->
                            val color = SelectableColors[index]
                            Box(
                                modifier = Modifier
                                    .padding(8.dp)
                                    .size(40.dp)
                                    .background(color, CircleShape)
                                    .clickable {
                                        viewModel.setAccentColor(color.toArgb())
                                        showAccentColorDialog = false
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                if (color.toArgb() == accentColorInt) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = if (color.toArgb() == Color.White.toArgb()) Color.Black else Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showAccentColorDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showSecondaryColorDialog) {
        AlertDialog(
            onDismissRequest = { showSecondaryColorDialog = false },
            title = { Text("Choose Secondary Color") },
            text = {
                Column {
                    androidx.compose.foundation.lazy.grid.LazyVerticalGrid(
                        columns = androidx.compose.foundation.lazy.grid.GridCells.Adaptive(minSize = 48.dp),
                        modifier = Modifier.height(200.dp)
                    ) {
                        items(SelectableColors.size) { index ->
                            val color = SelectableColors[index]
                            Box(
                                modifier = Modifier
                                    .padding(8.dp)
                                    .size(40.dp)
                                    .background(color, CircleShape)
                                    .clickable {
                                        viewModel.setSecondaryColor(color.toArgb())
                                        showSecondaryColorDialog = false
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                if (color.toArgb() == secondaryColorInt) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = if (color.toArgb() == Color.White.toArgb()) Color.Black else Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showSecondaryColorDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showTertiaryColorDialog) {
        AlertDialog(
            onDismissRequest = { showTertiaryColorDialog = false },
            title = { Text("Choose Tertiary Color") },
            text = {
                Column {
                    androidx.compose.foundation.lazy.grid.LazyVerticalGrid(
                        columns = androidx.compose.foundation.lazy.grid.GridCells.Adaptive(minSize = 48.dp),
                        modifier = Modifier.height(200.dp)
                    ) {
                        items(SelectableColors.size) { index ->
                            val color = SelectableColors[index]
                            Box(
                                modifier = Modifier
                                    .padding(8.dp)
                                    .size(40.dp)
                                    .background(color, CircleShape)
                                    .clickable {
                                        viewModel.setTertiaryColor(color.toArgb())
                                        showTertiaryColorDialog = false
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                if (color.toArgb() == tertiaryColorInt) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = if (color.toArgb() == Color.White.toArgb()) Color.Black else Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showTertiaryColorDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showDateFormatDialog) {
        AlertDialog(
            onDismissRequest = { showDateFormatDialog = false },
            title = { Text("Date Format") },
            text = {
                Column {
                    listOf("dd/MM/yyyy", "MM/dd/yyyy", "yyyy-MM-dd", "dd MMM yyyy").forEach { format ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.setDateFormat(format)
                                    showDateFormatDialog = false
                                }
                                .padding(vertical = 12.dp)
                        ) {
                            RadioButton(selected = dateFormat == format, onClick = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(format)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showDateFormatDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showFontDialog) {
        AlertDialog(
            onDismissRequest = { showFontDialog = false },
            title = { Text("Choose Font Style") },
            text = {
                Column {
                    listOf("Default", "Jakarta Sans", "Jakarta Sans Italic").forEach { font ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.setFontFamily(font)
                                    showFontDialog = false
                                }
                                .padding(vertical = 12.dp)
                        ) {
                            RadioButton(selected = fontFamily == font, onClick = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(font)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showFontDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showEditorDialog) {
        AlertDialog(
            onDismissRequest = { showEditorDialog = false },
            title = { Text("Choose Default Photo Editor") },
            text = {
                LazyColumn {
                    items(availableEditors) { editor ->
                        ListItem(
                            modifier = Modifier.clickable {
                                viewModel.setDefaultEditorPackage(editor.packageName)
                                showEditorDialog = false
                            },
                            headlineContent = { Text(editor.name) },
                            leadingContent = {
                                Image(
                                    bitmap = editor.icon.toBitmap().asImageBitmap(),
                                    contentDescription = null,
                                    modifier = Modifier.size(40.dp)
                                )
                            },
                            trailingContent = {
                                RadioButton(
                                    selected = defaultEditorPackage == editor.packageName,
                                    onClick = null
                                )
                            }
                        )
                    }
                    if (availableEditors.isEmpty()) {
                        item {
                            Text(
                                "No photo editor apps found",
                                modifier = Modifier.padding(16.dp),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showEditorDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showVideoEditorDialog) {
        AlertDialog(
            onDismissRequest = { showVideoEditorDialog = false },
            title = { Text("Choose Default Video Editor") },
            text = {
                LazyColumn {
                    items(availableVideoEditors) { editor ->
                        ListItem(
                            modifier = Modifier.clickable {
                                viewModel.setDefaultVideoEditorPackage(editor.packageName)
                                showVideoEditorDialog = false
                            },
                            headlineContent = { Text(editor.name) },
                            leadingContent = {
                                Image(
                                    bitmap = editor.icon.toBitmap().asImageBitmap(),
                                    contentDescription = null,
                                    modifier = Modifier.size(40.dp)
                                )
                            },
                            trailingContent = {
                                RadioButton(
                                    selected = defaultVideoEditorPackage == editor.packageName,
                                    onClick = null
                                )
                            }
                        )
                    }
                    if (availableVideoEditors.isEmpty()) {
                        item {
                            Text(
                                "No video editor apps found",
                                modifier = Modifier.padding(16.dp),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showVideoEditorDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

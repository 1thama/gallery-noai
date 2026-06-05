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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
    val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
    val dateFormat by viewModel.dateFormat.collectAsStateWithLifecycle()
    val useDefaultEditor by viewModel.useDefaultEditor.collectAsStateWithLifecycle()
    val defaultEditorPackage by viewModel.defaultEditorPackage.collectAsStateWithLifecycle()
    val useDefaultVideoEditor by viewModel.useDefaultVideoEditor.collectAsStateWithLifecycle()
    val defaultVideoEditorPackage by viewModel.defaultVideoEditorPackage.collectAsStateWithLifecycle()
    val searchAllFilesByDefault by viewModel.searchAllFilesByDefault.collectAsStateWithLifecycle()
    val fontFamily by viewModel.fontFamily.collectAsStateWithLifecycle()
    val accentColorInt by viewModel.accentColor.collectAsStateWithLifecycle()
    val secondaryColorInt by viewModel.secondaryColor.collectAsStateWithLifecycle()
    val tertiaryColorInt by viewModel.tertiaryColor.collectAsStateWithLifecycle()

    val scrollState = rememberScrollState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    var showThemeDialog by remember { mutableStateOf(false) }
    var showAccentColorDialog by remember { mutableStateOf(false) }
    var showSecondaryColorDialog by remember { mutableStateOf(false) }
    var showTertiaryColorDialog by remember { mutableStateOf(false) }
    var showDateFormatDialog by remember { mutableStateOf(false) }
    var showFontDialog by remember { mutableStateOf(false) }
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
                headlineContent = { Text("Search All Files") },
                supportingContent = { Text("Search recursively across all device directories") },
                leadingContent = { Icon(Icons.Default.ManageSearch, contentDescription = null) },
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

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            SettingsSectionHeader("Appearance")
            ListItem(
                modifier = Modifier.clickable { showThemeDialog = true },
                headlineContent = { Text("App Theme") },
                supportingContent = { Text(themeMode) },
                leadingContent = { Icon(Icons.Default.Brightness4, contentDescription = null) }
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
                leadingContent = { Icon(Icons.Default.Palette, contentDescription = null) }
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
                leadingContent = { Icon(Icons.Default.ColorLens, contentDescription = null) }
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
                leadingContent = { Icon(Icons.Default.FormatColorFill, contentDescription = null) }
            )
            ListItem(
                modifier = Modifier.clickable { showFontDialog = true },
                headlineContent = { Text("Font Style") },
                supportingContent = { Text(fontFamily) },
                leadingContent = { Icon(Icons.Default.TextFields, contentDescription = null) }
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

            Spacer(modifier = Modifier.height(32.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "By Thama - Version 1.0 (No AI)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
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

package com.tama.gallerynoai.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.tama.gallerynoai.data.model.MediaItem
import kotlinx.coroutines.flow.StateFlow

data class DragSelectionState(
    val startIndex: Int,
    val initialSelectedIds: Set<Long>,
    val shouldSelect: Boolean
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectionTopAppBar(
    selectedCount: Int,
    onClearSelection: () -> Unit,
    onDelete: () -> Unit,
    onShare: (() -> Unit)? = null,
    onSelectAll: (() -> Unit)? = null,
    onFavorite: (() -> Unit)? = null,
    onBatchTag: (() -> Unit)? = null,
    onCopyToFolder: (() -> Unit)? = null,
    onMoveToFolder: (() -> Unit)? = null,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    isAllFavorite: Boolean = false
) {
    var showMenu by remember { mutableStateOf(false) }
    val hasMenuOptions = onSelectAll != null || onFavorite != null || onBatchTag != null || onCopyToFolder != null || onMoveToFolder != null

    TopAppBar(
        title = {
            Text(
                "$selectedCount Selected",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge
            )
        },
        navigationIcon = {
            IconButton(onClick = onClearSelection) {
                Icon(Icons.Default.Close, contentDescription = "Clear Selection")
            }
        },
        actions = {
            if (onShare != null) {
                IconButton(onClick = onShare) {
                    Icon(Icons.Default.Share, contentDescription = "Share")
                }
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Move to Trash")
            }
            if (hasMenuOptions) {
                IconButton(onClick = { showMenu = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "More options")
                }
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    onSelectAll?.let {
                        DropdownMenuItem(
                            text = { Text("Select All") },
                            onClick = {
                                showMenu = false
                                it()
                            }
                        )
                    }
                    onFavorite?.let {
                        DropdownMenuItem(
                            text = { Text(if (isAllFavorite) "Unfavorite" else "Favorite") },
                            onClick = {
                                showMenu = false
                                it()
                            }
                        )
                    }
                    onBatchTag?.let {
                        DropdownMenuItem(
                            text = { Text("Add Tag") },
                            onClick = {
                                showMenu = false
                                it()
                            }
                        )
                    }
                    onCopyToFolder?.let {
                        DropdownMenuItem(
                            text = { Text("Copy to folder") },
                            onClick = {
                                showMenu = false
                                it()
                            }
                        )
                    }
                    onMoveToFolder?.let {
                        DropdownMenuItem(
                            text = { Text("Move to folder") },
                            onClick = {
                                showMenu = false
                                it()
                            }
                        )
                    }
                }
            }
        },
        scrollBehavior = scrollBehavior,
        windowInsets = WindowInsets(0, 0, 0, 0),
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            scrolledContainerColor = MaterialTheme.colorScheme.surface
        )
    )
}

@Composable
fun DateHeader(date: String) {
    Text(
        text = date,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp, 16.dp, 16.dp, 8.dp),
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MediaGridItem(
    item: MediaItem,
    isSelected: Boolean = false,
    onClick: () -> Unit,
    onLongClick: () -> Unit = {},
    isSelectionMode: Boolean = false
) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .then(
                if (isSelectionMode) {
                    Modifier.bouncyClick(onClick = onClick)
                } else {
                    Modifier.bouncyCombinedClick(
                        onClick = onClick,
                        onLongClick = onLongClick
                    )
                }
            )
    ) {
        val context = LocalContext.current
        val imageRequest = remember(item.uri) {
            ImageRequest.Builder(context)
                .data(item.uri)
                .crossfade(100)
                .size(300)
                .precision(coil.size.Precision.INEXACT)
                .memoryCacheKey("${item.uri}_thumb")
                .diskCacheKey("${item.uri}_thumb")
                .build()
        }

        AsyncImage(
            model = imageRequest,
            contentDescription = item.name,
            modifier = Modifier.fillMaxSize().then(
                if (isSelected) Modifier.padding(12.dp).clip(RoundedCornerShape(12.dp)) else Modifier
            ),
            contentScale = ContentScale.Crop
        )

        if (isSelected) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White.copy(alpha = 0.3f))
            )
            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.TopStart)
                    .background(MaterialTheme.colorScheme.primary, shape = CircleShape)
                    .padding(2.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Selected",
                    modifier = Modifier.size(16.dp),
                    tint = Color.White
                )
            }
        }

        if (item.isVideo && !isSelected) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(4.dp)
                    .background(Color.Black.copy(alpha = 0.5f), shape = CircleShape)
                    .padding(2.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Video",
                    modifier = Modifier.size(16.dp),
                    tint = Color.White
                )
            }
        }

        if (item.isFavorite && !isSelected) {
            Box(
                modifier = Modifier
                    .align(if (item.isVideo) Alignment.BottomStart else Alignment.BottomEnd)
                    .padding(4.dp)
                    .background(Color.Black.copy(alpha = 0.5f), shape = CircleShape)
                    .padding(2.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Favorite",
                    modifier = Modifier.size(16.dp),
                    tint = Color.Red
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddTagDialog(
    allTagsFlow: StateFlow<List<String>>,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var tagName by remember { mutableStateOf("") }
    val allTags by allTagsFlow.collectAsState()

    val keyboardController = LocalSoftwareKeyboardController.current

    val suggestions = remember(tagName, allTags) {
        if (tagName.isBlank()) emptyList()
        else allTags.asSequence().filter { it.contains(tagName, ignoreCase = true) && !it.equals(tagName, ignoreCase = true) }.take(5).toList()
    }

    AlertDialog(
        onDismissRequest = {
            keyboardController?.hide()
            onDismiss()
        },
        title = { Text("Add Custom Tag") },
        text = {
            Column {
                TextField(
                    value = tagName,
                    onValueChange = { tagName = it },
                    label = { Text("Tag Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                if (suggestions.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Suggestions:", style = MaterialTheme.typography.labelSmall)
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        suggestions.forEach { suggestion ->
                            SuggestionChip(
                                onClick = { tagName = suggestion },
                                label = { Text(suggestion) }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    keyboardController?.hide()
                    onConfirm(tagName)
                },
                enabled = tagName.isNotBlank()
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = {
                keyboardController?.hide()
                onDismiss()
            }) {
                Text("Cancel")
            }
        }
    )
}

package com.tama.gallerynoai.ui.screens

import android.content.Intent
import android.content.IntentSender
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.font.FontWeight
import com.tama.gallerynoai.ui.components.CreateFolderDialog
import com.tama.gallerynoai.ui.components.FolderSelectionDialog
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.tama.gallerynoai.data.model.MediaItem
import com.tama.gallerynoai.data.model.SortType
import com.tama.gallerynoai.ui.components.FastDateScroller
import com.tama.gallerynoai.ui.components.bouncyClick
import com.tama.gallerynoai.ui.components.bouncyCombinedClick
import com.tama.gallerynoai.ui.viewmodel.GalleryItem
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.awaitVerticalTouchSlopOrCancellation
import androidx.compose.foundation.gestures.drag
import com.tama.gallerynoai.ui.components.DateHeader
import com.tama.gallerynoai.ui.components.DragSelectionState
import com.tama.gallerynoai.ui.components.MediaGridItem
import com.tama.gallerynoai.ui.components.SelectionTopAppBar
import com.tama.gallerynoai.ui.components.AddTagDialog
import com.tama.gallerynoai.ui.viewmodel.GalleryViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.roundToInt
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import androidx.paging.LoadState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GalleryScreen(
    viewModel: GalleryViewModel,
    onMediaClick: (MediaItem) -> Unit,
    onSearchClick: () -> Unit,
    onDeleteRequest: (IntentSender) -> Unit = {}
) {
    val pagedItems = viewModel.pagedMediaItems.collectAsLazyPagingItems()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val hasLoadedOnce by viewModel.hasLoadedOnce.collectAsStateWithLifecycle()
    val mediaItems by viewModel.mediaItems.collectAsStateWithLifecycle()
    val sortType by viewModel.sortType.collectAsStateWithLifecycle()
    val selectedIds by viewModel.selectedIds.collectAsStateWithLifecycle()
    val isSelectionMode by viewModel.selectionMode.collectAsStateWithLifecycle()
    val groupedItemsState by viewModel.groupedItems.collectAsStateWithLifecycle()
    val dateFormat by viewModel.dateFormat.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    var showMenu by remember { mutableStateOf(false) }

    val gridState = rememberLazyGridState()
    val coroutineScope = rememberCoroutineScope()

    BackHandler(enabled = isSelectionMode) {
        viewModel.setSelectionMode(false)
    }

    var showFolderSelection by remember { mutableStateOf(false) }
    var showCreateFolder by remember { mutableStateOf(false) }
    var isMoveOperation by remember { mutableStateOf(false) }
    var showBatchTagDialog by remember { mutableStateOf(false) }
    val albums by viewModel.albums.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.scrollToTopTrigger.collect { route ->
            if (route == "photos") {
                gridState.animateScrollToItem(0)
            }
        }
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            if (isSelectionMode) {
                SelectionTopAppBar(
                    selectedCount = selectedIds.size,
                    onClearSelection = { viewModel.setSelectionMode(false) },
                    onDelete = {
                        val selectedUris = mediaItems.filter { selectedIds.contains(it.id) }.map { it.uri }
                        viewModel.clearSelection()
                        viewModel.moveToTrash(selectedUris)?.let { onDeleteRequest(it) }
                    },
                    onShare = {
                        val selectedUris = mediaItems.filter { selectedIds.contains(it.id) }.map { it.uri }
                        if (selectedUris.isNotEmpty()) {
                            val intent = Intent(Intent.ACTION_SEND_MULTIPLE).apply {
                                type = "image/* video/*"
                                putParcelableArrayListExtra(Intent.EXTRA_STREAM, ArrayList(selectedUris))
                                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            }
                            context.startActivity(Intent.createChooser(intent, "Share media"))
                        }
                    },
                    onSelectAll = {
                        viewModel.selectAll(mediaItems)
                    },
                    onFavorite = {
                        val selectedMedia = mediaItems.filter { selectedIds.contains(it.id) }
                        val selectedUris = selectedMedia.map { it.uri }
                        val isAllFavorite = selectedMedia.all { it.isFavorite }
                        viewModel.favoriteMedia(selectedUris, !isAllFavorite)?.let { onDeleteRequest(it) }
                    },
                    onBatchTag = {
                        showBatchTagDialog = true
                    },
                    onCopyToFolder = {
                        isMoveOperation = false
                        showFolderSelection = true
                    },
                    onMoveToFolder = {
                        isMoveOperation = true
                        showFolderSelection = true
                    },
                    scrollBehavior = scrollBehavior,
                    isAllFavorite = remember(selectedIds, mediaItems) {
                        val selectedMedia = mediaItems.filter { selectedIds.contains(it.id) }
                        selectedMedia.isNotEmpty() && selectedMedia.all { it.isFavorite }
                    }
                )
            } else {
                TopAppBar(
                    title = {
                        Text(
                            "Photos",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.headlineMedium
                        )
                    },
                    actions = {
                        TextButton(onClick = { viewModel.setSelectionMode(true) }) {
                            Text("Select")
                        }
                        IconButton(onClick = onSearchClick) {
                            Icon(Icons.Default.Search, contentDescription = "Search")
                        }
                        Box {
                            IconButton(onClick = { showMenu = true }) {
                                Icon(Icons.Default.MoreVert, contentDescription = "Options")
                            }
                            DropdownMenu(
                                expanded = showMenu,
                                onDismissRequest = { showMenu = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Date (Newest)") },
                                    onClick = {
                                        viewModel.setSortType(SortType.DATE_NEWEST)
                                        showMenu = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Date (Oldest)") },
                                    onClick = {
                                        viewModel.setSortType(SortType.DATE_OLDEST)
                                        showMenu = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Size (Largest)") },
                                    onClick = {
                                        viewModel.setSortType(SortType.SIZE_LARGEST)
                                        showMenu = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Size (Smallest)") },
                                    onClick = {
                                        viewModel.setSortType(SortType.SIZE_SMALLEST)
                                        showMenu = false
                                    }
                                )
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
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            val isPagingLoading = pagedItems.loadState.refresh is LoadState.Loading

            if (pagedItems.itemCount == 0) {
                if (isLoading || isPagingLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else if (hasLoadedOnce) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No photos or videos found")
                    }
                }
            } else {
                var dragSelectionState by remember { mutableStateOf<DragSelectionState?>(null) }
                val haptic = LocalHapticFeedback.current

                // Handle Drag Selection Logic
                fun updateDragSelection(currentOffset: Offset) {
                    val startState = dragSelectionState ?: return

                    // Find item under current offset
                    val itemUnderPointer = gridState.layoutInfo.visibleItemsInfo.firstOrNull { item ->
                        val itemTop = item.offset.y.toFloat()
                        val itemBottom = itemTop + item.size.height
                        val itemLeft = item.offset.x.toFloat()
                        val itemRight = itemLeft + item.size.width

                        currentOffset.y >= itemTop && currentOffset.y <= itemBottom &&
                                currentOffset.x >= itemLeft && currentOffset.x <= itemRight
                    }

                    if (itemUnderPointer != null) {
                        val currentMediaItem = pagedItems.peek(itemUnderPointer.index) as? GalleryItem.Media
                        if (currentMediaItem != null) {
                            val currentIndex = itemUnderPointer.index
                            val startIndex = startState.startIndex

                            val minIndex = minOf(startIndex, currentIndex)
                            val maxIndex = maxOf(startIndex, currentIndex)

                            val newSelectedIds = startState.initialSelectedIds.toMutableSet()

                            // Get all media items in the range
                            for (i in minIndex..maxIndex) {
                                val itemInRange = pagedItems.peek(i) as? GalleryItem.Media
                                if (itemInRange != null) {
                                    if (startState.shouldSelect) {
                                        newSelectedIds.add(itemInRange.item.id)
                                    } else {
                                        newSelectedIds.remove(itemInRange.item.id)
                                    }
                                }
                            }

                            if (newSelectedIds != selectedIds) {
                                viewModel.setSelectedIds(newSelectedIds)
                            }
                        }
                    }
                }

                // The Grid
                LazyVerticalGrid(
                    state = gridState,
                    columns = GridCells.Fixed(3),
                    modifier = Modifier
                        .fillMaxSize()
                        // 🔥 PERBAIKAN DI SINI: Menghapus pagedItems.itemCount agar sentuhan tidak restart saat data bertambah
                        .pointerInput(isSelectionMode) {
                            if (!isSelectionMode) return@pointerInput

                            detectDragGestures(
                                onDragStart = { offset ->
                                    val itemUnderPointer = gridState.layoutInfo.visibleItemsInfo.firstOrNull { item ->
                                        offset.y >= item.offset.y && offset.y <= (item.offset.y + item.size.height) &&
                                                offset.x >= item.offset.x && offset.x <= (item.offset.x + item.size.width)
                                    }

                                    if (itemUnderPointer != null) {
                                        val mediaItem = pagedItems.peek(itemUnderPointer.index) as? GalleryItem.Media
                                        if (mediaItem != null) {
                                            val isCurrentlySelected = selectedIds.contains(mediaItem.item.id)
                                            dragSelectionState = DragSelectionState(
                                                startIndex = itemUnderPointer.index,
                                                initialSelectedIds = selectedIds,
                                                shouldSelect = !isCurrentlySelected
                                            )
                                            viewModel.toggleSelection(mediaItem.item.id)
                                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        }
                                    }
                                },
                                onDrag = { change, _ ->
                                    change.consume()
                                    updateDragSelection(change.position)

                                    // Auto-scroll
                                    val viewHeight = gridState.layoutInfo.viewportSize.height
                                    val threshold = 100f
                                    if (change.position.y < threshold) {
                                        coroutineScope.launch {
                                            gridState.scrollBy(-30f)
                                        }
                                    } else if (change.position.y > viewHeight - threshold) {
                                        coroutineScope.launch {
                                            gridState.scrollBy(30f)
                                        }
                                    }
                                },
                                onDragEnd = { dragSelectionState = null },
                                onDragCancel = { dragSelectionState = null }
                            )
                        },
                    contentPadding = PaddingValues(1.dp),
                    horizontalArrangement = Arrangement.spacedBy(1.dp),
                    verticalArrangement = Arrangement.spacedBy(1.dp)
                ) {
                    items(
                        count = pagedItems.itemCount,
                        key = pagedItems.itemKey { item ->
                            when (item) {
                                is GalleryItem.Header -> item.date
                                is GalleryItem.Media -> item.item.id
                            }
                        },
                        span = { index ->
                            val item = pagedItems.peek(index)
                            when (item) {
                                is GalleryItem.Header -> GridItemSpan(maxLineSpan)
                                else -> GridItemSpan(1)
                            }
                        },
                        contentType = pagedItems.itemContentType { item ->
                            when (item) {
                                is GalleryItem.Header -> "header"
                                is GalleryItem.Media -> "media"
                            }
                        }
                    ) { index ->
                        val item = pagedItems[index]
                        when (item) {
                            is GalleryItem.Header -> {
                                DateHeader(date = item.date)
                            }
                            is GalleryItem.Media -> {
                                val isSelected = remember(selectedIds) { selectedIds.contains(item.item.id) }
                                MediaGridItem(
                                    item = item.item,
                                    isSelected = isSelected,
                                    onClick = {
                                        if (isSelectionMode) {
                                            viewModel.toggleSelection(item.item.id)
                                        } else {
                                            onMediaClick(item.item)
                                        }
                                    },
                                    onLongClick = {
                                        if (!isSelectionMode) {
                                            viewModel.toggleSelection(item.item.id)
                                        }
                                    },
                                    isSelectionMode = isSelectionMode
                                )
                            }
                            null -> {
                                Box(modifier = Modifier.aspectRatio(1f).background(MaterialTheme.colorScheme.surfaceVariant))
                            }
                        }
                    }
                }

                // --- Fast Scroll Slider ---
                if (sortType == SortType.DATE_NEWEST || sortType == SortType.DATE_OLDEST) {
                    FastDateScroller(
                        gridState = gridState,
                        items = pagedItems.itemSnapshotList.items,
                        dateFormat = dateFormat
                    )
                }
            }
        }
    }
    if (showFolderSelection) {
        FolderSelectionDialog(
            albums = albums,
            onDismiss = { showFolderSelection = false },
            onFolderSelected = { albumId ->
                showFolderSelection = false
                val selectedUris = mediaItems.filter { selectedIds.contains(it.id) }.map { it.uri }
                val targetAlbum = albums.find { it.id == albumId }
                val targetPath = targetAlbum?.relativePath ?: "Pictures/${targetAlbum?.name ?: ""}"

                if (isMoveOperation) {
                    viewModel.moveMedia(selectedUris, targetPath)?.let { onDeleteRequest(it) }
                } else {
                    viewModel.copyMedia(selectedUris, targetPath)
                }
            },
            onCreateNewFolder = {
                showCreateFolder = true
            }
        )
    }

    if (showCreateFolder) {
        CreateFolderDialog(
            onDismiss = { showCreateFolder = false },
            onConfirm = { folderName ->
                showCreateFolder = false
                showFolderSelection = false
                val selectedUris = mediaItems.filter { selectedIds.contains(it.id) }.map { it.uri }
                if (isMoveOperation) {
                    viewModel.moveMedia(selectedUris, "Pictures/$folderName")?.let { onDeleteRequest(it) }
                } else {
                    viewModel.copyMedia(selectedUris, "Pictures/$folderName")
                }
            }
        )
    }

    if (showBatchTagDialog) {
        AddTagDialog(
            allTagsFlow = viewModel.allUniqueTags,
            onDismiss = { showBatchTagDialog = false },
            onConfirm = { tag ->
                showBatchTagDialog = false
                viewModel.batchUpdateMediaMetadata(selectedIds.toList(), tag)
            }
        )
    }
}

package com.tama.gallerynoai.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.IntSize
import com.tama.gallerynoai.data.model.MediaItem
import com.tama.gallerynoai.ui.components.FastDateScroller
import com.tama.gallerynoai.ui.components.MediaGridItem
import com.tama.gallerynoai.ui.components.bouncyClick
import com.tama.gallerynoai.ui.theme.*
import com.tama.gallerynoai.ui.viewmodel.GalleryItem
import com.tama.gallerynoai.ui.viewmodel.GalleryViewModel
import com.tama.gallerynoai.data.model.SortType

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SearchScreen(
    viewModel: GalleryViewModel,
    onMediaClick: (MediaItem) -> Unit
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()
    val isSearching by viewModel.isSearching.collectAsState()
    val recentSearches by viewModel.recentSearches.collectAsState()
    val searchSuggestions by viewModel.searchSuggestions.collectAsState()
    val allTags by viewModel.allUniqueTags.collectAsState()
    val dateFormat by viewModel.dateFormat.collectAsState()
    val sortType by viewModel.sortType.collectAsState()

    var isSearchBarActive by remember { mutableStateOf(value = false) }
    val gridState = rememberLazyGridState()
    val keyboardController = LocalSoftwareKeyboardController.current
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    BackHandler(enabled = isSearchBarActive || searchQuery.isNotEmpty()) {
        if (isSearchBarActive) {
            isSearchBarActive = false
        } else if (searchQuery.isNotEmpty()) {
            viewModel.onSearchQueryChange("")
        }
    }

    val searchAnimationSpec = tween<Float>(durationMillis = 400, easing = FastOutSlowInEasing)
    val IntSizeAnimationSpec = tween<IntSize>(durationMillis = 400, easing = FastOutSlowInEasing)
    val searchDpAnimationSpec = tween<Dp>(durationMillis = 400, easing = FastOutSlowInEasing)
    val searchColorAnimationSpec = tween<Color>(durationMillis = 400, easing = FastOutSlowInEasing)

    val searchBarPaddingHorizontal by animateDpAsState(
        targetValue = if (isSearchBarActive) 0.dp else 16.dp,
        animationSpec = searchDpAnimationSpec,
        label = "SearchBarHorizontalPadding"
    )
    val searchBarPaddingBottom by animateDpAsState(
        targetValue = if (isSearchBarActive) 0.dp else 8.dp,
        animationSpec = searchDpAnimationSpec,
        label = "SearchBarBottomPadding"
    )
    val searchBarContainerColor by animateColorAsState(
        targetValue = if (isSearchBarActive) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        animationSpec = searchColorAnimationSpec,
        label = "SearchBarContainerColor"
    )

    LaunchedEffect(Unit) {
        viewModel.scrollToTopTrigger.collect { route ->
            if (route == "search") {
                if (searchQuery.isNotEmpty()) {
                    if (searchResults.isNotEmpty() && gridState.firstVisibleItemIndex > 0) {
                        gridState.animateScrollToItem(0)
                    } else {
                        viewModel.onSearchQueryChange("")
                    }
                } else {
                    gridState.animateScrollToItem(0)
                }
            }
        }
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            Column(
                modifier = Modifier.background(MaterialTheme.colorScheme.surface)
            ) {
                AnimatedVisibility(
                    visible = !isSearchBarActive,
                    enter = fadeIn(searchAnimationSpec) + expandVertically(IntSizeAnimationSpec),
                    exit = fadeOut(searchAnimationSpec) + shrinkVertically(IntSizeAnimationSpec)
                ) {
                    TopAppBar(
                        title = {
                            Text(
                                text = "Search",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.headlineMedium
                            )
                        },
                        windowInsets = WindowInsets(0, 0, 0, 0),
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color.Transparent,
                            scrolledContainerColor = Color.Transparent
                        )
                    )
                }

                SearchBar(
                    query = searchQuery,
                    onQueryChange = { viewModel.onSearchQueryChange(it) },
                    onSearch = {
                        viewModel.saveSearch(it)
                        isSearchBarActive = false
                        keyboardController?.hide()
                    },
                    active = isSearchBarActive,
                    onActiveChange = { isSearchBarActive = it },
                    placeholder = { Text("Search photos, videos, tags...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.onSearchQueryChange("") }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear")
                            }
                        }
                    },
                    colors = SearchBarDefaults.colors(
                        containerColor = searchBarContainerColor,
                    ),
                    windowInsets = WindowInsets(0, 0, 0, 0),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = searchBarPaddingHorizontal)
                        .padding(bottom = searchBarPaddingBottom)
                ) {
                    if (searchQuery.isEmpty()) {
                        RecentSearchesList(
                            recentSearches = recentSearches,
                            onSearchClick = {
                                viewModel.onSearchQueryChange(it)
                                isSearchBarActive = false
                            },
                            onDeleteClick = { viewModel.deleteRecentSearch(it) },
                            onClearAll = { viewModel.clearSearchHistory() }
                        )
                    } else {
                        SuggestionSection(
                            suggestions = searchSuggestions,
                            onSuggestionClick = {
                                viewModel.onSearchQueryChange(it)
                                viewModel.saveSearch(it)
                                isSearchBarActive = false
                                keyboardController?.hide()
                            }
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Box(modifier = Modifier.weight(1f)) {
                AnimatedContent(
                    targetState = searchQuery.isNotEmpty(),
                    label = "SearchContentAnimation",
                    transitionSpec = {
                        fadeIn(searchAnimationSpec) togetherWith fadeOut(searchAnimationSpec)
                    }
                ) { isShowingResults ->
                    if (isShowingResults) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            SearchResultsGrid(
                                results = searchResults,
                                isSearching = isSearching,
                                query = searchQuery,
                                gridState = gridState,
                                onMediaClick = onMediaClick
                            )

                            if (searchQuery.isNotEmpty() && (sortType == SortType.DATE_NEWEST || sortType == SortType.DATE_OLDEST)) {
                                val galleryItems = remember(searchResults) {
                                    searchResults.map { GalleryItem.Media(it) }
                                }
                                FastDateScroller(
                                    gridState = gridState,
                                    items = galleryItems,
                                    dateFormat = dateFormat
                                )
                            }
                        }
                    } else {
                        SearchHome(
                            recentSearches = recentSearches,
                            tags = allTags,
                            onSearchClick = { viewModel.onSearchQueryChange(it) },
                            onDeleteRecent = { viewModel.deleteRecentSearch(it) },
                            onClearRecent = { viewModel.clearSearchHistory() }
                        )
                    }
                }

                if (isSearchBarActive) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.01f))
                            .clickable(enabled = true, onClick = { isSearchBarActive = false })
                    )
                }
            }
        }
    }
}

@Composable
fun SearchHome(
    recentSearches: List<String>,
    tags: List<String>,
    onSearchClick: (String) -> Unit,
    onDeleteRecent: (String) -> Unit,
    onClearRecent: () -> Unit
) {
    androidx.compose.foundation.lazy.LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        // Recent Searches (Horizontal Chips)
        if (recentSearches.isNotEmpty()) {
            item {
                SearchSection(
                    title = "Recent",
                    trailingText = "Clear All",
                    onTrailingClick = onClearRecent
                ) {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(recentSearches.take(10), key = { it }) { query ->
                            InputChip(
                                modifier = Modifier.animateItem(),
                                selected = false,
                                onClick = { onSearchClick(query) },
                                label = { Text(query) },
                                trailingIcon = {
                                    Icon(
                                        Icons.Default.Close,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp).clickable { onDeleteRecent(query) }
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }

        // Custom Tags
        if (tags.isNotEmpty()) {
            item {
                SearchSection(title = "Your Tags") {
                    @OptIn(ExperimentalLayoutApi::class)
                    FlowRow(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        tags.take(15).forEach { tag ->
                            SuggestionChip(
                                onClick = { onSearchClick(tag) },
                                label = { Text(tag) }
                            )
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(48.dp))
            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Search for filenames or tags.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun SearchResultsGrid(
    results: List<MediaItem>,
    isSearching: Boolean,
    query: String,
    gridState: androidx.compose.foundation.lazy.grid.LazyGridState,
    onMediaClick: (MediaItem) -> Unit,
) {
    if (isSearching && results.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else if (!isSearching && results.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize().padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    Icons.Default.SearchOff,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.outline
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "No results found for \"$query\"",
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center
                )
            }
        }
    } else {
        LazyVerticalGrid(
            state = gridState,
            columns = GridCells.Fixed(3),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(1.dp),
            horizontalArrangement = Arrangement.spacedBy(1.dp),
            verticalArrangement = Arrangement.spacedBy(1.dp)
        ) {
            items(results, key = { it.id }) { item ->
                MediaGridItem(item = item, onClick = { onMediaClick(item) })
            }
        }
    }
}

@Composable
fun SearchSection(
    title: String,
    trailingText: String? = null,
    onTrailingClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    Column(modifier = Modifier.padding(vertical = 12.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            if (trailingText != null) {
                TextButton(onClick = { onTrailingClick?.invoke() }) {
                    Text(trailingText, style = MaterialTheme.typography.labelLarge)
                }
            }
        }
        content()
    }
}

@Composable
fun RecentSearchesList(
    recentSearches: List<String>,
    onSearchClick: (String) -> Unit,
    onDeleteClick: (String) -> Unit,
    onClearAll: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Recent Searches",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary
            )
            TextButton(onClick = onClearAll) {
                Text("Clear All")
            }
        }

        recentSearches.take(8).forEach { query ->
            ListItem(
                headlineContent = { Text(query) },
                leadingContent = { Icon(Icons.Default.History, contentDescription = null) },
                trailingContent = {
                    IconButton(onClick = { onDeleteClick(query) }) {
                        Icon(Icons.Default.Close, contentDescription = "Remove", modifier = Modifier.size(18.dp))
                    }
                },
                modifier = Modifier.clickable { onSearchClick(query) }
            )
        }
    }
}

@Composable
fun SuggestionSection(
    suggestions: List<String>,
    onSuggestionClick: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        suggestions.forEach { suggestion ->
            ListItem(
                headlineContent = { Text(suggestion) },
                leadingContent = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingContent = { Icon(Icons.Default.NorthWest, contentDescription = null, modifier = Modifier.size(18.dp)) },
                modifier = Modifier.clickable { onSuggestionClick(suggestion) }
            )
        }
    }
}

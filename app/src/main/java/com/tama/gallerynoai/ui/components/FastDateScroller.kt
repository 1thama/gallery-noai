package com.tama.gallerynoai.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.tama.gallerynoai.ui.viewmodel.GalleryItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun FastDateScroller(
    gridState: LazyGridState,
    items: List<GalleryItem>,
    dateFormat: String = "MMM yyyy",
    modifier: Modifier = Modifier
) {
    var isDraggingSlider by remember { mutableStateOf(false) }
    var isScrolling by remember { mutableStateOf(false) }
    var dragOffsetPx by remember { mutableStateOf<Float?>(null) }

    val showSlider by remember { derivedStateOf { isDraggingSlider || isScrolling } }
    val coroutineScope = rememberCoroutineScope()
    val density = LocalDensity.current
    val haptic = LocalHapticFeedback.current

    // 🔥 PERBAIKAN: Menyimpan ukuran item terbaru tanpa memicu restart pada pointerInput
    val currentItemSize by rememberUpdatedState(items.size)

    LaunchedEffect(gridState.isScrollInProgress) {
        isScrolling = gridState.isScrollInProgress
        if (!gridState.isScrollInProgress) {
            delay(1500)
            isScrolling = false
        }
    }

    val scrollProgress by remember(items.size) {
        derivedStateOf {
            val layoutInfo = gridState.layoutInfo
            val totalItemsCount = items.size
            if (totalItemsCount == 0 || layoutInfo.visibleItemsInfo.isEmpty()) 0f else {
                val firstVisibleItemIndex = layoutInfo.visibleItemsInfo.first().index
                val lastVisibleItemIndex = layoutInfo.visibleItemsInfo.last().index
                val visibleItemsCount = lastVisibleItemIndex - firstVisibleItemIndex + 1

                if (lastVisibleItemIndex >= totalItemsCount - 1) 1f
                else firstVisibleItemIndex.toFloat() / (totalItemsCount - visibleItemsCount).coerceAtLeast(1).toFloat()
            }
        }
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .zIndex(100f)
    ) {
        val maxHeightPx = constraints.maxHeight.toFloat()

        val handleHeight = if (isDraggingSlider) 48.dp else 40.dp
        val handleWidth = if (isDraggingSlider) 10.dp else 6.dp
        val handleHeightPx = with(density) { handleHeight.toPx() }

        // 🔥 PERBAIKAN: Gunakan updated state agar pointerInput tidak restart saat nilai ini berubah
        val currentMaxHeightPx by rememberUpdatedState(maxHeightPx)
        val currentHandleHeightPx by rememberUpdatedState(handleHeightPx)

        val animatedHandleWidth by animateDpAsState(handleWidth, label = "width")
        val animatedHandleHeight by animateDpAsState(handleHeight, label = "height")

        val currentYOffsetPx = if (isDraggingSlider && dragOffsetPx != null) {
            dragOffsetPx!!.coerceIn(0f, (maxHeightPx - handleHeightPx).coerceAtLeast(0f))
        } else {
            (scrollProgress * (maxHeightPx - handleHeightPx)).coerceIn(0f, (maxHeightPx - handleHeightPx).coerceAtLeast(0f))
        }

        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(40.dp)
                .align(Alignment.TopEnd)
                // 🔥 PERBAIKAN: Gunakan key yang stabil (Unit) agar drag tidak terputus
                .pointerInput(Unit) {
                    detectVerticalDragGestures(
                        onDragStart = {
                            isDraggingSlider = true
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            dragOffsetPx = (scrollProgress * (currentMaxHeightPx - currentHandleHeightPx))
                                .coerceIn(0f, (currentMaxHeightPx - currentHandleHeightPx).coerceAtLeast(0f))
                        },
                        onDragEnd = {
                            isDraggingSlider = false
                            dragOffsetPx = null
                        },
                        onDragCancel = {
                            isDraggingSlider = false
                            dragOffsetPx = null
                        },
                        onVerticalDrag = { change, dragAmount ->
                            change.consume()
                            val newOffset = (dragOffsetPx!! + dragAmount)
                                .coerceIn(0f, (currentMaxHeightPx - currentHandleHeightPx).coerceAtLeast(0f))
                            dragOffsetPx = newOffset

                            val progress = newOffset / (currentMaxHeightPx - currentHandleHeightPx).coerceAtLeast(1f)
                            // Menggunakan currentItemSize yang selalu update tanpa memutus sentuhan
                            val targetIndex = (progress * (currentItemSize - 1)).toInt().coerceIn(0, (currentItemSize - 1).coerceAtLeast(0))

                            coroutineScope.launch {
                                gridState.scrollToItem(targetIndex)
                            }
                        }
                    )
                }
        )

        AnimatedVisibility(
            visible = showSlider,
            modifier = Modifier.align(Alignment.TopEnd),
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .offset { IntOffset(0, currentYOffsetPx.roundToInt()) }
                    .padding(end = 6.dp)
                    .size(animatedHandleWidth, animatedHandleHeight)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
            )
        }

        AnimatedVisibility(
            visible = isDraggingSlider,
            modifier = Modifier.align(Alignment.TopEnd),
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            val bubbleProgress = if (dragOffsetPx != null) {
                dragOffsetPx!! / (maxHeightPx - handleHeightPx).coerceAtLeast(1f)
            } else {
                scrollProgress
            }
            val currentIndex = (bubbleProgress * (currentItemSize - 1)).toInt().coerceIn(0, (currentItemSize - 1).coerceAtLeast(0))

            val timestamp = remember(currentIndex, items) {
                var foundTimestamp = 0L
                for (i in currentIndex downTo 0) {
                    val item = items.getOrNull(i)
                    if (item is GalleryItem.Header) {
                        foundTimestamp = item.timestamp
                        break
                    }
                }
                if (foundTimestamp == 0L) {
                    val currentItem = items.getOrNull(currentIndex)
                    if (currentItem is GalleryItem.Media) {
                        foundTimestamp = currentItem.item.dateModified
                    }
                }
                foundTimestamp
            }

            val bubbleText = remember(timestamp, dateFormat) {
                if (timestamp > 0) {
                    try {
                        DateTimeFormatter.ofPattern(dateFormat, Locale.getDefault())
                            .withZone(ZoneId.systemDefault())
                            .format(Instant.ofEpochSecond(timestamp))
                            .uppercase()
                    } catch (e: Exception) {
                        try {
                            DateTimeFormatter.ofPattern("MMM yyyy", Locale.getDefault())
                                .withZone(ZoneId.systemDefault())
                                .format(Instant.ofEpochSecond(timestamp))
                                .uppercase()
                        } catch (e2: Exception) {
                            ""
                        }
                    }
                } else ""
            }

            if (bubbleText.isNotEmpty()) {
                Surface(
                    modifier = Modifier
                        .offset {
                            IntOffset(
                                x = (-76).dp.toPx().toInt(),
                                y = (currentYOffsetPx + (handleHeightPx / 2) - 18.dp.toPx()).roundToInt()
                            )
                        }
                        .size(width = 92.dp, height = 36.dp),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary,
                    tonalElevation = 8.dp,
                    shadowElevation = 6.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = bubbleText,
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontSize = 13.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 0.8.sp
                            ),
                            color = MaterialTheme.colorScheme.onPrimary,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

# Rewrite FastDateScroller to Match Google Photos Behavior

Rewrite `FastDateScroller.kt` to provide an immediate, continuous, and non-leaking fast scroll experience identical to Google Photos.

## Proposed Changes

### UI Components

#### [FastDateScroller.kt](file:///D:/Users/ktb02865/AndroidStudioProjects/Gallery No AI/app/src/main/java/com/tama/gallerynoai/ui/components/FastDateScroller.kt)

- **Gesture Implementation**: Replace `detectVerticalDragGestures` with a manual `awaitPointerEventScope` + `awaitFirstDown` loop.
    - Consume the "down" event immediately to prevent the parent `LazyVerticalGrid` from capturing the gesture.
    - Trigger haptic feedback on touch down.
    - Calculate `progress = currentY / containerHeight` and scroll the grid in real-time.
- **State Management**:
    - Use a single `LaunchedEffect` to manage `isScrolling` with a 1500ms delay after both dragging and grid scrolling stop.
    - Use `dragProgress` to track the current position during a drag.
- **Touch Zone**: Increase the width of the touch-sensitive area to `48.dp`.
- **Visibility**:
    - Handle appears immediately on touch down (`isDraggingSlider`).
    - Handle stays visible for 1500ms after interaction ends.
    - Date bubble appears instantly during drag and disappears on drag end.
- **Refined Calculations**:
    - `targetIndex = (progress * totalItems).toInt().coerceIn(0, totalItems - 1)`
    - Handle position (`currentYOffsetPx`) calculated from `dragProgress` during drag or `scrollProgress` during normal scroll.

```kotlin
// Example of the new pointerInput structure
.pointerInput(Unit) {
    awaitPointerEventScope {
        while (true) {
            val down = awaitFirstDown(requireUnconsumed = false)
            if (down.position.x >= size.width - 48.dp.toPx()) {
                down.consume()
                isDraggingSlider = true
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)

                fun update(y: Float) {
                    val progress = (y / size.height).coerceIn(0f, 1f)
                    dragProgress = progress
                    if (currentItems.isNotEmpty()) {
                        val targetIndex = (progress * currentItems.size).toInt()
                            .coerceIn(0, currentItems.size - 1)
                        coroutineScope.launch {
                            gridState.scrollToItem(targetIndex)
                        }
                    }
                }

                update(down.position.y)

                verticalDrag(down.id) { change ->
                    change.consume()
                    update(change.position.y)
                }

                isDraggingSlider = false
                dragProgress = null
            }
        }
    }
}
```

## Verification Plan

### Automated Tests
- No specific automated tests exist for this UI component. Verification will be manual.

### Manual Verification
1. **Immediate Appearance**: Verify that touching the right edge makes the handle appear instantly without any delay or long-press.
2. **Continuous Scroll**: Verify that a single swipe from top to bottom scrolls the list from the first item to the last item.
3. **Real-time Feedback**: Verify that the list scrolls smoothly as the finger moves.
4. **No Gesture Leak**: Verify that dragging the handle does NOT cause the underlying grid to scroll independently (the grid should only follow the handle).
5. **Auto-hide**: Verify that the handle fades out 1500ms after the list stops scrolling or the finger is lifted.
6. **Date Bubble**: Verify the date bubble appears only during drag and correctly shows the date for the current position.
7. **Touch Zone**: Verify that the touch zone is wide enough (48dp) and correctly aligned to the right edge.

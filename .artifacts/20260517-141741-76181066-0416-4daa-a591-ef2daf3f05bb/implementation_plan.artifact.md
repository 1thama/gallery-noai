# Adjust Video Controls Position

Raise the video playback controls (progress bar, duration, settings, and fullscreen) so they appear above the bottom action bar (Favorite, Edit, Info, Delete) instead of overlapping with it.

## Proposed Changes

### [MediaDetailScreen.kt](file:///D:/Users/ktb02865/AndroidStudioProjects/gallery/app/src/main/java/com/example/gallery/ui/screens/MediaDetailScreen.kt)

- Modify `VideoPlayerControls` to add bottom padding or adjust its vertical alignment.
- Specifically, the `Column` containing the bottom controls (duration row and slider) will have its bottom padding increased to clear the bottom action bar's height.
- Since the bottom action bar is part of the `MediaDetailScreen` overlay and not the `VideoPage`, I will pass an additional `bottomPadding` parameter to `VideoPlayerControls`.

#### [MediaDetailScreen.kt](file:///D:/Users/ktb02865/AndroidStudioProjects/gallery/app/src/main/java/com/example/gallery/ui/screens/MediaDetailScreen.kt)

```kotlin
@Composable
fun VideoPlayerControls(
    // ... other params
    bottomPadding: Dp = 0.dp // New parameter
) {
    // ...
    Column(
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .padding(horizontal = 16.dp)
            .padding(bottom = bottomPadding + 24.dp) // Offset by bottom bar height
            .fillMaxWidth()
    ) {
        // Duration, Settings, Fullscreen row
        // Slider
    }
}
```

## Verification Plan

### Automated Tests
- Build the project to ensure no syntax errors.
  `gradlew assembleDebug`

### Manual Verification
1. Open a video in the gallery.
2. Ensure both the video controls and the bottom action bar are visible.
3. Verify that the video progress bar and duration text are clearly visible above the "Favorite", "Edit", etc. icons.
4. Check that the UI still looks correct when the bottom bar is hidden (e.g., in manual fullscreen).

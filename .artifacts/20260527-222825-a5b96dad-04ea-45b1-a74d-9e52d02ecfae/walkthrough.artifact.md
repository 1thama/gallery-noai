# Walkthrough - Improve AI Analysis Progress Bar and Move to Settings

I have improved the AI analysis progress bar and moved it from the Search screen to the Settings screen. The progress bar now shows real progress from the background indexing worker, including the number of items processed.

## Changes

### Data & Background Workers

#### [MediaIndexingWorker.kt](file:///D:/Users/ktb02865/AndroidStudioProjects/gallery/app/src/main/java/com/example/gallery/data/worker/MediaIndexingWorker.kt)

- Added progress reporting using `setProgress()`.
- The worker now calculates and emits `progress` (Float), `indexedCount` (Int), and `totalCount` (Int).
- Progress is updated for every item processed to ensure a smooth UI experience.

### UI & ViewModels

#### [SettingsViewModel.kt](file:///D:/Users/ktb02865/AndroidStudioProjects/gallery/app/src/main/java/com/example/gallery/ui/viewmodel/SettingsViewModel.kt)

- Now observes `WorkManager` progress for `MediaIndexingWork`.
- Exposes `indexingProgress` as a `StateFlow<IndexingState>`, which includes progress percentage, current count, and total count.
- Added a check to show the progress bar only when the worker is actively processing items.

#### [GalleryViewModelFactory.kt](file:///D:/Users/ktb02865/AndroidStudioProjects/gallery/app/src/main/java/com/example/gallery/ui/viewmodel/GalleryViewModelFactory.kt)

- Updated to pass `MediaRepository` to `SettingsViewModel`.

#### [GalleryViewModel.kt](file:///D:/Users/ktb02865/AndroidStudioProjects/gallery/app/src/main/java/com/example/gallery/ui/viewmodel/GalleryViewModel.kt)

- Removed the old, unused `indexingProgress` state.

### Screens

#### [SettingsScreen.kt](file:///D:/Users/ktb02865/AndroidStudioProjects/gallery/app/src/main/java/com/example/gallery/ui/screens/SettingsScreen.kt)

- Added `AnalysisProgressSection` under the "AI Image Analysis" setting.
- The new UI is animated (slides in/out) and shows:
    - An "AutoAwesome" icon.
    - "Analyzing gallery..." status text.
    - A counter (e.g., "42 / 100").
    - A smooth progress bar.

#### [SearchScreen.kt](file:///D:/Users/ktb02865/AndroidStudioProjects/gallery/app/src/main/java/com/example/gallery/ui/screens/SearchScreen.kt)

- Removed the old progress bar implementation.

## Verification Results

### Manual Verification
1.  **Indexing Progress**: Verified that when AI analysis is enabled and the background worker starts, a progress bar appears in the Settings screen.
2.  **Accuracy**: The progress bar correctly shows the number of processed items out of the total.
3.  **UI/UX**: The progress bar is located in the Settings menu as requested and no longer appears in the Search screen.
4.  **Auto-hide**: Verified that the progress bar disappears automatically when indexing is complete.

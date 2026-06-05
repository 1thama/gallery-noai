# Improve AI Analysis Progress Bar and Move to Settings

The goal is to fix the AI analysis progress bar (which currently doesn't show progress) and move it from `SearchScreen.kt` to `SettingsScreen.kt`. The progress bar will be more informative, showing the number of items processed and the total count.

## Proposed Changes

### [Data / Worker]

#### [MediaIndexingWorker.kt](file:///D:/Users/ktb02865/AndroidStudioProjects/gallery/app/src/main/java/com/example/gallery/data/worker/MediaIndexingWorker.kt)

- Update `doWork()` to report progress using `setProgress()`.
- Calculate progress based on the number of items processed vs total items to index.
- Include `progress`, `indexedCount`, and `totalCount` in the progress data.

---

### [UI / ViewModel]

#### [GalleryViewModelFactory.kt](file:///D:/Users/ktb02865/AndroidStudioProjects/gallery/app/src/main/java/com/example/gallery/ui/viewmodel/GalleryViewModelFactory.kt)

- Pass `repository` to `SettingsViewModel` constructor.

#### [SettingsViewModel.kt](file:///D:/Users/ktb02865/AndroidStudioProjects/gallery/app/src/main/java/com/example/gallery/ui/viewmodel/SettingsViewModel.kt)

- Add `repository` to the constructor.
- Add `indexingProgress` StateFlow to observe `WorkManager` progress for `MediaIndexingWork`.
- Parse `WorkInfo` progress data to update the UI state.
- Create a data class `IndexingState` to hold `progress`, `current`, `total`, and `isWorking`.
- **Update**: Ensure `isWorking` is true even in `ENQUEUED` state so the user knows it's pending.
- **New**: Add `triggerIndexing()` to manually start indexing when AI is enabled.

#### [GalleryViewModel.kt](file:///D:/Users/ktb02865/AndroidStudioProjects/gallery/app/src/main/java/com/example/gallery/ui/viewmodel/GalleryViewModel.kt)

- Remove unused `_indexingProgress` and `indexingProgress`.
- **Update**: Relax `WorkManager` constraints (remove charging/idle) so it runs more reliably for the user.

---

### [UI / Screens]

#### [SettingsScreen.kt](file:///D:/Users/ktb02865/AndroidStudioProjects/gallery/app/src/main/java/com/example/gallery/ui/screens/SettingsScreen.kt)

- Add `AnalysisProgressSection` composable (improved version of the one from SearchScreen).
- Integrate `AnalysisProgressSection` into the `SettingsScreen` column, specifically under the "AI Image Analysis" setting.
- The progress bar will show "Analyzing... X of Y items (Z%)".

#### [SearchScreen.kt](file:///D:/Users/ktb02865/AndroidStudioProjects/gallery/app/src/main/java/com/example/gallery/ui/screens/SearchScreen.kt)

- Remove `IndexingProgressSection` function and its usage.

---

## Verification Plan

### Automated Tests
- No new automated tests planned, as this is primarily UI and background worker interaction.

### Manual Verification
1.  **Trigger Indexing**:
    - Open Settings.
    - Enable "AI Image Analysis" if disabled.
    - Observe the progress bar appearing in Settings if there are items to index.
    - Verify the progress bar updates as items are processed.
    - Verify the text shows "Analyzing X of Y items".
2.  **Verify Removal**:
    - Open Search screen.
    - Verify the old progress bar is no longer there.
3.  **Completion**:
    - Verify the progress bar disappears when indexing is complete.

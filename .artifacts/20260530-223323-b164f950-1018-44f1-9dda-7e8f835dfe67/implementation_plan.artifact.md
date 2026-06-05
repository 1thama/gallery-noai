# Implementation Plan - Analysis Indicator and Crash Handling (Settings Screen)

Add a UI indicator with animation to show the progress of gallery analysis in the Settings screen, and handle cases where the analysis fails (crashes) with a retry option.

## Proposed Changes

### ViewModel

#### [SettingsViewModel.kt](file:///D:/Users/ktb02865/AndroidStudioProjects/gallery/app/src/main/java/com/example/gallery/ui/viewmodel/SettingsViewModel.kt)

- Update `IndexingState` data class to include `isError: Boolean`.
- Update `indexingProgress` logic to detect `WorkInfo.State.FAILED` and set `isError`.
- Add `retryIndexing()` function to manually trigger `MediaIndexingWorker` again.

### UI Components

#### [SettingsScreen.kt](file:///D:/Users/ktb02865/AndroidStudioProjects/gallery/app/src/main/java/com/example/gallery/ui/screens/SettingsScreen.kt)

- Modify `AnalysisProgressSection` to:
    - Show an "Analyzing" animation (pulsing text or infinite progress animation) when `isWorking` is true.
    - Show an error state (red text/icon) when `isError` is true.
    - Add a "Retry" button that calls `viewModel.retryIndexing()` when an error occurs or when the user wants to force a re-scan.
- Add a subtle pulsing effect to the "Analyzing gallery..." text using `InfiniteTransition`.

---

## Verification Plan

### Automated Tests
- No existing tests for `SettingsViewModel` found. I will focus on manual verification.

### Manual Verification
- **Run the app** and observe the progress in Settings.
- **Simulate a crash**:
    1. Temporarily modify `MediaIndexingWorker.kt` to throw an exception immediately.
    2. Observe the error indicator in Settings.
- **Test Retry**:
    1. Revert the crash modification.
    2. Click "Retry" in Settings.
    3. Verify indexing starts again and progress updates.
- **Check Animation**: Verify the "Analyzing" text has a subtle pulsing animation.

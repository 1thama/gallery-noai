# Implementation Plan - Completing Features and Optimizations

Based on the research, the features mentioned (Paging 3 & Room, Slideshow, Google Cast) are partially implemented. This plan aims to complete the integration and ensure everything works as described.

## User Review Required

- **Metadata Migration**: I will move AI labels and custom tags from JSON to Room. The `MetadataManager` (JSON) will be removed after migration.
- **Google Cast**: I will integrate the existing `CastHelper` into `MediaDetailScreen` to enable actual casting.

## Proposed Changes

### [Data Layer]

Complete the Room integration by ensuring it stores all metadata and removing JSON redundancy.

#### [MediaRepository.kt](file:///D:/Users/ktb02865/AndroidStudioProjects/gallery/app/src/main/java/com/example/gallery/data/repository/MediaRepository.kt)
- Add a function to update AI labels and metadata directly in Room.
- Update `syncMediaStoreWithRoom` if needed.

#### [GalleryViewModel.kt](file:///D:/Users/ktb02865/AndroidStudioProjects/gallery/app/src/main/java/com/example/gallery/ui/viewmodel/GalleryViewModel.kt)
- Update `startIndexing` and `updateMediaMetadata` to save to Room via `MediaRepository` instead of `MetadataManager`.
- Remove dependency on `MetadataManager`.

#### [DELETE] [MetadataManager.kt](file:///D:/Users/ktb02865/AndroidStudioProjects/gallery/app/src/main/java/com/example/gallery/data/local/MetadataManager.kt)
- Remove the JSON-based metadata storage.

---

### [UI Layer]

Enable Google Cast functionality and optimize media detail screen.

#### [MediaDetailScreen.kt](file:///D:/Users/ktb02865/AndroidStudioProjects/gallery/app/src/main/java/com/example/gallery/ui/screens/MediaDetailScreen.kt)
- Instantiate `CastHelper`.
- Use `LaunchedEffect(pagerState.currentPage)` to call `castHelper.loadMedia(currentItem)` whenever the user swipes.

---

## Verification Plan

### Automated Tests
- I will run existing build to ensure no regressions.
- `gradlew assembleDebug`

### Manual Verification
- **Room & Paging**: Check if AI labels persist after app restart (showing they are in Room, not just memory).
- **Slideshow**: Verify 3s auto-scroll.
- **Google Cast**: Verify the Cast button is functional (though actual casting requires physical device, I can verify the code triggers the `loadMedia` call).

# Implementation Plan - Persisting AI Indexing Results

The goal is to prevent the app from re-indexing all media items every time it's opened. We will use Room database to store the AI analysis results (labels, OCR text, and dominant colors) and only index new or modified items.

## User Review Required

> [!IMPORTANT]
> This change introduces Room database to the project. This will increase the APK size slightly but significantly improve performance and user experience by avoiding redundant indexing.

## Proposed Changes

### Build Configuration

#### [libs.versions.toml](file:///D:/Users/ktb02865/AndroidStudioProjects/gallery/gradle/libs.versions.toml)
- Add Room and KSP versions and library definitions.

#### [app/build.gradle.kts](file:///D:/Users/ktb02865/AndroidStudioProjects/gallery/app/build.gradle.kts)
- Apply KSP plugin.
- Add Room dependencies (`room-runtime`, `room-ktx`, `room-compiler`).

---

### Data Layer

#### [NEW] [MediaMetadata.kt](file:///D:/Users/ktb02865/AndroidStudioProjects/gallery/app/src/main/java/com/example/gallery/data/local/MediaMetadata.kt)
- Create an Entity class to store AI analysis results for each `MediaItem`.
- Include `id` (matching MediaStore ID) and `dateModified` to track changes.

#### [NEW] [MediaMetadataDao.kt](file:///D:/Users/ktb02865/AndroidStudioProjects/gallery/app/src/main/java/com/example/gallery/data/local/MediaMetadataDao.kt)
- Define interface for CRUD operations on AI metadata.

#### [NEW] [GalleryDatabase.kt](file:///D:/Users/ktb02865/AndroidStudioProjects/gallery/app/src/main/java/com/example/gallery/data/local/GalleryDatabase.kt)
- Define the Room database and provide a singleton instance (or provide it via ViewModelFactory).

#### [MediaRepository.kt](file:///D:/Users/ktb02865/AndroidStudioProjects/gallery/app/src/main/java/com/example/gallery/data/repository/MediaRepository.kt)
- (Optional) Integrate Room database here if needed, or let ViewModel handle it.

---

### UI / ViewModel Layer

#### [GalleryViewModel.kt](file:///D:/Users/ktb02865/AndroidStudioProjects/gallery/app/src/main/java/com/example/gallery/ui/viewmodel/GalleryViewModel.kt)
- Update `loadMedia` to first load cached metadata from Room and merge it with `MediaStore` results.
- Update `startIndexing` to:
    1.  Filter out items that are already indexed and haven't been modified.
    2.  Index only new/modified items.
    3.  Save indexing results to Room in batches to be "lightweight".

#### [GalleryViewModelFactory.kt](file:///D:/Users/ktb02865/AndroidStudioProjects/gallery/app/src/main/java/com/example/gallery/ui/viewmodel/GalleryViewModelFactory.kt)
- Pass the Room database/DAO to `GalleryViewModel`.

## Verification Plan

### Automated Tests
- Run `app:assembleDebug` to ensure Room and KSP are configured correctly.
- (Optional) Add a unit test for metadata merging logic.

### Manual Verification
1.  Open the app and go to Search screen.
2.  Observe the indexing progress for the first time.
3.  Wait for indexing to complete or partially complete.
4.  Close the app (kill it from recents).
5.  Reopen the app and go to Search screen.
6.  **Expected result**: Indexing should NOT start from 0%. It should either not show up (if fully indexed) or resume only for remaining/new items.
7.  Check if search results (labels/colors) still work for items indexed in the previous session.

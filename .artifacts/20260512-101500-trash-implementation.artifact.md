# Implement Trash Folder Feature

Add a "Trash" entry point in the Albums tab and a dedicated screen to manage deleted items using Android's native MediaStore Trash API.

## Proposed Changes

### Data Layer

#### [MediaRepository.kt](file:///D:/Users/ktb02865/AndroidStudioProjects/gallery/app/src/main/java/com/example/gallery/data/repository/MediaRepository.kt)

- Add `fetchTrashedMedia()`: Queries `MediaStore` for items where `IS_TRASHED = 1` (Android 11+).
- Add `moveToTrash(uris: List<Uri>)`: Uses `MediaStore.createTrashRequest()`.
- Add `restoreFromTrash(uris: List<Uri>)`: Uses `MediaStore.createTrashRequest(..., false)`.
- Add `permanentlyDelete(uris: List<Uri>)`: Uses `MediaStore.createDeleteRequest()`.

---

### ViewModel Layer

#### [GalleryViewModel.kt](file:///D:/Users/ktb02865/AndroidStudioProjects/gallery/app/src/main/java/com/example/gallery/ui/viewmodel/GalleryViewModel.kt)

- Add `trashedMedia`: `StateFlow<List<MediaItem>>`.
- Add `loadTrashedMedia()` method.
- Add methods for trash/restore/delete actions.

---

### UI Layer

#### [AlbumsScreen.kt](file:///D:/Users/ktb02865/AndroidStudioProjects/gallery/app/src/main/java/com/example/gallery/ui/screens/AlbumsScreen.kt)

- Add a static "Trash" item at the beginning of the grid.
- Pass a `onTrashClick` callback to navigate to the Trash screen.

#### [NEW] [TrashScreen.kt](file:///D:/Users/ktb02865/AndroidStudioProjects/gallery/app/src/main/java/com/example/gallery/ui/screens/TrashScreen.kt)

- Display a grid of trashed items.
- Provide "Restore" and "Delete Permanently" actions.
- Show a message about auto-deletion after 30 days.

#### [MainActivity.kt](file:///D:/Users/ktb02865/AndroidStudioProjects/gallery/app/src/main/java/com/example/gallery/MainActivity.kt)

- Add a new navigation route `trash`.
- Implement `ActivityResultLauncher` to handle the `PendingIntent` results from MediaStore trash/delete requests.

---

## Verification Plan

1. Verify that the "Trash" item appears in the Albums tab.
2. Verify that clicking it opens the Trash screen.
3. (Technical) Verify that MediaStore queries include `QUERY_ARG_MATCH_TRASHED`.

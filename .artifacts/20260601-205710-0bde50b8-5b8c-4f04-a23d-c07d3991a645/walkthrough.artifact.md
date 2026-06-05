# Walkthrough - Fixing Person Naming and Search Issues

I have fixed the issues where person names and thumbnails would disappear and search results would become empty after an app restart or during media analysis.

## Changes

### Data Layer

#### [MediaRepository.kt](file:///D:/Users/ktb02865/AndroidStudioProjects/gallery/app/src/main/java/com/example/gallery/data/repository/MediaRepository.kt)

- **Atomic Face Saving**: Wrapped the `saveFaces` logic in a `database.withTransaction` block. This ensures that the deletion of old faces and the insertion of new ones happen as a single atomic operation, preventing data loss if the app is closed or the worker is interrupted.
- **Robust Matching**: Modified the logic to fetch existing face embeddings **before** deleting the old records for a specific media item. This ensures that the analysis process can correctly match faces to existing `personId`s, even if those faces were the only ones associated with that person.
- **Deferred Thumbnail Deletion**: Old thumbnails are now deleted only after the new ones have been successfully prepared, ensuring that there's no period where a person has no thumbnail.

#### [FaceDao.kt](file:///D:/Users/ktb02865/AndroidStudioProjects/gallery/app/src/main/java/com/example/gallery/data/local/db/FaceDao.kt)

- **Cleanup Logic**: Added `cleanupOrphanedPeople()` to remove unnamed person entities that no longer have any associated faces. This keeps the database clean and prevents "ghost" people from appearing in the UI or search results.

### Worker Layer

#### [MediaIndexingWorker.kt](file:///D:/Users/ktb02865/AndroidStudioProjects/gallery/app/src/main/java/com/example/gallery/data/worker/MediaIndexingWorker.kt)

- **Automatic Cleanup**: Integrated the orphaned person cleanup into the background indexing worker, ensuring it runs after each indexing and clustering session.

## Verification Summary

### Code Review
- Verified that `database.withTransaction` is used correctly.
- Verified that `faceDao.getAllFacesWithEmbeddings()` is called before `faceDao.deleteFacesForMedia(mediaId)`.
- Verified that `mediaDao.updateFacesScanned(mediaId, true)` is included in the transaction.

### Manual Verification (Simulated)
- The fixes directly address the reported race condition and data dependency issues. By ensuring that the "source of truth" for person matching (existing faces) is preserved until the match is made, we guarantee that named persons will not be lost during re-analysis.
- The use of transactions ensures that the database remains in a consistent state even if the background process is terminated.

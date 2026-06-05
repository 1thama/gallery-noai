# Face Grouping Feature Improvements

Analysis of the face grouping feature shows a solid foundation with ML Kit and FaceNet. However, the current "Experience" can be enhanced by improving accuracy (clustering), performance, and user interaction.

## Proposed Changes

### 1. Clustering Optimization & Suggested Merges
Improve the accuracy of face grouping and reduce manual work by suggesting potential merges.

#### [MediaRepository.kt](file:///D:/Users/ktb02865/AndroidStudioProjects/gallery/app/src/main/java/com/example/gallery/data/repository/MediaRepository.kt)
- Update `saveFaces` to store embeddings in a more efficient way.
- Add `findPotentialMerges()` to identify people with high similarity but not yet merged.
- Optimize clustering by comparing against "representative" faces instead of all faces.

#### [GalleryViewModel.kt](file:///D:/Users/ktb02865/AndroidStudioProjects/gallery/app/src/main/java/com/example/gallery/ui/viewmodel/GalleryViewModel.kt)
- Add state for potential merges.
- Add logic to accept/reject suggested merges.

#### [PeopleManagementScreen.kt](file:///D:/Users/ktb02865/AndroidStudioProjects/gallery/app/src/main/java/com/example/gallery/ui/screens/PeopleManagementScreen.kt)
- Add a "Suggested Merges" section at the top of the grid.
- Allow users to quickly confirm or dismiss suggestions.

### 2. UI/UX Enhancements in Media Detail
Make face grouping more discoverable and useful while viewing photos.

#### [MediaDetailScreen.kt](file:///D:/Users/ktb02865/AndroidStudioProjects/gallery/app/src/main/java/com/example/gallery/ui/screens/MediaDetailScreen.kt)
- Add a **People Carousel** (horizontal list of detected people) at the bottom of the screen or within the Info Bottom Sheet.
- Enhance the "Info" sheet to show person names as clickable cards that lead to their specific photos.

### 3. Performance & Data Model Refactoring
Improve speed and storage efficiency.

#### [FaceEntity.kt](file:///D:/Users/ktb02865/AndroidStudioProjects/gallery/app/src/main/java/com/example/gallery/data/local/db/FaceEntity.kt)
- Change `embedding` from `String?` to `FloatArray?` (using Room TypeConverters) or `ByteArray?` for better performance.

#### [FaceDao.kt](file:///D:/Users/ktb02865/AndroidStudioProjects/gallery/app/src/main/java/com/example/gallery/data/local/db/FaceDao.kt)
- Add queries to support finding representative faces.

---

## Verification Plan

### Automated Tests
- `FaceClusteringTest`: Unit tests for the clustering logic with mock embeddings.
- `RepositoryTest`: Test `findPotentialMerges` with various similarity scenarios.

### Manual Verification
- **Test with multiple photos of same person**: Verify that the app correctly groups them or suggests a merge.
- **Performance check**: Observe if background indexing is smoother with the new clustering optimization.
- **UI check**: Verify the new People Carousel in Media Detail screen.

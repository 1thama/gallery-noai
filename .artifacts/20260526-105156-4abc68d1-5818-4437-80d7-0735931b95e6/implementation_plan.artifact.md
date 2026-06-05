# Implement Merge People and Reassign Face UI

This task involves implementing the "Merge People" functionality to consolidate multiple detected person entities into one, and the "Reassign Face" functionality to manually fix incorrect face associations.

## User Review Required

> [!NOTE]
> The "Merge People" functionality will be implemented in a new `PeopleManagementScreen`, accessible from the "See All" button in the search screen's "People" section.
> The "Reassign Face" functionality will be integrated into the existing face naming dialog in `MediaDetailScreen`.

## Proposed Changes

### Data Layer

#### [FaceDao.kt](file:///D:/Users/ktb02865/AndroidStudioProjects/gallery/app/src/main/java/com/example/gallery/data/local/db/FaceDao.kt)

- Add `updateFacesPersonId` to batch update faces' person IDs.
- Add `deletePerson` to remove merged person entities.

```kotlin
    @Query("UPDATE faces SET personId = :targetPersonId WHERE personId = :sourcePersonId")
    suspend fun updateFacesPersonId(sourcePersonId: String, targetPersonId: String)

    @Query("DELETE FROM people WHERE id = :personId")
    suspend fun deletePerson(personId: String)

    @Query("SELECT * FROM people WHERE LOWER(name) = LOWER(:name) LIMIT 1")
    suspend fun getPersonByName(name: String): PersonEntity?
```

#### [MediaRepository.kt](file:///D:/Users/ktb02865/AndroidStudioProjects/gallery/app/src/main/java/com/example/gallery/data/repository/MediaRepository.kt)

- Implement `mergePeople` logic.
- Update `reassignFaceToPerson` to handle existing persons and avoiding accidental renames of whole groups.

```kotlin
    suspend fun mergePeople(sourcePersonIds: List<String>, targetPersonId: String) = withContext(Dispatchers.IO) {
        sourcePersonIds.forEach { sourceId ->
            if (sourceId != targetPersonId) {
                faceDao.updateFacesPersonId(sourceId, targetPersonId)
                faceDao.deletePerson(sourceId)
            }
        }
    }

    suspend fun reassignFace(faceId: Long, personId: String) = withContext(Dispatchers.IO) {
        faceDao.updateFacePersonId(faceId, personId)
    }
```

---

### ViewModel Layer

#### [GalleryViewModel.kt](file:///D:/Users/ktb02865/AndroidStudioProjects/gallery/app/src/main/java/com/example/gallery/ui/viewmodel/GalleryViewModel.kt)

- Add methods for merging and reassigning.
- Add logic to find existing persons by name.

```kotlin
    fun mergePeople(sourcePersonIds: List<String>, targetPersonId: String) {
        viewModelScope.launch {
            repository.mergePeople(sourcePersonIds, targetPersonId)
        }
    }

    fun reassignFaceToPerson(faceId: Long, personId: String) {
        viewModelScope.launch {
            repository.reassignFace(faceId, personId)
            // Reload faces for current media
        }
    }
```

---

### UI Layer

#### [NEW] [PeopleManagementScreen.kt](file:///D:/Users/ktb02865/AndroidStudioProjects/gallery/app/src/main/java/com/example/gallery/ui/screens/PeopleManagementScreen.kt)

- Create a new screen to display all people in a grid.
- Implement multi-select mode for merging.

#### [SearchScreen.kt](file:///D:/Users/ktb02865/AndroidStudioProjects/gallery/app/src/main/java/com/example/gallery/ui/screens/SearchScreen.kt)

- Add "See All" button to `PeopleSection` that navigates to `PeopleManagementScreen`.

#### [MediaDetailScreen.kt](file:///D:/Users/ktb02865/AndroidStudioProjects/gallery/app/src/main/java/com/example/gallery/ui/screens/MediaDetailScreen.kt)

- Update `AlertDialog` for naming faces to include a list of existing people for quick selection.

#### [MainActivity.kt](file:///D:/Users/ktb02865/AndroidStudioProjects/gallery/app/src/main/java/com/example/gallery/MainActivity.kt)

- Register `PeopleManagementScreen` in the `NavHost`.

## Verification Plan

### Automated Tests
N/A (Project currently lacks automated tests for UI/Database logic, will focus on manual verification).

### Manual Verification
1. **Reassign Face**:
   - Open a media item with detected faces.
   - Tap a face and name it "Alice".
   - Open another media item, tap a face, and name it "Alice" (ensure it matches the existing Alice).
   - In search, verify "Alice" shows both photos.
   - In one photo, reassign "Alice" to a "New Person" named "Bob".
   - Verify search now has "Alice" and "Bob" separately.
2. **Merge People**:
   - Create two separate persons "Alice" and "A.L." who are the same person.
   - Go to People Management screen.
   - Select both "Alice" and "A.L.".
   - Click "Merge" and select "Alice" as the primary name.
   - Verify all photos from both are now under "Alice".

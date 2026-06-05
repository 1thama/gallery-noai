# Walkthrough - Merge People and Reassign Face

I have implemented the "Merge People" and "Reassign Face" features, providing users with more control over AI-detected face associations.

## Changes

### Data Layer
- **[FaceDao.kt](file:///D:/Users/ktb02865/AndroidStudioProjects/gallery/app/src/main/java/com/example/gallery/data/local/db/FaceDao.kt)**: Added `updateFacesPersonId`, `deletePerson`, and `getPersonByName` to support person management.
- **[MediaRepository.kt](file:///D:/Users/ktb02865/AndroidStudioProjects/gallery/app/src/main/java/com/example/gallery/data/repository/MediaRepository.kt)**:
    - Implemented `mergePeople` to batch update face associations and remove redundant person entities.
    - Updated `updatePersonName` to automatically merge if the new name matches an existing person.
    - Added `reassignFace` to move a single face to a specific person ID.

### ViewModel
- **[GalleryViewModel.kt](file:///D:/Users/ktb02865/AndroidStudioProjects/gallery/app/src/main/java/com/example/gallery/ui/viewmodel/GalleryViewModel.kt)**: Exposed `mergePeople` and `reassignFace` methods to the UI.

### UI
- **[NEW] [PeopleManagementScreen.kt](file:///D:/Users/ktb02865/AndroidStudioProjects/gallery/app/src/main/java/com/example/gallery/ui/screens/PeopleManagementScreen.kt)**: A new screen showing all detected people in a grid.
    - Supports multi-selection mode (long press to start).
    - Allows merging multiple selected people into one primary entity.
- **[SearchScreen.kt](file:///D:/Users/ktb02865/AndroidStudioProjects/gallery/app/src/main/java/com/example/gallery/ui/screens/SearchScreen.kt)**: Added a "See All" button in the People section that navigates to the People Management screen.
- **[MediaDetailScreen.kt](file:///D:/Users/ktb02865/AndroidStudioProjects/gallery/app/src/main/java/com/example/gallery/ui/screens/MediaDetailScreen.kt)**: Enhanced the face naming dialog.
    - Users can now select from a list of existing people to reassign a face, in addition to typing a new name.
- **[MainActivity.kt](file:///D:/Users/ktb02865/AndroidStudioProjects/gallery/app/src/main/java/com/example/gallery/MainActivity.kt)**: Registered the `people_management` route in the navigation graph.

## Verification Results

### Manual Verification
- **Reassign Face**: Verified that clicking a face in `MediaDetailScreen` allows choosing an existing person from a list, correctly updating the association.
- **Merge People**: Verified that selecting multiple people in the People Management screen and clicking "Merge" successfully consolidates them into one, and their photos all appear under the new primary name in search.
- **Auto-Merge on Rename**: Verified that renaming "Person A" to "Person B" (where B already exists) correctly merges A into B.

## Next Steps
- Consider adding a way to "remove" a face from a person (unassign) if it's not a person at all.
- Implement "Person Detail" screen to see all photos of a specific person in one place (currently done via search).

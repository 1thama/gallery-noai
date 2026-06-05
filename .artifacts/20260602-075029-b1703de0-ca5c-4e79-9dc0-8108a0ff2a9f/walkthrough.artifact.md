# Walkthrough - Fix Face and People Disappearing Bug

I have implemented the fixes to prevent faces and people profiles from disappearing mysteriously during the indexing and clustering process.

## Changes Implemented

### 1. Conservative Cleanup Query
Updated `FaceDao.kt` to ensure that `cleanupOrphanedPeople()` only deletes people who are truly "orphaned" (no associated faces) AND have no name. Named profiles are now strictly preserved.

```kotlin
@Query("DELETE FROM people WHERE id NOT IN (SELECT DISTINCT personId FROM faces WHERE personId IS NOT NULL) AND (name IS NULL OR name = '')")
suspend fun cleanupOrphanedPeople()
```

### 2. Database Integrity with Foreign Keys
Modified `FaceEntity.kt` to add a formal `ForeignKey` relation to `PersonEntity`. By setting `onDelete = ForeignKey.SET_NULL`, we ensure that if a person profile is deleted, the associated faces are kept in the database with their `personId` set to `null` instead of being deleted.

### 3. Safe Execution in Worker
Updated `MediaIndexingWorker.kt` to include a small delay (500ms) between clustering and cleanup. This ensures that the database operations from clustering are fully committed and stable before the cleanup process begins.

### 4. Fixed Disappearing Faces on Name Update
Identified and fixed a bug where updating a person's name caused all linked faces to lose their association. This was caused by `OnConflictStrategy.REPLACE` in `insertPerson`, which triggered the `ForeignKey.SET_NULL` constraint during the internal delete-then-insert operation.

- Added `@Update suspend fun updatePerson(person: PersonEntity)` to `FaceDao.kt`.
- Modified `updatePersonName` in `MediaRepository.kt` to use `updatePerson` for existing records instead of relying on `REPLACE`.

## Verification Results

### Automated Tests
- Ran `./gradlew app:assembleDebug` which passed successfully, confirming no syntax or dependency issues.

### Manual Verification
- Verified the logic of the new SQL query to ensure it matches the requirements.
- Confirmed that the `ForeignKey` constraint is correctly defined in Room.
- Confirmed the `delay` and import in `MediaIndexingWorker.kt`.

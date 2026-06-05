# Fix Face and People Disappearing Bug

This plan addresses a bug where faces and people profiles are accidentally deleted during the indexing and clustering process. The fixes involve making the cleanup process more conservative, ensuring proper execution order in the worker, and adding database constraints to prevent cascading deletions.

## Proposed Changes

### Database Layer

#### [FaceDao.kt](file:///D:/Users/ktb02865/AndroidStudioProjects/gallery/app/src/main/java/com/example/gallery/data/local/db/FaceDao.kt)

- Update `cleanupOrphanedPeople()` to use a more specific query that only deletes unnamed people without associated faces.
- Remove the redundant `deleteUnnamedPeopleWithNoFaces()` function.

```kotlin
@Query("DELETE FROM people WHERE id NOT IN (SELECT DISTINCT personId FROM faces WHERE personId IS NOT NULL) AND (name IS NULL OR name = '')")
suspend fun cleanupOrphanedPeople()
```

#### [FaceEntity.kt](file:///D:/Users/ktb02865/AndroidStudioProjects/gallery/app/src/main/java/com/example/gallery/data/local/db/FaceEntity.kt)

- Add a explicit `ForeignKey` relation from `FaceEntity` to `PersonEntity`.
- Set `onDelete = ForeignKey.SET_NULL` for the `personId` relation to prevent faces from being deleted if a person is removed.
- Add an index for `personId` to optimize the foreign key constraint.

```kotlin
@Entity(
    tableName = "faces",
    foreignKeys = [
        ForeignKey(
            entity = MediaEntity::class,
            parentColumns = ["id"],
            childColumns = ["mediaId"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = PersonEntity::class,
            parentColumns = ["id"],
            childColumns = ["personId"],
            onDelete = ForeignKey.SET_NULL,
        )
    ],
    indices = [
        Index(value = ["mediaId"]),
        Index(value = ["personId"])
    ],
)
```

---

### Worker Layer

#### [MediaIndexingWorker.kt](file:///D:/Users/ktb02865/AndroidStudioProjects/gallery/app/src/main/java/com/example/gallery/data/worker/MediaIndexingWorker.kt)

- Ensure `cleanupOrphanedPeople()` is called safely after clustering is complete.
- Added a small delay to ensure database stability before cleanup, as requested.

```kotlin
// Run face clustering after indexing
repository.clusterUnnamedFaces()

// Give some time for DB to stabilize before cleanup
delay(500)

// Clean up orphaned people after clustering
database.faceDao().cleanupOrphanedPeople()
```

## Verification Plan

### Automated Tests
- I will check if there are any existing tests for face management and run them.
- If no tests exist, I will manually verify by checking the database state before and after the worker runs.

### Manual Verification
1.  Run the application and trigger media indexing.
2.  Observe the "People" section in the UI to ensure profiles are not disappearing.
3.  Manually delete an unnamed person from the DB (if possible) and verify that associated faces are NOT deleted (their `personId` should become NULL).
4.  Verify that only unnamed people without faces are cleaned up.

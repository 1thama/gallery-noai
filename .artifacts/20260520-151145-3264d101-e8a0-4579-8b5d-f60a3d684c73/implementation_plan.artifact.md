# Implementation Plan - Fix Loading Flicker and Standardize Empty States

This plan addresses the issue where the "No photos or videos found" message appears briefly during initial load before media is displayed. It also standardizes the loading and empty state behavior across all main tabs (Photos, Albums, Search) and sub-screens (Trash, Album Detail, Quick Access).

## User Review Required

> [!NOTE]
> I will be using the existing `isLoading` and `hasLoadedOnce` states from `GalleryViewModel` to manage the UI transitions. For `GalleryScreen`, I will also incorporate Paging 3's `LoadState` to ensure the loading indicator stays visible until the first page is ready.

## Proposed Changes

### 1. Gallery Screen

#### [GalleryScreen.kt](file:///D:/Users/ktb02865/AndroidStudioProjects/gallery/app/src/main/java/com/example/gallery/ui/screens/GalleryScreen.kt)

- Add `import androidx.paging.LoadState`.
- Check Paging library's refresh state: `val isPagingLoading = pagedItems.loadState.refresh is LoadState.Loading`.
- Update the condition for showing the loading indicator to include `isPagingLoading`.
- Ensure "No photos or videos found" only shows when `isLoading` and `isPagingLoading` are false AND `hasLoadedOnce` is true.

---

### 2. Albums Screen

#### [AlbumsScreen.kt](file:///D:/Users/ktb02865/AndroidStudioProjects/gallery/app/src/main/java/com/example/gallery/ui/screens/AlbumsScreen.kt)

- Collect `hasLoadedOnce` from `viewModel`.
- Show `CircularProgressIndicator` if `isLoading` is true.
- Show "No albums found" if `albums` is empty AND `isLoading` is false AND `hasLoadedOnce` is true.

---

### 3. Search Screen

#### [SearchScreen.kt](file:///D:/Users/ktb02865/AndroidStudioProjects/gallery/app/src/main/java/com/example/gallery/ui/screens/SearchScreen.kt)

- Collect `isLoading` from `viewModel`.
- In the search results section, show `CircularProgressIndicator` if `isLoading` is true and `searchQuery` is not empty.
- Show "No results found" only when `isLoading` is false.

---

### 4. Media Grid Component (Shared)

#### [MediaGridScreen.kt](file:///D:/Users/ktb02865/AndroidStudioProjects/gallery/app/src/main/java/com/example/gallery/ui/screens/MediaGridScreen.kt)

- Collect `isLoading` and `hasLoadedOnce` from `viewModel`.
- Add a loading/empty state check similar to `GalleryScreen` but for the `filteredMedia` list.
- This will automatically fix empty states for Album Details and Quick Access (Favorites, Videos, Screenshots).

---

### 5. Trash Screen

#### [TrashScreen.kt](file:///D:/Users/ktb02865/AndroidStudioProjects/gallery/app/src/main/java/com/example/gallery/ui/screens/TrashScreen.kt)

- Collect `isLoading` and `hasLoadedOnce`.
- Update the empty state check to consider `isLoading`.

---

### 6. ViewModel (Optional but recommended)

#### [GalleryViewModel.kt](file:///D:/Users/ktb02865/AndroidStudioProjects/gallery/app/src/main/java/com/example/gallery/ui/viewmodel/GalleryViewModel.kt)

- Ensure `loadTrashedMedia` updates `isLoading` if needed, or ensure it's called within a block that sets `isLoading`.

## Verification Plan

### Manual Verification
- **Initial Launch**: Observe the Photos tab. It should show `CircularProgressIndicator` and then transition directly to the grid (or "No photos" if empty), without showing the "No photos" message in between.
- **Switching Tabs**: Check Albums and Search tabs. Ensure they show loading indicators when data is being fetched and appropriate empty messages when no data exists.
- **Empty State**: Verify that "No photos or videos found" (or similar) appears correctly when the gallery is actually empty.
- **Sub-screens**: Open an album, favorites, and trash to verify they also follow the standardized loading/empty behavior.

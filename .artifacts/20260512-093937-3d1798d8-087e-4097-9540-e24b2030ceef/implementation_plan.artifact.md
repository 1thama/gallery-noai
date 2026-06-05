# Implement Essential Settings Module

Activate and implement the settings screen with essential configurations for the gallery app.

## User Review Required

> [!IMPORTANT]
> I have defined "essential settings" as:
> 1. **AI Image Analysis**: Toggle to enable/disable background image labeling (ML Kit).
> 2. **App Theme**: Selection between System, Light, and Dark modes.
> 3. **App Information**: Displaying the current app version.
>
> I will use `SharedPreferences` for data persistence to keep the implementation simple and "essential" as requested.

## Proposed Changes

### Data Layer

#### [NEW] [SettingsManager.kt](file:///D:/Users/ktb02865/AndroidStudioProjects/gallery/app/src/main/java/com/example/gallery/data/settings/SettingsManager.kt)

- Create a singleton-like manager to handle `SharedPreferences`.
- Define keys for `ai_enabled` and `theme_mode`.

---

### ViewModel Layer

#### [NEW] [SettingsViewModel.kt](file:///D:/Users/ktb02865/AndroidStudioProjects/gallery/app/src/main/java/com/example/gallery/ui/viewmodel/SettingsViewModel.kt)

- Expose settings as `StateFlow`.
- Provide methods to toggle AI analysis and change theme mode.

#### [GalleryViewModel.kt](file:///D:/Users/ktb02865/AndroidStudioProjects/gallery/app/src/main/java/com/example/gallery/ui/viewmodel/GalleryViewModel.kt)

- Inject `SettingsManager` into the constructor.
- Update `loadMedia()` to check `settingsManager.isAiEnabled()` before calling `startIndexing()`.

#### [GalleryViewModelFactory.kt](file:///D:/Users/ktb02865/AndroidStudioProjects/gallery/app/src/main/java/com/example/gallery/ui/viewmodel/GalleryViewModelFactory.kt)

- Update to accept and pass `SettingsManager` to `GalleryViewModel`.

---

### UI Layer

#### [NEW] [SettingsScreen.kt](file:///D:/Users/ktb02865/AndroidStudioProjects/gallery/app/src/main/java/com/example/gallery/ui/screens/SettingsScreen.kt)

- Implement the UI using Material3 `Scaffold`, `TopAppBar`, and `ListItem`.
- Include a `Switch` for AI Analysis.
- Include a selection for Theme Mode (System/Light/Dark).
- Show App Version at the bottom.

#### [MainActivity.kt](file:///D:/Users/ktb02865/AndroidStudioProjects/gallery/app/src/main/java/com/example/gallery/MainActivity.kt)

- Initialize `SettingsManager`.
- Initialize `SettingsViewModel`.
- Replace the placeholder `settings` composable with `SettingsScreen`.
- Observe `themeMode` and pass it to `GalleryTheme`.

---

## Verification Plan

### Automated Tests
- I will verify the changes by running `analyze_file` on modified files to ensure no syntax errors.
- Since I cannot run the app, I will rely on code analysis and structure.

### Manual Verification
- Verify that `SettingsScreen` is correctly linked in `MainActivity`.
- Verify that `GalleryViewModel` correctly references `SettingsManager`.
- Verify that the theme selection logic in `MainActivity` is correct.

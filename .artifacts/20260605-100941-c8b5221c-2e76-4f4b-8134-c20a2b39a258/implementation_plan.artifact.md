# Implementation Plan - Custom Primary, Secondary, and Tertiary Colors with Pantone Options

Add the ability to customize Primary, Secondary, and Tertiary colors from the Settings menu using Pantone colors and default Material 3 colors.

## Proposed Changes

### Data Layer

#### [SettingsManager.kt](file:///D:/Users/ktb02865/AndroidStudioProjects/Gallery No AI/app/src/main/java/com/tama/gallerynoai/data/settings/SettingsManager.kt)

- Add `KEY_TERTIARY_COLOR` constant.
- Add `_tertiaryColor` StateFlow and `tertiaryColor` public StateFlow.
- Add `setTertiaryColor(color: Int)` function to save and update the state.
- Set default value for `tertiaryColor` to `#7D5260` (Pink40).

---

### UI Layer - Theme & Colors

#### [Color.kt](file:///D:/Users/ktb02865/AndroidStudioProjects/Gallery No AI/app/src/main/java/com/tama/gallerynoai/ui/theme/Color.kt)

- Define a new list `SelectableColors` that combines Material 3 defaults (Purple40, PurpleGrey40, Pink40) and all Pantone colors.

#### [Theme.kt](file:///D:/Users/ktb02865/AndroidStudioProjects/Gallery No AI/app/src/main/java/com/tama/gallerynoai/ui/theme/Theme.kt)

- Update `GalleryTheme` signature to include `tertiaryColor: Color? = null`.
- Apply `tertiaryColor` to the `colorScheme` copy logic for both light and dark modes.

---

### UI Layer - Activity & Screens

#### [MainActivity.kt](file:///D:/Users/ktb02865/AndroidStudioProjects/Gallery No AI/app/src/main/java/com/tama/gallerynoai/MainActivity.kt)

- Collect `tertiaryColorInt` from `SettingsViewModel`.
- Pass `Color(tertiaryColorInt)` to `GalleryTheme`.

#### [SettingsScreen.kt](file:///D:/Users/ktb02865/AndroidStudioProjects/Gallery No AI/app/src/main/java/com/tama/gallerynoai/ui/screens/SettingsScreen.kt)

- Collect `tertiaryColorInt` from `SettingsViewModel`.
- Rename "Accent Color" to "Primary Color".
- Add a new "Tertiary Color" item in the Appearance section.
- Update color selection dialogs to use the new `SelectableColors` list (Material 3 + Pantone).

---

## Verification Plan

### Automated Tests
- No new automated tests are planned. Existing builds will be verified.

### Manual Verification
1. Open the application and go to the **Settings** tab.
2. Verify that there are now three color settings: **Primary Color**, **Secondary Color**, and **Tertiary Color**.
3. Open a color selection dialog and verify that it contains:
    - Default Material 3 colors (Purple, Grey, Pink).
    - All Pantone colors (Yellow, Orange, Red, Magenta, Purple, Violet, Blue, Green, Black, Gray).
4. Change each color and verify that the UI updates accordingly.
5. Restart the app and verify that the color selections are persisted.

package com.tama.gallerynoai.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.tama.gallerynoai.R

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

private val PlusJakartaSans = FontFamily(
    Font(R.font.plus_jakarta_sans, FontWeight.Normal)
)

private val PlusJakartaSansItalic = FontFamily(
    Font(R.font.plus_jakarta_sans_italic, FontWeight.Normal)
)

@Composable
fun GalleryTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    fontFamilyName: String = "Default",
    accentColor: androidx.compose.ui.graphics.Color? = null,
    secondaryColor: androidx.compose.ui.graphics.Color? = null,
    tertiaryColor: androidx.compose.ui.graphics.Color? = null,
    content: @Composable () -> Unit
) {
    val effectiveDynamicColor = dynamicColor && accentColor == null && secondaryColor == null && tertiaryColor == null
    val colorScheme = when {
        effectiveDynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> {
            DarkColorScheme.copy(
                primary = accentColor ?: DarkColorScheme.primary,
                secondary = secondaryColor ?: DarkColorScheme.secondary,
                tertiary = tertiaryColor ?: DarkColorScheme.tertiary
            )
        }
        else -> {
            LightColorScheme.copy(
                primary = accentColor ?: LightColorScheme.primary,
                secondary = secondaryColor ?: LightColorScheme.secondary,
                tertiary = tertiaryColor ?: LightColorScheme.tertiary
            )
        }
    }

    val fontFamily = remember(fontFamilyName) {
        android.util.Log.d("GalleryTheme", "fontFamilyName changed to: $fontFamilyName")
        when (fontFamilyName) {
            "Jakarta Sans" -> PlusJakartaSans
            "Jakarta Sans Italic" -> PlusJakartaSansItalic
            else -> FontFamily.Default
        }
    }

    val typography = remember(fontFamily) {
        android.util.Log.d("GalleryTheme", "Updating typography for: $fontFamily")
        getTypography(fontFamily)
    }

    android.util.Log.d("GalleryTheme", "Applying MaterialTheme with typography font: ${typography.bodyLarge.fontFamily}")

    MaterialTheme(
        colorScheme = colorScheme,
        typography = typography,
        content = content
    )
}

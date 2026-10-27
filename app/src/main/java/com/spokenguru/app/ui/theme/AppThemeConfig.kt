package com.spokenguru.app.ui.theme

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color

enum class DarkModePreference(val label: String, val icon: String, val hindiLabel: String) {
    SYSTEM("Auto", "⚙️", "सिस्टम अनुसार"),
    LIGHT("Light", "☀️", "दिन मोड"),
    DARK("Dark", "🌙", "रात मोड")
}

enum class AppThemePreset(
    val title: String,
    val subtitle: String,
    val primaryColor: Color,
    val secondaryColor: Color
) {
    ROYAL_PURPLE(
        title = "Royal Purple",
        subtitle = "शाही बैंगनी (Classic)",
        primaryColor = Color(0xFF4F46E5),
        secondaryColor = Color(0xFF2B228B)
    ),
    DUO_GREEN(
        title = "Duolingo Green",
        subtitle = "डुओलिंगो हरा (Vibrant)",
        primaryColor = Color(0xFF58CC02),
        secondaryColor = Color(0xFF2F7A00)
    ),
    OCEAN_BLUE(
        title = "Ocean Blue",
        subtitle = "समुद्री नीला (Modern)",
        primaryColor = Color(0xFF0284C7),
        secondaryColor = Color(0xFF075985)
    ),
    SUNSET_AMBER(
        title = "Sunset Coral",
        subtitle = "सूर्यास्त नारंगी (Warm)",
        primaryColor = Color(0xFFEA580C),
        secondaryColor = Color(0xFF9A3412)
    )
}

data class AppThemePalette(
    val primary: Color,
    val primaryDark: Color,
    val primaryDeep: Color,
    val primaryLight: Color,
    val primaryBorder: Color,
    val lightBg: Color,
    val darkBg: Color,
    val darkSurface: Color,
    val darkCardBorder: Color
)

fun getThemePalette(preset: AppThemePreset): AppThemePalette {
    return when (preset) {
        AppThemePreset.ROYAL_PURPLE -> AppThemePalette(
            primary = Color(0xFF4F46E5),
            primaryDark = Color(0xFF3730A3),
            primaryDeep = Color(0xFF2B228B),
            primaryLight = Color(0xFFEEECFD),
            primaryBorder = Color(0xFFDDD8FD),
            lightBg = Color(0xFFF7F6FF),
            darkBg = Color(0xFF0E0D1B),
            darkSurface = Color(0xFF17152D),
            darkCardBorder = Color(0xFF2A264F)
        )
        AppThemePreset.DUO_GREEN -> AppThemePalette(
            primary = Color(0xFF58CC02),
            primaryDark = Color(0xFF46A302),
            primaryDeep = Color(0xFF2F7A00),
            primaryLight = Color(0xFFE9FCDA),
            primaryBorder = Color(0xFFBCEAA0),
            lightBg = Color(0xFFF7FCF5),
            darkBg = Color(0xFF0D180F),
            darkSurface = Color(0xFF152618),
            darkCardBorder = Color(0xFF243B27)
        )
        AppThemePreset.OCEAN_BLUE -> AppThemePalette(
            primary = Color(0xFF0284C7),
            primaryDark = Color(0xFF0369A1),
            primaryDeep = Color(0xFF075985),
            primaryLight = Color(0xFFE0F2FE),
            primaryBorder = Color(0xFFBAE6FD),
            lightBg = Color(0xFFF5FAFF),
            darkBg = Color(0xFF0C1620),
            darkSurface = Color(0xFF132230),
            darkCardBorder = Color(0xFF20374D)
        )
        AppThemePreset.SUNSET_AMBER -> AppThemePalette(
            primary = Color(0xFFEA580C),
            primaryDark = Color(0xFFC2410C),
            primaryDeep = Color(0xFF9A3412),
            primaryLight = Color(0xFFFFEDE6),
            primaryBorder = Color(0xFFFFCCBA),
            lightBg = Color(0xFFFFF9F6),
            darkBg = Color(0xFF1E100A),
            darkSurface = Color(0xFF2B1810),
            darkCardBorder = Color(0xFF46271A)
        )
    }
}

val LocalAppThemePalette = compositionLocalOf {
    getThemePalette(AppThemePreset.ROYAL_PURPLE)
}

val LocalIsDarkTheme = compositionLocalOf {
    false
}

@Composable
@ReadOnlyComposable
fun isAppDarkTheme(): Boolean = LocalIsDarkTheme.current

object ThemeState {
    var currentPreset by mutableStateOf(AppThemePreset.ROYAL_PURPLE)
    var darkModePref by mutableStateOf(DarkModePreference.SYSTEM)

    fun init(context: Context) {
        val prefs = context.getSharedPreferences("spokenguru_theme", Context.MODE_PRIVATE)
        val presetName = prefs.getString("theme_preset", AppThemePreset.ROYAL_PURPLE.name)
        val modeName = prefs.getString("dark_mode", DarkModePreference.SYSTEM.name)
        currentPreset = try {
            AppThemePreset.valueOf(presetName ?: AppThemePreset.ROYAL_PURPLE.name)
        } catch (e: Exception) {
            AppThemePreset.ROYAL_PURPLE
        }
        darkModePref = try {
            DarkModePreference.valueOf(modeName ?: DarkModePreference.SYSTEM.name)
        } catch (e: Exception) {
            DarkModePreference.SYSTEM
        }
    }

    fun updatePreset(context: Context, newPreset: AppThemePreset) {
        currentPreset = newPreset
        context.getSharedPreferences("spokenguru_theme", Context.MODE_PRIVATE)
            .edit()
            .putString("theme_preset", newPreset.name)
            .apply()
    }

    fun updateDarkMode(context: Context, newMode: DarkModePreference) {
        darkModePref = newMode
        context.getSharedPreferences("spokenguru_theme", Context.MODE_PRIVATE)
            .edit()
            .putString("dark_mode", newMode.name)
            .apply()
    }
}

package com.spokenguru.app.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color

// Dynamic Theme Palette getters (backed by LocalAppThemePalette)
val GuruPurple: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppThemePalette.current.primary

val GuruPurpleDark: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppThemePalette.current.primaryDark

val GuruPurpleDeep: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppThemePalette.current.primaryDeep

val GuruPurpleLight: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppThemePalette.current.primaryLight

val GuruPurpleBorder: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppThemePalette.current.primaryBorder

val GuruMint = Color(0xFF10B981)
val GuruMintLight = Color(0xFFE6FBF2)
val GuruMintDark = Color(0xFF047857)

val GuruRed = Color(0xFFE11D48)
val GuruRedLight = Color(0xFFFEECEE)
val GuruRedDark = Color(0xFFBE123C)

val GuruAmber = Color(0xFFD97706)
val GuruAmberLight = Color(0xFFFEF9E7)

val GuruBlue = Color(0xFF2563EB)
val GuruBlueLight = Color(0xFFEBF5FF)

val GuruBg: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppThemePalette.current.lightBg

val GuruCardBorder = Color(0xFFECEAFE)
val GuruCardBorderDashed = Color(0xFFDDD8FD)
val GuruTextDark = Color(0xFF1E1B4B)
val GuruTextMuted = Color(0xFF6B7280)
val GuruTextSubtle = Color(0xFF9CA3AF)

// Dark Theme Variants
val GuruDarkBg: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppThemePalette.current.darkBg

val GuruDarkSurface: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppThemePalette.current.darkSurface

val GuruDarkCardBorder: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppThemePalette.current.darkCardBorder

val GuruDarkText = Color(0xFFF5F3FF)
val GuruDarkTextMuted = Color(0xFFA5B4FC)

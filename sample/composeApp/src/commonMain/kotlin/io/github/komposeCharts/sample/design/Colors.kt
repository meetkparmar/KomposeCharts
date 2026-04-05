package io.github.komposeCharts.sample.design

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

// ── Dark palette ───────────────────────────────────────────────────────────────

private val Dark_Primary = Color(0xFF4FC3DC)
private val Dark_OnPrimary = Color(0xFF003544)
private val Dark_PrimaryContainer = Color(0xFF005B71)
private val Dark_OnPrimaryContainer = Color(0xFFB3ECFA)

private val Dark_Secondary = Color(0xFF8FCDD9)
private val Dark_OnSecondary = Color(0xFF1A3840)
private val Dark_SecondaryContainer = Color(0xFF253E45)
private val Dark_OnSecondaryContainer = Color(0xFFCCE8EE)

private val Dark_Tertiary = Color(0xFFA1E4FE)
private val Dark_OnTertiary = Color(0xFF003F52)
private val Dark_TertiaryContainer = Color(0xFF004F65)
private val Dark_OnTertiaryContainer = Color(0xFFCAEBFA)

private val Dark_Background = Color(0xFF0D1517)
private val Dark_OnBackground = Color(0xFFDDE8EA)

private val Dark_Surface = Color(0xFF121A1C)
private val Dark_OnSurface = Color(0xFFDDE8EA)
private val Dark_SurfaceVariant = Color(0xFF243538)
private val Dark_OnSurfaceVariant = Color(0xFFA5BDC1)

private val Dark_SurfaceContainerLowest = Color(0xFF0D1517)
private val Dark_SurfaceContainerLow = Color(0xFF171F22)
private val Dark_SurfaceContainer = Color(0xFF1C2629)
private val Dark_SurfaceContainerHigh = Color(0xFF222E31)
private val Dark_SurfaceContainerHighest = Color(0xFF2D3E42)

private val Dark_Outline = Color(0xFF4A6468)
private val Dark_OutlineVariant = Color(0xFF1E2E31)

private val Dark_Error = Color(0xFFFFB4AB)
private val Dark_OnError = Color(0xFF690005)

// ── Light palette ──────────────────────────────────────────────────────────────

private val Light_Primary = Color(0xFF00687F)
private val Light_OnPrimary = Color(0xFFFFFFFF)
private val Light_PrimaryContainer = Color(0xFFB3ECFA)
private val Light_OnPrimaryContainer = Color(0xFF001F2A)

private val Light_Secondary = Color(0xFF4A6970)
private val Light_OnSecondary = Color(0xFFFFFFFF)
private val Light_SecondaryContainer = Color(0xFFCCE8EE)
private val Light_OnSecondaryContainer = Color(0xFF051F24)

private val Light_Tertiary = Color(0xFF006783)
private val Light_OnTertiary = Color(0xFFFFFFFF)
private val Light_TertiaryContainer = Color(0xFFCAEBFA)
private val Light_OnTertiaryContainer = Color(0xFF001F2A)

private val Light_Background = Color(0xFFF5FAFB)
private val Light_OnBackground = Color(0xFF171C1E)

private val Light_Surface = Color(0xFFF5FAFB)
private val Light_OnSurface = Color(0xFF171C1E)
private val Light_SurfaceVariant = Color(0xFFDBE4E7)
private val Light_OnSurfaceVariant = Color(0xFF3F4849)

private val Light_SurfaceContainerLowest = Color(0xFFFFFFFF)
private val Light_SurfaceContainerLow = Color(0xFFEFF4F6)
private val Light_SurfaceContainer = Color(0xFFE9EFF1)
private val Light_SurfaceContainerHigh = Color(0xFFE3E9EB)
private val Light_SurfaceContainerHighest = Color(0xFFDDE4E6)

private val Light_Outline = Color(0xFF6F7980)
private val Light_OutlineVariant = Color(0xFFBFC8CA)

private val Light_Error = Color(0xFFBA1A1A)
private val Light_OnError = Color(0xFFFFFFFF)

// ── Material3 color schemes ────────────────────────────────────────────────────

internal val AnalyticalGalleryDarkColorScheme = darkColorScheme(
    primary = Dark_Primary,
    onPrimary = Dark_OnPrimary,
    primaryContainer = Dark_PrimaryContainer,
    onPrimaryContainer = Dark_OnPrimaryContainer,
    secondary = Dark_Secondary,
    onSecondary = Dark_OnSecondary,
    secondaryContainer = Dark_SecondaryContainer,
    onSecondaryContainer = Dark_OnSecondaryContainer,
    tertiary = Dark_Tertiary,
    onTertiary = Dark_OnTertiary,
    tertiaryContainer = Dark_TertiaryContainer,
    onTertiaryContainer = Dark_OnTertiaryContainer,
    background = Dark_Background,
    onBackground = Dark_OnBackground,
    surface = Dark_Surface,
    onSurface = Dark_OnSurface,
    surfaceVariant = Dark_SurfaceVariant,
    onSurfaceVariant = Dark_OnSurfaceVariant,
    surfaceContainerLowest = Dark_SurfaceContainerLowest,
    surfaceContainerLow = Dark_SurfaceContainerLow,
    surfaceContainer = Dark_SurfaceContainer,
    surfaceContainerHigh = Dark_SurfaceContainerHigh,
    surfaceContainerHighest = Dark_SurfaceContainerHighest,
    outline = Dark_Outline,
    outlineVariant = Dark_OutlineVariant,
    error = Dark_Error,
    onError = Dark_OnError,
)

internal val AnalyticalGalleryLightColorScheme = lightColorScheme(
    primary = Light_Primary,
    onPrimary = Light_OnPrimary,
    primaryContainer = Light_PrimaryContainer,
    onPrimaryContainer = Light_OnPrimaryContainer,
    secondary = Light_Secondary,
    onSecondary = Light_OnSecondary,
    secondaryContainer = Light_SecondaryContainer,
    onSecondaryContainer = Light_OnSecondaryContainer,
    tertiary = Light_Tertiary,
    onTertiary = Light_OnTertiary,
    tertiaryContainer = Light_TertiaryContainer,
    onTertiaryContainer = Light_OnTertiaryContainer,
    background = Light_Background,
    onBackground = Light_OnBackground,
    surface = Light_Surface,
    onSurface = Light_OnSurface,
    surfaceVariant = Light_SurfaceVariant,
    onSurfaceVariant = Light_OnSurfaceVariant,
    surfaceContainerLowest = Light_SurfaceContainerLowest,
    surfaceContainerLow = Light_SurfaceContainerLow,
    surfaceContainer = Light_SurfaceContainer,
    surfaceContainerHigh = Light_SurfaceContainerHigh,
    surfaceContainerHighest = Light_SurfaceContainerHighest,
    outline = Light_Outline,
    outlineVariant = Light_OutlineVariant,
    error = Light_Error,
    onError = Light_OnError,
)

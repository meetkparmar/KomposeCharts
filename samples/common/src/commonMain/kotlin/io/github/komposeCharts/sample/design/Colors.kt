package io.github.komposeCharts.sample.design

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

// ── Design-system color tokens ───────────────────────────────────────────────

object AppColors {
    // Neutrals - text & ink
    val Ink = Color(0xFF18181B)
    val Body = Color(0xFF3F3F46)
    val Muted = Color(0xFF52525B)
    val Secondary = Color(0xFF71717A)
    val Faint = Color(0xFFA1A1AA)
    val Disabled = Color(0xFFC7C7CC)

    // Surfaces
    val Canvas = Color(0xFFEBECEE)
    val App = Color(0xFFF7F7F8)
    val Panel = Color(0xFFFAFAFB)
    val Card = Color(0xFFFFFFFF)
    val Code = Color(0xFF1A1A1E)
    val CodeText = Color(0xFFE7E7EA)

    // Borders & hairlines
    val Hairline = Color(0xFFF4F4F5)
    val Divider = Color(0xFFF1F1F3)
    val Border = Color(0xFFECECEE)
    val BorderStrong = Color(0xFFE4E4E7)

    // Accent
    val AccentDefault = Color(0xFF4F46E5)

    // Convenience aliases
    val Surface get() = Card
    val Background get() = Canvas
    val SurfaceMuted get() = Panel
    val SurfaceChip get() = Hairline
    val BorderSubtle get() = Border
    val BorderMuted get() = Divider
    val BorderFaint get() = Hairline
    val TextPrimary get() = Ink
    val TextSecondary get() = Muted
    val TextMuted get() = Secondary
    val TextFaint get() = Faint
    val Chevron get() = Disabled
    val White = Color(0xFFFFFFFF)
    val Transparent = Color.Transparent
    val CodeBackground get() = Code

    // Accent palette
    val AccentPalette = listOf(
        Color(0xFF4F46E5), Color(0xFF2563EB), Color(0xFF0EA5E9), Color(0xFF0891B2),
        Color(0xFF10B981), Color(0xFF16A34A), Color(0xFFCA8A04), Color(0xFFEA580C),
        Color(0xFFE11D48), Color(0xFFDB2777), Color(0xFF9333EA), Color(0xFF18181B),
    )

    // Chart palettes
    val ChartMono = listOf(
        Color(0xFF4F46E5), Color(0xFF818CF8), Color(0xFF52525B),
        Color(0xFFA1A1AA), Color(0xFFD4D4D8),
    )
    val ChartCategorical = listOf(
        Color(0xFF4F46E5), Color(0xFF0EA5E9), Color(0xFF10B981),
        Color(0xFFF59E0B), Color(0xFFEF4444), Color(0xFF8B5CF6),
    )
}

// ── Dark palette (indigo accent) ──────────────────────────────────────────────

private val Dark_Primary = Color(0xFF818CF8)           // indigo-400
private val Dark_OnPrimary = Color(0xFF1E1B4B)         // indigo-950
private val Dark_PrimaryContainer = Color(0xFF312E81)  // indigo-900
private val Dark_OnPrimaryContainer = Color(0xFFC7D2FE) // indigo-200

private val Dark_Secondary = Color(0xFFA5B4FC)         // indigo-300
private val Dark_OnSecondary = Color(0xFF1E1B4B)
private val Dark_SecondaryContainer = Color(0xFF3730A3) // indigo-800
private val Dark_OnSecondaryContainer = Color(0xFFE0E7FF) // indigo-100

private val Dark_Tertiary = Color(0xFF93C5FD)          // blue-300
private val Dark_OnTertiary = Color(0xFF1E3A5F)
private val Dark_TertiaryContainer = Color(0xFF1E40AF) // blue-800
private val Dark_OnTertiaryContainer = Color(0xFFDBEAFE) // blue-100

private val Dark_Background = Color(0xFF0C0C0F)
private val Dark_OnBackground = Color(0xFFE4E4E7)

private val Dark_Surface = Color(0xFF111114)
private val Dark_OnSurface = Color(0xFFE4E4E7)
private val Dark_SurfaceVariant = Color(0xFF27272A)
private val Dark_OnSurfaceVariant = Color(0xFFA1A1AA)

private val Dark_SurfaceContainerLowest = Color(0xFF0C0C0F)
private val Dark_SurfaceContainerLow = Color(0xFF18181B)
private val Dark_SurfaceContainer = Color(0xFF1F1F23)
private val Dark_SurfaceContainerHigh = Color(0xFF27272A)
private val Dark_SurfaceContainerHighest = Color(0xFF3F3F46)

private val Dark_Outline = Color(0xFF52525B)
private val Dark_OutlineVariant = Color(0xFF27272A)

private val Dark_Error = Color(0xFFFFB4AB)
private val Dark_OnError = Color(0xFF690005)

// ── Light palette (indigo accent) ─────────────────────────────────────────────

private val Light_Primary = Color(0xFF4F46E5)          // indigo-600
private val Light_OnPrimary = Color(0xFFFFFFFF)
private val Light_PrimaryContainer = Color(0xFFE0E7FF) // indigo-100
private val Light_OnPrimaryContainer = Color(0xFF1E1B4B) // indigo-950

private val Light_Secondary = Color(0xFF6366F1)        // indigo-500
private val Light_OnSecondary = Color(0xFFFFFFFF)
private val Light_SecondaryContainer = Color(0xFFC7D2FE) // indigo-200
private val Light_OnSecondaryContainer = Color(0xFF312E81) // indigo-900

private val Light_Tertiary = Color(0xFF2563EB)         // blue-600
private val Light_OnTertiary = Color(0xFFFFFFFF)
private val Light_TertiaryContainer = Color(0xFFDBEAFE) // blue-100
private val Light_OnTertiaryContainer = Color(0xFF1E3A5F)

private val Light_Background = Color(0xFFEBECEE)       // Canvas
private val Light_OnBackground = Color(0xFF18181B)      // Ink

private val Light_Surface = Color(0xFFF7F7F8)           // App
private val Light_OnSurface = Color(0xFF18181B)         // Ink
private val Light_SurfaceVariant = Color(0xFFE4E4E7)    // Strong border
private val Light_OnSurfaceVariant = Color(0xFF52525B)  // Muted

private val Light_SurfaceContainerLowest = Color(0xFFFFFFFF) // Card
private val Light_SurfaceContainerLow = Color(0xFFFAFAFB)   // Panel
private val Light_SurfaceContainer = Color(0xFFF7F7F8)      // App
private val Light_SurfaceContainerHigh = Color(0xFFF1F1F3)  // Divider-ish
private val Light_SurfaceContainerHighest = Color(0xFFEBECEE) // Canvas

private val Light_Outline = Color(0xFF71717A)           // Secondary
private val Light_OutlineVariant = Color(0xFFE4E4E7)    // Strong border

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

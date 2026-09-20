package com.example.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import java.util.Calendar

enum class TimeOfDay(
    val displayName: String,
    val icon: String,
    val timeSpan: String,
    val description: String
) {
    DAWN("Sunrise Dawn", "🌅", "5:00 - 8:59 AM", "Warm peach & morning golden dew glass"),
    DAY("Snap Noon", "☀️", "9:00 AM - 5:59 PM", "Iconic vibrant Snapchat electric yellow glass"),
    SUNSET("Twilight Dusk", "🌆", "6:00 - 8:59 PM", "Fiery twilight coral & radiant amethyst glow"),
    NIGHT("Midnight Stealth", "🌙", "9:00 PM - 4:59 AM", "Encrypted stealth obsidian & neon yellow refraction");

    companion object {
        fun fromCurrentHour(): TimeOfDay {
            val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
            return when (hour) {
                in 5..8 -> DAWN
                in 9..17 -> DAY
                in 18..20 -> SUNSET
                else -> NIGHT
            }
        }
    }
}

data class LiquidGlassPalette(
    val timeOfDay: TimeOfDay,
    // Background gradient
    val backgroundBrush: Brush,
    val ambientGlowBrush: Brush,
    // Glass surface tokens
    val glassCardBackground: Color,
    val glassCardBorder: Color,
    val glassCardInnerGlow: Color,
    val glassHeaderBackground: Color,
    val glassHeaderBorder: Color,
    // Accent colors (Snapchat yellow variants per time of day)
    val snapPrimaryYellow: Color,
    val accentSecondary: Color,
    val accentTertiary: Color,
    // Text colors
    val textPrimary: Color,
    val textSecondary: Color,
    val textTertiary: Color,
    val onAccentText: Color,
    // Chat bubble specific
    val myBubbleBrush: Brush,
    val myBubbleBorder: Color,
    val myBubbleText: Color,
    val friendBubbleBrush: Brush,
    val friendBubbleBorder: Color,
    val friendBubbleText: Color,
    val savedMessageBorder: Color,
    val ephemeralBurnAccent: Color,
    // Surface highlights
    val chipBackground: Color,
    val chipBorder: Color,
    val inputBarBackground: Color
)

val DawnPalette = LiquidGlassPalette(
    timeOfDay = TimeOfDay.DAWN,
    backgroundBrush = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF1F162B),
            Color(0xFF2E1C38),
            Color(0xFF452244),
            Color(0xFF2A172D)
        )
    ),
    ambientGlowBrush = Brush.radialGradient(
        colors = listOf(
            Color(0xFFFF9E7D).copy(alpha = 0.35f),
            Color(0xFFFFD166).copy(alpha = 0.20f),
            Color.Transparent
        )
    ),
    glassCardBackground = Color(0x33FFE0C7),
    glassCardBorder = Color(0x55FFC8A2),
    glassCardInnerGlow = Color(0x22FFFFFF),
    glassHeaderBackground = Color(0x662A172D),
    glassHeaderBorder = Color(0x44FFB58A),
    snapPrimaryYellow = Color(0xFFFFD152),
    accentSecondary = Color(0xFFFF7E67),
    accentTertiary = Color(0xFFFFB085),
    textPrimary = Color(0xFFFFF7F2),
    textSecondary = Color(0xFFE2C4B8),
    textTertiary = Color(0xFFA58D84),
    onAccentText = Color(0xFF1E1005),
    myBubbleBrush = Brush.linearGradient(
        colors = listOf(
            Color(0xFFFFD152),
            Color(0xFFFF9E7D)
        )
    ),
    myBubbleBorder = Color(0x88FFE6B0),
    myBubbleText = Color(0xFF231003),
    friendBubbleBrush = Brush.linearGradient(
        colors = listOf(
            Color(0x555C3754),
            Color(0x33442340)
        )
    ),
    friendBubbleBorder = Color(0x66FFB58A),
    friendBubbleText = Color(0xFFFFF5F0),
    savedMessageBorder = Color(0xFFFFD152),
    ephemeralBurnAccent = Color(0xFFFF5252),
    chipBackground = Color(0x38FFFFFF),
    chipBorder = Color(0x55FFD152),
    inputBarBackground = Color(0x55321A36)
)

val DayPalette = LiquidGlassPalette(
    timeOfDay = TimeOfDay.DAY,
    backgroundBrush = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF0F141C),
            Color(0xFF131D2A),
            Color(0xFF1A2636),
            Color(0xFF0D131B)
        )
    ),
    ambientGlowBrush = Brush.radialGradient(
        colors = listOf(
            Color(0xFFFFFC00).copy(alpha = 0.28f),
            Color(0xFF00E5FF).copy(alpha = 0.15f),
            Color.Transparent
        )
    ),
    glassCardBackground = Color(0x2EFFFFFF),
    glassCardBorder = Color(0x4AFFFFFF),
    glassCardInnerGlow = Color(0x33FFFFFF),
    glassHeaderBackground = Color(0x66101724),
    glassHeaderBorder = Color(0x44FFFFFF),
    snapPrimaryYellow = Color(0xFFFFFC00), // Classic electric Snapchat yellow
    accentSecondary = Color(0xFF00E5FF),
    accentTertiary = Color(0xFF00F076),
    textPrimary = Color(0xFFFFFFFF),
    textSecondary = Color(0xFFC7D3E0),
    textTertiary = Color(0xFF8898AA),
    onAccentText = Color(0xFF000000),
    myBubbleBrush = Brush.linearGradient(
        colors = listOf(
            Color(0xFFFFFC00),
            Color(0xFFFFE600)
        )
    ),
    myBubbleBorder = Color(0xAAFFFFFF),
    myBubbleText = Color(0xFF000000),
    friendBubbleBrush = Brush.linearGradient(
        colors = listOf(
            Color(0x4D2A3A4F),
            Color(0x331C2837)
        )
    ),
    friendBubbleBorder = Color(0x5580C4FF),
    friendBubbleText = Color(0xFFF0F6FF),
    savedMessageBorder = Color(0xFFFFFC00),
    ephemeralBurnAccent = Color(0xFFFF2E63),
    chipBackground = Color(0x33FFFFFF),
    chipBorder = Color(0x66FFFC00),
    inputBarBackground = Color(0x66182333)
)

val SunsetPalette = LiquidGlassPalette(
    timeOfDay = TimeOfDay.SUNSET,
    backgroundBrush = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF180A28),
            Color(0xFF2C0B3A),
            Color(0xFF3D0C38),
            Color(0xFF210720)
        )
    ),
    ambientGlowBrush = Brush.radialGradient(
        colors = listOf(
            Color(0xFFFF2A85).copy(alpha = 0.35f),
            Color(0xFFFF7B00).copy(alpha = 0.22f),
            Color.Transparent
        )
    ),
    glassCardBackground = Color(0x33FFB3D9),
    glassCardBorder = Color(0x55FF60AA),
    glassCardInnerGlow = Color(0x33FFFFFF),
    glassHeaderBackground = Color(0x73210720),
    glassHeaderBorder = Color(0x4DFF52A5),
    snapPrimaryYellow = Color(0xFFFFD000),
    accentSecondary = Color(0xFFFF1493),
    accentTertiary = Color(0xFFFF6F00),
    textPrimary = Color(0xFFFFF0F7),
    textSecondary = Color(0xFFE9BCD4),
    textTertiary = Color(0xFFA57E94),
    onAccentText = Color(0xFF200318),
    myBubbleBrush = Brush.linearGradient(
        colors = listOf(
            Color(0xFFFF7B00),
            Color(0xFFFF1493)
        )
    ),
    myBubbleBorder = Color(0x99FFA3C8),
    myBubbleText = Color(0xFFFFFFFF),
    friendBubbleBrush = Brush.linearGradient(
        colors = listOf(
            Color(0x5952134E),
            Color(0x38340733)
        )
    ),
    friendBubbleBorder = Color(0x66FF77BC),
    friendBubbleText = Color(0xFFFFF0F8),
    savedMessageBorder = Color(0xFFFFD000),
    ephemeralBurnAccent = Color(0xFFFF1744),
    chipBackground = Color(0x3BFFFFFF),
    chipBorder = Color(0x77FF1493),
    inputBarBackground = Color(0x662E0B38)
)

val NightPalette = LiquidGlassPalette(
    timeOfDay = TimeOfDay.NIGHT,
    backgroundBrush = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF06090E),
            Color(0xFF0B101A),
            Color(0xFF0F1522),
            Color(0xFF04070B)
        )
    ),
    ambientGlowBrush = Brush.radialGradient(
        colors = listOf(
            Color(0xFFFFFC00).copy(alpha = 0.18f),
            Color(0xFF38EF7D).copy(alpha = 0.15f),
            Color.Transparent
        )
    ),
    glassCardBackground = Color(0x24162438),
    glassCardBorder = Color(0x403A5880),
    glassCardInnerGlow = Color(0x18FFFFFF),
    glassHeaderBackground = Color(0x80080D16),
    glassHeaderBorder = Color(0x334E75A6),
    snapPrimaryYellow = Color(0xFFFFEE00), // Bioluminescent neon yellow
    accentSecondary = Color(0xFF00FFC2),
    accentTertiary = Color(0xFF7000FF),
    textPrimary = Color(0xFFE6EDF5),
    textSecondary = Color(0xFF91A3B8),
    textTertiary = Color(0xFF5A6C80),
    onAccentText = Color(0xFF000000),
    myBubbleBrush = Brush.linearGradient(
        colors = listOf(
            Color(0xFFFFEE00),
            Color(0xFFB8B000)
        )
    ),
    myBubbleBorder = Color(0x80FFF566),
    myBubbleText = Color(0xFF000000),
    friendBubbleBrush = Brush.linearGradient(
        colors = listOf(
            Color(0x4D132238),
            Color(0x330B1422)
        )
    ),
    friendBubbleBorder = Color(0x5500FFC2),
    friendBubbleText = Color(0xFFDEF2FF),
    savedMessageBorder = Color(0xFFFFEE00),
    ephemeralBurnAccent = Color(0xFFFF3366),
    chipBackground = Color(0x281B2B3F),
    chipBorder = Color(0x5500FFC2),
    inputBarBackground = Color(0x800A1220)
)

fun getPaletteForTime(timeOfDay: TimeOfDay): LiquidGlassPalette {
    return when (timeOfDay) {
        TimeOfDay.DAWN -> DawnPalette
        TimeOfDay.DAY -> DayPalette
        TimeOfDay.SUNSET -> SunsetPalette
        TimeOfDay.NIGHT -> NightPalette
    }
}

val LocalLiquidGlassPalette = staticCompositionLocalOf { DayPalette }

object GlassTheme {
    val palette: LiquidGlassPalette
        @Composable
        @ReadOnlyComposable
        get() = LocalLiquidGlassPalette.current
}

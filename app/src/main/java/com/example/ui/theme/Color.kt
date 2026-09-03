package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// FocusMate "Immersive UI" Theme Palette
val FocusBackground = Color(0xFF111317) // Deep Obsidian Canvas
val FocusBackgroundAlt = Color(0xFF0D0F13) // Darker Layer Canvas
val FocusSurface = Color(0xFF161920) // Primary Card Surface
val FocusSurfaceVariant = Color(0xFF1E232C) // Elevated Card Surface
val FocusSurfaceElevated = Color(0xFF272D38) // Interactive Hover / High Element Surface

val FocusPrimary = Color(0xFFFFBF00) // Radiant Gold / Amber
val FocusPrimaryVariant = Color(0xFFFFAB00)
val FocusPrimaryContainer = Color(0x29FFBF00) // 16% opacity warm amber pill tint
val FocusPrimaryBorder = Color(0x59FFBF00) // Crisp amber outline
val FocusOnPrimary = Color(0xFF111317) // Dark obsidian on amber

val FocusSecondary = Color(0xFF6366F1) // Electric Indigo accent
val FocusSecondaryContainer = Color(0x266366F1)
val FocusOnSecondary = Color(0xFFFFFFFF)

val FocusTextPrimary = Color(0xFFE2E2E8) // Crisp luminous high-contrast text
val FocusTextSecondary = Color(0xFF9CA3AF) // Cool slate secondary text
val FocusTextTertiary = Color(0xFF6B7280) // Muted caption slate

val FocusBorder = Color(0xFF2A303D) // Refined subtle card outline
val FocusBorderSubtle = Color(0xFF1F242E) // Ultra-delicate divider outline
val FocusBorderGlow = Color(0x40FFBF00) // Glowing amber perimeter

val FocusError = Color(0xFFFF6B6B) // Soft warm coral error
val FocusOnError = Color(0xFF380004)
val FocusSuccess = Color(0xFF34D399) // Mint emerald success
val FocusStreakFlame = Color(0xFFFF7A00) // Radiant streak flame
val FocusCardGlow = Color(0x24FFBF00) // Ambient card highlight

// Gradients for Immersive Surfaces
val ImmersiveCardGradient = Brush.linearGradient(
  listOf(Color(0xFF1F242E), Color(0xFF161920))
)

val ImmersiveHeroGradient = Brush.linearGradient(
  listOf(Color(0xFF242B38), Color(0xFF161921))
)

val ImmersiveGoldGradient = Brush.horizontalGradient(
  listOf(Color(0xFFFFBF00), Color(0xFFFF9500))
)


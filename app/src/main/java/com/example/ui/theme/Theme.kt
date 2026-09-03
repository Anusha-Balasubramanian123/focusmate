package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val FocusMateColorScheme = darkColorScheme(
  primary = FocusPrimary,
  onPrimary = FocusOnPrimary,
  primaryContainer = FocusPrimaryContainer,
  onPrimaryContainer = FocusPrimary,

  secondary = FocusSecondary,
  onSecondary = FocusOnSecondary,
  secondaryContainer = FocusSecondaryContainer,
  onSecondaryContainer = FocusTextPrimary,

  background = FocusBackground,
  onBackground = FocusTextPrimary,

  surface = FocusSurface,
  onSurface = FocusTextPrimary,
  surfaceVariant = FocusSurfaceVariant,
  onSurfaceVariant = FocusTextSecondary,

  outline = FocusBorder,
  outlineVariant = FocusBorderSubtle,

  error = FocusError,
  onError = FocusOnError,
)

@Composable
fun FocusMateTheme(
  content: @Composable () -> Unit,
) {
  MaterialTheme(
    colorScheme = FocusMateColorScheme,
    typography = Typography,
    content = content,
  )
}

package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Adjust
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.outlined.Adjust
import androidx.compose.material.icons.outlined.Checklist
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.FocusBackground
import com.example.ui.theme.FocusBorder
import com.example.ui.theme.FocusBorderSubtle
import com.example.ui.theme.FocusPrimary
import com.example.ui.theme.FocusPrimaryBorder
import com.example.ui.theme.FocusPrimaryContainer
import com.example.ui.theme.FocusSurface
import com.example.ui.theme.FocusTextPrimary
import com.example.ui.theme.FocusTextSecondary
import com.example.ui.theme.FocusTextTertiary

enum class FocusMateScreen(
  val route: String,
  val label: String,
  val activeIcon: ImageVector,
  val inactiveIcon: ImageVector
) {
  HOME("home", "Home", Icons.Filled.Home, Icons.Outlined.Home),
  TASKS("tasks", "Tasks", Icons.Filled.Checklist, Icons.Outlined.Checklist),
  FOCUS("focus", "Focus", Icons.Filled.Adjust, Icons.Outlined.Adjust),
  PROGRESS("progress", "Progress", Icons.Filled.TrendingUp, Icons.Outlined.TrendingUp),
  PROFILE("profile", "Profile", Icons.Filled.Person, Icons.Outlined.Person)
}

@Composable
fun FocusBottomNavigation(
  currentScreen: FocusMateScreen,
  onScreenSelected: (FocusMateScreen) -> Unit,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .fillMaxWidth()
      .background(FocusSurface)
      .border(
        width = 1.dp,
        color = FocusBorderSubtle,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
      )
      .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
      .windowInsetsPadding(WindowInsets.navigationBars)
      .testTag("focus_bottom_navigation")
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .height(68.dp)
        .padding(horizontal = 8.dp),
      horizontalArrangement = Arrangement.SpaceAround,
      verticalAlignment = Alignment.CenterVertically
    ) {
      FocusMateScreen.entries.forEach { screen ->
        val isSelected = currentScreen == screen

        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.Center,
          modifier = Modifier
            .weight(1f)
            .height(56.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable { onScreenSelected(screen) }
            .testTag("nav_item_${screen.route}")
        ) {
          Box(
            modifier = Modifier
              .size(40.dp, 28.dp)
              .clip(RoundedCornerShape(14.dp))
              .background(if (isSelected) FocusPrimaryContainer else androidx.compose.ui.graphics.Color.Transparent)
              .then(
                if (isSelected) Modifier.border(1.dp, FocusPrimaryBorder, RoundedCornerShape(14.dp))
                else Modifier
              ),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = if (isSelected) screen.activeIcon else screen.inactiveIcon,
              contentDescription = screen.label,
              tint = if (isSelected) FocusPrimary else FocusTextTertiary,
              modifier = Modifier.size(19.dp)
            )
          }

          Spacer(modifier = Modifier.height(3.dp))

          Text(
            text = screen.label,
            style = MaterialTheme.typography.labelSmall,
            color = if (isSelected) FocusPrimary else FocusTextTertiary,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            fontSize = 11.sp
          )
        }
      }
    }
  }
}

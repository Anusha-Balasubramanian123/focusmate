package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.StatisticCard
import com.example.ui.state.FocusMateViewModel
import com.example.ui.theme.FocusBackground
import com.example.ui.theme.FocusBorderGlow
import com.example.ui.theme.FocusBorderSubtle
import com.example.ui.theme.FocusCardGlow
import com.example.ui.theme.FocusOnPrimary
import com.example.ui.theme.FocusPrimary
import com.example.ui.theme.FocusPrimaryContainer
import com.example.ui.theme.FocusSurface
import com.example.ui.theme.FocusSurfaceVariant
import com.example.ui.theme.FocusTextPrimary
import com.example.ui.theme.FocusTextSecondary
import com.example.ui.theme.FocusTextTertiary
import com.example.ui.theme.ImmersiveHeroGradient

@Composable
fun ProfileScreen(
  viewModel: FocusMateViewModel,
  modifier: Modifier = Modifier
) {
  val currentStreak by viewModel.currentStreak.collectAsState()
  val bestStreak by viewModel.bestStreak.collectAsState()
  val tasks by viewModel.tasks.collectAsState()
  val completedTasksCount = tasks.count { it.completed }
  val notificationsEnabled by viewModel.notificationsEnabled.collectAsState()
  val soundEnabled by viewModel.soundEnabled.collectAsState()
  val hapticEnabled by viewModel.hapticEnabled.collectAsState()

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(FocusBackground)
  ) {
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 20.dp)
        .testTag("profile_screen_content"),
      contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // Header
      item {
        Column(modifier = Modifier.fillMaxWidth()) {
          Text(
            text = "Profile & Settings",
            style = MaterialTheme.typography.headlineLarge,
            color = FocusTextPrimary,
            fontWeight = FontWeight.Bold
          )
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = "FocusMate Consistency Profile",
            style = MaterialTheme.typography.bodySmall,
            color = FocusTextSecondary
          )
        }
      }

      // User Profile Card
      item {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(ImmersiveHeroGradient)
            .border(1.dp, FocusBorderGlow, RoundedCornerShape(20.dp))
            .padding(18.dp)
            .testTag("user_profile_card")
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
          ) {
            // Avatar
            Box(
              modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(FocusPrimaryContainer)
                .border(2.dp, FocusPrimary, CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Text("A", style = MaterialTheme.typography.headlineMedium, color = FocusPrimary, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
              Text(
                text = "Anusha",
                style = MaterialTheme.typography.titleLarge,
                color = FocusTextPrimary,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = "anushabalasubramanian123@gmail.com",
                style = MaterialTheme.typography.bodySmall,
                color = FocusTextSecondary
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "FocusMate Pro Member",
                style = MaterialTheme.typography.labelSmall,
                color = FocusPrimary,
                fontWeight = FontWeight.SemiBold
              )
            }
          }
        }
      }

      // Productivity Summary
      item {
        Text(
          text = "PRODUCTIVITY SUMMARY",
          style = MaterialTheme.typography.labelSmall,
          color = FocusTextTertiary,
          fontWeight = FontWeight.Bold,
          letterSpacing = 1.sp
        )
      }

      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          StatisticCard(
            title = "Current",
            value = "$currentStreak days",
            subtitle = "Streak",
            iconEmoji = "🔥",
            modifier = Modifier.weight(1f)
          )
          StatisticCard(
            title = "Best",
            value = "$bestStreak days",
            subtitle = "Record",
            iconEmoji = "🏆",
            modifier = Modifier.weight(1f)
          )
          StatisticCard(
            title = "Completed",
            value = "$completedTasksCount",
            subtitle = "Tasks",
            iconEmoji = "✅",
            modifier = Modifier.weight(1f)
          )
        }
      }

      // Settings Group
      item {
        Text(
          text = "PREFERENCES & FEEDBACK",
          style = MaterialTheme.typography.labelSmall,
          color = FocusTextTertiary,
          fontWeight = FontWeight.Bold,
          letterSpacing = 1.sp
        )
      }

      item {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(FocusSurfaceVariant)
            .border(1.dp, FocusBorderSubtle, RoundedCornerShape(18.dp))
        ) {
          SettingsToggleRow(
            icon = Icons.Default.Notifications,
            title = "Notifications",
            subtitle = "Receive reminders before tasks and focus timers",
            checked = notificationsEnabled,
            onCheckedChange = { viewModel.toggleNotifications() }
          )

          SettingsToggleRow(
            icon = Icons.Default.VolumeUp,
            title = "Sound",
            subtitle = "Chimes on session completion and streaks",
            checked = soundEnabled,
            onCheckedChange = { viewModel.toggleSound() }
          )

          SettingsToggleRow(
            icon = Icons.Default.Vibration,
            title = "Haptic Feedback",
            subtitle = "Vibration on button presses and timer tick",
            checked = hapticEnabled,
            onCheckedChange = { viewModel.toggleHaptic() },
            isLast = true
          )
        }
      }

      // Action: Productivity Insights
      item {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(FocusSurfaceVariant)
            .border(1.dp, FocusBorderSubtle, RoundedCornerShape(16.dp))
            .clickable { viewModel.openInsights() }
            .padding(16.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            Icon(Icons.Default.TrendingUp, contentDescription = null, tint = FocusPrimary)
            Column {
              Text("Productivity Insights", style = MaterialTheme.typography.titleMedium, color = FocusTextPrimary, fontWeight = FontWeight.SemiBold)
              Text("View detailed pattern analysis", style = MaterialTheme.typography.bodySmall, color = FocusTextSecondary)
            }
          }
          Icon(Icons.Default.ChevronRight, contentDescription = null, tint = FocusTextTertiary)
        }
      }

      // About FocusMate Section
      item {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(FocusSurfaceVariant)
            .border(1.dp, FocusBorderSubtle, RoundedCornerShape(16.dp))
            .padding(18.dp)
        ) {
          Column {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Icon(Icons.Default.Info, contentDescription = null, tint = FocusPrimary, modifier = Modifier.size(16.dp))
              Text(
                text = "ABOUT FOCUSMATE",
                style = MaterialTheme.typography.labelSmall,
                color = FocusPrimary,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
              )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = "FocusMate v1.0.0",
              style = MaterialTheme.typography.titleMedium,
              color = FocusTextPrimary,
              fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "\"Plan your day. Focus deeply. Finish consistently.\"",
              style = MaterialTheme.typography.bodyMedium,
              color = FocusTextSecondary
            )
          }
        }
      }
    }
  }
}

@Composable
private fun SettingsToggleRow(
  icon: ImageVector,
  title: String,
  subtitle: String,
  checked: Boolean,
  onCheckedChange: (Boolean) -> Unit,
  isLast: Boolean = false
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 12.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.weight(1f)
    ) {
      Box(
        modifier = Modifier
          .size(36.dp)
          .clip(RoundedCornerShape(8.dp))
          .background(FocusPrimaryContainer),
        contentAlignment = Alignment.Center
      ) {
        Icon(icon, contentDescription = null, tint = FocusPrimary, modifier = Modifier.size(18.dp))
      }
      Spacer(modifier = Modifier.width(12.dp))
      Column {
        Text(title, style = MaterialTheme.typography.bodyLarge, color = FocusTextPrimary, fontWeight = FontWeight.SemiBold)
        Text(subtitle, style = MaterialTheme.typography.bodySmall, color = FocusTextTertiary)
      }
    }

    Switch(
      checked = checked,
      onCheckedChange = onCheckedChange,
      colors = SwitchDefaults.colors(
        checkedThumbColor = FocusOnPrimary,
        checkedTrackColor = FocusPrimary,
        uncheckedThumbColor = FocusTextTertiary,
        uncheckedTrackColor = FocusSurface
      )
    )
  }
}

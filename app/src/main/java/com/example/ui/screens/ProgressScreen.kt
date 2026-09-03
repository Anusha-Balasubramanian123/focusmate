package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.ConsistencyCalendar
import com.example.ui.components.PrimaryButton
import com.example.ui.components.StatisticCard
import com.example.ui.components.WeeklyChart
import com.example.ui.state.FocusMateViewModel
import com.example.ui.theme.FocusBackground
import com.example.ui.theme.FocusBorderGlow
import com.example.ui.theme.FocusBorderSubtle
import com.example.ui.theme.FocusPrimary
import com.example.ui.theme.FocusSurfaceVariant
import com.example.ui.theme.FocusTextPrimary
import com.example.ui.theme.FocusTextSecondary
import com.example.ui.theme.FocusTextTertiary
import com.example.ui.theme.ImmersiveHeroGradient

@Composable
fun ProgressScreen(
  viewModel: FocusMateViewModel,
  modifier: Modifier = Modifier
) {
  val currentStreak by viewModel.currentStreak.collectAsState()
  val bestStreak by viewModel.bestStreak.collectAsState()
  val tasks by viewModel.tasks.collectAsState()
  val focusSecondsToday by viewModel.focusSecondsToday.collectAsState()
  val focusSessionCountToday by viewModel.focusSessionCountToday.collectAsState()

  val completedTasksCount = tasks.count { it.completed }
  val totalTasksCount = tasks.size
  val completionRatePercent = if (totalTasksCount > 0) {
    (completedTasksCount * 100) / totalTasksCount
  } else {
    0
  }
  val missedTasksCount = tasks.count { it.isMissed }

  val focusHours = focusSecondsToday / 3600
  val focusMinutes = (focusSecondsToday % 3600) / 60
  val focusTimeDisplay = if (focusHours > 0) "${focusHours}h ${focusMinutes}m" else "${focusMinutes}m"

  val weeklyActivity by viewModel.weeklyActivity.collectAsState()
  val weeklyData = if (weeklyActivity.isNotEmpty()) {
    weeklyActivity
  } else {
    listOf(
      "Mon" to 0,
      "Tue" to 0,
      "Wed" to 0,
      "Thu" to 0,
      "Fri" to 0,
      "Sat" to 0,
      "Sun" to 0
    )
  }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(FocusBackground)
  ) {
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 20.dp)
        .testTag("progress_screen_content"),
      contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // Header
      item {
        Column(modifier = Modifier.fillMaxWidth()) {
          Text(
            text = "Productivity & Progress",
            style = MaterialTheme.typography.headlineLarge,
            color = FocusTextPrimary,
            fontWeight = FontWeight.Bold
          )
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = "Track your consistency and focus momentum",
            style = MaterialTheme.typography.bodySmall,
            color = FocusTextSecondary
          )
        }
      }

      // Key Statistics Grid (2x2 + 1)
      item {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            StatisticCard(
              title = "Current Streak",
              value = "$currentStreak days",
              subtitle = "Active streak",
              iconEmoji = "🔥",
              modifier = Modifier.weight(1f)
            )
            StatisticCard(
              title = "Best Streak",
              value = "$bestStreak days",
              subtitle = "Personal record",
              iconEmoji = "🏆",
              modifier = Modifier.weight(1f)
            )
          }

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            StatisticCard(
              title = "Completed",
              value = "$completedTasksCount tasks",
              subtitle = if (totalTasksCount > 0) "of $totalTasksCount total" else "No tasks yet",
              iconEmoji = "✅",
              modifier = Modifier.weight(1f)
            )
            StatisticCard(
              title = "Completion Rate",
              value = "$completionRatePercent%",
              subtitle = if (completionRatePercent >= 70) "Above avg goal" else "Keep pushing",
              iconEmoji = "📈",
              modifier = Modifier.weight(1f)
            )
          }

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            StatisticCard(
              title = "Focus Time",
              value = focusTimeDisplay,
              subtitle = "$focusSessionCountToday sessions logged",
              iconEmoji = "⏱️",
              modifier = Modifier.weight(1f)
            )
            StatisticCard(
              title = "Missed Tasks",
              value = "$missedTasksCount tasks",
              subtitle = "Forgiving & reset",
              iconEmoji = "🌱",
              modifier = Modifier.weight(1f)
            )
          }
        }
      }

      // Weekly Activity Chart
      item {
        WeeklyChart(
          weeklyData = weeklyData,
          maxGoal = 8,
          todayIndex = 2 // Wednesday
        )
      }

      // Consistency Heatmap
      item {
        ConsistencyCalendar()
      }

      // Productivity Insights Navigation Button
      item {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(ImmersiveHeroGradient)
            .border(1.dp, FocusBorderGlow, RoundedCornerShape(20.dp))
            .padding(18.dp)
        ) {
          Column {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Text("✨", fontSize = 20.sp)
              Text(
                text = "PRODUCTIVITY PATTERNS",
                style = MaterialTheme.typography.labelSmall,
                color = FocusPrimary,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
              )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = "Understand your best focus hours and consistency trends.",
              style = MaterialTheme.typography.bodyMedium,
              color = FocusTextSecondary
            )
            Spacer(modifier = Modifier.height(14.dp))
            PrimaryButton(
              text = "View Productivity Insights",
              onClick = { viewModel.openInsights() },
              leadingIcon = Icons.Default.AutoAwesome,
              testTag = "view_insights_button"
            )
          }
        }
      }
    }
  }
}

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Adjust
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Task
import com.example.ui.components.FocusTimer
import com.example.ui.state.FocusMateViewModel
import com.example.ui.theme.FocusBackground
import com.example.ui.theme.FocusBorder
import com.example.ui.theme.FocusBorderSubtle
import com.example.ui.theme.FocusCardGlow
import com.example.ui.theme.FocusPrimary
import com.example.ui.theme.FocusPrimaryBorder
import com.example.ui.theme.FocusPrimaryContainer
import com.example.ui.theme.FocusSurface
import com.example.ui.theme.FocusSurfaceElevated
import com.example.ui.theme.FocusSurfaceVariant
import com.example.ui.theme.FocusTextPrimary
import com.example.ui.theme.FocusTextSecondary
import com.example.ui.theme.FocusTextTertiary
import com.example.ui.theme.ImmersiveCardGradient

@Composable
fun FocusScreen(
  viewModel: FocusMateViewModel,
  modifier: Modifier = Modifier
) {
  val remainingSeconds by viewModel.remainingSeconds.collectAsState()
  val totalDurationSeconds by viewModel.totalDurationSeconds.collectAsState()
  val isTimerRunning by viewModel.isTimerRunning.collectAsState()
  val selectedPresetMinutes by viewModel.selectedPresetMinutes.collectAsState()
  val currentFocusTask by viewModel.currentFocusTask.collectAsState()
  val sessionCountToday by viewModel.focusSessionCountToday.collectAsState()
  val focusSecondsToday by viewModel.focusSecondsToday.collectAsState()
  val allTasks by viewModel.tasks.collectAsState()

  var showTaskPicker by remember { mutableStateOf(false) }

  val hoursToday = focusSecondsToday / 3600
  val minutesToday = (focusSecondsToday % 3600) / 60
  val formattedFocusTime = "${hoursToday}h ${minutesToday}m"

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(FocusBackground)
  ) {
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 20.dp)
        .testTag("focus_screen_content"),
      contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
      // Header
      item {
        Column(
          modifier = Modifier.fillMaxWidth(),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Text(
            text = "FOCUS MODE",
            style = MaterialTheme.typography.labelMedium,
            color = FocusPrimary,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.5.sp
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "Distraction-Free Work",
            style = MaterialTheme.typography.headlineMedium,
            color = FocusTextPrimary,
            fontWeight = FontWeight.Bold
          )
        }
      }

      // Stats Pills: Session # and Today's Focus Time
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceEvenly
        ) {
          // Session Counter Pill
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(14.dp))
              .background(FocusSurfaceVariant)
              .border(1.dp, FocusBorderSubtle, RoundedCornerShape(14.dp))
              .padding(horizontal = 16.dp, vertical = 10.dp)
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(
                text = "SESSION",
                style = MaterialTheme.typography.labelSmall,
                color = FocusTextTertiary,
                fontSize = 10.sp
              )
              Text(
                text = "Focus Session #$sessionCountToday",
                style = MaterialTheme.typography.titleMedium,
                color = FocusTextPrimary,
                fontWeight = FontWeight.Bold
              )
            }
          }

          // Today's Focus Time Pill
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(14.dp))
              .background(FocusSurfaceVariant)
              .border(1.dp, FocusBorderSubtle, RoundedCornerShape(14.dp))
              .padding(horizontal = 16.dp, vertical = 10.dp)
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(
                text = "TODAY'S FOCUS TIME",
                style = MaterialTheme.typography.labelSmall,
                color = FocusTextTertiary,
                fontSize = 10.sp
              )
              Text(
                text = formattedFocusTime,
                style = MaterialTheme.typography.titleMedium,
                color = FocusPrimary,
                fontWeight = FontWeight.Bold
              )
            }
          }
        }
      }

      // Current Focus Task Card
      item {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(ImmersiveCardGradient)
            .border(1.dp, FocusBorder, RoundedCornerShape(18.dp))
            .clickable { showTaskPicker = true }
            .padding(14.dp)
            .testTag("current_focus_task_card")
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.weight(1f)
            ) {
              Box(
                modifier = Modifier
                  .size(38.dp)
                  .clip(RoundedCornerShape(10.dp))
                  .background(FocusPrimaryContainer)
                  .border(1.dp, FocusPrimaryBorder, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.Adjust,
                  contentDescription = null,
                  tint = FocusPrimary,
                  modifier = Modifier.size(20.dp)
                )
              }
              Spacer(modifier = Modifier.width(12.dp))
              Column {
                Text(
                  text = "CURRENT TASK",
                  style = MaterialTheme.typography.labelSmall,
                  color = FocusTextTertiary,
                  fontWeight = FontWeight.SemiBold
                )
                Text(
                  text = currentFocusTask?.title ?: "Select a task to focus on",
                  style = MaterialTheme.typography.titleMedium,
                  color = FocusTextPrimary,
                  fontWeight = FontWeight.Bold,
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis
                )
              }
            }

            Icon(
              imageVector = Icons.Default.KeyboardArrowDown,
              contentDescription = "Change task",
              tint = FocusTextSecondary
            )
          }
        }
      }

      // Focus Timer Dial & Controls
      item {
        FocusTimer(
          remainingSeconds = remainingSeconds,
          totalDurationSeconds = totalDurationSeconds,
          isRunning = isTimerRunning,
          selectedPresetMinutes = selectedPresetMinutes,
          onPresetSelected = { viewModel.selectPreset(it) },
          onStartClick = { viewModel.startTimer() },
          onPauseClick = { viewModel.pauseTimer() },
          onResumeClick = { viewModel.resumeTimer() },
          onResetClick = { viewModel.resetTimer() },
          onFinishSessionClick = { viewModel.finishSession() }
        )
      }
    }

    // Task Selection Dialog
    if (showTaskPicker) {
      AlertDialog(
        onDismissRequest = { showTaskPicker = false },
        containerColor = FocusSurface,
        title = {
          Text("Select Focus Task", color = FocusTextPrimary, fontWeight = FontWeight.Bold)
        },
        text = {
          Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            val pendingTasks = allTasks.filter { !it.completed }
            if (pendingTasks.isEmpty()) {
              Text("No pending tasks. Create one in Tasks tab!", color = FocusTextSecondary)
            } else {
              pendingTasks.forEach { task ->
                val isSelected = currentFocusTask?.id == task.id
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isSelected) FocusPrimaryContainer else FocusSurfaceVariant)
                    .clickable {
                      viewModel.setFocusTask(task.id)
                      showTaskPicker = false
                    }
                    .padding(12.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text(
                    text = task.title,
                    color = if (isSelected) FocusPrimary else FocusTextPrimary,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                  )
                }
              }
            }
          }
        },
        confirmButton = {
          TextButton(onClick = { showTaskPicker = false }) {
            Text("Done", color = FocusPrimary)
          }
        }
      )
    }
  }
}

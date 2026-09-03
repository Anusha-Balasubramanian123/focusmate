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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MotivationalContent
import com.example.ui.components.CategoryChip
import com.example.ui.components.MotivationCard
import com.example.ui.components.PrimaryButton
import com.example.ui.components.PriorityBadge
import com.example.ui.components.SecondaryButton
import com.example.ui.state.FocusMateViewModel
import com.example.ui.theme.FocusBackground
import com.example.ui.theme.FocusBorder
import com.example.ui.theme.FocusBorderSubtle
import com.example.ui.theme.FocusCardGlow
import com.example.ui.theme.FocusError
import com.example.ui.theme.FocusOnPrimary
import com.example.ui.theme.FocusPrimary
import com.example.ui.theme.FocusPrimaryContainer
import com.example.ui.theme.FocusSuccess
import com.example.ui.theme.FocusSurface
import com.example.ui.theme.FocusSurfaceVariant
import com.example.ui.theme.FocusTextPrimary
import com.example.ui.theme.FocusTextSecondary
import com.example.ui.theme.FocusTextTertiary

@Composable
fun TaskDetailsScreen(
  taskId: String,
  viewModel: FocusMateViewModel,
  modifier: Modifier = Modifier
) {
  val tasks by viewModel.tasks.collectAsState()
  val task = tasks.find { it.id == taskId }

  var showDeleteDialog by remember { mutableStateOf(false) }

  if (task == null) {
    Box(
      modifier = modifier
        .fillMaxSize()
        .background(FocusBackground),
      contentAlignment = Alignment.Center
    ) {
      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Task not found", color = FocusTextSecondary)
        Spacer(modifier = Modifier.height(8.dp))
        TextButton(onClick = { viewModel.closeTaskDetails() }) {
          Text("Go Back", color = FocusPrimary)
        }
      }
    }
    return
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
        .testTag("task_details_content"),
      contentPadding = PaddingValues(top = 16.dp, bottom = 40.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // Header: Back Button & Actions
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
              onClick = { viewModel.closeTaskDetails() },
              modifier = Modifier.testTag("task_details_back_btn")
            ) {
              Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = FocusTextPrimary
              )
            }
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "Task Details",
              style = MaterialTheme.typography.titleLarge,
              color = FocusTextPrimary,
              fontWeight = FontWeight.Bold
            )
          }

          Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
              onClick = { viewModel.openEditTask(task.id) },
              modifier = Modifier.testTag("task_details_edit_btn")
            ) {
              Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Edit Task",
                tint = FocusPrimary
              )
            }
            IconButton(
              onClick = { showDeleteDialog = true },
              modifier = Modifier.testTag("task_details_delete_btn")
            ) {
              Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete",
                tint = FocusError
              )
            }
          }
        }
      }

      // Completed State Banner
      if (task.completed) {
        item {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(16.dp))
              .background(FocusSurfaceVariant)
              .border(1.dp, FocusSuccess.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
              .padding(16.dp)
              .testTag("task_completed_banner")
          ) {
            Column {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text("🎉", fontSize = 20.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = "Task completed! 🎉",
                  style = MaterialTheme.typography.titleMedium,
                  color = FocusSuccess,
                  fontWeight = FontWeight.Bold
                )
              }
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "You're building momentum. Great finish.",
                style = MaterialTheme.typography.bodyMedium,
                color = FocusTextSecondary
              )
            }
          }
        }
      }

      // Missed State Banner (Gentle, never shaming)
      if (task.isMissed && !task.completed) {
        item {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(16.dp))
              .background(FocusSurfaceVariant)
              .border(1.dp, FocusError.copy(alpha = 0.35f), RoundedCornerShape(16.dp))
              .padding(16.dp)
              .testTag("task_missed_banner")
          ) {
            Column {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text("🌱", fontSize = 20.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = "Missed this one? That's okay.",
                  style = MaterialTheme.typography.titleMedium,
                  color = FocusTextPrimary,
                  fontWeight = FontWeight.Bold
                )
              }
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "Reset and take the next step. Consistency isn't perfection.",
                style = MaterialTheme.typography.bodyMedium,
                color = FocusTextSecondary
              )
            }
          }
        }
      }

      // Task Title & Tags Card
      item {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(FocusSurfaceVariant)
            .border(1.dp, FocusBorderSubtle, RoundedCornerShape(18.dp))
            .padding(18.dp)
        ) {
          Column {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              CategoryChip(category = task.category)
              PriorityBadge(priority = task.priority)
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
              text = task.title,
              style = MaterialTheme.typography.headlineMedium,
              color = FocusTextPrimary,
              fontWeight = FontWeight.Bold
            )

            if (task.description.isNotBlank()) {
              Spacer(modifier = Modifier.height(10.dp))
              Text(
                text = task.description,
                style = MaterialTheme.typography.bodyLarge,
                color = FocusTextSecondary,
                lineHeight = 22.sp
              )
            }
          }
        }
      }

      // Details Metadata Grid
      item {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(FocusSurfaceVariant)
            .border(1.dp, FocusBorderSubtle, RoundedCornerShape(18.dp))
            .padding(16.dp),
          verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
          MetadataRow(
            icon = Icons.Default.CalendarToday,
            label = "Date",
            value = task.date
          )
          MetadataRow(
            icon = Icons.Default.Schedule,
            label = "Time",
            value = task.formattedTime.ifBlank { "All Day" }
          )
          MetadataRow(
            icon = Icons.Default.Notifications,
            label = "Reminder",
            value = task.reminderTime.label
          )
          MetadataRow(
            icon = Icons.Default.Repeat,
            label = "Repeat",
            value = task.repeatType.label
          )
        }
      }

      // Motivational Quote
      item {
        MotivationCard(
          quote = MotivationalContent.dailyQuotes[1]
        )
      }

      // Actions: Complete / Incomplete & Postpone
      item {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          PrimaryButton(
            text = if (task.completed) "Mark as Incomplete" else "✓ Mark as Completed",
            onClick = { viewModel.toggleTaskComplete(task.id) },
            testTag = "task_details_toggle_complete_btn"
          )

          if (!task.completed) {
            SecondaryButton(
              text = "Postpone to Tomorrow",
              onClick = {
                viewModel.postponeTask(task.id)
                viewModel.closeTaskDetails()
              },
              testTag = "task_details_postpone_btn"
            )
          }
        }
      }
    }

    // Delete Confirmation Dialog
    if (showDeleteDialog) {
      AlertDialog(
        onDismissRequest = { showDeleteDialog = false },
        containerColor = FocusSurface,
        title = {
          Text("Delete Task", color = FocusTextPrimary, fontWeight = FontWeight.Bold)
        },
        text = {
          Text("Are you sure you want to remove \"${task.title}\"?", color = FocusTextSecondary)
        },
        confirmButton = {
          TextButton(
            onClick = {
              viewModel.deleteTask(task.id)
              showDeleteDialog = false
            }
          ) {
            Text("Delete", color = FocusError, fontWeight = FontWeight.Bold)
          }
        },
        dismissButton = {
          TextButton(onClick = { showDeleteDialog = false }) {
            Text("Cancel", color = FocusTextSecondary)
          }
        }
      )
    }
  }
}

@Composable
private fun MetadataRow(
  icon: ImageVector,
  label: String,
  value: String
) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      Icon(icon, contentDescription = null, tint = FocusTextTertiary, modifier = Modifier.size(16.dp))
      Text(label, style = MaterialTheme.typography.bodyMedium, color = FocusTextSecondary)
    }
    Text(value, style = MaterialTheme.typography.bodyMedium, color = FocusTextPrimary, fontWeight = FontWeight.SemiBold)
  }
}

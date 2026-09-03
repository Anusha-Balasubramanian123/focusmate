package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ReminderOption
import com.example.data.model.Task
import com.example.ui.theme.FocusBorder
import com.example.ui.theme.FocusBorderSubtle
import com.example.ui.theme.FocusError
import com.example.ui.theme.FocusOnPrimary
import com.example.ui.theme.FocusPrimary
import com.example.ui.theme.FocusSuccess
import com.example.ui.theme.FocusSurface
import com.example.ui.theme.FocusSurfaceElevated
import com.example.ui.theme.FocusSurfaceVariant
import com.example.ui.theme.FocusTextPrimary
import com.example.ui.theme.FocusTextSecondary
import com.example.ui.theme.FocusTextTertiary
import com.example.ui.theme.ImmersiveCardGradient

@Composable
fun TaskCard(
  task: Task,
  onToggleComplete: () -> Unit,
  onClick: () -> Unit,
  onDelete: (() -> Unit)? = null,
  onPostpone: (() -> Unit)? = null,
  showQuickActions: Boolean = false,
  modifier: Modifier = Modifier
) {
  var expandedActions by remember { mutableStateOf(false) }

  val borderCol = when {
    task.completed -> FocusBorderSubtle
    task.isMissed -> FocusError.copy(alpha = 0.45f)
    else -> FocusBorder
  }

  val cardModifier = if (task.completed) {
    modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(16.dp))
      .background(FocusSurface.copy(alpha = 0.85f))
      .border(1.dp, borderCol, RoundedCornerShape(16.dp))
  } else {
    modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(16.dp))
      .background(ImmersiveCardGradient)
      .border(1.dp, borderCol, RoundedCornerShape(16.dp))
  }

  Box(
    modifier = cardModifier
      .clickable { onClick() }
      .padding(14.dp)
      .testTag("task_card_${task.id}")
  ) {
    Column {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.weight(1f)
        ) {
          // Custom Animated Completion Checkbox
          Box(
            modifier = Modifier
              .size(24.dp)
              .clip(CircleShape)
              .background(
                if (task.completed) FocusPrimary else Color.Transparent
              )
              .border(
                2.dp,
                if (task.completed) FocusPrimary else FocusTextSecondary,
                CircleShape
              )
              .clickable { onToggleComplete() }
              .testTag("task_checkbox_${task.id}"),
            contentAlignment = Alignment.Center
          ) {
            if (task.completed) {
              Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Completed",
                tint = FocusOnPrimary,
                modifier = Modifier.size(15.dp)
              )
            }
          }

          Spacer(modifier = Modifier.width(12.dp))

          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = task.title,
              style = MaterialTheme.typography.titleMedium,
              color = if (task.completed) FocusTextTertiary else FocusTextPrimary,
              maxLines = 2,
              overflow = TextOverflow.Ellipsis,
              textDecoration = if (task.completed) TextDecoration.LineThrough else TextDecoration.None,
              fontWeight = if (task.completed) FontWeight.Normal else FontWeight.SemiBold
            )

            if (task.description.isNotBlank()) {
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = task.description,
                style = MaterialTheme.typography.bodySmall,
                color = FocusTextTertiary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
            }
          }
        }

        // Priority Badge on the right
        PriorityBadge(priority = task.priority)
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Footer: Time, Reminder Icon, Category Chip, Status Badge
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          // Time badge
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
              .background(FocusSurfaceElevated, RoundedCornerShape(8.dp))
              .padding(horizontal = 7.dp, vertical = 3.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Schedule,
              contentDescription = "Time",
              tint = FocusTextSecondary,
              modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = task.formattedTime.ifBlank { "All Day" },
              style = MaterialTheme.typography.labelSmall,
              color = FocusTextSecondary
            )
          }

          // Reminder Icon if enabled
          if (task.reminderTime != ReminderOption.NONE) {
            Box(
              modifier = Modifier
                .size(22.dp)
                .background(FocusSurfaceElevated, CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = "Reminder set",
                tint = FocusPrimary,
                modifier = Modifier.size(12.dp)
              )
            }
          }

          // Category Chip
          CategoryChip(category = task.category)
        }

        // Status or Action toggler
        if (task.completed) {
          Text(
            text = "Completed ✨",
            style = MaterialTheme.typography.labelSmall,
            color = FocusSuccess,
            fontWeight = FontWeight.Medium
          )
        } else if (task.isMissed) {
          Text(
            text = "Missed",
            style = MaterialTheme.typography.labelSmall,
            color = FocusError,
            fontWeight = FontWeight.Medium
          )
        } else if (showQuickActions && (onDelete != null || onPostpone != null)) {
          Row {
            if (onPostpone != null) {
              Text(
                text = "Postpone",
                style = MaterialTheme.typography.labelSmall,
                color = FocusPrimary,
                modifier = Modifier
                  .clickable { onPostpone() }
                  .padding(4.dp)
              )
            }
            if (onDelete != null) {
              IconButton(
                onClick = { onDelete() },
                modifier = Modifier.size(24.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.DeleteOutline,
                  contentDescription = "Delete task",
                  tint = FocusTextTertiary,
                  modifier = Modifier.size(16.dp)
                )
              }
            }
          }
        }
      }
    }
  }
}

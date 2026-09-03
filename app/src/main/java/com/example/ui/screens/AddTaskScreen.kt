package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Priority
import com.example.data.model.ReminderOption
import com.example.data.model.RepeatType
import com.example.data.model.TaskCategory
import com.example.ui.components.CategoryChip
import com.example.ui.components.PrimaryButton
import com.example.ui.state.FocusMateViewModel
import com.example.ui.theme.FocusBackground
import com.example.ui.theme.FocusBorder
import com.example.ui.theme.FocusBorderSubtle
import com.example.ui.theme.FocusError
import com.example.ui.theme.FocusOnPrimary
import com.example.ui.theme.FocusPrimary
import com.example.ui.theme.FocusPrimaryContainer
import com.example.ui.theme.FocusSurfaceVariant
import com.example.ui.theme.FocusTextPrimary
import com.example.ui.theme.FocusTextSecondary
import com.example.ui.theme.FocusTextTertiary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AddTaskScreen(
  viewModel: FocusMateViewModel,
  modifier: Modifier = Modifier
) {
  val todayStr = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()) }

  val editingTaskId by viewModel.editingTaskId.collectAsState()
  val allTasks by viewModel.tasks.collectAsState()
  val existingTask = remember(editingTaskId, allTasks) {
    editingTaskId?.let { id -> allTasks.find { it.id == id } }
  }
  val isEditMode = existingTask != null

  var title by remember(existingTask) { mutableStateOf(existingTask?.title ?: "") }
  var description by remember(existingTask) { mutableStateOf(existingTask?.description ?: "") }
  var selectedDate by remember(existingTask) { mutableStateOf(existingTask?.date ?: todayStr) }
  var selectedTime by remember(existingTask) { mutableStateOf(existingTask?.time ?: "18:00") }
  var selectedPriority by remember(existingTask) { mutableStateOf(existingTask?.priority ?: Priority.HIGH) }
  var selectedCategory by remember(existingTask) { mutableStateOf(existingTask?.category ?: TaskCategory.WORK) }
  var selectedReminder by remember(existingTask) { mutableStateOf(existingTask?.reminderTime ?: ReminderOption.FIFTEEN_MIN_BEFORE) }
  var selectedRepeat by remember(existingTask) { mutableStateOf(existingTask?.repeatType ?: RepeatType.DOES_NOT_REPEAT) }

  var titleError by remember { mutableStateOf(false) }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(FocusBackground)
  ) {
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 20.dp)
        .testTag("add_task_screen_content"),
      contentPadding = PaddingValues(top = 16.dp, bottom = 40.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // Header with Back Button
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically
        ) {
          IconButton(
            onClick = { viewModel.closeAddTask() },
            modifier = Modifier.testTag("add_task_back_btn")
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "Back",
              tint = FocusTextPrimary
            )
          }
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = if (isEditMode) "Edit Task" else "Create Task",
            style = MaterialTheme.typography.headlineMedium,
            color = FocusTextPrimary,
            fontWeight = FontWeight.Bold
          )
        }
      }

      // Title Input
      item {
        Column {
          Text(
            text = "TASK TITLE *",
            style = MaterialTheme.typography.labelSmall,
            color = if (titleError) FocusError else FocusTextTertiary,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
          )
          Spacer(modifier = Modifier.height(6.dp))
          OutlinedTextField(
            value = title,
            onValueChange = {
              title = it
              if (it.isNotBlank()) titleError = false
            },
            placeholder = { Text("e.g. Build Android UI", color = FocusTextTertiary) },
            singleLine = true,
            isError = titleError,
            keyboardOptions = KeyboardOptions(
              capitalization = KeyboardCapitalization.Sentences,
              imeAction = ImeAction.Next
            ),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("task_title_input"),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
              focusedContainerColor = FocusSurfaceVariant,
              unfocusedContainerColor = FocusSurfaceVariant,
              focusedBorderColor = FocusPrimary,
              unfocusedBorderColor = FocusBorder,
              errorBorderColor = FocusError,
              focusedTextColor = FocusTextPrimary,
              unfocusedTextColor = FocusTextPrimary
            )
          )
          if (titleError) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "Task title is required",
              style = MaterialTheme.typography.bodySmall,
              color = FocusError
            )
          }
        }
      }

      // Description Input
      item {
        Column {
          Text(
            text = "DESCRIPTION",
            style = MaterialTheme.typography.labelSmall,
            color = FocusTextTertiary,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
          )
          Spacer(modifier = Modifier.height(6.dp))
          OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            placeholder = { Text("Add notes, milestones, or instructions...", color = FocusTextTertiary) },
            minLines = 3,
            maxLines = 5,
            keyboardOptions = KeyboardOptions(
              capitalization = KeyboardCapitalization.Sentences,
              imeAction = ImeAction.Default
            ),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("task_description_input"),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
              focusedContainerColor = FocusSurfaceVariant,
              unfocusedContainerColor = FocusSurfaceVariant,
              focusedBorderColor = FocusPrimary,
              unfocusedBorderColor = FocusBorder,
              focusedTextColor = FocusTextPrimary,
              unfocusedTextColor = FocusTextPrimary
            )
          )
        }
      }

      // Priority Selector
      item {
        Column {
          Text(
            text = "PRIORITY",
            style = MaterialTheme.typography.labelSmall,
            color = FocusTextTertiary,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
          )
          Spacer(modifier = Modifier.height(8.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Priority.entries.forEach { p ->
              val isSelected = selectedPriority == p
              val pillBg = if (isSelected) FocusPrimaryContainer else FocusSurfaceVariant
              val borderCol = if (isSelected) FocusPrimary else FocusBorderSubtle
              val textCol = if (isSelected) FocusPrimary else FocusTextSecondary

              Box(
                modifier = Modifier
                  .weight(1f)
                  .clip(RoundedCornerShape(12.dp))
                  .background(pillBg)
                  .border(1.dp, borderCol, RoundedCornerShape(12.dp))
                  .clickable { selectedPriority = p }
                  .padding(vertical = 12.dp)
                  .testTag("priority_option_${p.name.lowercase()}"),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = p.label,
                  style = MaterialTheme.typography.labelMedium,
                  color = textCol,
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                )
              }
            }
          }
        }
      }

      // Category Selector
      item {
        Column {
          Text(
            text = "CATEGORY",
            style = MaterialTheme.typography.labelSmall,
            color = FocusTextTertiary,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
          )
          Spacer(modifier = Modifier.height(8.dp))
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            TaskCategory.entries.forEach { cat ->
              CategoryChip(
                category = cat,
                isSelected = selectedCategory == cat,
                onClick = { selectedCategory = cat }
              )
            }
          }
        }
      }

      // Date & Time Selectors
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          // Date Shortcut Selector
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "DATE",
              style = MaterialTheme.typography.labelSmall,
              color = FocusTextTertiary,
              fontWeight = FontWeight.Bold,
              letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
              listOf("Today", "Tomorrow").forEach { dLabel ->
                val isToday = dLabel == "Today"
                val dateVal = if (isToday) todayStr else "2026-09-03"
                val isSelected = selectedDate == dateVal

                Box(
                  modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isSelected) FocusPrimaryContainer else FocusSurfaceVariant)
                    .border(1.dp, if (isSelected) FocusPrimary else FocusBorderSubtle, RoundedCornerShape(10.dp))
                    .clickable { selectedDate = dateVal }
                    .padding(vertical = 10.dp),
                  contentAlignment = Alignment.Center
                ) {
                  Text(
                    text = dLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isSelected) FocusPrimary else FocusTextSecondary,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                  )
                }
              }
            }
          }

          // Time Shortcut Selector
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "TIME",
              style = MaterialTheme.typography.labelSmall,
              color = FocusTextTertiary,
              fontWeight = FontWeight.Bold,
              letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
              listOf("09:00", "14:00", "18:00").forEach { tVal ->
                val isSelected = selectedTime == tVal
                val label = when (tVal) {
                  "09:00" -> "9 AM"
                  "14:00" -> "2 PM"
                  else -> "6 PM"
                }

                Box(
                  modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isSelected) FocusPrimaryContainer else FocusSurfaceVariant)
                    .border(1.dp, if (isSelected) FocusPrimary else FocusBorderSubtle, RoundedCornerShape(10.dp))
                    .clickable { selectedTime = tVal }
                    .padding(vertical = 10.dp),
                  contentAlignment = Alignment.Center
                ) {
                  Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isSelected) FocusPrimary else FocusTextSecondary,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                  )
                }
              }
            }
          }
        }
      }

      // Reminder Selector
      item {
        Column {
          Text(
            text = "REMINDER",
            style = MaterialTheme.typography.labelSmall,
            color = FocusTextTertiary,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
          )
          Spacer(modifier = Modifier.height(8.dp))
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            ReminderOption.entries.forEach { rem ->
              val isSelected = selectedReminder == rem
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(12.dp))
                  .background(if (isSelected) FocusPrimaryContainer else FocusSurfaceVariant)
                  .border(1.dp, if (isSelected) FocusPrimary else FocusBorderSubtle, RoundedCornerShape(12.dp))
                  .clickable { selectedReminder = rem }
                  .padding(horizontal = 12.dp, vertical = 8.dp)
                  .testTag("reminder_option_${rem.name.lowercase()}"),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = rem.label,
                  style = MaterialTheme.typography.labelSmall,
                  color = if (isSelected) FocusPrimary else FocusTextSecondary,
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                )
              }
            }
          }
        }
      }

      // Repeat Selector
      item {
        Column {
          Text(
            text = "REPEAT",
            style = MaterialTheme.typography.labelSmall,
            color = FocusTextTertiary,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
          )
          Spacer(modifier = Modifier.height(8.dp))
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            RepeatType.entries.forEach { rep ->
              val isSelected = selectedRepeat == rep
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(12.dp))
                  .background(if (isSelected) FocusPrimaryContainer else FocusSurfaceVariant)
                  .border(1.dp, if (isSelected) FocusPrimary else FocusBorderSubtle, RoundedCornerShape(12.dp))
                  .clickable { selectedRepeat = rep }
                  .padding(horizontal = 12.dp, vertical = 8.dp)
                  .testTag("repeat_option_${rep.name.lowercase()}"),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = rep.label,
                  style = MaterialTheme.typography.labelSmall,
                  color = if (isSelected) FocusPrimary else FocusTextSecondary,
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                )
              }
            }
          }
        }
      }

      // Primary CTA: Create / Save Task
      item {
        Spacer(modifier = Modifier.height(10.dp))
        PrimaryButton(
          text = if (isEditMode) "Save Changes" else "Create Task",
          onClick = {
            if (title.isBlank()) {
              titleError = true
            } else {
              if (isEditMode && existingTask != null) {
                viewModel.updateTask(
                  existingTask.copy(
                    title = title.trim(),
                    description = description.trim(),
                    date = selectedDate.ifBlank { todayStr },
                    time = selectedTime.ifBlank { "12:00" },
                    priority = selectedPriority,
                    category = selectedCategory,
                    reminderTime = selectedReminder,
                    repeatType = selectedRepeat
                  )
                )
                viewModel.closeEditTask()
              } else {
                viewModel.addTask(
                  title = title,
                  description = description,
                  date = selectedDate,
                  time = selectedTime,
                  priority = selectedPriority,
                  category = selectedCategory,
                  reminderTime = selectedReminder,
                  repeatType = selectedRepeat
                )
              }
            }
          },
          leadingIcon = Icons.Default.Check,
          testTag = if (isEditMode) "save_task_submit_btn" else "create_task_submit_btn"
        )
      }
    }
  }
}

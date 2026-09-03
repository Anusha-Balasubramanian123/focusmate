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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
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
import com.example.data.model.MotivationalContent
import com.example.ui.components.FocusMateScreen
import com.example.ui.components.MotivationCard
import com.example.ui.components.TaskCard
import com.example.ui.components.TodayProgressCard
import com.example.ui.state.FocusMateViewModel
import com.example.ui.theme.FocusBackground
import com.example.ui.theme.FocusBorderSubtle
import com.example.ui.theme.FocusCardGlow
import com.example.ui.theme.FocusOnPrimary
import com.example.ui.theme.FocusPrimary
import com.example.ui.theme.FocusPrimaryBorder
import com.example.ui.theme.FocusPrimaryContainer
import com.example.ui.theme.FocusSurface
import com.example.ui.theme.FocusSurfaceVariant
import com.example.ui.theme.FocusTextPrimary
import com.example.ui.theme.FocusTextSecondary
import com.example.ui.theme.FocusTextTertiary
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(
  viewModel: FocusMateViewModel,
  modifier: Modifier = Modifier
) {
  val tasks by viewModel.tasks.collectAsState()
  val streakDays by viewModel.currentStreak.collectAsState()

  // Filter today's tasks
  val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
  val todayTasks = tasks.filter { it.date == todayStr }
  val completedToday = todayTasks.count { it.completed }
  val totalToday = todayTasks.size.coerceAtLeast(1)

  // Top focus priority tasks (pending tasks first, high priority first)
  val focusTasks = todayTasks
    .filter { !it.completed }
    .sortedByDescending { it.priority.ordinal }

  // Format today's human-readable date e.g. "Wednesday, September 2"
  val formattedDate = SimpleDateFormat("EEEE, MMMM d", Locale.getDefault()).format(Date())

  // Dynamic greeting based on current hour
  val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
  val greetingTime = when (hour) {
    in 5..11 -> "Good morning"
    in 12..16 -> "Good afternoon"
    else -> "Good evening"
  }

  Box(modifier = modifier.fillMaxSize().background(FocusBackground)) {
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 20.dp)
        .testTag("home_screen_content"),
      contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // Personalized Header
      item {
        Column(modifier = Modifier.fillMaxWidth().testTag("home_header")) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "$greetingTime, Anusha 👋",
                style = MaterialTheme.typography.headlineLarge,
                color = FocusTextPrimary,
                fontWeight = FontWeight.Bold
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "Let's make today count.",
                style = MaterialTheme.typography.bodyMedium,
                color = FocusTextSecondary
              )
            }

            // Date Badge
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(FocusSurfaceVariant)
                .border(1.dp, FocusBorderSubtle, RoundedCornerShape(12.dp))
                .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
              Text(
                text = SimpleDateFormat("MMM d", Locale.getDefault()).format(Date()).uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = FocusPrimary,
                fontWeight = FontWeight.Bold
              )
            }
          }

          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = formattedDate,
            style = MaterialTheme.typography.bodySmall,
            color = FocusTextTertiary
          )
        }
      }

      // Today's Progress Card
      item {
        TodayProgressCard(
          completedCount = completedToday,
          totalCount = totalToday,
          streakDays = streakDays,
          onViewProgressClick = { viewModel.navigateTo(FocusMateScreen.PROGRESS) }
        )
      }

      // Motivational Card
      item {
        MotivationCard(
          quote = MotivationalContent.dailyQuotes.first()
        )
      }

      // TODAY'S FOCUS Section Header
      item {
        Row(
          modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = "TODAY'S FOCUS",
              style = MaterialTheme.typography.titleMedium,
              color = FocusTextPrimary,
              fontWeight = FontWeight.Bold,
              letterSpacing = 0.5.sp
            )
            Text(
              text = "${focusTasks.size} priority items remaining",
              style = MaterialTheme.typography.bodySmall,
              color = FocusTextTertiary
            )
          }

          Button(
            onClick = { viewModel.openAddTask() },
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
              containerColor = FocusPrimaryContainer,
              contentColor = FocusPrimary
            ),
            border = androidx.compose.foundation.BorderStroke(1.dp, FocusPrimaryBorder),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
            modifier = Modifier.testTag("home_add_task_inline_btn")
          ) {
            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Add Task", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
          }
        }
      }

      // Task List
      if (focusTasks.isEmpty()) {
        item {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(16.dp))
              .background(FocusSurfaceVariant)
              .padding(24.dp),
            contentAlignment = Alignment.Center
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text("🎉", fontSize = 28.sp)
              Spacer(modifier = Modifier.height(8.dp))
              Text(
                text = "All priority tasks completed!",
                style = MaterialTheme.typography.titleMedium,
                color = FocusTextPrimary,
                fontWeight = FontWeight.Bold
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "You're building tremendous consistency today.",
                style = MaterialTheme.typography.bodySmall,
                color = FocusTextSecondary
              )
            }
          }
        }
      } else {
        items(focusTasks, key = { it.id }) { task ->
          TaskCard(
            task = task,
            onToggleComplete = { viewModel.toggleTaskComplete(task.id) },
            onClick = { viewModel.openTaskDetails(task.id) },
            onDelete = { viewModel.deleteTask(task.id) },
            onPostpone = { viewModel.postponeTask(task.id) },
            showQuickActions = true
          )
        }
      }

      // Completed Today Section (Compact summary)
      if (completedToday > 0) {
        item {
          val completedList = todayTasks.filter { it.completed }
          Column(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
          ) {
            Text(
              text = "COMPLETED TODAY ($completedToday)",
              style = MaterialTheme.typography.labelSmall,
              color = FocusTextTertiary,
              fontWeight = FontWeight.Bold,
              letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            completedList.take(3).forEach { task ->
              TaskCard(
                task = task,
                onToggleComplete = { viewModel.toggleTaskComplete(task.id) },
                onClick = { viewModel.openTaskDetails(task.id) },
                modifier = Modifier.padding(bottom = 8.dp)
              )
            }
          }
        }
      }
    }

    // Floating Action Button
    FloatingActionButton(
      onClick = { viewModel.openAddTask() },
      containerColor = FocusPrimary,
      contentColor = FocusOnPrimary,
      shape = CircleShape,
      modifier = Modifier
        .align(Alignment.BottomEnd)
        .padding(end = 20.dp, bottom = 80.dp)
        .testTag("home_fab_add_task")
    ) {
      Icon(
        imageVector = Icons.Default.Add,
        contentDescription = "Add Task",
        modifier = Modifier.size(24.dp)
      )
    }
  }
}

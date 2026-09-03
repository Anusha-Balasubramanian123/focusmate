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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TaskCategory
import com.example.data.model.TaskSortOrder
import com.example.data.model.TaskStatusFilter
import com.example.ui.components.CategoryChip
import com.example.ui.components.FilterChipComponent
import com.example.ui.components.PrimaryButton
import com.example.ui.components.TaskCard
import com.example.ui.state.FocusMateViewModel
import com.example.ui.theme.FocusBackground
import com.example.ui.theme.FocusBorder
import com.example.ui.theme.FocusBorderSubtle
import com.example.ui.theme.FocusOnPrimary
import com.example.ui.theme.FocusPrimary
import com.example.ui.theme.FocusSurface
import com.example.ui.theme.FocusSurfaceVariant
import com.example.ui.theme.FocusTextPrimary
import com.example.ui.theme.FocusTextSecondary
import com.example.ui.theme.FocusTextTertiary

@Composable
fun TasksScreen(
  viewModel: FocusMateViewModel,
  modifier: Modifier = Modifier
) {
  val filteredTasks by viewModel.filteredTasks.collectAsState()
  val searchQuery by viewModel.searchQuery.collectAsState()
  val statusFilter by viewModel.statusFilter.collectAsState()
  val categoryFilter by viewModel.categoryFilter.collectAsState()
  val sortOrder by viewModel.sortOrder.collectAsState()
  val focusManager = LocalFocusManager.current

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(FocusBackground)
  ) {
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 20.dp)
        .testTag("tasks_screen_list"),
      contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      // Screen Header
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = "My Tasks",
              style = MaterialTheme.typography.headlineLarge,
              color = FocusTextPrimary,
              fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = "${filteredTasks.size} tasks in view",
              style = MaterialTheme.typography.bodySmall,
              color = FocusTextSecondary
            )
          }

          // Sort Cycle Button
          Row(
            modifier = Modifier
              .clip(RoundedCornerShape(12.dp))
              .background(FocusSurfaceVariant)
              .border(1.dp, FocusBorderSubtle, RoundedCornerShape(12.dp))
              .clickable {
                val nextOrder = when (sortOrder) {
                  TaskSortOrder.TIME -> TaskSortOrder.PRIORITY
                  TaskSortOrder.PRIORITY -> TaskSortOrder.CATEGORY
                  TaskSortOrder.CATEGORY -> TaskSortOrder.TIME
                }
                viewModel.setSortOrder(nextOrder)
              }
              .padding(horizontal = 10.dp, vertical = 6.dp)
              .testTag("sort_button"),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.Sort,
              contentDescription = "Sort",
              tint = FocusPrimary,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = sortOrder.label,
              style = MaterialTheme.typography.labelSmall,
              color = FocusTextPrimary,
              fontWeight = FontWeight.SemiBold
            )
          }
        }
      }

      // Search Bar
      item {
        OutlinedTextField(
          value = searchQuery,
          onValueChange = { viewModel.setSearchQuery(it) },
          placeholder = {
            Text("Search tasks...", color = FocusTextTertiary, style = MaterialTheme.typography.bodyMedium)
          },
          leadingIcon = {
            Icon(Icons.Default.Search, contentDescription = "Search", tint = FocusTextTertiary, modifier = Modifier.size(18.dp))
          },
          trailingIcon = {
            if (searchQuery.isNotEmpty()) {
              IconButton(onClick = { viewModel.setSearchQuery("") }) {
                Icon(Icons.Default.Close, contentDescription = "Clear", tint = FocusTextTertiary, modifier = Modifier.size(18.dp))
              }
            }
          },
          singleLine = true,
          keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
          keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("task_search_input"),
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

      // Status Filter Chips (Horizontal Scroll)
      item {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          TaskStatusFilter.entries.forEach { filter ->
            FilterChipComponent(
              label = filter.label,
              isSelected = statusFilter == filter,
              onClick = { viewModel.setStatusFilter(filter) }
            )
          }
        }
      }

      // Category Filters (Horizontal Scroll)
      item {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          // All Categories Chip
          val isAllSelected = categoryFilter == null
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(20.dp))
              .background(if (isAllSelected) FocusPrimary else FocusSurfaceVariant)
              .border(1.dp, if (isAllSelected) FocusPrimary else FocusBorderSubtle, RoundedCornerShape(20.dp))
              .clickable { viewModel.setCategoryFilter(null) }
              .padding(horizontal = 12.dp, vertical = 6.dp)
              .testTag("category_filter_all"),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = "All Categories",
              style = MaterialTheme.typography.labelMedium,
              color = if (isAllSelected) FocusOnPrimary else FocusTextSecondary,
              fontWeight = if (isAllSelected) FontWeight.Bold else FontWeight.Normal
            )
          }

          TaskCategory.entries.forEach { cat ->
            CategoryChip(
              category = cat,
              isSelected = categoryFilter == cat,
              onClick = {
                viewModel.setCategoryFilter(if (categoryFilter == cat) null else cat)
              }
            )
          }
        }
      }

      // Task List items or Empty State
      if (filteredTasks.isEmpty()) {
        item {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(18.dp))
              .background(FocusSurfaceVariant)
              .border(1.dp, FocusBorderSubtle, RoundedCornerShape(18.dp))
              .padding(32.dp)
              .testTag("tasks_empty_state"),
            contentAlignment = Alignment.Center
          ) {
            Column(
              horizontalAlignment = Alignment.CenterHorizontally,
              verticalArrangement = Arrangement.Center
            ) {
              Text("📝", fontSize = 36.sp)
              Spacer(modifier = Modifier.height(12.dp))
              Text(
                text = "No tasks yet",
                style = MaterialTheme.typography.headlineMedium,
                color = FocusTextPrimary,
                fontWeight = FontWeight.Bold
              )
              Spacer(modifier = Modifier.height(6.dp))
              Text(
                text = "Create your first task and start building momentum.",
                style = MaterialTheme.typography.bodyMedium,
                color = FocusTextSecondary,
                modifier = Modifier.padding(horizontal = 16.dp),
                lineHeight = 20.sp
              )
              Spacer(modifier = Modifier.height(18.dp))
              PrimaryButton(
                text = "+ Add Task",
                onClick = { viewModel.openAddTask() },
                modifier = Modifier.width(180.dp),
                testTag = "empty_state_add_task_btn"
              )
            }
          }
        }
      } else {
        items(filteredTasks, key = { it.id }) { task ->
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
        .testTag("tasks_fab_add_task")
    ) {
      Icon(
        imageVector = Icons.Default.Add,
        contentDescription = "Add Task",
        modifier = Modifier.size(24.dp)
      )
    }
  }
}

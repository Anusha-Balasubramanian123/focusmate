package com.example.ui

import android.app.Application
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.FocusBottomNavigation
import com.example.ui.components.FocusMateScreen
import com.example.ui.screens.AddTaskScreen
import com.example.ui.screens.FocusScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.ProductivityInsightsScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.ProgressScreen
import com.example.ui.screens.TaskDetailsScreen
import com.example.ui.screens.TasksScreen
import com.example.ui.state.FocusMateViewModel
import com.example.ui.theme.FocusBackground
import com.example.ui.theme.FocusMateTheme
import com.example.ui.theme.FocusPrimary
import com.example.ui.theme.FocusSurfaceElevated
import com.example.ui.theme.FocusTextPrimary

@Composable
fun FocusMateApp(
  viewModel: FocusMateViewModel = viewModel(
    factory = FocusMateViewModel.provideFactory(LocalContext.current.applicationContext as Application)
  )
) {
  FocusMateTheme {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val selectedTaskId by viewModel.selectedTaskId.collectAsState()
    val isAddingTask by viewModel.isAddingTask.collectAsState()
    val isViewingInsights by viewModel.isViewingInsights.collectAsState()
    val feedbackMessage by viewModel.feedbackMessage.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    // Handle System Back Press
    BackHandler(enabled = isAddingTask || selectedTaskId != null || isViewingInsights || currentScreen != FocusMateScreen.HOME) {
      when {
        isAddingTask -> viewModel.closeAddTask()
        selectedTaskId != null -> viewModel.closeTaskDetails()
        isViewingInsights -> viewModel.closeInsights()
        currentScreen != FocusMateScreen.HOME -> viewModel.navigateTo(FocusMateScreen.HOME)
      }
    }

    // React to feedback messages
    LaunchedEffect(feedbackMessage) {
      feedbackMessage?.let { msg ->
        snackbarHostState.showSnackbar(msg)
        viewModel.clearFeedback()
      }
    }

    val isSecondaryScreenOpen = isAddingTask || selectedTaskId != null || isViewingInsights

    Scaffold(
      modifier = Modifier
        .fillMaxSize()
        .background(FocusBackground)
        .testTag("focusmate_scaffold"),
      contentWindowInsets = WindowInsets(0, 0, 0, 0),
      containerColor = FocusBackground,
      contentColor = FocusTextPrimary,
      snackbarHost = {
        SnackbarHost(
          hostState = snackbarHostState,
          modifier = Modifier.padding(bottom = if (isSecondaryScreenOpen) 20.dp else 80.dp)
        )
      },
      bottomBar = {
        if (!isSecondaryScreenOpen) {
          FocusBottomNavigation(
            currentScreen = currentScreen,
            onScreenSelected = { viewModel.navigateTo(it) }
          )
        }
      }
    ) { innerPadding ->
      Box(
        modifier = Modifier
          .fillMaxSize()
          .background(
            androidx.compose.ui.graphics.Brush.verticalGradient(
              listOf(
                androidx.compose.ui.graphics.Color(0xFF14171E),
                FocusBackground,
                androidx.compose.ui.graphics.Color(0xFF0D0F13)
              )
            )
          )
          .windowInsetsPadding(WindowInsets.statusBars)
          .padding(
            bottom = if (!isSecondaryScreenOpen) innerPadding.calculateBottomPadding() else 0.dp
          )
          .then(
            if (isSecondaryScreenOpen) Modifier.windowInsetsPadding(WindowInsets.navigationBars)
            else Modifier
          )
          .imePadding()
      ) {
        AnimatedContent(
          targetState = when {
            isAddingTask -> "add_task"
            selectedTaskId != null -> "task_details"
            isViewingInsights -> "insights"
            else -> currentScreen.route
          },
          transitionSpec = { fadeIn() togetherWith fadeOut() },
          label = "screen_transition"
        ) { target ->
          when (target) {
            "add_task" -> AddTaskScreen(viewModel = viewModel)
            "task_details" -> TaskDetailsScreen(taskId = selectedTaskId ?: "", viewModel = viewModel)
            "insights" -> ProductivityInsightsScreen(viewModel = viewModel)
            FocusMateScreen.HOME.route -> HomeScreen(viewModel = viewModel)
            FocusMateScreen.TASKS.route -> TasksScreen(viewModel = viewModel)
            FocusMateScreen.FOCUS.route -> FocusScreen(viewModel = viewModel)
            FocusMateScreen.PROGRESS.route -> ProgressScreen(viewModel = viewModel)
            FocusMateScreen.PROFILE.route -> ProfileScreen(viewModel = viewModel)
          }
        }
      }
    }
  }
}

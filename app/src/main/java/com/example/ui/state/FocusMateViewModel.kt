package com.example.ui.state

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.model.FocusSession
import com.example.data.model.MotivationalContent
import com.example.data.model.Priority
import com.example.data.model.ReminderOption
import com.example.data.model.RepeatType
import com.example.data.model.Task
import com.example.data.model.TaskCategory
import com.example.data.model.TaskSortOrder
import com.example.data.model.TaskStatusFilter
import com.example.data.repository.FocusMateRepository
import com.example.ui.components.FocusMateScreen
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FocusMateViewModel(
  application: Application,
  private val repository: FocusMateRepository = FocusMateRepository.getInstance(application)
) : AndroidViewModel(application) {

  private val todayDateStr: String
    get() = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

  // ROOM DATABASE IS THE SINGLE SOURCE OF TRUTH
  val tasks: StateFlow<List<Task>> = repository.tasksFlow
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val focusSessions: StateFlow<List<FocusSession>> = repository.focusSessionsFlow
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  init {
    // Seed initial demo data only once on first launch into Room
    viewModelScope.launch {
      repository.seedInitialDataIfFirstLaunch()
    }
  }

  // Navigation state
  private val _currentScreen = MutableStateFlow(FocusMateScreen.HOME)
  val currentScreen: StateFlow<FocusMateScreen> = _currentScreen.asStateFlow()

  private val _selectedTaskId = MutableStateFlow<String?>(null)
  val selectedTaskId: StateFlow<String?> = _selectedTaskId.asStateFlow()

  private val _isAddingTask = MutableStateFlow(false)
  val isAddingTask: StateFlow<Boolean> = _isAddingTask.asStateFlow()

  private val _editingTaskId = MutableStateFlow<String?>(null)
  val editingTaskId: StateFlow<String?> = _editingTaskId.asStateFlow()

  private val _isViewingInsights = MutableStateFlow(false)
  val isViewingInsights: StateFlow<Boolean> = _isViewingInsights.asStateFlow()

  // Tasks Screen Filters
  private val _searchQuery = MutableStateFlow("")
  val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

  private val _statusFilter = MutableStateFlow(TaskStatusFilter.ALL)
  val statusFilter: StateFlow<TaskStatusFilter> = _statusFilter.asStateFlow()

  private val _categoryFilter = MutableStateFlow<TaskCategory?>(null)
  val categoryFilter: StateFlow<TaskCategory?> = _categoryFilter.asStateFlow()

  private val _sortOrder = MutableStateFlow(TaskSortOrder.TIME)
  val sortOrder: StateFlow<TaskSortOrder> = _sortOrder.asStateFlow()

  // Filtered & Sorted Tasks Flow directly derived from Room tasks
  val filteredTasks: StateFlow<List<Task>> = combine(
    tasks,
    _searchQuery,
    _statusFilter,
    _categoryFilter,
    _sortOrder
  ) { allTasks, query, status, category, sort ->
    val today = todayDateStr
    allTasks.filter { task ->
      // Query filter
      val matchesQuery = query.isBlank() ||
        task.title.contains(query, ignoreCase = true) ||
        task.description.contains(query, ignoreCase = true)

      // Category filter
      val matchesCategory = category == null || task.category == category

      // Status filter
      val matchesStatus = when (status) {
        TaskStatusFilter.ALL -> true
        TaskStatusFilter.TODAY -> task.date == today
        TaskStatusFilter.UPCOMING -> task.date > today && !task.completed
        TaskStatusFilter.COMPLETED -> task.completed
        TaskStatusFilter.MISSED -> task.isMissed
      }

      matchesQuery && matchesCategory && matchesStatus
    }.sortedWith { a, b ->
      when (sort) {
        TaskSortOrder.TIME -> a.time.compareTo(b.time)
        TaskSortOrder.PRIORITY -> b.priority.ordinal.compareTo(a.priority.ordinal) // HIGH first
        TaskSortOrder.CATEGORY -> a.category.label.compareTo(b.category.label)
      }
    }
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Focus Mode State
  private val _selectedPresetMinutes = MutableStateFlow(25)
  val selectedPresetMinutes: StateFlow<Int> = _selectedPresetMinutes.asStateFlow()

  private val _totalDurationSeconds = MutableStateFlow(25 * 60)
  val totalDurationSeconds: StateFlow<Int> = _totalDurationSeconds.asStateFlow()

  private val _remainingSeconds = MutableStateFlow(25 * 60)
  val remainingSeconds: StateFlow<Int> = _remainingSeconds.asStateFlow()

  private val _isTimerRunning = MutableStateFlow(false)
  val isTimerRunning: StateFlow<Boolean> = _isTimerRunning.asStateFlow()

  // Focus Session stats derived from Room database
  val focusSessionCountToday: StateFlow<Int> = focusSessions.map { list ->
    val todayStartMs = getStartOfDayMs()
    val todayEndMs = getEndOfDayMs()
    list.count { it.completed && it.startTime in todayStartMs..todayEndMs }
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

  val focusSecondsToday: StateFlow<Int> = focusSessions.map { list ->
    val todayStartMs = getStartOfDayMs()
    val todayEndMs = getEndOfDayMs()
    list.filter { it.completed && it.startTime in todayStartMs..todayEndMs }
      .sumOf { it.durationSeconds }
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

  private val _currentFocusTaskId = MutableStateFlow<String?>(null)
  val currentFocusTask: StateFlow<Task?> = combine(tasks, _currentFocusTaskId) { taskList, id ->
    if (id != null) {
      taskList.find { it.id == id }
    } else {
      taskList.firstOrNull { !it.completed }
    }
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

  // Feedback Snackbar
  private val _feedbackMessage = MutableStateFlow<String?>(null)
  val feedbackMessage: StateFlow<String?> = _feedbackMessage.asStateFlow()

  // Settings State persisted in SharedPreferences through repository
  private val _notificationsEnabled = MutableStateFlow(repository.getBoolean("pref_notifications", true))
  val notificationsEnabled: StateFlow<Boolean> = _notificationsEnabled.asStateFlow()

  private val _soundEnabled = MutableStateFlow(repository.getBoolean("pref_sound", true))
  val soundEnabled: StateFlow<Boolean> = _soundEnabled.asStateFlow()

  private val _hapticEnabled = MutableStateFlow(repository.getBoolean("pref_haptic", true))
  val hapticEnabled: StateFlow<Boolean> = _hapticEnabled.asStateFlow()

  private var timerJob: Job? = null
  private var sessionStartTimeMs: Long = 0L
  private var plannedEndTimestampMs: Long = 0L
  private var isFinishingSession = false

  // Streak & Statistics calculated from true persistent dates
  val currentStreak: StateFlow<Int> = combine(tasks, focusSessions) { taskList, sessionList ->
    val productiveDates = calculateProductiveDates(taskList, sessionList)
    calculateCurrentStreak(productiveDates)
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

  val bestStreak: StateFlow<Int> = combine(tasks, focusSessions) { taskList, sessionList ->
    val productiveDates = calculateProductiveDates(taskList, sessionList)
    calculateBestStreak(productiveDates)
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

  // Weekly Activity for Progress Screen
  val weeklyActivity: StateFlow<List<Pair<String, Int>>> = combine(tasks, focusSessions) { taskList, sessionList ->
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val dayNameFormat = SimpleDateFormat("EEE", Locale.getDefault())
    val cal = Calendar.getInstance()
    cal.firstDayOfWeek = Calendar.MONDAY
    cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)

    val days = mutableListOf<Pair<String, Int>>()
    for (i in 0 until 7) {
      val dayDateStr = dateFormat.format(cal.time)
      val dayName = dayNameFormat.format(cal.time)

      val completedTasksOnDay = taskList.count { task ->
        task.completed && (
          (task.completedAt != null && dateFormat.format(Date(task.completedAt)) == dayDateStr) ||
          (task.completedAt == null && task.date == dayDateStr)
        )
      }

      val focusSessionsOnDay = sessionList.count { session ->
        session.completed && dateFormat.format(Date(session.startTime)) == dayDateStr
      }

      days.add(dayName to (completedTasksOnDay + focusSessionsOnDay))
      cal.add(Calendar.DAY_OF_YEAR, 1)
    }
    days
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Navigation Methods
  fun navigateTo(screen: FocusMateScreen) {
    _currentScreen.value = screen
    _selectedTaskId.value = null
    _isAddingTask.value = false
    _isViewingInsights.value = false
  }

  fun openTaskDetails(taskId: String) {
    _selectedTaskId.value = taskId
  }

  fun closeTaskDetails() {
    _selectedTaskId.value = null
  }

  fun openAddTask() {
    _isAddingTask.value = true
  }

  fun closeAddTask() {
    _isAddingTask.value = false
    _editingTaskId.value = null
  }

  fun openEditTask(taskId: String) {
    _editingTaskId.value = taskId
    _isAddingTask.value = true
  }

  fun closeEditTask() {
    _editingTaskId.value = null
    _isAddingTask.value = false
  }

  fun openInsights() {
    _isViewingInsights.value = true
  }

  fun closeInsights() {
    _isViewingInsights.value = false
  }

  // Filter Methods
  fun setSearchQuery(query: String) {
    _searchQuery.value = query
  }

  fun setStatusFilter(filter: TaskStatusFilter) {
    _statusFilter.value = filter
  }

  fun setCategoryFilter(category: TaskCategory?) {
    _categoryFilter.value = category
  }

  fun setSortOrder(order: TaskSortOrder) {
    _sortOrder.value = order
  }

  // Task Actions (All persist directly to Room)
  fun toggleTaskComplete(taskId: String) {
    viewModelScope.launch {
      val task = tasks.value.find { it.id == taskId } ?: return@launch
      val willBeCompleted = !task.completed
      repository.toggleTaskComplete(task)
      if (willBeCompleted) {
        val randomMsg = MotivationalContent.completionMessages.random()
        showFeedback(randomMsg)
      }
    }
  }

  fun deleteTask(taskId: String) {
    viewModelScope.launch {
      repository.deleteTask(taskId)
      if (_selectedTaskId.value == taskId) {
        _selectedTaskId.value = null
      }
      showFeedback("Task removed")
    }
  }

  fun postponeTask(taskId: String) {
    viewModelScope.launch {
      val cal = Calendar.getInstance()
      cal.add(Calendar.DAY_OF_YEAR, 1)
      val tomorrowStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.time)
      val task = tasks.value.find { it.id == taskId }
      val time = task?.time ?: "12:00"
      repository.postponeTask(taskId, tomorrowStr, time)
      showFeedback("Task postponed to tomorrow")
    }
  }

  fun addTask(
    title: String,
    description: String,
    date: String,
    time: String,
    priority: Priority,
    category: TaskCategory,
    reminderTime: ReminderOption,
    repeatType: RepeatType
  ) {
    viewModelScope.launch {
      val newTask = Task(
        id = UUID.randomUUID().toString(),
        title = title.trim(),
        description = description.trim(),
        date = date.ifBlank { todayDateStr },
        time = time.ifBlank { "12:00" },
        priority = priority,
        category = category,
        reminderTime = reminderTime,
        repeatType = repeatType,
        completed = false,
        createdAt = System.currentTimeMillis(),
        updatedAt = System.currentTimeMillis()
      )
      repository.insertTask(newTask)
      _isAddingTask.value = false
      showFeedback("Task created! Let's get to work.")
    }
  }

  fun updateTask(updated: Task) {
    viewModelScope.launch {
      repository.updateTask(updated)
      showFeedback("Task updated")
    }
  }

  // Timer Methods
  fun selectPreset(minutes: Int) {
    if (_isTimerRunning.value) return
    _selectedPresetMinutes.value = minutes
    _totalDurationSeconds.value = minutes * 60
    _remainingSeconds.value = minutes * 60
  }

  fun setFocusTask(taskId: String) {
    _currentFocusTaskId.value = taskId
  }

  fun startTimer() {
    _isTimerRunning.value = true
    sessionStartTimeMs = System.currentTimeMillis()
    plannedEndTimestampMs = System.currentTimeMillis() + (_remainingSeconds.value * 1000L)
    startTimerTicker()
  }

  fun pauseTimer() {
    _isTimerRunning.value = false
    timerJob?.cancel()
  }

  fun resumeTimer() {
    _isTimerRunning.value = true
    plannedEndTimestampMs = System.currentTimeMillis() + (_remainingSeconds.value * 1000L)
    startTimerTicker()
  }

  fun resetTimer() {
    pauseTimer()
    _remainingSeconds.value = _selectedPresetMinutes.value * 60
  }

  fun finishSession() {
    if (isFinishingSession) return
    isFinishingSession = true
    pauseTimer()

    val elapsedSeconds = (_totalDurationSeconds.value - _remainingSeconds.value).coerceAtLeast(0)
    val sessionDuration = if (elapsedSeconds > 0) elapsedSeconds else _totalDurationSeconds.value

    viewModelScope.launch {
      val task = currentFocusTask.value
      val session = FocusSession(
        id = UUID.randomUUID().toString(),
        taskId = task?.id,
        taskTitle = task?.title,
        startTime = if (sessionStartTimeMs > 0) sessionStartTimeMs else (System.currentTimeMillis() - (sessionDuration * 1000L)),
        durationSeconds = sessionDuration,
        completed = true,
        createdAt = System.currentTimeMillis()
      )
      repository.recordFocusSession(session)
      isFinishingSession = false
    }

    _remainingSeconds.value = _selectedPresetMinutes.value * 60
    showFeedback("Focus session complete! 🎉")
  }

  private fun startTimerTicker() {
    timerJob?.cancel()
    timerJob = viewModelScope.launch {
      while (_isTimerRunning.value) {
        val now = System.currentTimeMillis()
        val remainingMs = plannedEndTimestampMs - now
        if (remainingMs <= 0) {
          _remainingSeconds.value = 0
          _isTimerRunning.value = false
          finishSession()
          break
        } else {
          _remainingSeconds.value = (remainingMs / 1000).toInt()
        }
        delay(500)
      }
    }
  }

  // Settings Toggles (Persisted in SharedPreferences)
  fun toggleNotifications() {
    val newVal = !_notificationsEnabled.value
    _notificationsEnabled.value = newVal
    repository.setBoolean("pref_notifications", newVal)
  }

  fun toggleSound() {
    val newVal = !_soundEnabled.value
    _soundEnabled.value = newVal
    repository.setBoolean("pref_sound", newVal)
  }

  fun toggleHaptic() {
    val newVal = !_hapticEnabled.value
    _hapticEnabled.value = newVal
    repository.setBoolean("pref_haptic", newVal)
  }

  fun showFeedback(msg: String) {
    _feedbackMessage.value = msg
  }

  fun clearFeedback() {
    _feedbackMessage.value = null
  }

  fun clearForTesting() {
    timerJob?.cancel()
    viewModelScope.cancel()
  }

  // Helpers for Real Streak Calculation
  private fun calculateProductiveDates(taskList: List<Task>, sessionList: List<FocusSession>): Set<String> {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val dates = mutableSetOf<String>()

    // 1. A completed task on a day
    for (task in taskList) {
      if (task.completed) {
        if (task.completedAt != null && task.completedAt > 0) {
          dates.add(dateFormat.format(Date(task.completedAt)))
        } else if (task.date.isNotBlank()) {
          dates.add(task.date)
        }
      }
    }

    // 2. A completed focus session >= 15 min (900 seconds)
    for (session in sessionList) {
      if (session.completed && session.durationSeconds >= 900) {
        dates.add(dateFormat.format(Date(session.startTime)))
      }
    }

    return dates
  }

  private fun calculateCurrentStreak(productiveDates: Set<String>): Int {
    if (productiveDates.isEmpty()) return 0

    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val cal = Calendar.getInstance()
    val today = dateFormat.format(cal.time)

    cal.add(Calendar.DAY_OF_YEAR, -1)
    val yesterday = dateFormat.format(cal.time)

    val startsFromToday = productiveDates.contains(today)
    val startsFromYesterday = productiveDates.contains(yesterday)

    if (!startsFromToday && !startsFromYesterday) {
      return 0
    }

    var streak = 0
    val checkCal = Calendar.getInstance()
    if (!startsFromToday) {
      checkCal.add(Calendar.DAY_OF_YEAR, -1)
    }

    while (true) {
      val dateStr = dateFormat.format(checkCal.time)
      if (productiveDates.contains(dateStr)) {
        streak++
        checkCal.add(Calendar.DAY_OF_YEAR, -1)
      } else {
        break
      }
    }

    return streak
  }

  private fun calculateBestStreak(productiveDates: Set<String>): Int {
    if (productiveDates.isEmpty()) return 0
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val sortedDates = productiveDates.toList().sorted()

    var maxStreak = 1
    var currentStreakRun = 1

    for (i in 1 until sortedDates.size) {
      try {
        val prevDate = dateFormat.parse(sortedDates[i - 1])
        val currDate = dateFormat.parse(sortedDates[i])
        if (prevDate != null && currDate != null) {
          val diffDays = (currDate.time - prevDate.time) / (24 * 60 * 60 * 1000L)
          if (diffDays == 1L) {
            currentStreakRun++
            if (currentStreakRun > maxStreak) {
              maxStreak = currentStreakRun
            }
          } else if (diffDays > 1L) {
            currentStreakRun = 1
          }
        }
      } catch (_: Exception) {
      }
    }

    val current = calculateCurrentStreak(productiveDates)
    return maxOf(maxStreak, current)
  }

  private fun getStartOfDayMs(): Long {
    val cal = Calendar.getInstance()
    cal.set(Calendar.HOUR_OF_DAY, 0)
    cal.set(Calendar.MINUTE, 0)
    cal.set(Calendar.SECOND, 0)
    cal.set(Calendar.MILLISECOND, 0)
    return cal.timeInMillis
  }

  private fun getEndOfDayMs(): Long {
    val cal = Calendar.getInstance()
    cal.set(Calendar.HOUR_OF_DAY, 23)
    cal.set(Calendar.MINUTE, 59)
    cal.set(Calendar.SECOND, 59)
    cal.set(Calendar.MILLISECOND, 999)
    return cal.timeInMillis
  }

  companion object {
    fun provideFactory(application: Application): ViewModelProvider.Factory =
      object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
          return FocusMateViewModel(application) as T
        }
      }
  }
}

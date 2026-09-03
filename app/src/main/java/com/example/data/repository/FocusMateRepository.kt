package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.data.local.FocusMateDatabase
import com.example.data.local.dao.FocusSessionDao
import com.example.data.local.dao.TaskDao
import com.example.data.local.entity.FocusSessionEntity
import com.example.data.local.entity.TaskEntity
import com.example.data.model.FocusSession
import com.example.data.model.Priority
import com.example.data.model.ReminderOption
import com.example.data.model.RepeatType
import com.example.data.model.Task
import com.example.data.model.TaskCategory
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class FocusMateRepository(
  private val taskDao: TaskDao,
  private val focusSessionDao: FocusSessionDao,
  private val preferences: SharedPreferences
) {

  val tasksFlow: Flow<List<Task>> = taskDao.getAllTasksFlow().map { entities ->
    entities.map { it.toDomain() }
  }

  val focusSessionsFlow: Flow<List<FocusSession>> = focusSessionDao.getAllSessionsFlow().map { entities ->
    entities.map { it.toDomain() }
  }

  val totalFocusDurationFlow: Flow<Int> = focusSessionDao.getTotalFocusDurationFlow()
  val completedSessionCountFlow: Flow<Int> = focusSessionDao.getCompletedSessionCountFlow()

  suspend fun insertTask(task: Task) = withContext(Dispatchers.IO) {
    taskDao.insertTask(TaskEntity.fromDomain(task))
  }

  suspend fun updateTask(task: Task) = withContext(Dispatchers.IO) {
    taskDao.updateTask(TaskEntity.fromDomain(task.copy(updatedAt = System.currentTimeMillis())))
  }

  suspend fun deleteTask(taskId: String) = withContext(Dispatchers.IO) {
    taskDao.deleteTaskById(taskId)
  }

  suspend fun toggleTaskComplete(task: Task) = withContext(Dispatchers.IO) {
    val newCompleted = !task.completed
    val completedAt = if (newCompleted) System.currentTimeMillis() else null
    val updatedAt = System.currentTimeMillis()
    taskDao.updateCompletionStatus(task.id, newCompleted, completedAt, updatedAt)
  }

  suspend fun markTaskMissed(taskId: String, isMissed: Boolean) = withContext(Dispatchers.IO) {
    taskDao.updateMissedStatus(taskId, isMissed, System.currentTimeMillis())
  }

  suspend fun postponeTask(taskId: String, newDate: String, newTime: String) = withContext(Dispatchers.IO) {
    taskDao.updateDateTime(taskId, newDate, newTime, System.currentTimeMillis())
    taskDao.updateMissedStatus(taskId, false, System.currentTimeMillis())
  }

  suspend fun recordFocusSession(session: FocusSession) = withContext(Dispatchers.IO) {
    focusSessionDao.insertSession(FocusSessionEntity.fromDomain(session))
  }

  fun getSessionsInRangeFlow(startMs: Long, endMs: Long): Flow<List<FocusSession>> {
    return focusSessionDao.getSessionsInRangeFlow(startMs, endMs).map { entities ->
      entities.map { it.toDomain() }
    }
  }

  // Preferences persistence
  fun getBoolean(key: String, defaultValue: Boolean): Boolean = preferences.getBoolean(key, defaultValue)
  fun setBoolean(key: String, value: Boolean) = preferences.edit().putBoolean(key, value).apply()

  fun getInt(key: String, defaultValue: Int): Int = preferences.getInt(key, defaultValue)
  fun setInt(key: String, value: Int) = preferences.edit().putInt(key, value).apply()

  /**
   * Seed initial demo tasks only once on first launch into Room.
   */
  suspend fun seedInitialDataIfFirstLaunch() = withContext(Dispatchers.IO) {
    val hasSeeded = preferences.getBoolean(KEY_HAS_SEEDED, false)
    if (hasSeeded) return@withContext

    val existingCount = taskDao.getTaskCount()
    if (existingCount == 0) {
      val defaultTasks = createInitialTasks()
      taskDao.insertTasks(defaultTasks.map { TaskEntity.fromDomain(it) })

      // Also seed a couple of historical focus sessions so initial progress is lively
      val now = System.currentTimeMillis()
      val oneDayMs = 24 * 60 * 60 * 1000L
      val defaultSessions = listOf(
        FocusSession(
          id = "init_session_1",
          taskId = "t1",
          taskTitle = "Team Standup Preparation",
          startTime = now - (3 * oneDayMs) + (10 * 60 * 1000L),
          durationSeconds = 25 * 60,
          completed = true,
          createdAt = now - (3 * oneDayMs)
        ),
        FocusSession(
          id = "init_session_2",
          taskId = "t2",
          taskTitle = "Deep Work: Jetpack Compose Migration",
          startTime = now - (2 * oneDayMs) + (14 * 60 * 1000L),
          durationSeconds = 45 * 60,
          completed = true,
          createdAt = now - (2 * oneDayMs)
        ),
        FocusSession(
          id = "init_session_3",
          taskId = "t3",
          taskTitle = "Evening Journaling & Reflection",
          startTime = now - (1 * oneDayMs) + (20 * 60 * 1000L),
          durationSeconds = 30 * 60,
          completed = true,
          createdAt = now - (1 * oneDayMs)
        )
      )
      defaultSessions.forEach { focusSessionDao.insertSession(FocusSessionEntity.fromDomain(it)) }
    }

    preferences.edit().putBoolean(KEY_HAS_SEEDED, true).apply()
  }

  private fun createInitialTasks(): List<Task> {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val cal = Calendar.getInstance()
    val today = dateFormat.format(cal.time)

    cal.add(Calendar.DAY_OF_YEAR, -1)
    val yesterday = dateFormat.format(cal.time)

    cal.add(Calendar.DAY_OF_YEAR, -1)
    val twoDaysAgo = dateFormat.format(cal.time)

    cal.add(Calendar.DAY_OF_YEAR, -1)
    val threeDaysAgo = dateFormat.format(cal.time)

    cal.add(Calendar.DAY_OF_YEAR, 4) // +1 from today
    val tomorrow = dateFormat.format(cal.time)

    val now = System.currentTimeMillis()
    val oneDayMs = 24 * 60 * 60 * 1000L

    return listOf(
      Task(
        id = "t1",
        title = "Team Standup Preparation",
        description = "Review sprint board and prep blocker updates for 10 AM sync.",
        date = today,
        time = "09:30",
        priority = Priority.HIGH,
        category = TaskCategory.WORK,
        reminderTime = ReminderOption.FIFTEEN_MIN_BEFORE,
        repeatType = RepeatType.WEEKDAYS,
        completed = true,
        completedAt = now - (2 * 60 * 60 * 1000L),
        createdAt = now - (4 * oneDayMs)
      ),
      Task(
        id = "t2",
        title = "Deep Work: Jetpack Compose Architecture",
        description = "Implement Room persistence with repository pattern and M3 theme.",
        date = today,
        time = "14:00",
        priority = Priority.HIGH,
        category = TaskCategory.WORK,
        reminderTime = ReminderOption.THIRTY_MIN_BEFORE,
        repeatType = RepeatType.DOES_NOT_REPEAT,
        completed = false,
        createdAt = now - (3 * oneDayMs)
      ),
      Task(
        id = "t3",
        title = "Evening Journaling & Wind-Down",
        description = "Log 3 positive highlights from today and unplug screens.",
        date = today,
        time = "21:00",
        priority = Priority.LOW,
        category = TaskCategory.PERSONAL,
        reminderTime = ReminderOption.AT_TASK_TIME,
        repeatType = RepeatType.DAILY,
        completed = false,
        createdAt = now - (3 * oneDayMs)
      ),
      Task(
        id = "t4",
        title = "Quarterly Objectives Review",
        description = "Align Q3 development goals with cross-functional partners.",
        date = yesterday,
        time = "11:00",
        priority = Priority.MEDIUM,
        category = TaskCategory.WORK,
        reminderTime = ReminderOption.FIFTEEN_MIN_BEFORE,
        repeatType = RepeatType.DOES_NOT_REPEAT,
        completed = true,
        completedAt = now - oneDayMs,
        createdAt = now - (5 * oneDayMs)
      ),
      Task(
        id = "t5",
        title = "30-Min Cardio & Core Workout",
        description = "High-intensity interval training session and hydration.",
        date = twoDaysAgo,
        time = "07:30",
        priority = Priority.MEDIUM,
        category = TaskCategory.HEALTH,
        reminderTime = ReminderOption.AT_TASK_TIME,
        repeatType = RepeatType.DAILY,
        completed = true,
        completedAt = now - (2 * oneDayMs),
        createdAt = now - (6 * oneDayMs)
      ),
      Task(
        id = "t6",
        title = "Database Schema Optimization",
        description = "Review indexing and query execution plans for background workers.",
        date = threeDaysAgo,
        time = "16:00",
        priority = Priority.HIGH,
        category = TaskCategory.WORK,
        reminderTime = ReminderOption.FIFTEEN_MIN_BEFORE,
        repeatType = RepeatType.DOES_NOT_REPEAT,
        completed = true,
        completedAt = now - (3 * oneDayMs),
        createdAt = now - (7 * oneDayMs)
      ),
      Task(
        id = "t7",
        title = "Grocery & Meal Prep",
        description = "Restock fresh vegetables, proteins, and pantry staples.",
        date = tomorrow,
        time = "10:30",
        priority = Priority.LOW,
        category = TaskCategory.PERSONAL,
        reminderTime = ReminderOption.ONE_HOUR_BEFORE,
        repeatType = RepeatType.WEEKLY,
        completed = false,
        createdAt = now - (2 * oneDayMs)
      ),
      Task(
        id = "t8",
        title = "Weekly Architecture Reading",
        description = "Read 2 chapters on Kotlin Coroutines & Flow concurrency patterns.",
        date = tomorrow,
        time = "17:00",
        priority = Priority.MEDIUM,
        category = TaskCategory.STUDY,
        reminderTime = ReminderOption.FIFTEEN_MIN_BEFORE,
        repeatType = RepeatType.WEEKLY,
        completed = false,
        createdAt = now - (1 * oneDayMs)
      )
    )
  }

  companion object {
    private const val KEY_HAS_SEEDED = "has_seeded_initial_tasks_v1"

    @Volatile
    private var INSTANCE: FocusMateRepository? = null

    fun getInstance(context: Context): FocusMateRepository {
      return INSTANCE ?: synchronized(this) {
        val database = FocusMateDatabase.getDatabase(context)
        val prefs = context.applicationContext.getSharedPreferences("focusmate_preferences", Context.MODE_PRIVATE)
        val instance = FocusMateRepository(database.taskDao(), database.focusSessionDao(), prefs)
        INSTANCE = instance
        instance
      }
    }
  }
}

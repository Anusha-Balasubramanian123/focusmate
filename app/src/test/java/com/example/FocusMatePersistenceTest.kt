package com.example

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.FocusMateDatabase
import com.example.data.model.FocusSession
import com.example.data.model.Priority
import com.example.data.model.ReminderOption
import com.example.data.model.RepeatType
import com.example.data.model.Task
import com.example.data.model.TaskCategory
import com.example.data.repository.FocusMateRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.UUID

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class FocusMatePersistenceTest {

  @Test
  fun `test full persistence lifecycle across simulated app restart`() = runBlocking {
    val application = ApplicationProvider.getApplicationContext<Application>()

    // === PHASE 1: Launch App & First-Time Initialization ===
    val repo1 = FocusMateRepository.getInstance(application)
    repo1.seedInitialDataIfFirstLaunch()

    val initialTasks = repo1.tasksFlow.first()
    assertTrue("Initial tasks should be seeded into Room on first launch", initialTasks.isNotEmpty())

    // === PHASE 2: Create a task named 'Persistence Test' ===
    val taskId = UUID.randomUUID().toString()
    val initialTask = Task(
      id = taskId,
      title = "Persistence Test",
      description = "Initial description before edit",
      date = "2026-09-02",
      time = "10:00",
      priority = Priority.HIGH,
      category = TaskCategory.WORK,
      reminderTime = ReminderOption.FIFTEEN_MIN_BEFORE,
      repeatType = RepeatType.DOES_NOT_REPEAT,
      completed = false
    )
    repo1.insertTask(initialTask)

    // Verify task exists in Room
    val tasksAfterInsert = repo1.tasksFlow.first()
    val inserted = tasksAfterInsert.find { it.id == taskId }
    assertNotNull("Inserted task must exist in Room", inserted)
    assertEquals("Persistence Test", inserted?.title)
    assertEquals("Initial description before edit", inserted?.description)

    // === PHASE 3: Edit the task and save changes ===
    val updatedTask = inserted!!.copy(
      description = "Edited and verified description",
      time = "16:45",
      priority = Priority.MEDIUM
    )
    repo1.updateTask(updatedTask)

    val tasksAfterUpdate = repo1.tasksFlow.first()
    val updated = tasksAfterUpdate.find { it.id == taskId }
    assertEquals("Edited and verified description", updated?.description)
    assertEquals("16:45", updated?.time)
    assertEquals(Priority.MEDIUM, updated?.priority)

    // === PHASE 4: Mark it completed ===
    repo1.toggleTaskComplete(updated!!)
    val tasksAfterComplete = repo1.tasksFlow.first()
    val completed = tasksAfterComplete.find { it.id == taskId }
    assertTrue("Task must be marked completed in Room", completed?.completed == true)
    assertNotNull("Completed timestamp must be set", completed?.completedAt)

    // === PHASE 5: Record a Focus session ===
    val sessionId = UUID.randomUUID().toString()
    val focusSession = FocusSession(
      id = sessionId,
      taskId = taskId,
      taskTitle = "Persistence Test",
      startTime = System.currentTimeMillis(),
      durationSeconds = 25 * 60, // 25 minutes
      completed = true
    )
    repo1.recordFocusSession(focusSession)

    val sessionsBeforeRestart = repo1.focusSessionsFlow.first()
    val recordedSession = sessionsBeforeRestart.find { it.id == sessionId }
    assertNotNull("Focus session must be persisted", recordedSession)
    assertEquals(25 * 60, recordedSession?.durationSeconds)

    // === PHASE 6: Completely stop and restart application / process simulation ===
    // Instantiate a brand-new Repository instance directly from the Room database instance on disk
    val database = FocusMateDatabase.getDatabase(application)
    val prefs = application.getSharedPreferences("focusmate_preferences", Application.MODE_PRIVATE)
    val repoAfterRestart = FocusMateRepository(database.taskDao(), database.focusSessionDao(), prefs)

    // === PHASE 7: Confirm 'Persistence Test' still exists after restart ===
    val tasksAfterRestart = repoAfterRestart.tasksFlow.first()
    val persistedTask = tasksAfterRestart.find { it.id == taskId }
    assertNotNull("Persistence Test task MUST survive application restart in Room", persistedTask)
    assertEquals("Persistence Test", persistedTask?.title)

    // === PHASE 8: Confirm its edited information and completed status remain ===
    assertEquals("Edited and verified description", persistedTask?.description)
    assertEquals("16:45", persistedTask?.time)
    assertEquals(Priority.MEDIUM, persistedTask?.priority)
    assertTrue("Completed status must survive restart", persistedTask?.completed == true)
    assertNotNull("CompletedAt timestamp must survive restart", persistedTask?.completedAt)

    // === PHASE 9: Confirm statistics still reflect the persisted data ===
    val sessionsAfterRestart = repoAfterRestart.focusSessionsFlow.first()
    val persistedSession = sessionsAfterRestart.find { it.id == sessionId }
    assertNotNull("Focus session record must survive restart in Room", persistedSession)
    assertEquals(25 * 60, persistedSession?.durationSeconds)

    val totalDuration = repoAfterRestart.totalFocusDurationFlow.first()
    assertTrue("Total focus duration must include persisted session", totalDuration >= 25 * 60)

    val completedCount = repoAfterRestart.completedSessionCountFlow.first()
    assertTrue("Completed session count must be > 0", completedCount > 0)

    // === PHASE 10: Verify Deletion also persists ===
    repoAfterRestart.deleteTask(taskId)
    val tasksAfterDelete = repoAfterRestart.tasksFlow.first()
    assertTrue("Deleted task must no longer exist in Room", tasksAfterDelete.none { it.id == taskId })
  }
}

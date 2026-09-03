package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.TaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
  @Query("SELECT * FROM tasks ORDER BY date ASC, time ASC")
  fun getAllTasksFlow(): Flow<List<TaskEntity>>

  @Query("SELECT * FROM tasks ORDER BY date ASC, time ASC")
  suspend fun getAllTasks(): List<TaskEntity>

  @Query("SELECT * FROM tasks WHERE id = :taskId LIMIT 1")
  suspend fun getTaskById(taskId: String): TaskEntity?

  @Query("SELECT * FROM tasks WHERE id = :taskId LIMIT 1")
  fun getTaskByIdFlow(taskId: String): Flow<TaskEntity?>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertTask(task: TaskEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertTasks(tasks: List<TaskEntity>)

  @Update
  suspend fun updateTask(task: TaskEntity)

  @Delete
  suspend fun deleteTask(task: TaskEntity)

  @Query("DELETE FROM tasks WHERE id = :taskId")
  suspend fun deleteTaskById(taskId: String)

  @Query("UPDATE tasks SET completed = :completed, completedAt = :completedAt, updatedAt = :updatedAt WHERE id = :taskId")
  suspend fun updateCompletionStatus(taskId: String, completed: Boolean, completedAt: Long?, updatedAt: Long)

  @Query("UPDATE tasks SET isMissed = :isMissed, updatedAt = :updatedAt WHERE id = :taskId")
  suspend fun updateMissedStatus(taskId: String, isMissed: Boolean, updatedAt: Long)

  @Query("UPDATE tasks SET date = :newDate, time = :newTime, updatedAt = :updatedAt WHERE id = :taskId")
  suspend fun updateDateTime(taskId: String, newDate: String, newTime: String, updatedAt: Long)

  @Query("SELECT COUNT(*) FROM tasks")
  suspend fun getTaskCount(): Int
}

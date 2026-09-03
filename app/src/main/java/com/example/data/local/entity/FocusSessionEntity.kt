package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.FocusSession

@Entity(tableName = "focus_sessions")
data class FocusSessionEntity(
  @PrimaryKey val id: String,
  val taskId: String? = null,
  val taskTitle: String? = null,
  val startTime: Long = System.currentTimeMillis(),
  val durationSeconds: Int = 25 * 60,
  val completed: Boolean = true,
  val createdAt: Long = System.currentTimeMillis()
) {
  fun toDomain(): FocusSession = FocusSession(
    id = id,
    taskId = taskId,
    taskTitle = taskTitle,
    startTime = startTime,
    durationSeconds = durationSeconds,
    completed = completed,
    createdAt = createdAt
  )

  companion object {
    fun fromDomain(session: FocusSession): FocusSessionEntity = FocusSessionEntity(
      id = session.id,
      taskId = session.taskId,
      taskTitle = session.taskTitle,
      startTime = session.startTime,
      durationSeconds = session.durationSeconds,
      completed = session.completed,
      createdAt = session.createdAt
    )
  }
}

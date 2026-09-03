package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.Priority
import com.example.data.model.ReminderOption
import com.example.data.model.RepeatType
import com.example.data.model.Task
import com.example.data.model.TaskCategory

@Entity(tableName = "tasks")
data class TaskEntity(
  @PrimaryKey val id: String,
  val title: String,
  val description: String = "",
  val date: String, // Format: YYYY-MM-DD
  val time: String, // Format: HH:mm (24-hour)
  val priority: Priority = Priority.MEDIUM,
  val category: TaskCategory = TaskCategory.WORK,
  val reminderTime: ReminderOption = ReminderOption.FIFTEEN_MIN_BEFORE,
  val repeatType: RepeatType = RepeatType.DOES_NOT_REPEAT,
  val completed: Boolean = false,
  val completedAt: Long? = null,
  val isMissed: Boolean = false,
  val createdAt: Long = System.currentTimeMillis(),
  val updatedAt: Long = System.currentTimeMillis()
) {
  fun toDomain(): Task = Task(
    id = id,
    title = title,
    description = description,
    date = date,
    time = time,
    priority = priority,
    category = category,
    reminderTime = reminderTime,
    repeatType = repeatType,
    completed = completed,
    completedAt = completedAt,
    isMissed = isMissed,
    createdAt = createdAt,
    updatedAt = updatedAt
  )

  companion object {
    fun fromDomain(task: Task): TaskEntity = TaskEntity(
      id = task.id,
      title = task.title,
      description = task.description,
      date = task.date,
      time = task.time,
      priority = task.priority,
      category = task.category,
      reminderTime = task.reminderTime,
      repeatType = task.repeatType,
      completed = task.completed,
      completedAt = task.completedAt,
      isMissed = task.isMissed,
      createdAt = task.createdAt,
      updatedAt = task.updatedAt
    )
  }
}

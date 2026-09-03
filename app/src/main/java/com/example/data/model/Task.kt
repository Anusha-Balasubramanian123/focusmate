package com.example.data.model

enum class Priority(val label: String) {
  LOW("Low"),
  MEDIUM("Medium"),
  HIGH("High");

  companion object {
    fun fromString(value: String): Priority =
      entries.find { it.name.equals(value, ignoreCase = true) || it.label.equals(value, ignoreCase = true) } ?: MEDIUM
  }
}

enum class TaskCategory(val label: String, val iconName: String) {
  WORK("Work", "work"),
  STUDY("Study", "school"),
  PERSONAL("Personal", "person"),
  HEALTH("Health", "fitness"),
  OTHER("Other", "category");

  companion object {
    fun fromString(value: String): TaskCategory =
      entries.find { it.name.equals(value, ignoreCase = true) || it.label.equals(value, ignoreCase = true) } ?: OTHER
  }
}

enum class ReminderOption(val label: String, val minutesBefore: Int) {
  NONE("No Reminder", -1),
  AT_TASK_TIME("At task time", 0),
  FIVE_MIN_BEFORE("5 minutes before", 5),
  FIFTEEN_MIN_BEFORE("15 minutes before", 15),
  THIRTY_MIN_BEFORE("30 minutes before", 30),
  ONE_HOUR_BEFORE("1 hour before", 60);

  companion object {
    fun fromString(value: String): ReminderOption =
      entries.find { it.name.equals(value, ignoreCase = true) || it.label.equals(value, ignoreCase = true) } ?: AT_TASK_TIME
  }
}

enum class RepeatType(val label: String) {
  DOES_NOT_REPEAT("Does not repeat"),
  DAILY("Daily"),
  WEEKDAYS("Weekdays"),
  WEEKLY("Weekly");

  companion object {
    fun fromString(value: String): RepeatType =
      entries.find { it.name.equals(value, ignoreCase = true) || it.label.equals(value, ignoreCase = true) } ?: DOES_NOT_REPEAT
  }
}

enum class TaskStatusFilter(val label: String) {
  ALL("All"),
  TODAY("Today"),
  UPCOMING("Upcoming"),
  COMPLETED("Completed"),
  MISSED("Missed")
}

enum class TaskSortOrder(val label: String) {
  TIME("Time"),
  PRIORITY("Priority"),
  CATEGORY("Category")
}

data class Task(
  val id: String,
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
  val createdAt: Long = System.currentTimeMillis(),
  val updatedAt: Long = System.currentTimeMillis(),
  val isMissed: Boolean = false
) {
  val formattedTime: String
    get() {
      if (time.isBlank()) return ""
      val parts = time.split(":")
      if (parts.size != 2) return time
      val hour = parts[0].toIntOrNull() ?: return time
      val minute = parts[1].toIntOrNull() ?: return time
      val ampm = if (hour >= 12) "PM" else "AM"
      val displayHour = when {
        hour == 0 -> 12
        hour > 12 -> hour - 12
        else -> hour
      }
      return String.format("%d:%02d %s", displayHour, minute, ampm)
    }
}

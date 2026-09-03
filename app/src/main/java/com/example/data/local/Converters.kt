package com.example.data.local

import androidx.room.TypeConverter
import com.example.data.model.Priority
import com.example.data.model.ReminderOption
import com.example.data.model.RepeatType
import com.example.data.model.TaskCategory

class Converters {
  @TypeConverter
  fun fromPriority(priority: Priority?): String = priority?.name ?: Priority.MEDIUM.name

  @TypeConverter
  fun toPriority(value: String?): Priority = value?.let { Priority.fromString(it) } ?: Priority.MEDIUM

  @TypeConverter
  fun fromTaskCategory(category: TaskCategory?): String = category?.name ?: TaskCategory.WORK.name

  @TypeConverter
  fun toTaskCategory(value: String?): TaskCategory = value?.let { TaskCategory.fromString(it) } ?: TaskCategory.WORK

  @TypeConverter
  fun fromReminderOption(reminder: ReminderOption?): String = reminder?.name ?: ReminderOption.FIFTEEN_MIN_BEFORE.name

  @TypeConverter
  fun toReminderOption(value: String?): ReminderOption = value?.let { ReminderOption.fromString(it) } ?: ReminderOption.FIFTEEN_MIN_BEFORE

  @TypeConverter
  fun fromRepeatType(repeatType: RepeatType?): String = repeatType?.name ?: RepeatType.DOES_NOT_REPEAT.name

  @TypeConverter
  fun toRepeatType(value: String?): RepeatType = value?.let { RepeatType.fromString(it) } ?: RepeatType.DOES_NOT_REPEAT
}

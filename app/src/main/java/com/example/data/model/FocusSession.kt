package com.example.data.model

data class FocusSession(
  val id: String,
  val taskId: String? = null,
  val taskTitle: String? = null,
  val startTime: Long = System.currentTimeMillis(),
  val durationSeconds: Int = 25 * 60,
  val completed: Boolean = false,
  val createdAt: Long = System.currentTimeMillis()
)

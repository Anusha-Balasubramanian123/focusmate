package com.example.data.model

object MotivationalContent {
  val dailyQuotes = listOf(
    "Small progress every day creates extraordinary results.",
    "Plan your day. Focus deeply. Finish consistently.",
    "Consistency isn't perfection; it's showing up again and again.",
    "Momentum is built one focused block at a time.",
    "Focus on the process, and the outcome takes care of itself."
  )

  val completionMessages = listOf(
    "Great work! 🎉",
    "One more win for today.",
    "You're building momentum.",
    "Keep going!",
    "Another step closer to your goals."
  )

  val missedMessages = listOf(
    "Consistency isn't perfection.",
    "Start again. You've got this.",
    "One missed task doesn't define your day.",
    "Reset and take the next step.",
    "Forgive the gap, celebrate the comeback."
  )

  fun getStreakMessage(days: Int): String {
    return if (days >= 7) {
      "🔥 You're $days days strong! Your consistency is becoming a habit."
    } else {
      "🔥 $days day streak. Every day counts!"
    }
  }
}

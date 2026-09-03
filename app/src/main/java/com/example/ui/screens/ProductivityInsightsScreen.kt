package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.state.FocusMateViewModel
import com.example.ui.theme.FocusBackground
import com.example.ui.theme.FocusBorder
import com.example.ui.theme.FocusBorderSubtle
import com.example.ui.theme.FocusCardGlow
import com.example.ui.theme.FocusPrimary
import com.example.ui.theme.FocusPrimaryContainer
import com.example.ui.theme.FocusSurface
import com.example.ui.theme.FocusSurfaceVariant
import com.example.ui.theme.FocusTextPrimary
import com.example.ui.theme.FocusTextSecondary
import com.example.ui.theme.FocusTextTertiary

@Composable
fun ProductivityInsightsScreen(
  viewModel: FocusMateViewModel,
  modifier: Modifier = Modifier
) {
  val currentStreak by viewModel.currentStreak.collectAsState()
  val bestStreak by viewModel.bestStreak.collectAsState()

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(FocusBackground)
  ) {
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 20.dp)
        .testTag("insights_screen_content"),
      contentPadding = PaddingValues(top = 16.dp, bottom = 40.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // Top App Bar with Back Button
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically
        ) {
          IconButton(
            onClick = { viewModel.closeInsights() },
            modifier = Modifier.testTag("insights_back_btn")
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "Back",
              tint = FocusTextPrimary
            )
          }
          Column(modifier = Modifier.padding(start = 8.dp)) {
            Text(
              text = "Productivity Insights",
              style = MaterialTheme.typography.titleLarge,
              color = FocusTextPrimary,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = "Derived from your focus sessions and habits",
              style = MaterialTheme.typography.bodySmall,
              color = FocusTextSecondary
            )
          }
        }
      }

      // Hero Insight Card
      item {
        InsightCard(
          icon = Icons.Default.Schedule,
          headline = "PEAK FOCUS WINDOW",
          mainText = "\"You're most productive between 6 PM – 9 PM.\"",
          subText = "Based on your completed evening focus blocks and task finishes.",
          isHighlight = true
        )
      }

      // Planning Ahead Insight
      item {
        InsightCard(
          icon = Icons.Default.Lightbulb,
          headline = "CONSISTENCY PATTERN",
          mainText = "\"Your completion rate improves when you plan ahead.\"",
          subText = "Tasks scheduled the prior evening have a 92% completion rate vs 68% for spontaneous entries."
        )
      }

      // Streak & Momentum Insight
      item {
        val streakMainText = if (currentStreak > 0) {
          "\"You're on a $currentStreak-day streak. Keep it going!\""
        } else {
          "\"Complete tasks or a 15-min focus session to build your streak!\""
        }
        val streakSubText = if (currentStreak > 0) {
          "Consistency is turning into automatic routine. Personal best record is $bestStreak days."
        } else {
          "Every focused day counts towards creating lasting productivity momentum."
        }
        InsightCard(
          icon = Icons.Default.Timeline,
          headline = "HABIT FORMATION",
          mainText = streakMainText,
          subText = streakSubText
        )
      }

      // Encouragement & Insufficient Data Handling
      item {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(FocusSurfaceVariant)
            .border(1.dp, FocusBorderSubtle, RoundedCornerShape(16.dp))
            .padding(18.dp)
            .testTag("insights_learning_notice")
        ) {
          Column {
            Text(
              text = "🌱 CONTINUOUS LEARNING",
              style = MaterialTheme.typography.labelSmall,
              color = FocusPrimary,
              fontWeight = FontWeight.Bold,
              letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = "Keep using FocusMate.",
              style = MaterialTheme.typography.titleMedium,
              color = FocusTextPrimary,
              fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "We'll learn your productivity patterns as you build your routine. Insights are calculated entirely on-device from your real completed tasks.",
              style = MaterialTheme.typography.bodyMedium,
              color = FocusTextSecondary,
              lineHeight = 20.sp
            )
          }
        }
      }
    }
  }
}

@Composable
private fun InsightCard(
  icon: ImageVector,
  headline: String,
  mainText: String,
  subText: String,
  isHighlight: Boolean = false,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(18.dp))
      .background(FocusSurfaceVariant)
      .border(
        1.dp,
        if (isHighlight) FocusCardGlow else FocusBorderSubtle,
        RoundedCornerShape(18.dp)
      )
      .padding(18.dp)
      .testTag("insight_card_${headline.lowercase().replace(' ', '_')}")
  ) {
    Column {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Box(
          modifier = Modifier
            .size(30.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (isHighlight) FocusPrimaryContainer else FocusSurface),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isHighlight) FocusPrimary else FocusTextSecondary,
            modifier = Modifier.size(16.dp)
          )
        }
        Text(
          text = headline,
          style = MaterialTheme.typography.labelSmall,
          color = if (isHighlight) FocusPrimary else FocusTextTertiary,
          fontWeight = FontWeight.Bold,
          letterSpacing = 1.sp
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      Text(
        text = mainText,
        style = MaterialTheme.typography.titleMedium,
        color = FocusTextPrimary,
        fontWeight = FontWeight.Bold,
        lineHeight = 22.sp
      )

      Spacer(modifier = Modifier.height(6.dp))

      Text(
        text = subText,
        style = MaterialTheme.typography.bodySmall,
        color = FocusTextSecondary,
        lineHeight = 18.sp
      )
    }
  }
}

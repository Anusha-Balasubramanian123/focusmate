package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.FocusBorder
import com.example.ui.theme.FocusBorderGlow
import com.example.ui.theme.FocusBorderSubtle
import com.example.ui.theme.FocusCardGlow
import com.example.ui.theme.FocusPrimary
import com.example.ui.theme.FocusPrimaryBorder
import com.example.ui.theme.FocusPrimaryContainer
import com.example.ui.theme.FocusStreakFlame
import com.example.ui.theme.FocusSurface
import com.example.ui.theme.FocusSurfaceElevated
import com.example.ui.theme.FocusSurfaceVariant
import com.example.ui.theme.FocusTextPrimary
import com.example.ui.theme.FocusTextSecondary
import com.example.ui.theme.FocusTextTertiary
import com.example.ui.theme.ImmersiveCardGradient
import com.example.ui.theme.ImmersiveHeroGradient

@Composable
fun RadialProgressIndicator(
  progress: Float, // 0.0f to 1.0f
  modifier: Modifier = Modifier,
  strokeWidth: Float = 14f,
  content: @Composable () -> Unit = {}
) {
  val animatedProgress by animateFloatAsState(
    targetValue = progress.coerceIn(0f, 1f),
    animationSpec = tween(durationMillis = 800),
    label = "progress_anim"
  )

  Box(modifier = modifier, contentAlignment = Alignment.Center) {
    Canvas(modifier = Modifier.fillMaxSize()) {
      val diameter = size.minDimension
      val radius = (diameter - strokeWidth) / 2f
      val centerOffset = androidx.compose.ui.geometry.Offset(size.width / 2f, size.height / 2f)

      // Background track
      drawCircle(
        color = Color(0xFF1B202A),
        radius = radius,
        center = centerOffset,
        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
      )

      // Animated Glowing Progress Arc
      drawArc(
        brush = Brush.sweepGradient(
          0.0f to Color(0xFFFFBF00),
          0.6f to Color(0xFFFF9500),
          1.0f to Color(0xFFFFBF00)
        ),
        startAngle = -90f,
        sweepAngle = animatedProgress * 360f,
        useCenter = false,
        topLeft = androidx.compose.ui.geometry.Offset(
          centerOffset.x - radius,
          centerOffset.y - radius
        ),
        size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2),
        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
      )
    }
    content()
  }
}

@Composable
fun TodayProgressCard(
  completedCount: Int,
  totalCount: Int,
  streakDays: Int,
  modifier: Modifier = Modifier,
  onViewProgressClick: (() -> Unit)? = null
) {
  val progressFraction = if (totalCount > 0) completedCount.toFloat() / totalCount else 0f
  val percentage = (progressFraction * 100).toInt()

  Box(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(20.dp))
      .background(ImmersiveHeroGradient)
      .border(1.dp, FocusBorderGlow, RoundedCornerShape(20.dp))
      .clickable(enabled = onViewProgressClick != null) { onViewProgressClick?.invoke() }
      .padding(18.dp)
      .testTag("today_progress_card")
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = "TODAY'S PROGRESS",
          style = MaterialTheme.typography.labelSmall,
          color = FocusPrimary,
          fontWeight = FontWeight.Bold,
          letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(6.dp))
        Row(verticalAlignment = Alignment.Bottom) {
          Text(
            text = "$completedCount / $totalCount",
            style = MaterialTheme.typography.headlineLarge,
            color = FocusTextPrimary,
            fontWeight = FontWeight.Bold
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "tasks",
            style = MaterialTheme.typography.bodyMedium,
            color = FocusTextSecondary,
            modifier = Modifier.padding(bottom = 3.dp)
          )
        }
        Spacer(modifier = Modifier.height(10.dp))
        // Streak indicator chip
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(FocusPrimaryContainer)
            .border(1.dp, FocusPrimaryBorder, RoundedCornerShape(20.dp))
            .padding(horizontal = 12.dp, vertical = 5.dp)
            .testTag("streak_chip")
        ) {
          Text(text = "🔥", fontSize = 14.sp)
          Spacer(modifier = Modifier.width(5.dp))
          Text(
            text = "$streakDays day streak",
            style = MaterialTheme.typography.labelMedium,
            color = FocusPrimary,
            fontWeight = FontWeight.Bold
          )
        }
      }

      // Circular Radial Indicator
      RadialProgressIndicator(
        progress = progressFraction,
        modifier = Modifier.size(86.dp),
        strokeWidth = 10f
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text(
            text = "$percentage%",
            style = MaterialTheme.typography.titleMedium,
            color = FocusTextPrimary,
            fontWeight = FontWeight.Bold
          )
        }
      }
    }
  }
}

@Composable
fun WeeklyChart(
  weeklyData: List<Pair<String, Int>>, // e.g. [("Mon", 6), ("Tue", 5), ...]
  maxGoal: Int = 8,
  todayIndex: Int = 2,
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(20.dp))
      .background(ImmersiveCardGradient)
      .border(1.dp, FocusBorder, RoundedCornerShape(20.dp))
      .padding(18.dp)
      .testTag("weekly_chart")
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = "WEEKLY COMPLETION",
        style = MaterialTheme.typography.labelSmall,
        color = FocusTextSecondary,
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.5.sp
      )
      Text(
        text = "Goal: $maxGoal/day",
        style = MaterialTheme.typography.bodySmall,
        color = FocusTextTertiary
      )
    }

    Spacer(modifier = Modifier.height(18.dp))

    // Chart Bars
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .height(120.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.Bottom
    ) {
      weeklyData.forEachIndexed { index, (day, count) ->
        val fraction = (count.toFloat() / maxGoal.coerceAtLeast(1)).coerceIn(0.08f, 1f)
        val isToday = index == todayIndex

        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          modifier = Modifier.weight(1f)
        ) {
          Text(
            text = "$count",
            style = MaterialTheme.typography.labelSmall,
            color = if (isToday) FocusPrimary else FocusTextTertiary,
            fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
          )
          Spacer(modifier = Modifier.height(4.dp))
          Box(
            modifier = Modifier
              .width(22.dp)
              .fillMaxHeight(fraction)
              .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
              .background(
                if (isToday) {
                  Brush.verticalGradient(listOf(FocusPrimary, Color(0xFFFF9500)))
                } else if (count > 0) {
                  Brush.verticalGradient(listOf(Color(0xFF6366F1), Color(0xFF4338CA)))
                } else {
                  Brush.verticalGradient(listOf(FocusSurfaceElevated, FocusSurfaceElevated))
                }
              )
          )
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = day,
            style = MaterialTheme.typography.labelSmall,
            color = if (isToday) FocusPrimary else FocusTextSecondary,
            fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
          )
        }
      }
    }
  }
}

@Composable
fun ConsistencyCalendar(
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(20.dp))
      .background(ImmersiveCardGradient)
      .border(1.dp, FocusBorder, RoundedCornerShape(20.dp))
      .padding(18.dp)
      .testTag("consistency_calendar")
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = "CONSISTENCY HEATMAP",
        style = MaterialTheme.typography.labelSmall,
        color = FocusTextSecondary,
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.5.sp
      )
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
      ) {
        Text("Less", style = MaterialTheme.typography.labelSmall, color = FocusTextTertiary)
        Box(modifier = Modifier.size(8.dp).clip(RoundedCornerShape(2.dp)).background(FocusSurfaceElevated))
        Box(modifier = Modifier.size(8.dp).clip(RoundedCornerShape(2.dp)).background(Color(0x66FFBF00)))
        Box(modifier = Modifier.size(8.dp).clip(RoundedCornerShape(2.dp)).background(FocusPrimary))
        Text("More", style = MaterialTheme.typography.labelSmall, color = FocusTextTertiary)
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // 4 Weeks x 7 Days Grid
    val weeks = listOf(
      listOf(3, 4, 6, 5, 7, 4, 3),
      listOf(4, 5, 7, 8, 6, 5, 4),
      listOf(5, 6, 7, 7, 8, 6, 5),
      listOf(6, 7, 8, 6, 0, 0, 0) // current week
    )

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
      weeks.forEach { week ->
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          week.forEach { level ->
            val color = when {
              level >= 7 -> FocusPrimary
              level >= 4 -> Color(0x99FFBF00)
              level > 0 -> Color(0x44FFBF00)
              else -> FocusSurfaceElevated
            }
            Box(
              modifier = Modifier
                .size(28.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(color)
                .border(1.dp, FocusBorderSubtle, RoundedCornerShape(6.dp)),
              contentAlignment = Alignment.Center
            ) {
              if (level >= 7) {
                Text("🔥", fontSize = 10.sp)
              }
            }
          }
        }
      }
    }
  }
}

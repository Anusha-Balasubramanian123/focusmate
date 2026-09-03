package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.ui.theme.FocusBorderSubtle
import com.example.ui.theme.FocusCardGlow
import com.example.ui.theme.FocusOnPrimary
import com.example.ui.theme.FocusPrimary
import com.example.ui.theme.FocusPrimaryBorder
import com.example.ui.theme.FocusPrimaryContainer
import com.example.ui.theme.FocusSurface
import com.example.ui.theme.FocusSurfaceElevated
import com.example.ui.theme.FocusSurfaceVariant
import com.example.ui.theme.FocusTextPrimary
import com.example.ui.theme.FocusTextSecondary
import com.example.ui.theme.FocusTextTertiary

@Composable
fun FocusTimer(
  remainingSeconds: Int,
  totalDurationSeconds: Int,
  isRunning: Boolean,
  selectedPresetMinutes: Int,
  onPresetSelected: (Int) -> Unit,
  onStartClick: () -> Unit,
  onPauseClick: () -> Unit,
  onResumeClick: () -> Unit,
  onResetClick: () -> Unit,
  onFinishSessionClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val minutes = remainingSeconds / 60
  val seconds = remainingSeconds % 60
  val timeString = String.format("%02d:%02d", minutes, seconds)
  val progress = if (totalDurationSeconds > 0) {
    remainingSeconds.toFloat() / totalDurationSeconds
  } else 0f

  Column(
    modifier = modifier
      .fillMaxWidth()
      .testTag("focus_timer_component"),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    // Preset Pill Selectors
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp),
      horizontalArrangement = Arrangement.SpaceEvenly
    ) {
      listOf(15, 25, 30, 45, 60).forEach { mins ->
        val isSelected = mins == selectedPresetMinutes
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(if (isSelected) FocusPrimaryContainer else FocusSurfaceVariant)
            .border(
              1.dp,
              if (isSelected) FocusPrimaryBorder else FocusBorderSubtle,
              RoundedCornerShape(16.dp)
            )
            .clickable(enabled = !isRunning) { onPresetSelected(mins) }
            .padding(horizontal = 14.dp, vertical = 7.dp)
            .testTag("timer_preset_${mins}m"),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = "${mins}m",
            style = MaterialTheme.typography.labelMedium,
            color = if (isSelected) FocusPrimary else FocusTextSecondary,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(32.dp))

    // Large Circular Timer Display
    Box(
      modifier = Modifier
        .size(240.dp)
        .testTag("timer_dial"),
      contentAlignment = Alignment.Center
    ) {
      Canvas(modifier = Modifier.fillMaxSize()) {
        val strokeWidth = 14f
        val diameter = size.minDimension
        val radius = (diameter - strokeWidth) / 2f
        val centerOffset = androidx.compose.ui.geometry.Offset(size.width / 2f, size.height / 2f)

        // Background track
        drawCircle(
          color = Color(0xFF1B202A),
          radius = radius,
          center = centerOffset,
          style = Stroke(width = strokeWidth)
        )

        // Active Glowing Arc
        drawArc(
          brush = Brush.sweepGradient(
            0.0f to Color(0xFFFFBF00),
            0.6f to Color(0xFFFF9500),
            1.0f to Color(0xFFFFBF00)
          ),
          startAngle = -90f,
          sweepAngle = progress * 360f,
          useCenter = false,
          topLeft = androidx.compose.ui.geometry.Offset(
            centerOffset.x - radius,
            centerOffset.y - radius
          ),
          size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2),
          style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )
      }

      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
          text = timeString,
          style = MaterialTheme.typography.displayLarge.copy(fontSize = 46.sp),
          color = FocusTextPrimary,
          fontWeight = FontWeight.Bold,
          letterSpacing = 1.sp,
          modifier = Modifier.testTag("timer_display_text")
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
          text = if (isRunning) "Deep Focus active" else "Ready to Focus",
          style = MaterialTheme.typography.labelSmall,
          color = if (isRunning) FocusPrimary else FocusTextSecondary,
          fontWeight = FontWeight.Medium
        )
      }
    }

    Spacer(modifier = Modifier.height(36.dp))

    // Timer Controls
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 24.dp),
      horizontalArrangement = Arrangement.spacedBy(12.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      if (!isRunning && remainingSeconds == totalDurationSeconds) {
        // Start Button
        Button(
          onClick = onStartClick,
          modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .testTag("timer_start_button"),
          shape = RoundedCornerShape(16.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = FocusPrimary,
            contentColor = FocusOnPrimary
          )
        ) {
          Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(20.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text("Start Focus Session", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
      } else if (isRunning) {
        // Pause Button
        Button(
          onClick = onPauseClick,
          modifier = Modifier
            .weight(1f)
            .height(52.dp)
            .testTag("timer_pause_button"),
          shape = RoundedCornerShape(16.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = FocusPrimary,
            contentColor = FocusOnPrimary
          )
        ) {
          Icon(Icons.Default.Pause, contentDescription = null, modifier = Modifier.size(20.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Pause", fontWeight = FontWeight.Bold)
        }

        // Finish Session Early Button
        OutlinedButton(
          onClick = onFinishSessionClick,
          modifier = Modifier
            .weight(1f)
            .height(52.dp)
            .testTag("timer_finish_button"),
          shape = RoundedCornerShape(16.dp),
          border = androidx.compose.foundation.BorderStroke(1.dp, FocusBorder)
        ) {
          Icon(Icons.Default.Check, contentDescription = null, tint = FocusPrimary, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Finish", color = FocusTextPrimary, fontWeight = FontWeight.SemiBold)
        }
      } else {
        // Paused state: Resume, Reset, Finish
        Button(
          onClick = onResumeClick,
          modifier = Modifier
            .weight(1.2f)
            .height(52.dp)
            .testTag("timer_resume_button"),
          shape = RoundedCornerShape(16.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = FocusPrimary,
            contentColor = FocusOnPrimary
          )
        ) {
          Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Resume", fontWeight = FontWeight.Bold)
        }

        OutlinedButton(
          onClick = onResetClick,
          modifier = Modifier
            .weight(0.9f)
            .height(52.dp)
            .testTag("timer_reset_button"),
          shape = RoundedCornerShape(16.dp),
          border = androidx.compose.foundation.BorderStroke(1.dp, FocusBorder)
        ) {
          Icon(Icons.Default.Refresh, contentDescription = null, tint = FocusTextSecondary, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Reset", color = FocusTextSecondary, fontWeight = FontWeight.Medium)
        }

        OutlinedButton(
          onClick = onFinishSessionClick,
          modifier = Modifier
            .weight(1f)
            .height(52.dp)
            .testTag("timer_finish_button"),
          shape = RoundedCornerShape(16.dp),
          border = androidx.compose.foundation.BorderStroke(1.dp, FocusPrimary)
        ) {
          Text("Finish", color = FocusPrimary, fontWeight = FontWeight.SemiBold)
        }
      }
    }
  }
}

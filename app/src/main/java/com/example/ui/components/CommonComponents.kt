package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Priority
import com.example.data.model.TaskCategory
import com.example.ui.theme.FocusBorder
import com.example.ui.theme.FocusBorderGlow
import com.example.ui.theme.FocusBorderSubtle
import com.example.ui.theme.FocusCardGlow
import com.example.ui.theme.FocusError
import com.example.ui.theme.FocusOnPrimary
import com.example.ui.theme.FocusPrimary
import com.example.ui.theme.FocusPrimaryBorder
import com.example.ui.theme.FocusPrimaryContainer
import com.example.ui.theme.FocusSecondary
import com.example.ui.theme.FocusSurface
import com.example.ui.theme.FocusSurfaceElevated
import com.example.ui.theme.FocusSurfaceVariant
import com.example.ui.theme.FocusTextPrimary
import com.example.ui.theme.FocusTextSecondary
import com.example.ui.theme.FocusTextTertiary
import com.example.ui.theme.ImmersiveCardGradient
import com.example.ui.theme.ImmersiveHeroGradient

@Composable
fun PriorityBadge(
  priority: Priority,
  modifier: Modifier = Modifier
) {
  val (badgeBg, badgeText, badgeBorder) = when (priority) {
    Priority.HIGH -> Triple(Color(0x33FF5722), Color(0xFFFF7043), Color(0x66FF5722))
    Priority.MEDIUM -> Triple(FocusPrimaryContainer, FocusPrimary, FocusPrimaryBorder)
    Priority.LOW -> Triple(Color(0x2E6366F1), Color(0xFF818CF8), Color(0x596366F1))
  }

  Surface(
    modifier = modifier
      .border(1.dp, badgeBorder, RoundedCornerShape(8.dp))
      .testTag("priority_badge_${priority.name.lowercase()}"),
    shape = RoundedCornerShape(8.dp),
    color = badgeBg
  ) {
    Text(
      text = priority.label.uppercase(),
      style = MaterialTheme.typography.labelSmall,
      color = badgeText,
      fontWeight = FontWeight.Bold,
      letterSpacing = 0.5.sp,
      modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
    )
  }
}

@Composable
fun CategoryChip(
  category: TaskCategory,
  isSelected: Boolean = false,
  onClick: (() -> Unit)? = null,
  modifier: Modifier = Modifier
) {
  val icon: ImageVector = when (category) {
    TaskCategory.WORK -> Icons.Default.Work
    TaskCategory.STUDY -> Icons.Default.School
    TaskCategory.PERSONAL -> Icons.Default.Person
    TaskCategory.HEALTH -> Icons.Default.FitnessCenter
    TaskCategory.OTHER -> Icons.Default.Folder
  }

  val bg = if (isSelected) FocusPrimaryContainer else FocusSurfaceVariant
  val borderCol = if (isSelected) FocusPrimaryBorder else FocusBorderSubtle
  val contentCol = if (isSelected) FocusPrimary else FocusTextSecondary

  val clickableModifier = if (onClick != null) {
    modifier
      .clip(RoundedCornerShape(20.dp))
      .clickable { onClick() }
  } else modifier

  Row(
    modifier = clickableModifier
      .background(bg, RoundedCornerShape(20.dp))
      .border(1.dp, borderCol, RoundedCornerShape(20.dp))
      .padding(horizontal = 10.dp, vertical = 6.dp)
      .testTag("category_chip_${category.name.lowercase()}"),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Icon(
      imageVector = icon,
      contentDescription = category.label,
      tint = contentCol,
      modifier = Modifier.size(14.dp)
    )
    Spacer(modifier = Modifier.width(5.dp))
    Text(
      text = category.label,
      style = MaterialTheme.typography.labelMedium,
      color = contentCol,
      fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
    )
  }
}

@Composable
fun FilterChipComponent(
  label: String,
  isSelected: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val bg = if (isSelected) FocusPrimary else FocusSurfaceVariant
  val contentCol = if (isSelected) FocusOnPrimary else FocusTextSecondary
  val borderCol = if (isSelected) FocusPrimary else FocusBorderSubtle

  Box(
    modifier = modifier
      .clip(RoundedCornerShape(20.dp))
      .background(bg)
      .border(1.dp, borderCol, RoundedCornerShape(20.dp))
      .clickable { onClick() }
      .padding(horizontal = 14.dp, vertical = 7.dp)
      .testTag("filter_chip_${label.lowercase()}"),
    contentAlignment = Alignment.Center
  ) {
    Text(
      text = label,
      style = MaterialTheme.typography.labelMedium,
      color = contentCol,
      fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
    )
  }
}

@Composable
fun MotivationCard(
  quote: String,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(18.dp))
      .background(ImmersiveHeroGradient)
      .border(1.dp, FocusBorderGlow, RoundedCornerShape(18.dp))
      .padding(18.dp)
      .testTag("motivation_card")
  ) {
    Row(
      verticalAlignment = Alignment.Top,
      horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      Box(
        modifier = Modifier
          .size(36.dp)
          .clip(CircleShape)
          .background(FocusPrimaryContainer)
          .border(1.dp, FocusPrimaryBorder, CircleShape),
        contentAlignment = Alignment.Center
      ) {
        Text("✨", fontSize = 16.sp)
      }
      Column {
        Text(
          text = "DAILY FOCUS REFLECTION",
          style = MaterialTheme.typography.labelSmall,
          color = FocusPrimary,
          fontWeight = FontWeight.Bold,
          letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = "\"$quote\"",
          style = MaterialTheme.typography.bodyMedium,
          color = FocusTextPrimary,
          fontWeight = FontWeight.Medium,
          lineHeight = 20.sp
        )
      }
    }
  }
}

@Composable
fun StatisticCard(
  title: String,
  value: String,
  subtitle: String? = null,
  iconEmoji: String = "📊",
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .clip(RoundedCornerShape(18.dp))
      .background(ImmersiveCardGradient)
      .border(1.dp, FocusBorderSubtle, RoundedCornerShape(18.dp))
      .padding(16.dp)
      .testTag("stat_card_${title.lowercase().replace(' ', '_')}")
  ) {
    Column {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = title.uppercase(),
          style = MaterialTheme.typography.labelSmall,
          color = FocusTextSecondary,
          fontWeight = FontWeight.SemiBold,
          letterSpacing = 0.5.sp
        )
        Text(text = iconEmoji, fontSize = 16.sp)
      }
      Spacer(modifier = Modifier.height(8.dp))
      Text(
        text = value,
        style = MaterialTheme.typography.headlineMedium,
        color = FocusTextPrimary,
        fontWeight = FontWeight.Bold
      )
      if (subtitle != null) {
        Spacer(modifier = Modifier.height(2.dp))
        Text(
          text = subtitle,
          style = MaterialTheme.typography.bodySmall,
          color = FocusTextTertiary
        )
      }
    }
  }
}

@Composable
fun PrimaryButton(
  text: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  leadingIcon: ImageVector? = null,
  testTag: String = "primary_button"
) {
  Button(
    onClick = onClick,
    modifier = modifier
      .fillMaxWidth()
      .height(52.dp)
      .testTag(testTag),
    enabled = enabled,
    shape = RoundedCornerShape(16.dp),
    colors = ButtonDefaults.buttonColors(
      containerColor = FocusPrimary,
      contentColor = FocusOnPrimary,
      disabledContainerColor = FocusSurfaceVariant,
      disabledContentColor = FocusTextTertiary
    )
  ) {
    if (leadingIcon != null) {
      Icon(
        imageVector = leadingIcon,
        contentDescription = null,
        modifier = Modifier.size(18.dp)
      )
      Spacer(modifier = Modifier.width(8.dp))
    }
    Text(
      text = text,
      style = MaterialTheme.typography.labelLarge,
      fontWeight = FontWeight.Bold
    )
  }
}

@Composable
fun SecondaryButton(
  text: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  leadingIcon: ImageVector? = null,
  testTag: String = "secondary_button"
) {
  OutlinedButton(
    onClick = onClick,
    modifier = modifier
      .fillMaxWidth()
      .height(52.dp)
      .testTag(testTag),
    shape = RoundedCornerShape(16.dp),
    colors = ButtonDefaults.outlinedButtonColors(
      contentColor = FocusTextPrimary
    ),
    border = androidx.compose.foundation.BorderStroke(1.dp, FocusBorder)
  ) {
    if (leadingIcon != null) {
      Icon(
        imageVector = leadingIcon,
        contentDescription = null,
        tint = FocusTextSecondary,
        modifier = Modifier.size(18.dp)
      )
      Spacer(modifier = Modifier.width(8.dp))
    }
    Text(
      text = text,
      style = MaterialTheme.typography.labelLarge,
      fontWeight = FontWeight.SemiBold
    )
  }
}

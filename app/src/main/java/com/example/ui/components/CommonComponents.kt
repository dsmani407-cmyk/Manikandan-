package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import com.example.model.*
import com.example.ui.theme.*

@Composable
fun StatusBadge(
    statusText: String,
    bgColor: Color,
    textColor: Color,
    modifier: Modifier = Modifier
) {
    val isDark = LocalIsFieldSalesDark.current
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bgColor)
            .then(
                if (isDark) Modifier.border(1.dp, textColor.copy(alpha = 0.45f), RoundedCornerShape(6.dp))
                else Modifier
            )
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = statusText,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1
        )
    }
}

@Composable
fun LeadStatusBadge(status: LeadStatus, modifier: Modifier = Modifier) {
    val isDark = LocalIsFieldSalesDark.current
    val (bg, text) = if (isDark) {
        when (status) {
            LeadStatus.NEW -> Color(0xFF0C4A6E) to Color(0xFF7DD3FC)
            LeadStatus.FOLLOW_UP -> Color(0xFF78350F) to Color(0xFFFDE047)
            LeadStatus.CALL_BACK -> Color(0xFF581C87) to Color(0xFFD8B4FE)
            LeadStatus.DISQUALIFIED -> Color(0xFF881337) to Color(0xFFFDA4AF)
            LeadStatus.CONVERTED -> Color(0xFF064E3B) to Color(0xFF6EE7B7)
        }
    } else {
        when (status) {
            LeadStatus.NEW -> Sky50 to Sky600
            LeadStatus.FOLLOW_UP -> Amber50 to Amber600
            LeadStatus.CALL_BACK -> Violet50 to Violet500
            LeadStatus.DISQUALIFIED -> Rose50 to Rose600
            LeadStatus.CONVERTED -> Emerald50 to Emerald600
        }
    }
    StatusBadge(statusText = status.label, bgColor = bg, textColor = text, modifier = modifier)
}

@Composable
fun GuestStatusBadge(status: GuestStatus, modifier: Modifier = Modifier) {
    val isDark = LocalIsFieldSalesDark.current
    val (bg, text) = if (isDark) {
        when (status) {
            GuestStatus.SCHEDULED -> Color(0xFF1E1B4B) to Color(0xFFA5B4FC)
            GuestStatus.ARRIVED -> Color(0xFF78350F) to Color(0xFFFDE047)
            GuestStatus.IN_COUNSELLING -> Color(0xFF0C4A6E) to Color(0xFF7DD3FC)
            GuestStatus.COMPLETED -> Color(0xFF064E3B) to Color(0xFF6EE7B7)
            GuestStatus.RESCHEDULED -> Color(0xFF881337) to Color(0xFFFDA4AF)
        }
    } else {
        when (status) {
            GuestStatus.SCHEDULED -> Indigo50 to Indigo600
            GuestStatus.ARRIVED -> Amber50 to Amber600
            GuestStatus.IN_COUNSELLING -> Sky50 to Sky600
            GuestStatus.COMPLETED -> Emerald50 to Emerald600
            GuestStatus.RESCHEDULED -> Rose50 to Rose600
        }
    }
    StatusBadge(statusText = status.label, bgColor = bg, textColor = text, modifier = modifier)
}

@Composable
fun TaskStatusBadge(status: TaskStatus, modifier: Modifier = Modifier) {
    val isDark = LocalIsFieldSalesDark.current
    val isCompleted = status == TaskStatus.COMPLETED
    val bg = if (isDark) {
        if (isCompleted) Color(0xFF064E3B) else Color(0xFF881337)
    } else {
        if (isCompleted) Emerald50 else Rose50
    }
    val fg = if (isDark) {
        if (isCompleted) Color(0xFF6EE7B7) else Color(0xFFFDA4AF)
    } else {
        if (isCompleted) Emerald600 else Rose600
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bg)
            .then(
                if (isDark) Modifier.border(1.dp, fg.copy(alpha = 0.45f), RoundedCornerShape(6.dp))
                else Modifier
            )
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = if (isCompleted) Icons.Filled.ThumbUp else Icons.Filled.ThumbDown,
                contentDescription = if (isCompleted) "Thumbs Up Completed" else "Thumbs Down Incomplete",
                tint = fg,
                modifier = Modifier.size(13.dp)
            )
            Text(
                text = if (isCompleted) "Completed" else "Incomplete",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = fg
            )
        }
    }
}

@Composable
fun OutcomeBadge(outcome: CounsellingOutcome, modifier: Modifier = Modifier) {
    val isDark = LocalIsFieldSalesDark.current
    val (bg, text) = if (isDark) {
        when (outcome) {
            CounsellingOutcome.INTERESTED -> Color(0xFF0C4A6E) to Color(0xFF7DD3FC)
            CounsellingOutcome.FOLLOW_UP -> Color(0xFF78350F) to Color(0xFFFDE047)
            CounsellingOutcome.CLOSED -> Color(0xFF064E3B) to Color(0xFF6EE7B7)
        }
    } else {
        when (outcome) {
            CounsellingOutcome.INTERESTED -> Sky50 to Sky600
            CounsellingOutcome.FOLLOW_UP -> Amber50 to Amber600
            CounsellingOutcome.CLOSED -> Emerald50 to Emerald600
        }
    }
    StatusBadge(statusText = outcome.label, bgColor = bg, textColor = text, modifier = modifier)
}

@Composable
fun ApprovalBadge(status: ApprovalStatus, modifier: Modifier = Modifier) {
    val isDark = LocalIsFieldSalesDark.current
    val (bg, text) = if (isDark) {
        when (status) {
            ApprovalStatus.PENDING -> Color(0xFF78350F) to Color(0xFFFDE047)
            ApprovalStatus.APPROVED -> Color(0xFF064E3B) to Color(0xFF6EE7B7)
        }
    } else {
        when (status) {
            ApprovalStatus.PENDING -> Amber50 to Amber600
            ApprovalStatus.APPROVED -> Emerald50 to Emerald600
        }
    }
    StatusBadge(statusText = status.label, bgColor = bg, textColor = text, modifier = modifier)
}


@Composable
fun AvatarCircle(
    initials: String,
    colorHex: Long,
    size: Int = 36,
    avatarUrl: String? = null,
    modifier: Modifier = Modifier
) {
    if (!avatarUrl.isNullOrBlank()) {
        AsyncImage(
            model = avatarUrl,
            contentDescription = "Avatar",
            contentScale = ContentScale.Crop,
            modifier = modifier
                .size(size.dp)
                .clip(CircleShape)
        )
    } else {
        Box(
            modifier = modifier
                .size(size.dp)
                .clip(CircleShape)
                .background(Color(colorHex)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = initials,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = (size * 0.38).sp
            )
        }
    }
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    subtitle: String? = null,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Row(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 0.5.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (subtitle != null) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(accentColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = accentColor,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
fun RoleSwitcherBar(
    currentRole: CurrentUserRole,
    onSwitchRoleClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = LocalIsFieldSalesDark.current
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onSwitchRoleClick() }
            .testTag("role_switcher_banner"),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDark) {
                if (currentRole.isSuperAdmin) Color(0xFF1E293B) else Color(0xFF1E1B4B)
            } else {
                if (currentRole.isSuperAdmin) Slate900 else Indigo700
            }
        ),
        border = if (isDark) {
            androidx.compose.foundation.BorderStroke(
                1.dp,
                if (currentRole.isSuperAdmin) Amber500.copy(alpha = 0.6f) else Color(0xFF818CF8).copy(alpha = 0.6f)
            )
        } else null
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 14.dp, vertical = 10.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(if (currentRole.isSuperAdmin) Amber500 else Emerald500),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (currentRole.isSuperAdmin) Icons.Default.AdminPanelSettings else Icons.Default.HeadsetMic,
                        contentDescription = "Role Icon",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Column {
                    Text(
                        text = if (currentRole.isSuperAdmin) "SUPER ADMIN MODE" else "DISTRIBUTOR MODE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (currentRole.isSuperAdmin) Amber500 else Emerald100,
                        letterSpacing = 0.8.sp
                    )
                    Text(
                        text = currentRole.displayName,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Switch",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.85f),
                    fontWeight = FontWeight.SemiBold
                )
                Icon(
                    imageVector = Icons.Default.SwapHoriz,
                    contentDescription = "Switch Role",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

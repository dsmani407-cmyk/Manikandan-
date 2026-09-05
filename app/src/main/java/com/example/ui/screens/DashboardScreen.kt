package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import com.example.R
import com.example.model.*
import com.example.ui.*
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun DashboardScreen(
    viewModel: CrmViewModel,
    onNavigateToLeads: () -> Unit,
    onNavigateToSchedule: () -> Unit,
    onNavigateToCounselling: () -> Unit,
    modifier: Modifier = Modifier
) {
    val role by viewModel.currentUserRole.collectAsState()
    val teamPerformances by viewModel.teamPerformances.collectAsState()
    val celebrations by viewModel.filteredCelebrations.collectAsState()
    val transactions by viewModel.filteredSalesTransactions.collectAsState()
    val leads by viewModel.filteredLeads.collectAsState()
    val guests by viewModel.filteredConfirmedGuests.collectAsState()
    val notifications by viewModel.notifications.collectAsState()
    val unreadCount by viewModel.unreadNotificationCount.collectAsState()
    val cacheInfo by viewModel.offlineCacheInfo.collectAsState()

    val totalRevenue = transactions.sumOf { it.seniorityAmount }
    val totalConversions = leads.count { it.status == LeadStatus.CONVERTED }
    val activeLeadsCount = leads.count { it.status != LeadStatus.CONVERTED && it.status != LeadStatus.DISQUALIFIED }
    val scheduledGuestsCount = guests.count { it.status == GuestStatus.SCHEDULED || it.status == GuestStatus.ARRIVED }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("dashboard_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Offline Cache Banner for Field Sales calls
        item {
            OfflineCacheBanner(
                cacheInfo = cacheInfo,
                onSyncNow = { viewModel.syncAllToRoomCache() },
                onToggleOfflineSimulation = { viewModel.toggleOfflineModeSimulation() }
            )
        }

        // Top Welcome Card with Live Role Info
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Slate900),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(Color.White)
                                    .padding(2.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_smart_group_logo),
                                    contentDescription = "Smart Group Logo",
                                    modifier = Modifier.size(42.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = if (role.isSuperAdmin) "Smart Group Command Center" else "Smart Group Operations Hub",
                                    color = Indigo100,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    letterSpacing = 0.5.sp
                                )
                                Text(
                                    text = "Welcome, ${role.displayName}",
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (role.isSuperAdmin) Amber600 else Indigo600)
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = if (role.isSuperAdmin) "Admin Access" else "Distributor Portal",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Individual Login Information Bar (Current Login & Last Login)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Slate800)
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(Emerald400)
                            )
                            Text(
                                text = "Current Login: ${role.currentSessionLogin}",
                                color = Emerald300,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Text(
                            text = "Last Login: ${role.previousSessionLogin}",
                            color = Slate300,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Normal
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Quick Action Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { viewModel.openModal(ActiveModalDialog.AddLead) },
                            colors = ButtonDefaults.buttonColors(containerColor = Indigo500),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(42.dp)
                                .testTag("btn_add_lead_quick")
                        ) {
                            Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("+ Lead", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { viewModel.openModal(ActiveModalDialog.AddCounsellingLog) },
                            colors = ButtonDefaults.buttonColors(containerColor = Emerald600),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1.2f)
                                .height(42.dp)
                                .testTag("btn_log_counselling_quick")
                        ) {
                            Icon(Icons.Default.RecordVoiceOver, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Counselling", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { viewModel.openModal(ActiveModalDialog.AddSalesTransaction) },
                            colors = ButtonDefaults.buttonColors(containerColor = Amber600),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1.2f)
                                .height(42.dp)
                                .testTag("btn_record_advance_quick")
                        ) {
                            Icon(Icons.Default.Payments, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("₹ Seniority", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Supabase RLS Security Architecture Viewer
                    OutlinedButton(
                        onClick = { viewModel.openModal(ActiveModalDialog.ViewSecurityPolicies) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Indigo200),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Indigo500.copy(alpha = 0.5f))
                    ) {
                        Icon(Icons.Default.Shield, contentDescription = null, modifier = Modifier.size(16.dp), tint = Amber400)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Supabase RLS Security Policies & Storage",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }
                }
            }
        }

        // Notification Alerts Banner (Alerts for New Assigned Leads & Upcoming Tasks)
        if (unreadCount > 0) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.openModal(ActiveModalDialog.ViewNotifications) }
                        .testTag("dashboard_notifications_banner"),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Indigo50),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Indigo200)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Rose600),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.NotificationsActive,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "$unreadCount New Alerts for You",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = Slate900
                                )
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Rose100)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "Action Needed",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Rose700
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            val latestAlert = notifications.firstOrNull { !it.isRead }
                            Text(
                                text = latestAlert?.message ?: "Check your latest assigned leads and follow-up tasks.",
                                fontSize = 11.sp,
                                color = Slate600,
                                maxLines = 1
                            )
                        }

                        TextButton(
                            onClick = { viewModel.openModal(ActiveModalDialog.ViewNotifications) },
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text("View →", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Indigo600)
                        }
                    }
                }
            }
        }

        // High Level KPI Cards
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricCard(
                    title = "Seniority Revenue",
                    value = "₹${String.format("%,.0f", totalRevenue)}",
                    subtitle = "${transactions.size} advance closures",
                    icon = Icons.Default.CurrencyRupee,
                    accentColor = Emerald500,
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = "Total Converted",
                    value = "$totalConversions",
                    subtitle = "${leads.size} total pipeline",
                    icon = Icons.Default.CheckCircle,
                    accentColor = Indigo600,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricCard(
                    title = "Active Calling Leads",
                    value = "$activeLeadsCount",
                    subtitle = "Needs follow-up/callbacks",
                    icon = Icons.Default.PhoneCallback,
                    accentColor = Sky500,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onNavigateToLeads() }
                )
                MetricCard(
                    title = "Confirmed Visits",
                    value = "$scheduledGuestsCount",
                    subtitle = "Campus / Centre walk-ins",
                    icon = Icons.Default.CalendarMonth,
                    accentColor = Amber500,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onNavigateToSchedule() }
                )
            }
        }

        // Celebration Gallery (Star Closers)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(Amber100),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = Amber600, modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Sales Celebration Gallery",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Recent successful closures & advance collections",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(celebrations) { celeb ->
                            CelebrationCard(celeb)
                        }
                    }
                }
            }
        }

        // Section: Visual Sales Leaderboard (Team-wise Performance)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = if (role.isSuperAdmin) "ALL TEAMS PERFORMANCE LEADERBOARD" else "YOUR TEAM PERFORMANCE & RANKING",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 1.sp
                )
                if (!role.isSuperAdmin) {
                    Text(
                        text = "🔒 Cross-Team Isolation Active: Individual financial targets and records of other teams are restricted by Supabase RLS.",
                        fontSize = 11.sp,
                        color = Indigo600,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        items(teamPerformances) { perf ->
            TeamLeaderboardCard(perf = perf)
        }
    }
}

@Composable
fun CelebrationCard(celeb: ClosureCelebration) {
    Card(
        modifier = Modifier
            .width(230.dp)
            .testTag("celebration_card_${celeb.id}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AvatarCircle(
                    initials = celeb.avatarInitials,
                    colorHex = celeb.avatarColorHex,
                    size = 38
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = celeb.agentName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = celeb.teamName,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Proof Image Preview if available
            if (!celeb.closureProofImageUrl.isNullOrBlank()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Indigo100)
                ) {
                    AsyncImage(
                        model = celeb.closureProofImageUrl,
                        contentDescription = "Closure Proof",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .background(Color.Black.copy(alpha = 0.65f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "Proof Attached",
                            color = Color.White,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
            } else {
                Divider(color = MaterialTheme.colorScheme.outlineVariant)
                Spacer(modifier = Modifier.height(8.dp))
            }

            Text(
                text = "Enrolled Candidate:",
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = celeb.candidateName,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatusBadge(
                    statusText = celeb.badgeTitle,
                    bgColor = Amber100,
                    textColor = Amber600
                )
                Text(
                    text = "₹${String.format("%,.0f", celeb.closedAmount)}",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 14.sp,
                    color = Emerald600
                )
            }
        }
    }
}


@Composable
fun TeamLeaderboardCard(perf: TeamPerformance) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("team_card_${perf.team.id}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(14.dp)
                            .clip(CircleShape)
                            .background(Color(perf.team.colorHex))
                    )
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = perf.team.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            if (perf.team.isArchived) {
                                StatusBadge(statusText = "ARCHIVED", bgColor = Slate200, textColor = Slate700)
                            }
                        }
                        Text(
                            text = "Leader: ${perf.team.leadName} • ${perf.memberCount} Distributors",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "₹${String.format("%,.0f", perf.totalSeniorityRevenue)}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = Emerald600
                    )
                    Text(
                        text = "${perf.totalConversions} Closures",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Progress Bar towards Target
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Target Progress (${String.format("%.1f", perf.targetAchievement)}%)",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Goal: ₹${String.format("%,.0f", perf.team.targetMonthlyRevenue)}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                LinearProgressIndicator(
                    progress = { (perf.targetAchievement / 100f).coerceIn(0.0, 1.0).toFloat() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = Color(perf.team.colorHex),
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Pipeline: ${perf.totalLeads} total leads",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Conversion Rate: ${String.format("%.1f", perf.conversionRate)}%",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Indigo600
                )
            }
        }
    }
}

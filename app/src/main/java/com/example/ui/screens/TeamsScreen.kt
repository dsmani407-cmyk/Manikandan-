package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.*
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun TeamsScreen(
    viewModel: CrmViewModel,
    modifier: Modifier = Modifier
) {
    val teams by viewModel.teams.collectAsState()
    val members by viewModel.members.collectAsState()
    val currentRole by viewModel.currentUserRole.collectAsState()

    var archiveFilter by remember { mutableStateOf("All") } // "All", "Active", "Archived"

    val displayedTeams = remember(teams, archiveFilter, currentRole) {
        val baseTeams = teams
        when (archiveFilter) {
            "Active" -> baseTeams.filter { !it.isArchived }
            "Archived" -> baseTeams.filter { it.isArchived }
            else -> baseTeams
        }
    }

    val currentUserId = if (currentRole is CurrentUserRole.Telecaller) (currentRole as CurrentUserRole.Telecaller).id else null
    val currentMember = members.find { it.id == currentUserId }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("teams_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (currentRole.isSuperAdmin) "Team & Distributor Management" else "Team & Distributor Directory",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${teams.count { !it.isArchived }} active teams • ${members.size} total distributors",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Button(
                    onClick = { viewModel.openModal(ActiveModalDialog.AddTeam) },
                    colors = ButtonDefaults.buttonColors(containerColor = Indigo600),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("btn_create_team")
                ) {
                    Icon(Icons.Default.GroupAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("+ New Team", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        // Distributor Own Profile & Credentials Card (Non-Admin View)
        if (!currentRole.isSuperAdmin && currentMember != null) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Slate900)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                AvatarCircle(
                                    initials = currentMember.avatarInitials,
                                    colorHex = currentMember.avatarColorHex,
                                    size = 40,
                                    avatarUrl = currentMember.avatarUrl
                                )
                                Column {
                                    Text(
                                        text = "${currentMember.name} (You)",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )
                                    Text(
                                        text = "${currentMember.role} • ${currentMember.phone}",
                                        color = Slate300,
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Indigo600)
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "MY ACCOUNT",
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Credentials & Session strip
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Slate800
                        ) {
                            Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Account: ${currentMember.role}", color = Amber300, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    Text("Status: Active Session", color = Emerald400, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Current Login: ${currentMember.currentLoginAt}", color = Slate300, fontSize = 10.sp)
                                    Text("Last Login: ${currentMember.lastLoginAt}", color = Slate400, fontSize = 10.sp)
                                }
                            }
                        }

                        Button(
                            onClick = { viewModel.openModal(ActiveModalDialog.EditMemberCredentials(currentMember)) },
                            colors = ButtonDefaults.buttonColors(containerColor = Indigo600),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Key, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Change My Password", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Archive / Active Filter Chips for Admin
        if (currentRole.isSuperAdmin) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("All", "Active", "Archived").forEach { f ->
                        FilterChip(
                            selected = archiveFilter == f,
                            onClick = { archiveFilter = f },
                            label = {
                                val count = when (f) {
                                    "Active" -> teams.count { !it.isArchived }
                                    "Archived" -> teams.count { it.isArchived }
                                    else -> teams.size
                                }
                                Text("$f ($count)")
                            }
                        )
                    }
                }
            }
        }

        // Teams Directory
        items(displayedTeams, key = { it.id }) { team ->
            val teamMemberList = members.filter { it.teamId == team.id }
            TeamDirectoryCard(
                team = team,
                members = teamMemberList,
                isSuperAdmin = currentRole.isSuperAdmin,
                currentUserId = currentUserId,
                onAddMember = { viewModel.openModal(ActiveModalDialog.AddMember(team.id)) },
                onToggleArchive = { viewModel.toggleArchiveTeam(team.id) },
                onDeleteTeam = { viewModel.deleteTeam(team.id) },
                onRemoveMember = { memberId -> viewModel.removeMember(memberId) },
                onEditCredentials = { member -> viewModel.openModal(ActiveModalDialog.EditMemberCredentials(member)) }
            )
        }
    }
}

@Composable
fun TeamDirectoryCard(
    team: Team,
    members: List<TeamMember>,
    isSuperAdmin: Boolean,
    currentUserId: String? = null,
    onAddMember: () -> Unit,
    onToggleArchive: () -> Unit,
    onDeleteTeam: () -> Unit,
    onRemoveMember: (String) -> Unit,
    onEditCredentials: (TeamMember) -> Unit = {}
) {
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = null,
                    tint = Rose500,
                    modifier = Modifier.size(28.dp)
                )
            },
            title = {
                Text(text = "Delete Team", fontWeight = FontWeight.Bold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Are you sure you want to delete \"${team.name}\"?")
                    Text(
                        "This will remove the team permanently and unassign all ${members.size} team member(s).",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 13.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteConfirmation = false
                        onDeleteTeam()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Rose600)
                ) {
                    Text("Confirm Delete", color = Color.White)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showDeleteConfirmation = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("team_directory_${team.id}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Team Header
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
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(Color(team.colorHex))
                    )
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = team.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            if (team.isArchived) {
                                StatusBadge(statusText = "ARCHIVED", bgColor = Slate200, textColor = Slate700)
                            }
                        }
                        Text(
                            text = "Team Lead: ${team.leadName} (${team.leadPhone})",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onToggleArchive,
                        modifier = Modifier.testTag("archive_team_${team.id}")
                    ) {
                        Icon(
                            imageVector = if (team.isArchived) Icons.Default.Unarchive else Icons.Default.Archive,
                            contentDescription = if (team.isArchived) "Unarchive Team" else "Archive Team",
                            tint = Indigo600
                        )
                    }
                    IconButton(
                        onClick = { showDeleteConfirmation = true },
                        modifier = Modifier.testTag("delete_team_${team.id}")
                    ) {
                        Icon(Icons.Default.DeleteOutline, contentDescription = "Delete Team", tint = Rose500)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Divider(color = Slate200)
            Spacer(modifier = Modifier.height(10.dp))

            // Member list header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "TEAM MEMBERS (${members.size})",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Slate500,
                    letterSpacing = 0.5.sp
                )

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    TextButton(
                        onClick = { showDeleteConfirmation = true },
                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Icon(Icons.Default.DeleteOutline, contentDescription = null, modifier = Modifier.size(13.dp), tint = Rose500)
                        Spacer(modifier = Modifier.width(3.dp))
                        Text("Delete Team", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Rose500)
                    }

                    TextButton(
                        onClick = onAddMember,
                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(13.dp), tint = Indigo600)
                        Spacer(modifier = Modifier.width(3.dp))
                        Text("+ Add Member", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Indigo600)
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Member Rows
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                members.forEach { member ->
                    val isSelf = (member.id == currentUserId)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(if (isSelf) Indigo50.copy(alpha = 0.5f) else Slate50, RoundedCornerShape(8.dp))
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            AvatarCircle(
                                initials = member.avatarInitials,
                                colorHex = member.avatarColorHex,
                                size = 32,
                                avatarUrl = member.avatarUrl
                            )
                            Column {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = if (isSelf) "${member.name} (You)" else member.name,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelf) Indigo900 else Slate900
                                    )
                                }

                                if (isSuperAdmin) {
                                    Text(
                                        text = "${member.role} • ${member.phone}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Indigo600
                                    )
                                    Text(
                                        text = "Last login: ${member.lastLoginAt}",
                                        fontSize = 10.sp,
                                        color = Slate500
                                    )
                                } else if (isSelf) {
                                    Text(
                                        text = "${member.role} • ${member.phone}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Indigo600
                                    )
                                    Text(
                                        text = "Last login: ${member.lastLoginAt}",
                                        fontSize = 10.sp,
                                        color = Slate400
                                    )
                                } else {
                                    Text(
                                        text = member.role,
                                        fontSize = 11.sp,
                                        color = Slate500
                                    )
                                }
                            }
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "₹${String.format("%,.0f", member.totalClosedAmount)}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Emerald600
                                )
                                Text(
                                    text = "${member.conversionsCount} sales",
                                    fontSize = 10.sp,
                                    color = Slate500
                                )
                            }

                            if (isSuperAdmin) {
                                IconButton(
                                    onClick = { onEditCredentials(member) },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Key,
                                        contentDescription = "Manage Login Credentials",
                                        tint = Amber600,
                                        modifier = Modifier.size(15.dp)
                                    )
                                }
                                IconButton(
                                    onClick = { onRemoveMember(member.id) },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Close,
                                        contentDescription = "Remove Member",
                                        tint = Slate400,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            } else if (isSelf) {
                                IconButton(
                                    onClick = { onEditCredentials(member) },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Key,
                                        contentDescription = "Change Password",
                                        tint = Indigo600,
                                        modifier = Modifier.size(15.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

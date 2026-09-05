package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.*
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun LeadsScreen(
    viewModel: CrmViewModel,
    modifier: Modifier = Modifier
) {
    val leads by viewModel.filteredLeads.collectAsState()
    val teams by viewModel.teams.collectAsState()
    val selectedTeamFilter by viewModel.selectedTeamFilter.collectAsState()
    val selectedStatusFilter by viewModel.selectedLeadStatusFilter.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val currentRole by viewModel.currentUserRole.collectAsState()
    val unreadLeadsCount by viewModel.unreadLeadsBadgeCount.collectAsState()
    val cacheInfo by viewModel.offlineCacheInfo.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("leads_screen")
    ) {
        // Top Action & Search Header
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Offline Room Cache Banner for distributors in the field
            OfflineCacheBanner(
                cacheInfo = cacheInfo,
                onSyncNow = { viewModel.syncAllToRoomCache() },
                onToggleOfflineSimulation = { viewModel.toggleOfflineModeSimulation() }
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Telecalling Pipeline",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${leads.size} leads currently in view",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Button(
                    onClick = { viewModel.openModal(ActiveModalDialog.AddLead) },
                    colors = ButtonDefaults.buttonColors(containerColor = Indigo600),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("btn_upload_calling_sheet")
                ) {
                    Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("+ Add Lead / Sheet", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.setSearchQuery(it) },
                placeholder = { Text("Search by candidate, phone, program, city...", fontSize = 13.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp)) },
                trailingIcon = {
                    if (searchQuery.isNotBlank()) {
                        IconButton(onClick = { viewModel.setSearchQuery("") }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear", modifier = Modifier.size(16.dp))
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("search_leads_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Slate50,
                    focusedContainerColor = Color.White
                )
            )

            // Team Filter Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                FilterChip(
                    selected = selectedTeamFilter == null,
                    onClick = { viewModel.setTeamFilter(null) },
                    label = { Text("All Teams (${teams.size})", fontSize = 12.sp) }
                )
                teams.forEach { team ->
                    FilterChip(
                        selected = selectedTeamFilter == team.id,
                        onClick = { viewModel.setTeamFilter(team.id) },
                        label = { Text(team.name, fontSize = 12.sp) }
                    )
                }
            }

            // Status Filter Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                FilterChip(
                    selected = selectedStatusFilter == null,
                    onClick = { viewModel.setLeadStatusFilter(null) },
                    label = { Text("All Statuses", fontSize = 11.sp) }
                )
                LeadStatus.values().forEach { status ->
                    FilterChip(
                        selected = selectedStatusFilter == status,
                        onClick = { viewModel.setLeadStatusFilter(status) },
                        label = { Text(status.label, fontSize = 11.sp) }
                    )
                }
            }
        }

        Divider(color = Slate200)

        // New Assigned Leads Alert Strip
        if (unreadLeadsCount > 0) {
            Surface(
                color = Sky50,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.openModal(ActiveModalDialog.ViewNotifications) }
                    .testTag("leads_new_assigned_alert_bar")
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(Sky500),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.PersonAdd,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                        Text(
                            text = "$unreadLeadsCount newly assigned lead${if (unreadLeadsCount > 1) "s" else ""} for your follow-up!",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Sky900
                        )
                    }

                    Text(
                        text = "View Alerts →",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Sky600
                    )
                }
            }
            Divider(color = Sky200)
        }

        // Lead List
        if (leads.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.SearchOff,
                        contentDescription = null,
                        tint = Slate400,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "No leads match current filter",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Slate600
                    )
                    Text(
                        text = "Try clearing filters or search query, or add new calling records.",
                        fontSize = 12.sp,
                        color = Slate400
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(leads, key = { it.id }) { lead ->
                    LeadCardItem(
                        lead = lead,
                        onStatusChange = { newStatus -> viewModel.updateLeadStatus(lead.id, newStatus) },
                        onTransitionToGuest = { viewModel.openModal(ActiveModalDialog.ConvertLead(lead)) }
                    )
                }
            }
        }
    }
}

@Composable
fun LeadCardItem(
    lead: Lead,
    onStatusChange: (LeadStatus) -> Unit,
    onTransitionToGuest: () -> Unit
) {
    var expandedStatusMenu by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("lead_card_${lead.id}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row: Name, City & Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = lead.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${lead.phone} • ${lead.city}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Box {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .clickable { expandedStatusMenu = true },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        LeadStatusBadge(status = lead.status)
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = Slate500)
                    }

                    DropdownMenu(
                        expanded = expandedStatusMenu,
                        onDismissRequest = { expandedStatusMenu = false }
                    ) {
                        LeadStatus.values().forEach { st ->
                            DropdownMenuItem(
                                text = { Text(st.label) },
                                onClick = {
                                    onStatusChange(st)
                                    expandedStatusMenu = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Program / Interest Badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = lead.courseOrProgram,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )
            }

            if (lead.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Notes: ${lead.notes}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            Divider(color = MaterialTheme.colorScheme.outlineVariant)
            Spacer(modifier = Modifier.height(8.dp))

            // Footer: Assigned Telecaller & One-Click "Confirmed Guest" Transition
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
                    Text(
                        text = lead.assignedTelecallerName,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                }

                // One-click action to transition lead into Confirmed Guest
                if (lead.status != LeadStatus.CONVERTED) {
                    Button(
                        onClick = onTransitionToGuest,
                        colors = ButtonDefaults.buttonColors(containerColor = Emerald600),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier
                            .height(34.dp)
                            .testTag("btn_confirm_guest_${lead.id}")
                    ) {
                        Icon(Icons.Default.EventAvailable, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Confirm Guest", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Emerald600, modifier = Modifier.size(16.dp))
                        Text("Confirmed Guest", fontSize = 12.sp, color = Emerald600, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

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
fun ScheduleScreen(
    viewModel: CrmViewModel,
    modifier: Modifier = Modifier
) {
    val guests by viewModel.filteredConfirmedGuests.collectAsState()
    var selectedFilter by remember { mutableStateOf<String>("All") } // "All", "Today", "Tomorrow", "Completed"
    var isTableView by remember { mutableStateOf(false) }

    val filteredGuests = remember(guests, selectedFilter) {
        when (selectedFilter) {
            "Today" -> guests.filter { it.visitDateTime.contains("Today", ignoreCase = true) }
            "Tomorrow" -> guests.filter { it.visitDateTime.contains("Tomorrow", ignoreCase = true) }
            "Completed" -> guests.filter { it.status == GuestStatus.COMPLETED }
            else -> guests
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("schedule_screen")
    ) {
        // Header
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Confirmed Guest Schedule",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Campus / Centre appointments & walk-ins",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Button(
                    onClick = { viewModel.openModal(ActiveModalDialog.AddConfirmedGuest) },
                    colors = ButtonDefaults.buttonColors(containerColor = Indigo600),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("btn_add_guest_appointment")
                ) {
                    Icon(Icons.Default.AddCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("+ Book Visit", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            // Quick Filters & View Switcher
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("All", "Today", "Tomorrow", "Completed").forEach { f ->
                        FilterChip(
                            selected = selectedFilter == f,
                            onClick = { selectedFilter = f },
                            label = { Text(f, fontSize = 12.sp) }
                        )
                    }
                }

                IconButton(
                    onClick = { isTableView = !isTableView },
                    modifier = Modifier.testTag("toggle_view_mode")
                ) {
                    Icon(
                        imageVector = if (isTableView) Icons.Default.ViewAgenda else Icons.Default.TableRows,
                        contentDescription = "Toggle View",
                        tint = Indigo600
                    )
                }
            }
        }

        Divider(color = Slate200)

        if (filteredGuests.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.EventBusy,
                        contentDescription = null,
                        tint = Slate400,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "No guest visits found",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Slate600
                    )
                    Text(
                        text = "Schedule a new guest appointment or check other date filters.",
                        fontSize = 12.sp,
                        color = Slate400
                    )
                }
            }
        } else if (isTableView) {
            // Table-Style Compact Grid
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Slate100, RoundedCornerShape(8.dp))
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("GUEST / TIME", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Slate600, modifier = Modifier.weight(1.2f))
                        Text("COUNSELLOR", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Slate600, modifier = Modifier.weight(1f))
                        Text("STATUS", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Slate600, modifier = Modifier.weight(0.8f))
                    }
                }

                items(filteredGuests, key = { it.id }) { guest ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = CardDefaults.outlinedCardBorder()
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(12.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1.2f)) {
                                Text(guest.guestName, fontWeight = FontWeight.Bold, fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Text(guest.visitDateTime, fontSize = 11.sp, color = Indigo600, fontWeight = FontWeight.Medium)
                                Text(guest.phone, fontSize = 10.sp, color = Slate400)
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(guest.assignedCounsellor, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Slate800, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Text("via ${guest.assignedTelecallerName}", fontSize = 10.sp, color = Slate500, maxLines = 1)
                            }
                            Box(modifier = Modifier.weight(0.8f)) {
                                GuestStatusBadge(status = guest.status)
                            }
                        }
                    }
                }
            }
        } else {
            // Rich Detailed Appointment Cards
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredGuests, key = { it.id }) { guest ->
                    GuestScheduleCard(
                        guest = guest,
                        onStatusChange = { newStatus -> viewModel.updateGuestStatus(guest.id, newStatus) },
                        onLogCounselling = {
                            viewModel.openModal(ActiveModalDialog.AddCounsellingLog)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun GuestScheduleCard(
    guest: ConfirmedGuest,
    onStatusChange: (GuestStatus) -> Unit,
    onLogCounselling: () -> Unit
) {
    var expandedStatusMenu by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("guest_card_${guest.id}"),
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
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Indigo50),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.PersonPinCircle, contentDescription = null, tint = Indigo600, modifier = Modifier.size(24.dp))
                    }
                    Column {
                        Text(
                            text = guest.guestName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${guest.phone} • ${guest.locationOrRoom}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Box {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .clickable { expandedStatusMenu = true },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        GuestStatusBadge(status = guest.status)
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = Slate500)
                    }

                    DropdownMenu(
                        expanded = expandedStatusMenu,
                        onDismissRequest = { expandedStatusMenu = false }
                    ) {
                        GuestStatus.values().forEach { st ->
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

            Spacer(modifier = Modifier.height(10.dp))

            // Time & Counsellor details
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Visit Schedule", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Medium)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Schedule, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(guest.visitDateTime, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text("Assigned Counsellor", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Medium)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.RecordVoiceOver, contentDescription = null, tint = Emerald600, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(guest.assignedCounsellor, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    }
                }
            }

            if (guest.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Notes: ${guest.notes}",
                    fontSize = 11.sp,
                    color = Slate600
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Distributor: ${guest.assignedTelecallerName}",
                    fontSize = 11.sp,
                    color = Slate500
                )

                OutlinedButton(
                    onClick = onLogCounselling,
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Icon(Icons.Default.EditNote, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Log Counselling", fontSize = 11.sp)
                }
            }
        }
    }
}

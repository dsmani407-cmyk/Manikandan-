package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.*
import com.example.ui.*
import com.example.ui.theme.*

@Composable
fun CrmModalsHost(
    viewModel: CrmViewModel
) {
    val activeModal by viewModel.activeModal.collectAsState()
    val teams by viewModel.teams.collectAsState()
    val members by viewModel.members.collectAsState()
    val currentRole by viewModel.currentUserRole.collectAsState()

    when (val modal = activeModal) {
        is ActiveModalDialog.None -> {}

        is ActiveModalDialog.AddLead -> {
            AddLeadModal(
                teams = teams,
                onDismiss = { viewModel.closeModal() },
                onConfirm = { name, phone, email, city, course, teamId, notes ->
                    viewModel.addLead(name, phone, email, city, course, teamId, notes)
                }
            )
        }

        is ActiveModalDialog.ConvertLead -> {
            ConvertLeadToGuestModal(
                lead = modal.lead,
                members = members,
                onDismiss = { viewModel.closeModal() },
                onConfirm = { visitTime, counsellor, location, notes ->
                    viewModel.convertLeadToConfirmedGuest(modal.lead.id, visitTime, counsellor, location, notes)
                }
            )
        }

        is ActiveModalDialog.AddConfirmedGuest -> {
            AddConfirmedGuestModal(
                members = members,
                onDismiss = { viewModel.closeModal() },
                onConfirm = { name, phone, telecaller, visitTime, counsellor, location, notes ->
                    viewModel.addConfirmedGuest(name, phone, telecaller, visitTime, counsellor, location, notes)
                }
            )
        }

        is ActiveModalDialog.AddDailyTask -> {
            AddDailyTaskModal(
                members = members,
                currentRole = currentRole,
                onDismiss = { viewModel.closeModal() },
                onConfirm = { title, description, memberId, priority, dueTime, isSelfCreated ->
                    viewModel.addTask(title, memberId, priority, dueTime, description, isSelfCreated)
                }
            )
        }

        is ActiveModalDialog.EditDailyTask -> {
            EditDailyTaskModal(
                task = modal.task,
                onDismiss = { viewModel.closeModal() },
                onConfirm = { updatedTitle, updatedDesc, priority, dueTime, status, remarks ->
                    viewModel.updateTask(modal.task.id, updatedTitle, updatedDesc, priority, dueTime, status, remarks)
                },
                onDelete = {
                    viewModel.deleteTask(modal.task.id)
                }
            )
        }

        is ActiveModalDialog.AddCounsellingLog -> {
            AddCounsellingLogModal(
                members = members,
                currentRole = currentRole,
                onDismiss = { viewModel.closeModal() },
                onConfirm = { counsellor, candidate, phone, outcome, notes, recordedBy ->
                    viewModel.addCounsellingLog(counsellor, candidate, phone, outcome, notes, recordedBy)
                }
            )
        }

        is ActiveModalDialog.AddSalesTransaction -> {
            AddSalesTransactionModal(
                teams = teams,
                members = members,
                onDismiss = { viewModel.closeModal() },
                onConfirm = { candidate, phone, amount, mode, ref, teamId, agent ->
                    viewModel.addSalesTransaction(candidate, phone, amount, mode, ref, teamId, agent)
                }
            )
        }

        is ActiveModalDialog.AddTeam -> {
            AddTeamModal(
                onDismiss = { viewModel.closeModal() },
                onConfirm = { name, leadName, email, phone, target ->
                    viewModel.addTeam(name, leadName, email, phone, target)
                }
            )
        }

        is ActiveModalDialog.AddMember -> {
            AddMemberModal(
                teams = teams,
                preselectedTeamId = modal.preselectedTeamId,
                onDismiss = { viewModel.closeModal() },
                onConfirm = { name, teamId, role, email, phone ->
                    viewModel.addMember(name, teamId, role, email, phone)
                }
            )
        }

        is ActiveModalDialog.ViewClosureProof -> {
            ViewClosureProofModal(
                transaction = modal.transaction,
                onDismiss = { viewModel.closeModal() }
            )
        }

        is ActiveModalDialog.ViewSecurityPolicies -> {
            ViewSecurityPoliciesModal(
                onDismiss = { viewModel.closeModal() }
            )
        }

        is ActiveModalDialog.DailyTaskClosing -> {
            val dailyTasks by viewModel.dailyTasks.collectAsState()
            val closingRecords by viewModel.dailyClosingRecords.collectAsState()
            DailyTaskClosingModal(
                tasks = dailyTasks,
                currentRole = currentRole,
                closingRecords = closingRecords,
                onDismiss = { viewModel.closeModal() },
                onConfirm = { remarks ->
                    viewModel.closeDailyTasks(remarks)
                }
            )
        }

        is ActiveModalDialog.EditMemberCredentials -> {
            EditMemberCredentialsModal(
                member = modal.member,
                isSuperAdmin = currentRole.isSuperAdmin,
                onDismiss = { viewModel.closeModal() },
                onConfirm = { newUserId, newPassword ->
                    viewModel.updateMemberCredentials(modal.member.id, newUserId, newPassword)
                }
            )
        }

        is ActiveModalDialog.ViewNotifications -> {
            val notifications by viewModel.notifications.collectAsState()
            NotificationsCenterModal(
                notifications = notifications,
                onDismiss = { viewModel.closeModal() },
                onMarkAsRead = { viewModel.markNotificationAsRead(it) },
                onMarkAllAsRead = { viewModel.markAllNotificationsAsRead() },
                onClearAll = { viewModel.clearAllNotifications() }
            )
        }

        is ActiveModalDialog.LeadDetails -> {}
    }
}

@Composable
fun AddLeadModal(
    teams: List<Team>,
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, String, String, String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("Chennai") }
    var course by remember { mutableStateOf("Executive PG - AI & ML") }
    var selectedTeamId by remember { mutableStateOf(teams.firstOrNull()?.id ?: "") }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Add Lead to Telecalling Sheet", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Candidate Full Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Contact Phone (+91)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email Address") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = city,
                    onValueChange = { city = it },
                    label = { Text("City / Location") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = course,
                    onValueChange = { course = it },
                    label = { Text("Course / Program of Interest") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Calling / Background Notes") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank() && phone.isNotBlank()) {
                        onConfirm(name, phone, email, city, course, selectedTeamId, notes)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Indigo600)
            ) {
                Text("Save Lead")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun ConvertLeadToGuestModal(
    lead: Lead,
    members: List<TeamMember>,
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, String) -> Unit
) {
    var visitDateTime by remember { mutableStateOf("Today, 04:00 PM") }
    var selectedCounsellor by remember { mutableStateOf(members.firstOrNull()?.name ?: "Senior Counsellor") }
    var location by remember { mutableStateOf("Executive Room 1") }
    var notes by remember { mutableStateOf(lead.notes) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Transition Lead to Confirmed Guest", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Emerald50),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(lead.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Emerald600)
                        Text("${lead.phone} • ${lead.courseOrProgram}", fontSize = 11.sp, color = Slate600)
                    }
                }

                OutlinedTextField(
                    value = visitDateTime,
                    onValueChange = { visitDateTime = it },
                    label = { Text("Visit Date & Time") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = selectedCounsellor,
                    onValueChange = { selectedCounsellor = it },
                    label = { Text("Assigned Counsellor") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Meeting Room / Centre Location") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Session Objectives / Notes") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(visitDateTime, selectedCounsellor, location, notes) },
                colors = ButtonDefaults.buttonColors(containerColor = Emerald600)
            ) {
                Text("Confirm Appointment")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun AddConfirmedGuestModal(
    members: List<TeamMember>,
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, String, String, String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var telecaller by remember { mutableStateOf(members.firstOrNull()?.name ?: "Telecaller") }
    var visitDateTime by remember { mutableStateOf("Tomorrow, 11:30 AM") }
    var counsellor by remember { mutableStateOf("Rajesh Kumar") }
    var location by remember { mutableStateOf("Centre Hall A") }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Schedule Walk-in Guest Appointment", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Guest Name") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Contact Phone") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = telecaller, onValueChange = { telecaller = it }, label = { Text("Assigned Distributor") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = visitDateTime, onValueChange = { visitDateTime = it }, label = { Text("Visit Date & Time") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = counsellor, onValueChange = { counsellor = it }, label = { Text("Assigned Counsellor") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = location, onValueChange = { location = it }, label = { Text("Location / Room") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text("Notes") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank() && phone.isNotBlank()) {
                        onConfirm(name, phone, telecaller, visitDateTime, counsellor, location, notes)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Indigo600)
            ) {
                Text("Schedule Visit")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun AddDailyTaskModal(
    members: List<TeamMember>,
    currentRole: CurrentUserRole,
    onDismiss: () -> Unit,
    onConfirm: (title: String, description: String, memberId: String, priority: String, dueTime: String, isSelfCreated: Boolean) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedMemberId by remember {
        mutableStateOf(
            when (currentRole) {
                is CurrentUserRole.Telecaller -> currentRole.id
                is CurrentUserRole.SuperAdmin -> members.firstOrNull()?.id ?: ""
            }
        )
    }
    var isSelfAssigned by remember {
        mutableStateOf(currentRole is CurrentUserRole.Telecaller)
    }
    var priority by remember { mutableStateOf("High") }
    var dueTime by remember { mutableStateOf("05:00 PM") }

    val quickIdeas = listOf(
        "📞 Call 35 Leads Quota",
        "🤝 5 Confirmed Guest Follow-ups",
        "💳 Collect Seniority Advance Receipt",
        "📋 EOD Daily Closing & Handover",
        "🎯 Afternoon Callback Pipeline"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(
                    modifier = Modifier.size(32.dp).clip(CircleShape).background(Indigo100),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.EditNote, contentDescription = null, tint = Indigo600, modifier = Modifier.size(20.dp))
                }
                Column {
                    Text("Create Daily Task • Own Writing", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Slate900)
                    Text("Self-typing method for individual workflow", fontSize = 11.sp, color = Slate500)
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Quick Suggestion Chips for Fast Self-Typing
                Text("Quick Ideas (Tap to fill, then edit with your own writing):", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Slate600)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    quickIdeas.forEach { idea ->
                        SuggestionChip(
                            onClick = {
                                title = idea.substringAfter(" ")
                            },
                            label = { Text(idea, fontSize = 10.sp) }
                        )
                    }
                }

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Task Title / Goal (Own Writing) *") },
                    placeholder = { Text("e.g. Call 40 fresh leads from Madurai batch") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_task_title")
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Self Notes / Action Plan (Own Writing)") },
                    placeholder = { Text("Write personal action items, targets, notes, or checklist...") },
                    minLines = 3,
                    maxLines = 5,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_task_notes")
                )

                // Assignment Type
                if (currentRole is CurrentUserRole.SuperAdmin) {
                    Text("Assign Task To:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = isSelfAssigned,
                            onClick = { isSelfAssigned = true },
                            label = { Text("Self (Admin Task)") }
                        )
                        FilterChip(
                            selected = !isSelfAssigned,
                            onClick = { isSelfAssigned = false },
                            label = { Text("Distributor / Team Member") }
                        )
                    }

                    if (!isSelfAssigned) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            members.forEach { m ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (selectedMemberId == m.id) Indigo50 else Slate50)
                                        .clickable { selectedMemberId = m.id }
                                        .padding(8.dp)
                                ) {
                                    RadioButton(
                                        selected = selectedMemberId == m.id,
                                        onClick = { selectedMemberId = m.id }
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(m.name, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                }
                            }
                        }
                    }
                } else {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Indigo50),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = Indigo600, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Created for Self (${currentRole.displayName})", fontSize = 12.sp, color = Indigo800, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = dueTime,
                        onValueChange = { dueTime = it },
                        label = { Text("Due Time") },
                        modifier = Modifier.weight(1f)
                    )

                    Column(modifier = Modifier.weight(1f)) {
                        Text("Priority:", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            listOf("Normal", "High", "Urgent").forEach { p ->
                                FilterChip(
                                    selected = priority == p,
                                    onClick = { priority = p },
                                    label = { Text(p, fontSize = 10.sp) }
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        val finalMemberId = if (isSelfAssigned) {
                            when (currentRole) {
                                is CurrentUserRole.SuperAdmin -> members.firstOrNull()?.id ?: "mem-admin"
                                is CurrentUserRole.Telecaller -> currentRole.id
                            }
                        } else selectedMemberId
                        onConfirm(title.trim(), description.trim(), finalMemberId, priority, dueTime, isSelfAssigned)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Indigo600),
                modifier = Modifier.testTag("btn_confirm_add_task")
            ) {
                Text("Create Task")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun EditDailyTaskModal(
    task: DailyTask,
    onDismiss: () -> Unit,
    onConfirm: (title: String, description: String, priority: String, dueTime: String, status: TaskStatus, remarks: String?) -> Unit,
    onDelete: () -> Unit
) {
    var title by remember { mutableStateOf(task.title) }
    var description by remember { mutableStateOf(task.description) }
    var priority by remember { mutableStateOf(task.priority) }
    var dueTime by remember { mutableStateOf(task.dueTime) }
    var status by remember { mutableStateOf(task.status) }
    var remarks by remember { mutableStateOf(task.closingRemarks ?: "") }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete Task?", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to remove this task from your daily checklist?") },
            confirmButton = {
                Button(
                    onClick = onDelete,
                    colors = ButtonDefaults.buttonColors(containerColor = Rose600)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) { Text("Cancel") }
            }
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier.size(32.dp).clip(CircleShape).background(Emerald50),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null, tint = Emerald600, modifier = Modifier.size(18.dp))
                    }
                    Column {
                        Text("Update Own Task", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Slate900)
                        Text("Edit writing, plan & status individually", fontSize = 11.sp, color = Slate500)
                    }
                }

                IconButton(
                    onClick = { showDeleteConfirm = true }
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete task", tint = Rose600)
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Status Switcher (Thumbs-Up / Thumbs-Down / In Progress)
                Text("Task Status Update:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Slate800)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Button(
                        onClick = { status = TaskStatus.COMPLETED },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (status == TaskStatus.COMPLETED) Emerald600 else Slate100,
                            contentColor = if (status == TaskStatus.COMPLETED) Color.White else Slate700
                        ),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Filled.ThumbUp, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Done 👍", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { status = TaskStatus.PENDING },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (status == TaskStatus.PENDING) Rose600 else Slate100,
                            contentColor = if (status == TaskStatus.PENDING) Color.White else Slate700
                        ),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Filled.ThumbDown, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Pending 👎", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { status = TaskStatus.IN_PROGRESS },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (status == TaskStatus.IN_PROGRESS) Amber500 else Slate100,
                            contentColor = if (status == TaskStatus.IN_PROGRESS) Slate900 else Slate700
                        ),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("In Progress", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Task Title / Objective (Own Writing) *") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("edit_task_title")
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Personal Notes & Plan (Own Writing)") },
                    placeholder = { Text("Own thoughts, strategy, checklist...") },
                    minLines = 3,
                    maxLines = 5,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("edit_task_notes")
                )

                OutlinedTextField(
                    value = remarks,
                    onValueChange = { remarks = it },
                    label = { Text("Self Completion / Follow-up Remarks") },
                    placeholder = { Text("e.g. Completed 35 calls, 3 booked for counselling") },
                    minLines = 2,
                    maxLines = 4,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("edit_task_remarks")
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = dueTime,
                        onValueChange = { dueTime = it },
                        label = { Text("Due Time") },
                        modifier = Modifier.weight(1f)
                    )

                    Column(modifier = Modifier.weight(1f)) {
                        Text("Priority:", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            listOf("Normal", "High", "Urgent").forEach { p ->
                                FilterChip(
                                    selected = priority == p,
                                    onClick = { priority = p },
                                    label = { Text(p, fontSize = 10.sp) }
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onConfirm(title.trim(), description.trim(), priority, dueTime, status, remarks.ifBlank { null })
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Indigo600),
                modifier = Modifier.testTag("btn_confirm_edit_task")
            ) {
                Text("Save & Update")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun AddCounsellingLogModal(
    members: List<TeamMember>,
    currentRole: CurrentUserRole,
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, CounsellingOutcome, String, String) -> Unit
) {
    var counsellor by remember { mutableStateOf("Rajesh Kumar") }
    var candidate by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var outcome by remember { mutableStateOf(CounsellingOutcome.INTERESTED) }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text("Log Counselling Session", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("Councelling Trainer Name & Councelling attend Name", fontSize = 11.sp, color = Indigo600)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = counsellor,
                    onValueChange = { counsellor = it },
                    label = { Text("Councelling Trainer Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = candidate,
                    onValueChange = { candidate = it },
                    label = { Text("Councelling attend Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Candidate Phone") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Counselling Outcome:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    CounsellingOutcome.values().forEach { out ->
                        FilterChip(
                            selected = outcome == out,
                            onClick = { outcome = out },
                            label = { Text(out.label, fontSize = 11.sp) }
                        )
                    }
                }

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Key Discussion & Feedback Notes") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (candidate.isNotBlank()) {
                        onConfirm(counsellor, candidate, phone, outcome, notes, currentRole.displayName)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Indigo600)
            ) {
                Text("Save Entry")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun AddSalesTransactionModal(
    teams: List<Team>,
    members: List<TeamMember>,
    onDismiss: () -> Unit,
    onConfirm: (String, String, Double, String, String, String, String) -> Unit
) {
    var candidate by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf("25000") }
    var paymentMode by remember { mutableStateOf("UPI (Google Pay / PhonePe)") }
    var ref by remember { mutableStateOf("UPI-${System.currentTimeMillis().toString().takeLast(6)}") }
    var selectedTeamId by remember { mutableStateOf(teams.firstOrNull()?.id ?: "") }
    var agent by remember { mutableStateOf(members.firstOrNull()?.name ?: "Distributor") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Record Seniority / Advance Amount", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = candidate,
                    onValueChange = { candidate = it },
                    label = { Text("Candidate Full Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Candidate Phone") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text("Seniority / Advance Amount (₹)") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = paymentMode,
                    onValueChange = { paymentMode = it },
                    label = { Text("Payment Mode (UPI, Net Banking, Cash, Card)") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = ref,
                    onValueChange = { ref = it },
                    label = { Text("Receipt / Transaction Reference") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = agent,
                    onValueChange = { agent = it },
                    label = { Text("Closing Distributor Name") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amt = amountText.toDoubleOrNull() ?: 20000.0
                    if (candidate.isNotBlank()) {
                        onConfirm(candidate, phone, amt, paymentMode, ref, selectedTeamId, agent)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Emerald600)
            ) {
                Text("Record Closure")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun AddTeamModal(
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, String, Double) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var leadName by remember { mutableStateOf("") }
    var leadEmail by remember { mutableStateOf("") }
    var leadPhone by remember { mutableStateOf("") }
    var targetText by remember { mutableStateOf("500000") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Create New Team", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Team Name (e.g. Delta Kings)") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = leadName, onValueChange = { leadName = it }, label = { Text("Team Lead Name") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = leadEmail, onValueChange = { leadEmail = it }, label = { Text("Lead Email") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = leadPhone, onValueChange = { leadPhone = it }, label = { Text("Lead Phone") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = targetText, onValueChange = { targetText = it }, label = { Text("Monthly Revenue Target (₹)") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val target = targetText.toDoubleOrNull() ?: 500000.0
                    if (name.isNotBlank() && leadName.isNotBlank()) {
                        onConfirm(name, leadName, leadEmail, leadPhone, target)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Indigo600)
            ) {
                Text("Create Team")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun AddMemberModal(
    teams: List<Team>,
    preselectedTeamId: String?,
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var selectedTeamId by remember { mutableStateOf(preselectedTeamId ?: teams.firstOrNull()?.id ?: "") }
    var role by remember { mutableStateOf("Distributor / Leader") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Add Member to Team", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Full Name") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = role, onValueChange = { role = it }, label = { Text("Role / Designation") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Work Email") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Contact Phone") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onConfirm(name, selectedTeamId, role, email, phone)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Indigo600)
            ) {
                Text("Add Member")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun ViewClosureProofModal(
    transaction: SalesTransaction,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = Indigo600)
                Text(
                    text = "Sales Closure Proof",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (!transaction.closureProofImageUrl.isNullOrBlank()) {
                    Card(
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = Slate100),
                        border = CardDefaults.outlinedCardBorder()
                    ) {
                        AsyncImage(
                            model = transaction.closureProofImageUrl,
                            contentDescription = "Closure Proof Receipt",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 160.dp, max = 280.dp)
                        )
                    }
                } else {
                    Card(
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = Slate100),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.ImageNotSupported, contentDescription = null, tint = Slate400, modifier = Modifier.size(36.dp))
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("No proof photo uploaded", color = Slate500, fontSize = 12.sp)
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Candidate", fontSize = 11.sp, color = Slate500)
                        Text(transaction.candidateName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text(transaction.candidatePhone, fontSize = 11.sp, color = Slate600)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Advance Seniority", fontSize = 11.sp, color = Slate500)
                        Text("₹${String.format("%,.0f", transaction.seniorityAmount)}", fontWeight = FontWeight.ExtraBold, fontSize = 15.sp, color = Emerald600)
                        Text(transaction.paymentMode, fontSize = 11.sp, color = Slate600)
                    }
                }

                Divider(color = Slate200)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Receipt #", fontSize = 11.sp, color = Slate500)
                        Text(transaction.receiptNumber, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                    }
                    Column {
                        Text("Reference ID", fontSize = 11.sp, color = Slate500)
                        Text(transaction.transactionRef, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Team & Distributor", fontSize = 11.sp, color = Slate500)
                        Text("${transaction.teamName} • ${transaction.agentName}", fontSize = 11.sp, fontWeight = FontWeight.Medium)
                    }
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Indigo50),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.CloudQueue, contentDescription = null, tint = Indigo600, modifier = Modifier.size(16.dp))
                        Text(
                            text = "Supabase Storage: bucket '${transaction.storageBucket}'",
                            fontSize = 11.sp,
                            color = Indigo700,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = Indigo600)
            ) {
                Text("Close")
            }
        }
    )
}

@Composable
fun ViewSecurityPoliciesModal(
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.Shield, contentDescription = null, tint = Amber500)
                Text(
                    text = "Supabase Row-Level Security (RLS)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Data isolation between teams is guaranteed at the PostgreSQL database engine layer using Row Level Security (RLS). Team members can never query or mutate another team's data.",
                    fontSize = 12.sp,
                    color = Slate600
                )

                Text(
                    text = "1. SALES CLOSINGS (sales_closings)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = Indigo600
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Slate900)
                        .padding(10.dp)
                ) {
                    Text(
                        text = """
ALTER TABLE sales_closings ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Team Wise Sales Access Policy" 
ON sales_closings
FOR SELECT TO authenticated
USING (
    EXISTS (
        SELECT 1 FROM app_users 
        WHERE id = auth.uid() AND role = 'ADMIN'
    )
    OR
    team_id = (
        SELECT team_id FROM app_users 
        WHERE id = auth.uid()
    )
);
                        """.trimIndent(),
                        color = Emerald400Text(),
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Text(
                    text = "2. TELECALLING LEADS (telecalling_leads)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = Indigo600
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Slate900)
                        .padding(10.dp)
                ) {
                    Text(
                        text = """
ALTER TABLE telecalling_leads ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Team Wise Leads Access Policy" 
ON telecalling_leads
FOR ALL TO authenticated
USING (
    EXISTS (
        SELECT 1 FROM app_users 
        WHERE id = auth.uid() AND role = 'ADMIN'
    )
    OR
    team_id = (
        SELECT team_id FROM app_users 
        WHERE id = auth.uid()
    )
);
                        """.trimIndent(),
                        color = Emerald400Text(),
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Text(
                    text = "3. COUNSELLING SESSIONS (counselling_sessions)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = Indigo600
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Slate900)
                        .padding(10.dp)
                ) {
                    Text(
                        text = """
ALTER TABLE counselling_sessions ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Team Wise Counselling Access Policy" 
ON counselling_sessions
FOR ALL TO authenticated
USING (
    EXISTS (
        SELECT 1 FROM app_users 
        WHERE id = auth.uid() AND role = 'ADMIN'
    )
    OR
    team_id = (
        SELECT team_id FROM app_users 
        WHERE id = auth.uid()
    )
);
                        """.trimIndent(),
                        color = Emerald400Text(),
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Text(
                    text = "4. STORAGE BUCKET (sales-proofs)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = Indigo600
                )
                Card(
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Slate100),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("• Bucket name: sales-proofs", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text("• Uploaded receipts are encrypted and scoped to transaction references", fontSize = 11.sp, color = Slate600)
                        Text("• Read access restricted to team members belonging to the same team_id", fontSize = 11.sp, color = Slate600)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = Slate900)
            ) {
                Text("Got It")
            }
        }
    )
}

@Composable
private fun Emerald400Text(): Color = Color(0xFF34D399)

@Composable
fun DailyTaskClosingModal(
    tasks: List<DailyTask>,
    currentRole: CurrentUserRole,
    closingRecords: List<DailyTaskClosingRecord>,
    onDismiss: () -> Unit,
    onConfirm: (remarks: String) -> Unit
) {
    var remarks by remember { mutableStateOf("End-of-day checklist audit completed. All priorities reviewed.") }
    val completedCount = tasks.count { it.status == TaskStatus.COMPLETED }
    val incompleteCount = tasks.count { it.status != TaskStatus.COMPLETED }
    val completionPercent = if (tasks.isNotEmpty()) (completedCount * 100) / tasks.size else 0

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Indigo100),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.LockClock,
                        contentDescription = null,
                        tint = Indigo600,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Column {
                    Text(
                        text = "Daily Task Closing",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = Slate900
                    )
                    Text(
                        text = "End-of-day task audit & handover",
                        fontSize = 11.sp,
                        color = Slate500
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Summary Metrics Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = Slate100)
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Today's Completion: $completionPercent%",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Slate800
                            )
                            Text(
                                text = "${tasks.size} Total Tasks",
                                fontSize = 11.sp,
                                color = Slate500
                            )
                        }

                        LinearProgressIndicator(
                            progress = { if (tasks.isNotEmpty()) completedCount.toFloat() / tasks.size.toFloat() else 0f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = Emerald500,
                            trackColor = Slate200
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Completed (Thumbs-up)
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    Icons.Filled.ThumbUp,
                                    contentDescription = "Completed",
                                    tint = Emerald600,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "$completedCount Completed",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Emerald700
                                )
                            }

                            // Incomplete (Thumbs-down)
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    Icons.Filled.ThumbDown,
                                    contentDescription = "Incomplete",
                                    tint = Rose600,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "$incompleteCount Incomplete",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Rose700
                                )
                            }
                        }
                    }
                }

                // Breakdown list
                Text(
                    text = "TASK AUDIT CHECKLIST (${tasks.size})",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Slate600,
                    letterSpacing = 0.5.sp
                )

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    tasks.forEach { task ->
                        val isDone = task.status == TaskStatus.COMPLETED
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isDone) Emerald50.copy(alpha = 0.5f) else Rose50.copy(alpha = 0.5f))
                                .padding(horizontal = 10.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = if (isDone) Icons.Filled.ThumbUp else Icons.Filled.ThumbDown,
                                    contentDescription = null,
                                    tint = if (isDone) Emerald600 else Rose600,
                                    modifier = Modifier.size(16.dp)
                                )
                                Column {
                                    Text(
                                        text = task.title,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Slate900
                                    )
                                    Text(
                                        text = "${task.assignedToMemberName} • Due: ${task.dueTime}",
                                        fontSize = 10.sp,
                                        color = Slate500
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(if (isDone) Emerald100 else Rose100)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = if (isDone) "Done 👍" else "Incomplete 👎",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDone) Emerald800 else Rose800
                                )
                            }
                        }
                    }
                }

                // Remarks input
                OutlinedTextField(
                    value = remarks,
                    onValueChange = { remarks = it },
                    label = { Text("Closing Remarks & Handover Notes") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_task_closing_remarks"),
                    minLines = 2,
                    shape = RoundedCornerShape(8.dp)
                )

                // Audit signatory
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.padding(top = 2.dp)
                ) {
                    Icon(
                        Icons.Default.VerifiedUser,
                        contentDescription = null,
                        tint = Indigo600,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "Audited & closed by: ${currentRole.displayName} (${currentRole.loginId})",
                        fontSize = 10.sp,
                        color = Slate600
                    )
                }

                // Past closing history
                if (closingRecords.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "PREVIOUS CLOSING AUDIT HISTORY",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate500
                    )
                    closingRecords.take(3).forEach { record ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(Slate50)
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "${record.date} at ${record.closedAt}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Slate800
                                )
                                Text(
                                    text = "Closed by ${record.closedBy} • ${record.closingRemarks}",
                                    fontSize = 10.sp,
                                    color = Slate500,
                                    maxLines = 1
                                )
                            }
                            Text(
                                text = "👍 ${record.completedCount} / 👎 ${record.incompleteCount}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Emerald700
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(remarks)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Indigo600),
                modifier = Modifier.testTag("btn_confirm_task_closing")
            ) {
                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Confirm Task Closing")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun EditMemberCredentialsModal(
    member: TeamMember,
    isSuperAdmin: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (newUserId: String, newPass: String) -> Unit
) {
    var userId by remember { mutableStateOf(member.loginUserId) }
    var password by remember { mutableStateOf(member.loginPassword) }
    var showPassword by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(if (isSuperAdmin) Amber100 else Indigo100),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Key,
                        contentDescription = null,
                        tint = if (isSuperAdmin) Amber700 else Indigo600,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Column {
                    Text(
                        text = if (isSuperAdmin) "Admin Credentials Management" else "Change My Password",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = if (isSuperAdmin) "Super Admin clearance for ${member.name}" else "Confidential login for ${member.name}",
                        fontSize = 11.sp,
                        color = Slate500
                    )
                }
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // Confidentiality Callout
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isSuperAdmin) Amber50 else Slate100
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = if (isSuperAdmin) Icons.Default.AdminPanelSettings else Icons.Default.Lock,
                            contentDescription = null,
                            tint = if (isSuperAdmin) Amber700 else Indigo600,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = if (isSuperAdmin) {
                                "Admin Mode: You have administrative access to configure login IDs and reset passwords for any team member."
                            } else {
                                "🔒 Private: Other team members cannot view your login password or credentials."
                            },
                            fontSize = 11.sp,
                            color = Slate700,
                            lineHeight = 15.sp
                        )
                    }
                }

                OutlinedTextField(
                    value = userId,
                    onValueChange = { if (isSuperAdmin) userId = it },
                    enabled = isSuperAdmin,
                    label = { Text(if (isSuperAdmin) "App User ID / Login ID" else "My User ID (Fixed by Admin)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    singleLine = true,
                    trailingIcon = {
                        IconButton(onClick = { showPassword = !showPassword }) {
                            Icon(
                                if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = null
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (userId.isNotBlank() && password.isNotBlank()) {
                        onConfirm(userId.trim(), password.trim())
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = if (isSuperAdmin) Amber600 else Indigo600)
            ) {
                Text(if (isSuperAdmin) "Save Admin Changes" else "Update Password")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun NotificationsCenterModal(
    notifications: List<AppNotification>,
    onDismiss: () -> Unit,
    onMarkAsRead: (String) -> Unit,
    onMarkAllAsRead: () -> Unit,
    onClearAll: () -> Unit
) {
    val unreadCount = notifications.count { !it.isRead }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.testTag("dialog_notifications_center"),
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(Indigo100),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Notifications,
                            contentDescription = null,
                            tint = Indigo600,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Column {
                        Text("Notification Center", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text(
                            text = if (unreadCount > 0) "$unreadCount unread alerts" else "All alerts caught up",
                            fontSize = 11.sp,
                            color = if (unreadCount > 0) Rose600 else Slate500,
                            fontWeight = if (unreadCount > 0) FontWeight.SemiBold else FontWeight.Normal
                        )
                    }
                }

                if (unreadCount > 0) {
                    TextButton(
                        onClick = onMarkAllAsRead,
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        modifier = Modifier.testTag("btn_mark_all_read")
                    ) {
                        Text("Mark all read", fontSize = 12.sp, color = Indigo600, fontWeight = FontWeight.Bold)
                    }
                }
            }
        },
        text = {
            if (notifications.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.NotificationsNone,
                            contentDescription = null,
                            tint = Slate400,
                            modifier = Modifier.size(44.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "No notifications yet",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            color = Slate600
                        )
                        Text(
                            "New lead assignments and follow-up tasks will appear here.",
                            fontSize = 11.sp,
                            color = Slate400,
                            modifier = Modifier.padding(horizontal = 16.dp),
                            lineHeight = 16.sp
                        )
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 380.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    notifications.forEach { notif ->
                        val isLead = notif.type == NotificationType.NEW_LEAD
                        val bgCol = if (notif.isRead) Slate50 else if (isLead) Sky50 else Amber50
                        val borderCol = if (notif.isRead) Slate200 else if (isLead) Sky500.copy(alpha = 0.5f) else Amber500.copy(alpha = 0.5f)

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    if (!notif.isRead) onMarkAsRead(notif.id)
                                }
                                .testTag("notification_card_${notif.id}"),
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = bgCol),
                            border = androidx.compose.foundation.BorderStroke(1.dp, borderCol)
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(12.dp)
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(if (isLead) Sky500 else Amber500),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (isLead) Icons.Default.PersonAdd else Icons.Default.Schedule,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Text(
                                                text = notif.title,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp,
                                                color = Slate900
                                            )
                                            if (!notif.isRead) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(8.dp)
                                                        .clip(CircleShape)
                                                        .background(Rose500)
                                                )
                                            }
                                        }

                                        Text(
                                            text = notif.timestamp,
                                            fontSize = 10.sp,
                                            color = Slate400
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(3.dp))
                                    Text(
                                        text = notif.message,
                                        fontSize = 12.sp,
                                        color = Slate600,
                                        lineHeight = 16.sp
                                    )

                                    Spacer(modifier = Modifier.height(6.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(if (isLead) Indigo50 else Emerald50)
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = if (isLead) "Telecalling Sheet" else "Task Tracker",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isLead) Indigo600 else Emerald600
                                            )
                                        }

                                        if (!notif.isRead) {
                                            Text(
                                                text = "Tap to mark read",
                                                fontSize = 10.sp,
                                                color = Indigo600,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        } else {
                                            Text(
                                                text = "Read ✓",
                                                fontSize = 10.sp,
                                                color = Slate400
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (notifications.isNotEmpty()) {
                    TextButton(
                        onClick = onClearAll,
                        colors = ButtonDefaults.textButtonColors(contentColor = Slate500)
                    ) {
                        Text("Clear All", fontSize = 12.sp)
                    }
                }
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = Indigo600)
                ) {
                    Text("Close")
                }
            }
        }
    )
}



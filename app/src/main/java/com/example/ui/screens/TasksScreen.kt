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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.ActiveModalDialog
import com.example.ui.CrmViewModel
import com.example.ui.StaffAuditProgress
import com.example.ui.components.AvatarCircle
import com.example.ui.components.TaskStatusBadge
import com.example.ui.theme.*

enum class TaskFilterTab {
    ALL,
    COMPLETED_THUMB_UP,
    INCOMPLETE_THUMB_DOWN
}

@Composable
fun TasksScreen(
    viewModel: CrmViewModel,
    modifier: Modifier = Modifier
) {
    val tasks by viewModel.filteredDailyTasks.collectAsState()
    val auditList by viewModel.staffAuditProgressList.collectAsState()
    val currentRole by viewModel.currentUserRole.collectAsState()
    val closingRecords by viewModel.dailyClosingRecords.collectAsState()
    val unreadTasksCount by viewModel.unreadTasksBadgeCount.collectAsState()

    val latestClosing = closingRecords.firstOrNull()

    var showOnlyMyTasks by remember { mutableStateOf(!currentRole.isSuperAdmin) }
    var selectedFilterTab by remember { mutableStateOf(TaskFilterTab.ALL) }

    var inlineTaskTitle by remember { mutableStateOf("") }
    var inlineTaskNotes by remember { mutableStateOf("") }
    var inlineShowNotes by remember { mutableStateOf(false) }

    val quickTaskIdeas = listOf(
        "📞 Call 35 Leads Quota",
        "🤝 5 Confirmed Guest Follow-ups",
        "💳 Collect Seniority Advance Receipt",
        "📋 EOD Daily Closing & Handover",
        "🎯 Afternoon Callback Pipeline"
    )

    val myTasksCount = remember(tasks, currentRole) {
        when (val role = currentRole) {
            is CurrentUserRole.Telecaller -> tasks.count { it.assignedToMemberId == role.id }
            is CurrentUserRole.SuperAdmin -> tasks.count { it.isSelfCreated || it.assignedToMemberName.contains("Admin") }
        }
    }

    val baseTasks = remember(tasks, currentRole, showOnlyMyTasks) {
        if (showOnlyMyTasks) {
            when (val role = currentRole) {
                is CurrentUserRole.Telecaller -> tasks.filter { it.assignedToMemberId == role.id }
                is CurrentUserRole.SuperAdmin -> tasks.filter { it.isSelfCreated || it.assignedToMemberName.contains("Admin") }
            }
        } else {
            tasks
        }
    }

    val displayedTasks = remember(baseTasks, selectedFilterTab) {
        when (selectedFilterTab) {
            TaskFilterTab.ALL -> baseTasks
            TaskFilterTab.COMPLETED_THUMB_UP -> baseTasks.filter { it.status == TaskStatus.COMPLETED }
            TaskFilterTab.INCOMPLETE_THUMB_DOWN -> baseTasks.filter { it.status != TaskStatus.COMPLETED }
        }
    }

    val completedCount = baseTasks.count { it.status == TaskStatus.COMPLETED }
    val incompleteCount = baseTasks.count { it.status != TaskStatus.COMPLETED }
    val completionPercent = if (baseTasks.isNotEmpty()) (completedCount * 100) / baseTasks.size else 0

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("tasks_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Top Action Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Daily Workflow & Task Tracker",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Self-typing method & individual task management",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Button(
                        onClick = { viewModel.openModal(ActiveModalDialog.AddDailyTask) },
                        colors = ButtonDefaults.buttonColors(containerColor = Indigo600),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("btn_write_own_task")
                    ) {
                        Icon(Icons.Default.EditNote, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("+ Write Task", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }

        // Upcoming Follow-up Tasks Alert Banner
        if (unreadTasksCount > 0) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.openModal(ActiveModalDialog.ViewNotifications) }
                        .testTag("tasks_followup_alert_bar"),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Amber50),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Amber300)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(30.dp)
                                    .clip(CircleShape)
                                    .background(Amber500),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Schedule,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            Column {
                                Text(
                                    text = "$unreadTasksCount Upcoming Follow-up Task${if (unreadTasksCount > 1) "s" else ""}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = Amber900
                                )
                                Text(
                                    text = "Tap to review reminders and task details",
                                    fontSize = 11.sp,
                                    color = Amber800
                                )
                            }
                        }

                        Text(
                            text = "View Alerts →",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Amber900
                        )
                    }
                }
            }
        }

        // Daily Task Closing & Audit Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Slate900),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
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
                                    .background(Amber500),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LockClock,
                                    contentDescription = null,
                                    tint = Slate900,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "Daily Task Closing & EOD Audit",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "End-of-day checklist review & handover",
                                    fontSize = 11.sp,
                                    color = Slate400
                                )
                            }
                        }

                        Button(
                            onClick = { viewModel.openModal(ActiveModalDialog.DailyTaskClosing) },
                            colors = ButtonDefaults.buttonColors(containerColor = Amber400, contentColor = Slate900),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                            modifier = Modifier.testTag("btn_daily_task_closing")
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Daily Closing", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Progress Bar
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Today's Task Completion: $completionPercent%",
                                fontSize = 11.sp,
                                color = Slate300,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "${baseTasks.size} Total Routines",
                                fontSize = 11.sp,
                                color = Slate400
                            )
                        }
                        LinearProgressIndicator(
                            progress = { if (baseTasks.isNotEmpty()) completedCount.toFloat() / baseTasks.size.toFloat() else 0f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = Emerald400,
                            trackColor = Slate700
                        )
                    }

                    // Thumbs Indicators Banner
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Slate800)
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Completed (Thumbs-up)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                Icons.Filled.ThumbUp,
                                contentDescription = "Thumbs-up Completed",
                                tint = Emerald400,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "$completedCount Completed",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Emerald300
                            )
                        }

                        // Divider
                        Box(
                            modifier = Modifier
                                .height(16.dp)
                                .width(1.dp)
                                .background(Slate600)
                        )

                        // Incomplete (Thumbs-down)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                Icons.Filled.ThumbDown,
                                contentDescription = "Thumbs-down Incomplete",
                                tint = Rose400,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "$incompleteCount Incomplete",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Rose300
                            )
                        }
                    }

                    // Latest Closing Info (if available)
                    if (latestClosing != null) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Default.History, contentDescription = null, tint = Slate400, modifier = Modifier.size(14.dp))
                            Text(
                                text = "Last closed: ${latestClosing.date} at ${latestClosing.closedAt} by ${latestClosing.closedBy}",
                                fontSize = 10.sp,
                                color = Slate400
                            )
                        }
                    }
                }
            }
        }

        // Live Staff Audit Panel
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
                                    .background(Indigo50),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.FactCheck, contentDescription = null, tint = Indigo600, modifier = Modifier.size(18.dp))
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Live Distributor Audit & Daily Routine",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Visual indicators of distributor checklist completion",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        if (currentRole is CurrentUserRole.Telecaller) {
                            FilterChip(
                                selected = showOnlyMyTasks,
                                onClick = { showOnlyMyTasks = !showOnlyMyTasks },
                                label = { Text(if (showOnlyMyTasks) "My Tasks" else "All Distributors", fontSize = 11.sp) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Audit Progress per Member
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        auditList.forEach { audit ->
                            StaffAuditRow(audit = audit)
                        }
                    }
                }
            }
        }

        // Self-Typing & Own Writing Quick Task Box
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("card_self_typing_task"),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(Indigo50),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.EditNote, contentDescription = null, tint = Indigo600, modifier = Modifier.size(20.dp))
                            }
                            Column {
                                Text(
                                    text = "Make Own Task • Self-Typing",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Slate900
                                )
                                Text(
                                    text = "Self-typing method • Own writing & action plan",
                                    fontSize = 11.sp,
                                    color = Slate500
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Indigo50
                        ) {
                            Text(
                                text = "Self: ${currentRole.displayName.take(16)}",
                                fontSize = 10.sp,
                                color = Indigo700,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    // Quick Suggestion Chips for Fast Self-Typing
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        quickTaskIdeas.forEach { idea ->
                            SuggestionChip(
                                onClick = {
                                    inlineTaskTitle = idea.substringAfter(" ")
                                },
                                label = { Text(idea, fontSize = 10.sp) }
                            )
                        }
                    }

                    // Main Self-Typing Title Field
                    OutlinedTextField(
                        value = inlineTaskTitle,
                        onValueChange = { inlineTaskTitle = it },
                        placeholder = { Text("Type your own daily task here...", fontSize = 13.sp) },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_self_typing_task_title"),
                        trailingIcon = {
                            if (inlineTaskTitle.isNotBlank()) {
                                IconButton(onClick = { inlineTaskTitle = "" }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear", modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    )

                    // Optional / Expandable Self Notes (Own Writing)
                    if (inlineShowNotes) {
                        OutlinedTextField(
                            value = inlineTaskNotes,
                            onValueChange = { inlineTaskNotes = it },
                            label = { Text("Personal Action Plan / Notes (Own Writing)") },
                            placeholder = { Text("Add specific targets, phone numbers, lead names, or strategy...") },
                            minLines = 2,
                            maxLines = 4,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_self_typing_notes")
                        )
                    }

                    // Bottom Bar with Details & Add Button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(
                            onClick = { inlineShowNotes = !inlineShowNotes },
                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp)
                        ) {
                            Icon(
                                if (inlineShowNotes) Icons.Default.ExpandLess else Icons.Default.Edit,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                if (inlineShowNotes) "Hide Notes" else "+ Add Own Writing Notes",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Button(
                            onClick = {
                                if (inlineTaskTitle.isNotBlank()) {
                                    val memberId = when (val role = currentRole) {
                                        is CurrentUserRole.Telecaller -> role.id
                                        is CurrentUserRole.SuperAdmin -> "mem-admin"
                                    }
                                    viewModel.addTask(
                                        title = inlineTaskTitle.trim(),
                                        memberId = memberId,
                                        priority = "High",
                                        dueTime = "06:00 PM",
                                        description = inlineTaskNotes.trim(),
                                        isSelfCreated = true
                                    )
                                    inlineTaskTitle = ""
                                    inlineTaskNotes = ""
                                    inlineShowNotes = false
                                }
                            },
                            enabled = inlineTaskTitle.isNotBlank(),
                            colors = ButtonDefaults.buttonColors(containerColor = Indigo600),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                            modifier = Modifier.testTag("btn_submit_own_task")
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Create My Task", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // View Scope Switcher: My Own Tasks vs All Team Routines
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Slate100)
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Surface(
                    onClick = { showOnlyMyTasks = true },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    color = if (showOnlyMyTasks) Color.White else Color.Transparent,
                    shadowElevation = if (showOnlyMyTasks) 1.dp else 0.dp
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = if (showOnlyMyTasks) Indigo600 else Slate500
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "My Own Tasks ($myTasksCount)",
                            fontSize = 12.sp,
                            fontWeight = if (showOnlyMyTasks) FontWeight.Bold else FontWeight.Medium,
                            color = if (showOnlyMyTasks) Indigo900 else Slate600
                        )
                    }
                }

                Surface(
                    onClick = { showOnlyMyTasks = false },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    color = if (!showOnlyMyTasks) Color.White else Color.Transparent,
                    shadowElevation = if (!showOnlyMyTasks) 1.dp else 0.dp
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Groups,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = if (!showOnlyMyTasks) Indigo600 else Slate500
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "All Team Tasks (${tasks.size})",
                            fontSize = 12.sp,
                            fontWeight = if (!showOnlyMyTasks) FontWeight.Bold else FontWeight.Medium,
                            color = if (!showOnlyMyTasks) Indigo900 else Slate600
                        )
                    }
                }
            }
        }

        // Filters: All vs Completed (👍) vs Incomplete (👎)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "DAILY TASK UPDATES (${displayedTasks.size})",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 0.5.sp
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = selectedFilterTab == TaskFilterTab.ALL,
                        onClick = { selectedFilterTab = TaskFilterTab.ALL },
                        label = { Text("All (${baseTasks.size})", fontSize = 11.sp) }
                    )

                    FilterChip(
                        selected = selectedFilterTab == TaskFilterTab.COMPLETED_THUMB_UP,
                        onClick = { selectedFilterTab = TaskFilterTab.COMPLETED_THUMB_UP },
                        leadingIcon = {
                            Icon(Icons.Filled.ThumbUp, contentDescription = null, tint = Emerald600, modifier = Modifier.size(14.dp))
                        },
                        label = { Text("Done ($completedCount)", fontSize = 11.sp) }
                    )

                    FilterChip(
                        selected = selectedFilterTab == TaskFilterTab.INCOMPLETE_THUMB_DOWN,
                        onClick = { selectedFilterTab = TaskFilterTab.INCOMPLETE_THUMB_DOWN },
                        leadingIcon = {
                            Icon(Icons.Filled.ThumbDown, contentDescription = null, tint = Rose600, modifier = Modifier.size(14.dp))
                        },
                        label = { Text("Incomplete ($incompleteCount)", fontSize = 11.sp) }
                    )
                }
            }
        }

        // Empty state when no tasks match
        if (displayedTasks.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.AssignmentLate, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(36.dp))
                        Text("No Tasks Found in this View", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
                        Text(
                            "Type your task in the box above to start your daily routine!",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }
            }
        }

        // Task Items with Interactive Thumbs-Up / Thumbs-Down Controls
        items(displayedTasks, key = { it.id }) { task ->
            TaskItemCard(
                task = task,
                onSetCompleted = { isCompleted ->
                    viewModel.setTaskCompleted(task.id, isCompleted)
                },
                onEditTask = {
                    viewModel.openModal(ActiveModalDialog.EditDailyTask(task))
                }
            )
        }
    }
}

@Composable
fun StaffAuditRow(audit: StaffAuditProgress) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
            .padding(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AvatarCircle(
                    initials = audit.member.avatarInitials,
                    colorHex = audit.member.avatarColorHex,
                    size = 28
                )
                Column {
                    Text(
                        text = audit.member.name,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${audit.completedTasks}/${audit.totalTasks} completed",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Mini thumbs indicators
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                    Icon(Icons.Filled.ThumbUp, contentDescription = null, tint = Emerald600, modifier = Modifier.size(12.dp))
                    Text("${audit.completedTasks}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Emerald600)
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                    Icon(Icons.Filled.ThumbDown, contentDescription = null, tint = Rose600, modifier = Modifier.size(12.dp))
                    Text("${audit.totalTasks - audit.completedTasks}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Rose600)
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        LinearProgressIndicator(
            progress = { audit.completionPercentage },
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = if (audit.completionPercentage >= 0.99f) Emerald500 else if (audit.completionPercentage > 0.4f) Indigo500 else Amber500,
            trackColor = MaterialTheme.colorScheme.outlineVariant
        )
    }
}

@Composable
fun TaskItemCard(
    task: DailyTask,
    onSetCompleted: (Boolean) -> Unit,
    onEditTask: () -> Unit
) {
    val isDark = LocalIsFieldSalesDark.current
    val isCompleted = task.status == TaskStatus.COMPLETED

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("task_item_${task.id}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCompleted) {
                if (isDark) Color(0xFF064E3B).copy(alpha = 0.25f) else Emerald50.copy(alpha = 0.4f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            // Task info row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    if (task.isSelfCreated) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.padding(bottom = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                Icon(Icons.Default.BorderColor, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(11.dp))
                                Text("Self Task • Own Writing", fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }

                    Text(
                        text = task.title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isCompleted) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
                        textDecoration = if (isCompleted) TextDecoration.LineThrough else null
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (task.isSelfCreated) "Self (${task.assignedToMemberName})" else "Assigned: ${task.assignedToMemberName}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "•",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.outlineVariant
                        )
                        Text(
                            text = "Due: ${task.dueTime}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "•",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.outlineVariant
                        )
                        Text(
                            text = task.priority,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (task.priority == "Urgent") Rose600 else MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    TaskStatusBadge(status = task.status)
                    IconButton(
                        onClick = onEditTask,
                        modifier = Modifier.size(30.dp)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Task", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                    }
                }
            }

            // Personal Notes & Action Plan (Own Writing)
            if (task.description.isNotBlank()) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(Icons.Default.Notes, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(13.dp))
                            Text("Own Writing & Action Plan:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                        }
                        Text(
                            text = task.description,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 15.sp
                        )
                    }
                }
            }

            // Self Completion Remarks (if recorded)
            if (!task.closingRemarks.isNullOrBlank()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isDark) Color(0xFF78350F).copy(alpha = 0.4f) else Amber50)
                        .padding(6.dp)
                ) {
                    Icon(Icons.Default.Comment, contentDescription = null, tint = Amber400, modifier = Modifier.size(13.dp))
                    Text(
                        text = "Remark: ${task.closingRemarks}",
                        fontSize = 11.sp,
                        color = if (isDark) Amber300 else Amber900,
                        fontStyle = FontStyle.Italic
                    )
                }
            }

            // Interactive Thumbs-Up / Thumbs-Down & Edit Action Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(6.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Thumbs-Up (Completed) Button
                OutlinedButton(
                    onClick = { onSetCompleted(true) },
                    modifier = Modifier
                        .weight(1.1f)
                        .height(38.dp)
                        .testTag("task_thumb_up_${task.id}"),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (isCompleted) Emerald500 else Color.Transparent,
                        contentColor = if (isCompleted) Color.White else Emerald700
                    ),
                    shape = RoundedCornerShape(6.dp),
                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 4.dp),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = androidx.compose.ui.graphics.SolidColor(if (isCompleted) Emerald600 else Emerald200)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Filled.ThumbUp,
                        contentDescription = "Mark Completed",
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isCompleted) "Completed 👍" else "Done 👍",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Thumbs-Down (Incomplete) Button
                OutlinedButton(
                    onClick = { onSetCompleted(false) },
                    modifier = Modifier
                        .weight(1.1f)
                        .height(38.dp)
                        .testTag("task_thumb_down_${task.id}"),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (!isCompleted) Rose500 else Color.Transparent,
                        contentColor = if (!isCompleted) Color.White else Rose700
                    ),
                    shape = RoundedCornerShape(6.dp),
                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 4.dp),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = androidx.compose.ui.graphics.SolidColor(if (!isCompleted) Rose600 else Rose200)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Filled.ThumbDown,
                        contentDescription = "Mark Incomplete",
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (!isCompleted) "Pending 👎" else "Incomplete 👎",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Edit / Update Own Task Button
                FilledTonalButton(
                    onClick = onEditTask,
                    modifier = Modifier
                        .weight(0.9f)
                        .height(38.dp)
                        .testTag("task_edit_${task.id}"),
                    shape = RoundedCornerShape(6.dp),
                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 4.dp),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = Indigo50,
                        contentColor = Indigo700
                    )
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(3.dp))
                    Text("Edit ✏️", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

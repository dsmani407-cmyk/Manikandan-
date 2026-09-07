package com.example.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.R
import com.example.model.*
import com.example.ui.components.*
import com.example.ui.screens.*
import com.example.ui.theme.*

enum class CrmDestination(
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    DASHBOARD("Dashboard", Icons.Filled.Dashboard, Icons.Outlined.Dashboard),
    LEADS("Leads", Icons.Filled.PhoneInTalk, Icons.Outlined.PhoneInTalk),
    SCHEDULE("Schedule", Icons.Filled.EventAvailable, Icons.Outlined.EventAvailable),
    TASKS("Tasks", Icons.Filled.Checklist, Icons.Outlined.Checklist),
    COUNSELLING("Counselling & Sales", Icons.Filled.Payments, Icons.Outlined.Payments),
    TEAMS("Teams", Icons.Filled.Groups, Icons.Outlined.Groups)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen(
    crmViewModel: CrmViewModel = viewModel()
) {
    var currentDestination by remember { mutableStateOf(CrmDestination.DASHBOARD) }
    var showRoleSwitchDialog by remember { mutableStateOf(false) }

    val currentRole by crmViewModel.currentUserRole.collectAsState()
    val isUserLoggedIn by crmViewModel.isUserLoggedIn.collectAsState()
    val teams by crmViewModel.teams.collectAsState()
    val members by crmViewModel.members.collectAsState()
    val snackbarMessage by crmViewModel.snackbarMessage.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    val unreadTotalCount by crmViewModel.unreadNotificationCount.collectAsState()
    val unreadLeadsCount by crmViewModel.unreadLeadsBadgeCount.collectAsState()
    val unreadTasksCount by crmViewModel.unreadTasksBadgeCount.collectAsState()
    val toastNotification by crmViewModel.toastNotification.collectAsState()
    val themeMode by crmViewModel.themeMode.collectAsState()

    // Auto-dismiss in-app toast banner after 4.5 seconds
    LaunchedEffect(toastNotification) {
        if (toastNotification != null) {
            kotlinx.coroutines.delay(4500)
            crmViewModel.dismissToastNotification()
        }
    }

    if (!isUserLoggedIn) {
        LoginScreen(viewModel = crmViewModel)
        return
    }

    val visibleDestinations = remember(currentRole) {
        if (currentRole.isSuperAdmin) {
            CrmDestination.values().toList()
        } else {
            CrmDestination.values().filter { it != CrmDestination.TEAMS }
        }
    }

    LaunchedEffect(currentRole) {
        if (!currentRole.isSuperAdmin && currentDestination == CrmDestination.TEAMS) {
            currentDestination = CrmDestination.DASHBOARD
        }
    }

    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let {
            snackbarHostState.showSnackbar(it)
            crmViewModel.clearSnackbar()
        }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isWideScreen = maxWidth >= 600.dp

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            contentWindowInsets = WindowInsets.safeDrawing,
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                TopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(Color.White)
                                    .clickable { showRoleSwitchDialog = true },
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_smart_group_logo),
                                    contentDescription = "Smart Group Logo",
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "Smart Group",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Tranz India Authorized Dealer • ${currentRole.displayName}",
                                    fontSize = 10.sp,
                                    color = if (currentRole.isSuperAdmin) Amber600 else Indigo600,
                                    fontWeight = FontWeight.SemiBold,
                                    maxLines = 1
                                )
                            }
                        }
                    },
                    actions = {
                        // User-Selectable Theme Toggle (Professional Light Mode <-> Field Sales High-Contrast Dark Mode)
                        IconButton(
                            onClick = { crmViewModel.toggleThemeMode() },
                            modifier = Modifier.testTag("top_theme_toggle_btn")
                        ) {
                            if (themeMode.isDark) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(Amber400.copy(alpha = 0.2f))
                                        .border(1.dp, Amber400.copy(alpha = 0.6f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.LightMode,
                                        contentDescription = "Switch to Professional Light Mode",
                                        tint = Amber400,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            } else {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(Indigo50)
                                        .border(1.dp, Indigo200, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.DarkMode,
                                        contentDescription = "Switch to Field Sales High-Contrast Dark Mode",
                                        tint = Indigo600,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }

                        // Notifications Bell Button with Badge
                        IconButton(
                            onClick = { crmViewModel.openModal(ActiveModalDialog.ViewNotifications) },
                            modifier = Modifier.testTag("top_notifications_btn")
                        ) {
                            BadgedBox(
                                badge = {
                                    if (unreadTotalCount > 0) {
                                        Badge(
                                            containerColor = Rose600,
                                            contentColor = Color.White
                                        ) {
                                            Text(
                                                text = if (unreadTotalCount > 9) "9+" else "$unreadTotalCount",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = if (unreadTotalCount > 0) Icons.Default.NotificationsActive else Icons.Outlined.Notifications,
                                    contentDescription = "Notifications ($unreadTotalCount unread)",
                                    tint = if (unreadTotalCount > 0) Indigo600 else Slate600,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }

                        // Role Switch Button
                        Card(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { showRoleSwitchDialog = true }
                                .padding(end = 4.dp)
                                .testTag("top_role_switch_btn"),
                            colors = CardDefaults.cardColors(
                                containerColor = if (currentRole.isSuperAdmin) Amber50 else Indigo50
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = if (currentRole.isSuperAdmin) Icons.Default.AdminPanelSettings else Icons.Default.HeadsetMic,
                                    contentDescription = null,
                                    tint = if (currentRole.isSuperAdmin) Amber600 else Indigo600,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = if (currentRole.isSuperAdmin) "Admin" else "Distributor",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (currentRole.isSuperAdmin) Amber600 else Indigo600
                                )
                                Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = Slate500, modifier = Modifier.size(16.dp))
                            }
                        }

                        // Logout button
                        IconButton(
                            onClick = { crmViewModel.logout() },
                            modifier = Modifier.testTag("top_logout_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Logout,
                                contentDescription = "Log Out",
                                tint = Slate600,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
                )
            },
            bottomBar = {
                if (!isWideScreen) {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surface,
                        tonalElevation = 8.dp,
                        windowInsets = WindowInsets.navigationBars
                    ) {
                        visibleDestinations.forEach { destination ->
                            val selected = currentDestination == destination
                            val badgeCount = when (destination) {
                                CrmDestination.LEADS -> unreadLeadsCount
                                CrmDestination.TASKS -> unreadTasksCount
                                else -> 0
                            }

                            NavigationBarItem(
                                selected = selected,
                                onClick = { currentDestination = destination },
                                icon = {
                                    BadgedBox(
                                        badge = {
                                            if (badgeCount > 0) {
                                                Badge(
                                                    containerColor = if (destination == CrmDestination.LEADS) Sky500 else Amber500,
                                                    contentColor = Color.White
                                                ) {
                                                    Text(
                                                        text = if (badgeCount > 9) "9+" else "$badgeCount",
                                                        fontSize = 9.sp,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                            }
                                        }
                                    ) {
                                        Icon(
                                            imageVector = if (selected) destination.selectedIcon else destination.unselectedIcon,
                                            contentDescription = destination.label
                                        )
                                    }
                                },
                                label = {
                                    Text(
                                        text = destination.label,
                                        fontSize = 10.sp,
                                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                        maxLines = 1
                                    )
                                },
                                modifier = Modifier.testTag("nav_item_${destination.name.lowercase()}"),
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = MaterialTheme.colorScheme.primary,
                                    selectedTextColor = MaterialTheme.colorScheme.primary,
                                    indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            Row(modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding()) {
                if (isWideScreen) {
                    NavigationRail(
                        containerColor = MaterialTheme.colorScheme.surface,
                        modifier = Modifier.fillMaxHeight()
                    ) {
                        Spacer(modifier = Modifier.height(16.dp))
                        visibleDestinations.forEach { destination ->
                            val selected = currentDestination == destination
                            val badgeCount = when (destination) {
                                CrmDestination.LEADS -> unreadLeadsCount
                                CrmDestination.TASKS -> unreadTasksCount
                                else -> 0
                            }

                            NavigationRailItem(
                                selected = selected,
                                onClick = { currentDestination = destination },
                                icon = {
                                    BadgedBox(
                                        badge = {
                                            if (badgeCount > 0) {
                                                Badge(
                                                    containerColor = if (destination == CrmDestination.LEADS) Sky500 else Amber500,
                                                    contentColor = Color.White
                                                ) {
                                                    Text(
                                                        text = if (badgeCount > 9) "9+" else "$badgeCount",
                                                        fontSize = 9.sp,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                            }
                                        }
                                    ) {
                                        Icon(
                                            imageVector = if (selected) destination.selectedIcon else destination.unselectedIcon,
                                            contentDescription = destination.label
                                        )
                                    }
                                },
                                label = { Text(destination.label, fontSize = 11.sp) },
                                modifier = Modifier.testTag("rail_item_${destination.name.lowercase()}")
                            )
                        }
                    }
                    Divider(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(1.dp),
                        color = MaterialTheme.colorScheme.outlineVariant
                    )
                }

                Box(modifier = Modifier.weight(1f)) {
                    when (currentDestination) {
                        CrmDestination.DASHBOARD -> DashboardScreen(
                            viewModel = crmViewModel,
                            onNavigateToLeads = { currentDestination = CrmDestination.LEADS },
                            onNavigateToSchedule = { currentDestination = CrmDestination.SCHEDULE },
                            onNavigateToCounselling = { currentDestination = CrmDestination.COUNSELLING }
                        )
                        CrmDestination.LEADS -> LeadsScreen(viewModel = crmViewModel)
                        CrmDestination.SCHEDULE -> ScheduleScreen(viewModel = crmViewModel)
                        CrmDestination.TASKS -> TasksScreen(viewModel = crmViewModel)
                        CrmDestination.COUNSELLING -> CounsellingAndSalesScreen(viewModel = crmViewModel)
                        CrmDestination.TEAMS -> TeamsScreen(viewModel = crmViewModel)
                    }
                }
            }
        }

        // Real-Time Animated Toast Banner for Distributor alerts (New Lead or Upcoming Task)
        AnimatedVisibility(
            visible = toastNotification != null,
            enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 12.dp, start = 16.dp, end = 16.dp)
                .widthIn(max = 500.dp)
        ) {
            toastNotification?.let { notif ->
                val isLead = notif.type == NotificationType.NEW_LEAD
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            if (notif.targetDestination == "LEADS") {
                                currentDestination = CrmDestination.LEADS
                            } else if (notif.targetDestination == "TASKS") {
                                currentDestination = CrmDestination.TASKS
                            }
                            crmViewModel.markNotificationAsRead(notif.id)
                            crmViewModel.dismissToastNotification()
                        }
                        .testTag("realtime_toast_banner"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Slate900),
                    elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(14.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(if (isLead) Sky500 else Amber500),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isLead) Icons.Default.PersonAdd else Icons.Default.Schedule,
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
                                    text = notif.title,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(if (isLead) Sky900 else Amber900)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = if (isLead) "New Lead" else "Follow-up",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isLead) Sky300 else Amber300
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = notif.message,
                                fontSize = 11.sp,
                                color = Slate300,
                                maxLines = 2,
                                lineHeight = 15.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Tap to view in ${if (isLead) "Leads Sheet" else "Task Tracker"} →",
                                fontSize = 10.sp,
                                color = if (isLead) Sky400 else Amber400,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        IconButton(
                            onClick = { crmViewModel.dismissToastNotification() },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Dismiss",
                                tint = Slate400,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    // Modal Host
    CrmModalsHost(viewModel = crmViewModel)

    // Role & Profile Dialog (Strictly Scoped: Distributor sees ONLY their own details; Super Admin sees all)
    if (showRoleSwitchDialog) {
        AlertDialog(
            onDismissRequest = { showRoleSwitchDialog = false },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_smart_group_logo),
                        contentDescription = "Smart Group Logo",
                        modifier = Modifier.size(32.dp)
                    )
                    Column {
                        Text(
                            text = if (currentRole.isSuperAdmin) "Super Admin Control" else "My Distributor Profile",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = if (currentRole.isSuperAdmin) "System oversight & distributor management" else "Individual confidential distributor account",
                            fontSize = 11.sp,
                            color = Slate500
                        )
                    }
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Display & Theme Readability Settings Card (Professional Light vs Field Sales High-Contrast Dark)
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        shape = RoundedCornerShape(10.dp),
                        border = CardDefaults.outlinedCardBorder()
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Icon(
                                        imageVector = if (themeMode.isDark) Icons.Default.DarkMode else Icons.Default.LightMode,
                                        contentDescription = null,
                                        tint = if (themeMode.isDark) Amber400 else Indigo600,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = "Display Theme & Contrast",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(if (themeMode.isDark) Color(0xFF0F172A) else Indigo100)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = if (themeMode.isDark) "Field Sales Mode" else "Light Mode",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = if (themeMode.isDark) Amber400 else Indigo700
                                    )
                                }
                            }

                            Text(
                                text = "Field Sales mode provides deep pitch-black high contrast engineered for readability during outdoor and in-vehicle prospect calls.",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 15.sp
                            )

                            // Interactive Theme Toggle Segmented Options
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // Option 1: Professional Light
                                Card(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable { crmViewModel.setThemeMode(ThemeMode.LIGHT) }
                                        .testTag("theme_opt_light"),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (!themeMode.isDark) Color.White else Color(0xFF0F172A)
                                    ),
                                    border = BorderStroke(
                                        width = if (!themeMode.isDark) 2.dp else 1.dp,
                                        color = if (!themeMode.isDark) Indigo600 else Slate700
                                    )
                                ) {
                                    Column(
                                        modifier = Modifier.padding(8.dp),
                                        verticalArrangement = Arrangement.spacedBy(2.dp)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Icon(
                                                Icons.Default.LightMode,
                                                contentDescription = null,
                                                tint = if (!themeMode.isDark) Indigo600 else Slate400,
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Text(
                                                text = "Light Mode",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 11.sp,
                                                color = if (!themeMode.isDark) Indigo600 else Slate300
                                            )
                                        }
                                        Text(
                                            text = "Indoor office clarity",
                                            fontSize = 9.sp,
                                            color = if (!themeMode.isDark) Slate600 else Slate400
                                        )
                                    }
                                }

                                // Option 2: Field Sales High-Contrast Dark
                                Card(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable { crmViewModel.setThemeMode(ThemeMode.DARK_HIGH_CONTRAST) }
                                        .testTag("theme_opt_field_sales_dark"),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (themeMode.isDark) Color(0xFF070B14) else Slate100
                                    ),
                                    border = BorderStroke(
                                        width = if (themeMode.isDark) 2.dp else 1.dp,
                                        color = if (themeMode.isDark) Amber400 else Slate300
                                    )
                                ) {
                                    Column(
                                        modifier = Modifier.padding(8.dp),
                                        verticalArrangement = Arrangement.spacedBy(2.dp)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Icon(
                                                Icons.Default.DarkMode,
                                                contentDescription = null,
                                                tint = if (themeMode.isDark) Amber400 else Slate600,
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Text(
                                                text = "Field Sales ⚡",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 11.sp,
                                                color = if (themeMode.isDark) Amber400 else Slate700
                                            )
                                        }
                                        Text(
                                            text = "Outdoor high contrast",
                                            fontSize = 9.sp,
                                            color = if (themeMode.isDark) Color(0xFFF1F5F9) else Slate500
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Active Session Details Box
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = if (currentRole.isSuperAdmin) Amber50 else Slate100),
                        shape = RoundedCornerShape(10.dp),
                        border = CardDefaults.outlinedCardBorder()
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(currentRole.displayName, fontWeight = FontWeight.ExtraBold, fontSize = 13.sp, color = Indigo600)
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(if (currentRole.isSuperAdmin) Amber500 else Indigo600)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(currentRole.roleName, fontWeight = FontWeight.Bold, fontSize = 10.sp, color = Color.White)
                                }
                            }
                            if (currentRole is CurrentUserRole.Telecaller) {
                                Text("Team: ${(currentRole as CurrentUserRole.Telecaller).teamName}", fontSize = 11.sp, color = Slate600)
                            }
                            Divider(color = Slate200, modifier = Modifier.padding(vertical = 2.dp))
                            Text("Current Session: ${currentRole.currentSessionLogin}", fontSize = 10.sp, color = Slate600)
                            Text("Last Session: ${currentRole.previousSessionLogin}", fontSize = 10.sp, color = Slate500)
                        }
                    }

                    if (currentRole is CurrentUserRole.Telecaller) {
                        val telecallerRole = currentRole as CurrentUserRole.Telecaller
                        // Distributor's own credential management button
                        val myMember = members.find { it.id == telecallerRole.id }
                        if (myMember != null) {
                            Button(
                                onClick = {
                                    showRoleSwitchDialog = false
                                    crmViewModel.openModal(ActiveModalDialog.EditMemberCredentials(myMember))
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Slate900),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.Key, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Change My Password", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    } else {
                        // Super Admin: Option to edit Admin ID, Name & Password
                        Button(
                            onClick = {
                                showRoleSwitchDialog = false
                                crmViewModel.openModal(ActiveModalDialog.EditAdminCredentials)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Amber600),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("btn_edit_admin_credentials")
                        ) {
                            Icon(Icons.Default.AdminPanelSettings, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Edit Admin ID, Name & Password", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }

                        // Super Admin: Distributor accounts management
                        Text(
                            text = "Distributor Accounts Management:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Slate700
                        )

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            members.forEach { m ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Slate50)
                                        .padding(8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        AvatarCircle(
                                            initials = m.avatarInitials,
                                            colorHex = m.avatarColorHex,
                                            size = 30,
                                            avatarUrl = m.avatarUrl
                                        )
                                        Column {
                                            Text(m.name, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Slate900)
                                            Text("${m.role} • ${m.phone}", fontSize = 10.sp, color = Slate500, fontWeight = FontWeight.Medium)
                                            Text("Last Login: ${m.lastLoginAt}", fontSize = 9.sp, color = Slate400)
                                        }
                                    }

                                    IconButton(
                                        onClick = {
                                            showRoleSwitchDialog = false
                                            crmViewModel.openModal(ActiveModalDialog.EditMemberCredentials(m))
                                        },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(Icons.Default.Key, contentDescription = "Manage Credentials", tint = Amber600, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = {
                            showRoleSwitchDialog = false
                            crmViewModel.logout()
                        },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Rose600)
                    ) {
                        Icon(Icons.Default.Logout, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Sign Out", fontSize = 12.sp)
                    }

                    Button(
                        onClick = { showRoleSwitchDialog = false },
                        colors = ButtonDefaults.buttonColors(containerColor = Indigo600)
                    ) {
                        Text("Close", fontSize = 12.sp)
                    }
                }
            }
        )
    }
}

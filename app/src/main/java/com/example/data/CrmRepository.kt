package com.example.data

import com.example.data.local.*
import com.example.data.remote.CloudSyncStatus
import com.example.data.remote.SupabaseClient
import com.example.data.remote.SupabaseUserSession
import com.example.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import java.util.*

class CrmRepository(
    private val crmDao: CrmDao? = null
) {
    private val repoScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    val supabaseClient: SupabaseClient = SupabaseClient()
    val cloudSyncStatus: StateFlow<CloudSyncStatus> = supabaseClient.syncStatus
    private var periodicSyncJob: Job? = null

    private val _isUserLoggedIn = MutableStateFlow<Boolean>(false)
    val isUserLoggedIn: StateFlow<Boolean> = _isUserLoggedIn.asStateFlow()

    private val _currentUserRole = MutableStateFlow<CurrentUserRole>(CurrentUserRole.SuperAdmin())
    val currentUserRole: StateFlow<CurrentUserRole> = _currentUserRole.asStateFlow()

    private val _teams = MutableStateFlow(MockDataProvider.teams)
    val teams: StateFlow<List<Team>> = _teams.asStateFlow()

    private val _members = MutableStateFlow(MockDataProvider.members)
    val members: StateFlow<List<TeamMember>> = _members.asStateFlow()

    private val _leads = MutableStateFlow(MockDataProvider.leads)
    val leads: StateFlow<List<Lead>> = _leads.asStateFlow()

    private val _confirmedGuests = MutableStateFlow(MockDataProvider.confirmedGuests)
    val confirmedGuests: StateFlow<List<ConfirmedGuest>> = _confirmedGuests.asStateFlow()

    private val _dailyTasks = MutableStateFlow(MockDataProvider.dailyTasks)
    val dailyTasks: StateFlow<List<DailyTask>> = _dailyTasks.asStateFlow()

    private val _offlineCacheInfo = MutableStateFlow(OfflineCacheInfo())
    val offlineCacheInfo: StateFlow<OfflineCacheInfo> = _offlineCacheInfo.asStateFlow()

    data class AdminAccount(
        val adminId: String = "admin",
        val name: String = "Command SuperAdmin",
        val email: String = "d.s.mani407@gmail.com"
    )

    private val _adminAccount = MutableStateFlow(AdminAccount())
    val adminAccount: StateFlow<AdminAccount> = _adminAccount.asStateFlow()

    init {
        // Automatically seed/cache initial data into Room database if available
        if (crmDao != null) {
            seedRoomDatabaseIfEmpty()
        }
    }

    private fun seedRoomDatabaseIfEmpty() {
        repoScope.launch {
            try {
                _offlineCacheInfo.value = _offlineCacheInfo.value.copy(isSyncing = true)
                val leadCount = crmDao?.getCachedLeadsCount() ?: 0
                val now = System.currentTimeMillis()
                val sdf = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
                val timeStr = sdf.format(Date(now))

                if (leadCount == 0) {
                    // Seed initial data into Room cache
                    crmDao?.insertLeads(MockDataProvider.leads.map { CachedLeadEntity.fromDomain(it, now) })
                    crmDao?.insertTransactions(MockDataProvider.salesTransactions.map { CachedSalesTransactionEntity.fromDomain(it, now) })
                    crmDao?.insertGuests(MockDataProvider.confirmedGuests.map { CachedConfirmedGuestEntity.fromDomain(it, now) })
                    crmDao?.insertTasks(MockDataProvider.dailyTasks.map { CachedDailyTaskEntity.fromDomain(it, now) })
                }

                updateCacheInfo()
            } catch (e: Exception) {
                _offlineCacheInfo.value = _offlineCacheInfo.value.copy(isSyncing = false)
            }
        }
    }

    suspend fun updateCacheInfo() {
        val sdf = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
        val leadsCount = crmDao?.getCachedLeadsCount() ?: _leads.value.size
        val salesCount = crmDao?.getCachedTransactionsCount() ?: _salesTransactions.value.size
        _offlineCacheInfo.value = _offlineCacheInfo.value.copy(
            lastSyncTimestamp = sdf.format(Date()),
            cachedLeadsCount = leadsCount,
            cachedSalesCount = salesCount,
            cachedGuestsCount = _confirmedGuests.value.size,
            cachedTasksCount = _dailyTasks.value.size,
            isSyncing = false
        )
    }

    fun syncAllToRoomCache() {
        if (crmDao == null) return
        repoScope.launch {
            _offlineCacheInfo.value = _offlineCacheInfo.value.copy(isSyncing = true)
            val now = System.currentTimeMillis()
            crmDao.insertLeads(_leads.value.map { CachedLeadEntity.fromDomain(it, now) })
            crmDao.insertTransactions(_salesTransactions.value.map { CachedSalesTransactionEntity.fromDomain(it, now) })
            crmDao.insertGuests(_confirmedGuests.value.map { CachedConfirmedGuestEntity.fromDomain(it, now) })
            crmDao.insertTasks(_dailyTasks.value.map { CachedDailyTaskEntity.fromDomain(it, now) })
            updateCacheInfo()
        }
    }

    fun toggleOfflineModeSimulation() {
        val current = _offlineCacheInfo.value.isOfflineModeActive
        _offlineCacheInfo.value = _offlineCacheInfo.value.copy(isOfflineModeActive = !current)
    }


    private val _dailyClosingRecords = MutableStateFlow<List<DailyTaskClosingRecord>>(
        listOf(
            DailyTaskClosingRecord(
                id = "closing-prev-1",
                date = "03 Sep 2026",
                closedBy = "Mani (Super Admin)",
                totalTasks = 6,
                completedCount = 5,
                incompleteCount = 1,
                closingRemarks = "Daily operations audit concluded. Alpha & Beta teams met afternoon telecalling targets.",
                closedAt = "06:30 PM"
            )
        )
    )
    val dailyClosingRecords: StateFlow<List<DailyTaskClosingRecord>> = _dailyClosingRecords.asStateFlow()

    private val _counsellingLogs = MutableStateFlow(MockDataProvider.counsellingLogs)
    val counsellingLogs: StateFlow<List<CounsellingLog>> = _counsellingLogs.asStateFlow()

    private val _salesTransactions = MutableStateFlow(MockDataProvider.salesTransactions)
    val salesTransactions: StateFlow<List<SalesTransaction>> = _salesTransactions.asStateFlow()

    private val _celebrations = MutableStateFlow(MockDataProvider.celebrations)
    val celebrations: StateFlow<List<ClosureCelebration>> = _celebrations.asStateFlow()

    private val _notifications = MutableStateFlow<List<AppNotification>>(
        listOf(
            AppNotification(
                id = "notif-init-1",
                title = "New Lead Assigned",
                message = "Ramesh Venkatesan assigned for Executive PG program follow-up.",
                timestamp = "Just now",
                type = NotificationType.NEW_LEAD,
                targetDestination = "LEADS",
                forMemberId = "mem-1",
                forTeamId = "team-1",
                isRead = false
            ),
            AppNotification(
                id = "notif-init-2",
                title = "Upcoming Follow-up Task",
                message = "Follow-up calls scheduled for afternoon batches at 02:00 PM.",
                timestamp = "10m ago",
                type = NotificationType.UPCOMING_TASK,
                targetDestination = "TASKS",
                forMemberId = "mem-1",
                forTeamId = "team-1",
                isRead = false
            ),
            AppNotification(
                id = "notif-init-3",
                title = "New Lead Assigned",
                message = "Ananya Sankar assigned from website inquiry for Data Science program.",
                timestamp = "25m ago",
                type = NotificationType.NEW_LEAD,
                targetDestination = "LEADS",
                forMemberId = "mem-2",
                forTeamId = "team-1",
                isRead = false
            )
        )
    )
    val notifications: StateFlow<List<AppNotification>> = _notifications.asStateFlow()

    fun addNotification(notification: AppNotification) {
        _notifications.value = listOf(notification) + _notifications.value
    }

    fun markNotificationAsRead(id: String) {
        _notifications.value = _notifications.value.map {
            if (it.id == id) it.copy(isRead = true) else it
        }
    }

    fun markAllNotificationsAsRead(memberId: String?, teamId: String?, isSuperAdmin: Boolean) {
        _notifications.value = _notifications.value.map { notif ->
            val applies = isSuperAdmin ||
                (memberId != null && notif.forMemberId == memberId) ||
                (teamId != null && notif.forTeamId == teamId) ||
                (notif.forMemberId == null && notif.forTeamId == null)
            if (applies) notif.copy(isRead = true) else notif
        }
    }

    fun clearNotifications() {
        _notifications.value = emptyList()
    }

    // Theme Mode (Professional Light vs Field Sales High-Contrast Dark)
    private val _themeMode = MutableStateFlow(ThemeMode.LIGHT)
    val themeMode: StateFlow<ThemeMode> = _themeMode.asStateFlow()

    fun setThemeMode(mode: ThemeMode) {
        _themeMode.value = mode
    }

    fun toggleThemeMode(): ThemeMode {
        val newMode = if (_themeMode.value == ThemeMode.LIGHT) ThemeMode.DARK_HIGH_CONTRAST else ThemeMode.LIGHT
        _themeMode.value = newMode
        return newMode
    }

    // Filters
    private val _selectedTeamFilter = MutableStateFlow<String?>(null) // null = all
    val selectedTeamFilter: StateFlow<String?> = _selectedTeamFilter.asStateFlow()

    fun setTeamFilter(teamId: String?) {
        _selectedTeamFilter.value = teamId
    }

    private val _selectedDistributorFilter = MutableStateFlow<String?>(null) // null = all
    val selectedDistributorFilter: StateFlow<String?> = _selectedDistributorFilter.asStateFlow()

    fun setDistributorFilter(distributorId: String?) {
        _selectedDistributorFilter.value = distributorId
    }

    private val _selectedDateFilter = MutableStateFlow<String>("ALL") // ALL, TODAY, THIS_WEEK, THIS_MONTH
    val selectedDateFilter: StateFlow<String> = _selectedDateFilter.asStateFlow()

    fun setDateFilter(filter: String) {
        _selectedDateFilter.value = filter
    }

    private fun getCurrentTimestamp(): String {
        val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
        return sdf.format(Date())
    }

    suspend fun loginAsync(userIdInput: String, passwordInput: String): Pair<Boolean, String?> {
        val trimmedId = userIdInput.trim()
        val trimmedPass = passwordInput.trim()

        if (trimmedId.isEmpty() || trimmedPass.isEmpty()) {
            return false to "Please enter both User ID/Email and Password."
        }

        // Authenticate securely via Supabase Client
        val result = supabaseClient.signIn(trimmedId, trimmedPass)
        if (result.isSuccess) {
            val session = result.getOrThrow()
            val now = getCurrentTimestamp()
            val isSuperAdmin = session.role.equals("superadmin", ignoreCase = true) ||
                    session.role.equals("admin", ignoreCase = true)

            if (isSuperAdmin) {
                val adminRole = CurrentUserRole.SuperAdmin(
                    id = session.userId,
                    name = session.name,
                    email = session.email,
                    userId = session.email,
                    currentLoginAt = now
                )
                _currentUserRole.value = adminRole
            } else {
                val distributorRole = CurrentUserRole.Telecaller(
                    id = session.userId,
                    name = session.name,
                    teamId = session.teamId,
                    teamName = session.teamName,
                    email = session.email,
                    userId = session.agentCode,
                    currentLoginAt = now
                )
                _currentUserRole.value = distributorRole
            }

            _isUserLoggedIn.value = true
            syncFromCloud()
            startPeriodicSync()
            return true to null
        } else {
            return false to (result.exceptionOrNull()?.message ?: "Authentication failed. Please verify credentials.")
        }
    }

    fun login(userIdInput: String, passwordInput: String): Pair<Boolean, String?> {
        return runBlocking {
            loginAsync(userIdInput, passwordInput)
        }
    }

    suspend fun registerDistributor(
        email: String,
        pass: String,
        name: String,
        phone: String,
        teamId: String,
        teamName: String
    ): Pair<Boolean, String?> {
        val res = supabaseClient.signUp(email, pass, name, phone, teamId, teamName)
        if (res.isSuccess) {
            val session = res.getOrThrow()
            val now = getCurrentTimestamp()
            val distributorRole = CurrentUserRole.Telecaller(
                id = session.userId,
                name = session.name,
                teamId = session.teamId,
                teamName = session.teamName,
                email = session.email,
                userId = session.agentCode,
                currentLoginAt = now
            )
            _currentUserRole.value = distributorRole
            _isUserLoggedIn.value = true
            syncFromCloud()
            startPeriodicSync()
            return true to null
        } else {
            return false to (res.exceptionOrNull()?.message ?: "Registration failed.")
        }
    }

    fun syncFromCloud() {
        repoScope.launch {
            try {
                // Fetch latest leads from Supabase cloud
                val leadsResult = supabaseClient.fetchLeads()
                if (leadsResult.isSuccess) {
                    val remoteLeads = leadsResult.getOrNull()
                    if (!remoteLeads.isNullOrEmpty()) {
                        _leads.value = remoteLeads
                    }
                }

                // Fetch latest daily tasks
                val tasksResult = supabaseClient.fetchDailyTasks()
                if (tasksResult.isSuccess) {
                    val remoteTasks = tasksResult.getOrNull()
                    if (!remoteTasks.isNullOrEmpty()) {
                        _dailyTasks.value = remoteTasks
                    }
                }

                // Fetch latest sales
                val salesResult = supabaseClient.fetchSales()
                if (salesResult.isSuccess) {
                    val remoteSales = salesResult.getOrNull()
                    if (!remoteSales.isNullOrEmpty()) {
                        _salesTransactions.value = remoteSales
                    }
                }

                // Fetch latest counselling logs
                val logsResult = supabaseClient.fetchCounsellingLogs()
                if (logsResult.isSuccess) {
                    val remoteLogs = logsResult.getOrNull()
                    if (!remoteLogs.isNullOrEmpty()) {
                        _counsellingLogs.value = remoteLogs
                    }
                }

                // Fetch latest confirmed guests
                val guestsResult = supabaseClient.fetchConfirmedGuests()
                if (guestsResult.isSuccess) {
                    val remoteGuests = guestsResult.getOrNull()
                    if (!remoteGuests.isNullOrEmpty()) {
                        _confirmedGuests.value = remoteGuests
                    }
                }

                // If super admin, fetch all registered distributor profiles
                if (_currentUserRole.value.isSuperAdmin) {
                    val profilesResult = supabaseClient.fetchDistributorProfiles()
                    if (profilesResult.isSuccess) {
                        val remoteProfiles = profilesResult.getOrNull()
                        if (!remoteProfiles.isNullOrEmpty()) {
                            _members.value = remoteProfiles
                        }
                    }
                }

                updateCacheInfo()
            } catch (e: Exception) {
                // Network error silently caught; existing cache retained
            }
        }
    }

    private fun startPeriodicSync() {
        periodicSyncJob?.cancel()
        periodicSyncJob = repoScope.launch {
            while (_isUserLoggedIn.value) {
                delay(8000)
                if (_isUserLoggedIn.value) {
                    syncFromCloud()
                }
            }
        }
    }

    fun logout() {
        periodicSyncJob?.cancel()
        supabaseClient.signOut()
        _isUserLoggedIn.value = false
    }

    fun switchRole(role: CurrentUserRole) {
        _currentUserRole.value = role
    }

    // Lead Operations
    fun updateLeadStatus(leadId: String, newStatus: LeadStatus) {
        _leads.value = _leads.value.map {
            if (it.id == leadId) it.copy(status = newStatus) else it
        }
        repoScope.launch {
            supabaseClient.updateLeadStatus(leadId, newStatus)
        }
        crmDao?.let { dao ->
            repoScope.launch {
                dao.updateLeadStatus(leadId, newStatus.name)
                updateCacheInfo()
            }
        }
    }

    fun addLead(lead: Lead) {
        _leads.value = listOf(lead) + _leads.value
        repoScope.launch {
            supabaseClient.insertLead(lead)
        }
        val notif = AppNotification(
            id = "notif-lead-${System.currentTimeMillis()}",
            title = "New Lead Assigned",
            message = "${lead.name} (${lead.courseOrProgram}) assigned to ${lead.assignedTelecallerName}.",
            timestamp = "Just now",
            type = NotificationType.NEW_LEAD,
            targetDestination = "LEADS",
            forMemberId = lead.assignedTelecallerId,
            forTeamId = lead.teamId,
            isRead = false
        )
        addNotification(notif)
        crmDao?.let { dao ->
            repoScope.launch {
                dao.insertLead(CachedLeadEntity.fromDomain(lead))
                updateCacheInfo()
            }
        }
    }

    fun convertLeadToConfirmedGuest(
        leadId: String,
        visitDateTime: String,
        assignedCounsellor: String,
        location: String = "Counselling Hall A",
        notes: String = ""
    ) {
        val lead = _leads.value.find { it.id == leadId } ?: return
        // Update lead status to CONVERTED
        updateLeadStatus(leadId, LeadStatus.CONVERTED)

        // Create Confirmed Guest entry
        val newGuest = ConfirmedGuest(
            id = "guest-${UUID.randomUUID().toString().take(6)}",
            leadId = leadId,
            teamId = lead.teamId,
            guestName = lead.name,
            phone = lead.phone,
            assignedTelecallerName = lead.assignedTelecallerName,
            visitDateTime = visitDateTime,
            assignedCounsellor = assignedCounsellor,
            status = GuestStatus.SCHEDULED,
            locationOrRoom = location,
            notes = if (notes.isNotBlank()) notes else lead.notes,
            distributorId = lead.assignedTelecallerId
        )
        _confirmedGuests.value = listOf(newGuest) + _confirmedGuests.value
        repoScope.launch {
            supabaseClient.insertConfirmedGuest(newGuest)
        }
    }

    fun updateGuestStatus(guestId: String, newStatus: GuestStatus) {
        _confirmedGuests.value = _confirmedGuests.value.map {
            if (it.id == guestId) it.copy(status = newStatus) else it
        }
    }

    fun addConfirmedGuest(guest: ConfirmedGuest) {
        _confirmedGuests.value = listOf(guest) + _confirmedGuests.value
        repoScope.launch {
            supabaseClient.insertConfirmedGuest(guest)
        }
        crmDao?.let { dao ->
            repoScope.launch {
                dao.insertGuest(CachedConfirmedGuestEntity.fromDomain(guest))
                updateCacheInfo()
            }
        }
    }

    // Task Operations
    fun updateTaskStatus(taskId: String, newStatus: TaskStatus) {
        val isCompleted = (newStatus == TaskStatus.COMPLETED)
        _dailyTasks.value = _dailyTasks.value.map {
            if (it.id == taskId) it.copy(status = newStatus) else it
        }
        repoScope.launch {
            supabaseClient.updateTaskCompletion(taskId, isCompleted)
        }
        crmDao?.let { dao ->
            repoScope.launch {
                dao.updateTaskStatus(taskId, newStatus.name)
                updateCacheInfo()
            }
        }
    }

    fun setTaskCompletion(taskId: String, isCompleted: Boolean) {
        val newStatus = if (isCompleted) TaskStatus.COMPLETED else TaskStatus.PENDING
        _dailyTasks.value = _dailyTasks.value.map {
            if (it.id == taskId) it.copy(status = newStatus) else it
        }
        repoScope.launch {
            supabaseClient.updateTaskCompletion(taskId, isCompleted)
        }
        crmDao?.let { dao ->
            repoScope.launch {
                dao.updateTaskStatus(taskId, newStatus.name)
                updateCacheInfo()
            }
        }
    }

    fun closeDailyTasks(closedByName: String, remarks: String): DailyTaskClosingRecord {
        val currentTasks = _dailyTasks.value
        val completed = currentTasks.count { it.status == TaskStatus.COMPLETED }
        val incomplete = currentTasks.size - completed
        val sdfDate = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        val sdfTime = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val now = Date()

        val record = DailyTaskClosingRecord(
            id = "closing-${UUID.randomUUID().toString().take(6)}",
            date = sdfDate.format(now),
            closedBy = closedByName,
            totalTasks = currentTasks.size,
            completedCount = completed,
            incompleteCount = incomplete,
            closingRemarks = if (remarks.isNotBlank()) remarks else "Routine daily checklist reviewed and finalized.",
            closedAt = sdfTime.format(now)
        )

        // Mark current tasks as closed
        _dailyTasks.value = _dailyTasks.value.map {
            it.copy(isClosed = true, closingRemarks = remarks)
        }

        _dailyClosingRecords.value = listOf(record) + _dailyClosingRecords.value
        return record
    }

    fun addTask(task: DailyTask) {
        _dailyTasks.value = listOf(task) + _dailyTasks.value
        repoScope.launch {
            supabaseClient.insertDailyTask(task)
        }
        val notif = AppNotification(
            id = "notif-task-${System.currentTimeMillis()}",
            title = "Upcoming Follow-up Task",
            message = "${task.title} (Due: ${task.dueTime}) assigned to ${task.assignedToMemberName}.",
            timestamp = "Just now",
            type = NotificationType.UPCOMING_TASK,
            targetDestination = "TASKS",
            forMemberId = task.assignedToMemberId,
            forTeamId = task.teamId,
            isRead = false
        )
        addNotification(notif)
        crmDao?.let { dao ->
            repoScope.launch {
                dao.insertTask(CachedDailyTaskEntity.fromDomain(task))
                updateCacheInfo()
            }
        }
    }

    fun updateTask(
        taskId: String,
        title: String,
        description: String,
        priority: String,
        dueTime: String,
        status: TaskStatus,
        closingRemarks: String? = null
    ) {
        _dailyTasks.value = _dailyTasks.value.map {
            if (it.id == taskId) {
                it.copy(
                    title = title,
                    description = description,
                    priority = priority,
                    dueTime = dueTime,
                    status = status,
                    closingRemarks = closingRemarks ?: it.closingRemarks
                )
            } else it
        }
        repoScope.launch {
            supabaseClient.updateTaskCompletion(taskId, status == TaskStatus.COMPLETED)
        }
        crmDao?.let { dao ->
            repoScope.launch {
                val updatedTask = _dailyTasks.value.find { it.id == taskId }
                if (updatedTask != null) {
                    dao.insertTask(CachedDailyTaskEntity.fromDomain(updatedTask))
                }
            }
        }
    }

    fun deleteTask(taskId: String) {
        _dailyTasks.value = _dailyTasks.value.filter { it.id != taskId }
        crmDao?.let { dao ->
            repoScope.launch {
                dao.deleteTask(taskId)
                updateCacheInfo()
            }
        }
    }

    // Counselling Log Operations
    fun addCounsellingLog(log: CounsellingLog) {
        _counsellingLogs.value = listOf(log) + _counsellingLogs.value
        repoScope.launch {
            supabaseClient.insertCounsellingLog(log)
        }
    }

    // Sales & Seniority Operations
    fun addSalesTransaction(tx: SalesTransaction) {
        _salesTransactions.value = listOf(tx) + _salesTransactions.value
        repoScope.launch {
            supabaseClient.insertSalesTransaction(tx)
        }
        // If closed with amount, add to celebration wall
        val newCeleb = ClosureCelebration(
            id = "celeb-${UUID.randomUUID().toString().take(6)}",
            teamId = tx.teamId,
            agentName = tx.agentName,
            teamName = tx.teamName,
            candidateName = tx.candidateName,
            closedAmount = tx.seniorityAmount,
            date = "Today",
            avatarInitials = tx.agentName.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString(""),
            avatarColorHex = 0xFF10B981,
            closureProofImageUrl = tx.closureProofImageUrl,
            badgeTitle = "Fresh Closure!"
        )
        _celebrations.value = listOf(newCeleb) + _celebrations.value

        crmDao?.let { dao ->
            repoScope.launch {
                dao.insertTransaction(CachedSalesTransactionEntity.fromDomain(tx))
                updateCacheInfo()
            }
        }
    }

    fun approveTransaction(txId: String) {
        _salesTransactions.value = _salesTransactions.value.map {
            if (it.id == txId) it.copy(approvalStatus = ApprovalStatus.APPROVED) else it
        }
        crmDao?.let { dao ->
            repoScope.launch {
                dao.updateTransactionApproval(txId, ApprovalStatus.APPROVED.name)
                updateCacheInfo()
            }
        }
    }

    // Team & User Management (Admin Only)
    fun addTeam(name: String, leadName: String, leadEmail: String, leadPhone: String, target: Double) {
        val newTeam = Team(
            id = "team-${UUID.randomUUID().toString().take(6)}",
            name = name,
            leadName = leadName,
            leadEmail = leadEmail,
            leadPhone = leadPhone,
            colorHex = 0xFF6366F1,
            targetMonthlyRevenue = target,
            isArchived = false
        )
        _teams.value = _teams.value + newTeam
    }

    fun toggleArchiveTeam(teamId: String) {
        _teams.value = _teams.value.map {
            if (it.id == teamId) it.copy(isArchived = !it.isArchived) else it
        }
    }

    fun updateTeam(team: Team) {
        _teams.value = _teams.value.map {
            if (it.id == team.id) team else it
        }
    }

    fun deleteTeam(teamId: String) {
        _teams.value = _teams.value.filter { it.id != teamId }
        _members.value = _members.value.filter { it.teamId != teamId }
    }

    fun addMember(
        name: String,
        teamId: String,
        role: String,
        email: String,
        phone: String,
        avatarUrl: String? = null,
        loginUserId: String? = null
    ) {
        val initials = name.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString("")
        val sanitizedId = (loginUserId?.takeIf { it.isNotBlank() } ?: name.split(" ").firstOrNull()?.lowercase() ?: "user").filter { it.isLetterOrDigit() }
        val newMember = TeamMember(
            id = "mem-${UUID.randomUUID().toString().take(6)}",
            teamId = teamId,
            name = name,
            role = role,
            email = email,
            phone = phone,
            avatarInitials = if (initials.isNotBlank()) initials else "TM",
            avatarColorHex = 0xFF4F46E5,
            avatarUrl = avatarUrl,
            activeLeadsCount = 0,
            conversionsCount = 0,
            totalClosedAmount = 0.0,
            loginUserId = sanitizedId,
            currentLoginAt = getCurrentTimestamp(),
            lastLoginAt = "Never"
        )
        _members.value = _members.value + newMember
    }

    fun updateMemberCredentials(memberId: String, newUserId: String) {
        _members.value = _members.value.map {
            if (it.id == memberId) it.copy(loginUserId = newUserId) else it
        }
    }

    fun updateAdminCredentials(currentPass: String, newAdminId: String, newName: String, newPass: String): Pair<Boolean, String?> {
        if (newAdminId.isBlank() || newName.isBlank()) {
            return false to "Admin ID and Name cannot be empty."
        }
        val currentAdmin = _adminAccount.value
        val updated = currentAdmin.copy(
            adminId = newAdminId.trim(),
            name = newName.trim()
        )
        _adminAccount.value = updated

        if (_currentUserRole.value is CurrentUserRole.SuperAdmin) {
            val cur = _currentUserRole.value as CurrentUserRole.SuperAdmin
            _currentUserRole.value = cur.copy(
                userId = updated.adminId,
                name = updated.name
            )
        }
        return true to null
    }

    fun removeMember(memberId: String) {
        _members.value = _members.value.filter { it.id != memberId }
    }
}

package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.CrmRepository
import com.example.model.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class TeamPerformance(
    val team: Team,
    val memberCount: Int,
    val totalLeads: Int,
    val totalConversions: Int,
    val totalSeniorityRevenue: Double,
    val conversionRate: Double,
    val targetAchievement: Double
)

data class StaffAuditProgress(
    val member: TeamMember,
    val teamName: String,
    val totalTasks: Int,
    val completedTasks: Int,
    val inProgressTasks: Int,
    val pendingTasks: Int,
    val completionPercentage: Float
)

sealed class ActiveModalDialog {
    object None : ActiveModalDialog()
    object AddLead : ActiveModalDialog()
    data class ConvertLead(val lead: Lead) : ActiveModalDialog()
    object AddConfirmedGuest : ActiveModalDialog()
    object AddDailyTask : ActiveModalDialog()
    data class EditDailyTask(val task: DailyTask) : ActiveModalDialog()
    object DailyTaskClosing : ActiveModalDialog()
    object AddCounsellingLog : ActiveModalDialog()
    object AddSalesTransaction : ActiveModalDialog()
    object AddTeam : ActiveModalDialog()
    data class AddMember(val preselectedTeamId: String? = null) : ActiveModalDialog()
    data class EditMemberCredentials(val member: TeamMember) : ActiveModalDialog()
    data class LeadDetails(val lead: Lead) : ActiveModalDialog()
    object ViewSecurityPolicies : ActiveModalDialog()
    data class ViewClosureProof(val transaction: SalesTransaction) : ActiveModalDialog()
    object ViewNotifications : ActiveModalDialog()
}

class CrmViewModel(
    val repository: CrmRepository = CrmRepository()
) : ViewModel() {

    val isUserLoggedIn = repository.isUserLoggedIn
    val currentUserRole = repository.currentUserRole
    val teams = repository.teams
    val members = repository.members
    val leads = repository.leads
    val confirmedGuests = repository.confirmedGuests
    val dailyTasks = repository.dailyTasks
    val dailyClosingRecords = repository.dailyClosingRecords
    val counsellingLogs = repository.counsellingLogs
    val salesTransactions = repository.salesTransactions
    val celebrations = repository.celebrations
    val selectedTeamFilter = repository.selectedTeamFilter
    val allNotifications = repository.notifications
    val offlineCacheInfo: StateFlow<OfflineCacheInfo> = repository.offlineCacheInfo

    fun syncAllToRoomCache() {
        repository.syncAllToRoomCache()
        showSnackbar("Room offline cache synced! All sales and leads available offline.")
    }

    fun toggleOfflineModeSimulation() {
        repository.toggleOfflineModeSimulation()
        val isOffline = repository.offlineCacheInfo.value.isOfflineModeActive
        if (isOffline) {
            showSnackbar("Offline Simulation Enabled: Serving cached leads & sales from Room DB")
        } else {
            showSnackbar("Live Online Mode restored: Connected to server")
        }
    }


    // Notifications scoped to current role (Distributor gets their own and their team's notifications; SuperAdmin gets all)
    val notifications: StateFlow<List<AppNotification>> = combine(
        allNotifications,
        currentUserRole,
        selectedTeamFilter
    ) { notifs, role, teamFilter ->
        notifs.filter { notif ->
            when (role) {
                is CurrentUserRole.SuperAdmin -> teamFilter == null || notif.forTeamId == teamFilter || notif.forTeamId == null
                is CurrentUserRole.Telecaller -> notif.forMemberId == role.id || notif.forTeamId == role.teamId || (notif.forMemberId == null && notif.forTeamId == null)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    // Unread notification counts for badges
    val unreadNotificationCount: StateFlow<Int> = notifications.map { list ->
        list.count { !it.isRead }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    val unreadLeadsBadgeCount: StateFlow<Int> = notifications.map { list ->
        list.count { !it.isRead && it.type == NotificationType.NEW_LEAD }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    val unreadTasksBadgeCount: StateFlow<Int> = notifications.map { list ->
        list.count { !it.isRead && it.type == NotificationType.UPCOMING_TASK }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    private val _toastNotification = MutableStateFlow<AppNotification?>(null)
    val toastNotification = _toastNotification.asStateFlow()

    fun dismissToastNotification() {
        _toastNotification.value = null
    }

    fun markNotificationAsRead(id: String) {
        repository.markNotificationAsRead(id)
    }

    fun markAllNotificationsAsRead() {
        val role = currentUserRole.value
        val memberId = when (role) {
            is CurrentUserRole.Telecaller -> role.id
            is CurrentUserRole.SuperAdmin -> null
        }
        val teamId = when (role) {
            is CurrentUserRole.Telecaller -> role.teamId
            is CurrentUserRole.SuperAdmin -> null
        }
        repository.markAllNotificationsAsRead(memberId, teamId, role.isSuperAdmin)
        showSnackbar("All notifications marked as read")
    }

    fun clearAllNotifications() {
        repository.clearNotifications()
        showSnackbar("Notifications cleared")
    }

    // Theme Mode (Professional Light vs Field Sales High-Contrast Dark)
    val themeMode: StateFlow<ThemeMode> = repository.themeMode

    fun setThemeMode(mode: ThemeMode) {
        repository.setThemeMode(mode)
        showSnackbar("Switched to ${mode.title}")
    }

    fun toggleThemeMode() {
        val newMode = repository.toggleThemeMode()
        val note = if (newMode == ThemeMode.DARK_HIGH_CONTRAST) {
            "Field Sales High-Contrast Mode activated (Optimized for outdoor field calls & sunlight readability)"
        } else {
            "Professional Light Mode activated (Office & daylight clarity)"
        }
        showSnackbar(note)
    }

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _selectedLeadStatusFilter = MutableStateFlow<LeadStatus?>(null)
    val selectedLeadStatusFilter = _selectedLeadStatusFilter.asStateFlow()

    private val _activeModal = MutableStateFlow<ActiveModalDialog>(ActiveModalDialog.None)
    val activeModal = _activeModal.asStateFlow()

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage = _snackbarMessage.asStateFlow()

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setLeadStatusFilter(status: LeadStatus?) {
        _selectedLeadStatusFilter.value = status
    }

    fun setTeamFilter(teamId: String?) {
        repository.setTeamFilter(teamId)
    }

    fun switchRole(role: CurrentUserRole) {
        repository.switchRole(role)
        showSnackbar("Switched to role: ${role.displayName}")
    }

    fun openModal(modal: ActiveModalDialog) {
        _activeModal.value = modal
    }

    fun closeModal() {
        _activeModal.value = ActiveModalDialog.None
    }

    fun showSnackbar(msg: String) {
        _snackbarMessage.value = msg
    }

    fun clearSnackbar() {
        _snackbarMessage.value = null
    }

    // Filtered Leads (Strict Team Data Isolation)
    val filteredLeads: StateFlow<List<Lead>> = combine(
        leads,
        currentUserRole,
        selectedTeamFilter,
        selectedLeadStatusFilter,
        searchQuery
    ) { allLeads, role, teamFilter, statusFilter, query ->
        allLeads.filter { lead ->
            val matchesRole = when (role) {
                is CurrentUserRole.SuperAdmin -> true
                is CurrentUserRole.Telecaller -> lead.teamId == role.teamId
            }
            val matchesTeam = teamFilter == null || lead.teamId == teamFilter
            val matchesStatus = statusFilter == null || lead.status == statusFilter
            val matchesQuery = query.isBlank() ||
                    lead.name.contains(query, ignoreCase = true) ||
                    lead.phone.contains(query) ||
                    lead.courseOrProgram.contains(query, ignoreCase = true) ||
                    lead.city.contains(query, ignoreCase = true)

            matchesRole && matchesTeam && matchesStatus && matchesQuery
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    // Filtered Confirmed Guests (Strict Team Data Isolation)
    val filteredConfirmedGuests: StateFlow<List<ConfirmedGuest>> = combine(
        confirmedGuests,
        currentUserRole,
        selectedTeamFilter
    ) { allGuests, role, teamFilter ->
        allGuests.filter { guest ->
            when (role) {
                is CurrentUserRole.SuperAdmin -> teamFilter == null || guest.teamId == teamFilter
                is CurrentUserRole.Telecaller -> guest.teamId == role.teamId
            }
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    // Filtered Daily Tasks (Strict Team Data Isolation)
    val filteredDailyTasks: StateFlow<List<DailyTask>> = combine(
        dailyTasks,
        currentUserRole,
        selectedTeamFilter
    ) { allTasks, role, teamFilter ->
        allTasks.filter { task ->
            when (role) {
                is CurrentUserRole.SuperAdmin -> teamFilter == null || task.teamId == teamFilter
                is CurrentUserRole.Telecaller -> task.teamId == role.teamId
            }
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    // Filtered Counselling Logs (Strict Team Data Isolation)
    val filteredCounsellingLogs: StateFlow<List<CounsellingLog>> = combine(
        counsellingLogs,
        currentUserRole,
        selectedTeamFilter
    ) { allLogs, role, teamFilter ->
        allLogs.filter { log ->
            when (role) {
                is CurrentUserRole.SuperAdmin -> teamFilter == null || log.teamId == teamFilter
                is CurrentUserRole.Telecaller -> log.teamId == role.teamId
            }
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    // Filtered Sales Transactions (Strict Team Data Isolation)
    val filteredSalesTransactions: StateFlow<List<SalesTransaction>> = combine(
        salesTransactions,
        currentUserRole,
        selectedTeamFilter
    ) { allTxs, role, teamFilter ->
        allTxs.filter { tx ->
            when (role) {
                is CurrentUserRole.SuperAdmin -> teamFilter == null || tx.teamId == teamFilter
                is CurrentUserRole.Telecaller -> tx.teamId == role.teamId
            }
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    // Filtered Closure Celebrations (Strict Team Data Isolation)
    val filteredCelebrations: StateFlow<List<ClosureCelebration>> = combine(
        celebrations,
        currentUserRole,
        selectedTeamFilter
    ) { allCelebs, role, teamFilter ->
        allCelebs.filter { celeb ->
            when (role) {
                is CurrentUserRole.SuperAdmin -> teamFilter == null || celeb.teamId == teamFilter
                is CurrentUserRole.Telecaller -> celeb.teamId == role.teamId
            }
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    // Team Performance computation for Leaderboard
    val teamPerformances: StateFlow<List<TeamPerformance>> = combine(
        teams,
        members,
        leads,
        salesTransactions,
        currentUserRole
    ) { tms, mbrs, lds, txs, role ->
        val allPerfs = tms.map { team ->
            val teamMembers = mbrs.filter { it.teamId == team.id }
            val teamLeads = lds.filter { it.teamId == team.id }
            val teamConversions = teamLeads.count { it.status == LeadStatus.CONVERTED }
            val teamTransactions = txs.filter { it.teamId == team.id }
            val totalRev = teamTransactions.sumOf { it.seniorityAmount }
            val convRate = if (teamLeads.isNotEmpty()) (teamConversions.toDouble() / teamLeads.size.toDouble()) * 100 else 0.0
            val targetPercent = if (team.targetMonthlyRevenue > 0) (totalRev / team.targetMonthlyRevenue) * 100 else 0.0

            TeamPerformance(
                team = team,
                memberCount = teamMembers.size,
                totalLeads = teamLeads.size,
                totalConversions = teamConversions,
                totalSeniorityRevenue = totalRev,
                conversionRate = convRate,
                targetAchievement = targetPercent
            )
        }.sortedByDescending { it.totalSeniorityRevenue }

        when (role) {
            is CurrentUserRole.SuperAdmin -> allPerfs
            is CurrentUserRole.Telecaller -> allPerfs.filter { it.team.id == role.teamId }
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    // Staff Daily Task Audit (Strict Team Isolation for Telecallers)
    val staffAuditProgressList: StateFlow<List<StaffAuditProgress>> = combine(
        members,
        teams,
        dailyTasks,
        currentUserRole
    ) { mbrs, tms, tsks, role ->
        val teamMap = tms.associate { it.id to it.name }
        val visibleMembers = when (role) {
            is CurrentUserRole.SuperAdmin -> mbrs
            is CurrentUserRole.Telecaller -> mbrs.filter { it.teamId == role.teamId }
        }

        visibleMembers.map { member ->
            val memberTasks = tsks.filter { it.assignedToMemberId == member.id }
            val total = memberTasks.size
            val completed = memberTasks.count { it.status == TaskStatus.COMPLETED }
            val inProgress = memberTasks.count { it.status == TaskStatus.IN_PROGRESS }
            val pending = memberTasks.count { it.status == TaskStatus.PENDING }
            val percentage = if (total > 0) (completed.toFloat() / total.toFloat()) else 0f

            StaffAuditProgress(
                member = member,
                teamName = teamMap[member.teamId] ?: "General Team",
                totalTasks = total,
                completedTasks = completed,
                inProgressTasks = inProgress,
                pendingTasks = pending,
                completionPercentage = percentage
            )
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    // Operations delegated to repository
    fun updateLeadStatus(leadId: String, status: LeadStatus) {
        repository.updateLeadStatus(leadId, status)
        showSnackbar("Lead status updated to ${status.label}")
    }

    fun convertLeadToConfirmedGuest(leadId: String, visitDateTime: String, counsellor: String, location: String, notes: String) {
        repository.convertLeadToConfirmedGuest(leadId, visitDateTime, counsellor, location, notes)
        showSnackbar("Lead successfully converted to Confirmed Guest!")
        closeModal()
    }

    fun updateGuestStatus(guestId: String, status: GuestStatus) {
        repository.updateGuestStatus(guestId, status)
        showSnackbar("Guest status changed to ${status.label}")
    }

    fun toggleTaskStatus(taskId: String, currentStatus: TaskStatus) {
        val nextStatus = when (currentStatus) {
            TaskStatus.PENDING -> TaskStatus.COMPLETED
            TaskStatus.IN_PROGRESS -> TaskStatus.COMPLETED
            TaskStatus.COMPLETED -> TaskStatus.PENDING
        }
        repository.updateTaskStatus(taskId, nextStatus)
        showSnackbar(if (nextStatus == TaskStatus.COMPLETED) "Task completed! 👍" else "Task marked incomplete / pending 👎")
    }

    fun setTaskCompleted(taskId: String, completed: Boolean) {
        repository.setTaskCompletion(taskId, completed)
        showSnackbar(if (completed) "Task completed! 👍" else "Task marked incomplete! 👎")
    }

    fun closeDailyTasks(remarks: String) {
        val closerName = currentUserRole.value.displayName
        val record = repository.closeDailyTasks(closerName, remarks)
        showSnackbar("Daily Task Closing finalized! (${record.completedCount} 👍 Completed, ${record.incompleteCount} 👎 Incomplete)")
        closeModal()
    }

    fun login(userIdInput: String, passInput: String): Pair<Boolean, String?> {
        val result = repository.login(userIdInput, passInput)
        if (result.first) {
            val user = repository.currentUserRole.value
            showSnackbar("Welcome back, ${user.displayName}!")
        }
        return result
    }

    fun logout() {
        repository.logout()
        showSnackbar("Logged out successfully.")
    }

    fun updateMemberCredentials(memberId: String, newUserId: String, newPass: String) {
        repository.updateMemberCredentials(memberId, newUserId, newPass)
        showSnackbar("Distributor login credentials updated!")
        closeModal()
    }

    fun approveTransaction(txId: String) {
        repository.approveTransaction(txId)
        showSnackbar("Transaction approved by Super Admin!")
    }

    fun addLead(name: String, phone: String, email: String, city: String, course: String, teamId: String, notes: String) {
        val assignedMember = members.value.firstOrNull { it.teamId == teamId } ?: members.value.first()
        val newLead = Lead(
            id = "lead-${System.currentTimeMillis().toString().takeLast(5)}",
            name = name,
            phone = phone,
            email = email,
            city = city,
            courseOrProgram = course,
            status = LeadStatus.NEW,
            assignedTelecallerId = assignedMember.id,
            assignedTelecallerName = assignedMember.name,
            teamId = teamId,
            notes = notes,
            createdDate = "Today",
            lastCallDate = "Pending First Call"
        )
        repository.addLead(newLead)
        _toastNotification.value = AppNotification(
            id = "notif-lead-${System.currentTimeMillis()}",
            title = "New Lead Assigned",
            message = "$name ($course) assigned to ${assignedMember.name}",
            timestamp = "Just now",
            type = NotificationType.NEW_LEAD,
            targetDestination = "LEADS",
            forMemberId = assignedMember.id,
            forTeamId = teamId,
            isRead = false
        )
        showSnackbar("New lead assigned to ${assignedMember.name}!")
        closeModal()
    }

    fun addConfirmedGuest(name: String, phone: String, telecaller: String, visitDateTime: String, counsellor: String, location: String, notes: String, teamId: String = "team-1") {
        val guest = ConfirmedGuest(
            id = "guest-${System.currentTimeMillis().toString().takeLast(5)}",
            teamId = teamId,
            guestName = name,
            phone = phone,
            assignedTelecallerName = telecaller,
            visitDateTime = visitDateTime,
            assignedCounsellor = counsellor,
            status = GuestStatus.SCHEDULED,
            locationOrRoom = location,
            notes = notes
        )
        repository.addConfirmedGuest(guest)
        showSnackbar("Guest visit successfully scheduled!")
        closeModal()
    }

    fun addTask(
        title: String,
        memberId: String,
        priority: String,
        dueTime: String,
        description: String = "",
        isSelfCreated: Boolean = true
    ) {
        val member = members.value.find { it.id == memberId } ?: members.value.first()
        val task = DailyTask(
            id = "task-${System.currentTimeMillis().toString().takeLast(5)}",
            title = title,
            description = description,
            assignedToMemberId = member.id,
            assignedToMemberName = member.name,
            teamId = member.teamId,
            status = TaskStatus.PENDING,
            priority = priority,
            dueTime = dueTime,
            isSelfCreated = isSelfCreated
        )
        repository.addTask(task)
        _toastNotification.value = AppNotification(
            id = "notif-task-${System.currentTimeMillis()}",
            title = "Upcoming Follow-up Task",
            message = "$title (Due: $dueTime) for ${member.name}",
            timestamp = "Just now",
            type = NotificationType.UPCOMING_TASK,
            targetDestination = "TASKS",
            forMemberId = member.id,
            forTeamId = member.teamId,
            isRead = false
        )
        showSnackbar(if (isSelfCreated) "Own daily task created! 📝" else "Task assigned to ${member.name}!")
        closeModal()
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
        repository.updateTask(taskId, title, description, priority, dueTime, status, closingRemarks)
        showSnackbar("Task updated successfully! ✅")
        closeModal()
    }

    fun deleteTask(taskId: String) {
        repository.deleteTask(taskId)
        showSnackbar("Task removed from daily checklist.")
        closeModal()
    }

    fun addCounsellingLog(counsellor: String, candidate: String, phone: String, outcome: CounsellingOutcome, notes: String, recordedBy: String, teamId: String = "team-1") {
        val teamName = teams.value.find { it.id == teamId }?.name ?: "Alpha Warriors"
        val log = CounsellingLog(
            id = "counsel-${System.currentTimeMillis().toString().takeLast(5)}",
            teamId = teamId,
            teamName = teamName,
            dateTime = "Today, Now",
            counsellorName = counsellor,
            candidateName = candidate,
            candidatePhone = phone,
            outcome = outcome,
            keyDiscussion = notes,
            recordedByMemberName = recordedBy
        )
        repository.addCounsellingLog(log)
        showSnackbar("Counselling session logged!")
        closeModal()
    }

    fun addSalesTransaction(
        candidate: String,
        phone: String,
        amount: Double,
        mode: String,
        ref: String,
        teamId: String,
        agent: String,
        closureProofImageUrl: String? = null
    ) {
        val team = teams.value.find { it.id == teamId } ?: teams.value.first()
        val tx = SalesTransaction(
            id = "tx-${System.currentTimeMillis().toString().takeLast(5)}",
            candidateName = candidate,
            candidatePhone = phone,
            seniorityAmount = amount,
            paymentMode = mode,
            transactionRef = ref,
            receiptNumber = "REC-${System.currentTimeMillis().toString().takeLast(4)}",
            teamId = team.id,
            teamName = team.name,
            agentName = agent,
            closureProofImageUrl = closureProofImageUrl,
            storageBucket = "sales-proofs",
            date = "Today",
            approvalStatus = if (currentUserRole.value.isSuperAdmin) ApprovalStatus.APPROVED else ApprovalStatus.PENDING
        )
        repository.addSalesTransaction(tx)
        showSnackbar("Seniority transaction recorded with proof photo!")
        closeModal()
    }

    fun addTeam(name: String, leadName: String, email: String, phone: String, target: Double) {
        repository.addTeam(name, leadName, email, phone, target)
        showSnackbar("Team '$name' created successfully!")
        closeModal()
    }

    fun toggleArchiveTeam(teamId: String) {
        repository.toggleArchiveTeam(teamId)
        showSnackbar("Team archive status toggled.")
    }

    fun deleteTeam(teamId: String) {
        repository.deleteTeam(teamId)
        showSnackbar("Team deleted.")
    }

    fun addMember(name: String, teamId: String, role: String, email: String, phone: String, avatarUrl: String? = null) {
        repository.addMember(name, teamId, role, email, phone, avatarUrl)
        showSnackbar("Member '$name' added to team!")
        closeModal()
    }

    fun removeMember(memberId: String) {
        repository.removeMember(memberId)
        showSnackbar("Member removed.")
    }
}

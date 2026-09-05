package com.example.model

sealed class CurrentUserRole {
    data class SuperAdmin(
        val id: String = "admin-001",
        val name: String = "Mani (Super Admin)",
        val email: String = "d.s.mani407@gmail.com",
        val userId: String = "admin",
        val currentLoginAt: String = "04 Sep 2026, 08:58 AM",
        val lastLoginAt: String = "03 Sep 2026, 07:15 PM"
    ) : CurrentUserRole()

    data class Telecaller(
        val id: String,
        val name: String,
        val teamId: String,
        val teamName: String,
        val email: String = "",
        val userId: String = "",
        val currentLoginAt: String = "04 Sep 2026, 08:58 AM",
        val lastLoginAt: String = "03 Sep 2026, 06:45 PM"
    ) : CurrentUserRole()

    val displayName: String
        get() = when (this) {
            is SuperAdmin -> name
            is Telecaller -> "$name ($teamName)"
        }

    val isSuperAdmin: Boolean
        get() = this is SuperAdmin

    val roleName: String
        get() = when (this) {
            is SuperAdmin -> "ADMIN"
            is Telecaller -> "DISTRIBUTOR"
        }

    val currentSessionLogin: String
        get() = when (this) {
            is SuperAdmin -> currentLoginAt
            is Telecaller -> currentLoginAt
        }

    val previousSessionLogin: String
        get() = when (this) {
            is SuperAdmin -> lastLoginAt
            is Telecaller -> lastLoginAt
        }

    val loginId: String
        get() = when (this) {
            is SuperAdmin -> userId
            is Telecaller -> if (userId.isNotBlank()) userId else id
        }
}

data class Team(
    val id: String,
    val name: String,
    val leadName: String,
    val leadEmail: String,
    val leadPhone: String,
    val colorHex: Long = 0xFF4F46E5,
    val targetMonthlyRevenue: Double = 500000.0,
    val isArchived: Boolean = false
)

data class TeamMember(
    val id: String,
    val teamId: String,
    val name: String,
    val role: String = "Distributor / Advisor",
    val email: String,
    val phone: String,
    val avatarInitials: String,
    val avatarColorHex: Long = 0xFF6366F1,
    val avatarUrl: String? = null,
    val activeLeadsCount: Int = 12,
    val conversionsCount: Int = 5,
    val totalClosedAmount: Double = 125000.0,
    val loginUserId: String = "",
    val loginPassword: String = "pass123",
    val currentLoginAt: String = "04 Sep 2026, 08:58 AM",
    val lastLoginAt: String = "03 Sep 2026, 06:45 PM"
)

enum class LeadStatus(val label: String) {
    NEW("New"),
    FOLLOW_UP("Follow-up"),
    CALL_BACK("Call Back"),
    DISQUALIFIED("Disqualified"),
    CONVERTED("Converted")
}

data class Lead(
    val id: String,
    val name: String,
    val phone: String,
    val email: String,
    val city: String,
    val courseOrProgram: String,
    val status: LeadStatus,
    val assignedTelecallerId: String,
    val assignedTelecallerName: String,
    val teamId: String,
    val notes: String,
    val createdDate: String,
    val lastCallDate: String
)

enum class GuestStatus(val label: String) {
    SCHEDULED("Scheduled"),
    ARRIVED("Arrived"),
    IN_COUNSELLING("In Counselling"),
    COMPLETED("Completed"),
    RESCHEDULED("Rescheduled")
}

data class ConfirmedGuest(
    val id: String,
    val leadId: String? = null,
    val teamId: String,
    val guestName: String,
    val phone: String,
    val assignedTelecallerName: String,
    val visitDateTime: String,
    val assignedCounsellor: String,
    val status: GuestStatus = GuestStatus.SCHEDULED,
    val locationOrRoom: String = "Counselling Hall A",
    val notes: String = ""
)

enum class TaskStatus(val label: String) {
    PENDING("Incomplete / Pending"),
    IN_PROGRESS("In Progress"),
    COMPLETED("Completed")
}

data class DailyTask(
    val id: String,
    val title: String,
    val description: String = "",
    val assignedToMemberId: String,
    val assignedToMemberName: String,
    val teamId: String,
    val status: TaskStatus,
    val priority: String = "High",
    val dueTime: String = "06:00 PM",
    val isClosed: Boolean = false,
    val closingRemarks: String? = null,
    val isSelfCreated: Boolean = true
) {
    val isCompleted: Boolean
        get() = status == TaskStatus.COMPLETED
}

data class DailyTaskClosingRecord(
    val id: String,
    val date: String,
    val closedBy: String,
    val totalTasks: Int,
    val completedCount: Int,
    val incompleteCount: Int,
    val closingRemarks: String,
    val closedAt: String
)

data class UserCredential(
    val userId: String,
    val password: String,
    val role: CurrentUserRole,
    val distributorName: String
)

enum class CounsellingOutcome(val label: String) {
    INTERESTED("Interested"),
    FOLLOW_UP("Follow-up"),
    CLOSED("Closed")
}

data class CounsellingLog(
    val id: String,
    val teamId: String,
    val teamName: String,
    val dateTime: String,
    val counsellorName: String, // "Councelling Trainer Name"
    val candidateName: String,  // "Councelling attend Name"
    val candidatePhone: String,
    val outcome: CounsellingOutcome,
    val keyDiscussion: String,
    val recordedByMemberName: String
)

enum class ApprovalStatus(val label: String) {
    PENDING("Pending Approval"),
    APPROVED("Approved")
}

data class SalesTransaction(
    val id: String,
    val candidateName: String,
    val candidatePhone: String,
    val seniorityAmount: Double, // "Seniority / Advance Amount"
    val paymentMode: String,     // UPI, Net Banking, Cash, Card
    val transactionRef: String,
    val receiptNumber: String,
    val teamId: String,
    val teamName: String,
    val agentName: String,
    val date: String,
    val closureProofImageUrl: String? = null,
    val storageBucket: String = "sales-proofs",
    val approvalStatus: ApprovalStatus = ApprovalStatus.PENDING
)

data class ClosureCelebration(
    val id: String,
    val teamId: String,
    val agentName: String,
    val teamName: String,
    val candidateName: String,
    val closedAmount: Double,
    val date: String,
    val avatarInitials: String,
    val avatarColorHex: Long = 0xFF10B981,
    val closureProofImageUrl: String? = null,
    val badgeTitle: String = "Star Closer"
)

data class SupabaseSecurityRule(
    val tableName: String,
    val policyName: String,
    val operation: String,
    val tamilExplanation: String,
    val sqlCode: String
)

enum class NotificationType {
    NEW_LEAD,
    UPCOMING_TASK
}

data class AppNotification(
    val id: String,
    val title: String,
    val message: String,
    val timestamp: String,
    val type: NotificationType,
    val targetDestination: String, // "LEADS" or "TASKS"
    val forMemberId: String? = null, // specific distributor or null for all / team
    val forTeamId: String? = null,
    val isRead: Boolean = false
)

enum class ThemeMode(
    val title: String,
    val subtitle: String,
    val isDark: Boolean
) {
    LIGHT(
        title = "Professional Light",
        subtitle = "Clean, high-clarity daylight theme for indoor & office work",
        isDark = false
    ),
    DARK_HIGH_CONTRAST(
        title = "Field Sales High-Contrast",
        subtitle = "Deep pitch-dark canvas with maximum contrast for outdoor readability during field sales calls",
        isDark = true
    )
}

/**
 * Diagnostic & status information for the Room database offline cache.
 */
data class OfflineCacheInfo(
    val isOfflineModeActive: Boolean = false,
    val lastSyncTimestamp: String = "Just now",
    val cachedLeadsCount: Int = 0,
    val cachedSalesCount: Int = 0,
    val cachedGuestsCount: Int = 0,
    val cachedTasksCount: Int = 0,
    val isSyncing: Boolean = false
)



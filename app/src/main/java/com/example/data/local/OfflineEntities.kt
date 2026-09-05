package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.model.*

/**
 * Room Entity for caching Leads locally on device for offline field calls.
 */
@Entity(tableName = "cached_leads")
data class CachedLeadEntity(
    @PrimaryKey val id: String,
    val name: String,
    val phone: String,
    val email: String,
    val city: String,
    val courseOrProgram: String,
    val status: String,
    val assignedTelecallerId: String,
    val assignedTelecallerName: String,
    val teamId: String,
    val notes: String,
    val createdDate: String,
    val lastCallDate: String,
    val cachedTimestamp: Long = System.currentTimeMillis()
) {
    fun toDomain(): Lead {
        val domainStatus = try {
            LeadStatus.valueOf(status)
        } catch (e: Exception) {
            LeadStatus.NEW
        }
        return Lead(
            id = id,
            name = name,
            phone = phone,
            email = email,
            city = city,
            courseOrProgram = courseOrProgram,
            status = domainStatus,
            assignedTelecallerId = assignedTelecallerId,
            assignedTelecallerName = assignedTelecallerName,
            teamId = teamId,
            notes = notes,
            createdDate = createdDate,
            lastCallDate = lastCallDate
        )
    }

    companion object {
        fun fromDomain(lead: Lead, timestamp: Long = System.currentTimeMillis()): CachedLeadEntity {
            return CachedLeadEntity(
                id = lead.id,
                name = lead.name,
                phone = lead.phone,
                email = lead.email,
                city = lead.city,
                courseOrProgram = lead.courseOrProgram,
                status = lead.status.name,
                assignedTelecallerId = lead.assignedTelecallerId,
                assignedTelecallerName = lead.assignedTelecallerName,
                teamId = lead.teamId,
                notes = lead.notes,
                createdDate = lead.createdDate,
                lastCallDate = lead.lastCallDate,
                cachedTimestamp = timestamp
            )
        }
    }
}

/**
 * Room Entity for caching Sales and Seniority Transactions locally.
 */
@Entity(tableName = "cached_sales_transactions")
data class CachedSalesTransactionEntity(
    @PrimaryKey val id: String,
    val candidateName: String,
    val candidatePhone: String,
    val seniorityAmount: Double,
    val paymentMode: String,
    val transactionRef: String,
    val receiptNumber: String,
    val teamId: String,
    val teamName: String,
    val agentName: String,
    val date: String,
    val closureProofImageUrl: String? = null,
    val storageBucket: String = "sales-proofs",
    val approvalStatus: String = "PENDING",
    val cachedTimestamp: Long = System.currentTimeMillis()
) {
    fun toDomain(): SalesTransaction {
        val status = try {
            ApprovalStatus.valueOf(approvalStatus)
        } catch (e: Exception) {
            ApprovalStatus.PENDING
        }
        return SalesTransaction(
            id = id,
            candidateName = candidateName,
            candidatePhone = candidatePhone,
            seniorityAmount = seniorityAmount,
            paymentMode = paymentMode,
            transactionRef = transactionRef,
            receiptNumber = receiptNumber,
            teamId = teamId,
            teamName = teamName,
            agentName = agentName,
            date = date,
            closureProofImageUrl = closureProofImageUrl,
            storageBucket = storageBucket,
            approvalStatus = status
        )
    }

    companion object {
        fun fromDomain(tx: SalesTransaction, timestamp: Long = System.currentTimeMillis()): CachedSalesTransactionEntity {
            return CachedSalesTransactionEntity(
                id = tx.id,
                candidateName = tx.candidateName,
                candidatePhone = tx.candidatePhone,
                seniorityAmount = tx.seniorityAmount,
                paymentMode = tx.paymentMode,
                transactionRef = tx.transactionRef,
                receiptNumber = tx.receiptNumber,
                teamId = tx.teamId,
                teamName = tx.teamName,
                agentName = tx.agentName,
                date = tx.date,
                closureProofImageUrl = tx.closureProofImageUrl,
                storageBucket = tx.storageBucket,
                approvalStatus = tx.approvalStatus.name,
                cachedTimestamp = timestamp
            )
        }
    }
}

/**
 * Room Entity for caching Confirmed Scheduled Visits / Guests.
 */
@Entity(tableName = "cached_guests")
data class CachedConfirmedGuestEntity(
    @PrimaryKey val id: String,
    val leadId: String? = null,
    val teamId: String,
    val guestName: String,
    val phone: String,
    val assignedTelecallerName: String,
    val visitDateTime: String,
    val assignedCounsellor: String,
    val status: String = "SCHEDULED",
    val locationOrRoom: String = "Counselling Hall A",
    val notes: String = "",
    val cachedTimestamp: Long = System.currentTimeMillis()
) {
    fun toDomain(): ConfirmedGuest {
        val guestStatus = try {
            GuestStatus.valueOf(status)
        } catch (e: Exception) {
            GuestStatus.SCHEDULED
        }
        return ConfirmedGuest(
            id = id,
            leadId = leadId,
            teamId = teamId,
            guestName = guestName,
            phone = phone,
            assignedTelecallerName = assignedTelecallerName,
            visitDateTime = visitDateTime,
            assignedCounsellor = assignedCounsellor,
            status = guestStatus,
            locationOrRoom = locationOrRoom,
            notes = notes
        )
    }

    companion object {
        fun fromDomain(guest: ConfirmedGuest, timestamp: Long = System.currentTimeMillis()): CachedConfirmedGuestEntity {
            return CachedConfirmedGuestEntity(
                id = guest.id,
                leadId = guest.leadId,
                teamId = guest.teamId,
                guestName = guest.guestName,
                phone = guest.phone,
                assignedTelecallerName = guest.assignedTelecallerName,
                visitDateTime = guest.visitDateTime,
                assignedCounsellor = guest.assignedCounsellor,
                status = guest.status.name,
                locationOrRoom = guest.locationOrRoom,
                notes = guest.notes,
                cachedTimestamp = timestamp
            )
        }
    }
}

/**
 * Room Entity for caching Daily Tasks so distributors can review their work offline.
 */
@Entity(tableName = "cached_daily_tasks")
data class CachedDailyTaskEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String = "",
    val assignedToMemberId: String,
    val assignedToMemberName: String,
    val teamId: String,
    val status: String,
    val priority: String = "High",
    val dueTime: String = "06:00 PM",
    val isClosed: Boolean = false,
    val closingRemarks: String? = null,
    val isSelfCreated: Boolean = true,
    val cachedTimestamp: Long = System.currentTimeMillis()
) {
    fun toDomain(): DailyTask {
        val taskStatus = try {
            TaskStatus.valueOf(status)
        } catch (e: Exception) {
            TaskStatus.PENDING
        }
        return DailyTask(
            id = id,
            title = title,
            description = description,
            assignedToMemberId = assignedToMemberId,
            assignedToMemberName = assignedToMemberName,
            teamId = teamId,
            status = taskStatus,
            priority = priority,
            dueTime = dueTime,
            isClosed = isClosed,
            closingRemarks = closingRemarks,
            isSelfCreated = isSelfCreated
        )
    }

    companion object {
        fun fromDomain(task: DailyTask, timestamp: Long = System.currentTimeMillis()): CachedDailyTaskEntity {
            return CachedDailyTaskEntity(
                id = task.id,
                title = task.title,
                description = task.description,
                assignedToMemberId = task.assignedToMemberId,
                assignedToMemberName = task.assignedToMemberName,
                teamId = task.teamId,
                status = task.status.name,
                priority = task.priority,
                dueTime = task.dueTime,
                isClosed = task.isClosed,
                closingRemarks = task.closingRemarks,
                isSelfCreated = task.isSelfCreated,
                cachedTimestamp = timestamp
            )
        }
    }
}

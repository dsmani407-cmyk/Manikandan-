package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CrmDao {

    // --- LEADS ---
    @Query("SELECT * FROM cached_leads ORDER BY cachedTimestamp DESC")
    fun getAllCachedLeads(): Flow<List<CachedLeadEntity>>

    @Query("SELECT * FROM cached_leads WHERE id = :leadId")
    suspend fun getLeadById(leadId: String): CachedLeadEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLeads(leads: List<CachedLeadEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLead(lead: CachedLeadEntity)

    @Query("UPDATE cached_leads SET status = :status WHERE id = :leadId")
    suspend fun updateLeadStatus(leadId: String, status: String)

    @Query("DELETE FROM cached_leads WHERE id = :leadId")
    suspend fun deleteLead(leadId: String)

    @Query("DELETE FROM cached_leads")
    suspend fun clearLeads()

    // --- SALES TRANSACTIONS ---
    @Query("SELECT * FROM cached_sales_transactions ORDER BY cachedTimestamp DESC")
    fun getAllCachedTransactions(): Flow<List<CachedSalesTransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransactions(transactions: List<CachedSalesTransactionEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: CachedSalesTransactionEntity)

    @Query("UPDATE cached_sales_transactions SET approvalStatus = :approvalStatus WHERE id = :txId")
    suspend fun updateTransactionApproval(txId: String, approvalStatus: String)

    @Query("DELETE FROM cached_sales_transactions")
    suspend fun clearTransactions()

    // --- CONFIRMED GUESTS / VISITS ---
    @Query("SELECT * FROM cached_guests ORDER BY cachedTimestamp DESC")
    fun getAllCachedGuests(): Flow<List<CachedConfirmedGuestEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGuests(guests: List<CachedConfirmedGuestEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGuest(guest: CachedConfirmedGuestEntity)

    @Query("UPDATE cached_guests SET status = :status WHERE id = :guestId")
    suspend fun updateGuestStatus(guestId: String, status: String)

    @Query("DELETE FROM cached_guests")
    suspend fun clearGuests()

    // --- DAILY TASKS ---
    @Query("SELECT * FROM cached_daily_tasks ORDER BY cachedTimestamp DESC")
    fun getAllCachedTasks(): Flow<List<CachedDailyTaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTasks(tasks: List<CachedDailyTaskEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: CachedDailyTaskEntity)

    @Query("UPDATE cached_daily_tasks SET status = :status WHERE id = :taskId")
    suspend fun updateTaskStatus(taskId: String, status: String)

    @Query("DELETE FROM cached_daily_tasks WHERE id = :taskId")
    suspend fun deleteTask(taskId: String)

    @Query("DELETE FROM cached_daily_tasks")
    suspend fun clearTasks()

    // --- AGGREGATE STATS ---
    @Query("SELECT COUNT(*) FROM cached_leads")
    suspend fun getCachedLeadsCount(): Int

    @Query("SELECT COUNT(*) FROM cached_sales_transactions")
    suspend fun getCachedTransactionsCount(): Int
}

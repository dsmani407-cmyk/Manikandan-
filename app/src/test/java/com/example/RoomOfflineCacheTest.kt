package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.CrmRepository
import com.example.data.MockDataProvider
import com.example.data.local.*
import com.example.model.ApprovalStatus
import com.example.model.LeadStatus
import com.example.model.SalesTransaction
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class RoomOfflineCacheTest {

    private lateinit var database: CrmDatabase
    private lateinit var dao: CrmDao
    private lateinit var repository: CrmRepository

    @Before
    fun setup() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, CrmDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = database.crmDao()
        dao.clearLeads()
        dao.clearTransactions()
        dao.clearGuests()
        dao.clearTasks()
        repository = CrmRepository(crmDao = dao)
    }

    @After
    fun tearDown() = runBlocking {
        dao.clearLeads()
        dao.clearTransactions()
        database.close()
    }

    @Test
    fun testLeadCachingAndOfflineRetrieval() = runBlocking {
        dao.clearLeads()
        // Insert sample leads into Room cache
        val sampleLead = MockDataProvider.leads.first()
        dao.insertLead(CachedLeadEntity.fromDomain(sampleLead))

        // Retrieve from Room DAO Flow
        val cachedLeads = dao.getAllCachedLeads().first()
        assertFalse("Cached leads should not be empty", cachedLeads.isEmpty())
        assertEquals(sampleLead.name, cachedLeads.first().name)
        assertEquals(sampleLead.phone, cachedLeads.first().phone)

        // Verify status update in cache
        dao.updateLeadStatus(sampleLead.id, LeadStatus.CONVERTED.name)
        val updated = dao.getLeadById(sampleLead.id)
        assertNotNull(updated)
        assertEquals(LeadStatus.CONVERTED.name, updated?.status)
    }

    @Test
    fun testSalesTransactionCachingAndOfflineAccessibility() = runBlocking {
        dao.clearTransactions()
        // Insert sample transaction
        val tx = SalesTransaction(
            id = "tx-offline-1",
            candidateName = "Kavitha R",
            candidatePhone = "+91 99444 55666",
            seniorityAmount = 25000.0,
            paymentMode = "UPI",
            transactionRef = "UPI-OFFLINE-987",
            receiptNumber = "REC-987",
            teamId = "team-1",
            teamName = "Alpha Warriors",
            agentName = "Priya Sharma",
            date = "05 Sep 2026",
            approvalStatus = ApprovalStatus.PENDING
        )

        dao.insertTransaction(CachedSalesTransactionEntity.fromDomain(tx))

        // Retrieve from Room DAO Flow
        val cachedTxs = dao.getAllCachedTransactions().first()
        assertTrue("Cached transactions should not be empty", cachedTxs.isNotEmpty())
        val foundTx = cachedTxs.find { it.id == "tx-offline-1" }
        assertNotNull("Expected transaction tx-offline-1 in cache", foundTx)
        assertEquals("Kavitha R", foundTx?.candidateName)
        assertEquals(25000.0, foundTx?.seniorityAmount ?: 0.0, 0.001)

        // Approve transaction in cache
        dao.updateTransactionApproval("tx-offline-1", ApprovalStatus.APPROVED.name)
        val updatedTxs = dao.getAllCachedTransactions().first()
        val approvedTx = updatedTxs.find { it.id == "tx-offline-1" }
        assertEquals(ApprovalStatus.APPROVED.name, approvedTx?.approvalStatus)
    }

    @Test
    fun testOfflineSyncAndCounts() = runBlocking {
        dao.clearLeads()
        dao.clearTransactions()

        val leadsEntities = MockDataProvider.leads.map { CachedLeadEntity.fromDomain(it) }
        val salesEntities = MockDataProvider.salesTransactions.map { CachedSalesTransactionEntity.fromDomain(it) }

        dao.insertLeads(leadsEntities)
        dao.insertTransactions(salesEntities)

        val leadsCount = dao.getCachedLeadsCount()
        val salesCount = dao.getCachedTransactionsCount()

        assertTrue(leadsCount > 0)
        assertTrue(salesCount > 0)
        assertEquals(MockDataProvider.leads.size, leadsCount)
        assertEquals(MockDataProvider.salesTransactions.size, salesCount)
    }

}

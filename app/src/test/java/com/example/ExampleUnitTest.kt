package com.example

import com.example.data.CrmRepository
import com.example.model.ApprovalStatus
import com.example.model.CurrentUserRole
import com.example.model.LeadStatus
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun crm_leadConversionAndFinanceApproval_works() {
    val repository = CrmRepository()

    // Verify initial leads
    val initialLeads = repository.leads.value
    assertTrue("Leads should not be empty", initialLeads.isNotEmpty())
    val targetLead = initialLeads.first()

    // Transition to confirmed guest
    repository.convertLeadToConfirmedGuest(
      leadId = targetLead.id,
      visitDateTime = "Tomorrow, 02:00 PM",
      assignedCounsellor = "Rajesh Kumar",
      location = "Room 1",
      notes = "Walk-in interview"
    )

    // Check updated lead status
    val updatedLead = repository.leads.value.find { it.id == targetLead.id }
    assertEquals(LeadStatus.CONVERTED, updatedLead?.status)

    // Check guest created
    val guests = repository.confirmedGuests.value
    assertTrue(guests.any { it.guestName == targetLead.name })

    // Finance approval test
    val txs = repository.salesTransactions.value
    val pendingTx = txs.find { it.approvalStatus == ApprovalStatus.PENDING }
    if (pendingTx != null) {
      repository.approveTransaction(pendingTx.id)
      val approvedTx = repository.salesTransactions.value.find { it.id == pendingTx.id }
      assertEquals(ApprovalStatus.APPROVED, approvedTx?.approvalStatus)
    }

    // Role switch test
    repository.switchRole(CurrentUserRole.SuperAdmin())
    assertTrue(repository.currentUserRole.value.isSuperAdmin)

    // Self-typing individual task test
    val initialTasksCount = repository.dailyTasks.value.size
    val newTask = com.example.model.DailyTask(
      id = "test-task-1",
      title = "Call 35 Leads Quota - Self Routine",
      assignedToMemberId = "mem-1",
      assignedToMemberName = "Priya Sharma",
      teamId = "team-1",
      priority = "High",
      dueTime = "05:00 PM",
      status = com.example.model.TaskStatus.PENDING,
      description = "Focus on Chennai North distributors and follow up on registration fees.",
      isSelfCreated = true
    )
    repository.addTask(newTask)

    val updatedTasks = repository.dailyTasks.value
    assertEquals(initialTasksCount + 1, updatedTasks.size)
    val myCreatedTask = updatedTasks.find { it.id == "test-task-1" }
    assertNotNull(myCreatedTask)
    assertTrue(myCreatedTask!!.isSelfCreated)
    assertEquals("Focus on Chennai North distributors and follow up on registration fees.", myCreatedTask.description)

    // Update status to completed (thumbs-up)
    repository.setTaskCompletion(myCreatedTask.id, true)
    val completedTask = repository.dailyTasks.value.find { it.id == myCreatedTask.id }
    assertEquals(com.example.model.TaskStatus.COMPLETED, completedTask?.status)

    // Edit task own writing description & remarks
    repository.updateTask(
      taskId = myCreatedTask.id,
      title = "Call 35 Leads Quota - Completed All",
      priority = "Urgent",
      dueTime = "06:00 PM",
      status = com.example.model.TaskStatus.COMPLETED,
      description = "All 35 contacts reached successfully.",
      closingRemarks = "Exceeded target with 6 conversions 👍"
    )
    val editedTask = repository.dailyTasks.value.find { it.id == myCreatedTask.id }
    assertEquals("Call 35 Leads Quota - Completed All", editedTask?.title)
    assertEquals("Exceeded target with 6 conversions 👍", editedTask?.closingRemarks)

    // Verify distributor account management & user ID update
    val member1 = repository.members.value.find { it.id == "mem-1" }
    assertNotNull(member1)

    // Admin updates distributor User ID
    repository.updateMemberCredentials("mem-1", "priya_super")
    val updatedMember = repository.members.value.find { it.id == "mem-1" }
    assertEquals("priya_super", updatedMember?.loginUserId)
  }
}


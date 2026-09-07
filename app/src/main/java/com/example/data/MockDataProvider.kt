package com.example.data

import com.example.model.*

object MockDataProvider {
    val teams = listOf(
        Team(
            id = "team-1",
            name = "Alpha Warriors",
            leadName = "Rajesh Kumar",
            leadEmail = "rajesh.k@opsplatform.com",
            leadPhone = "+91 98765 43210",
            colorHex = 0xFF4F46E5,
            targetMonthlyRevenue = 600000.0
        ),
        Team(
            id = "team-2",
            name = "Beta Titans",
            leadName = "Deepa Sundaram",
            leadEmail = "deepa.s@opsplatform.com",
            leadPhone = "+91 98450 11223",
            colorHex = 0xFF0284C7,
            targetMonthlyRevenue = 500000.0
        ),
        Team(
            id = "team-3",
            name = "Gamma Strikers",
            leadName = "Vignesh Murugan",
            leadEmail = "vignesh.m@opsplatform.com",
            leadPhone = "+91 97890 99887",
            colorHex = 0xFF059669,
            targetMonthlyRevenue = 450000.0
        )
    )

    val members = listOf(
        TeamMember(
            id = "mem-1",
            teamId = "team-1",
            name = "Priya Sharma",
            role = "Distributor / Senior Telecaller",
            email = "priya.s@opsplatform.com",
            phone = "+91 98111 22334",
            avatarInitials = "PS",
            avatarColorHex = 0xFF4F46E5,
            activeLeadsCount = 28,
            conversionsCount = 8,
            totalClosedAmount = 180000.0,
            loginUserId = "priya",
            currentLoginAt = "04 Sep 2026, 08:30 AM",
            lastLoginAt = "03 Sep 2026, 06:15 PM"
        ),
        TeamMember(
            id = "mem-2",
            teamId = "team-1",
            name = "Arun Prakash",
            role = "Distributor / Advisor",
            email = "arun.p@opsplatform.com",
            phone = "+91 98222 33445",
            avatarInitials = "AP",
            avatarColorHex = 0xFF6366F1,
            activeLeadsCount = 22,
            conversionsCount = 5,
            totalClosedAmount = 110000.0,
            loginUserId = "arun",
            currentLoginAt = "04 Sep 2026, 08:45 AM",
            lastLoginAt = "03 Sep 2026, 05:50 PM"
        ),
        TeamMember(
            id = "mem-3",
            teamId = "team-2",
            name = "Karthik Raja",
            role = "Distributor / Senior Advisor",
            email = "karthik.r@opsplatform.com",
            phone = "+91 98333 44556",
            avatarInitials = "KR",
            avatarColorHex = 0xFF0284C7,
            activeLeadsCount = 31,
            conversionsCount = 9,
            totalClosedAmount = 215000.0,
            loginUserId = "karthik",
            currentLoginAt = "04 Sep 2026, 09:00 AM",
            lastLoginAt = "03 Sep 2026, 07:10 PM"
        ),
        TeamMember(
            id = "mem-4",
            teamId = "team-2",
            name = "Sneha Lakshmi",
            role = "Distributor / Advisor",
            email = "sneha.l@opsplatform.com",
            phone = "+91 98444 55667",
            avatarInitials = "SL",
            avatarColorHex = 0xFF0EA5E9,
            activeLeadsCount = 19,
            conversionsCount = 4,
            totalClosedAmount = 90000.0,
            loginUserId = "sneha",
            currentLoginAt = "04 Sep 2026, 08:15 AM",
            lastLoginAt = "02 Sep 2026, 06:30 PM"
        ),
        TeamMember(
            id = "mem-5",
            teamId = "team-3",
            name = "Divya Menon",
            role = "Distributor / Counselling Lead",
            email = "divya.m@opsplatform.com",
            phone = "+91 98555 66778",
            avatarInitials = "DM",
            avatarColorHex = 0xFF059669,
            activeLeadsCount = 25,
            conversionsCount = 6,
            totalClosedAmount = 145000.0,
            loginUserId = "divya",
            currentLoginAt = "04 Sep 2026, 08:50 AM",
            lastLoginAt = "03 Sep 2026, 06:40 PM"
        )
    )

    val leads = listOf(
        Lead(
            id = "lead-101",
            name = "Ramesh Venkatesan",
            phone = "+91 98401 23456",
            email = "ramesh.v@gmail.com",
            city = "Chennai",
            courseOrProgram = "Executive PG - AI & Machine Learning",
            status = LeadStatus.FOLLOW_UP,
            assignedTelecallerId = "mem-1",
            assignedTelecallerName = "Priya Sharma",
            teamId = "team-1",
            notes = "Interested in weekend classroom batch. Needs syllabus comparison.",
            createdDate = "02 Sep 2026",
            lastCallDate = "04 Sep 2026, 11:30 AM"
        ),
        Lead(
            id = "lead-102",
            name = "Kavitha Sundar",
            phone = "+91 98402 34567",
            email = "kavitha.s@outlook.com",
            city = "Coimbatore",
            courseOrProgram = "Full Stack Cloud Native Dev",
            status = LeadStatus.NEW,
            assignedTelecallerId = "mem-1",
            assignedTelecallerName = "Priya Sharma",
            teamId = "team-1",
            notes = "Enquiry from Instagram campaign. Prefers morning counselling call.",
            createdDate = "04 Sep 2026",
            lastCallDate = "Pending First Call"
        ),
        Lead(
            id = "lead-103",
            name = "Gokul Nath R",
            phone = "+91 98403 45678",
            email = "gokul.nath@yahoo.com",
            city = "Madurai",
            courseOrProgram = "Data Analytics & Business Intelligence",
            status = LeadStatus.CALL_BACK,
            assignedTelecallerId = "mem-3",
            assignedTelecallerName = "Karthik Raja",
            teamId = "team-2",
            notes = "Requested callback at 5:30 PM after office hours. Budget approved by company.",
            createdDate = "01 Sep 2026",
            lastCallDate = "03 Sep 2026, 04:15 PM"
        ),
        Lead(
            id = "lead-104",
            name = "Meenakshi Balaji",
            phone = "+91 98404 56789",
            email = "meena.balaji@gmail.com",
            city = "Trichy",
            courseOrProgram = "Cybersecurity & Ethical Hacking",
            status = LeadStatus.CONVERTED,
            assignedTelecallerId = "mem-3",
            assignedTelecallerName = "Karthik Raja",
            teamId = "team-2",
            notes = "Counselling finished successfully. Seniority advance paid ₹25,000.",
            createdDate = "28 Aug 2026",
            lastCallDate = "04 Sep 2026, 09:45 AM"
        ),
        Lead(
            id = "lead-105",
            name = "Saravanan Perumal",
            phone = "+91 98405 67890",
            email = "saravanan.p@rediffmail.com",
            city = "Salem",
            courseOrProgram = "DevOps & SRE Engineering",
            status = LeadStatus.FOLLOW_UP,
            assignedTelecallerId = "mem-2",
            assignedTelecallerName = "Arun Prakash",
            teamId = "team-1",
            notes = "Discussed EMI schemes. Waiting for parent's nod for campus visit.",
            createdDate = "03 Sep 2026",
            lastCallDate = "04 Sep 2026, 01:20 PM"
        ),
        Lead(
            id = "lead-106",
            name = "Anitha Swaminathan",
            phone = "+91 98406 78901",
            email = "anitha.swami@gmail.com",
            city = "Tirunelveli",
            courseOrProgram = "Digital Product Management",
            status = LeadStatus.DISQUALIFIED,
            assignedTelecallerId = "mem-4",
            assignedTelecallerName = "Sneha Lakshmi",
            teamId = "team-2",
            notes = "Looking for undergraduate engineering college degree, not professional certificate.",
            createdDate = "02 Sep 2026",
            lastCallDate = "03 Sep 2026, 02:00 PM"
        ),
        Lead(
            id = "lead-107",
            name = "Vijay Anand K",
            phone = "+91 98407 89012",
            email = "vijayanand.k@gmail.com",
            city = "Chennai",
            courseOrProgram = "Executive PG - AI & Machine Learning",
            status = LeadStatus.FOLLOW_UP,
            assignedTelecallerId = "mem-5",
            assignedTelecallerName = "Divya Menon",
            teamId = "team-3",
            notes = "Attending webinar today. Ready for walk-in appointment tomorrow.",
            createdDate = "03 Sep 2026",
            lastCallDate = "04 Sep 2026, 12:10 PM"
        )
    )

    val confirmedGuests = listOf(
        ConfirmedGuest(
            id = "guest-201",
            leadId = "lead-101",
            teamId = "team-1",
            guestName = "Ramesh Venkatesan",
            phone = "+91 98401 23456",
            assignedTelecallerName = "Priya Sharma",
            visitDateTime = "Today, 03:30 PM",
            assignedCounsellor = "Rajesh Kumar (Team Lead)",
            status = GuestStatus.ARRIVED,
            locationOrRoom = "Executive Room 2",
            notes = "Visiting along with spouse. Reviewing course credits and placement record."
        ),
        ConfirmedGuest(
            id = "guest-202",
            leadId = "lead-107",
            teamId = "team-3",
            guestName = "Vijay Anand K",
            phone = "+91 98407 89012",
            assignedTelecallerName = "Divya Menon",
            visitDateTime = "Tomorrow, 11:00 AM",
            assignedCounsellor = "Vignesh Murugan",
            status = GuestStatus.SCHEDULED,
            locationOrRoom = "Conference Suite 1",
            notes = "Requested meeting with alumni mentor if possible."
        ),
        ConfirmedGuest(
            id = "guest-203",
            leadId = null,
            teamId = "team-2",
            guestName = "Dr. S. Karthikeyan",
            phone = "+91 98409 11224",
            assignedTelecallerName = "Karthik Raja",
            visitDateTime = "Today, 05:00 PM",
            assignedCounsellor = "Deepa Sundaram",
            status = GuestStatus.IN_COUNSELLING,
            locationOrRoom = "Room 104",
            notes = "Direct walk-in conversion candidate. Finalizing batch slot."
        ),
        ConfirmedGuest(
            id = "guest-204",
            leadId = "lead-105",
            teamId = "team-1",
            guestName = "Saravanan Perumal",
            phone = "+91 98405 67890",
            assignedTelecallerName = "Arun Prakash",
            visitDateTime = "06 Sep 2026, 02:00 PM",
            assignedCounsellor = "Rajesh Kumar (Team Lead)",
            status = GuestStatus.SCHEDULED,
            locationOrRoom = "Main Office Hall B",
            notes = "Campus tour and lab infrastructure inspection."
        )
    )

    val dailyTasks = listOf(
        DailyTask(
            id = "task-301",
            title = "Complete 45 outbound telecalling calls quota",
            assignedToMemberId = "mem-1",
            assignedToMemberName = "Priya Sharma",
            teamId = "team-1",
            status = TaskStatus.COMPLETED,
            priority = "High",
            dueTime = "02:00 PM"
        ),
        DailyTask(
            id = "task-302",
            title = "Follow up with 8 scheduled walk-in attendees for today",
            assignedToMemberId = "mem-1",
            assignedToMemberName = "Priya Sharma",
            teamId = "team-1",
            status = TaskStatus.IN_PROGRESS,
            priority = "Urgent",
            dueTime = "04:30 PM"
        ),
        DailyTask(
            id = "task-303",
            title = "Upload calling sheets from Education Fair campaign",
            assignedToMemberId = "mem-2",
            assignedToMemberName = "Arun Prakash",
            teamId = "team-1",
            status = TaskStatus.COMPLETED,
            priority = "Normal",
            dueTime = "01:00 PM"
        ),
        DailyTask(
            id = "task-304",
            title = "Review morning callback pipeline and update dispositions",
            assignedToMemberId = "mem-3",
            assignedToMemberName = "Karthik Raja",
            teamId = "team-2",
            status = TaskStatus.COMPLETED,
            priority = "High",
            dueTime = "01:30 PM"
        ),
        DailyTask(
            id = "task-305",
            title = "Collect balance seniority advance receipt for Meenakshi",
            assignedToMemberId = "mem-3",
            assignedToMemberName = "Karthik Raja",
            teamId = "team-2",
            status = TaskStatus.IN_PROGRESS,
            priority = "High",
            dueTime = "05:00 PM"
        ),
        DailyTask(
            id = "task-306",
            title = "Conduct initial phone screening for 15 webinar leads",
            assignedToMemberId = "mem-4",
            assignedToMemberName = "Sneha Lakshmi",
            teamId = "team-2",
            status = TaskStatus.PENDING,
            priority = "Normal",
            dueTime = "06:00 PM"
        ),
        DailyTask(
            id = "task-307",
            title = "Update counselling log records for yesterday afternoon sessions",
            assignedToMemberId = "mem-5",
            assignedToMemberName = "Divya Menon",
            teamId = "team-3",
            status = TaskStatus.COMPLETED,
            priority = "High",
            dueTime = "11:00 AM"
        )
    )

    val counsellingLogs = listOf(
        CounsellingLog(
            id = "counsel-401",
            teamId = "team-1",
            teamName = "Alpha Warriors",
            dateTime = "04 Sep 2026, 11:30 AM",
            counsellorName = "Rajesh Kumar",
            candidateName = "Ramesh Venkatesan",
            candidatePhone = "+91 98401 23456",
            outcome = CounsellingOutcome.INTERESTED,
            keyDiscussion = "Candidate compared with online self-paced courses. Convinced of classroom mentorship and project reviews. Wants to confirm batch timings.",
            recordedByMemberName = "Priya Sharma"
        ),
        CounsellingLog(
            id = "counsel-402",
            teamId = "team-2",
            teamName = "Beta Titans",
            dateTime = "04 Sep 2026, 10:15 AM",
            counsellorName = "Deepa Sundaram",
            candidateName = "Meenakshi Balaji",
            candidatePhone = "+91 98404 56789",
            outcome = CounsellingOutcome.CLOSED,
            keyDiscussion = "Detailed career transition roadmap shared. Immediate enrollment for October batch. Seniority fee ₹25,000 paid via UPI.",
            recordedByMemberName = "Karthik Raja"
        ),
        CounsellingLog(
            id = "counsel-403",
            teamId = "team-3",
            teamName = "Gamma Strikers",
            dateTime = "03 Sep 2026, 04:00 PM",
            counsellorName = "Vignesh Murugan",
            candidateName = "Harish Krishnan",
            candidatePhone = "+91 98408 90123",
            outcome = CounsellingOutcome.FOLLOW_UP,
            keyDiscussion = "Candidate awaiting year-end appraisal incentive before making advance deposit. Re-connect on Sept 10.",
            recordedByMemberName = "Divya Menon"
        ),
        CounsellingLog(
            id = "counsel-404",
            teamId = "team-2",
            teamName = "Beta Titans",
            dateTime = "03 Sep 2026, 02:30 PM",
            counsellorName = "Deepa Sundaram",
            candidateName = "Preethi Narayanan",
            candidatePhone = "+91 98410 44332",
            outcome = CounsellingOutcome.CLOSED,
            keyDiscussion = "Full Stack Cloud certification confirmed. Full course seat reserved with ₹20,000 seniority token amount.",
            recordedByMemberName = "Sneha Lakshmi"
        )
    )

    val salesTransactions = listOf(
        SalesTransaction(
            id = "tx-501",
            candidateName = "Meenakshi Balaji",
            candidatePhone = "+91 98404 56789",
            seniorityAmount = 25000.0,
            paymentMode = "UPI (Google Pay)",
            transactionRef = "UPI/329481928301/PAY",
            receiptNumber = "REC-2026-0891",
            teamId = "team-2",
            teamName = "Beta Titans",
            agentName = "Karthik Raja",
            date = "04 Sep 2026, 10:45 AM",
            closureProofImageUrl = "https://images.unsplash.com/photo-1573496359142-b8d87734a5a2?w=500&auto=format&fit=crop&q=60",
            approvalStatus = ApprovalStatus.PENDING
        ),
        SalesTransaction(
            id = "tx-502",
            candidateName = "Preethi Narayanan",
            candidatePhone = "+91 98410 44332",
            seniorityAmount = 20000.0,
            paymentMode = "Net Banking (HDFC)",
            transactionRef = "HDFC-N839201948",
            receiptNumber = "REC-2026-0888",
            teamId = "team-2",
            teamName = "Beta Titans",
            agentName = "Sneha Lakshmi",
            date = "03 Sep 2026, 03:15 PM",
            closureProofImageUrl = "https://images.unsplash.com/photo-1560250097-0b93528c311a?w=500&auto=format&fit=crop&q=60",
            approvalStatus = ApprovalStatus.APPROVED
        ),
        SalesTransaction(
            id = "tx-503",
            candidateName = "Dinesh Karthik M",
            candidatePhone = "+91 98412 88776",
            seniorityAmount = 30000.0,
            paymentMode = "UPI (PhonePe)",
            transactionRef = "UPI/981247192841/TRX",
            receiptNumber = "REC-2026-0885",
            teamId = "team-1",
            teamName = "Alpha Warriors",
            agentName = "Priya Sharma",
            date = "03 Sep 2026, 11:20 AM",
            closureProofImageUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=500&auto=format&fit=crop&q=60",
            approvalStatus = ApprovalStatus.APPROVED
        ),
        SalesTransaction(
            id = "tx-504",
            candidateName = "Suresh Babu K",
            candidatePhone = "+91 98415 22110",
            seniorityAmount = 15000.0,
            paymentMode = "Cash / Counter Deposit",
            transactionRef = "CASH-CT-094",
            receiptNumber = "REC-2026-0880",
            teamId = "team-3",
            teamName = "Gamma Strikers",
            agentName = "Divya Menon",
            date = "02 Sep 2026, 05:40 PM",
            closureProofImageUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=500&auto=format&fit=crop&q=60",
            approvalStatus = ApprovalStatus.APPROVED
        ),
        SalesTransaction(
            id = "tx-505",
            candidateName = "Keerthana Rajesh",
            candidatePhone = "+91 98416 33441",
            seniorityAmount = 25000.0,
            paymentMode = "Credit Card (POS)",
            transactionRef = "POS-AXIS-482910",
            receiptNumber = "REC-2026-0876",
            teamId = "team-1",
            teamName = "Alpha Warriors",
            agentName = "Arun Prakash",
            date = "02 Sep 2026, 01:10 PM",
            closureProofImageUrl = "https://images.unsplash.com/photo-1519085360753-af0119f7cbe7?w=500&auto=format&fit=crop&q=60",
            approvalStatus = ApprovalStatus.APPROVED
        )
    )

    val celebrations = listOf(
        ClosureCelebration(
            id = "celeb-1",
            teamId = "team-2",
            agentName = "Karthik Raja",
            teamName = "Beta Titans",
            candidateName = "Meenakshi Balaji",
            closedAmount = 25000.0,
            date = "Today",
            avatarInitials = "KR",
            avatarColorHex = 0xFF0284C7,
            closureProofImageUrl = "https://images.unsplash.com/photo-1573496359142-b8d87734a5a2?w=500&auto=format&fit=crop&q=60",
            badgeTitle = "Top Performer of the Day"
        ),
        ClosureCelebration(
            id = "celeb-2",
            teamId = "team-1",
            agentName = "Priya Sharma",
            teamName = "Alpha Warriors",
            candidateName = "Dinesh Karthik M",
            closedAmount = 30000.0,
            date = "Yesterday",
            avatarInitials = "PS",
            avatarColorHex = 0xFF4F46E5,
            closureProofImageUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=500&auto=format&fit=crop&q=60",
            badgeTitle = "Highest Ticket Closer"
        ),
        ClosureCelebration(
            id = "celeb-3",
            teamId = "team-2",
            agentName = "Sneha Lakshmi",
            teamName = "Beta Titans",
            candidateName = "Preethi Narayanan",
            closedAmount = 20000.0,
            date = "03 Sep",
            avatarInitials = "SL",
            avatarColorHex = 0xFF0EA5E9,
            closureProofImageUrl = "https://images.unsplash.com/photo-1560250097-0b93528c311a?w=500&auto=format&fit=crop&q=60",
            badgeTitle = "Rapid Conversion"
        ),
        ClosureCelebration(
            id = "celeb-4",
            teamId = "team-3",
            agentName = "Divya Menon",
            teamName = "Gamma Strikers",
            candidateName = "Suresh Babu K",
            closedAmount = 15000.0,
            date = "02 Sep",
            avatarInitials = "DM",
            avatarColorHex = 0xFF059669,
            closureProofImageUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=500&auto=format&fit=crop&q=60",
            badgeTitle = "Consistent Closer"
        )
    )
}

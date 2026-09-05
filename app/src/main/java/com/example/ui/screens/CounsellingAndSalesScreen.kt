package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import com.example.model.*
import com.example.ui.*
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun CounsellingAndSalesScreen(
    viewModel: CrmViewModel,
    modifier: Modifier = Modifier
) {
    val counsellingLogs by viewModel.filteredCounsellingLogs.collectAsState()
    val transactions by viewModel.filteredSalesTransactions.collectAsState()
    val currentRole by viewModel.currentUserRole.collectAsState()
    val cacheInfo by viewModel.offlineCacheInfo.collectAsState()

    var selectedTab by remember { mutableStateOf(0) } // 0: Counselling Log, 1: Seniority / Sales Tracker

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("counselling_sales_screen")
    ) {
        // Tab Header
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Offline Room Cache Banner for sales transactions
            OfflineCacheBanner(
                cacheInfo = cacheInfo,
                onSyncNow = { viewModel.syncAllToRoomCache() },
                onToggleOfflineSimulation = { viewModel.toggleOfflineModeSimulation() }
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (selectedTab == 0) "Daily Counselling Log" else "Seniority & Sales Tracker",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (selectedTab == 0)
                            "Sessions breakdown & conversion outcomes"
                        else
                            "Advance fee payments, receipts & admin approval",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (selectedTab == 0) {
                    Button(
                        onClick = { viewModel.openModal(ActiveModalDialog.AddCounsellingLog) },
                        colors = ButtonDefaults.buttonColors(containerColor = Indigo600),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("btn_log_counselling_session")
                    ) {
                        Icon(Icons.Default.RecordVoiceOver, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("+ Log Session", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                } else {
                    Button(
                        onClick = { viewModel.openModal(ActiveModalDialog.AddSalesTransaction) },
                        colors = ButtonDefaults.buttonColors(containerColor = Emerald600),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("btn_record_advance_payment")
                    ) {
                        Icon(Icons.Default.Payments, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("+ Record Seniority", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            // Tab bar
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Slate100,
                modifier = Modifier.clip(RoundedCornerShape(8.dp))
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Counselling Log (${counsellingLogs.size})", fontWeight = FontWeight.SemiBold, fontSize = 12.sp) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Seniority Amount (${transactions.size})", fontWeight = FontWeight.SemiBold, fontSize = 12.sp) }
                )
            }
        }

        Divider(color = Slate200)

        if (selectedTab == 0) {
            // Counselling Logs Grid/List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    // Tamil + English Explanation Banner
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = Indigo50),
                        border = CardDefaults.outlinedCardBorder()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(Icons.Default.Info, contentDescription = null, tint = Indigo600)
                            Column {
                                Text(
                                    text = "Counselling Audit Guide:",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Indigo700
                                )
                                Text(
                                    text = "Councelling Trainer Name & Councelling attend Name",
                                    fontSize = 12.sp,
                                    color = Indigo700
                                )
                            }
                        }
                    }
                }

                items(counsellingLogs, key = { it.id }) { log ->
                    CounsellingLogItemCard(log = log)
                }
            }
        } else {
            // Seniority Amount Tracker
            val totalCollected = transactions.sumOf { it.seniorityAmount }
            val pendingApprovals = transactions.count { it.approvalStatus == ApprovalStatus.PENDING }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        MetricCard(
                            title = "Total Seniority Revenue",
                            value = "₹${String.format("%,.0f", totalCollected)}",
                            subtitle = "${transactions.size} transactions",
                            icon = Icons.Default.AccountBalanceWallet,
                            accentColor = Emerald600,
                            modifier = Modifier.weight(1f)
                        )
                        MetricCard(
                            title = "Admin Approvals",
                            value = "$pendingApprovals Pending",
                            subtitle = if (currentRole.isSuperAdmin) "Tap Approve below" else "Super Admin only",
                            icon = Icons.Default.VerifiedUser,
                            accentColor = Amber600,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                items(transactions, key = { it.id }) { tx ->
                    SalesTransactionCard(
                        transaction = tx,
                        isSuperAdmin = currentRole.isSuperAdmin,
                        onApprove = { viewModel.approveTransaction(tx.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun CounsellingLogItemCard(log: CounsellingLog) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("counsel_card_${log.id}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = log.dateTime,
                    fontSize = 11.sp,
                    color = Indigo600,
                    fontWeight = FontWeight.SemiBold
                )
                OutcomeBadge(outcome = log.outcome)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Two-column Who & Whom Grid (Tamil + English Labels)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Slate50, RoundedCornerShape(8.dp))
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Councelling Trainer Name",
                        fontSize = 10.sp,
                        color = Slate500,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = log.counsellorName,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate900
                    )
                }

                Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Councelling attend Name",
                        fontSize = 10.sp,
                        color = Slate500,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = log.candidateName,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate900
                    )
                    Text(
                        text = log.candidatePhone,
                        fontSize = 10.sp,
                        color = Slate400
                    )
                }
            }

            if (log.keyDiscussion.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Discussion & Notes:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Slate700
                )
                Text(
                    text = log.keyDiscussion,
                    fontSize = 12.sp,
                    color = Slate600
                )
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Recorded by: ${log.recordedByMemberName}",
                fontSize = 10.sp,
                color = Slate400
            )
        }
    }
}

@Composable
fun SalesTransactionCard(
    transaction: SalesTransaction,
    isSuperAdmin: Boolean,
    onApprove: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("tx_card_${transaction.id}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = transaction.candidateName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${transaction.candidatePhone} • ${transaction.receiptNumber}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Slate500
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "₹${String.format("%,.0f", transaction.seniorityAmount)}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = Emerald600
                    )
                    Text(
                        text = "Seniority / Advance",
                        fontSize = 10.sp,
                        color = Slate500
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Details Box
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Slate50, RoundedCornerShape(8.dp))
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Payment Mode", fontSize = 10.sp, color = Slate500)
                    Text(transaction.paymentMode, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Slate800)
                    Text("Ref: ${transaction.transactionRef}", fontSize = 10.sp, color = Slate500)
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text("Team & Distributor", fontSize = 10.sp, color = Slate500)
                    Text(transaction.teamName, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Indigo600)
                    Text("Distributor: ${transaction.agentName}", fontSize = 11.sp, color = Slate600)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Closure Proof Image section (Supabase Storage: sales-proofs)
            var showProofPreview by remember { mutableStateOf(false) }

            if (!transaction.closureProofImageUrl.isNullOrBlank()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Indigo50.copy(alpha = 0.5f))
                        .padding(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Indigo50.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Indigo100),
                                contentAlignment = Alignment.Center
                            ) {
                                AsyncImage(
                                    model = transaction.closureProofImageUrl,
                                    contentDescription = "Closure Proof",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                            Column {
                                Text(
                                    text = "Payment / Closure Proof",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Indigo900
                                )
                                Text(
                                    text = "Bucket: ${transaction.storageBucket}",
                                    fontSize = 10.sp,
                                    color = Indigo600
                                )
                            }
                        }

                        TextButton(
                            onClick = { showProofPreview = true },
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("View", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                if (showProofPreview) {
                    AlertDialog(
                        onDismissRequest = { showProofPreview = false },
                        title = {
                            Text("Closure Proof: ${transaction.candidateName}", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        },
                        text = {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Card(
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(220.dp)
                                ) {
                                    AsyncImage(
                                        model = transaction.closureProofImageUrl,
                                        contentDescription = "Full Closure Receipt",
                                        contentScale = ContentScale.Fit,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                                Text(
                                    text = "Receipt: ${transaction.receiptNumber} • Ref: ${transaction.transactionRef}",
                                    fontSize = 11.sp,
                                    color = Slate600
                                )
                                Text(
                                    text = "Secured via Supabase Storage RLS (Bucket: ${transaction.storageBucket})",
                                    fontSize = 10.sp,
                                    color = Emerald600,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        },
                        confirmButton = {
                            TextButton(onClick = { showProofPreview = false }) { Text("Close") }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))
            }

            // Approval Status & Super Admin Action
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ApprovalBadge(status = transaction.approvalStatus)

                if (isSuperAdmin && transaction.approvalStatus == ApprovalStatus.PENDING) {
                    Button(
                        onClick = onApprove,
                        colors = ButtonDefaults.buttonColors(containerColor = Emerald600),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        modifier = Modifier
                            .height(34.dp)
                            .testTag("btn_approve_tx_${transaction.id}")
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Approve Finance", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                } else if (transaction.approvalStatus == ApprovalStatus.APPROVED) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(Icons.Default.Verified, contentDescription = null, tint = Emerald600, modifier = Modifier.size(16.dp))
                        Text("Verified by Super Admin", fontSize = 11.sp, color = Emerald600, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }
    }
}

package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.OfflineCacheInfo
import com.example.ui.theme.*

/**
 * Offline & Room Cache Banner indicating database sync status,
 * ensuring sales data and lead information remain accessible during field calls.
 */
@Composable
fun OfflineCacheBanner(
    cacheInfo: OfflineCacheInfo,
    onSyncNow: () -> Unit,
    onToggleOfflineSimulation: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("offline_cache_banner"),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (cacheInfo.isOfflineModeActive) {
                Color(0xFF78350F).copy(alpha = 0.25f)
            } else {
                Emerald500.copy(alpha = 0.12f)
            }
        ),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(
                            if (cacheInfo.isOfflineModeActive) Amber500.copy(alpha = 0.2f) else Emerald500.copy(alpha = 0.2f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (cacheInfo.isOfflineModeActive) Icons.Default.CloudOff else Icons.Default.CloudDone,
                        contentDescription = null,
                        tint = if (cacheInfo.isOfflineModeActive) Amber500 else Emerald600,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = if (cacheInfo.isOfflineModeActive) "Field Call Offline Mode" else "Room Cache Active",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = if (cacheInfo.isOfflineModeActive) Amber500 else Emerald600
                        ) {
                            Text(
                                text = if (cacheInfo.isOfflineModeActive) "OFFLINE" else "CACHED",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                            )
                        }
                    }
                    Text(
                        text = "Room SQLite: ${cacheInfo.cachedLeadsCount} leads • ${cacheInfo.cachedSalesCount} sales cached for field calls",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Quick Toggle Simulation button for distributor field testing
                OutlinedButton(
                    onClick = onToggleOfflineSimulation,
                    shape = RoundedCornerShape(6.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    modifier = Modifier.height(30.dp).testTag("btn_toggle_offline_mode")
                ) {
                    Icon(
                        imageVector = if (cacheInfo.isOfflineModeActive) Icons.Default.Wifi else Icons.Default.WifiOff,
                        contentDescription = null,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (cacheInfo.isOfflineModeActive) "Go Online" else "Simulate Offline",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // Sync button
                IconButton(
                    onClick = onSyncNow,
                    modifier = Modifier.size(30.dp).testTag("btn_sync_room_cache")
                ) {
                    if (cacheInfo.isSyncing) {
                        CircularProgressIndicator(modifier = Modifier.size(14.dp), strokeWidth = 2.dp)
                    } else {
                        Icon(
                            Icons.Default.Sync,
                            contentDescription = "Sync Room Cache",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

package com.meditrack.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.outlined.History
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.meditrack.app.data.DoseEntity
import com.meditrack.app.ui.components.DoseStatusChip
import com.meditrack.app.ui.components.EmptyState
import com.meditrack.app.ui.components.MediCard
import com.meditrack.app.ui.theme.*
import com.meditrack.app.util.DateTimeUtils
import com.meditrack.app.viewmodel.HistoryViewModel

@Composable
fun HistoryScreen(viewModel: HistoryViewModel) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(OffWhite)
    ) {
        // Header
        Text(
            text = "History & Adherence",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = OnWhite,
            modifier = Modifier.padding(start = 16.dp, top = 16.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Adherence summary card
        MediCard(modifier = Modifier.padding(horizontal = 16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Circular progress showing adherence
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(
                        progress = { state.adherencePercent / 100f },
                        modifier = Modifier.size(72.dp),
                        strokeWidth = 8.dp,
                        color = if (state.adherencePercent >= 80) TakenGreen
                               else if (state.adherencePercent >= 50) AmberWarning
                               else ErrorRed,
                        trackColor = LightGray
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${state.adherencePercent}%",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = OnWhite
                    )
                }
                Spacer(modifier = Modifier.width(20.dp))
                Column {
                    Text("7-Day Adherence", fontSize = 14.sp, color = MediumGray)
                    Text(
                        "${state.takenCount} of ${state.totalCount} doses taken",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = OnWhite
                    )
                    val missed = state.totalCount - state.takenCount
                    if (missed > 0) {
                        Text(
                            "$missed dose(s) missed or skipped",
                            fontSize = 13.sp,
                            color = MediumGray
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Date navigation
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = { viewModel.changeDateByDays(-1) }) {
                Icon(Icons.Filled.ChevronLeft, contentDescription = "Previous day")
            }
            Text(
                text = DateTimeUtils.formatDisplayDate(state.selectedDate),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = OnWhite
            )
            IconButton(onClick = { viewModel.changeDateByDays(1) }) {
                Icon(Icons.Filled.ChevronRight, contentDescription = "Next day")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Doses list
        if (state.doses.isEmpty()) {
            EmptyState(
                icon = Icons.Outlined.History,
                title = "No records for this day",
                message = "Dose records for this date will appear here once you mark them."
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(state.doses, key = { it.id }) { dose ->
                    DoseHistoryCard(dose)
                }
            }
        }
    }
}

@Composable
private fun DoseHistoryCard(dose: DoseEntity) {
    MediCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Time badge
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFFE0F2F1)
            ) {
                Text(
                    text = DateTimeUtils.formatTime(dose.reminderTime),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TealPrimary,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = dose.medicineName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = OnWhite
                )
                Text(
                    text = DateTimeUtils.formatDisplayDate(dose.date),
                    fontSize = 12.sp,
                    color = MediumGray
                )
            }

            DoseStatusChip(dose.status)
        }
    }
}

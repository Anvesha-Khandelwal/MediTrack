package com.meditrack.app.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.Medication
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.meditrack.app.data.MedicineEntity
import com.meditrack.app.ui.components.EmptyState
import com.meditrack.app.ui.components.MediCard
import com.meditrack.app.ui.theme.*
import com.meditrack.app.util.DateTimeUtils
import com.meditrack.app.viewmodel.MedicineViewModel

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun MedicinesScreen(
    viewModel: MedicineViewModel,
    onAddMedicine: () -> Unit,
    onEditMedicine: (Long) -> Unit,
    onBack: () -> Unit
) {
    val medicines by viewModel.medicines.collectAsState()
    val saveResult by viewModel.saveResult.collectAsState()
    val context = LocalContext.current

    // Show toast on save/delete result
    LaunchedEffect(saveResult) {
        saveResult?.let {
            Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
            viewModel.clearSaveResult()
        }
    }

    var showDeleteDialog by remember { mutableStateOf<MedicineEntity?>(null) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddMedicine,
                containerColor = TealPrimary,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add Medicine")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(OffWhite)
                .padding(padding)
        ) {
            // Title
            Text(
                text = "My Medicines",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = OnWhite,
                modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
            )

            if (medicines.isEmpty()) {
                EmptyState(
                    icon = Icons.Outlined.Medication,
                    title = "No medicines added",
                    message = "Tap the + button to add your first medicine with reminders."
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(medicines, key = { it.id }) { medicine ->
                        MedicineCard(
                            medicine = medicine,
                            onEdit = { onEditMedicine(medicine.id) },
                            onDelete = { showDeleteDialog = medicine }
                        )
                    }
                }
            }
        }
    }

    // Delete confirmation dialog
    showDeleteDialog?.let { medicine ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("Delete ${medicine.name}?") },
            text = { Text("This will remove the medicine and all its dose history. This cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteMedicine(context, medicine)
                        showDeleteDialog = null
                    }
                ) {
                    Text("Delete", color = ErrorRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun MedicineCard(
    medicine: MedicineEntity,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val times = medicine.reminderTimes.split(",").map { it.trim() }.filter { it.isNotEmpty() }
    val displayTimes = times.joinToString("  •  ") { DateTimeUtils.formatTime(it) }

    MediCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            // Medicine icon
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFE0F2F1),
                modifier = Modifier.size(44.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Outlined.Medication,
                        contentDescription = null,
                        tint = TealPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = medicine.name,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = OnWhite
                )
                Text(
                    text = medicine.dosage,
                    fontSize = 14.sp,
                    color = MediumGray
                )
                if (medicine.instructions.isNotBlank()) {
                    Text(
                        text = medicine.instructions,
                        fontSize = 13.sp,
                        color = MediumGray
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = displayTimes,
                    fontSize = 13.sp,
                    color = TealPrimary,
                    fontWeight = FontWeight.Medium
                )
                // Date range
                val dateRange = if (medicine.endDate != null) {
                    "${DateTimeUtils.formatDayMonth(medicine.startDate)} – ${DateTimeUtils.formatDayMonth(medicine.endDate)}"
                } else {
                    "Since ${DateTimeUtils.formatDayMonth(medicine.startDate)}"
                }
                Text(
                    text = dateRange,
                    fontSize = 12.sp,
                    color = MediumGray
                )
            }

            // Action buttons
            Column {
                IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Filled.Edit, contentDescription = "Edit", tint = TealPrimary, modifier = Modifier.size(20.dp))
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Filled.Delete, contentDescription = "Delete", tint = ErrorRed, modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}

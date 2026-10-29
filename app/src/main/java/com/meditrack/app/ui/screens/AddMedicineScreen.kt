package com.meditrack.app.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.meditrack.app.ui.components.DatePickerDialog
import com.meditrack.app.ui.components.TimePickerDialog
import com.meditrack.app.ui.theme.*
import com.meditrack.app.util.DateTimeUtils
import com.meditrack.app.viewmodel.MedicineViewModel
import java.util.Calendar

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun AddMedicineScreen(
    viewModel: MedicineViewModel,
    onDone: () -> Unit,
    onCancel: () -> Unit
) {
    val context = LocalContext.current
    val saveResult by viewModel.saveResult.collectAsState()

    // Form state
    var name by remember { mutableStateOf("") }
    var dosage by remember { mutableStateOf("") }
    var instructions by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf(DateTimeUtils.todayIso()) }
    var hasEndDate by remember { mutableStateOf(false) }
    var endDate by remember { mutableStateOf(DateTimeUtils.todayIso()) }
    var reminderTimes by remember { mutableStateOf(listOf("08:00")) }

    // Picker visibility states
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }
    var timePickerIndex by remember { mutableIntStateOf(-1) }

    // Handle save result
    LaunchedEffect(saveResult) {
        saveResult?.let {
            Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
            if (it.success) {
                viewModel.clearSaveResult()
                onDone()
            } else {
                viewModel.clearSaveResult()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Medicine") },
                navigationIcon = {
                    IconButton(onClick = onCancel) {
                        Icon(Icons.Filled.Close, contentDescription = "Close")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(OffWhite)
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Name
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Medicine Name *") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Dosage
            OutlinedTextField(
                value = dosage,
                onValueChange = { dosage = it },
                label = { Text("Dosage *") },
                placeholder = { Text("e.g. 500mg, 1 tablet") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Instructions
            OutlinedTextField(
                value = instructions,
                onValueChange = { instructions = it },
                label = { Text("Instructions") },
                placeholder = { Text("e.g. After meals") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Start date
            Text("Start Date", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = MediumGray)
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedButton(
                onClick = { showStartDatePicker = true },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(DateTimeUtils.formatDisplayDate(startDate))
            }

            Spacer(modifier = Modifier.height(12.dp))

            // End date toggle
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = hasEndDate, onCheckedChange = { hasEndDate = it })
                Text("Set end date", fontSize = 14.sp)
            }

            if (hasEndDate) {
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedButton(
                    onClick = { showEndDatePicker = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(DateTimeUtils.formatDisplayDate(endDate))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Reminder times
            Text("Reminder Times *", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = MediumGray)
            Spacer(modifier = Modifier.height(8.dp))

            reminderTimes.forEachIndexed { index, time ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = { timePickerIndex = index },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Filled.Schedule, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(DateTimeUtils.formatTime(time))
                    }
                    if (reminderTimes.size > 1) {
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(onClick = {
                            reminderTimes = reminderTimes.toMutableList().also { it.removeAt(index) }
                        }) {
                            Icon(Icons.Filled.Close, contentDescription = "Remove time", tint = ErrorRed)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Add time button
            OutlinedButton(
                onClick = { reminderTimes = reminderTimes + "12:00" },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add Another Time")
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Save button
            Button(
                onClick = {
                    val effectiveEndDate = if (hasEndDate) endDate else null
                    viewModel.addMedicine(
                        context = context,
                        name = name,
                        dosage = dosage,
                        instructions = instructions,
                        startDate = startDate,
                        endDate = effectiveEndDate,
                        reminderTimes = reminderTimes
                    )
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
            ) {
                Text("Save Medicine", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Start date picker dialog
    if (showStartDatePicker) {
        val cal = Calendar.getInstance()
        cal.time = DateTimeUtils.parseDate(startDate)
        DatePickerDialog(
            initialYear = cal.get(Calendar.YEAR),
            initialMonth = cal.get(Calendar.MONTH),
            initialDay = cal.get(Calendar.DAY_OF_MONTH),
            onDateSet = { year, month, day ->
                val c = Calendar.getInstance()
                c.set(year, month, day)
                startDate = DateTimeUtils.formatDate(c.time)
                showStartDatePicker = false
            },
            onDismiss = { showStartDatePicker = false }
        )
    }

    // End date picker dialog
    if (showEndDatePicker) {
        val cal = Calendar.getInstance()
        cal.time = DateTimeUtils.parseDate(endDate)
        DatePickerDialog(
            initialYear = cal.get(Calendar.YEAR),
            initialMonth = cal.get(Calendar.MONTH),
            initialDay = cal.get(Calendar.DAY_OF_MONTH),
            onDateSet = { year, month, day ->
                val c = Calendar.getInstance()
                c.set(year, month, day)
                endDate = DateTimeUtils.formatDate(c.time)
                showEndDatePicker = false
            },
            onDismiss = { showEndDatePicker = false }
        )
    }

    // Time picker dialog
    if (timePickerIndex >= 0 && timePickerIndex < reminderTimes.size) {
        val currentTime = reminderTimes[timePickerIndex]
        val parts = currentTime.split(":")
        val h = parts.getOrNull(0)?.toIntOrNull() ?: 8
        val m = parts.getOrNull(1)?.toIntOrNull() ?: 0
        TimePickerDialog(
            initialHour = h,
            initialMinute = m,
            is24Hour = true,
            onTimeSet = { hour, minute ->
                val formatted = "%02d:%02d".format(hour, minute)
                reminderTimes = reminderTimes.toMutableList().also {
                    it[timePickerIndex] = formatted
                }
                timePickerIndex = -1
            },
            onDismiss = { timePickerIndex = -1 }
        )
    }
}

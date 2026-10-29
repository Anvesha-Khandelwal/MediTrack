package com.meditrack.app.ui.components

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import java.util.Calendar

/**
 * Compose wrapper around Android's DatePickerDialog.
 * Shows a date picker and calls onDateSet with year, month (0-indexed), day.
 */
@Composable
fun DatePickerDialog(
    initialYear: Int,
    initialMonth: Int,
    initialDay: Int,
    onDateSet: (year: Int, month: Int, day: Int) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        val dialog = DatePickerDialog(
            context,
            { _, year, month, day -> onDateSet(year, month, day) },
            initialYear,
            initialMonth,
            initialDay
        )
        dialog.setOnCancelListener { onDismiss() }
        dialog.show()
    }
}

/**
 * Compose wrapper around Android's TimePickerDialog.
 * Shows a time picker and calls onTimeSet with hour (0-23) and minute.
 */
@Composable
fun TimePickerDialog(
    initialHour: Int,
    initialMinute: Int,
    is24Hour: Boolean,
    onTimeSet: (hour: Int, minute: Int) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        val dialog = TimePickerDialog(
            context,
            { _, hour, minute -> onTimeSet(hour, minute) },
            initialHour,
            initialMinute,
            is24Hour
        )
        dialog.setOnCancelListener { onDismiss() }
        dialog.show()
    }
}

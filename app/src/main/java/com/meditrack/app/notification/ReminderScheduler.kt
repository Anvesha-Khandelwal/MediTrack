package com.meditrack.app.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.meditrack.app.data.MedicineEntity
import java.util.Calendar

/**
 * Schedules and cancels alarms for medicine reminders using AlarmManager.
 *
 * NOTE: Exact alarm delivery is best-effort. Android may delay or batch alarms
 * when battery saver mode is on or the manufacturer applies aggressive app-killing.
 * We use setExactAndAllowWhileIdle() for maximum reliability but cannot guarantee
 * the exact delivery time under all device conditions.
 */
object ReminderScheduler {

    private const val TAG = "ReminderScheduler"

    /**
     * Schedules daily repeating alarms for all reminder times of a medicine.
     * Each (medicineId, timeSlot) gets a unique request code so we can cancel individually.
     */
    fun scheduleMedicineReminders(context: Context, medicine: MedicineEntity) {
        val times = medicine.reminderTimes.split(",").map { it.trim() }.filter { it.isNotEmpty() }
        for (time in times) {
            scheduleAlarm(context, medicine.id, medicine.name, medicine.dosage, time)
        }
    }

    /**
     * Schedules a single alarm for one time slot.
     * Alarms repeat daily.
     */
    private fun scheduleAlarm(
        context: Context,
        medicineId: Long,
        medicineName: String,
        dosage: String,
        timeStr: String  // "HH:mm"
    ) {
        val parts = timeStr.split(":")
        if (parts.size < 2) return
        val hour = parts[0].toIntOrNull() ?: return
        val minute = parts[1].toIntOrNull() ?: return

        val requestCode = getRequestCode(medicineId, timeStr)

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            // If the time has already passed today, schedule for tomorrow
            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra("medicine_id", medicineId)
            putExtra("medicine_name", medicineName)
            putExtra("dosage", dosage)
            putExtra("reminder_time", timeStr)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        try {
            // On API 31+ we need canScheduleExactAlarms() to be true for exact alarms
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    // Exact alarm allowed even in Doze mode
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        calendar.timeInMillis,
                        pendingIntent
                    )
                } else {
                    // Fall back to inexact alarm if exact alarm permission not granted
                    alarmManager.setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        calendar.timeInMillis,
                        pendingIntent
                    )
                    Log.w(TAG, "Exact alarm not permitted; using inexact alarm for $medicineName at $timeStr")
                }
            } else {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            }
        } catch (e: SecurityException) {
            Log.e(TAG, "Failed to schedule alarm for $medicineName at $timeStr: ${e.message}")
        }
    }

    /**
     * Cancels all alarms for a given medicine.
     */
    fun cancelMedicineReminders(context: Context, medicine: MedicineEntity) {
        val times = medicine.reminderTimes.split(",").map { it.trim() }.filter { it.isNotEmpty() }
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        for (time in times) {
            val requestCode = getRequestCode(medicine.id, time)
            val intent = Intent(context, ReminderReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_NO_CREATE
            )
            if (pendingIntent != null) {
                alarmManager.cancel(pendingIntent)
            }
        }
    }

    /**
     * Restores all alarms from the database — called after device reboot
     * or when notifications are re-enabled.
     */
    fun restoreAllReminders(context: Context, medicines: List<MedicineEntity>) {
        for (medicine in medicines) {
            scheduleMedicineReminders(context, medicine)
        }
        Log.d(TAG, "Restored reminders for ${medicines.size} medicines")
    }

    /**
     * Generates a unique request code per (medicineId, timeSlot) pair.
     * Formula ensures no collisions for reasonable IDs.
     */
    private fun getRequestCode(medicineId: Long, timeStr: String): Int {
        return ((medicineId % 10000).toInt() * 10000) + timeStr.replace(":", "").toInt()
    }
}

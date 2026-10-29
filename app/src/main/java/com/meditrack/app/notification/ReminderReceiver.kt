package com.meditrack.app.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.meditrack.app.data.MediTrackDatabase

/**
 * Receives alarm broadcasts and fires the corresponding notification.
 * After firing, it reschedules the alarm for the next day (daily repeating).
 */
class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val medicineId = intent.getLongExtra("medicine_id", -1L)
        val medicineName = intent.getStringExtra("medicine_name") ?: "Medicine"
        val dosage = intent.getStringExtra("dosage") ?: ""
        val reminderTime = intent.getStringExtra("reminder_time") ?: ""

        if (medicineId == -1L) return

        // Show the notification
        val timeDigits = reminderTime.replace(":", "").toIntOrNull() ?: 0
        val notifId = ((medicineId % 10000).toInt() * 10000) + timeDigits

        NotificationHelper.showReminder(context, notifId, medicineName, dosage, reminderTime)

        // Reschedule for tomorrow (alarm doesn't repeat automatically)
        Thread {
            try {
                kotlinx.coroutines.runBlocking {
                    val dao = MediTrackDatabase.getInstance(context).medicineDao()
                    val medicine = dao.getById(medicineId)
                    if (medicine != null) {
                        // Check if the end date has passed; if so, don't reschedule
                        val shouldContinue = medicine.endDate == null ||
                            medicine.endDate!! >= com.meditrack.app.util.DateTimeUtils.todayIso()
                        if (shouldContinue) {
                            ReminderScheduler.scheduleMedicineReminders(context, medicine)
                        }
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("ReminderReceiver", "Reschedule failed: ${e.message}")
            }
        }.start()
    }
}

package com.meditrack.app.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.meditrack.app.data.MediTrackDatabase

/**
 * Restores all medicine reminder alarms after device reboot.
 * Registered in the manifest for BOOT_COMPLETED.
 */
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED ||
            intent.action == "android.intent.action.QUICKBOOT_POWERON"
        ) {
            // Run on a background thread since onReceive must return quickly
            Thread {
                try {
                    kotlinx.coroutines.runBlocking {
                        val medicines = MediTrackDatabase.getInstance(context)
                            .medicineDao()
                            .getAllMedicinesList()
                        ReminderScheduler.restoreAllReminders(context, medicines)
                    }
                } catch (e: Exception) {
                    android.util.Log.e("BootReceiver", "Restore failed: ${e.message}")
                }
            }.start()
        }
    }
}

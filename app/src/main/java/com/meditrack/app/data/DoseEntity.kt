package com.meditrack.app.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Records a single dose event: Taken, Skipped, or Missed.
 * The unique index on (medicineId + reminderTime + date) prevents duplicate recordings
 * for the same dose on the same day.
 */
@Entity(
    tableName = "doses",
    indices = [Index(value = ["medicineId", "reminderTime", "date"], unique = true)]
)
data class DoseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val medicineId: Long,
    val medicineName: String,
    val reminderTime: String,   // HH:mm — the time slot this dose belongs to
    val date: String,            // yyyy-MM-dd — the day this dose was recorded
    val status: String,          // "TAKEN", "SKIPPED", or "MISSED"
    val timestamp: Long = System.currentTimeMillis()
)

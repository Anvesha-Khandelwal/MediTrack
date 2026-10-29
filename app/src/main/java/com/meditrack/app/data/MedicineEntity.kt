package com.meditrack.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a medicine the user is tracking.
 * Each medicine can have multiple daily reminder times stored as a comma-separated string (HH:mm).
 */
@Entity(tableName = "medicines")
data class MedicineEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val name: String,
    val dosage: String,           // e.g. "500mg" or "1 tablet"
    val instructions: String,    // e.g. "After meals"
    val startDate: String,       // ISO format: yyyy-MM-dd
    val endDate: String?,        // null = ongoing
    val reminderTimes: String,   // comma-separated "08:00,14:00,20:00"
    val createdAt: Long = System.currentTimeMillis()
)

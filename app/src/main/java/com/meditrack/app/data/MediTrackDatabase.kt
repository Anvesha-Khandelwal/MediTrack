package com.meditrack.app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [MedicineEntity::class, DoseEntity::class], version = 1, exportSchema = false)
abstract class MediTrackDatabase : RoomDatabase() {
    abstract fun medicineDao(): MedicineDao
    abstract fun doseDao(): DoseDao

    companion object {
        @Volatile
        private var INSTANCE: MediTrackDatabase? = null

        fun getInstance(context: Context): MediTrackDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MediTrackDatabase::class.java,
                    "meditrack.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
